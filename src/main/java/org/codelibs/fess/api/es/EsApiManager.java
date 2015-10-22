package org.codelibs.fess.api.es;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.elasticsearch.runner.net.Curl.Method;
import org.codelibs.elasticsearch.runner.net.CurlRequest;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.BaseApiManager;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsApiManager extends BaseApiManager {
    private static final Logger logger = LoggerFactory.getLogger(EsApiManager.class);

    @Resource
    protected DynamicProperties crawlerProperties;

    protected String[] acceptedRoles = new String[] { "admin" };

    public EsApiManager() {
        setPathPrefix("/admin/server");
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        final String servletPath = request.getServletPath();
        if (servletPath.startsWith(pathPrefix)) {
            final FessLoginAssist loginAssist = ComponentUtil.getLoginAssist();
            return loginAssist.getSessionUserBean().map(user -> user.hasRoles(acceptedRoles)).orElseGet(() -> Boolean.FALSE).booleanValue();
        }
        return false;
    }

    @Override
    public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException,
            ServletException {
        String path = request.getServletPath().substring(pathPrefix.length());
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        final Method httpMethod = Method.valueOf(request.getMethod().toUpperCase(Locale.ROOT));
        final CurlRequest curlRequest = new CurlRequest(httpMethod, getUrl() + path);
        request.getParameterMap().entrySet().stream().forEach(entry -> {
            if (entry.getValue().length > 1) {
                curlRequest.param(entry.getKey(), String.join(",", entry.getValue()));
            } else if (entry.getValue().length == 1) {
                curlRequest.param(entry.getKey(), entry.getValue()[0]);
            }
        });
        curlRequest.onConnect((req, con) -> {
            con.setDoOutput(true);
            if (httpMethod != Method.GET) {
                try (ServletInputStream in = request.getInputStream(); OutputStream out = con.getOutputStream()) {
                    CopyUtil.copy(in, out);
                } catch (final IOException e) {
                    throw new IORuntimeException(e);
                }
            }
        }).execute(con -> {
            try (InputStream in = con.getInputStream(); ServletOutputStream out = response.getOutputStream()) {
                response.setStatus(con.getResponseCode());
                CopyUtil.copy(in, out);
            } catch (final IOException e) {
                try (InputStream err = con.getErrorStream()) {
                    logger.error(new String(InputStreamUtil.getBytes(err), Constants.CHARSET_UTF_8));
                } catch (final IOException e1) {}
                throw new IORuntimeException(e);
            }
        });
        // TODO exception
    }

    public void setAcceptedRoles(final String[] acceptedRoles) {
        this.acceptedRoles = acceptedRoles;
    }

    protected String getUrl() {
        return crawlerProperties.getProperty(Constants.ELASTICSEARCH_WEB_URL_PROPERTY, Constants.ELASTICSEARCH_WEB_URL);
    }
}
