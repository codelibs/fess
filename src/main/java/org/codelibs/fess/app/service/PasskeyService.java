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

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.exbhv.PasskeyBhv;
import org.codelibs.fess.opensearch.user.exbhv.UserBhv;
import org.codelibs.fess.opensearch.user.exentity.Passkey;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.util.LogUtil;
import org.dbflute.optional.OptionalEntity;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.credential.CredentialRecordImpl;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.AuthenticatorTransport;
import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialType;
import com.webauthn4j.data.RegistrationData;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.attestation.authenticator.AAGUID;
import com.webauthn4j.data.attestation.authenticator.AttestedCredentialData;
import com.webauthn4j.data.attestation.authenticator.AuthenticatorData;
import com.webauthn4j.data.attestation.authenticator.COSEKey;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.data.attestation.statement.NoneAttestationStatement;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.data.extension.authenticator.RegistrationExtensionAuthenticatorOutput;
import com.webauthn4j.server.ServerProperty;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

/**
 * Service for passkey (WebAuthn) login of local users. It issues the options of the registration
 * and login ceremonies, verifies their responses with webauthn4j and keeps one document per
 * credential in the {@code fess_user.passkey} index.
 *
 * <p>Every verification failure throws a {@link PasskeyException} whose message is a
 * {@code key=value} reason for the log; callers must answer the client the same way whatever the
 * reason is.</p>
 */
public class PasskeyService {

    private static final Logger logger = LogManager.getLogger(PasskeyService.class);

    /** The session attribute holding the challenge of a pending registration. */
    protected static final String REGISTRATION_CHALLENGE_KEY = PasskeyService.class.getName() + ".registrationChallenge";

    /** The session attribute holding the challenge of a pending login. */
    protected static final String LOGIN_CHALLENGE_KEY = PasskeyService.class.getName() + ".loginChallenge";

    /** The entry of a stored challenge holding its random bytes. */
    protected static final String CHALLENGE_VALUE = "challenge";

    /** The entry of a stored challenge holding when it was made, in epoch milliseconds. */
    protected static final String CHALLENGE_CREATED_AT = "createdAt";

    /** The entry of a stored registration challenge holding the name of the user it is for. */
    protected static final String CHALLENGE_USER_NAME = "userName";

    /** The entry of a stored registration challenge holding the user handle it uses. */
    protected static final String CHALLENGE_USER_HANDLE = "userHandle";

    /**
     * Makes reading and removing a challenge one step, so that two requests of one session cannot
     * both use it. The step is short and does no I/O, so one lock serves all sessions.
     */
    private static final Object CHALLENGE_LOCK = new Object();

    /** How long a challenge is valid, in milliseconds. */
    protected static final long CHALLENGE_TTL = 5 * 60 * 1000L;

    /** The number of random bytes of a challenge and of a user handle. */
    protected static final int RANDOM_BYTES = 32;

    /** The number of credential ID characters written to the log. */
    protected static final int LOGGED_ID_LENGTH = 8;

    /** The credential type of a passkey. */
    protected static final String PUBLIC_KEY_TYPE = "public-key";

    /** The COSE identifiers of the signature algorithms offered at registration: ES256 and RS256. */
    protected static final List<Long> PUB_KEY_CRED_ALGS = List.of(-7L, -257L);

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * The Fess configuration.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * The behavior of the passkey index.
     */
    @Resource
    protected PasskeyBhv passkeyBhv;

    /**
     * The behavior of the user index.
     */
    @Resource
    protected UserBhv userBhv;

    /**
     * Constructor.
     */
    public PasskeyService() {
        // Default constructor
    }

    /**
     * Returns whether the passkey feature is available. It is when passkey.enabled is set and both
     * passkey.rp.id and passkey.origins are not blank.
     *
     * @return true if passkeys can be registered and used to log in
     */
    public boolean isEnabled() {
        if (!fessConfig.isPasskeyEnabled()) {
            return false;
        }
        if (StringUtil.isBlank(fessConfig.getPasskeyRpId()) || StringUtil.isBlank(fessConfig.getPasskeyOrigins())) {
            LogUtil.warnOnce(logger, "misconfigured",
                    "passkey.enabled is true but passkey.rp.id or passkey.origins is empty, so passkeys are disabled.");
            return false;
        }
        return true;
    }

