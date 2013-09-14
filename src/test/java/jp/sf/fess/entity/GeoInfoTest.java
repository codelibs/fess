/*
 * Copyright 2009-2013 the Fess Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package jp.sf.fess.entity;

import org.seasar.extension.unit.S2TestCase;

public class GeoInfoTest extends S2TestCase {
    public void test_0_0_10() {
        final String latitude = "0";
        final String lonitude = "0";
        final String distance = "10";

        final GeoInfo geoInfo = create(latitude, lonitude, distance);
        assertTrue(geoInfo.isAvailable());
        assertEquals(
                "{!geofilt pt=" + Double.toString(Double.parseDouble(latitude))
                        + "," + Double.toString(Double.parseDouble(lonitude))
                        + " sfield=location d=" + distance + ".0}",
                geoInfo.toGeoQueryString());
    }

    public void test_90_180_10() {
        final String latitude = "90";
        final String lonitude = "180";
        final String distance = "10";

        final GeoInfo geoInfo = create(latitude, lonitude, distance);
        assertTrue(geoInfo.isAvailable());
        assertEquals(
                "{!geofilt pt=" + Double.toString(Double.parseDouble(latitude))
                        + "," + Double.toString(Double.parseDouble(lonitude))
                        + " sfield=location d=" + distance + ".0}",
                geoInfo.toGeoQueryString());
    }

    public void test_91_181_10() {
        final String latitude = "91";
        final String lonitude = "181";
        final String distance = "10";

        final GeoInfo geoInfo = create(latitude, lonitude, distance);
        assertTrue(geoInfo.isAvailable());
        assertEquals("{!geofilt pt=90.0,-179.0 sfield=location d=10.0}",
                geoInfo.toGeoQueryString());
    }

    public void test_91_361_10() {
        final String latitude = "91";
        final String lonitude = "361";
        final String distance = "100";

        final GeoInfo geoInfo = create(latitude, lonitude, distance);
        assertTrue(geoInfo.isAvailable());
        assertEquals("{!geofilt pt=90.0,1.0 sfield=location d=100.0}",
                geoInfo.toGeoQueryString());
    }

    public void test__90__180_10() {
        final String latitude = "-90";
        final String lonitude = "-180";
        final String distance = "10";

        final GeoInfo geoInfo = create(latitude, lonitude, distance);
        assertTrue(geoInfo.isAvailable());
        assertEquals(
                "{!geofilt pt=" + Double.toString(Double.parseDouble(latitude))
                        + "," + Double.toString(Double.parseDouble(lonitude))
                        + " sfield=location d=" + distance + ".0}",
                geoInfo.toGeoQueryString());
    }

    public void test__91__181_10() {
        final String latitude = "-91";
        final String lonitude = "-181";
        final String distance = "10";

        final GeoInfo geoInfo = create(latitude, lonitude, distance);
        assertTrue(geoInfo.isAvailable());
        assertEquals("{!geofilt pt=-90.0,179.0 sfield=location d=10.0}",
                geoInfo.toGeoQueryString());
    }

    public void test__91__361_10() {
        final String latitude = "-91";
        final String lonitude = "-361";
        final String distance = "100";

        final GeoInfo geoInfo = create(latitude, lonitude, distance);
        assertTrue(geoInfo.isAvailable());
        assertEquals("{!geofilt pt=-90.0,-1.0 sfield=location d=100.0}",
                geoInfo.toGeoQueryString());
    }

    public void test_0_0_0() {
        final String latitude = "0";
        final String lonitude = "0";
        final String distance = "0";

        final GeoInfo geoInfo = create(latitude, lonitude, distance);
        assertFalse(geoInfo.isAvailable());
    }

    public void test_x_0_0() {
        final String latitude = "x";
        final String lonitude = "0";
        final String distance = "10";

        final GeoInfo geoInfo = create(latitude, lonitude, distance);
        assertFalse(geoInfo.isAvailable());
    }

    public void test_0_x_0() {
        final String latitude = "0";
        final String lonitude = "x";
        final String distance = "10";

        final GeoInfo geoInfo = create(latitude, lonitude, distance);
        assertFalse(geoInfo.isAvailable());
    }

    private GeoInfo create(final String latitude, final String longitude,
            final String distance) {
        final GeoInfo geoInfo = new GeoInfo();
        geoInfo.latitude = latitude;
        geoInfo.longitude = longitude;
        geoInfo.distance = distance;
        return geoInfo;
    }
}
