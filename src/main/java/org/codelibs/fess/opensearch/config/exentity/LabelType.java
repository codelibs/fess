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
package org.codelibs.fess.opensearch.config.exentity;

import java.util.Locale;

import org.codelibs.fess.opensearch.config.bsentity.BsLabelType;
import org.codelibs.fess.util.ComponentUtil;

/**
 * @author FreeGen
 */
public class LabelType extends BsLabelType {

    private static final long serialVersionUID = 1L;

    /** The kind of a label that the crawler assigns to documents by the included and excluded paths. */
    public static final String KIND_LABEL = "label";

    /**
     * The kind of a label that is a tag users add from the search screen: its name is the tag name, its included paths
     * list the tagged URLs (matched exactly) and its permissions list who can see it, including the users who added it.
     */
    public static final String KIND_TAG = "tag";

    private Locale locale;

    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    /**
     * Returns whether this label type defines user tags. A label type without a kind is a plain label.
     *
     * @return true if the kind is {@value #KIND_TAG}
     */
    public boolean isTagKind() {
        return KIND_TAG.equals(getKind());
    }

    public Locale getLocale() {
        if (locale == null) {
            if (getValue() == null) {
                return Locale.ROOT;
            }
            locale = ComponentUtil.getFessConfig().getQueryLocaleFromName(getValue());
        }
        return locale;
    }

    @Override
    public String toString() {
        return "LabelType [createdBy=" + createdBy + ", createdTime=" + createdTime + ", excludedPaths=" + excludedPaths
                + ", includedPaths=" + includedPaths + ", kind=" + kind + ", name=" + name + ", sortOrder=" + sortOrder + ", updatedBy="
                + updatedBy + ", updatedTime=" + updatedTime + ", value=" + value + ", docMeta=" + docMeta + "]";
    }
}
