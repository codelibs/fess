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
package org.codelibs.fess.api.v2.handlers;

import java.util.regex.Pattern;

import org.codelibs.core.lang.StringUtil;

/**
 * Shared validation for the {@code doc_id} parameter used by the document-keyed v2
 * handlers ({@code CacheHandler}, {@code ClickHandler}, {@code FavoriteGetHandler},
 * {@code FavoritePostHandler}).
 *
 * <p>Conservative allowlist — the search backend accepts more, but v2 surfaces
 * {@code doc_id} via the URL path / request body, so it is locked down to reject odd
 * input at the edge rather than let it reach OpenSearch as a term query (or enable
 * path-traversal-style abuse). Centralised here so the handlers share a single
 * compiled pattern instead of each declaring its own copy.</p>
 */
public class DocIdValidator {

    /** Allowlist for {@code doc_id}: ASCII letters, digits, underscore and hyphen; 1–512 characters. */
    private static final Pattern DOC_ID_PATTERN = Pattern.compile("[A-Za-z0-9_-]{1,512}");

    /**
     * Creates a document-id validator. Registered as the DI component
     * {@code v2DocIdValidator} and obtained via
     * {@link org.codelibs.fess.util.ComponentUtil#getV2DocIdValidator()}.
     */
    public DocIdValidator() {
        // default constructor
    }

    /**
     * Checks whether a document id is acceptable for the v2 surface.
     *
     * @param docId the candidate document id
     * @return {@code true} if non-blank and composed only of allowed characters
     */
    public boolean isValid(final String docId) {
        return StringUtil.isNotBlank(docId) && DOC_ID_PATTERN.matcher(docId).matches();
    }
}
