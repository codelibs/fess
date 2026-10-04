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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.app.service.TagTypeService;
import org.codelibs.fess.entity.TagChange;
import org.codelibs.fess.exception.TagTypeConflictException;
import org.codelibs.fess.helper.TagTypeHelper;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the tags of the logged-in user: {@code GET/POST /api/v2/tags} and {@code PUT/DELETE /api/v2/tags/{id}}.
 *
 * <ul>
 *   <li>{@code GET /tags} lists the caller's tags as {@code {id, value, name, shared, sort_order, path_count}}.</li>
 *   <li>{@code POST /tags} with {@code {"name": "...", "shared": false}} creates a tag. A tag of the same name of the
 *       caller is a {@link V2ErrorCode#CONFLICT}; a caller has at most {@code user.tag.max.tags} tags.</li>
 *   <li>{@code PUT /tags/{id}} with {@code {"name"?, "shared"?}} renames the tag, which gives it a new id and value
 *       and queues the rename of the value on the documents, and/or changes whether it is shared, which only changes
 *       who sees it.</li>
 *   <li>{@code DELETE /tags/{id}} deletes the tag and queues its removal from the documents.</li>
 * </ul>
 */
public class TagsHandler extends AbstractTagHandler {

    private static final Logger logger = LogManager.getLogger(TagsHandler.class);

    /**
     * Default constructor used by the DI container. The handler holds no per-request state.
     */
    public TagsHandler() {
        // no-op
    }

    /**
     * Processes one {@code /api/v2/tags} or {@code /api/v2/tags/{id}} request.
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @param tagId the tag id of {@code /tags/{id}}, or null for {@code /tags}
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res, final String tagId) throws IOException {
        final String method = req.getMethod() == null ? StringUtil.EMPTY : req.getMethod().toUpperCase(Locale.ROOT);
        final boolean collection = tagId == null;
        if (collection ? !"GET".equals(method) && !"POST".equals(method) : !"PUT".equals(method) && !"DELETE".equals(method)) {
            res.setHeader("Allow", collection ? "GET, POST" : "PUT, DELETE");
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        try {
            final String userId = checkRequest();
            final Map<String, Object> payload = switch (method) {
            case "GET" -> listTags(userId);
            case "POST" -> createTag(req, userId);
            case "PUT" -> updateTag(req, tagId, userId);
            default -> deleteTag(tagId, userId);
            };
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
        } catch (final TagRequestException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(res, e.code, e.getMessage());
        } catch (final Exception e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2/tags " + method);
        }
    }

    private Map<String, Object> listTags(final String userId) {
        final TagTypeService service = getTagTypeService();
        final Map<String, Long> pathCountMap = service.getPathCountMapByOwner(userId);
        final List<Map<String, Object>> tags = new ArrayList<>();
        for (final TagType tagType : service.getTagTypeListByOwner(userId)) {
            final Long pathCount = pathCountMap.get(tagType.getName());
            tags.add(toTag(tagType, pathCount == null ? 0L : pathCount));
        }
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("tags", tags);
        return payload;
    }

    private Map<String, Object> createTag(final HttpServletRequest req, final String userId) throws TagRequestException, IOException {
        final Map<String, Object> body = readBody(req);
        final String name = toTagName(body.get("name"));
        final Boolean shared = toShared(body);
        final TagType tagType = newTagType(name, userId, Boolean.TRUE.equals(shared), new String[0]);
        try {
            getTagTypeService().insert(tagType);
        } catch (final TagTypeConflictException e) {
            throw new TagRequestException(V2ErrorCode.CONFLICT, "a tag with the name already exists");
        }
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("tag", toTag(tagType, 0L));
        return payload;
    }

    private Map<String, Object> updateTag(final HttpServletRequest req, final String tagId, final String userId)
            throws TagRequestException, IOException {
        final Map<String, Object> body = readBody(req);
        final String name = body.get("name") == null ? null : toTagName(body.get("name"));
        final Boolean shared = toShared(body);
        if (name == null && shared == null) {
            throw new TagRequestException(V2ErrorCode.INVALID_REQUEST, "name or shared is required");
        }
        final TagType current = getOwnTagType(tagId, userId);
        final TagType result;
        final boolean renamed = name != null && !name.equals(current.getName());
        if (renamed) {
            result = renameTag(current, name, shared, userId);
        } else if (shared == null) {
            result = current;
        } else {
            result = updateTagType(tagId, userId, tagType -> {
                final String[] permissions = applyShared(tagType.getPermissions(), tagType.getOwner(), shared);
                if (Arrays.equals(permissions, tagType.getPermissions())) {
                    return false;
                }
                tagType.setPermissions(permissions);
                return true;
            });
        }
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("tag", toTag(result, result.getPaths() == null ? 0L : result.getPaths().length));
        payload.put("renamed", renamed);
        return payload;
    }

    /**
     * Renames a tag: the tag of the new name is created with the paths, permissions, virtual host and sort order of
     * the old one, the old one is deleted, and the rename of the value on the documents is queued. When the caller
     * has a tag of the new name already, nothing changes. The old tag is deleted only if it did not change since it
     * was read; otherwise the new tag is removed again and the rename is retried on a fresh read, so that a URL
     * added to the old tag meanwhile is carried over.
     */
    private TagType renameTag(final TagType read, final String name, final Boolean shared, final String userId) throws TagRequestException {
        final TagTypeHelper helper = getTagTypeHelper();
        TagType current = read;
        for (int attempt = 1;; attempt++) {
            if (attempt > 1) {
                current = getOwnTagType(read.getId(), userId);
            }
            final TagType renamed = new TagType();
            renamed.setName(name);
            renamed.setOwner(current.getOwner());
            renamed.setId(helper.toId(renamed.getTagValue()));
            renamed.setPaths(current.getPaths());
            renamed.setPermissions(
                    shared == null ? current.getPermissions() : applyShared(current.getPermissions(), current.getOwner(), shared));
            renamed.setVirtualHost(current.getVirtualHost());
            renamed.setSortOrder(current.getSortOrder());
            renamed.setCreatedBy(current.getCreatedBy());
            renamed.setCreatedTime(current.getCreatedTime());
            renamed.setUpdatedBy(userId);
            renamed.setUpdatedTime(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
            final boolean replaced;
            try {
                replaced = getTagTypeService().replace(current, renamed);
            } catch (final TagTypeConflictException e) {
                throw new TagRequestException(V2ErrorCode.CONFLICT, "a tag with the name already exists");
            }
            if (!replaced) {
                if (attempt >= MAX_UPDATE_ATTEMPTS) {
                    logger.warn("Failed to rename the tag after {} attempts: id={}", attempt, read.getId());
                    throw new TagRequestException(V2ErrorCode.CONFLICT, "the tag was changed concurrently; try again");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("The tag was changed concurrently; retrying the rename: id={}, attempt={}", read.getId(), attempt);
                }
                continue;
            }
            enqueue(TagChange.rename(current.getTagValue(), renamed.getTagValue()));
            return renamed;
        }
    }

    /**
     * Adds the sharing roles to the permissions of a tag, or removes them. The owner's role and any
     * permission an administrator added are kept.
     *
     * @param permissions the current permissions
     * @param owner the owner of the tag
     * @param shared whether the tag is to be shared
     * @return the new permissions
     */
    private String[] applyShared(final String[] permissions, final String owner, final boolean shared) {
        final TagTypeHelper helper = getTagTypeHelper();
        final List<String> ownList = Arrays.asList(helper.buildPermissions(owner, false));
        final List<String> sharingList = new ArrayList<>(Arrays.asList(helper.buildPermissions(owner, true)));
        sharingList.removeAll(ownList);
        final List<String> list = new ArrayList<>();
        ownList.forEach(p -> {
            if (!list.contains(p)) {
                list.add(p);
            }
        });
        if (permissions != null) {
            for (final String p : permissions) {
                if (!list.contains(p) && !sharingList.contains(p)) {
                    list.add(p);
                }
            }
        }
        if (shared) {
            list.addAll(sharingList);
        }
        return list.toArray(String[]::new);
    }

    private Map<String, Object> deleteTag(final String tagId, final String userId) throws TagRequestException {
        for (int attempt = 1;; attempt++) {
            final TagType tagType = getOwnTagType(tagId, userId);
            try {
                getTagTypeService().delete(tagType);
            } catch (final TagTypeConflictException e) {
                if (attempt >= MAX_UPDATE_ATTEMPTS) {
                    logger.warn("Failed to delete the tag after {} attempts: id={}", attempt, tagId, e);
                    throw new TagRequestException(V2ErrorCode.CONFLICT, "the tag was changed concurrently; try again");
                }
                continue;
            }
            enqueue(TagChange.delete(tagType.getTagValue()));
            break;
        }
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", tagId);
        payload.put("deleted", true);
        return payload;
    }

    private Map<String, Object> toTag(final TagType tagType, final long pathCount) {
        final String value = tagType.getTagValue();
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", getTagTypeHelper().toId(value));
        map.put("value", value);
        map.put("name", tagType.getName());
        map.put("shared", getTagTypeHelper().isShared(tagType));
        map.put("sort_order", tagType.getSortOrder() == null ? 0 : tagType.getSortOrder());
        map.put("path_count", pathCount);
        return map;
    }
}