    // ===================================================================================
    //                                                                        Registration
    //                                                                        ============
    /**
     * Creates the options for {@code navigator.credentials.create} and keeps their challenge in the
     * session. The user handle is the one of the user's existing passkeys, or a new random one.
     *
     * @param session the HTTP session of the logged-in user
     * @param user the local user registering a passkey
     * @return the options, as JSON-ready values (binary values are Base64url)
     */
    public Map<String, Object> createRegistrationOptions(final HttpSession session, final User user) {
        final String userName = user.getName();
        final List<Passkey> passkeys = findByUserName(userName);
        final String userHandle = passkeys.stream()
                .map(Passkey::getUserHandle)
                .filter(StringUtil::isNotBlank)
                .findFirst()
                .orElseGet(() -> encode(randomBytes()));
        final byte[] challenge = randomBytes();
        session.setAttribute(REGISTRATION_CHALLENGE_KEY, toSessionValue(challenge, currentTimeMillis(), userName, userHandle));

        final Map<String, Object> rp = new LinkedHashMap<>();
        rp.put("id", fessConfig.getPasskeyRpId());
        rp.put("name", fessConfig.getPasskeyRpName());
        final Map<String, Object> userEntity = new LinkedHashMap<>();
        userEntity.put("id", userHandle);
        userEntity.put("name", userName);
        userEntity.put("displayName", userName);
        final List<Map<String, Object>> pubKeyCredParams = new ArrayList<>();
        for (final Long alg : PUB_KEY_CRED_ALGS) {
            final Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", PUBLIC_KEY_TYPE);
            map.put("alg", alg);
            pubKeyCredParams.add(map);
        }
        final List<Map<String, Object>> excludeCredentials = new ArrayList<>();
        for (final Passkey passkey : passkeys) {
            final Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", PUBLIC_KEY_TYPE);
            map.put("id", passkey.getId());
            if (passkey.getTransports() != null && passkey.getTransports().length > 0) {
                map.put("transports", List.of(passkey.getTransports()));
            }
            excludeCredentials.add(map);
        }
        final Map<String, Object> authenticatorSelection = new LinkedHashMap<>();
        authenticatorSelection.put("residentKey", "required");
        authenticatorSelection.put("requireResidentKey", Boolean.TRUE);
        authenticatorSelection.put("userVerification", "required");

        final Map<String, Object> options = new LinkedHashMap<>();
        options.put("rp", rp);
        options.put("user", userEntity);
        options.put("challenge", encode(challenge));
        options.put("pubKeyCredParams", pubKeyCredParams);
        options.put("timeout", CHALLENGE_TTL);
        options.put("excludeCredentials", excludeCredentials);
        options.put("authenticatorSelection", authenticatorSelection);
        options.put("attestation", "none");
        return options;
    }

    /**
     * Verifies the response of {@code navigator.credentials.create} against the challenge in the
     * session, and stores the new passkey. The challenge is removed from the session whatever the
     * outcome.
     *
     * @param session the HTTP session that received the registration options
     * @param user the local user registering the passkey; it must be the one the options were made for
     * @param label the name the user gives to the passkey
     * @param responseJson the registration response (PublicKeyCredential JSON)
     * @return the stored passkey
     * @throws PasskeyException when the response does not verify or the credential is already registered
     */
    public Passkey verifyRegistration(final HttpSession session, final User user, final String label, final String responseJson) {
        final PendingChallenge challenge = consumeChallenge(session, REGISTRATION_CHALLENGE_KEY);
        if (user == null || challenge.userName() == null || !challenge.userName().equals(user.getName())) {
            throw new PasskeyException("reason=registration_user_mismatch");
        }

        final RegisteredCredential registered = WebAuthn.verifyRegistration(responseJson, createServerSide(challenge));
        final String credentialId = encode(registered.credentialId());
        if (findByCredentialId(credentialId).isPresent()) {
            throw new PasskeyException("reason=duplicate_credential credentialId=" + abbreviate(credentialId));
        }

        final Passkey passkey = new Passkey();
        passkey.setId(credentialId);
        passkey.setUserName(challenge.userName());
        passkey.setUserHandle(challenge.userHandle());
        passkey.setPublicKey(encode(registered.coseKey()));
        passkey.setSignCount(registered.signCount());
        passkey.setTransports(registered.transports());
        passkey.setBackupEligible(registered.backupEligible());
        passkey.setBackupState(registered.backupState());
        passkey.setLabel(StringUtil.isBlank(label) ? null : label.trim());
        passkey.setCreatedAt(currentTimeMillis());
        insert(passkey);
        if (logger.isDebugEnabled()) {
            logger.debug("Registered a passkey: credentialId={}", abbreviate(credentialId));
        }
        return passkey;
    }

