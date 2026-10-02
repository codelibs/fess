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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.app.web.admin.labeltype.AdminLabeltypeAction;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.TagHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.dbflute.optional.OptionalThing;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code GET}, {@code POST} and {@code DELETE /api/v2/documents/{docId}/tags}.
 *
 * <p>A tag belongs to a label type of the kind {@code tag}, whose permissions decide who can see and add its
 * tags. Its value is {@code <label type value>:<tag name>}.</p>
 *
 * <ul>
 *   <li>{@code GET} lists the tags of the document that the caller can see, with the number of users who added
 *       each one and whether the caller added it.</li>
 *   <li>{@code POST} with {@code {"type": "...", "name": "..."}} adds a tag for the logged-in user.</li>
 *   <li>{@code DELETE ?value=<type>:<name>} removes the logged-in user's tag. A user with the admin role or the
 *       label administration role removes the tag of every user.</li>
 * </ul>
 *
 * <p>The document is resolved through the caller's roles, so a document the caller cannot search cannot be
 * tagged. Tags are recorded per URL in the tag log, and every indexed document of the URL is updated, so a tag
 * applies to the URL and survives a re-crawl. Writes need a logged-in user; an access token does not stand in
 * for one.</p>
 */
public class DocumentTagsHandler {

    private static final Logger logger = LogManager.getLogger(DocumentTagsHandler.class);

    /** The request body has two short strings. */
    private static final int MAX_BODY_BYTES = 2048;

    /**
     * Default constructor used by the DI container. The handler holds no per-request state.
     */
    public DocumentTagsHandler() {
        // no-op
    }

    /** A failure that is answered with an error envelope. */
    private static class TagRequestException extends Exception {
        private static final long serialVersionUID = 1L;

        private final V2ErrorCode code;

        TagRequestException(final V2ErrorCode code, final String message) {
            super(message, null, false, false);
            this.code = code;
        }
    }

    /**
     * Processes one {@code /api/v2/documents/{docId}/tags} request.
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @param docId the document id extracted from the URL path
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res, final String docId) throws IOException {
        final String method = req.getMethod() == null ? StringUtil.EMPTY : req.getMethod().toUpperCase(Locale.ROOT);
        if (!"GET".equals(method) && !"POST".equals(method) && !"DELETE".equals(method)) {
            res.setHeader("Allow", "GET, POST, DELETE");
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        final String context = "/api/v2/documents/" + docId + "/tags " + method;
        try {
            if (!ComponentUtil.getV2DocIdValidator().isValid(docId)) {
                throw new TagRequestException(V2ErrorCode.INVALID_REQUEST, "invalid doc_id");
            }
            final TagHelper tagHelper = ComponentUtil.getTagHelper();
            if (!tagHelper.isEnabled()) {
                throw new TagRequestException(V2ErrorCode.INVALID_REQUEST, "tag feature is not available");
            }
            final OptionalThing<FessUserBean> userBean = getUserBean();
            final String userId = userBean.map(FessUserBean::getUserId).orElse(null);
            if (!"GET".equals(method) && StringUtil.isBlank(userId)) {
                throw new TagRequestException(V2ErrorCode.AUTH_REQUIRED, "login required");
            }
            final Set<String> typeSet = getTagTypeValueSet(req);
            final String url = getDocumentUrl(docId, userBean);

            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("doc_id", docId);
            Map<String, Long> countMap;
            switch (method) {
            case "POST" -> {
                final boolean added = addTag(req, tagHelper, typeSet, userId, url, docId);
                countMap = added ? updateDocuments(tagHelper, url) : tagHelper.getTagCountMap(url);
                payload.put("added", added);
            }
            case "DELETE" -> {
                final String value = req.getParameter("value");
                if (!tagHelper.isVisible(value, typeSet)) {
                    throw new TagRequestException(V2ErrorCode.INVALID_REQUEST, "invalid tag value");
                }
                final int removed = tagHelper.removeTag(isTagAdministrator(userBean) ? null : userId, url, value);
                countMap = removed > 0 ? updateDocuments(tagHelper, url) : tagHelper.getTagCountMap(url);
                payload.put("removed", removed);
            }
            default -> countMap = tagHelper.getTagCountMap(url);
            }
            final Set<String> userTagSet = StringUtil.isBlank(userId) ? Collections.emptySet() : tagHelper.getUserTagSet(userId, url);
            payload.put("addable", StringUtil.isNotBlank(userId) && !typeSet.isEmpty());
            payload.put("tags", buildTags(tagHelper, countMap, typeSet, userTagSet, isTagAdministrator(userBean)));
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
        } catch (final TagRequestException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, e.code, e.getMessage());
        } catch (final Exception e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, context);
        }
    }

    /**
     * Validates the request body of a POST and records the tag.
     *
     * @return true if the tag was added, false if the user had already added it
     */
    private boolean addTag(final HttpServletRequest req, final TagHelper tagHelper, final Set<String> typeSet, final String userId,
            final String url, final String docId) throws TagRequestException, IOException {
        final Map<String, Object> body;
        try {
            body = ComponentUtil.getV2JsonBody().read(req, MAX_BODY_BYTES);
        } catch (final V2JsonBody.PayloadTooLargeException e) {
            throw new TagRequestException(V2ErrorCode.PAYLOAD_TOO_LARGE, e.getMessage());
        } catch (final V2JsonBody.UnsupportedMediaTypeException e) {
            throw new TagRequestException(V2ErrorCode.UNSUPPORTED_MEDIA_TYPE, e.getMessage());
        } catch (final V2JsonBody.MalformedJsonException e) {
            throw new TagRequestException(V2ErrorCode.INVALID_REQUEST, e.getMessage());
        }
        final Object type = body.get("type");
        if (!(type instanceof final String typeValue) || !typeSet.contains(typeValue)) {
            throw new TagRequestException(V2ErrorCode.INVALID_REQUEST, "invalid tag type");
        }
        final Object name = body.get("name");
        final String normalizedName = name instanceof final String nameValue ? tagHelper.normalizeName(nameValue) : null;
        if (normalizedName == null) {
            throw new TagRequestException(V2ErrorCode.INVALID_REQUEST,
                    "invalid tag name: enter 1 to " + tagHelper.getNameMaxLength() + " characters without \" or \\");
        }
        final String value = tagHelper.toValue(typeValue, normalizedName);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Set<String> userTagSet = tagHelper.getUserTagSet(userId, url);
        if (userTagSet.contains(value)) {
            return false;
        }
        if (userTagSet.size() >= fessConfig.getUserTagMaxPerDocumentAsInteger()) {
            throw new TagRequestException(V2ErrorCode.INVALID_REQUEST,
                    "too many tags: one user can add up to " + fessConfig.getUserTagMaxPerDocumentAsInteger() + " tags to a document");
        }
        final Map<String, Long> countMap = tagHelper.getTagCountMap(url);
        if (!countMap.containsKey(value) && countMap.size() >= fessConfig.getUserTagMaxDocumentTagsAsInteger()) {
            throw new TagRequestException(V2ErrorCode.INVALID_REQUEST,
                    "too many tags: a document can have up to " + fessConfig.getUserTagMaxDocumentTagsAsInteger() + " tags");
        }
        return tagHelper.addTag(userId, url, docId, value);
    }

