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
package org.codelibs.fess.app.web.api.admin.suggest;

import org.codelibs.fess.app.web.admin.suggest.SuggestForm;

/**
 * Represents the request body for suggest API operations.
 * This class extends {@link SuggestForm} and adds fields for tracking
 * the number of total, document, and query words.
 */
public class SuggestBody extends SuggestForm {
    /**
     * Constructs a new suggest body.
     */
    public SuggestBody() {
        // do nothing
    }

    /** The total number of words in the suggest index. */
    public Long totalWordsNum;

    /** The number of words from documents. */
    public Long documentWordsNum;

    /** The number of words from search queries. */
    public Long queryWordsNum;
}