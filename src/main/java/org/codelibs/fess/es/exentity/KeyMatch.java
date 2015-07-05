package org.codelibs.fess.es.exentity;

import org.codelibs.fess.es.bsentity.BsKeyMatch;

/**
 * @author FreeGen
 */
public class KeyMatch extends BsKeyMatch {

    private static final long serialVersionUID = 1L;

    public String getId() {
        return asDocMeta().id();
    }

    public void setId(String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(Long version) {
        asDocMeta().version(version);
    }
}
