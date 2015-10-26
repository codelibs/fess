package org.codelibs.fess.es.exentity;

import org.codelibs.fess.es.bsentity.BsGroup;

/**
 * @author FreeGen
 */
public class Group extends BsGroup {

    private static final long serialVersionUID = 1L;

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }
}
