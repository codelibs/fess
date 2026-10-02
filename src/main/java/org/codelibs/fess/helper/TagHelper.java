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
package org.codelibs.fess.helper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exentity.LabelType;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fesen.opensearch.script.Script;
import org.codelibs.fesen.opensearch.script.ScriptType;

/**
 * Helper for the tags that users add to documents.
 *
 * <p>A tag is a label type of the kind {@code tag}: its name is the tag name, its value is derived from the name, its
 * included paths list the tagged URLs (one per line, matched exactly) and its permissions list who can see it. A user
 * who adds a tag is added to its permissions, and a user in its permissions can remove it from themselves; the label
 * type is deleted when no permission is left. Administrators manage tags on the label admin screen like any label.</p>
 *
 * <p>The values of the tags of a URL are indexed in the {@code tag} field, by the crawler from the label types and
 * immediately by an update of the indexed documents when a user adds or removes a tag.</p>
 */
public class TagHelper {
    private static final Logger logger = LogManager.getLogger(TagHelper.class);

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    /** The number of attempts to store a tag that another request changed at the same time. */
    private static final int MAX_STORE_ATTEMPTS = 3;

    /** The result of adding a tag. */
    public enum AddResult {
        /** The URL was tagged or the user was added to the tag. */
        ADDED,
        /** The user had already tagged the URL. */
        ALREADY_ADDED,
        /** The document has the maximum number of tags. */
        TOO_MANY_TAGS,
        /** No more label types can be loaded. */
        TOO_MANY_LABELS
    }

    /**
     * Default constructor.
     */
    public TagHelper() {
        // do nothing
    }

    /**
     * Returns whether users can tag documents.
     *
     * @return true if {@code user.tag.enabled} is true
     */
    public boolean isEnabled() {
        return ComponentUtil.getFessConfig().isUserTagEnabled();
    }

