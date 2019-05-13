/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.config.exbhv;

import java.util.regex.Pattern;

import org.codelibs.fess.es.config.bsbhv.BsKeyMatchBhv;
import org.codelibs.fess.util.ComponentUtil;

/**
 * @author FreeGen
 */
public class KeyMatchBhv extends BsKeyMatchBhv {
    private String indexName = null;

    @Override
    protected String asEsIndex() {
        if (indexName == null) {
            final String name = ComponentUtil.getFessConfig().getIndexConfigIndex();
            indexName = super.asEsIndex().replaceFirst(Pattern.quote(".fess_config"), name);
        }
        return indexName;
    }
}
