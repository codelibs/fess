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
package org.codelibs.fess.app.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.DocumentReport;
import org.codelibs.fess.entity.DocumentReport.DuplicateGroup;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.taglib.FessFunctions;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.CsvUtil;
import org.codelibs.fesen.opensearch.action.search.SearchResponse;
import org.codelibs.fesen.opensearch.index.query.BoolQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fesen.opensearch.search.SearchHit;
import org.codelibs.fesen.opensearch.search.aggregations.AggregationBuilders;
import org.codelibs.fesen.opensearch.search.aggregations.BucketOrder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.composite.CompositeAggregation;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.composite.CompositeAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.composite.TermsValuesSourceBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.Terms;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.Sum;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.TopHits;
import org.codelibs.fesen.opensearch.search.sort.SortBuilder;
import org.codelibs.fesen.opensearch.search.sort.SortBuilders;
import org.codelibs.fesen.opensearch.search.sort.SortOrder;

import com.orangesignal.csv.CsvWriter;

import jakarta.annotation.Resource;

/**
 * Builds the document report of the admin screen: groups of documents with the same content and
 * documents that have not been modified for a long time, for cleaning up the crawled file servers.
 *
 * <p>Documents with the same content are found by the content signature
 * ({@code content_minhash_bits}) that the search engine computes from the content at index time, the
 * same signature the search result collapsing uses. Two documents fall into the same group only when
 * their whole signature is equal, that is, when their content is the same or nearly the same.</p>
 */
public class DocumentReportService {

    private static final Logger logger = LogManager.getLogger(DocumentReportService.class);

    /** Key of the URL of a report document. */
    public static final String DOC_URL = "url";

    /** Key of the title of a report document. */
    public static final String DOC_TITLE = "title";

    /** Key of the file name of a report document. */
    public static final String DOC_FILENAME = "filename";

    /** Key of the last modification date of a report document, a {@link java.util.Date}. */
    public static final String DOC_LAST_MODIFIED = "lastModified";

    /** Key of the content length of a report document. */
    public static final String DOC_CONTENT_LENGTH = "contentLength";

    /** Key of the file owner of a report document. */
    public static final String DOC_OWNER = "owner";

    /** Key of the last modifier of a report document. */
    public static final String DOC_LAST_MODIFIER = "lastModifier";

    /** Key of the click count of a report document. */
    public static final String DOC_CLICK_COUNT = "clickCount";

    /** Key of the document ID of a report document. */
    public static final String DOC_DOC_ID = "docId";

    /** Key of the content signature of a report document; only set while the duplicates are exported. */
    protected static final String DOC_HASH = "hash";

    /** The largest number of days the dormant report accepts. */
    public static final int MAX_DORMANT_DAYS = 36500;

    /** Header of the duplicate report CSV. */
    protected static final List<String> DUPLICATE_CSV_COLUMNS = List.of("group", "groupSize", DOC_URL, DOC_TITLE, DOC_FILENAME,
            DOC_CONTENT_LENGTH, DOC_LAST_MODIFIED, DOC_OWNER, DOC_LAST_MODIFIER, DOC_CLICK_COUNT, DOC_DOC_ID);

    /** Header of the dormant report CSV. */
    protected static final List<String> DORMANT_CSV_COLUMNS = List.of(DOC_URL, DOC_TITLE, DOC_FILENAME, DOC_CONTENT_LENGTH,
            DOC_LAST_MODIFIED, DOC_OWNER, DOC_LAST_MODIFIER, DOC_CLICK_COUNT, DOC_DOC_ID);

    private static final String GROUP_AGG = "duplicate_groups";

    private static final String DOCS_AGG = "duplicate_docs";

    private static final String SIGNATURE_AGG = "duplicate_signatures";

    private static final String SIGNATURE_KEY = "hash";

    private static final String SIZE_AGG = "total_size";

    private static final long DAY_IN_MILLIS = 24L * 60 * 60 * 1000;

    /** Number of documents read per request while a report is downloaded. */
    protected int scrollSize = 1000;

    /** Client of the search engine. */
    @Resource
    protected SearchEngineClient searchEngineClient;

    /** Fess configuration. */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Default constructor.
     */
    public DocumentReportService() {
        // nothing
    }

    /**
     * Returns whether the index has the content signature the duplicate report needs. The index
     * mappings for the plugin-less search engine types (vanilla and aws) do not compute it.
     *
     * @return true when duplicates can be reported
     */
    public boolean isDuplicateReportAvailable() {
        return !fessConfig.isFesenPluginless();
    }

