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
package org.codelibs.fess.session;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fesen.opensearch.OpenSearchException;
import org.codelibs.fesen.opensearch.action.DocWriteRequest.OpType;
import org.codelibs.fesen.opensearch.action.get.GetResponse;
import org.codelibs.fesen.opensearch.core.rest.RestStatus;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.SystemUtil;
import org.lastaflute.web.LastaWebKey;
import org.lastaflute.web.util.LaServletContextUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletContext;

/**
 * Stores the HTTP sessions of the Fess web application in OpenSearch, so that the nodes behind a
 * load balancer share them.
 *
 * <p>The Tomcat side of the store ({@code org.codelibs.fess.tomcat.session}) is loaded by Tomcat's own
 * class loader and cannot see this class. The two meet through the {@link ServletContext} attribute
 * {@value #CONTEXT_ATTRIBUTE}, whose value is a {@code Map<String, Function<Map<String, Object>, Object>>}
 * of the operations below. Only JDK types cross that boundary, and the attribute values of a session
 * travel as opaque objects.</p>
 *
 * <ul>
 *   <li>{@code load}: {@code {id, digest}} (the digest is optional) to {@code null} (no document, a format mismatch or an expired
 *   session) or a map of {@code creationTime}, {@code lastAccessedTime}, {@code maxInactiveInterval},
 *   {@code digest} and {@code attributes}. It throws when the store cannot be reached.</li>
 *   <li>{@code save}: {@code {id, creationTime, lastAccessedTime, maxInactiveInterval, attributes,
 *   digest, persistedAt}} to {@code {digest, persistedAt, skippedNames}} or, when the session is gone from the store,
 *   {@code {gone=true}}. The attributes are written only when their digest differs from the {@code digest}
 *   the caller already knows; otherwise only the expiry is touched, and only when the last write is older
 *   than {@value #TOUCH_INTERVAL_MS} ms. A session that was stored before ({@code persistedAt} greater than
 *   zero) is only ever updated, never created: if its document is missing, it was deleted (logout, a new
 *   session id, a password change) or expired on another node, and writing it again would log the old
 *   session id in on every node, so the result is {@code gone} and the caller must drop the session.</li>
 *   <li>{@code delete}: {@code {id}} to {@code null}.</li>
 *   <li>{@code purge}: {@code {}} to the number of expired documents deleted.</li>
 * </ul>
 *
 * <p>The document id is the SHA-256 of the session id, because the session id is a bearer secret.
 * Writers on several nodes race with last writer wins.</p>
 *
 * <p>The operations are registered only when the environment variable {@value #ENV_NAME} is
 * {@value #ENV_VALUE}, so a default installation does not touch the index.</p>
 */
public class SessionStore {

    private static final Logger logger = LogManager.getLogger(SessionStore.class);

    /** The name of the {@link ServletContext} attribute that holds the operations. */
    public static final String CONTEXT_ATTRIBUTE = "fess.session.store";

    /** The environment variable that selects the session store. */
    public static final String ENV_NAME = "FESS_SESSION_STORE";

    /** The value of {@value #ENV_NAME} that selects this store. */
    public static final String ENV_VALUE = "opensearch";

    /** How long a session may go unwritten before an unchanged session touches its expiry. */
    protected static final long TOUCH_INTERVAL_MS = 60 * 1000L;

    /** The configuration index that holds the sessions, with the default prefix. */
    protected static final String INDEX_NAME = "fess_config.session";

    /** The expiry that a session without a timeout gets. */
    protected static final long NEVER_EXPIRES = Long.MAX_VALUE;

    /** The field of the creation time. */
    protected static final String CREATION_TIME = "creationTime";

    /** The field of the last access time. */
    protected static final String LAST_ACCESSED_TIME = "lastAccessedTime";

    /** The field of the maximum inactive interval in seconds. */
    protected static final String MAX_INACTIVE_INTERVAL = "maxInactiveInterval";

    /** The field of the expiry time. */
    protected static final String EXPIRED_TIME = "expiredTime";

    /** The field of the digest of the serialized attributes. */
    protected static final String DIGEST = "digest";

