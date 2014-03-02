package jp.sf.fess.filter;

import javax.servlet.http.HttpServletRequest;

import jp.sf.fess.entity.LoginInfo;

import org.codelibs.sastruts.core.SSCConstants;
import org.codelibs.sastruts.core.entity.UserInfo;
import org.codelibs.sastruts.core.filter.AuthFilter;

public class AdminAuthFilter extends AuthFilter {
    @Override
    protected UserInfo getUserInfo(final HttpServletRequest req) {
        final Object obj = req.getSession()
                .getAttribute(SSCConstants.USER_INFO);
        if (obj instanceof LoginInfo) {
            final LoginInfo loginInfo = (LoginInfo) obj;
            if (loginInfo.isAdministrator()) {
                return loginInfo;
            }
        }
        return null;
    }
}
