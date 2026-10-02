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
import java.util.Collection;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.log.allcommon.EsPagingResultBean;
import org.codelibs.fess.opensearch.log.exbhv.TagLogBhv;
import org.codelibs.fess.opensearch.log.exentity.TagLog;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fesen.opensearch.script.Script;
import org.codelibs.fesen.opensearch.script.ScriptType;
import org.codelibs.fesen.opensearch.search.aggregations.Aggregation;
import org.codelibs.fesen.opensearch.search.aggregations.Aggregations;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.Terms;

/**
 * Helper for the tags that users add to documents.
 *
 * <p>A tag belongs to a label type of the kind {@code tag}, and its value is
 * {@code <label type value>:<tag name>}. The label type decides who can see and add its tags. One record in the
 * tag log is one tag that one user added to the documents of one URL; the log is the source of truth. The
 * {@code tag} and {@code tag_count} fields of the documents are rebuilt from it when a document is indexed and
 * when a tag is added or removed, so that tags survive a re-crawl.</p>
 */
public class TagHelper {
    private static final Logger logger = LogManager.getLogger(TagHelper.class);

    /** The separator between the label type value and the tag name. */
    public static final String SEPARATOR = ":";

    private static final String TAGS_AGGREGATION = "tags";

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    private static final Pattern TYPE_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    /**
     * Default constructor.
     */
    public TagHelper() {
        // do nothing
    }

    /**
     * Returns whether users can add tags.
     *
     * @return true if {@code user.tag.enabled} is true
     */
    public boolean isEnabled() {
        return ComponentUtil.getFessConfig().isUserTagEnabled();
    }

    /**
     * Builds a tag value from a label type value and a tag name.
     *
     * @param type the label type value
     * @param name the normalized tag name
     * @return the tag value
     */
    public String toValue(final String type, final String name) {
        return type + SEPARATOR + name;
    }

    /**
     * Returns the label type value of a tag value.
     *
     * @param value the tag value
     * @return the label type value, or null if the value is not a tag value
     */
    public String getType(final String value) {
        if (value == null) {
            return null;
        }
        final int pos = value.indexOf(SEPARATOR);
        if (pos <= 0 || pos == value.length() - 1) {
            return null;
        }
        final String type = value.substring(0, pos);
        return TYPE_PATTERN.matcher(type).matches() ? type : null;
    }

    /**
     * Returns the tag name of a tag value.
     *
     * @param value the tag value
     * @return the tag name, or null if the value is not a tag value
     */
    public String getName(final String value) {
        if (getType(value) == null) {
            return null;
        }
        return value.substring(value.indexOf(SEPARATOR) + 1);
    }

    /**
     * Normalizes a tag name that a user entered. The name is NFKC-normalized, whitespace is collapsed into one space
     * and the name is trimmed.
     *
     * @param name the entered name
     * @return the normalized name, or null if the name is blank, too long, or has a double quote, a backslash, a
     *         control character or a format character such as a zero-width space or a bidirectional override
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
            // a double quote or a backslash would break the quoted field query that a tag filter builds
            if (c == '"' || c == '\\' || Character.isISOControl(c) || Character.getType(c) == Character.FORMAT) {
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
     * Returns whether a tag value belongs to one of the given label types.
     *
     * @param value the tag value
     * @param typeSet the label type values that the user can see
     * @return true if the user can see the tag
     */
    public boolean isVisible(final String value, final Set<String> typeSet) {
        final String type = getType(value);
        return type != null && typeSet.contains(type);
    }

    /**
     * Converts tag values into maps with the value, the label type value and the tag name. Values whose label type
     * is not in the given set are left out.
     *
     * @param values the tag values
     * @param typeSet the label type values that the user can see
     * @return a list of maps with {@code value}, {@code type} and {@code name}
     */
    public List<Map<String, Object>> toTagItems(final Collection<String> values, final Set<String> typeSet) {
        final List<Map<String, Object>> list = new ArrayList<>();
        if (values == null) {
            return list;
        }
        for (final String value : new LinkedHashSet<>(values)) {
            if (isVisible(value, typeSet)) {
                final Map<String, Object> item = new LinkedHashMap<>();
                item.put("value", value);
                item.put("type", getType(value));
                item.put("name", getName(value));
                list.add(item);
            }
        }
        return list;
    }

    /**
     * Returns the number of users that added each tag to the documents of a URL, most frequent first.
     *
     * @param url the URL
     * @return a map of the tag value to its number of records
     */
    public Map<String, Long> getTagCountMap(final String url) {
        final Map<String, Long> countMap = new LinkedHashMap<>();
        if (StringUtil.isBlank(url)) {
            return countMap;
        }
        final int size = ComponentUtil.getFessConfig().getUserTagMaxDocumentTagsAsInteger();
        final EsPagingResultBean<TagLog> result = (EsPagingResultBean<TagLog>) getTagLogBhv().selectPage(cb -> {
            cb.fetchFirst(0);
            cb.query().setUrl_Term(url);
            cb.aggregation().setTag_Terms(TAGS_AGGREGATION, op -> op.size(size), null);
        });
        final Aggregations aggregations = result.getAggregations();
        if (aggregations != null) {
            final Aggregation aggregation = aggregations.get(TAGS_AGGREGATION);
            if (aggregation instanceof final Terms terms) {
                for (final Terms.Bucket bucket : terms.getBuckets()) {
                    countMap.put(bucket.getKeyAsString(), bucket.getDocCount());
                }
            }
        }
        return countMap;
    }

