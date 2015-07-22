/*
 * Copyright 2015 Chidiebere Okwudire.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Original implementation adapted from Thiago Locatelli's Parse4J project
 * (see https://github.com/thiagolocatelli/parse4j)
 */
package com.parse4cn1;

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.parse4cn1.command.ParseGetCommand;
import com.parse4cn1.command.ParseResponse;
import com.parse4cn1.encode.IParseObjectEncodingStrategy;
import com.parse4cn1.encode.PointerEncodingStrategy;
import com.parse4cn1.util.Logger;
import com.parse4cn1.encode.ParseEncoder;
import com.parse4cn1.util.ParseRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * The ParseQuery class defines a query that is used to fetch ParseObjects.
 *
 * @param <T> The type of the class associated with this query.
 */
public class ParseQuery<T extends ParseObject> {

    private static final Logger LOGGER = Logger.getInstance();

    private String className;
    private QueryConstraints where;
    private ArrayList<String> include;
    private ArrayList<String> selectedKeys;
    private int limit;
    private int skip;
    private String order;
    private boolean caseSensitive = true;

    /**
     * Creates a ParseQuery for the specified class type.
     *
     * @param subclass The ParseObject subclass type associated with this query.
     */
    private ParseQuery(Class<T> subclass) {
        this(ParseRegistry.getClassName(subclass));
    }

    /**
     * Creates a ParseQuery for the specified ParseObject (sub-)call.
     *
     * @param theClassName The name of the class associated with this query.
     */
    private ParseQuery(String theClassName) {
        if (theClassName == null) {
            throw new IllegalArgumentException("Null class name");
        }

        this.className = theClassName;
        this.limit = -1;
        this.skip = 0;
        this.where = new QueryConstraints();
        this.include = new ArrayList<String>();
    }

    /**
     * Creates a ParseQuery for the specified sub-class. For this approach to
     * work, {@code subclass} needs to have been registered via
     * {@link ParseRegistry#registerSubclass(java.lang.Class, java.lang.String)}.
     *
     * @param <T> The type of the class to be associated with the newly created
     * query.
     * @param subclass The ParseObject subclass type.
     * @return The newly created ParseQuery.
     */
    public static <T extends ParseObject> ParseQuery<T> getQuery(Class<T> subclass) {
        return new ParseQuery<T>(subclass);
    }

    /**
     * Creates a ParseQuery for the ParseObject sub-class with the specified
     * name..
     *
     * @param <T> The type of the class to be associated with the newly created
     * query.
     * @param className The ParseObject subclass name.
     * @return The newly created ParseQuery.
     */
    public static <T extends ParseObject> ParseQuery<T> getQuery(String className) {
        return new ParseQuery<T>(className);
    }
    
    /**
     * Create a ParseQuery whose where-clause is the disjunction of the 
     * where-clauses of the provided {@code queries}.
     * 
     * @param <T> The type of the class to be associated with the newly created
     * query.
     * @param queries A collection of at least two queries to be combined. Each 
     * of these queries must have a non-empty 'where' clause and have the same class.
     * @return The newly created ParseQuery.
     * @throws ParseException if the {@code queries} are not at least two, any 
     * of them is missing a where-clause, or the target class is different.
     */
    public static <T extends ParseObject> ParseQuery<T> getOrQuery(
            final Collection<ParseQuery> queries) throws ParseException {
       if (queries.size() < 2) {
           throw new ParseException(ParseException.OTHER_CAUSE, 
                   "At least two queries must be provided.");
       } 
       
       String targetClass = null;
       JSONArray array = new JSONArray();
       for (ParseQuery query : queries) {
           if (query.getQueryConstraints().isEmpty()) {
              throw new ParseException(ParseException.OTHER_CAUSE, 
                   "Query has no query constraints."); 
           }
           
           if (targetClass == null) {
               targetClass = query.getClassName();
           } else if (!targetClass.equals(query.getClassName())) {
              throw new ParseException(ParseException.OTHER_CAUSE, 
                   "All queries should be of the same target class: " + targetClass + ".");  
           }
           
           array.put(query.getQueryConstraints());
       }
       
       if (targetClass == null) {
           throw new ParseException(ParseException.OTHER_CAUSE, 
                   "A query target class is required."); 
       }
       ParseQuery query = ParseQuery.getQuery(targetClass);
       query.whereEqualTo("$or", array);
       return query;
    }

