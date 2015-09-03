package org.codelibs.fess.es.exentity;

import org.codelibs.fess.es.bsentity.BsCrawlingSession;

/**
 * @author FreeGen
 */
public class CrawlingSession extends BsCrawlingSession {

    private static final long serialVersionUID = 1L;

    public CrawlingSession() {
    }

    public CrawlingSession(final String sessionId) {
        setSessionId(sessionId);
    }

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
}