    /** The field of the format version of the serialized attributes. */
    protected static final String FORMAT_VERSION = "formatVersion";

    /** The field of the serialized attributes. */
    protected static final String DATA = "data";

    /** The serializer of the session attributes. */
    @Resource
    protected SessionDataSerializer sessionDataSerializer;

    /**
     * Default constructor.
     */
    public SessionStore() {
        // Default constructor
    }

    /**
     * Publishes the operations to the servlet context when the OpenSearch session store is selected.
     */
    @PostConstruct
    public void init() {
        if (!isEnabled()) {
            return;
        }
        final ServletContext servletContext = getServletContext();
        if (servletContext == null) {
            logger.warn("The session store is selected, but there is no servlet context: env={}", ENV_NAME);
            return;
        }
        servletContext.setAttribute(CONTEXT_ATTRIBUTE, createOperations());
        if (logger.isInfoEnabled()) {
            logger.info("The session store is available: index={}", getIndexName());
        }
    }

    /**
     * Withdraws the operations from the servlet context.
     */
    @PreDestroy
    public void destroy() {
        if (!isEnabled()) {
            return;
        }
        final ServletContext servletContext = getServletContext();
        if (servletContext != null) {
            servletContext.removeAttribute(CONTEXT_ATTRIBUTE);
        }
    }

    /**
     * Tells whether the environment selects the OpenSearch session store.
     *
     * @return true in the webapp process if {@value #ENV_NAME} equals {@value #ENV_VALUE}, ignoring the case.
     *         The job processes (crawler and the others) boot this component too and inherit the environment,
     *         but they have no sessions.
     */
    protected boolean isEnabled() {
        return SystemUtil.isWebappProcess() && ENV_VALUE.equalsIgnoreCase(getEnv(ENV_NAME));
    }

    /**
     * Reads an environment variable.
     *
     * @param name the name of the variable.
     * @return the value, or null if it is not set.
     */
    protected String getEnv(final String name) {
        return System.getenv(name);
    }

    /**
     * Returns the servlet context of the web application.
     *
     * @return the servlet context, or null if there is none.
     */
    protected ServletContext getServletContext() {
        try {
            return LaServletContextUtil.getServletContext();
        } catch (final RuntimeException e) {
            logger.debug("No servlet context.", e);
            return null;
        }
    }

    /**
     * Creates the operations that the Tomcat side calls.
     *
     * @return the operations by name.
     */
    protected Map<String, Function<Map<String, Object>, Object>> createOperations() {
        final Map<String, Function<Map<String, Object>, Object>> operations = new HashMap<>();
        operations.put("load", this::load);
        operations.put("save", this::save);
        operations.put("delete", this::delete);
        operations.put("purge", this::purge);
        return operations;
    }

    // ===================================================================================
    //                                                                          Operations
    //                                                                          ==========

    /**
     * Loads a session.
     *
     * @param args the arguments, which hold the session id as {@code id} and, optionally, the {@code digest} of the
     *        attributes the caller already has.
     * @return null if there is no usable session, or the session as a map. The map has no {@code attributes}
     *         if the stored digest equals the one passed in.
     * @throws RuntimeException if the store cannot be read.
     */
    protected Map<String, Object> load(final Map<String, Object> args) {
        final Map<String, Object> source = getSession(hashId(getId(args)));
        if (source == null) {
            return null;
        }
        if (!sessionDataSerializer.getFormatVersion().equals(source.get(FORMAT_VERSION))) {
            if (logger.isDebugEnabled()) {
                logger.debug("The stored session has another format version: stored={}, current={}", source.get(FORMAT_VERSION),
                        sessionDataSerializer.getFormatVersion());
            }
            return null;
        }
        if (!(source.get(EXPIRED_TIME) instanceof final Number expiredTime) || expiredTime.longValue() <= currentTimeMillis()) {
            return null;
        }
        final Map<String, Object> session = new HashMap<>();
        session.put(CREATION_TIME, toLong(source.get(CREATION_TIME)));
        session.put(LAST_ACCESSED_TIME, toLong(source.get(LAST_ACCESSED_TIME)));
        session.put(MAX_INACTIVE_INTERVAL, ((Number) source.get(MAX_INACTIVE_INTERVAL)).intValue());
        session.put(DIGEST, source.get(DIGEST));
        if (source.get(DIGEST) != null && source.get(DIGEST).equals(args.get(DIGEST))) {
            // The caller already holds this content: it is not decoded and deserialized on every request.
            return session;
        }
        if (!(source.get(DATA) instanceof final String data)) {
            return null;
        }
        // Base64 takes 4 characters for 3 bytes: a longer text is decoded to more than the serializer accepts
        if (data.length() > SessionDataSerializer.MAX_PAYLOAD_BYTES / 3 * 4 + 8) {
            return null;
        }
        final Map<String, Object> attributes = sessionDataSerializer.fromBytes(Base64.getDecoder().decode(data));
        if (attributes == null) {
            return null;
        }
        session.put("attributes", attributes);
        return session;
    }

