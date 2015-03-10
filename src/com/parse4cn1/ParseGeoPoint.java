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

import com.codename1.util.MathUtil;

public class ParseGeoPoint {

    static double EARTH_MEAN_RADIUS_KM = 6371.0D;
    static double EARTH_MEAN_RADIUS_MILE = 3958.8000000000002D;

    private double latitude = 0.0D;
    private double longitude = 0.0D;

    public ParseGeoPoint(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public void setLatitude(double latitude) {
        if ((latitude > 90.0D) || (latitude < -90.0D)) {
            throw new IllegalArgumentException(
                    "Latitude must be within the range (-90.0, 90.0).");
        }
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        if ((longitude > 180.0D) || (longitude < -180.0D)) {
            throw new IllegalArgumentException(
                    "Longitude must be within the range (-180.0, 180.0).");
        }
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

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

    public double distanceInKilometersTo(ParseGeoPoint point) {
        return distanceInRadiansTo(point) * EARTH_MEAN_RADIUS_KM;
    }

    public double distanceInMilesTo(ParseGeoPoint point) {
        return distanceInRadiansTo(point) * EARTH_MEAN_RADIUS_MILE;
    }
}
