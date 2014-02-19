/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.Mask;
import org.seasar.struts.annotation.Maxbytelength;

public class GeoInfo {
    @Mask(mask = "-?([0-9]+|[0-9]+\\.[0-9]+)")
    @Maxbytelength(maxbytelength = 20)
    public String latitude;

    @Mask(mask = "-?([0-9]+|[0-9]+\\.[0-9]+)")
    @Maxbytelength(maxbytelength = 20)
    public String longitude;

    @Mask(mask = "-?([0-9]+|[0-9]+\\.[0-9]+)")
    @Maxbytelength(maxbytelength = 20)
    public String distance;

    private boolean isInit = false;

    private void init() {
        if (!isInit) {
            isInit = true;

            if (StringUtil.isBlank(latitude) || StringUtil.isBlank(longitude)
                    || StringUtil.isBlank(distance)) {
                latitude = null;
                longitude = null;
                distance = null;
                return;
            }

            try {
                final double dist = Double.parseDouble(distance);
                double lat = Double.parseDouble(latitude);
                double lon = Double.parseDouble(longitude);

                if (dist > 0) {
                    distance = Double.toString(dist);
                } else {
                    distance = null;
                }

                if (lat > 90) {
                    lat = 90;
                } else if (lat < -90) {
                    lat = -90;
                }

                if (lon > 180) {
                    lon = lon % 360;
                    if (lon > 180) {
                        lon -= 360;
                    }
                } else if (lon < -180) {
                    lon = lon % 360;
                    if (lon < -180) {
                        lon += 360;
                    }
                }

                latitude = Double.toString(lat);
                longitude = Double.toString(lon);
            } catch (final NumberFormatException e) {
                latitude = null;
                longitude = null;
                distance = null;
            }
        }
    }

    public boolean isAvailable() {
        init();
        return StringUtil.isNotBlank(latitude)
                && StringUtil.isNotBlank(longitude)
                && StringUtil.isNotBlank(distance);
    }

    public String toGeoQueryString() {
        init();
        return "{!geofilt pt=" + latitude + "," + longitude
                + " sfield=location d=" + distance + "}";
    }

    @Override
    public String toString() {
        return "GeoInfo [latitude=" + latitude + ", longitude=" + longitude
                + ", distance=" + distance + ", isInit=" + isInit + "]";
    }
}
