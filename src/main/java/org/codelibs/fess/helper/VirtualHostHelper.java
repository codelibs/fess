package org.codelibs.fess.helper;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.function.Function;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.response.next.HtmlNext;
import org.lastaflute.web.util.LaRequestUtil;

public class VirtualHostHelper {

    public HtmlNext getVirtualHostPath(final HtmlNext page) {
        return processVirtualHost(s -> {
            final String basePath = getVirtualHostBasePath(s, page);
            return new HtmlNext(basePath + page.getRoutingPath());
        }, page);
    }

    protected String getVirtualHostBasePath(final String s, final HtmlNext page) {
        return StringUtil.isBlank(s) ? StringUtil.EMPTY : "/" + s;
    }

    public String getVirtualHostKey() {
        return LaRequestUtil.getOptionalRequest().map(req -> (String) req.getAttribute(FessConfig.VIRTUAL_HOST_VALUE)).orElseGet(() -> {
            final String value = processVirtualHost(s -> s, StringUtil.EMPTY);
            LaRequestUtil.getOptionalRequest().ifPresent(req -> req.setAttribute(FessConfig.VIRTUAL_HOST_VALUE, value));
            return value;
        });
    }

    protected <T> T processVirtualHost(final Function<String, T> func, final T defaultValue) {
        final Tuple3<String, String, String>[] vHosts = ComponentUtil.getFessConfig().getVirtualHosts();
        return LaRequestUtil.getOptionalRequest().map(req -> {
            for (final Tuple3<String, String, String> host : vHosts) {
                final String headerValue = req.getHeader(host.getValue1());
                if (host.getValue2().equalsIgnoreCase(headerValue)) {
                    return func.apply(host.getValue3());
                }
            }
            return defaultValue;
        }).orElse(defaultValue);
    }

    public String[] getVirtualHostPaths() {
        return stream(ComponentUtil.getFessConfig().getVirtualHosts()).get(
                stream -> stream.map(h -> "/" + h.getValue3()).toArray(n -> new String[n]));
    }
}
