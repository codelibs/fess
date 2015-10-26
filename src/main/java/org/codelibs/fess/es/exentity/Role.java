package org.codelibs.fess.es.exentity;

import org.codelibs.fess.es.bsentity.BsRole;

/**
 * @author FreeGen
 */
public class Role extends BsRole {

    private static final long serialVersionUID = 1L;

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }
}
