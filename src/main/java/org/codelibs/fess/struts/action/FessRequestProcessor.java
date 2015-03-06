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

package org.codelibs.fess.struts.action;

import java.util.Map;

import org.codelibs.fess.util.SearchParamMap;
import org.seasar.struts.action.S2RequestProcessor;

public class FessRequestProcessor extends S2RequestProcessor/*ActionRequestProcessor*/{
    @Override
    @SuppressWarnings("unchecked")
    protected void setMapProperty(final Map map, final String name,
            final Object value) {
        if (value instanceof String[]) {
            if (map.get(SearchParamMap.CLASS_NAME) != null) {
                map.put(name, value);
            } else {
                final String[] values = (String[]) value;
                map.put(name, values.length > 0 ? values[0] : null);
            }
        } else {
            map.put(name, value);
        }
    }

}
