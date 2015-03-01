/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.pager;

import java.time.LocalDateTime;

import org.codelibs.fess.Constants;
import org.codelibs.fess.crud.pager.BsUserInfoPager;

public class UserInfoPager extends BsUserInfoPager {

    private static final long serialVersionUID = 1L;

    public LocalDateTime updatedTimeBefore;

    public UserInfoPager() {
        super();
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    protected int getDefaultPageSize() {
        return Constants.DEFAULT_ADMIN_PAGE_SIZE;
    }

}
