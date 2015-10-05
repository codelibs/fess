package org.codelibs.fess.es.exentity;

import org.codelibs.fess.es.bsentity.BsOverlappingHost;

/**
 * @author FreeGen
 */
public class OverlappingHost extends BsOverlappingHost {

    private static final long serialVersionUID = 1L;

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    @Override
    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    public String convert(final String url) {
        final String targetStr = getOverlappingName().replaceAll("\\.", "\\\\.");
        return url.replaceFirst("://" + targetStr + "$", "://" + getRegularName()).replaceFirst("://" + targetStr + "([:/])",
                "://" + getRegularName() + "$1");
    }
}
