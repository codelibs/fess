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
package org.codelibs.fess.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fesen.opensearch.action.update.UpdateRequest;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fesen.opensearch.script.Script;
import org.codelibs.fesen.opensearch.script.ScriptType;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;

/**
 * Job that rebuilds the user tags of every document from the tag types, the way
 * {@link UpdateLabelJob} rebuilds labels. It recovers tag changes lost from the in-memory queue,
 * for example on a restart, and tags lost while {@code user.tag.enabled} was false.
 *
 * <p>Every document is walked, because a document that holds a stale tag and one whose URL gained
 * a tag must both be found; the tags of each page of {@code user.tag.process.batch.size}
 * documents are read with one query, and only the documents whose tags differ are written.</p>
 */
public class TagUpdaterJob {

    private static final Logger logger = LogManager.getLogger(TagUpdaterJob.class);

    /** How many times an update retries on a version conflict. */
    protected static final int RETRY_ON_CONFLICT = 3;

    /** The number of documents walked. */
    protected long checked;

    /** The number of documents whose tags were written. */
    protected long updated;

    /** The number of documents whose tags could not be written. */
    protected long failed;

    /**
     * Default constructor.
     */
    public TagUpdaterJob() {
        // do nothing
    }

    /**
     * Rebuilds the user tags of every document.
     *
     * @return the result message
     */
    public String execute() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isUserTagEnabled()) {
            return "User tags are disabled.\n";
        }
        final int batchSize = Math.max(1, fessConfig.getUserTagProcessBatchSizeAsInteger());
        final String index = fessConfig.getIndexDocumentUpdateIndex();
        final StringBuilder resultBuf = new StringBuilder();
        try {
            final List<Map<String, Object>> docList = new ArrayList<>();
            ComponentUtil.getSearchEngineClient().scrollSearch(index, builder -> {
                builder.setQuery(QueryBuilders.matchAllQuery())
                        .setFetchSource(new String[] { fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldLang(),
                                fessConfig.getIndexFieldTag() }, null)
                        .setSize(batchSize);
                return true;
            }, doc -> {
                docList.add(doc);
                if (docList.size() >= batchSize) {
                    updateTags(index, docList);
                    docList.clear();
                }
                return true;
            });
            updateTags(index, docList);
        } catch (final Exception e) {
            logger.warn("Could not update user tags.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }
        resultBuf.append(updated)
                .append(" documents updated (")
                .append(checked)
                .append(" documents checked, ")
                .append(failed)
                .append(" failed)\n");
        return resultBuf.toString();
    }

    /**
     * Writes the tags of the documents of one page whose tags differ from those of their URLs.
     *
     * @param index the index to write
     * @param docList the documents, holding their id, URL, language and tags
     */
    protected void updateTags(final String index, final List<Map<String, Object>> docList) {
        if (docList.isEmpty()) {
            return;
        }
        checked += docList.size();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String urlField = fessConfig.getIndexFieldUrl();
        final String tagField = fessConfig.getIndexFieldTag();
        final Set<String> urlSet = new LinkedHashSet<>();
        for (final Map<String, Object> doc : docList) {
            final String url = DocumentUtil.getValue(doc, urlField, String.class);
            if (url != null) {
                urlSet.add(url);
            }
        }
        final List<UpdateRequest> requestList = new ArrayList<>();
        try {
            final Map<String, Set<String>> tagMap = ComponentUtil.getTagTypeHelper().findTagValuesByUrls(urlSet);
            for (final Map<String, Object> doc : docList) {
                final String id = DocumentUtil.getValue(doc, fessConfig.getIndexFieldId(), String.class);
                if (id == null) {
                    continue;
                }
                final String url = DocumentUtil.getValue(doc, urlField, String.class);
                final Set<String> values = url == null ? null : tagMap.get(url);
                final Set<String> expected = values == null ? Collections.emptySet() : values;
                if (expected.equals(toValueSet(doc.get(tagField)))) {
                    continue;
                }
                final Script script;
                if (expected.isEmpty()) {
                    script = ComponentUtil.getLanguageHelper().createScript(doc, "ctx._source.remove('" + tagField + "')");
                } else {
                    final Script base = ComponentUtil.getLanguageHelper().createScript(doc, "ctx._source." + tagField + "=params.tag");
                    final Map<String, Object> params = new HashMap<>();
                    params.put("tag", new ArrayList<>(expected));
                    script = new Script(ScriptType.INLINE, "painless", base.getIdOrCode(), params);
                }
                requestList.add(new UpdateRequest(index, id).script(script).retryOnConflict(RETRY_ON_CONFLICT));
            }
            if (!requestList.isEmpty()) {
                ComponentUtil.getSearchHelper().bulkUpdate(builder -> requestList.forEach(builder::add));
                updated += requestList.size();
            }
        } catch (final Exception e) {
            failed += requestList.isEmpty() ? docList.size() : requestList.size();
            logger.warn("Failed to update the user tags of documents: documents={}, updates={}", docList.size(), requestList.size(), e);
        }
    }

    /**
     * Reads the tag field of a document, which may be missing, a single value or a list.
     *
     * @param value the field value
     * @return the tag values
     */
    protected Set<String> toValueSet(final Object value) {
        final Set<String> valueSet = new LinkedHashSet<>();
        if (value instanceof final Collection<?> collection) {
            for (final Object v : collection) {
                if (v != null) {
                    valueSet.add(v.toString());
                }
            }
        } else if (value instanceof final Object[] array) {
            for (final Object v : array) {
                if (v != null) {
                    valueSet.add(v.toString());
                }
            }
        } else if (value != null) {
            valueSet.add(value.toString());
        }
        return valueSet;
    }
}
