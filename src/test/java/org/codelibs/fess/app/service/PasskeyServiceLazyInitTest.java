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
package org.codelibs.fess.app.service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpSession;

/**
 * {@link PasskeyService} is injected into the login page, the user deletion and the admin user
 * page, so creating it and asking {@link PasskeyService#isEnabled()} must not touch webauthn4j: a
 * broken webauthn4j or Jackson would break the password login otherwise.
 *
 * <p>The service is loaded by its own class loader that refuses every webauthn4j class, so the test
 * does not depend on whether another test of the same JVM already initialized the holder.</p>
 */
public class PasskeyServiceLazyInitTest extends UnitFessTestCase {

    private static final String HOLDER = PasskeyService.class.getName() + "$WebAuthn";

    @Test
    public void test_creatingTheServiceAndIsEnabled_doNotLoadWebAuthn4j() throws Exception {
        final IsolatingLoader loader = new IsolatingLoader(getClass().getClassLoader());
        final Class<?> type = loader.loadClass(PasskeyService.class.getName());
        assertSame(loader, type.getClassLoader());
        // what the DI container looks at
        type.getDeclaredMethods();
        type.getDeclaredFields();
        type.getDeclaredConstructors();

        for (final boolean enabled : new boolean[] { true, false }) {
            final Object service = newService(type, enabled);
            assertEquals(enabled, type.getMethod("isEnabled").invoke(service));
        }

        assertFalse(loader.requested.contains(HOLDER), loader.requested.toString());
        assertTrue(loader.requested.stream().noneMatch(n -> n.startsWith("com.webauthn4j.")), loader.requested.toString());
    }

    @Test
    public void test_theLoaderSeesWhenAVerificationUsesWebAuthn4j() throws Exception {
        // the check above is not vacuous: a verification does reach the holder and webauthn4j
        org.codelibs.fess.util.ComponentUtil.register(new org.codelibs.fess.helper.SystemHelper(), "systemHelper");
        final IsolatingLoader loader = new IsolatingLoader(getClass().getClassLoader());
        final Class<?> type = loader.loadClass(PasskeyService.class.getName());
        final Object service = newService(type, true);
        final HttpSession session = session();
        type.getMethod("createLoginOptions", HttpSession.class).invoke(service, session);
        assertFalse(loader.requested.contains(HOLDER), "login options need no webauthn4j");

        final InvocationTargetException e = assertThrows(InvocationTargetException.class,
                () -> type.getMethod("verifyLogin", HttpSession.class, String.class).invoke(service, session, "{}"));
        assertTrue(e.getCause() instanceof LinkageError, String.valueOf(e.getCause()));
        assertTrue(loader.requested.contains(HOLDER), loader.requested.toString());
    }

    private static <T extends Throwable> T assertThrows(final Class<T> type, final org.junit.jupiter.api.function.Executable executable) {
        return org.junit.jupiter.api.Assertions.assertThrows(type, executable);
    }

    private static Object newService(final Class<?> type, final boolean enabled) throws Exception {
        final Object service = type.getConstructor().newInstance();
        final Field field = type.getDeclaredField("fessConfig");
        field.setAccessible(true);
        field.set(service, new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isPasskeyEnabled() {
                return enabled;
            }

            @Override
            public String getPasskeyRpId() {
                return "example.com";
            }

            @Override
            public String getPasskeyOrigins() {
                return "https://example.com";
            }
        });
        return service;
    }

    private static HttpSession session() {
        final Map<String, Object> attributes = new HashMap<>();
        return (HttpSession) Proxy.newProxyInstance(PasskeyServiceLazyInitTest.class.getClassLoader(), new Class<?>[] { HttpSession.class },
                (proxy, method, args) -> switch (method.getName()) {
                case "getAttribute" -> attributes.get(args[0]);
                case "setAttribute" -> attributes.put((String) args[0], args[1]);
                case "removeAttribute" -> attributes.remove(args[0]);
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> null;
                });
    }

    /**
     * Defines {@link PasskeyService} and its nested classes itself, records every class they ask
     * for, and refuses webauthn4j.
     */
    private static class IsolatingLoader extends ClassLoader {

        final Set<String> requested = Collections.synchronizedSet(new LinkedHashSet<>());

        IsolatingLoader(final ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                requested.add(name);
                if (name.startsWith("com.webauthn4j.")) {
                    throw new ClassNotFoundException(name);
                }
                final String serviceName = PasskeyService.class.getName();
                if (name.equals(serviceName) || name.startsWith(serviceName + "$")) {
                    Class<?> c = findLoadedClass(name);
                    if (c == null) {
                        try (InputStream in = getParent().getResourceAsStream(name.replace('.', '/') + ".class")) {
                            if (in == null) {
                                throw new ClassNotFoundException(name);
                            }
                            final byte[] bytes = in.readAllBytes();
                            c = defineClass(name, bytes, 0, bytes.length);
                        } catch (final IOException e) {
                            throw new ClassNotFoundException(name, e);
                        }
                    }
                    return c;
                }
                return super.loadClass(name, resolve);
            }
        }
    }
}