    /**
     * Rebuilds the tag fields of the indexed documents of the URL. The tag log is already updated, so a failure
     * here is logged and the next indexing of the URL brings the documents up to date.
     */
    private Map<String, Long> updateDocuments(final TagHelper tagHelper, final String url) {
        try {
            return tagHelper.updateDocuments(url);
        } catch (final Exception e) {
            logger.warn("Failed to update the tags of the documents: url={}", url, e);
            return tagHelper.getTagCountMap(url);
        }
    }

    private List<Map<String, Object>> buildTags(final TagHelper tagHelper, final Map<String, Long> countMap, final Set<String> typeSet,
            final Set<String> userTagSet, final boolean administrator) {
        final List<Map<String, Object>> tags = new ArrayList<>();
        for (final Map<String, Object> item : tagHelper.toTagItems(countMap.keySet(), typeSet)) {
            final String value = (String) item.get("value");
            final boolean mine = userTagSet.contains(value);
            item.put("count", countMap.get(value));
            item.put("mine", mine);
            item.put("removable", mine || administrator);
            tags.add(item);
        }
        return tags;
    }

    /**
     * Returns the URL of the document, resolved through the caller's roles.
     */
    private String getDocumentUrl(final String docId, final OptionalThing<FessUserBean> userBean) throws TagRequestException {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Map<String, Object> doc = getDocument(docId, new String[] { fessConfig.getIndexFieldUrl() }, userBean);
        final String url = doc == null ? null : DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
        if (StringUtil.isBlank(url)) {
            throw new TagRequestException(V2ErrorCode.NOT_FOUND, "doc not found: " + docId);
        }
        return url;
    }

    /**
     * Fetches a document with the caller's role filter. Exposed as a seam for unit tests.
     *
     * @param docId the document id
     * @param fields the fields to fetch
     * @param userBean the logged-in user
     * @return the document, or null if the caller cannot see it
     */
    protected Map<String, Object> getDocument(final String docId, final String[] fields, final OptionalThing<FessUserBean> userBean) {
        return ComponentUtil.getSearchHelper().getDocumentByDocId(docId, fields, userBean).orElse(null);
    }

    /**
     * Returns the values of the tag label types that the caller can see. Exposed as a seam for unit tests.
     *
     * @param req the request
     * @return the label type values
     */
    protected Set<String> getTagTypeValueSet(final HttpServletRequest req) {
        final Locale locale = req.getLocale() == null ? Locale.ROOT : req.getLocale();
        return ComponentUtil.getLabelTypeHelper().getTagTypeValueSet(SearchRequestType.JSON, locale);
    }

    /**
     * Returns the user of the login session. An access token is not a login session. Exposed as a seam for unit
     * tests.
     *
     * @return the logged-in user
     */
    protected OptionalThing<FessUserBean> getUserBean() {
        return ComponentUtil.getRequestManager().findUserBean(FessUserBean.class);
    }

    /**
     * Returns whether the user can remove the tags of other users: a user with an admin role or the label
     * administration role.
     *
     * @param userBean the logged-in user
     * @return true if the user moderates tags
     */
    protected boolean isTagAdministrator(final OptionalThing<FessUserBean> userBean) {
        final String[] adminRoles = ComponentUtil.getFessConfig().getAuthenticationAdminRolesAsArray();
        return userBean.map(user -> user.hasRoles(adminRoles) || user.hasRole(AdminLabeltypeAction.ROLE)).orElse(false);
    }
}
