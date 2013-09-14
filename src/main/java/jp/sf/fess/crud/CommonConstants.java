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

package jp.sf.fess.crud;

public class CommonConstants {
    public static final int LIST_MODE = 0;

    public static final int CREATE_MODE = 1;

    public static final int EDIT_MODE = 2;

    public static final int DELETE_MODE = 3;

    public static final int CONFIRM_MODE = 4;

    public static final String TIMESTAMP_PATTERN = "yyyy/MM/dd HH:mm:ss";

    public static final String TRUE = "T";

    public static final String FALSE = "F";

    public static final String[] PAGER_CONVERSION_RULE = { "allRecordCount",
            "pageSize", "currentPageNumber", "allPageCount", "existPrePage",
            "existNextPage" };

    protected CommonConstants() {
        // nothing
    }
}
