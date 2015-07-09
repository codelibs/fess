package org.codelibs.fess.es.exentity;

import java.util.Locale;

import org.codelibs.fess.es.bsentity.BsCrawlingSessionInfo;
import org.codelibs.fess.es.exbhv.CrawlingSessionBhv;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.seasar.struts.util.MessageResourcesUtil;
import org.seasar.struts.util.RequestUtil;

/**
 * @author FreeGen
 */
public class CrawlingSessionInfo extends BsCrawlingSessionInfo {

    private static final long serialVersionUID = 1L;

    private OptionalEntity<CrawlingSession> crawlingSession;

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

    public OptionalEntity<CrawlingSession> getCrawlingSession() {
        if (crawlingSession != null) {
            final CrawlingSessionBhv crawlingSessionBhv = ComponentUtil.getComponent(CrawlingSessionBhv.class);
            crawlingSession = crawlingSessionBhv.selectEntity(cb -> {
                cb.query().docMeta().setId_Equal(getCrawlingSessionId());
            });
        }
        return crawlingSession;
    }

    public String getKeyMsg() {
        final Locale locale = RequestUtil.getRequest().getLocale();
        final String message = MessageResourcesUtil.getMessage(locale, "labels.crawling_session_" + getKey());
        if (message == null || message.startsWith("???")) {
            return getKey();
        }
        return message;
    }

}
