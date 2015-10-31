package org.codelibs.fess.api.suggest;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codelibs.fess.api.BaseApiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuggestApiManager extends BaseApiManager {
    private static final Logger logger = LoggerFactory.getLogger(SuggestApiManager.class);

    public SuggestApiManager() {
        setPathPrefix("/suggest");
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        return false; // TODO remove
        //        final String servletPath = request.getServletPath();
        //        return servletPath.startsWith(pathPrefix);
    }

    @Override
    public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException,
            ServletException {
        throw new UnsupportedOperationException("TODO");
    }

}
