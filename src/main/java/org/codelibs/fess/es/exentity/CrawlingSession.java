package org.codelibs.fess.es.exentity;

import java.util.List;

import org.codelibs.fess.es.bsentity.BsCrawlingSession;

/**
 * @author FreeGen
 */
public class CrawlingSession extends BsCrawlingSession {

    private static final long serialVersionUID = 1L;

    private List<CrawlingSessionInfo> crawlingSessionInfoList;

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

    public void setCrawlingSessionInfoList(List<CrawlingSessionInfo> crawlingSessionInfoList) {
        this.crawlingSessionInfoList = crawlingSessionInfoList;
    }

    public List<CrawlingSessionInfo> getCrawlingSessionInfoList() {
        return crawlingSessionInfoList;
    }
}