    // ===================================================================================
    //                                                                               Login
    //                                                                               =====
    /**
     * Creates the options for {@code navigator.credentials.get} and keeps their challenge in the
     * session. No credential is listed: the authenticator offers its discoverable credentials, so the
     * options are the same whoever asks.
     *
     * @param session the HTTP session of the login
     * @return the options, as JSON-ready values (binary values are Base64url)
     */
    public Map<String, Object> createLoginOptions(final HttpSession session) {
        final byte[] challenge = randomBytes();
        session.setAttribute(LOGIN_CHALLENGE_KEY, toSessionValue(challenge, currentTimeMillis(), null, null));
        final Map<String, Object> options = new LinkedHashMap<>();
        options.put("challenge", encode(challenge));
        options.put("rpId", fessConfig.getPasskeyRpId());
        options.put("timeout", CHALLENGE_TTL);
        options.put("userVerification", "required");
        options.put("allowCredentials", Collections.emptyList());
        return options;
    }

    /**
     * Verifies the response of {@code navigator.credentials.get} against the challenge in the
     * session and the stored passkey, then records the new sign count and the time of use. The
     * challenge is removed from the session whatever the outcome.
     *
     * @param session the HTTP session that received the login options
     * @param responseJson the authentication response (PublicKeyCredential JSON)
     * @return the name of the local user the passkey belongs to
     * @throws PasskeyException when the response does not verify
     */
    public String verifyLogin(final HttpSession session, final String responseJson) {
        final PendingChallenge challenge = consumeChallenge(session, LOGIN_CHALLENGE_KEY);

        final Assertion assertion = WebAuthn.parseAuthentication(responseJson);
        if (assertion.credentialId() == null) {
            throw new PasskeyException("reason=login_rejected error=no_credential_id");
        }
        final String credentialId = encode(assertion.credentialId());
        final Passkey passkey = findByCredentialId(credentialId)
                .orElseThrow(() -> new PasskeyException("reason=unknown_credential credentialId=" + abbreviate(credentialId)));

        final byte[] userHandle = assertion.userHandle();
        if (userHandle == null || StringUtil.isBlank(passkey.getUserHandle())
                || !MessageDigest.isEqual(userHandle, decode(passkey.getUserHandle()))) {
            throw new PasskeyException("reason=user_handle_mismatch credentialId=" + abbreviate(credentialId));
        }
        if (!existsUser(passkey.getUserName())) {
            throw new PasskeyException("reason=unknown_user credentialId=" + abbreviate(credentialId));
        }

        final long presentedSignCount;
        try {
            presentedSignCount = WebAuthn.verifyAuthentication(assertion, createServerSide(challenge), passkey);
        } catch (final RuntimeException e) {
            throw new PasskeyException(
                    "reason=login_rejected error=" + e.getClass().getSimpleName() + " credentialId=" + abbreviate(credentialId));
        }

        final long usedAt = currentTimeMillis();
        checkSignCount(passkey, presentedSignCount, credentialId);
        if (!updateAfterLogin(passkey, Math.max(signCountOf(passkey), presentedSignCount), usedAt)) {
            // Another login of the same credential won the race: check against what it stored, once.
            final Passkey current = findByCredentialId(credentialId)
                    .orElseThrow(() -> new PasskeyException("reason=unknown_credential credentialId=" + abbreviate(credentialId)));
            checkSignCount(current, presentedSignCount, credentialId);
            if (!updateAfterLogin(current, Math.max(signCountOf(current), presentedSignCount), usedAt)) {
                throw new PasskeyException("reason=version_conflict credentialId=" + abbreviate(credentialId));
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Verified a passkey login: credentialId={}", abbreviate(credentialId));
        }
        return passkey.getUserName();
    }

    // ===================================================================================
    //                                                                          Management
    //                                                                          ==========
    /**
     * Returns the passkeys of a user, oldest first.
     *
     * @param userName the name of the local user
     * @return the passkeys
     */
    public List<Passkey> getPasskeys(final String userName) {
        return findByUserName(userName);
    }

    /**
     * Deletes a passkey of a user.
     *
     * @param userName the name of the user the passkey must belong to
     * @param credentialId the credential ID of the passkey
     * @return true if it was deleted; false if there is no such passkey of that user
     */
    public boolean delete(final String userName, final String credentialId) {
        if (StringUtil.isBlank(userName) || StringUtil.isBlank(credentialId)) {
            return false;
        }
        final boolean owned = findByCredentialId(credentialId).filter(p -> userName.equals(p.getUserName())).isPresent();
        if (!owned) {
            return false;
        }
        deleteBy(userName, credentialId);
        return true;
    }

    /**
     * Deletes all passkeys of a user.
     *
     * @param userName the name of the user
     */
    public void deleteByUserName(final String userName) {
        if (StringUtil.isBlank(userName)) {
            return;
        }
        deleteBy(userName, null);
    }

    // ===================================================================================
    //                                                                         Persistence
    //                                                                         ===========
    /**
     * Finds a passkey by its credential ID, with the sequence number and primary term it was read with.
     *
     * @param credentialId the credential ID (Base64url)
     * @return the passkey
     */
    protected OptionalEntity<Passkey> findByCredentialId(final String credentialId) {
        return passkeyBhv.selectByPK(credentialId);
    }

    /**
     * Finds the passkeys of a user, oldest first.
     *
     * @param userName the name of the user
     * @return the passkeys
     */
    protected List<Passkey> findByUserName(final String userName) {
        return passkeyBhv.selectList(cb -> {
            cb.query().setUserName_Term(userName);
            cb.query().addOrderBy_CreatedAt_Asc();
            cb.fetchFirst(fessConfig.getPageUserMaxFetchSizeAsInteger());
        });
    }

    /**
     * Stores a new passkey; it fails when a passkey with the same credential ID exists.
     *
     * @param passkey the passkey, with its credential ID as the ID
     * @throws PasskeyException when a passkey with the same credential ID exists
     */
    protected void insert(final Passkey passkey) {
        try {
            passkeyBhv.insert(passkey, op -> {
                op.setCreate(true);
                op.setRefreshPolicy(Constants.TRUE);
            });
        } catch (final RuntimeException e) {
            if (isVersionConflict(e)) {
                throw new PasskeyException("reason=duplicate_credential credentialId=" + abbreviate(passkey.getId()));
            }
            throw e;
        }
    }

    /**
     * Records the sign count and the time of a login, if the passkey did not change since it was read.
     *
     * @param passkey the passkey, carrying the sequence number and primary term it was read with
     * @param signCount the sign count to store
     * @param usedAt the time of the login
     * @return true if it was updated; false if the passkey changed since it was read
     */
    protected boolean updateAfterLogin(final Passkey passkey, final long signCount, final long usedAt) {
        final Long seqNo = passkey.getSeqNo();
        final Long primaryTerm = passkey.getPrimaryTerm();
        if (seqNo == null || seqNo.longValue() < 0L || primaryTerm == null || primaryTerm.longValue() <= 0L) {
            throw new PasskeyException("reason=no_version credentialId=" + abbreviate(passkey.getId()));
        }
        passkey.setSignCount(signCount);
        passkey.setLastUsedAt(usedAt);
        try {
            passkeyBhv.update(passkey, op -> {
                op.setIfSeqNo(seqNo);
                op.setIfPrimaryTerm(primaryTerm);
                op.setRefreshPolicy(Constants.TRUE);
            });
            return true;
        } catch (final RuntimeException e) {
            if (isVersionConflict(e)) {
                return false;
            }
            throw e;
        }
    }

    /**
     * Deletes the passkeys of a user.
     *
     * @param userName the name of the user
     * @param credentialIdOrNull the credential ID of the one passkey to delete, or null for all of them
     */
    protected void deleteBy(final String userName, final String credentialIdOrNull) {
        passkeyBhv.queryDelete(cb -> {
            cb.query().setUserName_Term(userName);
            if (credentialIdOrNull != null) {
                cb.query().setId_Equal(credentialIdOrNull);
            }
        });
        passkeyBhv.refresh();
    }

    /**
     * Returns whether a local user exists.
     *
     * @param userName the name of the user
     * @return true if the user is in the user index
     */
    protected boolean existsUser(final String userName) {
        return StringUtil.isNotBlank(userName) && userBhv.selectCount(cb -> cb.query().setName_Equal(userName)) > 0;
    }

    /**
     * Returns the current time in milliseconds.
     *
     * @return the current time
     */
    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    /**
     * Builds the session value of a challenge. It is a map of strings, a {@code Long} and a byte
     * array only, all of which {@code SessionDataSerializer} registers, so that the OpenSearch
     * session store shares it between nodes: an attribute of a class it does not register stays on
     * the node that set it.
     *
     * @param value the random bytes of the challenge
     * @param createdAt when the challenge was made
     * @param userName the user a registration is for, or null for a login
     * @param userHandle the user handle a registration uses, or null for a login
     * @return the value to store in the session
     */
    protected static Map<String, Object> toSessionValue(final byte[] value, final long createdAt, final String userName,
            final String userHandle) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put(CHALLENGE_VALUE, value);
        map.put(CHALLENGE_CREATED_AT, createdAt);
        if (userName != null) {
            map.put(CHALLENGE_USER_NAME, userName);
        }
        if (userHandle != null) {
            map.put(CHALLENGE_USER_HANDLE, userHandle);
        }
        return map;
    }

