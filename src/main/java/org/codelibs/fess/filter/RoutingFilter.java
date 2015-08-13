package org.codelibs.fess.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.lastaflute.web.LastaFilter;

public class RoutingFilter extends LastaFilter {

    @Override
    public void doFilter(ServletRequest servReq, ServletResponse servRes, FilterChain chain) throws IOException, ServletException {
        routingFilter.doFilter(servReq, servRes, prepareFinalChain(chain)); /* #to_action */
    }

}
