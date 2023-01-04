/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.entity;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;

public class GeoInfoTest extends UnitFessTestCase {

    public void test_34_150_10() {
        MockletHttpServletRequest request = getMockRequest();
        request.setParameter("geo.location.point", "34,150");
        request.setParameter("geo.location.distance", "10km");

        final GeoInfo geoInfo = new GeoInfo(request);
        String result =
                "{\"geo_distance\":{\"location\":[150.0,34.0],\"distance\":10000.0,\"distance_type\":\"arc\",\"validation_method\":\"STRICT\",\"ignore_unmapped\":false,\"boost\":1.0}}";
        assertEquals(result, geoInfo.toQueryBuilder().toString().replaceAll("[ \n]", ""));
    }

    public void test_34_150_10_x() {
        MockletHttpServletRequest request = getMockRequest();
        request.setParameter("geo.location.x.point", "34,150");
        request.setParameter("geo.location.x.distance", "10km");

        final GeoInfo geoInfo = new GeoInfo(request);
        String result =
                "{\"geo_distance\":{\"location\":[150.0,34.0],\"distance\":10000.0,\"distance_type\":\"arc\",\"validation_method\":\"STRICT\",\"ignore_unmapped\":false,\"boost\":1.0}}";
        assertEquals(result, geoInfo.toQueryBuilder().toString().replaceAll("[ \n]", ""));
    }

    public void test_34_150_10_2() {
        MockletHttpServletRequest request = getMockRequest();
        request.setParameter("geo.location.1.point", "34,150");
        request.setParameter("geo.location.1.distance", "10km");
        request.setParameter("geo.location.2.point", "35,151");
        request.setParameter("geo.location.2.distance", "1km");

        final GeoInfo geoInfo = new GeoInfo(request);
        String result =
                "{\"bool\":{\"should\":[{\"geo_distance\":{\"location\":[151.0,35.0],\"distance\":1000.0,\"distance_type\":\"arc\",\"validation_method\":\"STRICT\",\"ignore_unmapped\":false,\"boost\":1.0}},{\"geo_distance\":{\"location\":[150.0,34.0],\"distance\":10000.0,\"distance_type\":\"arc\",\"validation_method\":\"STRICT\",\"ignore_unmapped\":false,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}";
        assertEquals(result, geoInfo.toQueryBuilder().toString().replaceAll("[ \n]", ""));
    }

}