    /**
     * Add a constraint to the query that requires a particular key's value to
     * be equal to the provided value.
     *
     * @param key The key to check.
     * @param value The value that the ParseObject must contain.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereEqualTo(String key, Object value) {
        this.where.put(key, value);
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's value to
     * be less than the provided value.
     *
     * @param key The key to check.
     * @param value The value that provides an upper bound.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereLessThan(String key, Object value) {
        addCondition(key, "$lt", value);
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's value to
     * be not equal to the provided value.
     *
     * @param key The key to check.
     * @param value The value that must not be equalled.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereNotEqualTo(String key, Object value) {
        addCondition(key, "$ne", value);
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's value to
     * be greater than the provided value.
     *
     * @param key The key to check.
     * @param value The value that provides a lower bound.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereGreaterThan(String key, Object value) {
        addCondition(key, "$gt", value);
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's value to
     * be less than or equal to the provided value.
     *
     * @param key The key to check.
     * @param value The value that provides an upper bound.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereLessThanOrEqualTo(String key, Object value) {
        addCondition(key, "$lte", value);
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's value to
     * be greater than or equal to the provided value.
     *
     * @param key The key to check.
     * @param value The value that provides a lower bound.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereGreaterThanOrEqualTo(String key, Object value) {
        addCondition(key, "$gte", value);
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's value to
     * be contained in the provided list of values.
     *
     * @param key The key to check.
     * @param values The values that will match.
     * @return {@code this} object so that calls can be chained.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ParseQuery<T> whereContainedIn(String key, Collection<? extends Object> values) {
        addCondition(key, "$in", new ArrayList(values));
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's value not
     * to be contained in the provided list of values.
     *
     * @param key The key to check.
     * @param values The values that will not match.
     * @return {@code this} object so that calls can be chained.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ParseQuery<T> whereNotContainedIn(String key, Collection<? extends Object> values) {
        addCondition(key, "$nin", new ArrayList(values));
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's value to
     * contain every one of the provided list of values.
     * <p>
     * This only works on keys whose values are of array type.
     *
     * @param key The key to check. This key's value must be an array.
     * @param values The values that will match.
     * @return {@code this} object so that calls can be chained.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ParseQuery<T> whereContainsAll(String key, Collection<?> values) {
        addCondition(key, "$all", new ArrayList(values));
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's value
     * match another ParseQuery.
     * <p>
     * This only works on keys whose values are ParseObjects or lists of
     * ParseObjects.
     *
     * @param key The key to check.
     * @param query The query that the value should match.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereMatchesQuery(String key, ParseQuery<?> query) {
        addCondition(key, "$inQuery", query);
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's value does
     * not match another ParseQuery.
     * <p>
     * This only works on keys whose values are ParseObjects or lists of
     * ParseObjects.
     *
     * @param key The key to check.
     * @param query The query that the value should not match
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereDoesNotMatchQuery(String key, ParseQuery<?> query) {
        addCondition(key, "$notInQuery", query);
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's value
     * matches a value for a key in the results of another ParseQuery.
     *
     * @param key The key whose value is being checked.
     * @param keyInQuery The key in the objects from the sub query to look in.
     * @param query The nested query to run.
     * @return {@code this} object so that calls can be chained.
     * @throws ParseException if something goes wrong with creating the
     * constraint.
     */
    public ParseQuery<T> whereMatchesKeyInQuery(String key,
            String keyInQuery, ParseQuery<?> query) throws ParseException {
        JSONObject condition = new JSONObject();
        try {
            condition.put("key", keyInQuery);
            condition.put("query", query);
        } catch (JSONException e) {
            throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, e);
        }
        addCondition(key, "$select", condition);
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's value does
     * not match any value for a key in the results of another ParseQuery.
     *
     * @param key The key whose value is being checked and excluded.
     * @param keyInQuery The key in the objects from the sub query to look in.
     * @param query The nested query to run.
     * @return {@code this} object so that calls can be chained.
     * @throws ParseException if something goes wrong with creating the
     * constraint.
     */
    public ParseQuery<T> whereDoesNotMatchKeyInQuery(String key,
            String keyInQuery, ParseQuery<?> query) throws ParseException {
        JSONObject condition = new JSONObject();
        try {
            condition.put("key", keyInQuery);
            condition.put("query", query);
        } catch (JSONException e) {
            throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, e);
        }
        addCondition(key, "$dontSelect", condition);
        return this;
    }

    /**
     * Add a proximity based constraint for finding objects with key point
     * values near the point given.
     *
     * @param key The key that the ParseGeoPoint is stored in.
     * @param point The reference ParseGeoPoint that is used.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereNear(String key, ParseGeoPoint point) {
        addCondition(key, "$nearSphere", point);
        return this;
    }

    /**
     * Add a proximity-based constraint for finding objects with key point
     * values near the point given and within the maximum distance given.
     * <p>
     * Radius of earth used is 3958.8 miles.
     *
     * @param key The key that the ParseGeoPoint is stored in.
     * @param point The reference ParseGeoPoint that is used.
     * @param maxDistance Maximum distance (in miles) of results to return.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereWithinMiles(String key, ParseGeoPoint point, double maxDistance) {
        whereWithinRadians(key, point, maxDistance / ParseGeoPoint.EARTH_MEAN_RADIUS_MILE);
        return this;
    }

    /**
     * Add a proximity-based constraint for finding objects with key point
     * values near the point given and within the maximum distance given.
     * <p>
     * Radius of earth used is 6371.0 kilometers.
     *
     * @param key The key that the ParseGeoPoint is stored in.
     * @param point The reference ParseGeoPoint that is used.
     * @param maxDistance Maximum distance (in kilometers) of results to return.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereWithinKilometers(String key, ParseGeoPoint point, double maxDistance) {
        whereWithinRadians(key, point, maxDistance / ParseGeoPoint.EARTH_MEAN_RADIUS_KM);
        return this;
    }

    /**
     * Add a proximity based constraint for finding objects with key point
     * values near the point given and within the maximum distance given.
     *
     * @param key The key that the ParseGeoPoint is stored in.
     * @param point The reference ParseGeoPoint that is used.
     * @param maxDistance Maximum distance (in radians) of results to return.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereWithinRadians(String key, ParseGeoPoint point, double maxDistance) {
        addCondition(key, "$nearSphere", point);
        addCondition(key, "$maxDistance", maxDistance);
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's
     * coordinates be contained within a given rectangular geographic bounding
     * box.
     *
     * @param key The key to be constrained.
     * @param southwest The lower-left inclusive corner of the box.
     * @param northeast The upper-right inclusive corner of the box.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereWithinGeoBox(String key, ParseGeoPoint southwest, ParseGeoPoint northeast) {
        ArrayList<ParseGeoPoint> array = new ArrayList<ParseGeoPoint>();
        array.add(southwest);
        array.add(northeast);
        HashMap<String, Object> dictionary = new HashMap<String, Object>();
        dictionary.put("$box", array);
        addCondition(key, "$within", dictionary);
        return this;
    }

    /**
     * Add a regular expression constraint for finding string values that match
     * the provided regular expression.
     * <p>
     * This may be slow for large datasets.
     *
     * @param key The key that the string to match is stored in.
     * @param regex The regular expression pattern to match.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereMatches(String key, String regex) {
        addCondition(key, "$regex", regex);
        return this;
    }

    /**
     * Add a regular expression constraint for finding string values that match
     * the provided regular expression.
     * <p>
     * This may be slow for large datasets.
     *
     * @param key The key that the string to match is stored in.
     * @param regex The regular expression pattern to match.
     * @param modifiers Any of the following supported PCRE modifiers:
     * <br>i - Case insensitive search
     * <br>m - Search across multiple lines of input
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereMatches(String key, String regex, String modifiers) {
        addCondition(key, "$regex", regex);
        if (modifiers.length() != 0) {
            addCondition(key, "$options", modifiers);
        }
        return this;
    }

    /**
     * Add a constraint for finding string values that contain a provided
     * string. A case-sensitive comparison is performed by default. Use
     * {@link #setCaseSensitive(boolean)} to change that before calling this
     * method.
     * <p>
     * This will be slow for large datasets.
     *
     * @param key The key that the string to match is stored in.
     * @param substring The substring that the value must contain.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereContains(String key, String substring) {
        whereMatches(key, quote(substring), (caseSensitive ? "" : "i"));
        return this;
    }

    /**
     * Add a constraint for finding string values that start with a provided
     * string. A case-sensitive comparison is performed by default. Use
     * {@link #setCaseSensitive(boolean)} to change that before calling this
     * method.
     * <p>
     * This query will use the backend index, so it will be fast even for large
     * datasets.
     *
     * @param key The key that the string to match is stored in.
     * @param prefix The substring that the value must start with.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereStartsWith(String key, String prefix) {
        whereMatches(key, new StringBuilder("^").append(quote(prefix)).toString(),
                (caseSensitive ? "" : "i"));
        return this;
    }

    /**
     * Add a constraint for finding string values that end with a provided
     * string. A case-sensitive comparison is performed by default. Use
     * {@link #setCaseSensitive(boolean)} to change that before calling this
     * method.
     * <p>
     * This will be slow for large datasets.
     *
     * @param key The key that the string to match is stored in.
     * @param suffix The substring that the value must end with.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereEndsWith(String key, String suffix) {
        whereMatches(key, new StringBuilder(quote(suffix)).append("$").toString(),
                (caseSensitive ? "" : "i"));
        return this;
    }

    /**
     * Add a constraint for finding objects that contain the given key.
     *
     * @param key The key that should exist.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereExists(String key) {
        addCondition(key, "$exists", true);
        return this;
    }

    /**
     * Add a constraint for finding objects that do not contain a given key.
     *
     * @param key The key that should not exist.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereDoesNotExist(String key) {
        addCondition(key, "$exists", false);
        return this;
    }

    /**
     * Add a constraint for finding objects that are related to {@code parent}
     * by the given {@code key}.
     *
     * @param parent The ParseObject to be used in the relation.
     * @param key The key that should be used in the relation.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> whereRelatedTo(ParseObject parent, String key) {
        this.where.put("$relatedTo", new RelationConstraint(key, parent));
        return this;
    }

    /**
     * Include nested ParseObjects for the provided key.
     * <p>
     * Use dot notation to specify which fields in the included object that are
     * also fetched.
     *
     * @param key The key that should be included.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> include(String key) {
        this.include.add(key);
        return this;
    }

    /**
     * Sorts the results in ascending order by the given key.
     * <p>
     * <b>Note:</b> {@code key} overrides any previous ordering criteria!
     * Use {@link #addAscendingOrder(java.lang.String)} or 
     * {@link #addDescendingOrder(java.lang.String)} to concatenate ordering criteria.
     *
     * @param key The key to order by.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> orderByAscending(String key) {
        this.order = key;
        return this;
    }

    /**
     * Also sorts the results in ascending order by the given key.
     * <p>
     * The previous sort keys have precedence over this key.
     *
     * @param key The key to order by.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> addAscendingOrder(String key) {
        if (this.order == null) {
            this.order = key;
        } else {
            this.order = (this.order + "," + key);
        }
        return this;
    }

    /**
     * Sorts the results in descending order by the given key.
     * <p>
     * <b>Note:</b> {@code key} overrides any previous ordering criteria!
     * Use {@link #addAscendingOrder(java.lang.String)} or 
     * {@link #addDescendingOrder(java.lang.String)} to concatenate ordering criteria.
     *
     * @param key The key to order by.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> orderByDescending(String key) {
        this.order = ("-" + key);
        return this;
    }

    /**
     * Also sorts the results in descending order by the given key.
     * <p>
     * The previous sort keys have precedence over this key.
     *
     * @param key The key to order by.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> addDescendingOrder(String key) {
        if (this.order == null) {
            this.order = ("-" + key);
        } else {
            this.order = (this.order + ",-" + key);
        }
        return this;
    }

    /**
     * Controls the maximum number of results that are returned.
     * <p>
     * Setting a negative limit denotes retrieval without a limit. The default
     * limit is 100, with a maximum of 1000 results being returned at a time.
     *
     * @param newLimit The new limit.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> setLimit(int newLimit) {
        this.limit = newLimit;
        return this;
    }

    /**
     * Determines whether string constraints should be case-sensitive (default)
     * or case-insensitive.
     *
     * @param caseSensitive The new case sensitivity.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        return this;
    }

    /**
     * Accessor for the limit.
     *
     * @return The limit value.
     */
    public int getLimit() {
        return this.limit;
    }

    /**
     * Controls the number of results to setSkip before returning any results.
     * <p>
     * This is useful for pagination. Default is to setSkip zero results.
     *
     * @param newSkip The new setSkip.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> setSkip(int newSkip) {
        this.skip = newSkip;
        return this;
    }

    /**
     * Accessor for the setSkip.
     *
     * @return The setSkip value.
     */
    public int getSkip() {
        return this.skip;
    }

    /**
     * Accessor for the class name.
     *
     * @return The class name.
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * Accessor for the included nested ParseObjects.
     *
     * @return The keys of the included nested ParseObjects.
     */
    public List<String> getIncludes() {
        return Collections.unmodifiableList(this.include);
    }

    /**
     * Restrict the fields of returned ParseObjects to only include the provided
     * keys.
     * <p>
     * If this is called multiple times, then all of the keys specified in each
     * of the calls will be included.
     *
     * @param keys The set of keys to include in the result.
     * @return {@code this} object so that calls can be chained.
     */
    public ParseQuery<T> selectKeys(Collection<String> keys) {
        if (this.selectedKeys == null) {
            this.selectedKeys = new ArrayList<String>();
        }
        this.selectedKeys.addAll(keys);
        return this;
    }

    /**
     * Constructs a ParseObject whose id is already known by fetching data from
     * the source.
     * <p>
     * <b>Note:</b> This mutates the ParseQuery.
     *
     * @param objectId Object id of the ParseObject to fetch.
     * @return The first object returned found with {@code objectId} or
     * {@code null} if none is found. <b>Note that this deviates from the 
     * corresponding Parse Android API method where an exception is thrown when  
     * there is no such object.</b>
     * @throws ParseException if anything goes wrong.
     * @see <a href="http://www.parse.com/docs/android/api/com/parse/ParseQuery.html#get(java.lang.String)">Parse Android API get() method</a>
     */
    public T get(String objectId) throws ParseException {

        T obj = null;
        whereEqualTo(ParseConstants.FIELD_OBJECT_ID, objectId);

        List<T> results = find();
        if (results != null && results.size() > 0) {
            obj = results.get(0);
        }

        removeCondition(ParseConstants.FIELD_OBJECT_ID);

        return obj;
    }

    /**
     * Retrieves a list of ParseObjects that satisfy this query.
     *
     * @return A list of the ParseObjects matching the query or an empty list if
     * none is found. <b>Note that this deviates from the 
     * corresponding Parse Android API method where an exception is thrown when 
     * the query returns no results.</b>
     * @throws ParseException if anything goes wrong.
     * @see <a href="http://www.parse.com/docs/android/api/com/parse/ParseQuery.html#find()">Parse Android API find() method</a>
     */
    public List<T> find() throws ParseException {
        return find(encode());
    }

    /**
     * Encodes this query as a JSON object.
     *
     * @return The JSONObject corresponding to this query.
     * @throws ParseException if anything goes wrong with the conversion to
     * JSON.
     */
    public JSONObject encode() throws ParseException {
        JSONObject params = new JSONObject();
        try {
            params.put(ParseConstants.FIELD_CLASSNAME, this.className);

            if (this.where.size() > 0) {
                params.put("where",
                        ParseEncoder.encode(this.where, PointerEncodingStrategy.get()));
            }

            if (this.limit >= 0) {
                params.put("limit", this.limit);
            }

            if (this.skip > 0) {
                params.put("skip", this.skip);
            }

            if (this.order != null) {
                params.put("order", this.order);
            }

            if (!this.include.isEmpty()) {
                params.put("include", Parse.join(this.include, ","));
            }

            if (this.selectedKeys != null) {
                params.put("keys", Parse.join(this.selectedKeys, ","));
            }

        } catch (JSONException e) {
            LOGGER.error("Error encoding json: " + e);
            throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, e);
        }

        return params;
    }
    
    /**
     * Retrieves the query constraints (i.e., where-clause) of this ParseQuery.
     * @return The QueryConstraints of this ParseQuery. 
     */
    QueryConstraints getQueryConstraints() {
        return where;
    }

    /**
     * Retrieves a list of ParseObjects that satisfy the provided {@code query}.
     *
     * @param query The JSON-encoded
     * @return A list of the ParseObjects matching the query or an empty list if
     * none is found.
     * @throws ParseException if anything goes wrong.
     */
    @SuppressWarnings("unchecked")
    private List<T> find(JSONObject query) throws ParseException {

        ParseGetCommand command = new ParseGetCommand(getEndPoint());
        query.remove(ParseConstants.FIELD_CLASSNAME);
        addDataToCommand(command, query);
        ParseResponse response = command.perform();
        List<T> results = new ArrayList<T>();
        if (!response.isFailed()) {
            if (response.getJsonObject() == null) {
                LOGGER.debug("Empty response.");
                throw response.getException();
            }
            try {
                JSONObject json = response.getJsonObject();
                JSONArray objs = json.getJSONArray("results");
                if (objs.length() == 0) {
                    return results;
                }

                for (int i = 0; i < objs.length(); i++) {
                    T po = ParseRegistry.getObjectFactory(getClassName()).create(getClassName());
                    JSONObject obj = (JSONObject) objs.get(i);
                    po.setData(obj);
                    results.add((T) po);
                }

                return results;
            } catch (JSONException e) {
                LOGGER.error(
                        ParseException.ERR_INVALID_RESPONSE + " Error: "
                        + e.getMessage());
                throw new ParseException(
                        ParseException.INVALID_JSON,
                        ParseException.ERR_INVALID_RESPONSE,
                        e);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Error while instantiating class. Did you register your subclass? Error: "
                        + e.getMessage());
                throw new ParseException(
                        "An error occurred while processing query results.",
                        e);
            }
        } else {
            LOGGER.debug("Request failed.");
            throw response.getException();
        }
    }

    /**
     * Adds the query data as url-encoded parameters of the provided 
     * GET {@code command}.
     * 
     * @param command The command to which the query is to be added.
     * @param data The query to be added.
     * @throws ParseException if anything goes wrong while parsing the JSON data.
     */
    private void addDataToCommand(final ParseGetCommand command, 
            final JSONObject data) throws ParseException {
        Iterator<?> it = data.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            Object value = data.opt(key);
            command.addArgument(key, value.toString());
        }
    }
 
    /**
     * Counts the number of objects that match this query.
     *
     * @return The number of objects matching this query.
     * @throws ParseException if anything goes wrong.
     */
    public int count() throws ParseException {

        ParseGetCommand command = new ParseGetCommand(getEndPoint());
        JSONObject query = encode();
        try {
            query.put("count", 1);
            query.put("limit", 0);
        } catch (JSONException ex) {
            throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
        }
        query.remove(ParseConstants.FIELD_CLASSNAME);
        addDataToCommand(command, query);
        ParseResponse response = command.perform();
        if (!response.isFailed()) {
            if (response.getJsonObject() == null) {
                LOGGER.debug("Empty response.");
                throw response.getException();
            }
            try {
                JSONObject json = response.getJsonObject();
                int count = json.getInt("count");
                return count;
            } catch (JSONException e) {
                LOGGER.error(
                        ParseException.ERR_INVALID_RESPONSE + " Error: "
                        + e.getMessage());
                throw new ParseException(
                        ParseException.INVALID_JSON,
                        ParseException.ERR_INVALID_RESPONSE,
                        e);
            }
        } else {
            LOGGER.debug("Request failed.");
            throw response.getException();
        }
    }

    /**
     * Add a {@link KeyConstraints} to this query.
     *
     * @param key The key associated with the constraint.
     * @param condition The condition that must be met.
     * @param value The desired value.
     */
    private void addCondition(String key, String condition, Object value) {
        KeyConstraints whereValue = null;

        if (this.where.containsKey(key)) {
            Object existingValue = this.where.get(key);
            if (existingValue instanceof KeyConstraints) {
                whereValue = (KeyConstraints) existingValue;
            }
        }
        if (whereValue == null) {
            whereValue = new KeyConstraints();
        }

        whereValue.put(condition, value);
        this.where.put(key, whereValue);
    }

    /**
     * Removes a previously added constraint. If the constraint is not found,
     * this method has no effect.
     *
     * @param key The key associated with the constraint.
     */
    private void removeCondition(String key) {
        if (this.where.containsKey(key)) {
            this.where.remove(key);
        }
    }

    /**
     * Creates an end point formed by concatenating 
     * {@value ParseConstants#CLASSES_PATH} and the result of 
     * {@link #getClassName()}
     * 
     * @return The end point.
     */
    private String getEndPoint() {
        return ParseConstants.CLASSES_PATH + getClassName();
    }

    /**
     * Returns a literal pattern String for the specified String.
     * <p>
     * This method produces a String that can be used to getQuery a Pattern that
     * would match the string s as if it were a literal pattern.
     * <p>
     * Metacharacters or escape sequences in the input sequence will be given no
     * special meaning.
     * <p>
     * <b>Note:</b> CN1 does not support {@link java.util.regex} and the
     * substitute CN1 regex class does not have this
     * method. Hence this replacement from <a
     * href="http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/java/util/regex/Pattern.java#Pattern.quote%28java.lang.String%29">openjdk</a>.
     *
     * @param s The string to be literalized.
     * @return A literal string replacement.
     */
    public static String quote(String s) {
        int slashEIndex = s.indexOf("\\E");
        if (slashEIndex == -1) {
            return "\\Q" + s + "\\E";
        }

        StringBuilder sb = new StringBuilder(s.length() * 2);
        sb.append("\\Q");
        slashEIndex = 0;
        int current = 0;
        while ((slashEIndex = s.indexOf("\\E", current)) != -1) {
            sb.append(s.substring(current, slashEIndex));
            current = slashEIndex + 2;
            sb.append("\\E\\\\E\\Q");
        }
        sb.append(s.substring(current, s.length()));
        sb.append("\\E");
        return sb.toString();
    }

    @SuppressWarnings("serial")
    static class KeyConstraints extends HashMap<String, Object> {
    }

    @SuppressWarnings("serial")
    static class QueryConstraints extends HashMap<String, Object> {
    }

    /**
     * This class defines a relation constraint that is used when specifying
     * many-to-many relations.
     */
    public class RelationConstraint {

        private String key;
        private ParseObject object;

        /**
         * Constructors a relation constraint with the provided {@code key} and
         * {@code object}.
         *
         * @param key The key associated with this relation.
         * @param object The ParseObject associated with this relation.
         */
        public RelationConstraint(String key, ParseObject object) {
            if ((key == null) || (object == null)) {
                throw new IllegalArgumentException("Arguments must not be null.");
            }
            this.key = key;
            this.object = object;
        }

        /**
         * Accessor for the relation's key.
         *
         * @return The key.
         */
        public String getKey() {
            return this.key;
        }

        /**
         * Accessor for the relation's object.
         *
         * @return The object.
         */
        public ParseObject getObject() {
            return this.object;
        }

        /**
         * Accessor for the ParseRelation associated with this relation
         * constraint.
         *
         * @return The ParseRelation.
         */
        public ParseRelation<ParseObject> getRelation() {
            return this.object.getRelation(this.key);
        }

        /**
         * Encodes this relation constraint in JSON.
         *
         * @param objectEncoder The encoder to be used for the encoding.
         * @return This relation constraint encoded as a JSON object.
         * @throws ParseException if anything goes wrong.
         */
        public JSONObject encode(IParseObjectEncodingStrategy objectEncoder) throws ParseException {
            JSONObject json = new JSONObject();
            try {
                json.put("key", this.key);
                json.put("object",
                        objectEncoder.encodeRelatedObject(this.object));
            } catch (JSONException e) {
                throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, e);
            }
            return json;
        }
    }
}
