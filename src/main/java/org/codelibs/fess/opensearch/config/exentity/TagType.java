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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import org.codelibs.fess.opensearch.config.bsentity.BsTagType;

/**
 * A tag of one user: its name, its owner and the URLs of the documents it is put on.
 *
 * @author FreeGen
 */
public class TagType extends BsTagType {

    private static final long serialVersionUID = 1L;

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

    public Long getSeqNo() {
        return asDocMeta().seqNo();
    }

    public void setSeqNo(final Long seqNo) {
        asDocMeta().seqNo(seqNo);
    }

    public Long getPrimaryTerm() {
        return asDocMeta().primaryTerm();
    }

    public void setPrimaryTerm(final Long primaryTerm) {
        asDocMeta().primaryTerm(primaryTerm);
    }

    /**
     * Returns the value this tag puts on the tag field of a document: the name and the owner, each
     * encoded as Base64URL without padding from UTF-8, joined by {@code ':'}.
     *
     * @return the tag value, or null when the name or the owner is not set
     */
    public String getTagValue() {
        if (name == null || owner == null) {
            return null;
        }
        return toTagValue(name, owner);
    }

    /**
     * Encodes a tag name and its owner into the value a tag puts on the tag field of a document:
     * each encoded as Base64URL without padding from UTF-8, joined by {@code ':'}. Neither part
     * can contain {@code ':'}, so the value splits back into the two unambiguously.
     *
     * @param name the tag name
     * @param owner the owner of the tag
     * @return the tag value
     */
    public static String toTagValue(final String name, final String owner) {
        final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(name.getBytes(StandardCharsets.UTF_8)) + ":"
                + encoder.encodeToString(owner.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String toString() {
        return "TagType [createdBy=" + createdBy + ", createdTime=" + createdTime + ", name=" + name + ", owner=" + owner + ", paths="
                + (paths == null ? null : paths.length) + ", permissions=" + Arrays.toString(permissions) + ", sortOrder=" + sortOrder
                + ", updatedBy=" + updatedBy + ", updatedTime=" + updatedTime + ", virtualHost=" + virtualHost + ", docMeta=" + docMeta
                + "]";
    }
}
