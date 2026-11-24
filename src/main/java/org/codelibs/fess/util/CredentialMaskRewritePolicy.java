/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.util;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;

/**
 * Log4j rewrite policy that removes credentials from the log events of the specified loggers.
 * It is meant for the wire and header loggers of the HTTP client, which write every request and
 * response as it is on the wire: the {@code Authorization} header of a request, the {@code Cookie} and
 * {@code Set-Cookie} headers, the cookie values that the cookie handlers of the client log
 * ({@code Cookie accepted [name="value", ...]} and {@code Cookie [name: n; value: v; ...] match}), and the
 * {@code user:password@} of a URL that the server sends back in a {@code Location} header or in a page. The body of a request, such as the login form that the form
 * authentication posts, is not masked.
 */
@Plugin(name = "CredentialMaskRewritePolicy", category = Core.CATEGORY_NAME, elementType = "rewritePolicy", printObject = true)
public class CredentialMaskRewritePolicy implements RewritePolicy {

    /** The text that replaces the value of an Authorization or Cookie header. */
    protected static final String MASK = "****";

    /** The userinfo (user:password@) in the authority of a URL, as it appears in a text. */
    private static final Pattern USER_INFO = Pattern.compile("(?i)(\\b[a-z][a-z0-9+.-]*://)[^/?#\\s@\"'<>\\[\\]]+@");

    /**
     * The value of an Authorization or Proxy-Authorization header, up to the end of the line (or the wire log's CR LF mark).
     * A scheme such as {@code Basic} or {@code Bearer} is kept; a value without one is masked as a whole.
     */
    private static final Pattern AUTHORIZATION =
            Pattern.compile("(?i)\\b((?:proxy-)?authorization:\\s*)([a-z0-9_-]+\\s+)?(?:(?!\\[\\\\r\\]).)+");

    /** The value of a Cookie or Set-Cookie header, up to the end of the line (or the wire log's CR LF mark). */
    private static final Pattern COOKIE = Pattern.compile("(?i)\\b((?:set-)?cookie:\\s*)(?:(?!\\[\\\\r\\]).)+");

    /** The value in the {@code Cookie accepted [name="value", ...]} and {@code Cookie rejected [...]} lines of the cookie handler. */
    private static final Pattern COOKIE_ACCEPTED = Pattern.compile("(?i)(\\bcookie (?:accepted|rejected) \\[[^=\\]\"]*=)\"[^\"]*\"");

    /** The value in the {@code Cookie [name: n; value: v; ...] match [...]} line of the cookie handler. */
    private static final Pattern COOKIE_MATCH = Pattern.compile("(?i)(\\bcookie \\[name: [^;\\]]*; value: )[^;\\]]*");

    /** Array of logger name prefixes to apply the masking to */
    private final String[] loggerNames;

    /**
     * Constructs a new CredentialMaskRewritePolicy with the specified logger names.
     *
     * @param loggerNames array of logger name prefixes whose log events are masked
     */
    public CredentialMaskRewritePolicy(final String[] loggerNames) {
        this.loggerNames = loggerNames;
    }

    /**
     * Masks the credentials in a text.
     *
     * @param text the text (may be null)
     * @return the text without the userinfo of URLs and with the value of Authorization and Cookie headers replaced, or the text itself when there is none
     */
    public static String mask(final String text) {
        if (text == null) {
            return null;
        }
        String result = text;
        if (result.indexOf('@') >= 0) {
            result = USER_INFO.matcher(result).replaceAll("$1");
        }
        if (containsIgnoreCase(result, "authorization:")) {
            result = AUTHORIZATION.matcher(result).replaceAll("$1$2" + MASK);
        }
        if (containsIgnoreCase(result, "cookie:")) {
            result = COOKIE.matcher(result).replaceAll("$1" + MASK);
        }
        if (containsIgnoreCase(result, "cookie ")) {
            result = COOKIE_ACCEPTED.matcher(result).replaceAll("$1\"" + MASK + "\"");
            result = COOKIE_MATCH.matcher(result).replaceAll("$1" + MASK);
        }
        return result;
    }

    private static boolean containsIgnoreCase(final String text, final String word) {
        return text.toLowerCase(Locale.ROOT).contains(word);
    }

    /**
     * Rewrites log events by masking the credentials in the message of matching loggers.
     *
     * @param event the log event to potentially rewrite
     * @return the original event or a new event with the masked message
     */
    @Override
    public LogEvent rewrite(final LogEvent event) {
        final String loggerName = event.getLoggerName();
        if (loggerName == null || event.getMessage() == null) {
            return event;
        }
        for (final String name : loggerNames) {
            if (loggerName.startsWith(name)) {
                final String message = event.getMessage().getFormattedMessage();
                final String masked = mask(message);
                if (masked.equals(message)) {
                    return event;
                }
                return new Log4jLogEvent.Builder(event).setMessage(new SimpleMessage(masked)).build();
            }
        }
        return event;
    }

    /**
     * Factory method to create a CredentialMaskRewritePolicy instance.
     *
     * @param loggerNamePrefix comma-separated list of logger name prefixes
     * @return a new CredentialMaskRewritePolicy instance
     */
    @PluginFactory
    public static CredentialMaskRewritePolicy createPolicy(@PluginAttribute("loggers") final String loggerNamePrefix) {
        final String[] loggerNames = loggerNamePrefix != null
                ? Arrays.stream(loggerNamePrefix.split(",")).map(String::trim).filter(s -> s.length() > 0).toArray(n -> new String[n])
                : new String[0];
        return new CredentialMaskRewritePolicy(loggerNames);
    }

}
