package com.parse4cn1;

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONObject;
//import com.parse4cn1.operation.ParseFieldOperations;
//import com.parse4cn1.util.ParseRegistry;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//import java.util.SimpleTimeZone;

public class Parse {

    private static String mApplicationId;
    private static String mRestAPIKey;
//	private static final DateFormat dateFormat;

    static {

        // TODO: Fix me!!!
//		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//		format.setTimeZone(new SimpleTimeZone(0, "GMT"));
//		dateFormat = format;
//                
//		ParseRegistry.registerDefaultSubClasses();
//		ParseFieldOperations.registerDefaultDecoders();
    }

    static public void initialize(String applicationId, String restAPIKey) {
        mApplicationId = applicationId;
        mRestAPIKey = restAPIKey;
    }

    static public String getApplicationId() {
        return mApplicationId;
    }

    static public String getRestAPIKey() {
        return mRestAPIKey;
    }

    static public String getParseAPIUrl(String context) {
        return ParseConstants.API_ENDPOINT + "/" + ParseConstants.API_VERSION
                + "/" + ((context != null) ? context : "");
    }

                // TODO: Fix me!!!
//	public static synchronized String encodeDate(Date date) {
//		return dateFormat.format(date);
//	}
//
    // TODO: Fix me!!!
//	public static synchronized Date parseDate(String dateString) {
//		try {
//			return dateFormat.parse(dateString);
//		} catch (ParseException e) {
//			return null;
//		}
//	}
    public static boolean isReservedKey(String key) {
        return ParseConstants.FIELD_OBJECT_ID.equalsIgnoreCase(key)
                || ParseConstants.FIELD_CREATED_AT.equalsIgnoreCase(key)
                || ParseConstants.FIELD_UPDATED_AT.equalsIgnoreCase(key);
    }

    // TODO: Fix me!!!
//	public static boolean isValidType(Object value) {
//		return ((value instanceof JSONObject))
//				|| ((value instanceof JSONArray))
//				|| ((value instanceof String))
//				|| ((value instanceof Number))
//				|| ((value instanceof Boolean))
//				|| (value == JSONObject.NULL)
//				|| ((value instanceof ParseObject))
//				// || ((value instanceof ParseACL))
//				|| ((value instanceof ParseFile))
//				|| ((value instanceof ParseRelation))
//				|| ((value instanceof ParseGeoPoint))
//				|| ((value instanceof Date)) 
//				|| ((value instanceof byte[]))
//				|| ((value instanceof List)) 
//				|| ((value instanceof Map));
//	}
    @SuppressWarnings("rawtypes")
    public static String join(Collection<String> items, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = items.iterator();
        if (iter.hasNext()) {
            buffer.append((String) iter.next());
            while (iter.hasNext()) {
                buffer.append(delimiter);
                buffer.append((String) iter.next());
            }
        }
        return buffer.toString();
    }

}