    /**
     * Removes the pending challenge from the session and checks it is not expired.
     *
     * <p>Reading and removing it is one step for the sessions of this node, so only one of two
     * concurrent verifications of a session gets the challenge. With a session store shared by
     * several nodes, two requests of one session that are served by two nodes at the same time each
     * read their own copy, so the store does not make the challenge single-use across nodes; the
     * signature check still binds a response to the challenge it was made for.</p>
     *
     * @param session the HTTP session
     * @param key the session attribute
     * @return the challenge
     * @throws PasskeyException when there is no challenge or it is expired
     */
    protected PendingChallenge consumeChallenge(final HttpSession session, final String key) {
        final Object value;
        synchronized (CHALLENGE_LOCK) {
            value = session.getAttribute(key);
            if (value != null) {
                session.removeAttribute(key);
            }
        }
        final PendingChallenge challenge = PendingChallenge.of(value);
        if (challenge == null) {
            throw new PasskeyException("reason=no_challenge");
        }
        final long age = currentTimeMillis() - challenge.createdAt();
        if (age < 0L || age > CHALLENGE_TTL) {
            throw new PasskeyException("reason=challenge_expired");
        }
        return challenge;
    }

    /**
     * Rejects a sign count that does not increase, unless one of the counts is zero (an
     * authenticator without a counter, as synced passkeys are, always presents zero).
     */
    protected void checkSignCount(final Passkey passkey, final long presentedSignCount, final String credentialId) {
        final long storedSignCount = signCountOf(passkey);
        if (storedSignCount != 0L && presentedSignCount != 0L && presentedSignCount <= storedSignCount) {
            throw new PasskeyException("reason=sign_count_not_increased credentialId=" + abbreviate(credentialId) + " stored="
                    + storedSignCount + " presented=" + presentedSignCount);
        }
    }

