package jp.sf.fess.helper;

import javax.servlet.http.HttpServletRequest;

import org.seasar.struts.util.RequestUtil;

public class UserAgentHelper {
    private static final String USER_AGENT = "user-agent";

    private static final String USER_AGENT_TYPE = "ViewHelper.UserAgent";

    public UserAgentType getUserAgentType() {
        final HttpServletRequest request = RequestUtil.getRequest();
        UserAgentType uaType = (UserAgentType) request
                .getAttribute(USER_AGENT_TYPE);
        if (uaType == null) {
            final String userAgent = request.getHeader(USER_AGENT);
            if (userAgent != null) {
                if (userAgent.indexOf("MSIE") >= 0
                        || userAgent.indexOf("Trident") >= 0) {
                    uaType = UserAgentType.IE;
                } else if (userAgent.indexOf("Firefox") >= 0) {
                    uaType = UserAgentType.FIREFOX;
                } else if (userAgent.indexOf("Chrome") >= 0) {
                    uaType = UserAgentType.CHROME;
                } else if (userAgent.indexOf("Safari") >= 0) {
                    uaType = UserAgentType.SAFARI;
                } else if (userAgent.indexOf("Opera") >= 0) {
                    uaType = UserAgentType.OPERA;
                }
            }
            if (uaType == null) {
                uaType = UserAgentType.OTHER;
            }
            request.setAttribute(USER_AGENT_TYPE, uaType);
        }
        return uaType;
    }

    public enum UserAgentType {
        IE, FIREFOX, CHROME, SAFARI, OPERA, OTHER;
    }

}
