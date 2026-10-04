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

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fesen.opensearch.action.update.UpdateRequest;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fesen.opensearch.script.Script;
import org.codelibs.fesen.opensearch.script.ScriptType;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.TagChange;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.TagTypeCB;
import org.codelibs.fess.opensearch.config.exbhv.TagTypeBhv;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.util.LaRequestUtil;

import jakarta.annotation.Resource;

/**
 * Helper for user tags: the value a tag puts on a document, the tags a caller may see, and the
 * tags of the documents being indexed.
 *
 * <p>A tag is a {@code tag_type} document owned by one user. Its value, {@code b64url(name) + ":"
 * + b64url(owner)}, is what the {@code tag} field of a document holds, and its id is the SHA-256
 * of that value. Indexing runs in the crawler and data store child processes, so this helper
 * reads tag types through {@link TagTypeBhv} only, which {@code esflute_config.xml} registers in
 * every container that boots {@code app.xml}.</p>
 */
public class TagTypeHelper {
    private static final Logger logger = LogManager.getLogger(TagTypeHelper.class);

    /** The prefix of the request attribute caching the visible tag types, followed by the search request type. */
    public static final String VISIBLE_TAG_TYPES_ATTRIBUTE = "fess.tagTypeHelper.visibleTagTypes.";

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    /** How many times an update retries on a version conflict. */
    protected static final int RETRY_ON_CONFLICT = 3;

    /** The TagType behavior. */
    @Resource
    protected TagTypeBhv tagTypeBhv;

    /** The tag changes waiting to be applied to the documents. */
    protected final Queue<TagChange> tagChangeQueue = new ConcurrentLinkedQueue<>();

    /** The number of changes in {@link #tagChangeQueue}. */
    protected final AtomicInteger tagChangeQueueSize = new AtomicInteger();

    /**
     * Default constructor.
     */
    public TagTypeHelper() {
        // do nothing
    }

    /**
     * Encodes a tag name and its owner into a tag value.
     *
     * @param name the normalized tag name
     * @param owner the owner of the tag
     * @return the tag value
     */
    public String toTagValue(final String name, final String owner) {
        return TagType.toTagValue(name, owner);
    }

    /**
     * Decodes a tag value into its name and owner.
     *
     * @param value the tag value
     * @return the name and the owner, or empty when the value is not one {@link #toTagValue(String, String)} returns
     */
    public Optional<Pair<String, String>> parseTagValue(final String value) {
        if (StringUtil.isEmpty(value)) {
            return Optional.empty();
        }
        final int pos = value.indexOf(':');
        if (pos <= 0 || pos == value.length() - 1 || value.indexOf(':', pos + 1) >= 0) {
            return Optional.empty();
        }
        final String name = decodePart(value.substring(0, pos));
        final String owner = decodePart(value.substring(pos + 1));
        if (name == null || owner == null || !value.equals(toTagValue(name, owner))) {
            // not canonical: padding, stray bits or a lossy UTF-8 round trip
            return Optional.empty();
        }
        return Optional.of(new Pair<>(name, owner));
    }