    private static long signCountOf(final Passkey passkey) {
        return passkey.getSignCount() == null ? 0L : passkey.getSignCount();
    }

    /**
     * Builds the server side of a ceremony from the configured relying party ID and origins.
     *
     * @param challenge the challenge of the ceremony
     * @return the relying party ID, the allowed origins and the challenge
     */
    protected ServerSide createServerSide(final PendingChallenge challenge) {
        final Set<String> origins = new LinkedHashSet<>();
        for (final String origin : StringUtil.split(fessConfig.getPasskeyOrigins(), ",")) {
            if (StringUtil.isNotBlank(origin)) {
                origins.add(origin.trim());
            }
        }
        return new ServerSide(fessConfig.getPasskeyRpId(), origins, challenge.value());
    }

    /**
     * Shortens a credential ID for the log.
     */
    protected static String abbreviate(final String credentialId) {
        if (credentialId == null) {
            return "null";
        }
        return credentialId.length() <= LOGGED_ID_LENGTH ? credentialId : credentialId.substring(0, LOGGED_ID_LENGTH);
    }

    private static byte[] randomBytes() {
        final byte[] bytes = new byte[RANDOM_BYTES];
        RANDOM.nextBytes(bytes);
        return bytes;
    }

    private static String encode(final byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] decode(final String value) {
        return Base64.getUrlDecoder().decode(value);
    }

