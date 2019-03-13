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
package org.codelibs.fess.helper;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;

public class LanguageHelper {

    protected String[] langFields;

    protected String[] supportedLanguages;

    @PostConstruct
    public void init() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        langFields = fessConfig.getIndexerLanguageFieldsAsArray();
        supportedLanguages = fessConfig.getSupportedLanguagesAsArray();
    }

    public void updateDocument(final Map<String, Object> doc) {
        final String language =
                getSupportedLanguage(DocumentUtil.getValue(doc, ComponentUtil.getFessConfig().getIndexFieldLang(), String.class));
        if (language == null) {
            return;
        }

        for (final String f : langFields) {
            final String lf = f + "_" + language;
            if (doc.containsKey(f) && !doc.containsKey(lf)) {
                doc.put(lf, doc.get(f));
            }
        }
    }

    protected String getSupportedLanguage(final String lang) {
        for (final String l : supportedLanguages) {
            if (l.equals(lang)) {
                return l;
            }
        }
        return null;
    }

}