    /**
     * Lists the largest groups of documents with the same content, largest first. The number of
     * groups and of documents listed per group are limited by {@code docreport.duplicate.group.size}
     * and {@code docreport.duplicate.docs.size}. Never throws: on failure the report is empty and
     * marked as failed.
     *
     * <p>The groups are counted per shard first, so a group whose documents are spread one per shard
     * can be missing from this list; {@link #exportDuplicateCsv(String, Writer)} reads every group.</p>
     *
     * @param urlPrefix only documents whose URL starts with this prefix, or blank for all documents
     * @return the report
     */
    public DocumentReport getDuplicateReport(final String urlPrefix) {
        final DocumentReport report = new DocumentReport();
        if (!isDuplicateReportAvailable()) {
            report.setUnavailable(true);
            return report;
        }
        try {
            report.getGroups()
                    .addAll(searchDuplicateGroups(buildDuplicateQuery(urlPrefix), fessConfig.getDocreportDuplicateGroupSizeAsInteger(),
                            fessConfig.getDocreportDuplicateDocsSizeAsInteger()));
        } catch (final Exception e) {
            logger.warn("Failed to build the duplicate document report.", e);
            report.getGroups().clear();
            report.setFailed(true);
        }
        return report;
    }

    /**
     * Writes every group of documents with the same content as CSV: a header row followed by one row
     * per document, ordered by group. All the content signatures are walked page by page, so no group
     * is missed. The writer is flushed but not closed.
     *
     * @param urlPrefix only documents whose URL starts with this prefix, or blank for all documents
     * @param writer the writer to write the CSV to
     * @throws IOException if writing fails
     */
    public void exportDuplicateCsv(final String urlPrefix, final Writer writer) throws IOException {
        // not closed: that would close the writer of the caller
        @SuppressWarnings("resource")
        final CsvWriter csvWriter = new CsvWriter(writer, CsvUtil.createCsvConfig());
        csvWriter.writeValues(DUPLICATE_CSV_COLUMNS);
        if (isDuplicateReportAvailable()) {
            final QueryBuilder baseQuery = buildDuplicateQuery(urlPrefix);
            final String hashField = fessConfig.getIndexFieldContentMinhashBits();
            final long[] groupNumber = { 0 };
            final String[] currentHash = { null };
            try {
                scanDuplicateSignatures(baseQuery, fessConfig.getDocreportDuplicateExportPageSizeAsInteger(), counts -> {
                    final QueryBuilder query =
                            QueryBuilders.boolQuery().filter(baseQuery).filter(QueryBuilders.termsQuery(hashField, counts.keySet()));
                    scanDocuments(query, List.of(SortBuilders.fieldSort(hashField).order(SortOrder.ASC), urlSort()), true, doc -> {
                        final String hash = (String) doc.get(DOC_HASH);
                        if (hash == null || !counts.containsKey(hash)) {
                            return;
                        }
                        if (!hash.equals(currentHash[0])) {
                            currentHash[0] = hash;
                            groupNumber[0]++;
                        }
                        writeDocument(csvWriter, doc, groupNumber[0], counts.get(hash));
                    });
                });
            } catch (final UncheckedIOException e) {
                // thrown by the cursor handler, which cannot throw a checked exception
                throw e.getCause();
            }
        }
        csvWriter.flush();
    }

    /**
     * Lists one page of the documents that have not been modified for the given number of days,
     * oldest first, with their number and their total content length. Documents without a last
     * modification date are not dormant. Never throws: on failure the report is empty and marked as
     * failed.
     *
     * @param urlPrefix only documents whose URL starts with this prefix, or blank for all documents
     * @param days the number of days since the last modification
     * @param unclicked true to keep only the documents that have never been opened from a search result
     * @param offset the index of the first document of the page
     * @param size the number of documents of the page
     * @return the report
     */
    public DocumentReport getDormantReport(final String urlPrefix, final int days, final boolean unclicked, final int offset,
            final int size) {
        final DocumentReport report = new DocumentReport();
        try {
            final QueryBuilder query = buildDormantQuery(urlPrefix, days, unclicked);
            searchEngineClient.search(fessConfig.getIndexDocumentSearchIndex(), builder -> {
                builder.setQuery(query)
                        .setFrom(offset)
                        .setSize(size)
                        .setTrackTotalHits(true)
                        .setFetchSource(getSourceFields(), null)
                        .addSort(lastModifiedSort())
                        .addSort(urlSort())
                        .addAggregation(AggregationBuilders.sum(SIZE_AGG).field(fessConfig.getIndexFieldContentLength()));
                return true;
            }, (builder, execTime, response) -> {
                response.ifPresent(res -> {
                    report.setTotal(res.getHits().getTotalHits().value());
                    final Sum sum = res.getAggregations() != null ? res.getAggregations().get(SIZE_AGG) : null;
                    if (sum != null) {
                        report.setTotalSize((long) sum.getValue());
                    }
                    for (final SearchHit hit : res.getHits().getHits()) {
                        report.getDocs().add(toReportDocument(hit.getSourceAsMap()));
                    }
                });
                return null;
            });
        } catch (final Exception e) {
            logger.warn("Failed to build the dormant document report.", e);
            report.getDocs().clear();
            report.setFailed(true);
        }
        return report;
    }

