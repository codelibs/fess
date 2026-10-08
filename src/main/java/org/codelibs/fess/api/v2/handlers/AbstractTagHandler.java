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
package org.codelibs.fess.api.v2.handlers;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.app.service.TagTypeService;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.entity.TagChange;
import org.codelibs.fess.exception.TagTypeConflictException;
import org.codelibs.fess.helper.TagTypeHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;

import com.google.common.util.concurrent.Striped;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Shared parts of the user tag endpoints ({@link TagsHandler} and {@link DocumentTagsHandler}).
 *
 * <p>Every tag endpoint needs {@code user.tag.enabled} and a logged-in user: the user of the login session, which an
 * access token never stands in for. A caller changes only the tags it owns ({@code owner} equals
 * {@link FessUserBean#getUserId()}); the tag of someone else is reported as {@link V2ErrorCode#FORBIDDEN} when the
 * caller can see it and as {@link V2ErrorCode#NOT_FOUND} otherwise, so that a hidden tag is not revealed.</p>
 */
public abstract class AbstractTagHandler {

    private static final Logger logger = LogManager.getLogger(AbstractTagHandler.class);

    /** The request bodies hold a short name, an id and a flag. */
    protected static final int MAX_BODY_BYTES = 1024;

    /** How many times an update that lost a race with another writer is read and applied again. */
    protected static final int MAX_UPDATE_ATTEMPTS = 8;

    /** The wait before the first retry of an update, in milliseconds. It doubles with every retry. */
    protected static final long RETRY_WAIT_MILLIS = 20L;

    /** The longest wait before a retry of an update, in milliseconds. */
    protected static final long RETRY_WAIT_MAX_MILLIS = 320L;

    /**
     * Serializes the updates of one tag in this JVM: a fixed set of locks, each shared by the tags whose ids hash to it,
     * so that the number of locks does not grow with the number of tags.
     */
    private static final Striped<Lock> UPDATE_LOCKS = Striped.lock(64);

    /** A tag type id: the SHA-256 of the tag value in lowercase hex. */
    private static final Pattern TAG_ID_PATTERN = Pattern.compile("[0-9a-f]{64}");

    /**
     * Default constructor.
     */
    protected AbstractTagHandler() {
        // no-op
    }

    /** A failure that is answered with an error envelope. */
    protected static class TagRequestException extends Exception {
        private static final long serialVersionUID = 1L;

        /** The error code of the envelope. */
        protected final transient V2ErrorCode code;

        /**
         * Creates the failure.
         *
         * @param code the error code of the envelope
         * @param message the error message of the envelope
         */
        protected TagRequestException(final V2ErrorCode code, final String message) {
            super(message, null, false, false);
            this.code = code;
        }
    }

    /** A change applied to a freshly read tag type before it is written back. */
    @FunctionalInterface
    protected interface TagTypeModifier {
        /**
         * Changes the tag type.
         *
         * @param tagType the tag type as stored, with its paths
         * @return true if the tag type was changed and has to be written
         * @throws TagRequestException if the change is refused
         */
        boolean modify(TagType tagType) throws TagRequestException;
    }

    /**
     * Checks that user tags are enabled and returns the logged-in user.
     *
     * @return the user id of the caller
     * @throws TagRequestException if user tags are disabled or nobody is logged in
     */
    protected String checkRequest() throws TagRequestException {
        if (!ComponentUtil.getFessConfig().isUserTagEnabled()) {
            throw new TagRequestException(V2ErrorCode.INVALID_REQUEST, "tag feature is not available");
        }
        final String userId = getUserBean().map(FessUserBean::getUserId).filter(StringUtil::isNotBlank).orElse(null);
        if (userId == null) {
            throw new TagRequestException(V2ErrorCode.AUTH_REQUIRED, "login required");
        }
        return userId;
    }

    /**
     * Reads the JSON body of the request.
     *
     * @param req the request
     * @return the body
     * @throws TagRequestException if the body is not a small JSON object
     * @throws IOException if the body cannot be read
     */
    protected Map<String, Object> readBody(final HttpServletRequest req) throws TagRequestException, IOException {
        try {
            return ComponentUtil.getV2JsonBody().read(req, MAX_BODY_BYTES);
        } catch (final V2JsonBody.PayloadTooLargeException e) {
            throw new TagRequestException(V2ErrorCode.PAYLOAD_TOO_LARGE, e.getMessage());
        } catch (final V2JsonBody.UnsupportedMediaTypeException e) {
            throw new TagRequestException(V2ErrorCode.UNSUPPORTED_MEDIA_TYPE, e.getMessage());
        } catch (final V2JsonBody.MalformedJsonException e) {
            throw new TagRequestException(V2ErrorCode.INVALID_REQUEST, e.getMessage());
        }
    }

    /**
     * Returns the normalized tag name of a request.
     *
     * @param name the name in the request body
     * @return the normalized name
     * @throws TagRequestException if the name is not a valid tag name
     */
    protected String toTagName(final Object name) throws TagRequestException {
        final String normalized = name instanceof final String value ? getTagTypeHelper().normalizeName(value).orElse(null) : null;
        if (normalized == null) {
            throw new TagRequestException(V2ErrorCode.INVALID_REQUEST,
                    "invalid tag name: enter 1 to " + ComponentUtil.getFessConfig().getUserTagNameMaxLengthAsInteger() + " characters");
        }
        return normalized;
    }

    /**
     * Returns the {@code shared} flag of a request body.
     *
     * @param body the request body
     * @return the flag, or null when the body has none
     * @throws TagRequestException if the flag is not a boolean
     */
    protected Boolean toShared(final Map<String, Object> body) throws TagRequestException {
        final Object shared = body.get("shared");
        if (shared == null || shared instanceof Boolean) {
            return (Boolean) shared;
        }
        throw new TagRequestException(V2ErrorCode.INVALID_REQUEST, "shared must be a boolean");
    }

    /**
     * Builds a new tag of the caller, checking {@code user.tag.max.tags}. The tag is not stored.
     *
     * @param name the normalized tag name
     * @param userId the caller, who owns the tag
     * @param shared whether the tag is shared
     * @param paths the paths of the tag
     * @return the tag type with its id set
     * @throws TagRequestException if the caller has {@code user.tag.max.tags} tags
     */
    protected TagType newTagType(final String name, final String userId, final boolean shared, final String[] paths)
            throws TagRequestException {
        final int maxTags = ComponentUtil.getFessConfig().getUserTagMaxTagsAsInteger();
        if (getTagTypeService().countByOwner(userId) >= maxTags) {
            throw new TagRequestException(V2ErrorCode.INVALID_REQUEST, "too many tags: a user can have up to " + maxTags + " tags");
        }
        final TagTypeHelper helper = getTagTypeHelper();
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        final TagType tagType = new TagType();
        tagType.setName(name);
        tagType.setOwner(userId);
        tagType.setId(helper.toId(tagType.getTagValue()));
        tagType.setPaths(paths);
        tagType.setPermissions(helper.buildPermissions(userId, shared));
        tagType.setVirtualHost(ComponentUtil.getVirtualHostHelper().getVirtualHostKey());
        tagType.setSortOrder(0);
        tagType.setCreatedBy(userId);
        tagType.setCreatedTime(now);
        tagType.setUpdatedBy(userId);
        tagType.setUpdatedTime(now);
        return tagType;
    }

    /**
     * Reads a tag type that the caller owns, with its paths.
     *
     * @param id the tag type id
     * @param userId the caller
     * @return the tag type
     * @throws TagRequestException NOT_FOUND if there is no such tag or the caller cannot see it, FORBIDDEN if the
     *         caller can see it but does not own it
     */
    protected TagType getOwnTagType(final String id, final String userId) throws TagRequestException {
        final TagType tagType = id != null && TAG_ID_PATTERN.matcher(id).matches() ? getTagTypeService().getTagType(id).orElse(null) : null;
        if (tagType == null) {
            throw new TagRequestException(V2ErrorCode.NOT_FOUND, "tag not found");
        }
        if (!userId.equals(tagType.getOwner())) {
            final String value = tagType.getTagValue();
            if (getTagTypeHelper().getVisibleTagTypes(List.of(value), SearchRequestType.JSON).containsKey(value)) {
                throw new TagRequestException(V2ErrorCode.FORBIDDEN, "the tag is not yours");
            }
            throw new TagRequestException(V2ErrorCode.NOT_FOUND, "tag not found");
        }
        return tagType;
    }

    /**
     * Reads a tag type the caller owns, changes it and writes it back.
     *
     * <p>The updates of one tag run one at a time in this JVM, so that requests on the same tag do not lose a race to
     * each other. A write that still lost a race, to another node or to the admin console, is retried on a fresh read
     * after a wait that grows with every attempt, so that a concurrent change of the paths is not overwritten. The
     * wait is outside the lock.</p>
     *
     * @param id the tag type id
     * @param userId the caller
     * @param modifier the change
     * @return the tag type as written, or as read when the modifier changed nothing
     * @throws TagRequestException if the tag is not the caller's, the change is refused, or every attempt lost a race
     */
    protected TagType updateTagType(final String id, final String userId, final TagTypeModifier modifier) throws TagRequestException {
        final Lock lock = UPDATE_LOCKS.get(String.valueOf(id));
        for (int attempt = 1;; attempt++) {
            lock.lock();
            try {
                final TagType tagType = getOwnTagType(id, userId);
                if (!modifier.modify(tagType)) {
                    return tagType;
                }
                tagType.setUpdatedBy(userId);
                tagType.setUpdatedTime(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
                getTagTypeService().update(tagType);
                return tagType;
            } catch (final TagTypeConflictException e) {
                if (attempt >= MAX_UPDATE_ATTEMPTS) {
                    logger.warn("Failed to update the tag after {} attempts: id={}", attempt, id, e);
                    throw new TagRequestException(V2ErrorCode.CONFLICT, "the tag was changed concurrently; try again");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("The tag was changed concurrently; retrying: id={}, attempt={}", id, attempt);
                }
            } finally {
                lock.unlock();
            }
            pause(getRetryWaitMillis(attempt));
        }
    }

    /**
     * Returns how long to wait before the retry that follows a failed attempt: {@link #RETRY_WAIT_MILLIS} doubled for
     * every earlier failure up to {@link #RETRY_WAIT_MAX_MILLIS}, then cut by a random share of up to half, so that
     * writers that lost together do not retry together.
     *
     * @param attempt the number of the attempt that failed, starting at 1
     * @return the wait in milliseconds
     */
    protected long getRetryWaitMillis(final int attempt) {
        final long wait = Math.min(RETRY_WAIT_MAX_MILLIS, RETRY_WAIT_MILLIS * (1L << Math.min(attempt - 1, 16)));
        return ThreadLocalRandom.current().nextLong(wait / 2, wait + 1);
    }

    /**
     * Waits before a retry. Exposed as a seam for unit tests.
     *
     * @param millis the time to wait
     */
    protected void pause(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Queues a change of the tag field of the documents.
     *
     * @param change the change
     */
    protected void enqueue(final TagChange change) {
        if (ComponentUtil.getFessConfig().isUserTagEnabled()) {
            getTagTypeHelper().enqueue(change);
        }
    }

    /**
     * Builds the JSON of a tag as a document or a search hit shows it.
     *
     * @param tagType the tag type
     * @param userId the caller
     * @return {@code {id, value, name, owner, mine, shared}}
     */
    protected Map<String, Object> toDocumentTag(final TagType tagType, final String userId) {
        final String value = tagType.getTagValue();
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", getTagTypeHelper().toId(value));
        map.put("value", value);
        map.put("name", tagType.getName());
        map.put("owner", tagType.getOwner());
        map.put("mine", userId.equals(tagType.getOwner()));
        map.put("shared", getTagTypeHelper().isShared(tagType));
        return map;
    }

    /**
     * Returns the tag type helper.
     *
     * @return the helper
     */
    protected TagTypeHelper getTagTypeHelper() {
        return ComponentUtil.getTagTypeHelper();
    }

    /**
     * Returns the tag type service. Exposed as a seam for unit tests.
     *
     * @return the service
     */
    protected TagTypeService getTagTypeService() {
        return ComponentUtil.getComponent(TagTypeService.class);
    }

    /**
     * Returns the user of the login session. An access token is not a login session. Exposed as a seam for unit
     * tests.
     *
     * @return the logged-in user
     */
    protected OptionalThing<FessUserBean> getUserBean() {
        return ComponentUtil.getFessLoginAssist().getSavedUserBean();
    }
}
