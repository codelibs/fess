package org.codelibs.fess.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.lastaflute.web.LastaFilter;
import org.lastaflute.web.servlet.filter.RequestRoutingFilter;

public class RoutingFilter implements Filter {

    protected RequestRoutingFilter routingFilter;

    @Override
    public void doFilter(ServletRequest servReq, ServletResponse servRes, FilterChain chain) throws IOException, ServletException {
        routingFilter.doFilter(servReq, servRes, prepareFinalChain(chain)); /* #to_action */
    }

    protected FilterChain prepareFinalChain(FilterChain chain) {
        return chain;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        routingFilter = createRequestRoutingFilter();
        routingFilter.init(filterConfig);
    }

    protected RequestRoutingFilter createRequestRoutingFilter() {
        return new RequestRoutingFilter();
    }

    @Override
    public void destroy() {
        if (routingFilter != null) { // just in case
            routingFilter.destroy();
        }
    }

}