    /**
     * Returns the label type value of a tag name: the SHA-256 of the name in hex. It satisfies the label value rule
     * ({@code [a-zA-Z0-9_]}, at most 100 characters) for any name, and the same name always gets the same value.
     *
     * @param name the normalized tag name
     * @return the tag value
     */
    public String toValue(final String name) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(name.getBytes(StandardCharsets.UTF_8)));
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available.", e);
        }
    }

    /**
     * Normalizes a tag name that a user entered. The name is NFKC-normalized, whitespace is collapsed into one space
     * and the name is trimmed.
     *
     * @param name the entered name
     * @return the normalized name, or null if the name is blank, too long, or has a control character or a format
     *         character such as a zero-width space or a bidirectional override
     */
    public String normalizeName(final String name) {
        if (name == null) {
            return null;
        }
        final String normalized = WHITESPACE_PATTERN.matcher(Normalizer.normalize(name, Normalizer.Form.NFKC)).replaceAll(" ").trim();
        if (normalized.isEmpty() || normalized.codePointCount(0, normalized.length()) > getNameMaxLength()) {
            return null;
        }
        for (int i = 0; i < normalized.length(); i++) {
            final char c = normalized.charAt(i);
            if (Character.isISOControl(c) || Character.getType(c) == Character.FORMAT) {
                return null;
            }
        }
        return normalized;
    }

    /**
     * Returns the maximum length of a tag name.
     *
     * @return the maximum number of characters
     */
    public int getNameMaxLength() {
        return ComponentUtil.getFessConfig().getUserTagNameMaxLengthAsInteger();
    }

    /**
     * Returns the permission value of a user, such as {@code 1alice}.
     *
     * @param userId the user name
     * @return the encoded permission
     */
    public String toUserPermission(final String userId) {
        return ComponentUtil.getFessConfig().getRoleSearchUserPrefix() + userId;
    }

    /**
     * Tags the documents of a URL for a user. When a tag of the name exists, the URL is added to its included paths
     * and the user to its permissions; otherwise a tag is created that only the user can see.
     *
     * @param userId the user name
     * @param url the URL of the document
     * @param name the normalized tag name
     * @return the result
     */
    public synchronized AddResult addTag(final String userId, final String url, final String name) {
        final String value = toValue(name);
        final String permission = toUserPermission(userId);
        final LabelTypeService labelTypeService = getLabelTypeService();
        for (int attempt = 1;; attempt++) {
            final LabelType labelType = labelTypeService.getLabelTypeByValue(value).orElse(null);
            final AddResult result;
            final LabelType entity;
            if (labelType == null) {
                if (labelTypeService.getLabelTypeList().size() >= ComponentUtil.getFessConfig().getPageLabeltypeMaxFetchSizeAsInteger()) {
                    return AddResult.TOO_MANY_LABELS;
                }
                if (countTags(url) >= getMaxDocumentTags()) {
                    return AddResult.TOO_MANY_TAGS;
                }
                entity = createTag(userId, url, name, value, permission);
                result = AddResult.ADDED;
            } else {
                if (!labelType.isTagKind()) {
                    throw new IllegalStateException("The value of a tag is used by a label: " + value);
                }
                final Set<String> urlSet = LabelTypeHelper.toUrlSet(labelType.getIncludedPaths());
                final List<String> permissions = toList(labelType.getPermissions());
                if (urlSet.contains(url) && permissions.contains(permission)) {
                    return AddResult.ALREADY_ADDED;
                }
                if (!urlSet.contains(url) && countTags(url) >= getMaxDocumentTags()) {
                    return AddResult.TOO_MANY_TAGS;
                }
                urlSet.add(url);
                if (!permissions.contains(permission)) {
                    permissions.add(permission);
                }
                labelType.setIncludedPaths(String.join("\n", urlSet));
                labelType.setPermissions(permissions.toArray(new String[permissions.size()]));
                labelType.setUpdatedBy(userId);
                labelType.setUpdatedTime(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
                entity = labelType;
                result = AddResult.ADDED;
            }
            try {
                labelTypeService.store(entity);
                return result;
            } catch (final RuntimeException e) {
                // another request changed the same tag; read it again
                if (attempt >= MAX_STORE_ATTEMPTS) {
                    throw e;
                }
                logger.debug("Retrying to store the tag: value={}, attempt={}", value, attempt, e);
            }
        }
    }

    /**
     * Removes a user from the permissions of a tag. The tag is deleted when no permission, of a user, a group or a
     * role, is left.
     *
     * @param userId the user name
     * @param value the tag value
     * @return true if the tag was deleted, false if it was kept for its other permissions
     */
    public synchronized boolean removeTag(final String userId, final String value) {
        final String permission = toUserPermission(userId);
        final LabelTypeService labelTypeService = getLabelTypeService();
        for (int attempt = 1;; attempt++) {
            final LabelType labelType = labelTypeService.getLabelTypeByValue(value).orElse(null);
            if (labelType == null || !labelType.isTagKind()) {
                return false;
            }
            final List<String> permissions = toList(labelType.getPermissions());
            if (!permissions.remove(permission)) {
                return false;
            }
            try {
                if (permissions.isEmpty()) {
                    labelTypeService.delete(labelType);
                    return true;
                }
                labelType.setPermissions(permissions.toArray(new String[permissions.size()]));
                labelType.setUpdatedBy(userId);
                labelType.setUpdatedTime(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
                labelTypeService.store(labelType);
                return false;
            } catch (final RuntimeException e) {
                if (attempt >= MAX_STORE_ATTEMPTS) {
                    throw e;
                }
                logger.debug("Retrying to remove the user from the tag: value={}, attempt={}", value, attempt, e);
            }
        }
    }

    /**
     * Returns whether a user is in the permissions of a tag.
     *
     * @param item the tag
     * @param userId the user name, or null for an anonymous user
     * @return true if the user added the tag
     */
    public boolean isMine(final LabelTypeHelper.LabelTypeItem item, final String userId) {
        return StringUtil.isNotBlank(userId) && toList(item.getPermissions()).contains(toUserPermission(userId));
    }

    /**
     * Adds a tag value to the {@code tag} field of every indexed document of a URL.
     *
     * @param url the URL
     * @param value the tag value
     */
    public void addTagToDocuments(final String url, final String value) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String field = fessConfig.getIndexFieldTag();
        updateDocuments(QueryBuilders.termQuery(fessConfig.getIndexFieldUrl(), url),
                "if (ctx._source." + field + " == null) { ctx._source." + field + " = []; } else if (!(ctx._source." + field
                        + " instanceof List)) { ctx._source." + field + " = [ctx._source." + field + "]; } if (!ctx._source." + field
                        + ".contains(params.value)) { ctx._source." + field + ".add(params.value); }",
                value);
    }

    /**
     * Removes a tag value from the {@code tag} field of every indexed document.
     *
     * @param value the tag value
     */
    public void removeTagFromDocuments(final String value) {
        final String field = ComponentUtil.getFessConfig().getIndexFieldTag();
        updateDocuments(QueryBuilders.termQuery(field, value),
                "if (ctx._source." + field + " instanceof List) { ctx._source." + field
                        + ".removeIf(v -> v == params.value); } else if (ctx._source." + field + " == params.value) { ctx._source.remove('"
                        + field + "'); }",
                value);
    }

    private void updateDocuments(final org.codelibs.fesen.opensearch.index.query.QueryBuilder query, final String code,
            final String value) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Script script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, code, Map.of("value", value));
        final long updated = ComponentUtil.getSearchEngineClient()
                .updateByQuery(fessConfig.getIndexDocumentUpdateIndex(),
                        option -> option.setQuery(query).setFetchSource(new String[] { fessConfig.getIndexFieldUrl() }, null),
                        (builder, hit) -> builder.setScript(script));
        if (logger.isDebugEnabled()) {
            logger.debug("Updated the tag of documents: value={}, documents={}", value, updated);
        }
    }

    /**
     * Creates a tag that only its creator can see.
     */
    protected LabelType createTag(final String userId, final String url, final String name, final String value, final String permission) {
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        final LabelType labelType = new LabelType();
        labelType.setName(name);
        labelType.setValue(value);
        labelType.setKind(LabelType.KIND_TAG);
        labelType.setIncludedPaths(url);
        labelType.setPermissions(new String[] { permission });
        // a label with a virtual host is shown only on that host, and one without only when no virtual host matches
        final String virtualHostKey = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        labelType.setVirtualHost(StringUtil.isBlank(virtualHostKey) ? StringUtil.EMPTY : virtualHostKey);
        labelType.setSortOrder(0);
        labelType.setCreatedBy(userId);
        labelType.setCreatedTime(now);
        labelType.setUpdatedBy(userId);
        labelType.setUpdatedTime(now);
        return labelType;
    }

    /**
     * Returns the number of tags of a URL.
     */
    protected int countTags(final String url) {
        return ComponentUtil.getLabelTypeHelper().getMatchedTagValueSet(url).size();
    }

    /**
     * Returns the maximum number of tags on one document.
     */
    protected int getMaxDocumentTags() {
        return ComponentUtil.getFessConfig().getUserTagMaxDocumentTagsAsInteger();
    }

    /**
     * Returns the label type service.
     *
     * @return the service
     */
    protected LabelTypeService getLabelTypeService() {
        return ComponentUtil.getComponent(LabelTypeService.class);
    }

    private static List<String> toList(final String[] values) {
        final List<String> list = new ArrayList<>();
        if (values != null) {
            Collections.addAll(list, values);
        }
        return new ArrayList<>(new LinkedHashSet<>(list));
    }
}
