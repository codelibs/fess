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
package org.codelibs.fess.app.web.base.login;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.PasswordHashHelper;
import org.codelibs.fess.ldap.LdapManager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.sso.SsoManager;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.web.login.credential.LoginCredential;

/**
 * Tests how {@link FessLoginAssist} resolves a {@link PasskeyCredential}: only to a row of the local
 * user index, with no password check and no directory lookup.
 */
public class FessLoginAssistPasskeyTest extends UnitFessTestCase {

    private static final Set<String> ALLOWED_CALLERS = Set.of("PasskeyLoginHandler.java");

    private PasswordHashHelper passwordHashHelper;

    private PasskeyLoginAssist loginAssist;

    private final LdapManager ldapManager = new LdapManager() {
        @Override
        public OptionalEntity<FessUser> login(final String typedUsername, final String password) {
            fail("the directory must not be asked: " + typedUsername);
            return OptionalEntity.empty();
        }

        @Override
        public OptionalEntity<FessUser> login(final String username) {
            fail("the directory must not be asked: " + username);
            return OptionalEntity.empty();
        }
    };

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        final FessConfig config = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getAuthenticationAdminUsers() {
                return "admin";
            }

            @Override
            public String getAuthenticationAdminUsersIgnoreCase() {
                return "false";
            }

            @Override
            public Integer getLdapMaxUsernameLengthAsInteger() {
                return Integer.valueOf(-1);
            }

            @Override
            public String getAppPasswordAlgorithm() {
                return "bcrypt";
            }

            @Override
            public Integer getAppPasswordBcryptCostAsInteger() {
                return Integer.valueOf(4);
            }

            @Override
            public String getAppPasswordBcryptCost() {
                return "4";
            }

            @Override
            public boolean isAppPasswordUpgradeEnabled() {
                return false;
            }

            @Override
            public String getAppPasswordUpgradeEnabled() {
                return "false";
            }
        };
        ComponentUtil.setFessConfig(config);
        passwordHashHelper = new PasswordHashHelper();
        ComponentUtil.register(passwordHashHelper, "passwordHashHelper");
        ComponentUtil.register(ldapManager, "ldapManager");
        ComponentUtil.register(new SsoManager(), "ssoManager");
        loginAssist = new PasskeyLoginAssist();
        setFessConfigField(loginAssist, config);
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    @Test
    public void test_login_passkeyCredential_resolvesLocalUser() {
        loginAssist.stored = newUser("alice", passwordHashHelper.encode("wonderland"));

        final OptionalEntity<FessUser> result = resolve(PasskeyCredential.verified("alice"));

        assertTrue(result.isPresent());
        assertEquals("alice", ((User) result.get()).getName());
    }

    @Test
    public void test_login_passkeyCredential_unknownUser_fails() {
        loginAssist.stored = newUser("alice", passwordHashHelper.encode("wonderland"));

        assertFalse(resolve(PasskeyCredential.verified("mallory")).isPresent());
    }

    @Test
    public void test_login_passkeyCredential_blankUser_fails() {
        loginAssist.stored = newUser("alice", passwordHashHelper.encode("wonderland"));

        assertFalse(resolve(PasskeyCredential.verified(null)).isPresent());
        assertFalse(resolve(PasskeyCredential.verified("")).isPresent());
        assertFalse(resolve(PasskeyCredential.verified("  ")).isPresent());
        // a blank name must not even reach the index: an unset equality condition would match any row
        assertEquals(0, loginAssist.lookups);
    }

    @Test
    public void test_login_passkeyCredential_doesNotCallLdap() {
        loginAssist.stored = newUser("alice", passwordHashHelper.encode("wonderland"));

        // the stub LdapManager fails the test if it is asked, whether the user is known or not
        assertTrue(resolve(PasskeyCredential.verified("alice")).isPresent());
        assertFalse(resolve(PasskeyCredential.verified("mallory")).isPresent());
    }

    @Test
    public void test_login_passwordCredential_unchanged() {
        loginAssist.stored = newUser("alice", passwordHashHelper.encode("wonderland"));
        // a password login still prefers the directory for a name it may be asked about
        ComponentUtil.register(new LdapManager() {
            @Override
            public OptionalEntity<FessUser> login(final String typedUsername, final String password) {
                return OptionalEntity.empty();
            }
        }, "ldapManager");

        assertTrue(resolve(new LocalUserCredential("alice", "wonderland")).isPresent());
        assertFalse(resolve(new LocalUserCredential("alice", "WRONG")).isPresent());
    }

    @Test
    public void test_verified_isOnlyCalledFromHandlers() throws IOException {
        final Path mainDir = findMainSourceDir();
        final List<String> offenders;
        try (Stream<Path> files = Files.walk(mainDir)) {
            offenders = files.filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.getFileName().toString().equals("PasskeyCredential.java"))
                    .filter(p -> !ALLOWED_CALLERS.contains(p.getFileName().toString()))
                    .filter(p -> contains(p, "PasskeyCredential.verified("))
                    .map(p -> mainDir.relativize(p).toString())
                    .collect(Collectors.toList());
        }
        assertTrue("PasskeyCredential.verified( may only be called from " + ALLOWED_CALLERS + ", found in " + offenders,
                offenders.isEmpty());
    }

    private static boolean contains(final Path path, final String text) {
        try {
            return Files.readString(path).contains(text);
        } catch (final IOException e) {
            throw new IllegalStateException(path.toString(), e);
        }
    }

    private static Path findMainSourceDir() {
        Path dir = Paths.get("").toAbsolutePath();
        while (dir != null) {
            final Path candidate = dir.resolve("src").resolve("main").resolve("java");
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
            dir = dir.getParent();
        }
        throw new IllegalStateException("src/main/java not found above " + Paths.get("").toAbsolutePath());
    }

    private OptionalEntity<FessUser> resolve(final LoginCredential credential) {
        return loginAssist.resolveWith(credential);
    }

    private static User newUser(final String name, final String password) {
        final User user = new User();
        user.setName(name);
        user.setPassword(password);
        return user;
    }

    private static void setFessConfigField(final FessLoginAssist assist, final FessConfig config) {
        try {
            final Field f = FessLoginAssist.class.getDeclaredField("fessConfig");
            f.setAccessible(true);
            f.set(assist, config);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static class PasskeyLoginAssist extends FessLoginAssist {
        User stored;

        int lookups;

        OptionalEntity<FessUser> resolveWith(final LoginCredential credential) {
            final CredentialResolver resolver = new CredentialResolver(credential);
            resolveCredential(resolver);
            return resolver.getResolvedEntity();
        }

        @Override
        protected OptionalEntity<FessUser> doFindLoginUser(final String username) {
            lookups++;
            return stored != null && stored.getName().equals(username) ? OptionalEntity.of(stored) : OptionalEntity.empty();
        }
    }
}