    /**
     * Saves a session when its attributes changed, or touches its expiry when they did not.
     *
     * @param args the session, see the class description.
     * @return the digest and the time of the last write, for the caller to pass back next time, and the names of the
     *         attributes that were not stored ({@code skippedNames}), which the caller must keep local; or
     *         {@code gone=true} if the session was stored before and its document no longer exists.
     * @throws RuntimeException if the store cannot be written.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> save(final Map<String, Object> args) {
        final String id = hashId(getId(args));
        final long lastAccessedTime = ((Number) args.get(LAST_ACCESSED_TIME)).longValue();
        final int maxInactiveInterval = ((Number) args.get(MAX_INACTIVE_INTERVAL)).intValue();
        final long expiredTime = calculateExpiredTime(lastAccessedTime, maxInactiveInterval);

        final Set<String> skippedNames = new TreeSet<>();
        final byte[] bytes = sessionDataSerializer.toBytes((Map<String, Object>) args.get("attributes"), skippedNames);
        final String digest = digest(bytes);
        final long now = currentTimeMillis();

        if (!isStored(args) && isRebuildable(((Map<String, Object>) args.get("attributes")).keySet(), skippedNames)) {
            // A session that holds nothing but what every node derives from the request, such as the
            // locale of a client that sends no cookie (health checks, API clients), is not worth a document.
            return result(digest, 0L, skippedNames);
        }

        final boolean stored = isStored(args);
        if (stored && digest.equals(args.get(DIGEST))) {
            final Object persistedAt = args.get("persistedAt");
            if (now - ((Number) persistedAt).longValue() < TOUCH_INTERVAL_MS) {
                return result(digest, ((Number) persistedAt).longValue(), skippedNames);
            }
            return touchSession(id, lastAccessedTime, expiredTime) ? result(digest, now, skippedNames) : gone();
        }

        final Map<String, Object> source = new HashMap<>();
        source.put(DIGEST, digest);
        source.put(FORMAT_VERSION, sessionDataSerializer.getFormatVersion());
        source.put(DATA, Base64.getEncoder().encodeToString(bytes));
        source.put(CREATION_TIME, ((Number) args.get(CREATION_TIME)).longValue());
        source.put(LAST_ACCESSED_TIME, lastAccessedTime);
        source.put(MAX_INACTIVE_INTERVAL, maxInactiveInterval);
        source.put(EXPIRED_TIME, expiredTime);
        if (!stored && createSession(id, source)) {
            return result(digest, now, skippedNames);
        }
        // Either a stored session, or a new one whose document another request of the same session just created.
        // Both are updates: a document that is missing now was deleted, and must stay deleted.
        return updateSession(id, source) ? result(digest, now, skippedNames) : gone();
    }

    /**
     * Tells whether the session has been written to the store before.
     *
     * @param args the arguments of {@code save}.
     * @return true if the caller reports a write.
     */
    protected boolean isStored(final Map<String, Object> args) {
        return args.get("persistedAt") instanceof final Number number && number.longValue() > 0;
    }

