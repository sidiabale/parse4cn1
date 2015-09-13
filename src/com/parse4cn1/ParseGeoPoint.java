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

import com.codename1.io.Externalizable;
import com.codename1.io.Util;
import com.codename1.util.MathUtil;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * ParseGeoPoint represents a latitude / longitude point that may be associated 
 * with a key in a ParseObject or used as a reference point for geo queries. 
 * This allows proximity based queries on the key.
 * <p>
 * Distances are  calculated using the <a href="https://en.wikipedia.org/wiki/Haversine_formula">'Haversine' formula</a>.
 */
public class ParseGeoPoint implements Externalizable {

    static double EARTH_MEAN_RADIUS_KM = 6371.0D;
    static double EARTH_MEAN_RADIUS_MILE = 3958.8000000000002D;

    private double latitude = 0.0D;
    private double longitude = 0.0D;
    
    /**
     * @return A unique class name.
     */
    public static String getClassName() {
        return "ParseGeoPoint";
    }
    
    /**
     * Creates a new GeoPoint with default coordinates (0.0, 0.0).
     */
    public ParseGeoPoint() {
    }

    /**
     * Creates a new point with the specified latitude and longitude.
     * 
     * @param latitude The point's latitude.
     * @param longitude The point's longitude.
     */
    public ParseGeoPoint(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    /**
     * Set latitude. Valid range is (-90.0, 90.0). Extremes should not be used.
     * 
     * @param latitude The point's latitude
     */
    public final void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Set longitude. Valid range is (-180.0, 180.0). Extremes should not be used.
     * 
     * @param longitude The point's longitude.
     */
    public final void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    /**
     * Get distance in radians between this point and another ParseGeoPoint. 
     * This is the smallest angular distance between the two points.
     * 
     * @param point ParseGeoPoint describing the other point being measured against.
     * @return The distance between this point and {@code point} in radians.
     */
    public double distanceInRadiansTo(ParseGeoPoint point) {
        double d2r = 0.0174532925199433D;
        double lat1rad = this.latitude * d2r;
        double long1rad = this.longitude * d2r;
        double lat2rad = point.getLatitude() * d2r;
        double long2rad = point.getLongitude() * d2r;
        double deltaLat = lat1rad - lat2rad;
        double deltaLong = long1rad - long2rad;
        double sinDeltaLatDiv2 = Math.sin(deltaLat / 2.0D);
        double sinDeltaLongDiv2 = Math.sin(deltaLong / 2.0D);

        double a = sinDeltaLatDiv2 * sinDeltaLatDiv2 + Math.cos(lat1rad)
                * Math.cos(lat2rad) * sinDeltaLongDiv2 * sinDeltaLongDiv2;

        a = Math.min(1.0D, a);
        return 2.0D * MathUtil.asin(Math.sqrt(a));
    }

    /**
     * Get distance between this point and another ParseGeoPoint in kilometers.
     * 
     * @param point ParseGeoPoint describing the other point being measured against.
     * @return The distance between this point and {@code point} in kilometers.
     * @see #distanceInRadiansTo(com.parse4cn1.ParseGeoPoint)
     */
    public double distanceInKilometersTo(ParseGeoPoint point) {
        return distanceInRadiansTo(point) * EARTH_MEAN_RADIUS_KM;
    }

    /**
     * Get distance between this point and another ParseGeoPoint in miles.
     * 
     * @param point ParseGeoPoint describing the other point being measured against.
     * @return The distance between this point and {@code point} in miles.
     * @see #distanceInRadiansTo(com.parse4cn1.ParseGeoPoint)
     */
    public double distanceInMilesTo(ParseGeoPoint point) {
        return distanceInRadiansTo(point) * EARTH_MEAN_RADIUS_MILE;
    }
    
    /**
     * @see com.codename1.io.Externalizable
     */
    public int getVersion() {
        return Parse.getSerializationVersion();
    }

    /**
     * @see com.codename1.io.Externalizable
     */
    public void externalize(DataOutputStream out) throws IOException {
        Util.writeObject(getLatitude(), out);
        Util.writeObject(getLongitude(), out);
    }

    /**
     * @see com.codename1.io.Externalizable
     */
    public void internalize(int version, DataInputStream in) throws IOException {
        setLatitude((Double)Util.readObject(in));
        setLongitude((Double)Util.readObject(in));
    }

    /**
     * @see com.codename1.io.Externalizable
     */
    public String getObjectId() {
        return getClassName();
    }
}
