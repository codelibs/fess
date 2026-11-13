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

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.exentity.Passkey;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.session.SessionDataSerializer;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.function.Executable;

import com.webauthn4j.converter.AttestationObjectConverter;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import com.webauthn4j.data.PublicKeyCredentialRequestOptions;
import com.webauthn4j.data.attestation.AttestationObject;
import com.webauthn4j.data.attestation.authenticator.AuthenticatorData;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.extension.authenticator.RegistrationExtensionAuthenticatorOutput;
import com.webauthn4j.test.authenticator.webauthn.NoneAttestationAuthenticator;
import com.webauthn4j.test.authenticator.webauthn.WebAuthnAuthenticatorAdaptor;
import com.webauthn4j.test.client.ClientPlatform;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

/**
 * The feature gate of {@link PasskeyService}, and its registration and login ceremonies driven end to
 * end by the emulated client and authenticator of webauthn4j-test, so that the real CBOR attestation
 * and assertion data pass through the parser and the verifier.
 */
public class PasskeyServiceTest extends UnitFessTestCase {

    private static final String RP_ID = "localhost";

    private static final String ORIGIN = "http://localhost:8080";

    private static final ObjectConverter CONVERTER = new ObjectConverter();

    private InMemoryPasskeyService service;

    private StubHttpSession session;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        service = new InMemoryPasskeyService();
        service.rpId = RP_ID;
        service.users.add("alice");
        service.users.add("bob");
        session = new StubHttpSession();
    }

    // -----------------------------------------------------------------------
    // Feature gate
    // -----------------------------------------------------------------------

    private PasskeyService createService(final boolean enabled, final String rpId, final String origins) {
        final PasskeyService service = new PasskeyService();
        service.fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isPasskeyEnabled() {
                return enabled;
            }

            @Override
            public String getPasskeyRpId() {
                return rpId;
            }

            @Override
            public String getPasskeyOrigins() {
                return origins;
            }
        };
        return service;
    }

    @Test
    public void test_isEnabled_falseByDefault() {
        final PasskeyService service = new PasskeyService();
        service.fessConfig = ComponentUtil.getFessConfig();
        assertFalse(service.isEnabled());
    }

    @Test
    public void test_isEnabled_falseWhenDisabled() {
        assertFalse(createService(false, "example.com", "https://example.com").isEnabled());
    }

    @Test
    public void test_isEnabled_falseWhenRpIdBlank() {
        assertFalse(createService(true, "", "https://example.com").isEnabled());
        assertFalse(createService(true, "  ", "https://example.com").isEnabled());
    }

    @Test
    public void test_isEnabled_falseWhenOriginsBlank() {
        assertFalse(createService(true, "example.com", "").isEnabled());
        assertFalse(createService(true, "example.com", "  ").isEnabled());
    }

    @Test
    public void test_isEnabled_true() {
        assertTrue(createService(true, "example.com", "https://example.com").isEnabled());
    }

    // -----------------------------------------------------------------------
    // Success paths
    // -----------------------------------------------------------------------

    @Test
    public void test_registerThenLogin_succeeds() {
        final ClientPlatform client = newClient(ORIGIN);
        final Passkey passkey = register(client, user("alice"), "laptop");

        assertEquals("alice", passkey.getUserName());
        assertEquals("laptop", passkey.getLabel());
        assertNotNull(passkey.getPublicKey());
        assertEquals(Long.valueOf(1000L), passkey.getCreatedAt());
        assertSame(passkey, service.store.get(passkey.getId()));
        assertEquals(32, Base64.getUrlDecoder().decode(passkey.getUserHandle()).length);

        service.now = 2000L;
        final String userName = service.verifyLogin(session, login(client));
        assertEquals("alice", userName);
        final Passkey stored = service.store.get(passkey.getId());
        assertEquals(Long.valueOf(2000L), stored.getLastUsedAt());
        assertTrue(stored.getSignCount() > 0L);
    }

    @Test
    public void test_registrationOptions_shape() {
        final Map<String, Object> options = service.createRegistrationOptions(session, user("alice"));
        @SuppressWarnings("unchecked")
        final Map<String, Object> rp = (Map<String, Object>) options.get("rp");
        assertEquals(RP_ID, rp.get("id"));
        assertEquals("Fess", rp.get("name"));
        assertEquals("none", options.get("attestation"));
        @SuppressWarnings("unchecked")
        final Map<String, Object> selection = (Map<String, Object>) options.get("authenticatorSelection");
        assertEquals("required", selection.get("residentKey"));
        assertEquals("required", selection.get("userVerification"));
        assertEquals(32, Base64.getUrlDecoder().decode((String) options.get("challenge")).length);
    }

    @Test
    public void test_loginOptions_shape() {
        final Map<String, Object> options = service.createLoginOptions(session);
        assertEquals(RP_ID, options.get("rpId"));
        assertEquals("required", options.get("userVerification"));
        assertEquals(Collections.emptyList(), options.get("allowCredentials"));
        assertEquals(32, Base64.getUrlDecoder().decode((String) options.get("challenge")).length);
    }

    // -----------------------------------------------------------------------
    // Challenge
    // -----------------------------------------------------------------------

    @Test
    public void test_login_challengeReplayed_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        register(client, user("alice"), "laptop");
        final String response = login(client);
        assertEquals("alice", service.verifyLogin(session, response));

        assertRejected("reason=no_challenge", () -> service.verifyLogin(session, response));
    }

    @Test
    public void test_login_challengeOlderThanFiveMinutes_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        register(client, user("alice"), "laptop");
        service.now = 10_000L;
        final String response = login(client);
        service.now = 10_000L + 5 * 60 * 1000L + 1L;

        assertRejected("reason=challenge_expired", () -> service.verifyLogin(session, response));
    }

    @Test
    public void test_login_challengeAtFiveMinutes_succeeds() {
        final ClientPlatform client = newClient(ORIGIN);
        register(client, user("alice"), "laptop");
        service.now = 10_000L;
        final String response = login(client);
        service.now = 10_000L + 5 * 60 * 1000L;

        assertEquals("alice", service.verifyLogin(session, response));
    }

    @Test
    public void test_login_withoutChallenge_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        register(client, user("alice"), "laptop");
        final String response = login(client);
        session.removeAttribute(PasskeyService.LOGIN_CHALLENGE_KEY);

        assertRejected("reason=no_challenge", () -> service.verifyLogin(session, response));
    }

    @Test
    public void test_login_failureStillConsumesChallenge() {
        final ClientPlatform client = newClient(ORIGIN);
        register(client, user("alice"), "laptop");
        final String response = login(client);

        assertRejected("reason=login_rejected", () -> service.verifyLogin(session, "{}"));
        assertNull(session.getAttribute(PasskeyService.LOGIN_CHALLENGE_KEY));
        assertRejected("reason=no_challenge", () -> service.verifyLogin(session, response));
    }

    @Test
    public void test_register_challengeOfAnotherUser_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        final String response = createCredential(client, user("alice"));

        assertRejected("reason=registration_user_mismatch", () -> service.verifyRegistration(session, user("bob"), "x", response));
        assertTrue(service.store.isEmpty());
    }

    @Test
    public void test_register_challengeReplayed_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        final String response = createCredential(client, user("alice"));
        service.verifyRegistration(session, user("alice"), "laptop", response);
        service.store.clear();

        assertRejected("reason=no_challenge", () -> service.verifyRegistration(session, user("alice"), "laptop", response));
    }

    @Test
    public void test_challenges_surviveTheSessionStoreSerializer() {
        // The OpenSearch session store writes only registered classes; anything else stays on the
        // node that set it, so a verification served by another node would find no challenge.
        final SessionDataSerializer serializer = new SessionDataSerializer();
        final ClientPlatform client = newClient(ORIGIN);
        final String registration = createCredential(client, user("alice"));
        final StubHttpSession registrationNode = copyThroughSerializer(serializer, session, PasskeyService.REGISTRATION_CHALLENGE_KEY);
        assertNotNull(service.verifyRegistration(registrationNode, user("alice"), "laptop", registration));

        final String assertion = login(client);
        final StubHttpSession loginNode = copyThroughSerializer(serializer, session, PasskeyService.LOGIN_CHALLENGE_KEY);
        assertEquals("alice", service.verifyLogin(loginNode, assertion));
    }

    @Test
    public void test_registrationChallenge_keepsItsUserAcrossTheSessionStore() {
        final SessionDataSerializer serializer = new SessionDataSerializer();
        final String response = createCredential(newClient(ORIGIN), user("alice"));
        final StubHttpSession other = copyThroughSerializer(serializer, session, PasskeyService.REGISTRATION_CHALLENGE_KEY);

        assertRejected("reason=registration_user_mismatch", () -> service.verifyRegistration(other, user("bob"), "x", response));
    }

    @Test
    public void test_consumeChallenge_isSingleUseUnderConcurrency() throws Exception {
        final java.util.concurrent.CountDownLatch firstInside = new java.util.concurrent.CountDownLatch(1);
        final java.util.concurrent.CountDownLatch release = new java.util.concurrent.CountDownLatch(1);
        final AtomicInteger reads = new AtomicInteger();
        // The first read parks inside the session, between reading the challenge and removing it.
        final StubHttpSession parking = new StubHttpSession() {
            @Override
            public Object getAttribute(final String name) {
                final Object value = super.getAttribute(name);
                if (reads.incrementAndGet() == 1) {
                    firstInside.countDown();
                    try {
                        release.await(5, java.util.concurrent.TimeUnit.SECONDS);
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                return value;
            }
        };
        service.createLoginOptions(parking);
        final List<Object> results = Collections.synchronizedList(new ArrayList<>());
        final Runnable consume = () -> {
            try {
                results.add(service.consumeChallenge(parking, PasskeyService.LOGIN_CHALLENGE_KEY));
            } catch (final PasskeyException e) {
                results.add(e.getMessage());
            }
        };
        final Thread first = new Thread(consume);
        first.start();
        assertTrue(firstInside.await(5, java.util.concurrent.TimeUnit.SECONDS));
        final Thread second = new Thread(consume);
        second.start();
        // give the second request the time to read the challenge too, if nothing stops it
        second.join(300L);
        release.countDown();
        first.join(5000L);
        second.join(5000L);

        assertEquals(2, results.size(), results.toString());
        assertEquals(1, (int) results.stream().filter(r -> r instanceof PasskeyService.PendingChallenge).count(), results.toString());
        assertTrue(results.contains("reason=no_challenge"), results.toString());
    }

    private StubHttpSession copyThroughSerializer(final SessionDataSerializer serializer, final StubHttpSession from, final String key) {
        final Object value = from.getAttribute(key);
        assertNotNull(value);
        final Set<String> skipped = new HashSet<>();
        final byte[] bytes = serializer.toBytes(Map.of(key, value), skipped);
        assertTrue(skipped.isEmpty(), "not written: " + skipped);
        final Map<String, Object> read = serializer.fromBytes(bytes);
        assertNotNull(read);
        assertTrue(read.containsKey(key), read.toString());
        final StubHttpSession to = new StubHttpSession();
        to.setAttribute(key, read.get(key));
        return to;
    }

    // -----------------------------------------------------------------------
    // Relying party checks
    // -----------------------------------------------------------------------

    @Test
    public void test_login_wrongOrigin_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        register(client, user("alice"), "laptop");
        client.setOrigin(new Origin("http://evil.example:8080"));
        final String response = login(client);

        assertRejected("error=BadOriginException", () -> service.verifyLogin(session, response));
    }

    @Test
    public void test_register_wrongOrigin_fails() {
        final ClientPlatform client = newClient("http://evil.example:8080");

        assertRejected("error=BadOriginException",
                () -> service.verifyRegistration(session, user("alice"), "laptop", createCredential(client, user("alice"))));
        assertTrue(service.store.isEmpty());
    }

    @Test
    public void test_login_secondConfiguredOrigin_succeeds() {
        service.origins = ORIGIN + " , https://fess.example.com";
        final ClientPlatform client = newClient(ORIGIN);
        register(client, user("alice"), "laptop");
        client.setOrigin(new Origin("https://fess.example.com"));

        assertEquals("alice", service.verifyLogin(session, login(client)));
    }

    @Test
    public void test_login_wrongRpId_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        register(client, user("alice"), "laptop");
        final String response = login(client);
        service.rpId = "example.com";

        assertRejected("error=BadRpIdException", () -> service.verifyLogin(session, response));
    }

    @Test
    public void test_register_wrongRpId_fails() {
        final String response = createCredential(newClient(ORIGIN), user("alice"));
        service.rpId = "example.com";

        assertRejected("error=BadRpIdException", () -> service.verifyRegistration(session, user("alice"), "laptop", response));
        assertTrue(service.store.isEmpty());
    }

    @Test
    public void test_login_userNotVerified_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        register(client, user("alice"), "laptop");
        final Map<String, Object> options = new LinkedHashMap<>(service.createLoginOptions(session));
        options.put("userVerification", "discouraged");
        final String response = credentialJson(client.get(toRequestOptions(options)));

        assertRejected("error=UserNotVerifiedException", () -> service.verifyLogin(session, response));
    }

    @Test
    public void test_register_userNotVerified_fails() {
        // The emulator always verifies the user at registration, so clear the UV bit afterwards: with
        // attestation none the authenticator data is not signed, which is what a forged response does.
        final String response = createCredential(newClient(ORIGIN), user("alice"));
        @SuppressWarnings("unchecked")
        final Map<String, Object> json = CONVERTER.getJsonMapper().readValue(response, Map.class);
        @SuppressWarnings("unchecked")
        final Map<String, Object> attestationResponse = (Map<String, Object>) json.get("response");
        final AttestationObjectConverter attestationObjectConverter = new AttestationObjectConverter(CONVERTER);
        final AttestationObject attestationObject =
                attestationObjectConverter.convert((String) attestationResponse.get("attestationObject"));
        final AuthenticatorData<RegistrationExtensionAuthenticatorOutput> authenticatorData = attestationObject.getAuthenticatorData();
        assertTrue(authenticatorData.isFlagUV());
        final AuthenticatorData<RegistrationExtensionAuthenticatorOutput> unverified =
                new AuthenticatorData<>(authenticatorData.getRpIdHash(), (byte) (authenticatorData.getFlags() & ~AuthenticatorData.BIT_UV),
                        authenticatorData.getSignCount(), authenticatorData.getAttestedCredentialData(), authenticatorData.getExtensions());
        attestationResponse.put("attestationObject", attestationObjectConverter
                .convertToBase64urlString(new AttestationObject(unverified, attestationObject.getAttestationStatement())));
        final String forged = CONVERTER.getJsonMapper().writeValueAsString(json);

        assertRejected("error=UserNotVerifiedException", () -> service.verifyRegistration(session, user("alice"), "laptop", forged));
        assertTrue(service.store.isEmpty());
    }

    @Test
    public void test_register_untamperedResponseViaMap_succeeds() {
        // the same JSON round trip as above, without the change, still verifies
        final String response = createCredential(newClient(ORIGIN), user("alice"));
        final String copied = CONVERTER.getJsonMapper().writeValueAsString(CONVERTER.getJsonMapper().readValue(response, Map.class));

        assertNotNull(service.verifyRegistration(session, user("alice"), "laptop", copied));
    }

    // -----------------------------------------------------------------------
    // Credential and user binding
    // -----------------------------------------------------------------------

    @Test
    public void test_login_userHandleMismatch_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        final Passkey passkey = register(client, user("alice"), "laptop");
        passkey.setUserHandle(Base64.getUrlEncoder().withoutPadding().encodeToString(new byte[32]));
        final String response = login(client);

        assertRejected("reason=user_handle_mismatch", () -> service.verifyLogin(session, response));
        assertNull(service.store.get(passkey.getId()).getLastUsedAt());
    }

    @Test
    public void test_login_unknownCredential_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        final Passkey passkey = register(client, user("alice"), "laptop");
        service.store.remove(passkey.getId());

        assertRejected("reason=unknown_credential", () -> service.verifyLogin(session, login(client)));
    }

    @Test
    public void test_login_credentialOfDeletedUser_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        final Passkey passkey = register(client, user("alice"), "laptop");
        service.users.remove("alice");
        final String response = login(client);

        assertRejected("reason=unknown_user", () -> service.verifyLogin(session, response));
        assertNull(service.store.get(passkey.getId()).getLastUsedAt());
    }

    @Test
    public void test_register_duplicateCredentialId_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        final String response = createCredential(client, user("alice"));
        final String credentialId = (String) CONVERTER.getJsonMapper().readValue(response, Map.class).get("id");
        final Passkey other = new Passkey();
        other.setId(credentialId);
        other.setUserName("bob");
        service.store.put(credentialId, other);

        assertRejected("reason=duplicate_credential", () -> service.verifyRegistration(session, user("alice"), "laptop", response));
        assertSame(other, service.store.get(credentialId));
        assertEquals(1, service.store.size());
    }

    @Test
    public void test_register_insertConflict_fails() {
        service.insertConflict = true;
        final ClientPlatform client = newClient(ORIGIN);
        final String response = createCredential(client, user("alice"));

        assertRejected("reason=duplicate_credential", () -> service.verifyRegistration(session, user("alice"), "laptop", response));
    }

    @Test
    public void test_userHandle_sharedAcrossCredentialsOfOneUser() {
        final Passkey first = register(newClient(ORIGIN), user("alice"), "laptop");
        final Passkey second = register(newClient(ORIGIN), user("alice"), "phone");
        final Passkey other = register(newClient(ORIGIN), user("bob"), "laptop");

        assertNotEquals(first.getId(), second.getId());
        assertEquals(first.getUserHandle(), second.getUserHandle());
        assertNotEquals(first.getUserHandle(), other.getUserHandle());
        final String encodedName = Base64.getUrlEncoder().withoutPadding().encodeToString("alice".getBytes(StandardCharsets.UTF_8));
        assertNotEquals(encodedName, first.getUserHandle());
        assertEquals(32, Base64.getUrlDecoder().decode(first.getUserHandle()).length);
    }

    @Test
    public void test_registrationOptions_excludeExistingCredentials() {
        final Passkey first = register(newClient(ORIGIN), user("alice"), "laptop");
        register(newClient(ORIGIN), user("bob"), "laptop");

        final Map<String, Object> options = service.createRegistrationOptions(session, user("alice"));
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> exclude = (List<Map<String, Object>>) options.get("excludeCredentials");
        assertEquals(1, exclude.size());
        assertEquals(first.getId(), exclude.get(0).get("id"));
        assertEquals("public-key", exclude.get(0).get("type"));
        @SuppressWarnings("unchecked")
        final Map<String, Object> userEntity = (Map<String, Object>) options.get("user");
        assertEquals(first.getUserHandle(), userEntity.get("id"));
        assertEquals("alice", userEntity.get("name"));
    }

    @Test
    public void test_login_tamperedSignature_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        final Passkey passkey = register(client, user("alice"), "laptop");
        final String forged = mutateResponse(login(client), "signature", v -> flipLastByte(v));

        assertRejected("error=BadSignatureException", () -> service.verifyLogin(session, forged));
        assertNull(service.store.get(passkey.getId()).getLastUsedAt());
    }

    @Test
    public void test_login_tamperedAuthenticatorData_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        register(client, user("alice"), "laptop");
        // the last byte is the low byte of the sign count, which the signature covers
        final String forged = mutateResponse(login(client), "authenticatorData", v -> flipLastByte(v));

        assertRejected("error=BadSignatureException", () -> service.verifyLogin(session, forged));
    }

    @Test
    public void test_login_staleChallengeWhileAFreshOneExists_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        register(client, user("alice"), "laptop");
        final String stale = login(client);
        // a second login options call replaces the challenge the response was made for
        service.createLoginOptions(session);

        assertRejected("error=BadChallengeException", () -> service.verifyLogin(session, stale));
    }

    @Test
    public void test_login_assertionWithoutUserHandle_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        final Passkey passkey = register(client, user("alice"), "laptop");
        final String response = mutateResponse(login(client), "userHandle", value -> null);

        assertRejected("reason=user_handle_mismatch", () -> service.verifyLogin(session, response));
        assertNull(service.store.get(passkey.getId()).getLastUsedAt());
    }

    @Test
    public void test_login_corruptStoredUserHandle_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        final Passkey passkey = register(client, user("alice"), "laptop");
        passkey.setUserHandle("not*base64url");

        assertRejected("reason=corrupt_passkey", () -> service.verifyLogin(session, login(client)));
    }

    @Test
    public void test_login_userExistenceIsCheckedOnlyAfterTheSignature() {
        // a forged assertion of a deleted user's passkey is refused for its signature, so its
        // refusal does not tell whether the user exists
        final ClientPlatform client = newClient(ORIGIN);
        register(client, user("alice"), "laptop");
        service.users.remove("alice");
        final String forged = mutateResponse(login(client), "signature", v -> flipLastByte(v));

        assertRejected("error=BadSignatureException", () -> service.verifyLogin(session, forged));
    }

    @Test
    public void test_login_credentialIdOutOfBounds_failsBeforeLookup() {
        final ClientPlatform client = newClient(ORIGIN);
        register(client, user("alice"), "laptop");
        final String tooLong = Base64.getUrlEncoder().withoutPadding().encodeToString(new byte[PasskeyService.MAX_CREDENTIAL_ID_BYTES + 1]);
        final String response = login(client);
        @SuppressWarnings("unchecked")
        final Map<String, Object> json = CONVERTER.getJsonMapper().readValue(response, Map.class);
        json.put("id", tooLong);
        json.put("rawId", tooLong);
        final String forged = CONVERTER.getJsonMapper().writeValueAsString(json);
        final int lookups = service.lookups.get();

        assertRejected("error=credential_id_length", () -> service.verifyLogin(session, forged));
        assertEquals(lookups, service.lookups.get());
    }

    @Test
    public void test_delete_overlongCredentialId_returnsFalseWithoutLookup() {
        final int lookups = service.lookups.get();
        assertFalse(service.delete("alice", "x".repeat(PasskeyService.MAX_CREDENTIAL_ID_LENGTH + 1)));
        assertEquals(lookups, service.lookups.get());
    }

    // -----------------------------------------------------------------------
    // Label and limits
    // -----------------------------------------------------------------------

    @Test
    public void test_labelProblem() {
        assertEquals("label_required", PasskeyService.labelProblem(null));
        assertEquals("label_required", PasskeyService.labelProblem(" "));
        assertNull(PasskeyService.labelProblem("x".repeat(64)));
        assertEquals("label_too_long", PasskeyService.labelProblem("x".repeat(65)));
        // 64 characters outside the BMP are 128 UTF-16 units, and still a valid label
        assertNull(PasskeyService.labelProblem("\uD83D\uDD11".repeat(64)));
        assertEquals("label_too_long", PasskeyService.labelProblem("\uD83D\uDD11".repeat(65)));
        assertNull(PasskeyService.labelProblem("Work laptop \u00e9\u6f22"));
        for (final String bad : new String[] { "a\u0000b", "a\tb", "a\nb", "a\u007fb", "a\u0085b", "a\u202Eb", "a\u202Ab", "a\u2066b",
                "a\u2069b", "a\u200Eb", "a\u200Fb", "a\u061Cb" }) {
            org.junit.jupiter.api.Assertions.assertEquals("label_invalid_characters", PasskeyService.labelProblem(bad), bad);
        }
    }

    @Test
    public void test_register_invalidLabel_fails() {
        for (final String label : new String[] { "", "x".repeat(65), "evil\u202Etxt.exe" }) {
            final String response = createCredential(newClient(ORIGIN), user("alice"));
            assertRejected("reason=label_", () -> service.verifyRegistration(session, user("alice"), label, response));
        }
        assertTrue(service.store.isEmpty());
    }

    @Test
    public void test_registrationOptions_refusedAtTheLimit() {
        for (int i = 0; i < PasskeyService.MAX_PASSKEYS_PER_USER; i++) {
            final Passkey passkey = new Passkey();
            passkey.setId("id" + i);
            passkey.setUserName("alice");
            service.store.put(passkey.getId(), passkey);
        }

        assertRejected("reason=too_many_passkeys", () -> service.createRegistrationOptions(session, user("alice")));
        assertNull(session.getAttribute(PasskeyService.REGISTRATION_CHALLENGE_KEY), "no challenge is made");
        // another user is not limited by alice's passkeys
        assertNotNull(service.createRegistrationOptions(session, user("bob")));
    }

    @Test
    public void test_register_refusedWhenTheLimitWasReachedMeanwhile() {
        final String response = createCredential(newClient(ORIGIN), user("alice"));
        for (int i = 0; i < PasskeyService.MAX_PASSKEYS_PER_USER; i++) {
            final Passkey passkey = new Passkey();
            passkey.setId("id" + i);
            passkey.setUserName("alice");
            service.store.put(passkey.getId(), passkey);
        }

        assertRejected("reason=too_many_passkeys", () -> service.verifyRegistration(session, user("alice"), "laptop", response));
        assertEquals(PasskeyService.MAX_PASSKEYS_PER_USER, service.store.size());
    }

    @Test
    public void test_abbreviate() {
        assertEquals("-", PasskeyService.abbreviate(null));
        assertEquals("AbCdEfGh", PasskeyService.abbreviate("AbCdEfGh"));
        assertEquals("AbCdEfGh...", PasskeyService.abbreviate("AbCdEfGhI"));
    }

    private String mutateResponse(final String responseJson, final String field, final java.util.function.UnaryOperator<byte[]> mutation) {
        @SuppressWarnings("unchecked")
        final Map<String, Object> json = CONVERTER.getJsonMapper().readValue(responseJson, Map.class);
        @SuppressWarnings("unchecked")
        final Map<String, Object> response = (Map<String, Object>) json.get("response");
        final Object value = response.get(field);
        assertNotNull(value, field + " in " + responseJson);
        final byte[] mutated = mutation.apply(Base64.getUrlDecoder().decode((String) value));
        response.put(field, mutated == null ? null : Base64.getUrlEncoder().withoutPadding().encodeToString(mutated));
        return CONVERTER.getJsonMapper().writeValueAsString(json);
    }

    private static byte[] flipLastByte(final byte[] bytes) {
        final byte[] copy = bytes.clone();
        copy[copy.length - 1] ^= 0x01;
        return copy;
    }

    // -----------------------------------------------------------------------
    // Sign counter
    // -----------------------------------------------------------------------

    @Test
    public void test_login_counterZeroBoth_succeeds() {
        final NoneAttestationAuthenticator authenticator = new NoneAttestationAuthenticator();
        authenticator.setCountUpEnabled(false);
        final ClientPlatform client = new ClientPlatform(new Origin(ORIGIN), new WebAuthnAuthenticatorAdaptor(authenticator));
        final Passkey passkey = register(client, user("alice"), "laptop");
        assertEquals(Long.valueOf(0L), passkey.getSignCount());

        assertEquals("alice", service.verifyLogin(session, login(client)));
        assertEquals("alice", service.verifyLogin(session, login(client)));
        assertEquals(Long.valueOf(0L), service.store.get(passkey.getId()).getSignCount());
    }

    @Test
    public void test_login_counterZeroAfterNonZero_succeeds() {
        final NoneAttestationAuthenticator authenticator = new NoneAttestationAuthenticator();
        authenticator.setCountUpEnabled(false);
        final ClientPlatform client = new ClientPlatform(new Origin(ORIGIN), new WebAuthnAuthenticatorAdaptor(authenticator));
        final Passkey passkey = register(client, user("alice"), "laptop");
        passkey.setSignCount(5L);

        assertEquals("alice", service.verifyLogin(session, login(client)));
        assertEquals(Long.valueOf(5L), service.store.get(passkey.getId()).getSignCount());
    }

    @Test
    public void test_login_counterRegresses_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        final Passkey passkey = register(client, user("alice"), "laptop");
        passkey.setSignCount(1000L);
        final String response = login(client);

        assertRejected("reason=sign_count_not_increased", () -> service.verifyLogin(session, response));
        assertEquals(Long.valueOf(1000L), service.store.get(passkey.getId()).getSignCount());
    }

    @Test
    public void test_login_counterEqual_fails() {
        final ClientPlatform client = newClient(ORIGIN);
        final Passkey passkey = register(client, user("alice"), "laptop");
        // the emulator presents the stored count plus one on the next assertion
        passkey.setSignCount(passkey.getSignCount() + 1L);

        assertRejected("reason=sign_count_not_increased", () -> service.verifyLogin(session, login(client)));
    }

    @Test
    public void test_login_counterIncreases_storesPresentedCount() {
        final ClientPlatform client = newClient(ORIGIN);
        final Passkey passkey = register(client, user("alice"), "laptop");
        final long registered = passkey.getSignCount();

        service.verifyLogin(session, login(client));
        assertEquals(Long.valueOf(registered + 1L), service.store.get(passkey.getId()).getSignCount());
    }

    @Test
    public void test_login_versionConflict_retriesOnceThenFails() {
        final ClientPlatform client = newClient(ORIGIN);
        final Passkey passkey = register(client, user("alice"), "laptop");
        service.conflictsLeft = 2;
        final String response = login(client);

        assertRejected("reason=version_conflict", () -> service.verifyLogin(session, response));
        assertEquals(2, service.updateCalls.get());
        assertNull(service.store.get(passkey.getId()).getLastUsedAt());
    }

    @Test
    public void test_login_versionConflictOnce_retriesAndSucceeds() {
        final ClientPlatform client = newClient(ORIGIN);
        final Passkey passkey = register(client, user("alice"), "laptop");
        service.conflictsLeft = 1;

        assertEquals("alice", service.verifyLogin(session, login(client)));
        assertEquals(2, service.updateCalls.get());
        assertEquals(Long.valueOf(1000L), service.store.get(passkey.getId()).getLastUsedAt());
    }

    @Test
    public void test_login_versionConflict_retryRechecksCounter() {
        final ClientPlatform client = newClient(ORIGIN);
        final Passkey passkey = register(client, user("alice"), "laptop");
        service.conflictsLeft = 1;
        // a concurrent login stored a higher count meanwhile
        service.onConflict = () -> service.store.get(passkey.getId()).setSignCount(1000L);

        assertRejected("reason=sign_count_not_increased", () -> service.verifyLogin(session, login(client)));
        assertEquals(1, service.updateCalls.get());
    }

    // -----------------------------------------------------------------------
    // Listing and deletion
    // -----------------------------------------------------------------------

    @Test
    public void test_delete_otherUsersCredential_returnsFalse() {
        final Passkey alices = register(newClient(ORIGIN), user("alice"), "laptop");

        assertFalse(service.delete("bob", alices.getId()));
        assertNotNull(service.store.get(alices.getId()));
        assertFalse(service.delete("alice", "unknown"));
        assertTrue(service.delete("alice", alices.getId()));
        assertNull(service.store.get(alices.getId()));
    }

    @Test
    public void test_getPasskeys_andDeleteByUserName() {
        register(newClient(ORIGIN), user("alice"), "laptop");
        register(newClient(ORIGIN), user("alice"), "phone");
        final Passkey bobs = register(newClient(ORIGIN), user("bob"), "laptop");

        assertEquals(2, service.getPasskeys("alice").size());
        service.deleteByUserName("alice");
        assertEquals(0, service.getPasskeys("alice").size());
        assertEquals(1, service.store.size());
        assertNotNull(service.store.get(bobs.getId()));
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void assertRejected(final String reason, final Executable executable) {
        final PasskeyException e = assertThrows(PasskeyException.class, executable);
        assertTrue(e.getMessage().contains(reason), e.getMessage());
    }

    private static ClientPlatform newClient(final String origin) {
        return new ClientPlatform(new Origin(origin), new WebAuthnAuthenticatorAdaptor(new NoneAttestationAuthenticator()));
    }

    private static User user(final String name) {
        final User user = new User();
        user.setName(name);
        return user;
    }

    private String createCredential(final ClientPlatform client, final User user) {
        final Map<String, Object> options = service.createRegistrationOptions(session, user);
        return credentialJson(client.create(toCreationOptions(options)));
    }

    private Passkey register(final ClientPlatform client, final User user, final String label) {
        return service.verifyRegistration(session, user, label, createCredential(client, user));
    }

    private String login(final ClientPlatform client) {
        return credentialJson(client.get(toRequestOptions(service.createLoginOptions(session))));
    }

    private static PublicKeyCredentialCreationOptions toCreationOptions(final Map<String, Object> options) {
        return CONVERTER.getJsonMapper()
                .readValue(CONVERTER.getJsonMapper().writeValueAsString(options), PublicKeyCredentialCreationOptions.class);
    }

    private static PublicKeyCredentialRequestOptions toRequestOptions(final Map<String, Object> options) {
        return CONVERTER.getJsonMapper()
                .readValue(CONVERTER.getJsonMapper().writeValueAsString(options), PublicKeyCredentialRequestOptions.class);
    }

    private static String credentialJson(final Object credential) {
        return CONVERTER.getJsonMapper().writeValueAsString(credential);
    }

    /**
     * Keeps the credentials in a map, so that the ceremonies run without a search engine.
     */
    private static class InMemoryPasskeyService extends PasskeyService {
        final Map<String, Passkey> store = new LinkedHashMap<>();

        final Set<String> users = new HashSet<>();

        final AtomicInteger updateCalls = new AtomicInteger();

        final AtomicInteger lookups = new AtomicInteger();

        String rpId;

        String origins = ORIGIN;

        long now = 1000L;

        int conflictsLeft;

        boolean insertConflict;

        Runnable onConflict;

        InMemoryPasskeyService() {
            fessConfig = new FessConfig.SimpleImpl() {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean isPasskeyEnabled() {
                    return true;
                }

                @Override
                public String getPasskeyRpId() {
                    return rpId;
                }

                @Override
                public String getPasskeyRpName() {
                    return "Fess";
                }

                @Override
                public String getPasskeyOrigins() {
                    return origins;
                }
            };
        }

        @Override
        protected long currentTimeMillis() {
            return now;
        }

        @Override
        protected boolean existsUser(final String userName) {
            return users.contains(userName);
        }

        @Override
        protected OptionalEntity<Passkey> findByCredentialId(final String credentialId) {
            lookups.incrementAndGet();
            return OptionalEntity.ofNullable(store.get(credentialId), () -> {});
        }

        @Override
        protected List<Passkey> findByUserName(final String userName) {
            final List<Passkey> list = new ArrayList<>();
            for (final Passkey passkey : store.values()) {
                if (userName.equals(passkey.getUserName())) {
                    list.add(passkey);
                }
            }
            return list;
        }

        @Override
        protected void insert(final Passkey passkey) {
            if (insertConflict || store.containsKey(passkey.getId())) {
                throw new PasskeyException("reason=duplicate_credential");
            }
            store.put(passkey.getId(), passkey);
        }

        @Override
        protected boolean updateAfterLogin(final Passkey passkey, final long signCount, final long usedAt) {
            updateCalls.incrementAndGet();
            if (conflictsLeft > 0) {
                conflictsLeft--;
                if (onConflict != null) {
                    onConflict.run();
                }
                return false;
            }
            final Passkey stored = store.get(passkey.getId());
            stored.setSignCount(signCount);
            stored.setLastUsedAt(usedAt);
            return true;
        }

        @Override
        protected void deleteBy(final String userName, final String credentialIdOrNull) {
            store.values()
                    .removeIf(
                            p -> userName.equals(p.getUserName()) && (credentialIdOrNull == null || credentialIdOrNull.equals(p.getId())));
        }
    }

    /**
     * Map-backed stub of {@link HttpSession} for the attribute methods the service uses.
     */
    private static class StubHttpSession implements HttpSession {
        private final Map<String, Object> attributes = new HashMap<>();

        @Override
        public Object getAttribute(final String name) {
            return attributes.get(name);
        }

        @Override
        public void setAttribute(final String name, final Object value) {
            if (value == null) {
                attributes.remove(name);
            } else {
                attributes.put(name, value);
            }
        }

        @Override
        public void removeAttribute(final String name) {
            attributes.remove(name);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(attributes.keySet());
        }

        @Override
        public long getCreationTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLastAccessedTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ServletContext getServletContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setMaxInactiveInterval(final int interval) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getMaxInactiveInterval() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void invalidate() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isNew() {
            throw new UnsupportedOperationException();
        }
    }
}
