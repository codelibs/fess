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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.automaton.Operations;
import org.apache.lucene.util.automaton.TooComplexToDeterminizeException;
import org.codelibs.fesen.opensearch.action.index.IndexRequestBuilder;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.SettingChangedConcurrentlyException;
import org.lastaflute.core.message.UserMessages;

/**
 * Abstract base class for Fess application services.
 * This class provides common functionality and utilities for service implementations
 * throughout the Fess search application.
 */
public abstract class FessAppService {

    /**
     * Default constructor.
     */
    public FessAppService() {
        // Default constructor
    }

    /**
     * Wraps a query string with wildcard characters to enable partial matching.
     * This method ensures that the query string is surrounded by asterisks (*)
     * to support prefix and suffix matching in search operations.
     *
     * <p>
     * A pattern that the search engine cannot run (for example {@code *xxx...*} of 315 or more repeated characters, or
     * {@code *a?a?...*}) is refused by {@link #checkWildcard(String)} as an {@link InvalidQueryException}, which the
     * admin API reports as an invalid request (HTTP 400) instead of "all shards failed" (HTTP 500).
     * </p>
     *
     * @param query the query string to wrap with wildcards
     * @return the wrapped query string with leading and trailing asterisks
     * @throws InvalidQueryException if the search engine could not run the wildcard pattern
     */
    protected static String wrapQuery(final String query) {
        final StringBuilder sb = new StringBuilder();
        if (!query.startsWith("*")) {
            sb.append("*");
        }
        sb.append(query);
        if (!query.endsWith("*")) {
            sb.append("*");
        }
        return checkWildcard(sb.toString());
    }

    /**
     * Checks that the search engine can run a wildcard pattern, and returns it.
     * <p>
     * The search engine turns a wildcard pattern into an automaton and refuses one that it cannot determinize
     * within {@link Operations#DEFAULT_DETERMINIZE_WORK_LIMIT} (the limit of the bundled Lucene, which the search
     * engine uses by default), and the whole search fails with "all shards failed". This runs the same check.
     * </p>
     *
     * @param pattern the wildcard pattern of a filter
     * @return the pattern
     * @throws InvalidQueryException if the search engine could not run the pattern
     */
    protected static String checkWildcard(final String pattern) {
        try {
            Operations.determinize(WildcardQuery.toAutomaton(new Term("f", pattern), Operations.DEFAULT_DETERMINIZE_WORK_LIMIT),
                    Operations.DEFAULT_DETERMINIZE_WORK_LIMIT);
        } catch (final TooComplexToDeterminizeException e) {
            throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryParseError(UserMessages.GLOBAL_PROPERTY_KEY),
                    "The wildcard pattern of a filter is too complex: length=" + pattern.length(), e);
        }
        return pattern;
    }

    /**
     * Writes a setting of the {@code config} index with {@code insertOrUpdate} and an immediate refresh. A setting
     * that was read from the index is written only if the stored one is unchanged since that read: the sequence
     * number and primary term the setting carries go into the request, and the index refuses the write otherwise.
     * A new setting (one that was not read) is written without that condition.
     *
     * @param <E> the type of the setting
     * @param entity the setting to write
     * @param writer the {@code insertOrUpdate} of the behavior of the setting
     * @throws SettingChangedConcurrentlyException if the stored setting was changed or deleted since it was read
     */
    protected <E extends org.codelibs.fess.opensearch.config.allcommon.EsAbstractEntity> void insertOrUpdateIfUnchanged(final E entity,
            final BiConsumer<E, org.codelibs.fess.opensearch.config.allcommon.EsAbstractEntity.RequestOptionCall<IndexRequestBuilder>> writer) {
        final org.codelibs.fess.opensearch.config.allcommon.EsAbstractEntity.DocMeta docMeta = entity.asDocMeta();
        writeIfUnchanged(docMeta.id(), docMeta.seqNo(), docMeta.primaryTerm(), option -> writer.accept(entity, option::accept));
    }

    /**
     * Writes a setting of the {@code user} index (a group or a role) like
     * {@link #insertOrUpdateIfUnchanged(org.codelibs.fess.opensearch.config.allcommon.EsAbstractEntity, BiConsumer)}.
     *
     * @param <E> the type of the setting
     * @param entity the setting to write
     * @param writer the {@code insertOrUpdate} of the behavior of the setting
     * @throws SettingChangedConcurrentlyException if the stored setting was changed or deleted since it was read
     */
    protected <E extends org.codelibs.fess.opensearch.user.allcommon.EsAbstractEntity> void insertOrUpdateIfUnchanged(final E entity,
            final BiConsumer<E, org.codelibs.fess.opensearch.user.allcommon.EsAbstractEntity.RequestOptionCall<IndexRequestBuilder>> writer) {
        final org.codelibs.fess.opensearch.user.allcommon.EsAbstractEntity.DocMeta docMeta = entity.asDocMeta();
        writeIfUnchanged(docMeta.id(), docMeta.seqNo(), docMeta.primaryTerm(), option -> writer.accept(entity, option::accept));
    }

    /**
     * Tells whether a document was read from the index, so that its sequence number and primary term are known.
     *
     * @param seqNo the sequence number of the read, or null
     * @param primaryTerm the primary term of the read, or null
     * @return true if both are known
     */
    protected static boolean hasReadVersion(final Long seqNo, final Long primaryTerm) {
        return seqNo != null && seqNo.longValue() >= 0L && primaryTerm != null && primaryTerm.longValue() > 0L;
    }

    /**
     * Runs a write with an immediate refresh and, when the sequence number and primary term of a read are known,
     * on the condition that the document is unchanged since that read.
     *
     * @param id the ID of the document, for the message of the exception
     * @param seqNo the sequence number of the read, or null
     * @param primaryTerm the primary term of the read, or null
     * @param write the write, which applies the options it is given to its request
     * @throws SettingChangedConcurrentlyException if the index refused the write with a version conflict
     */
    private void writeIfUnchanged(final String id, final Long seqNo, final Long primaryTerm,
            final Consumer<Consumer<IndexRequestBuilder>> write) {
        final boolean versioned = hasReadVersion(seqNo, primaryTerm);
        try {
            write.accept(op -> {
                op.setRefreshPolicy(Constants.TRUE);
                if (versioned) {
                    op.setIfSeqNo(seqNo);
                    op.setIfPrimaryTerm(primaryTerm);
                }
            });
        } catch (final RuntimeException e) {
            if (isVersionConflict(e)) {
                throw new SettingChangedConcurrentlyException(
                        "The setting was changed or deleted since it was read, so it was not written: id=" + id, e);
            }
            throw e;
        }
    }

    /**
     * Tests whether the throwable, or one of its causes, is a version conflict of the search engine. The in-process
     * client throws a {@code VersionConflictEngineException}; the HTTP client throws a generic exception whose message
     * names the error type.
     *
     * @param t the throwable to inspect
     * @return true if it is a version conflict
     */
    protected boolean isVersionConflict(final Throwable t) {
        Throwable cur = t;
        while (cur != null) {
            if (cur.getClass().getName().endsWith("VersionConflictEngineException")) {
                return true;
            }
            final String msg = cur.getMessage();
            if (msg != null && msg.contains("version_conflict_engine_exception")) {
                return true;
            }
            final Throwable next = cur.getCause();
            if (next == cur) {
                break;
            }
            cur = next;
        }
        return false;
    }
}
