package org.codelibs.fess.es.exentity;

import org.codelibs.fess.es.bsentity.BsRequestHeader;

/**
 * @author FreeGen
 */
public class RequestHeader extends BsRequestHeader {

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

    public org.codelibs.robot.client.http.RequestHeader getS2RobotRequestHeader() {
        return new org.codelibs.robot.client.http.RequestHeader(getName(), getValue());
    }
}