    /**
     * Tells whether the session holds nothing that another node could not rebuild from a request.
     *
     * @param names the attribute names of the session.
     * @param skippedNames the names that are not stored.
     * @return true if no stored attribute is left apart from the locale.
     */
    protected boolean isRebuildable(final Set<String> names, final Set<String> skippedNames) {
        return names.stream().allMatch(name -> skippedNames.contains(name) || LastaWebKey.USER_LOCALE_KEY.equals(name));
    }

    /**
     * Deletes a session.
     *
     * @param args the arguments, which hold the session id as {@code id}.
     * @return null.
     */
    protected Object delete(final Map<String, Object> args) {
        deleteSession(hashId(getId(args)));
        return null;
    }

    /**
     * Deletes the sessions that expired.
     *
     * @param args the arguments, which are not used.
     * @return the number of deleted sessions.
     */
    protected Long purge(final Map<String, Object> args) {
        return deleteExpiredSessions(currentTimeMillis());
    }

    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======

    /**
     * Calculates when a session expires.
     *
     * @param lastAccessedTime the last access time in milliseconds.
     * @param maxInactiveInterval the maximum inactive interval in seconds; Tomcat does not expire a
     *        session when it is zero or negative.
     * @return the expiry time in milliseconds.
     */
    protected long calculateExpiredTime(final long lastAccessedTime, final int maxInactiveInterval) {
        return maxInactiveInterval > 0 ? lastAccessedTime + maxInactiveInterval * 1000L : NEVER_EXPIRES;
    }

    /**
     * Returns the current time.
     *
     * @return the current time in milliseconds.
     */
    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Hashes a session id into the id of its document.
     *
     * @param sessionId the session id.
     * @return the SHA-256 of the session id in lower case hexadecimal.
     */
    protected String hashId(final String sessionId) {
        return digest(sessionId.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Calculates the SHA-256 digest of bytes.
     *
     * @param bytes the bytes.
     * @return the digest in lower case hexadecimal.
     */
    protected String digest(final byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available.", e);
        }
    }

    private String getId(final Map<String, Object> args) {
        final Object id = args.get("id");
        if (!(id instanceof final String sessionId) || sessionId.isEmpty()) {
            throw new IllegalArgumentException("The session id is required.");
        }
        return sessionId;
    }

    private static Long toLong(final Object value) {
        return ((Number) value).longValue();
    }

    private static Map<String, Object> gone() {
        final Map<String, Object> result = new HashMap<>();
        result.put("gone", Boolean.TRUE);
        return result;
    }

    private static Map<String, Object> result(final String digest, final long persistedAt, final Set<String> skippedNames) {
        final Map<String, Object> result = new HashMap<>();
        result.put(DIGEST, digest);
        result.put("persistedAt", persistedAt);
        result.put("skippedNames", skippedNames);
        return result;
    }

    // ===================================================================================
    //                                                                        Search Engine
    //                                                                        =============

    /**
     * Returns the name of the index of the sessions.
     *
     * @return the index name.
     */
    protected String getIndexName() {
        return INDEX_NAME.replaceFirst("fess_config", ComponentUtil.getFessConfig().getIndexConfigIndex());
    }

    /**
     * Reads a session document. A get request is real-time by default and the HTTP client sends
     * {@code realtime=true}, so a session that another node just wrote is visible without waiting for a
     * refresh.
     *
     * @param id the document id.
     * @return the source of the document, or null if there is none.
     * @throws RuntimeException if the search engine fails.
     */
    protected Map<String, Object> getSession(final String id) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final GetResponse response = ComponentUtil.getSearchEngineClient()
                .prepareGet(getIndexName(), id)
                .execute()
                .actionGet(fessConfig.getIndexSearchTimeout());
        return response.isExists() ? response.getSourceAsMap() : null;
    }

