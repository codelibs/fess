/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.searchlog;

import jakarta.validation.constraints.Size;

/**
 * The GET filter form for the Search Log analytics tabs.
 */
public class AnalyticsForm {

    /**
     * Default constructor for AnalyticsForm.
     */
    public AnalyticsForm() {
    }

    /**
     * The period preset (today, yesterday, 7d, 28d, 90d or custom).
     */
    @Size(max = 10)
    public String range;

    /**
     * The first day of a custom period (yyyy-MM-dd).
     */
    @Size(max = 10)
    public String from;

    /**
     * The last day of a custom period (yyyy-MM-dd).
     */
    @Size(max = 10)
    public String to;

    /**
     * "true" to compare with the previous period.
     */
    @Size(max = 10)
    public String compare;

    /**
     * The access type filter.
     */
    @Size(max = 100)
    public String accessType;

    /**
     * The table size; blank means the tab's own default.
     */
    @Size(max = 3)
    public String size;
}
