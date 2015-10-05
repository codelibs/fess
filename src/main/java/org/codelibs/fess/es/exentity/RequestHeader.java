package org.codelibs.fess.es.exentity;

import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.es.bsentity.BsRequestHeader;
import org.codelibs.fess.util.ComponentUtil;

/**
 * @author FreeGen
 */
public class RequestHeader extends BsRequestHeader {

    private static final long serialVersionUID = 1L;
    private WebConfig webConfig;

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

    public org.codelibs.robot.client.http.RequestHeader getS2RobotRequestHeader() {
        return new org.codelibs.robot.client.http.RequestHeader(getName(), getValue());
    }

    public WebConfig getWebConfig() {
        if (webConfig == null) {
            final WebConfigService webConfigService = ComponentUtil.getComponent(WebConfigService.class);
            webConfig = webConfigService.getWebConfig(getWebConfigId());
        }
        return webConfig;
    }
}