    /**
     * Whether a write failed because the document changed, or already exists.
     */
    protected boolean isVersionConflict(final Throwable t) {
        Throwable cur = t;
        while (cur != null) {
            if (cur.getClass().getName().endsWith("VersionConflictEngineException")) {
                return true;
            }
            final String msg = cur.getMessage();
            if (msg != null && msg.contains("version_conflict_engine_exception")) {
                return true;
            }
            final Throwable next = cur.getCause();
            if (next == cur) {
                break;
            }
            cur = next;
        }
        return false;
    }

    /**
     * What the server checks a ceremony against.
     *
     * @param rpId the relying party ID
     * @param origins the origins a response may come from
     * @param challenge the random bytes of the challenge
     */
    protected record ServerSide(String rpId, Set<String> origins, byte[] challenge) {
    }

    /**
     * A credential whose registration response verified.
     *
     * @param credentialId the raw credential ID
     * @param coseKey the public key, COSE-encoded CBOR
     * @param signCount the sign count at registration
     * @param transports the transports the authenticator reported
     * @param backupEligible the BE flag
     * @param backupState the BS flag
     */
    protected record RegisteredCredential(byte[] credentialId, byte[] coseKey, long signCount, String[] transports, boolean backupEligible,
            boolean backupState) {
    }

    /**
     * A parsed authentication response, not verified yet.
     *
     * @param credentialId the raw credential ID, or null
     * @param userHandle the user handle, or null
     * @param data the parsed response for {@link WebAuthn#verifyAuthentication}
     */
    protected record Assertion(byte[] credentialId, byte[] userHandle, Object data) {
    }

    /**
     * Everything that uses webauthn4j. Its classes are loaded and its mappers built only when a
     * ceremony is verified, the first time this holder is used, never when the service is created
     * or {@link #isEnabled()} is asked: the service is injected into the login page, the user
     * deletion and the admin user page, and a broken webauthn4j or Jackson must not break them.
     */
    static final class WebAuthn {

        /**
         * Converts the CBOR and JSON data of the ceremonies. It is shared since services are
         * prototypes and building the mappers is not cheap; it is thread-safe once built.
         */
        static final ObjectConverter OBJECT_CONVERTER = new ObjectConverter();

        /**
         * Verifies the ceremonies; attestation is not checked (attestation {@code none}). The counter
         * rule of webauthn4j rejects a count that stays at zero against a non-zero stored one; the
         * rule of {@link PasskeyService#checkSignCount} is applied instead.
         */
        static final WebAuthnManager MANAGER = createManager();

        /** The signature algorithms offered at registration. */
        static final List<PublicKeyCredentialParameters> PUB_KEY_CRED_PARAMS = PUB_KEY_CRED_ALGS.stream()
                .map(alg -> new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.create(alg)))
                .toList();

        private WebAuthn() {
        }

        private static WebAuthnManager createManager() {
            final WebAuthnManager manager = WebAuthnManager.createNonStrictWebAuthnManager(OBJECT_CONVERTER);
            manager.getAuthenticationDataVerifier().setMaliciousCounterValueHandler(authenticationObject -> {});
            return manager;
        }

        static RegisteredCredential verifyRegistration(final String responseJson, final ServerSide serverSide) {
            final RegistrationData registrationData;
            try {
                registrationData = MANAGER.parseRegistrationResponseJSON(responseJson);
                MANAGER.verify(registrationData, new RegistrationParameters(toServerProperty(serverSide), PUB_KEY_CRED_PARAMS, true, true));
            } catch (final RuntimeException e) {
                throw new PasskeyException("reason=registration_rejected error=" + e.getClass().getSimpleName());
            }
            final AuthenticatorData<RegistrationExtensionAuthenticatorOutput> authenticatorData =
                    registrationData.getAttestationObject().getAuthenticatorData();
            final AttestedCredentialData attestedCredentialData = authenticatorData.getAttestedCredentialData();
            if (attestedCredentialData == null) {
                throw new PasskeyException("reason=registration_rejected error=no_attested_credential_data");
            }
            final Set<AuthenticatorTransport> transports = registrationData.getTransports();
            return new RegisteredCredential(attestedCredentialData.getCredentialId(),
                    OBJECT_CONVERTER.getCborConverter().writeValueAsBytes(attestedCredentialData.getCOSEKey()),
                    authenticatorData.getSignCount(),
                    transports == null ? new String[0] : transports.stream().map(AuthenticatorTransport::getValue).toArray(String[]::new),
                    authenticatorData.isFlagBE(), authenticatorData.isFlagBS());
        }