    /**
     * Writes every document that has not been modified for the given number of days as CSV, oldest
     * first: a header row followed by one row per document. The writer is flushed but not closed.
     *
     * @param urlPrefix only documents whose URL starts with this prefix, or blank for all documents
     * @param days the number of days since the last modification
     * @param unclicked true to keep only the documents that have never been opened from a search result
     * @param writer the writer to write the CSV to
     * @throws IOException if writing fails
     */
    public void exportDormantCsv(final String urlPrefix, final int days, final boolean unclicked, final Writer writer) throws IOException {
        // not closed: that would close the writer of the caller
        @SuppressWarnings("resource")
        final CsvWriter csvWriter = new CsvWriter(writer, CsvUtil.createCsvConfig());
        csvWriter.writeValues(DORMANT_CSV_COLUMNS);
        try {
            scanDocuments(buildDormantQuery(urlPrefix, days, unclicked), List.of(lastModifiedSort(), urlSort()), false,
                    doc -> writeDocument(csvWriter, doc, null, null));
        } catch (final UncheckedIOException e) {
            // thrown by the cursor handler, which cannot throw a checked exception
            throw e.getCause();
        }
        csvWriter.flush();
    }

    /**
     * Builds the filter shared by both reports: every document, or the documents whose URL starts
     * with the prefix.
     *
     * @param urlPrefix the URL prefix, or blank for all documents
     * @return the query
     */
    protected QueryBuilder buildBaseQuery(final String urlPrefix) {
        if (StringUtil.isBlank(urlPrefix)) {
            return QueryBuilders.matchAllQuery();
        }
        return QueryBuilders.boolQuery().filter(QueryBuilders.prefixQuery(fessConfig.getIndexFieldUrl(), urlPrefix.trim()));
    }

    /**
     * Builds the filter of the duplicate report: {@link #buildBaseQuery(String)} without the documents
     * whose signature has every bit set. That signature means the content has no token at all (an
     * empty file, or one with only stop words), so those documents are not copies of each other.
     *
     * @param urlPrefix the URL prefix, or blank for all documents
     * @return the query
     */
    protected QueryBuilder buildDuplicateQuery(final String urlPrefix) {
        return QueryBuilders.boolQuery()
                .filter(buildBaseQuery(urlPrefix))
                .mustNot(QueryBuilders.regexpQuery(fessConfig.getIndexFieldContentMinhashBits(), "1+"));
    }

