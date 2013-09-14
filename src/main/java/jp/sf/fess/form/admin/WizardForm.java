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

package jp.sf.fess.form.admin;

import java.io.Serializable;

import org.seasar.struts.annotation.IntRange;
import org.seasar.struts.annotation.LongRange;
import org.seasar.struts.annotation.Maxbytelength;
import org.seasar.struts.annotation.Required;

public class WizardForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Maxbytelength(maxbytelength = 10, target = "schedule")
    public String scheduleEnabled;

    @Maxbytelength(maxbytelength = 2, target = "schedule")
    public String scheduleMonth;

    @Maxbytelength(maxbytelength = 2, target = "schedule")
    public String scheduleDate;

    @Maxbytelength(maxbytelength = 2, target = "schedule")
    public String scheduleHour;

    @Maxbytelength(maxbytelength = 2, target = "schedule")
    public String scheduleMin;

    @Maxbytelength(maxbytelength = 20, target = "schedule")
    public String scheduleDay;

    @Maxbytelength(maxbytelength = 200, target = "crawlingConfig,crawlingConfigNext")
    public String crawlingConfigName;

    @Required(target = "crawlingConfig,crawlingConfigNext")
    @Maxbytelength(maxbytelength = 1000)
    public String crawlingConfigPath;

    @LongRange(min = 0, max = Long.MAX_VALUE, target = "crawlingConfig,crawlingConfigNext")
    public String maxAccessCount;

    @IntRange(min = 0, max = Integer.MAX_VALUE, target = "crawlingConfig,crawlingConfigNext")
    public String depth;

    public WizardForm() {
        scheduleEnabled = null;
        scheduleMonth = "*";
        scheduleDate = "*";
        scheduleHour = "0";
        scheduleMin = "0";
        maxAccessCount = "10000";
        depth = null;
    }
}