        static Assertion parseAuthentication(final String responseJson) {
            final AuthenticationData data;
            try {
                data = MANAGER.parseAuthenticationResponseJSON(responseJson);
            } catch (final RuntimeException e) {
                throw new PasskeyException("reason=login_rejected error=" + e.getClass().getSimpleName());
            }
            return new Assertion(data.getCredentialId(), data.getUserHandle(), data);
        }

        /**
         * Verifies an assertion against the stored passkey.
         *
         * @return the sign count the authenticator presented
         * @throws RuntimeException when the assertion does not verify
         */
        static long verifyAuthentication(final Assertion assertion, final ServerSide serverSide, final Passkey passkey) {
            final AuthenticationData data = (AuthenticationData) assertion.data();
            MANAGER.verify(data, new AuthenticationParameters(toServerProperty(serverSide), toCredentialRecord(passkey), null, true, true));
            return data.getAuthenticatorData().getSignCount();
        }

        private static ServerProperty toServerProperty(final ServerSide serverSide) {
            final Set<Origin> origins = new LinkedHashSet<>();
            for (final String origin : serverSide.origins()) {
                origins.add(new Origin(origin));
            }
            return ServerProperty.builder()
                    .origins(origins)
                    .rpId(serverSide.rpId())
                    .challenge(new DefaultChallenge(serverSide.challenge()))
                    .build();
        }

        /**
         * Rebuilds the webauthn4j credential record of a stored passkey.
         */
        private static CredentialRecord toCredentialRecord(final Passkey passkey) {
            final COSEKey coseKey = OBJECT_CONVERTER.getCborConverter().readValue(decode(passkey.getPublicKey()), COSEKey.class);
            final AttestedCredentialData attestedCredentialData = new AttestedCredentialData(AAGUID.ZERO, decode(passkey.getId()), coseKey);
            final Set<AuthenticatorTransport> transports = new HashSet<>();
            if (passkey.getTransports() != null) {
                for (final String transport : passkey.getTransports()) {
                    transports.add(AuthenticatorTransport.create(transport));
                }
            }
            return new CredentialRecordImpl(new NoneAttestationStatement(), Boolean.TRUE, passkey.getBackupEligible(),
                    passkey.getBackupState(), signCountOf(passkey), attestedCredentialData, null, null, null, transports);
        }
    }

    /**
     * A challenge read back from the session for the response of its ceremony. It is never stored
     * itself: {@link #toSessionValue} is what the session holds.
     *
     * @param value the random bytes of the challenge
     * @param createdAt when the challenge was made
     * @param userName the user a registration is for; null for a login
     * @param userHandle the user handle a registration uses; null for a login
     */
    protected record PendingChallenge(byte[] value, long createdAt, String userName, String userHandle) {

        /**
         * Reads a challenge from its session value.
         *
         * @param sessionValue the value of the session attribute
         * @return the challenge, or null when the value is not one
         */
        static PendingChallenge of(final Object sessionValue) {
            if (!(sessionValue instanceof final Map<?, ?> map) || !(map.get(CHALLENGE_VALUE) instanceof final byte[] value)
                    || value.length == 0 || !(map.get(CHALLENGE_CREATED_AT) instanceof final Long createdAt)) {
                return null;
            }
            final Object userName = map.get(CHALLENGE_USER_NAME);
            final Object userHandle = map.get(CHALLENGE_USER_HANDLE);
            if (userName != null && !(userName instanceof String) || userHandle != null && !(userHandle instanceof String)) {
                return null;
            }
            return new PendingChallenge(value, createdAt, (String) userName, (String) userHandle);
        }
    }
}
