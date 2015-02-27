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

package jp.sf.fess.helper.impl;

import java.util.List;

import jp.sf.fess.helper.WebManagementHelper;

public class EmptyManagementHelperImpl implements WebManagementHelper {

    @Override
    public boolean hasSolrInstance() {
        return false;
    }

    @Override
    public List<String> getSolrInstanceNameList() {
        return null;
    }

    @Override
    public String getStatus(final String name) {
        return null;
    }

    @Override
    public void start(final String name) {
        // nothing
    }

    @Override
    public void stop(final String name) {
        // nothing
    }

    @Override
    public void reload(final String name) {
        // nothing
    }

}
