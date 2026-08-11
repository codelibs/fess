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
package org.codelibs.fess.sso.spnego;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.codelibs.spnego.SpnegoProvider;
import org.junit.jupiter.api.Test;

import javax.security.auth.kerberos.KerberosPrincipal;

/**
 * Pins {@link SpnegoAuthenticator#getBasicRealm(String)} against the spnego library's own header
 * parser instead of against hardcoded expectations.
 *
 * <p>
 * Why this exists: on the Basic path, the {@code spnego.allowed.realms} check is the only place the
 * client-chosen Kerberos realm is ever inspected. After authentication the principal always carries
 * the <em>server</em> realm, so a header this check cannot read is a header the allow list cannot
 * govern. {@code getBasicRealm} used to split the scheme from the token on a literal space while
 * the library skips any run of whitespace (or none at all), so {@code Basic<tab>token} and
 * {@code Basictoken} were authenticated by the library while the allow list saw no realm -- a
 * complete bypass. The fix works by mirroring the library's parser, which means the two parsers can
 * silently drift apart again the next time the library is upgraded.
 * </p>
 *
 * <p>
 * The sibling {@code SpnegoAuthenticatorTest} hardcodes the expected realms: it pins Fess's
 * behaviour but never executes the library, so a change to {@code SpnegoProvider#parseAuthHeader}
 * or to the library's base64 decoder would leave both suites green while the bypass returned. This
 * test instead drives the real library parser for every header shape and compares what the library
 * would authenticate against what Fess extracts.
 * </p>
 *
 * <p>
 * <b>Deliberately not an equality assertion -- please do not "tighten" it into one.</b> The
 * security property is one-directional: Fess must never be <em>less</em> restrictive than the
 * library, but it is free to be more. So for every header shape where the library would
 * authenticate a credential naming realm {@code R}, this test requires only that Fess resolves a
 * realm that is at least as narrow as {@code R}: never {@code null} (which means no check runs at
 * all -- the original bypass), and never a value unrelated to {@code R} (which could match some
 * other allow-list entry). A value that merely <em>ends with</em> {@code "@" + R} is accepted: an
 * allow-list entry is a Kerberos realm name and contains no {@code '@'}, so such a value can only
 * ever fail a list that {@code R} would pass, i.e. it over-rejects, which is safe. Asserting exact
 * equality here would also couple this test to whether the user name is split at the first or the
 * last {@code '@'}, which is a separate concern being changed independently.
 * </p>
 *
 * <p>
 * {@code SpnegoProvider#getAuthScheme} is public, but the {@code SpnegoAuthScheme} it returns is a
 * package-private final class whose {@code isBasicScheme()} / {@code getToken()} are package
 * private too, so they are reached by reflection. Both the library and Fess load from the class
 * path (the unnamed module), so {@code setAccessible(true)} succeeds without any
 * {@code --add-opens}.
 * </p>
 */
public class SpnegoBasicRealmLibraryParityTest {

    /** Credentials whose realm is named by the header itself, used by the separator shapes. */
    private static final String REALM_CREDENTIALS = "alice@PARTNER.EXAMPLE:secret";

    /**
     * Every {@code Authorization} header shape the parity contract is checked over.
     *
     * <p>
     * The separator shapes are spelled out literally on purpose: routing them through a helper that
     * hardcodes {@code "Basic "} would hide the exact divergence this test exists to catch.
     * </p>
     *
     * @return the header shapes to check
     */
    private static List<String> headerShapes() {
        final String token = token(REALM_CREDENTIALS);
        return List.of(//
                // The separator between the scheme and the token: the library skips a run of any
                // whitespace, possibly empty, and matches the scheme case-insensitively.
                "Basic " + token, //
                "Basic\t" + token, //
                "Basic" + token, //
                "basic  " + token, //
                "BASIC" + token, //
                "Basic\f" + token, //
                "Basic \n" + token, //
                "Basic  " + token + "  ", //
                // Whitespace inside the token, which neither side can base64-decode.
                "Basic " + token.substring(0, 4) + " " + token.substring(4), //
                // Credential shapes: NetBIOS prefix, a second '@', no colon, no realm, empty realm.
                "Basic " + token("CORP\\alice@REALM.EXAMPLE:secret"), //
                "Basic " + token("alice@sub@NESTED.EXAMPLE:secret"), //
                "Basic " + token("alice-without-a-colon"), //
                "Basic " + token("alice:secret"), //
                "Basic " + token("alice@:secret"), //
                // A scheme with no token at all.
                "Basic ", //
                "Basic", //
                // Schemes that carry no Basic credentials.
                "Negotiate " + token, //
                "Bearer " + token, //
                // A truncated token (still decodable) and a token that is not base64 at all.
                "Basic " + token.substring(0, token.length() - 1), //
                "Basic ????");
    }

