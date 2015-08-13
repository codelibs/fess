package org.codelibs.fess.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Deque;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lastaflute.web.LastaFilter;
import org.lastaflute.web.servlet.filter.listener.FilterHook;

public class DIFilter extends LastaFilter {

    @Override
    protected void resolveCharacterEncoding(HttpServletRequest request) throws UnsupportedEncodingException {
        // nothing
    }

    @Override
    protected void viaInsideHookDeque(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Deque<FilterHook> deque)
            throws IOException, ServletException {
        if (deque != null && !deque.isEmpty()) {
            super.viaInsideHookDeque(request, response, chain, deque);
        } else {
            routingFilter.doFilter(request, response, prepareFinalChain(chain)); /* #to_action */
        }
    }
}
