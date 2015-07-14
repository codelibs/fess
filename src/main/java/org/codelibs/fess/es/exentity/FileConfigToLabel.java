package org.codelibs.fess.es.exentity;

import org.codelibs.fess.es.bsentity.BsFileConfigToLabel;

/**
 * @author FreeGen
 */
public class FileConfigToLabel extends BsFileConfigToLabel {

    private static final long serialVersionUID = 1L;

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    @Override
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