    /**
     * Requires Fess's realm extraction to be no weaker than the library's own parse of the same
     * header, for every shape in {@link #headerShapes()}.
     */
    @Test
    public void test_basicRealmIsNeverWeakerThanTheLibraryParse() {
        // Guards against a vacuous run: if the harness stopped reaching the library parser, every
        // shape below would be skipped and the loop would assert nothing.
        assertNotNull(libraryRealm("Basic " + token(REALM_CREDENTIALS)),
                "the harness no longer reaches the library parser: even a canonical 'Basic <token>' header resolves to no realm");

        final List<String> governed = new ArrayList<>();
        for (final String header : headerShapes()) {
            final String libraryRealm = libraryRealm(header);
            if (libraryRealm == null) {
                // The library authenticates no client-named realm here, so there is nothing for
                // spnego.allowed.realms to govern and Fess may resolve whatever it likes.
                continue;
            }
            governed.add(header);
            final String fessRealm = SpnegoAuthenticator.getBasicRealm(header);
            assertNotNull(fessRealm, visible(header) + ": the library authenticates realm " + libraryRealm
                    + ", but the allow-list check reads no realm at all, so spnego.allowed.realms cannot govern this header");
            assertTrue(libraryRealm.equals(fessRealm) || fessRealm.endsWith("@" + libraryRealm),
                    visible(header) + ": the library authenticates realm " + libraryRealm
                            + ", but the allow-list check reads the unrelated realm " + fessRealm
                            + ", which a different allow-list entry could match");
        }
        assertTrue(governed.size() > 1, "no header shape reached the parity check beyond the canonical one");
    }

    /**
     * Returns the Kerberos realm the library would authenticate the given header against, or null
     * when the library authenticates no realm named by the header itself.
     *
     * <p>
     * The scheme and token are parsed by the real {@code SpnegoProvider}; the steps after that
     * follow {@code org.codelibs.spnego.SpnegoAuthenticator#doBasicAuth}, which decodes the token as
     * UTF-8, splits it on the first colon, drops a NetBIOS {@code DOMAIN\} prefix and hands the
     * remainder to Kerberos as the principal name. The realm is then derived by
     * {@link KerberosPrincipal}, not by this test.
     * </p>
     *
     * @param authzHeader the raw Authorization header value
     * @return the realm the library would use, or null when it would authenticate nothing, would
     *         reject the credential, or would fall back to the local default realm
     */
    private static String libraryRealm(final String authzHeader) {
        final Object scheme;
        try {
            scheme = SpnegoProvider.getAuthScheme(authzHeader);
        } catch (final UnsupportedOperationException e) {
            // Neither Negotiate nor Basic, or a scheme with no token: nobody is authenticated.
            return null;
        }
        if (scheme == null || !((Boolean) invoke(scheme, "isBasicScheme")).booleanValue()) {
            return null;
        }
        final byte[] data;
        try {
            data = (byte[]) invoke(scheme, "getToken");
        } catch (final IllegalArgumentException e) {
            // The library's own base64 decoder refused the token.
            return null;
        }
        if (data.length == 0) {
            return null;
        }
        final String[] basicData = new String(data, StandardCharsets.UTF_8).split(":", 2);
        if (basicData.length != 2) {
            // doBasicAuth throws IllegalArgumentException for a token without a colon.
            return null;
        }
        final String username = basicData[0].substring(basicData[0].indexOf('\\') + 1);
        if (username.indexOf('@') < 0) {
            // The header names no realm, so Kerberos would use the local default realm rather than
            // one the client chose. That is the server's own realm, which the allow list does not
            // govern -- and resolving it here would depend on the krb5 config of the build machine.
            return null;
        }
        try {
            return new KerberosPrincipal(username, KerberosPrincipal.KRB_NT_PRINCIPAL).getRealm();
        } catch (final IllegalArgumentException e) {
            // Not a principal name Kerberos accepts, so the login never leaves the server.
            return null;
        }
    }

    /**
     * Invokes a package-private no-argument method of the library's scheme object.
     *
     * @param scheme the {@code SpnegoAuthScheme} returned by the library
     * @param methodName the method to invoke
     * @return the method's return value
     */
    private static Object invoke(final Object scheme, final String methodName) {
        try {
            final Method method = scheme.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(scheme);
        } catch (final InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw new AssertionError(methodName + "() failed on " + scheme.getClass().getName(), e.getCause());
        } catch (final ReflectiveOperationException e) {
            throw new AssertionError(
                    "cannot reach " + methodName + "() on " + scheme.getClass().getName() + "; the library's scheme type changed", e);
        }
    }

    /**
     * Base64-encodes credentials. Only the credentials: the header itself is always spelled out at
     * the call site so that no separator is hidden behind a helper.
     *
     * @param credentials the decoded {@code user:password} string
     * @return the base64 token
     */
    private static String token(final String credentials) {
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Escapes the whitespace that distinguishes the header shapes so a failure message names the
     * shape that failed.
     *
     * @param header the raw header value
     * @return the header with its break characters escaped
     */
    private static String visible(final String header) {
        return header.replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r").replace("\f", "\\f");
    }
}
