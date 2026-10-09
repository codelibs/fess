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
import java.util.Arrays;
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
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.entity.TagChange;
import org.codelibs.fess.exception.TagTypeConflictException;
import org.codelibs.fess.helper.TagTypeHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the tags of one document: {@code GET/POST /api/v2/documents/{docId}/tags} and
 * {@code DELETE /api/v2/documents/{docId}/tags/{id}}.
 *
 * <p>A tag is put on the {@code url} of the document: the URL is added to the paths of the tag and the change of the
 * tag field of every document of the URL is queued. The document is resolved with the caller's roles, so a document
 * the caller cannot search is not found.</p>
 *
 * <ul>
 *   <li>{@code GET} lists the tags on the document that the caller can see ({@code tags}) and the caller's own tags
 *       that are not on it ({@code addable}).</li>
 *   <li>{@code POST} with {@code {"id": "..."}} puts the caller's tag on the document; with {@code {"name": "..."}} it
 *       puts the caller's tag of the name on it, creating a private tag when there is none. A tag holds at most
 *       {@code user.tag.max.paths} URLs.</li>
 *   <li>{@code DELETE .../tags/{id}} takes the caller's tag off the document.</li>
 * </ul>
 */
public class DocumentTagsHandler extends AbstractTagHandler {

    private static final Logger logger = LogManager.getLogger(DocumentTagsHandler.class);

    /**
     * Default constructor used by the DI container. The handler holds no per-request state.
     */
    public DocumentTagsHandler() {
        // no-op
    }

    /**
     * Processes one {@code /api/v2/documents/{docId}/tags} or {@code /api/v2/documents/{docId}/tags/{id}} request.
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @param docId the document id from the URL path
     * @param tagId the tag id of {@code .../tags/{id}}, or null for {@code .../tags}
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res, final String docId, final String tagId)
            throws IOException {
        final String method = req.getMethod() == null ? StringUtil.EMPTY : req.getMethod().toUpperCase(Locale.ROOT);
        final boolean collection = tagId == null;
        if (collection ? !"GET".equals(method) && !"POST".equals(method) : !"DELETE".equals(method)) {
            res.setHeader("Allow", collection ? "GET, POST" : "DELETE");
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        if (!ComponentUtil.getV2DocIdValidator().isValid(docId)) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, "invalid doc_id");
            return;
        }
        try {
            final String userId = checkRequest();
            final String url = getDocumentUrl(docId);
            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("doc_id", docId);
            switch (method) {
            case "POST" -> addTag(req, userId, url, payload);
            case "DELETE" -> payload.put("removed", removeTag(tagId, userId, url));
            default -> {
                // GET lists the tags only
            }
            }
            putTags(payload, userId, url);
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
        } catch (final TagRequestException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, e.code, e.getMessage());
        } catch (final Exception e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2/documents/" + docId + "/tags " + method);
        }
    }

    /**
     * Puts the caller's tag of the request body on the URL and adds {@code added} and {@code tag} to the payload.
     */
    private void addTag(final HttpServletRequest req, final String userId, final String url, final Map<String, Object> payload)
            throws TagRequestException, IOException {
        final Map<String, Object> body = readBody(req);
        final String id;
        if (body.get("id") instanceof final String value) {
            id = value;
        } else if (body.get("name") != null) {
            final String name = toTagName(body.get("name"));
            id = getTagTypeHelper().toId(getTagTypeHelper().toTagValue(name, userId));
            if (!getTagTypeService().getTagType(id).isPresent()) {
                final TagType created = createTag(name, userId, url);
                if (created != null) {
                    payload.put("added", true);
                    payload.put("tag", toDocumentTag(created, userId));
                    return;
                }
            }
        } else {
            throw new TagRequestException(V2ErrorCode.INVALID_REQUEST, "name or id is required");
        }
        final boolean[] added = { false };
        final int maxPaths = ComponentUtil.getFessConfig().getUserTagMaxPathsAsInteger();
        final TagType tagType = updateTagType(id, userId, t -> {
            // the modifier runs again on a fresh read when its write lost a race: only the last run counts
            added[0] = false;
            final String[] paths = t.getPaths() == null ? new String[0] : t.getPaths();
            if (Arrays.asList(paths).contains(url)) {
                return false;
            }
            if (paths.length >= maxPaths) {
                throw new TagRequestException(V2ErrorCode.INVALID_REQUEST,
                        "too many documents: a tag can be put on up to " + maxPaths + " documents");
            }
            final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
            newPaths[paths.length] = url;
            t.setPaths(newPaths);
            added[0] = true;
            return true;
        });
        if (added[0]) {
            enqueue(TagChange.add(tagType.getTagValue(), url));
        }
        payload.put("added", added[0]);
        payload.put("tag", toDocumentTag(tagType, userId));
    }

    /**
     * Creates a private tag of the caller on the URL and queues the addition.
     *
     * @return the tag, or null when a concurrent request created the tag of the name first
     */
    private TagType createTag(final String name, final String userId, final String url) throws TagRequestException {
        final TagType tagType = newTagType(name, userId, false, new String[] { url });
        try {
            insertTagType(tagType);
        } catch (final TagTypeConflictException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("The tag was created concurrently; adding the URL to it: id={}", tagType.getId());
            }
            return null;
        }
        enqueue(TagChange.add(tagType.getTagValue(), url));
        return tagType;
    }

    /**
     * Takes the caller's tag off the URL.
     *
     * @return true if the tag was on the URL
     */
    private boolean removeTag(final String tagId, final String userId, final String url) throws TagRequestException {
        final boolean[] removed = { false };
        final TagType tagType = updateTagType(tagId, userId, t -> {
            // the modifier runs again on a fresh read when its write lost a race: only the last run counts
            removed[0] = false;
            final String[] paths = t.getPaths() == null ? new String[0] : t.getPaths();
            final String[] newPaths = Arrays.stream(paths).filter(p -> !url.equals(p)).toArray(String[]::new);
            if (newPaths.length == paths.length) {
                return false;
            }
            t.setPaths(newPaths);
            removed[0] = true;
            return true;
        });
        if (removed[0]) {
            enqueue(TagChange.remove(tagType.getTagValue(), url));
        }
        return removed[0];
    }

    /**
     * Adds {@code tags}, the tags on the URL that the caller can see, and {@code addable}, the caller's tags that are
     * not on the URL, to the payload.
     */
    private void putTags(final Map<String, Object> payload, final String userId, final String url) {
        final TagTypeHelper helper = getTagTypeHelper();
        final Set<String> values = helper.findTagValuesByUrls(List.of(url)).getOrDefault(url, Collections.emptySet());
        final List<Map<String, Object>> tags = new ArrayList<>();
        helper.getVisibleTagTypes(values, SearchRequestType.JSON).values().forEach(tagType -> tags.add(toDocumentTag(tagType, userId)));
        final List<Map<String, Object>> addable = new ArrayList<>();
        for (final TagType tagType : getTagTypeService().getTagTypeListByOwner(userId)) {
            if (!values.contains(tagType.getTagValue())) {
                addable.add(toDocumentTag(tagType, userId));
            }
        }
        payload.put("tags", tags);
        payload.put("addable", addable);
    }

    /**
     * Returns the URL of the document, resolved with the caller's roles.
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
}
