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
package org.codelibs.fess.opensearch.user.exentity;

import org.codelibs.fess.opensearch.user.bsentity.BsPasskey;

/**
 * @author ESFlute (using FreeGen)
 */
public class Passkey extends BsPasskey {

    private static final long serialVersionUID = 1L;

    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
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
     * Omits the public key so that an entity written to a log never carries credential material.
     */
    @Override
    public String toString() {
        return "Passkey [userName=" + userName + ", label=" + label + "]";
    }
}