    /**
     * Returns the tag values that a user added to the documents of a URL.
     *
     * @param user the user name
     * @param url the URL
     * @return the tag values
     */
    public Set<String> getUserTagSet(final String user, final String url) {
        final Set<String> tagSet = new LinkedHashSet<>();
        if (StringUtil.isBlank(user) || StringUtil.isBlank(url)) {
            return tagSet;
        }
        getTagLogBhv().selectList(cb -> {
            cb.query().setUser_Term(user);
            cb.query().setUrl_Term(url);
            cb.query().addOrderBy_CreatedAt_Asc();
            cb.fetchFirst(ComponentUtil.getFessConfig().getUserTagMaxPerDocumentAsInteger());
        }).forEach(log -> tagSet.add(log.getTag()));
        return tagSet;
    }

    /**
     * Records a tag that a user added to the documents of a URL. The record ID is derived from the user, the URL and
     * the tag, so adding the same tag twice keeps one record.
     *
     * @param user the user name
     * @param url the URL
     * @param docId the document ID that the user tagged
     * @param value the tag value
     * @return true if the tag was added, false if the user had already added it
     */
    public boolean addTag(final String user, final String url, final String docId, final String value) {
        final TagLogBhv tagLogBhv = getTagLogBhv();
        final String id = createId(user, url, value);
        if (tagLogBhv.selectByPK(id).isPresent()) {
            return false;
        }
        final TagLog tagLog = new TagLog();
        tagLog.setId(id);
        tagLog.setUser(user);
        tagLog.setUrl(url);
        tagLog.setDocId(docId);
        tagLog.setTag(value);
        tagLog.setCreatedAt(ComponentUtil.getSystemHelper().getCurrentTimeAsLocalDateTime());
        tagLogBhv.insertOrUpdate(tagLog);
        tagLogBhv.refresh();
        return true;
    }

    /**
     * Removes the records of a tag from the documents of a URL.
     *
     * @param user the user whose record is removed, or null to remove the records of all users
     * @param url the URL
     * @param value the tag value
     * @return the number of removed records
     */
    public int removeTag(final String user, final String url, final String value) {
        final TagLogBhv tagLogBhv = getTagLogBhv();
        final int count = tagLogBhv.queryDelete(cb -> {
            cb.query().setUrl_Term(url);
            cb.query().setTag_Term(value);
            if (user != null) {
                cb.query().setUser_Term(user);
            }
        });
        tagLogBhv.refresh();
        return count;
    }

    /**
     * Puts the tags of the URL of a document into the {@code tag} and {@code tag_count} fields of the document.
     * The tags come only from the tag log; a {@code tag} value that a crawler or a data store set is replaced.
     *
     * @param doc the document to be indexed
     */
    public void addTagFields(final Map<String, Object> doc) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Object url = doc.get(fessConfig.getIndexFieldUrl());
        if (url == null) {
            return;
        }
        try {
            final Map<String, Long> countMap = getTagCountMap(url.toString());
            putTagFields(doc, countMap);
            if (logger.isDebugEnabled()) {
                logger.debug("Tags: tags={}, url={}", countMap, url);
            }
        } catch (final Exception e) {
            logger.warn("Failed to load the tags: url={}", url, e);
        }
    }

    /**
     * Puts the given tags into the {@code tag} and {@code tag_count} fields of a document.
     *
     * @param doc the document
     * @param countMap a map of the tag value to its number of records
     */
    protected void putTagFields(final Map<String, Object> doc, final Map<String, Long> countMap) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (countMap.isEmpty()) {
            doc.remove(fessConfig.getIndexFieldTag());
        } else {
            doc.put(fessConfig.getIndexFieldTag(), countMap.keySet().toArray(new String[countMap.size()]));
        }
        doc.put(fessConfig.getIndexFieldTagCount(), sum(countMap));
    }

    /**
     * Rebuilds the {@code tag} and {@code tag_count} fields of every indexed document of a URL from the tag log.
     * A URL can have several documents, for example one per crawl config or set of roles.
     *
     * @param url the URL
     * @return a map of the tag value to its number of records after the update
     */
    public Map<String, Long> updateDocuments(final String url) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Map<String, Long> countMap = getTagCountMap(url);
        final Map<String, Object> params = new HashMap<>();
        params.put("tags", new ArrayList<>(countMap.keySet()));
        params.put("count", sum(countMap));
        final Script script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, "ctx._source." + fessConfig.getIndexFieldTag()
                + "=params.tags;ctx._source." + fessConfig.getIndexFieldTagCount() + "=params.count", params);
        final long updated = ComponentUtil.getSearchEngineClient()
                .updateByQuery(fessConfig.getIndexDocumentUpdateIndex(),
                        option -> option.setQuery(QueryBuilders.termQuery(fessConfig.getIndexFieldUrl(), url))
                                .setFetchSource(new String[] { fessConfig.getIndexFieldUrl() }, null),
                        (builder, hit) -> builder.setScript(script));
        if (logger.isDebugEnabled()) {
            logger.debug("Updated the tags of documents: url={}, documents={}, tags={}", url, updated, countMap);
        }
        return countMap;
    }

    /**
     * Creates the ID of a tag log record.
     *
     * @param user the user name
     * @param url the URL
     * @param value the tag value
     * @return a SHA-256 hex string
     */
    protected String createId(final String user, final String url, final String value) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // NUL never appears in a user name, a URL or a tag value, so the joined string is unambiguous
            final byte[] hash = digest.digest((user + '\0' + url + '\0' + value).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available.", e);
        }
    }

    private static long sum(final Map<String, Long> countMap) {
        return countMap.values().stream().mapToLong(Long::longValue).sum();
    }

    /**
     * Returns the behavior of the tag log.
     *
     * @return the tag log behavior
     */
    protected TagLogBhv getTagLogBhv() {
        return ComponentUtil.getComponent(TagLogBhv.class);
    }
}