    /**
     * Builds the query of the dormant documents: last modified before now minus the number of days,
     * optionally never clicked, and optionally under a URL prefix.
     *
     * @param urlPrefix the URL prefix, or blank for all documents
     * @param days the number of days since the last modification
     * @param unclicked true to keep only the documents whose click count is not positive
     * @return the query
     */
    protected QueryBuilder buildDormantQuery(final String urlPrefix, final int days, final boolean unclicked) {
        final long threshold = getCurrentTimeAsLong() - days * DAY_IN_MILLIS;
        final BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.rangeQuery(fessConfig.getIndexFieldLastModified()).lt(threshold).format("epoch_millis"));
        if (StringUtil.isNotBlank(urlPrefix)) {
            query.filter(QueryBuilders.prefixQuery(fessConfig.getIndexFieldUrl(), urlPrefix.trim()));
        }
        if (unclicked) {
            query.mustNot(QueryBuilders.rangeQuery(fessConfig.getIndexFieldClickCount()).gt(0));
        }
        return query;
    }

    /**
     * Searches the largest duplicate groups with a terms aggregation on the content signature and
     * the first documents of each group, ordered by URL.
     *
     * @param query the filter of the documents
     * @param groupSize the maximum number of groups
     * @param docsSize the maximum number of documents listed per group
     * @return the groups, largest first
     */
    protected List<DuplicateGroup> searchDuplicateGroups(final QueryBuilder query, final int groupSize, final int docsSize) {
        final String hashField = fessConfig.getIndexFieldContentMinhashBits();
        return searchEngineClient.search(fessConfig.getIndexDocumentSearchIndex(), builder -> {
            builder.setQuery(query).setSize(0).setTrackTotalHits(false);
            builder.addAggregation(AggregationBuilders.terms(GROUP_AGG)
                    .field(hashField)
                    .minDocCount(2)
                    .size(groupSize)
                    .order(List.of(BucketOrder.count(false), BucketOrder.key(true)))
                    .subAggregation(
                            AggregationBuilders.topHits(DOCS_AGG).size(docsSize).fetchSource(getSourceFields(), null).sort(urlSort())));
            return true;
        }, (builder, execTime, response) -> {
            final List<DuplicateGroup> groups = new ArrayList<>();
            response.ifPresent(res -> {
                final Terms terms = res.getAggregations() != null ? res.getAggregations().get(GROUP_AGG) : null;
                if (terms == null) {
                    return;
                }
                for (final Terms.Bucket bucket : terms.getBuckets()) {
                    final List<Map<String, Object>> docs = new ArrayList<>();
                    final TopHits topHits = bucket.getAggregations().get(DOCS_AGG);
                    if (topHits != null) {
                        for (final SearchHit hit : topHits.getHits().getHits()) {
                            docs.add(toReportDocument(hit.getSourceAsMap()));
                        }
                    }
                    groups.add(new DuplicateGroup(bucket.getKeyAsString(), bucket.getDocCount(), docs));
                }
            });
            return groups;
        });
    }

    /**
     * Walks every content signature of the matching documents with a composite aggregation, in
     * signature order, and hands over the signatures shared by two or more documents, one page at a
     * time. Unlike a terms aggregation, this counts every signature over the whole index.
     *
     * @param query the filter of the documents
     * @param pageSize the number of signatures read per request
     * @param pageHandler receives the shared signatures of a page with their number of documents, in signature order
     */
    protected void scanDuplicateSignatures(final QueryBuilder query, final int pageSize, final Consumer<Map<String, Long>> pageHandler) {
        final String hashField = fessConfig.getIndexFieldContentMinhashBits();
        Map<String, Object> afterKey = null;
        while (true) {
            final Map<String, Object> after = afterKey;
            final CompositeAggregation composite = searchEngineClient.search(fessConfig.getIndexDocumentSearchIndex(), builder -> {
                final CompositeAggregationBuilder aggregation = new CompositeAggregationBuilder(SIGNATURE_AGG,
                        List.of(new TermsValuesSourceBuilder(SIGNATURE_KEY).field(hashField))).size(pageSize);
                if (after != null) {
                    aggregation.aggregateAfter(after);
                }
                builder.setQuery(query).setSize(0).setTrackTotalHits(false).addAggregation(aggregation);
                return true;
            }, (builder, execTime, response) -> response.map(SearchResponse::getAggregations)
                    .map(aggregations -> (CompositeAggregation) aggregations.get(SIGNATURE_AGG))
                    .orElse(null));
            if (composite == null || composite.getBuckets().isEmpty()) {
                return;
            }
            final Map<String, Long> counts = new LinkedHashMap<>();
            for (final CompositeAggregation.Bucket bucket : composite.getBuckets()) {
                if (bucket.getDocCount() >= 2) {
                    counts.put(String.valueOf(bucket.getKey().get(SIGNATURE_KEY)), bucket.getDocCount());
                }
            }
            if (!counts.isEmpty()) {
                pageHandler.accept(counts);
            }
            afterKey = composite.afterKey();
            if (afterKey == null || composite.getBuckets().size() < pageSize) {
                return;
            }
        }
    }

    /**
     * Reads every matching document in the given order with a point in time.
     *
     * @param query the filter of the documents
     * @param sorts the order of the documents
     * @param withHash true to put the value of the first sort, the content signature, under {@link #DOC_HASH}
     * @param handler receives each document, converted by {@link #toReportDocument(Map)}
     */
    protected void scanDocuments(final QueryBuilder query, final List<SortBuilder<?>> sorts, final boolean withHash,
            final Consumer<Map<String, Object>> handler) {
        searchEngineClient.<Map<String, Object>> scrollSearch(fessConfig.getIndexDocumentSearchIndex(), builder -> {
            builder.setQuery(query).setSize(scrollSize).setFetchSource(getSourceFields(), null);
            sorts.forEach(builder::addSort);
            return true;
        }, (response, hit) -> {
            final Map<String, Object> doc = toReportDocument(hit.getSourceAsMap());
            final Object[] sortValues = hit.getSortValues();
            if (withHash && sortValues != null && sortValues.length > 0 && sortValues[0] != null) {
                doc.put(DOC_HASH, sortValues[0].toString());
            }
            return doc;
        }, doc -> {
            handler.accept(doc);
            return true;
        });
    }

    /**
     * Converts the source of an indexed document to a report document with the {@code DOC_*} keys.
     * The last modification date is parsed to a {@link java.util.Date}.
     *
     * @param source the document source, may be null
     * @return the report document
     */
    protected Map<String, Object> toReportDocument(final Map<String, Object> source) {
        final Map<String, Object> doc = new HashMap<>();
        if (source == null) {
            return doc;
        }
        doc.put(DOC_URL, source.get(fessConfig.getIndexFieldUrl()));
        doc.put(DOC_TITLE, source.get(fessConfig.getIndexFieldTitle()));
        doc.put(DOC_FILENAME, source.get(fessConfig.getIndexFieldFilename()));
        doc.put(DOC_CONTENT_LENGTH, source.get(fessConfig.getIndexFieldContentLength()));
        final Object lastModified = source.get(fessConfig.getIndexFieldLastModified());
        doc.put(DOC_LAST_MODIFIED, lastModified != null ? FessFunctions.parseDate(lastModified.toString()) : null);
        doc.put(DOC_OWNER, source.get(fessConfig.getIndexFieldOwner()));
        doc.put(DOC_LAST_MODIFIER, source.get(fessConfig.getIndexFieldLastModifier()));
        doc.put(DOC_CLICK_COUNT, source.get(fessConfig.getIndexFieldClickCount()));
        doc.put(DOC_DOC_ID, source.get(fessConfig.getIndexFieldDocId()));
        return doc;
    }

    /**
     * Writes one document as a CSV row, with its group number and group size first when given.
     *
     * @param csvWriter the CSV writer
     * @param doc the report document
     * @param group the group number, or null for the dormant report
     * @param groupSize the number of documents of the group, or null for the dormant report
     * @throws UncheckedIOException if writing fails
     */
    protected void writeDocument(final CsvWriter csvWriter, final Map<String, Object> doc, final Long group, final Long groupSize) {
        final List<String> values = new ArrayList<>();
        if (group != null) {
            values.add(CsvUtil.toCell(group));
            values.add(CsvUtil.toCell(groupSize));
        }
        values.add(CsvUtil.toCell(doc.get(DOC_URL)));
        values.add(CsvUtil.toCell(doc.get(DOC_TITLE)));
        values.add(CsvUtil.toCell(doc.get(DOC_FILENAME)));
        values.add(CsvUtil.toCell(doc.get(DOC_CONTENT_LENGTH)));
        values.add(doc.get(DOC_LAST_MODIFIED) instanceof final java.util.Date date ? FessFunctions.formatDate(date) : StringUtil.EMPTY);
        values.add(CsvUtil.toCell(doc.get(DOC_OWNER)));
        values.add(CsvUtil.toCell(doc.get(DOC_LAST_MODIFIER)));
        values.add(CsvUtil.toCell(doc.get(DOC_CLICK_COUNT)));
        values.add(CsvUtil.toCell(doc.get(DOC_DOC_ID)));
        try {
            csvWriter.writeValues(values);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Returns the index fields a report document is built from.
     *
     * @return the field names
     */
    protected String[] getSourceFields() {
        return new String[] { fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldTitle(), fessConfig.getIndexFieldFilename(),
                fessConfig.getIndexFieldContentLength(), fessConfig.getIndexFieldLastModified(), fessConfig.getIndexFieldOwner(),
                fessConfig.getIndexFieldLastModifier(), fessConfig.getIndexFieldClickCount(), fessConfig.getIndexFieldDocId() };
    }

    /**
     * Returns the current time the number of days of the dormant report counts from.
     *
     * @return the current time in milliseconds
     */
    protected long getCurrentTimeAsLong() {
        return ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
    }

    private SortBuilder<?> lastModifiedSort() {
        return SortBuilders.fieldSort(fessConfig.getIndexFieldLastModified()).order(SortOrder.ASC);
    }

    private SortBuilder<?> urlSort() {
        return SortBuilders.fieldSort(fessConfig.getIndexFieldUrl()).order(SortOrder.ASC);
    }
}