    private String decodePart(final String part) {
        try {
            final byte[] bytes = Base64.getUrlDecoder().decode(part);
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes))
                    .toString();
        } catch (final IllegalArgumentException | CharacterCodingException e) {
            return null;
        }
    }

    /**
     * Returns the id of the tag type of a tag value.
     *
     * @param tagValue the tag value
     * @return the SHA-256 of the value in lowercase hex
     */
    public String toId(final String tagValue) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(tagValue.getBytes(StandardCharsets.UTF_8)));
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available.", e);
        }
    }

    /**
     * Normalizes a tag name: NFKC, whitespace runs collapsed to one space and trimmed. A name
     * that is then empty, longer than {@code user.tag.name.max.length} code points, or holds a
     * control, format or unpaired surrogate character is refused.
     *
     * @param raw the name as entered
     * @return the normalized name, or empty when the name is refused
     */
    public Optional<String> normalizeName(final String raw) {
        if (raw == null) {
            return Optional.empty();
        }
        final String normalized = WHITESPACE_PATTERN.matcher(Normalizer.normalize(raw, Normalizer.Form.NFKC)).replaceAll(" ").trim();
        if (normalized.isEmpty()
                || normalized.codePointCount(0, normalized.length()) > ComponentUtil.getFessConfig().getUserTagNameMaxLengthAsInteger()) {
            return Optional.empty();
        }
        final boolean refused = normalized.codePoints().anyMatch(cp -> {
            final int type = Character.getType(cp);
            return type == Character.CONTROL || type == Character.FORMAT || type == Character.SURROGATE;
        });
        return refused ? Optional.empty() : Optional.of(normalized);
    }

    /**
     * Tests whether a tag is shared, that is, its permissions hold one of the encoded
     * {@code role.search.guest.permissions}. The guest user role is not one of them, so the
     * private tag of a user named {@code guest} is not shared.
     *
     * @param tagType the tag type
     * @return true if every logged-in user may see the tag
     */
    public boolean isShared(final TagType tagType) {
        final String[] permissions = tagType.getPermissions();
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        final List<String> sharedRoleList = getSharedRoleList();
        for (final String permission : permissions) {
            if (sharedRoleList.contains(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Builds the permissions stored on a tag: the user role of the owner and, for a shared tag,
     * the encoded {@code role.search.guest.permissions}. The guest user role that
     * {@link FessConfig#getSearchGuestRoleList()} adds is not stored.
     *
     * @param owner the owner of the tag
     * @param shared whether the tag is shared
     * @return the permissions
     */
    public String[] buildPermissions(final String owner, final boolean shared) {
        final List<String> permissions = new ArrayList<>();
        permissions.add(ComponentUtil.getSystemHelper().getSearchRoleByDirectoryUser(owner));
        if (shared) {
            for (final String role : getSharedRoleList()) {
                if (!permissions.contains(role)) {
                    permissions.add(role);
                }
            }
        }
        return permissions.toArray(String[]::new);
    }

    /**
     * Returns the tag types of the given values that the caller may see, without their paths.
     * Nothing is visible to a caller who is not logged in; an admin search sees every tag.
     * Otherwise a tag is visible when the virtual host of the request is not set or is the tag's,
     * and the caller owns the tag or holds one of its permissions, the sharing roles included.
     * The result of each value is cached in the request.
     *
     * @param values the tag values
     * @param type the search request type
     * @return the visible tag types keyed by their value
     */
    public Map<String, TagType> getVisibleTagTypes(final Collection<String> values, final SearchRequestType type) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyMap();
        }
        final String userId = getLoggedInUserId();
        if (userId == null) {
            return Collections.emptyMap();
        }

        final Map<String, Optional<TagType>> cache = getVisibleTagTypeCache(type);
        final Set<String> missing = new LinkedHashSet<>();
        for (final String value : values) {
            if (value != null && !cache.containsKey(value)) {
                missing.add(value);
            }
        }
        if (!missing.isEmpty()) {
            final Map<String, TagType> fetched = fetchTagTypes(missing);
            final boolean adminSearch = SearchRequestType.ADMIN_SEARCH == type;
            final Set<String> roles = adminSearch ? Collections.emptySet() : getViewerRoles(type);
            final String virtualHostKey = adminSearch ? null : ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
            for (final String value : missing) {
                final TagType tagType = fetched.get(value);
                final boolean visible = tagType != null && (adminSearch || isVisible(tagType, userId, roles, virtualHostKey));
                cache.put(value, visible ? Optional.of(tagType) : Optional.empty());
            }
        }

        final Map<String, TagType> result = new LinkedHashMap<>();
        for (final String value : values) {
            if (value != null) {
                cache.get(value).ifPresent(tagType -> result.put(value, tagType));
            }
        }
        return result;
    }

    /**
     * Returns the values of the tags the caller may see: the caller's own tags and then those
     * of others whose permissions the caller holds, limited to the virtual host of the request and to
     * {@code user.tag.visible.max.size} tags. Empty for a caller who is not logged in.
     *
     * @param type the search request type
     * @return the visible tag values
     */
    public Set<String> getVisibleTagValues(final SearchRequestType type) {
        final String userId = getLoggedInUserId();
        if (userId == null) {
            return Collections.emptySet();
        }
        final boolean adminSearch = SearchRequestType.ADMIN_SEARCH == type;
        final Set<String> roles = adminSearch ? Collections.emptySet() : getViewerRoles(type);
        final String virtualHostKey = adminSearch ? null : ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        final int maxSize = ComponentUtil.getFessConfig().getUserTagVisibleMaxSizeAsInteger();
        final List<TagType> tagTypeList = new ArrayList<>();
        if (adminSearch) {
            tagTypeList.addAll(tagTypeBhv.selectList(cb -> {
                cb.query().matchAll();
                specifyNameAndOwner(cb);
                cb.fetchFirst(maxSize);
            }));
        } else {
            // the caller's own tags first, so that tags of others never push them out
            tagTypeList.addAll(tagTypeBhv.selectList(cb -> {
                cb.query().bool((must, should, mustNot, filter) -> {
                    filter.setOwner_Term(userId);
                    if (StringUtil.isNotBlank(virtualHostKey)) {
                        filter.setVirtualHost_Term(virtualHostKey);
                    }
                });
                specifyNameAndOwner(cb);
                cb.fetchFirst(maxSize);
            }));
            final int remaining = maxSize - tagTypeList.size();
            if (remaining > 0 && !roles.isEmpty()) {
                tagTypeList.addAll(tagTypeBhv.selectList(cb -> {
                    cb.query().bool((must, should, mustNot, filter) -> {
                        filter.setPermissions_InScope(roles);
                        mustNot.setOwner_Term(userId);
                        if (StringUtil.isNotBlank(virtualHostKey)) {
                            filter.setVirtualHost_Term(virtualHostKey);
                        }
                    });
                    specifyNameAndOwner(cb);
                    cb.fetchFirst(remaining);
                }));
            }
        }
        final Set<String> valueSet = new LinkedHashSet<>();
        for (final TagType tagType : tagTypeList) {
            final String value = tagType.getTagValue();
            if (value != null && valueSet.size() < maxSize) {
                valueSet.add(value);
            }
        }
        return valueSet;
    }

    private void specifyNameAndOwner(final TagTypeCB cb) {
        cb.specify().columnName();
        cb.specify().columnOwner();
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();
    }

    /**
     * Finds the tags put on each of the given URLs. A URL matches a tag only when it equals one
     * of the tag's paths. Every matching tag is returned, however many there are.
     *
     * @param urls the URLs of documents
     * @return the tag values of each URL that has a tag
     */
    public Map<String, Set<String>> findTagValuesByUrls(final Collection<String> urls) {
        final Set<String> urlSet = new LinkedHashSet<>();
        if (urls != null) {
            for (final String url : urls) {
                if (StringUtil.isNotEmpty(url)) {
                    urlSet.add(url);
                }
            }
        }
        if (urlSet.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, Set<String>> result = new HashMap<>();
        tagTypeBhv.selectBulk(cb -> {
            cb.query().setPaths_InScope(urlSet);
            cb.specify().columnName();
            cb.specify().columnOwner();
            cb.specify().columnPaths();
        }, list -> {
            for (final TagType tagType : list) {
                final String value = tagType.getTagValue();
                final String[] paths = tagType.getPaths();
                if (value == null || paths == null) {
                    continue;
                }
                for (final String path : paths) {
                    if (urlSet.contains(path)) {
                        result.computeIfAbsent(path, k -> new LinkedHashSet<>()).add(value);
                    }
                }
            }
        });
        return result;
    }

    /**
     * Sets the tag field of documents about to be indexed from the tags put on their URLs, and
     * removes it from a document whose URL has no tag. Does nothing unless
     * {@code user.tag.enabled} is true. When the tags cannot be read, the documents are left as
     * they are.
     *
     * @param docList the documents
     */
    public void applyTags(final List<Map<String, Object>> docList) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isUserTagEnabled() || docList == null || docList.isEmpty()) {
            return;
        }
        final String urlField = fessConfig.getIndexFieldUrl();
        final String tagField = fessConfig.getIndexFieldTag();
        final Set<String> urlSet = new LinkedHashSet<>();
        for (final Map<String, Object> doc : docList) {
            if (doc.get(urlField) instanceof final String url) {
                urlSet.add(url);
            }
        }
        final Map<String, Set<String>> tagMap;
        try {
            tagMap = findTagValuesByUrls(urlSet);
        } catch (final RuntimeException e) {
            logger.warn("Failed to read the user tags of documents. Run tag_updater to restore them: documents={}", docList.size(), e);
            return;
        }
        for (final Map<String, Object> doc : docList) {
            final Set<String> values = doc.get(urlField) instanceof final String url ? tagMap.get(url) : null;
            if (values == null || values.isEmpty()) {
                doc.remove(tagField);
            } else {
                doc.put(tagField, values.toArray(String[]::new));
            }
        }
    }

    /**
     * Queues a tag change to be applied to the documents by {@link #processQueue()}. A change is
     * dropped when {@code user.tag.queue.max.size} changes are already waiting.
     *
     * @param change the tag change
     * @return true if the change was queued
     */
    public boolean enqueue(final TagChange change) {
        if (change == null || change.type() == null) {
            return false;
        }
        final int maxSize = ComponentUtil.getFessConfig().getUserTagQueueMaxSizeAsInteger();
        if (tagChangeQueueSize.incrementAndGet() > maxSize) {
            tagChangeQueueSize.decrementAndGet();
            logger.warn("The tag change queue is full. Run tag_updater to restore the dropped change: type={}, limit={}", change.type(),
                    maxSize);
            return false;
        }
        tagChangeQueue.add(change);
        return true;
    }

    /**
     * Applies the queued tag changes to the documents, in the order they were queued. Additions
     * and removals are gathered per URL as the final state of each value and written in bulk,
     * {@code user.tag.process.batch.size} URLs at a time; a deletion or a rename first writes what
     * was gathered and then updates every document holding the value. A failed write is logged
     * and the remaining changes are still applied.
     *
     * @return the number of changes taken from the queue
     */
    public synchronized int processQueue() {
        final List<TagChange> changeList = new ArrayList<>();
        TagChange change;
        while ((change = tagChangeQueue.poll()) != null) {
            tagChangeQueueSize.decrementAndGet();
            changeList.add(change);
        }
        if (changeList.isEmpty()) {
            return 0;
        }
        final Map<String, Map<String, Boolean>> pendingMap = new LinkedHashMap<>();
        for (final TagChange tagChange : changeList) {
            switch (tagChange.type()) {
            case ADD, REMOVE -> {
                if (StringUtil.isNotEmpty(tagChange.url()) && StringUtil.isNotEmpty(tagChange.value())) {
                    pendingMap.computeIfAbsent(tagChange.url(), k -> new LinkedHashMap<>())
                            .put(tagChange.value(), tagChange.type() == TagChange.Type.ADD);
                }
            }
            case DELETE -> {
                flushTagChanges(pendingMap);
                updateDocumentsWithTag(tagChange.value(), Collections.emptyList(), List.of(tagChange.value()));
            }
            case RENAME -> {
                flushTagChanges(pendingMap);
                if (!StringUtil.equals(tagChange.value(), tagChange.newValue()) && StringUtil.isNotEmpty(tagChange.newValue())) {
                    updateDocumentsWithTag(tagChange.value(), List.of(tagChange.newValue()), List.of(tagChange.value()));
                }
            }
            default -> logger.warn("Unknown tag change: {}", tagChange.type());
            }
        }
        flushTagChanges(pendingMap);
        return changeList.size();
    }

    /**
     * Writes the gathered additions and removals to every document of their URLs, and clears them.
     *
     * @param pendingMap whether each value is to be added (true) or removed (false), keyed by URL
     */
    protected void flushTagChanges(final Map<String, Map<String, Boolean>> pendingMap) {
        if (pendingMap.isEmpty()) {
            return;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int batchSize = Math.max(1, fessConfig.getUserTagProcessBatchSizeAsInteger());
        final List<String> urlList = new ArrayList<>(pendingMap.keySet());
        for (int i = 0; i < urlList.size(); i += batchSize) {
            final List<String> urls = urlList.subList(i, Math.min(i + batchSize, urlList.size()));
            int documents = 0;
            try {
                final List<UpdateRequest> requestList = new ArrayList<>();
                final String index = fessConfig.getIndexDocumentUpdateIndex();
                ComponentUtil.getSearchEngineClient().scrollSearch(index, builder -> {
                    builder.setQuery(QueryBuilders.termsQuery(fessConfig.getIndexFieldUrl(), urls))
                            .setFetchSource(new String[] { fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldLang() }, null)
                            .setSize(batchSize);
                    return true;
                }, doc -> {
                    final String id = DocumentUtil.getValue(doc, fessConfig.getIndexFieldId(), String.class);
                    final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
                    final Map<String, Boolean> valueMap = url == null ? null : pendingMap.get(url);
                    if (id != null && valueMap != null) {
                        final List<String> addList = new ArrayList<>();
                        final List<String> removeList = new ArrayList<>();
                        valueMap.forEach((value, add) -> (add ? addList : removeList).add(value));
                        requestList.add(new UpdateRequest(index, id).script(createUpdateTagScript(doc, addList, removeList))
                                .retryOnConflict(RETRY_ON_CONFLICT));
                    }
                    return true;
                });
                documents = requestList.size();
                if (!requestList.isEmpty()) {
                    ComponentUtil.getSearchHelper().bulkUpdate(builder -> requestList.forEach(builder::add));
                }
            } catch (final Exception e) {
                logger.warn("Failed to update the user tags of documents. Run tag_updater to restore them: urls={}, documents={}",
                        urls.size(), documents, e);
            }
        }
        pendingMap.clear();
    }

    /**
     * Updates every document holding a tag value. A failure is logged.
     *
     * @param value the tag value the documents hold
     * @param addList the values to put on the documents
     * @param removeList the values to take off the documents
     */
    protected void updateDocumentsWithTag(final String value, final List<String> addList, final List<String> removeList) {
        if (StringUtil.isEmpty(value)) {
            return;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        try {
            final long count = ComponentUtil.getSearchEngineClient()
                    .updateByQuery(fessConfig.getIndexDocumentUpdateIndex(),
                            builder -> builder.setQuery(QueryBuilders.termQuery(fessConfig.getIndexFieldTag(), value))
                                    .setFetchSource(new String[] { fessConfig.getIndexFieldLang() }, null),
                            (builder, hit) -> builder.setScript(createUpdateTagScript(hit.getSourceAsMap(), addList, removeList))
                                    .setRetryOnConflict(RETRY_ON_CONFLICT));
            if (logger.isDebugEnabled()) {
                logger.debug("Updated the user tag of documents: added={}, removed={}, documents={}", addList.size(), removeList.size(),
                        count);
            }
        } catch (final Exception e) {
            logger.warn("Failed to update the user tag of documents. Run tag_updater to restore them: added={}, removed={}", addList.size(),
                    removeList.size(), e);
        }
    }

    /**
     * Builds the painless code that puts the values of {@code params.add} on a document and takes
     * those of {@code params.remove} off it. The field may be missing, a single value or a list; a
     * value is never added twice. The two parameters never share a value, so the order of adding
     * and removing does not matter, and the code ends with an expression so that
     * {@link LanguageHelper#createScript(Map, String)} can append its statements: painless has no
     * empty statement to follow a closing brace.
     *
     * @param field the tag field
     * @return the painless code
     */
    protected static String buildUpdateTagScript(final String field) {
        final String f = "ctx._source." + field;
        return "if(" + f + "==null){" + f + "=new ArrayList();}" //
                + "else if(!(" + f + " instanceof List)){" + f + "=new ArrayList([" + f + "]);}" //
                + "for(v in params.add){if(!" + f + ".contains(v)){" + f + ".add(v);}}" //
                + f + ".removeIf(x -> params.remove.contains(x))";
    }

    /**
     * Builds the script that puts values on a document and takes others off it, followed by the
     * statements that keep its language fields.
     *
     * @param doc the document, holding its language
     * @param addList the values to put on the document
     * @param removeList the values to take off the document
     * @return the script
     */
    protected Script createUpdateTagScript(final Map<String, Object> doc, final List<String> addList, final List<String> removeList) {
        final Script script =
                ComponentUtil.getLanguageHelper().createScript(doc, buildUpdateTagScript(ComponentUtil.getFessConfig().getIndexFieldTag()));
        final Map<String, Object> params = new HashMap<>();
        params.put("add", addList);
        params.put("remove", removeList);
        return new Script(ScriptType.INLINE, "painless", script.getIdOrCode(), params);
    }

    /**
     * Reads the tag types of the given values, without their paths.
     *
     * @param values the tag values
     * @return the tag types found, keyed by their value
     */
    protected Map<String, TagType> fetchTagTypes(final Collection<String> values) {
        final Map<String, String> idMap = new LinkedHashMap<>();
        for (final String value : values) {
            if (parseTagValue(value).isPresent()) {
                idMap.put(toId(value), value);
            }
        }
        final Map<String, TagType> result = new HashMap<>();
        if (idMap.isEmpty()) {
            return result;
        }
        final int chunkSize = Math.max(1, ComponentUtil.getFessConfig().getPageTagtypeMaxFetchSizeAsInteger());
        final List<String> idList = new ArrayList<>(idMap.keySet());
        for (int i = 0; i < idList.size(); i += chunkSize) {
            final List<String> ids = idList.subList(i, Math.min(i + chunkSize, idList.size()));
            final List<TagType> tagTypeList = tagTypeBhv.selectList(cb -> {
                cb.query().setId_InScope(ids);
                specifyColumnsWithoutPaths(cb);
                cb.fetchFirst(ids.size());
            });
            for (final TagType tagType : tagTypeList) {
                final String value = tagType.getTagValue();
                if (value != null && values.contains(value)) {
                    result.put(value, tagType);
                }
            }
        }
        return result;
    }

    /**
     * Fetches every field except {@code paths}.
     *
     * @param cb the condition bean
     */
    protected void specifyColumnsWithoutPaths(final TagTypeCB cb) {
        cb.specify().columnName();
        cb.specify().columnOwner();
        cb.specify().columnPermissions();
        cb.specify().columnVirtualHost();
        cb.specify().columnSortOrder();
        cb.specify().columnCreatedBy();
        cb.specify().columnCreatedTime();
        cb.specify().columnUpdatedBy();
        cb.specify().columnUpdatedTime();
    }

    /**
     * Tests whether a tag is visible to a logged-in caller outside an admin search.
     *
     * @param tagType the tag type
     * @param userId the user id of the caller
     * @param roles the roles of the caller, the sharing roles included
     * @param virtualHostKey the virtual host key of the request
     * @return true if the caller may see the tag
     */
    protected boolean isVisible(final TagType tagType, final String userId, final Set<String> roles, final String virtualHostKey) {
        if (StringUtil.isNotBlank(virtualHostKey) && !virtualHostKey.equals(tagType.getVirtualHost())) {
            return false;
        }
        if (userId.equals(tagType.getOwner())) {
            return true;
        }
        final String[] permissions = tagType.getPermissions();
        if (permissions != null) {
            for (final String permission : permissions) {
                if (roles.contains(permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the roles of the caller together with the sharing roles, without the guest user role.
     *
     * @param type the search request type
     * @return the roles
     */
    protected Set<String> getViewerRoles(final SearchRequestType type) {
        final Set<String> roles = new HashSet<>(ComponentUtil.getRoleQueryHelper().build(type));
        roles.addAll(getSharedRoleList());
        // the guest user role is the own permission of a user named guest, not a sharing role
        roles.remove(getGuestUserRole());
        return roles;
    }

    /**
     * Returns the roles that mark a tag as shared: the encoded
     * {@code role.search.guest.permissions}, without the guest user role that
     * {@link FessConfig#getSearchGuestRoleList()} adds.
     *
     * @return the sharing roles
     */
    protected List<String> getSharedRoleList() {
        final String guestUserRole = getGuestUserRole();
        final List<String> list = new ArrayList<>();
        for (final String role : ComponentUtil.getFessConfig().getSearchGuestRoleList()) {
            if (!guestUserRole.equals(role)) {
                list.add(role);
            }
        }
        return list;
    }

    private String getGuestUserRole() {
        return ComponentUtil.getFessConfig().getRoleSearchUserPrefix() + Constants.GUEST_USER;
    }

    /**
     * Returns the cache of visible tag types of the current request, or a map that is dropped
     * after the call when there is no request, as in a job or a child process.
     *
     * @param type the search request type
     * @return the cache, keyed by tag value; an empty optional marks a value that is not visible
     */
    protected Map<String, Optional<TagType>> getVisibleTagTypeCache(final SearchRequestType type) {
        return LaRequestUtil.getOptionalRequest().map(request -> {
            final String name = VISIBLE_TAG_TYPES_ATTRIBUTE + (type == null ? StringUtil.EMPTY : type.name());
            @SuppressWarnings("unchecked")
            Map<String, Optional<TagType>> cache = (Map<String, Optional<TagType>>) request.getAttribute(name);
            if (cache == null) {
                cache = new HashMap<>();
                request.setAttribute(name, cache);
            }
            return cache;
        }).orElseGet(HashMap::new);
    }

    /**
     * Returns the user id of the logged-in caller.
     *
     * @return the user id, or null when the caller is not logged in
     */
    protected String getLoggedInUserId() {
        return getSavedUserBean().map(FessUserBean::getUserId).filter(StringUtil::isNotBlank).orElse(null);
    }

    /**
     * Resolves the logged-in user. A failed lookup counts as a caller who is not logged in.
     *
     * @return the saved user bean, or empty
     */
    protected OptionalThing<FessUserBean> getSavedUserBean() {
        try {
            return ComponentUtil.getFessLoginAssist().getSavedUserBean();
        } catch (final Exception e) {
            logger.debug("Failed to resolve the logged-in user; treating the caller as not logged in.", e);
            return OptionalThing.empty();
        }
    }
}