    /**
     * Creates the document of a new session. The request has {@code op_type=create}, so it fails instead of
     * replacing a document that exists. The index is not refreshed, because the reads are real-time.
     *
     * @param id the document id.
     * @param source the source of the document.
     * @return false if the document already exists, true if it was created.
     */
    protected boolean createSession(final String id, final Map<String, Object> source) {
        try {
            ComponentUtil.getSearchEngineClient()
                    .prepareIndex(getIndexName())
                    .setId(id)
                    .setOpType(OpType.CREATE)
                    .setSource(source)
                    .execute()
                    .actionGet(ComponentUtil.getFessConfig().getIndexIndexTimeout());
            return true;
        } catch (final OpenSearchException e) {
            if (e.status() == RestStatus.CONFLICT) {
                return false;
            }
            throw e;
        }
    }

    /**
     * Writes the fields of a session document with a partial update, without upsert, so that a document
     * that was deleted is not created again.
     *
     * @param id the document id.
     * @param source the fields to write.
     * @return false if the document does not exist, true otherwise.
     */
    protected boolean updateSession(final String id, final Map<String, Object> source) {
        try {
            ComponentUtil.getSearchEngineClient()
                    .prepareUpdate(getIndexName(), id)
                    .setDoc(toKeyValues(source))
                    .setDocAsUpsert(false)
                    .execute()
                    .actionGet(ComponentUtil.getFessConfig().getIndexIndexTimeout());
            return true;
        } catch (final OpenSearchException e) {
            if (isDocumentMissing(e)) {
                logger.debug("A session document to update was gone: id={}", id);
                return false;
            }
            throw e;
        }
    }

    /**
     * Flattens a map into the key and value pairs that {@code UpdateRequestBuilder#setDoc(Object...)} takes.
     * A {@code Map} passed to {@code setDoc} directly binds to that varargs method as a single argument and fails.
     *
     * @param fields the fields of the document.
     * @return the keys and values in turn.
     */
    protected static Object[] toKeyValues(final Map<String, Object> fields) {
        final Object[] keyValues = new Object[fields.size() * 2];
        int i = 0;
        for (final Map.Entry<String, Object> entry : fields.entrySet()) {
            keyValues[i++] = entry.getKey();
            keyValues[i++] = entry.getValue();
        }
        return keyValues;
    }

    /**
     * Moves the expiry of a stored session with a partial update, without upsert.
     *
     * @param id the document id.
     * @param lastAccessedTime the last access time.
     * @param expiredTime the expiry time.
     * @return false if the document does not exist, true otherwise.
     */
    protected boolean touchSession(final String id, final long lastAccessedTime, final long expiredTime) {
        final Map<String, Object> doc = new HashMap<>();
        doc.put(LAST_ACCESSED_TIME, lastAccessedTime);
        doc.put(EXPIRED_TIME, expiredTime);
        try {
            ComponentUtil.getSearchEngineClient()
                    .prepareUpdate(getIndexName(), id)
                    .setDoc(toKeyValues(doc))
                    .execute()
                    .actionGet(ComponentUtil.getFessConfig().getIndexIndexTimeout());
            return true;
        } catch (final OpenSearchException e) {
            if (isDocumentMissing(e)) {
                logger.debug("A session document to touch was gone: id={}", id);
                return false;
            }
            throw e;
        }
    }

    /**
     * Tells whether an update failed because the document does not exist. Through the HTTP client the
     * exception is a plain status exception that carries the type in its message.
     *
     * @param e the exception of the update.
     * @return true if the document is missing.
     */
    protected boolean isDocumentMissing(final OpenSearchException e) {
        return e.status() == RestStatus.NOT_FOUND && e.getMessage() != null && e.getMessage().contains("document_missing_exception");
    }

    /**
     * Deletes a session document.
     *
     * @param id the document id.
     */
    protected void deleteSession(final String id) {
        ComponentUtil.getSearchEngineClient().delete(getIndexName(), id);
    }

    /**
     * Deletes the session documents that expired before a time.
     *
     * @param now the time in milliseconds.
     * @return the number of deleted documents.
     */
    protected long deleteExpiredSessions(final long now) {
        return ComponentUtil.getSearchEngineClient().deleteByQuery(getIndexName(), QueryBuilders.rangeQuery(EXPIRED_TIME).lt(now));
    }
}
