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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.LabelTypeHelper.LabelTypeItem;
import org.codelibs.fess.helper.TagHelper;
import org.codelibs.fess.helper.TagHelper.AddResult;
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
 * <p>A tag is a label type of the kind {@code tag}: its name is the tag name, its included paths list the tagged
 * URLs and its permissions list who can see it.</p>
 *
 * <ul>
 *   <li>{@code GET} lists the tags of the document that the caller can see, and whether the caller added each.</li>
 *   <li>{@code POST} with {@code {"name": "..."}} tags the document for the logged-in user: the URL is added to the
 *       tag of the name and the user to its permissions, and a new tag is visible only to the user.</li>
 *   <li>{@code DELETE ?value=<tag value>} removes the logged-in user from the permissions of the tag. The tag is
 *       deleted when no permission is left.</li>
 * </ul>
 *
 * <p>The document is resolved through the caller's roles, so a document the caller cannot search cannot be
 * tagged. Writes need a logged-in user; an access token does not stand in for one.</p>
 */
public class DocumentTagsHandler {

    private static final Logger logger = LogManager.getLogger(DocumentTagsHandler.class);

    /** The request body has one short string. */
    private static final int MAX_BODY_BYTES = 1024;

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
            final String userId = getUserBean().map(FessUserBean::getUserId).orElse(null);
            if (!"GET".equals(method) && StringUtil.isBlank(userId)) {
                throw new TagRequestException(V2ErrorCode.AUTH_REQUIRED, "login required");
            }
            final String url = getDocumentUrl(docId);

            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("doc_id", docId);
            switch (method) {
            case "POST" -> payload.put("added", addTag(req, tagHelper, userId, url));
            case "DELETE" -> payload.put("removed", removeTag(req, tagHelper, userId));
            default -> {
                // GET lists the tags only
            }
            }
            payload.put("addable", StringUtil.isNotBlank(userId));
            payload.put("tags", buildTags(req, tagHelper, userId, url));
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
        } catch (final TagRequestException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, e.code, e.getMessage());
        } catch (final Exception e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, context);
        }
    }

    /**
     * Validates the request body of a POST and tags the URL.
     *
     * @return true if the URL was tagged, false if the user had already tagged it
     */
    private boolean addTag(final HttpServletRequest req, final TagHelper tagHelper, final String userId, final String url)
            throws TagRequestException, IOException {
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
        final Object name = body.get("name");
        final String normalizedName = name instanceof final String nameValue ? tagHelper.normalizeName(nameValue) : null;
        if (normalizedName == null) {
            throw new TagRequestException(V2ErrorCode.INVALID_REQUEST,
                    "invalid tag name: enter 1 to " + tagHelper.getNameMaxLength() + " characters");
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final AddResult result = tagHelper.addTag(userId, url, normalizedName);
        switch (result) {
        case TOO_MANY_TAGS -> throw new TagRequestException(V2ErrorCode.INVALID_REQUEST,
                "too many tags: a document can have up to " + fessConfig.getUserTagMaxDocumentTagsAsInteger() + " tags");
        case TOO_MANY_LABELS -> throw new TagRequestException(V2ErrorCode.INVALID_REQUEST,
                "no more tags can be created: the number of labels reached page.labeltype.max.fetch.size");
        case ALREADY_ADDED -> {
            return false;
        }
        default -> {
            updateDocuments(() -> tagHelper.addTagToDocuments(url, tagHelper.toValue(normalizedName)), url);
            return true;
        }
        }
    }

    /**
     * Removes the user from the permissions of the tag of the DELETE request.
     *
     * @return true if the user was removed
     */
    private boolean removeTag(final HttpServletRequest req, final TagHelper tagHelper, final String userId) throws TagRequestException {
        final String value = req.getParameter("value");
        final LabelTypeItem item = StringUtil.isBlank(value) ? null
                : getTagItemList(req).stream().filter(i -> value.equals(i.getValue())).findFirst().orElse(null);
        if (item == null) {
            throw new TagRequestException(V2ErrorCode.INVALID_REQUEST, "invalid tag value");
        }
        if (!tagHelper.isMine(item, userId)) {
            throw new TagRequestException(V2ErrorCode.FORBIDDEN, "the tag was not added by the user");
        }
        if (tagHelper.removeTag(userId, value)) {
            updateDocuments(() -> tagHelper.removeTagFromDocuments(value), value);
        }
        return true;
    }

    /**
     * Updates the tag field of the indexed documents. The label type is already stored, so a failure here is
     * logged and the next crawl or the label updater job brings the documents up to date.
     */
    private void updateDocuments(final Runnable update, final String target) {
        try {
            update.run();
        } catch (final Exception e) {
            logger.warn("Failed to update the tag field of the documents: target={}", target, e);
        }
    }

    private List<Map<String, Object>> buildTags(final HttpServletRequest req, final TagHelper tagHelper, final String userId,
            final String url) {
        final List<Map<String, Object>> tags = new ArrayList<>();
        for (final LabelTypeItem item : getTagItemList(req)) {
            if (item.getUrlSet().contains(url)) {
                final Map<String, Object> tag = new LinkedHashMap<>();
                tag.put("value", item.getValue());
                tag.put("name", item.getLabel());
                tag.put("mine", tagHelper.isMine(item, userId));
                tags.add(tag);
            }
        }
        return tags;
    }

    /**
     * Returns the URL of the document, resolved through the caller's roles.
     */
    private String getDocumentUrl(final String docId) throws TagRequestException {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Map<String, Object> doc = getDocument(docId, new String[] { fessConfig.getIndexFieldUrl() });
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
     * @return the document, or null if the caller cannot see it
     */
    protected Map<String, Object> getDocument(final String docId, final String[] fields) {
        return ComponentUtil.getSearchHelper().getDocumentByDocId(docId, fields, getUserBean()).orElse(null);
    }

    /**
     * Returns the tags that the caller can see. Exposed as a seam for unit tests.
     *
     * @param req the request
     * @return the visible tags
     */
    protected List<LabelTypeItem> getTagItemList(final HttpServletRequest req) {
        final Locale locale = req.getLocale() == null ? Locale.ROOT : req.getLocale();
        return ComponentUtil.getLabelTypeHelper().getTagItemList(SearchRequestType.JSON, locale);
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
}
