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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fesen.opensearch.search.aggregations.Aggregations;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.Terms;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.ValueCount;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.TagTypePager;
import org.codelibs.fess.exception.TagTypeConflictException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.allcommon.EsPagingResultBean;
import org.codelibs.fess.opensearch.config.cbean.TagTypeCB;
import org.codelibs.fess.opensearch.config.exbhv.TagTypeBhv;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for tag types, the tags each logged-in user owns.
 *
 * <p>The id of a tag type is derived from its name and owner, so a create must not overwrite a
 * tag type that already exists, and an update must not overwrite a change made since the tag
 * type was read. Both are enforced by the search engine: {@link #insert(TagType)} creates the
 * document only when its id is free, and {@link #update(TagType)} writes it only when its
 * sequence number and primary term are still the ones read. A lost race on either is reported as
 * a {@link TagTypeConflictException}.</p>
 */
public class TagTypeService extends FessAppService {

    /** The name of the aggregations that count the paths of each tag. */
    private static final String PATH_COUNT_AGGREGATION = "path_count";

    /** The TagType behavior. */
    @Resource
    protected TagTypeBhv tagTypeBhv;

    /** The Fess config. */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Default constructor.
     */
    public TagTypeService() {
    }

    /**
     * Get a page of tag types.
     *
     * @param tagTypePager The pager for tag types.
     * @return A list of tag types.
     */
    public List<TagType> getTagTypeList(final TagTypePager tagTypePager) {

        final PagingResultBean<TagType> tagTypeList = tagTypeBhv.selectPage(cb -> {
            cb.paging(tagTypePager.getPageSize(), tagTypePager.getCurrentPageNumber());
            setupListCondition(cb, tagTypePager);
        });

        // update pager
        BeanUtil.copyBeanToBean(tagTypeList, tagTypePager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        tagTypePager.setPageNumberList(tagTypeList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return tagTypeList;
    }

    /**
     * Set up list conditions.
     *
     * @param cb The condition bean.
     * @param tagTypePager The pager for tag types.
     */
    protected void setupListCondition(final TagTypeCB cb, final TagTypePager tagTypePager) {
        if (StringUtil.isNotBlank(tagTypePager.name)) {
            cb.query().setName_Wildcard(wrapQuery(tagTypePager.name));
        }
        if (StringUtil.isNotBlank(tagTypePager.owner)) {
            cb.query().setOwner_Wildcard(wrapQuery(tagTypePager.owner));
        }
        specifyColumnsWithoutPaths(cb);

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();
        cb.query().addOrderBy_Owner_Asc();
    }

    /**
     * Get a tag type with all of its fields.
     *
     * @param id The ID of the tag type.
     * @return An optional entity of the tag type.
     */
    public OptionalEntity<TagType> getTagType(final String id) {
        return tagTypeBhv.selectByPK(id);
    }

    /**
     * Get the tag types of one owner, sorted by sort order and name.
     *
     * <p>The returned entities do not carry their {@code paths}, which can hold
     * {@code user.tag.max.paths} URLs each. Read a tag type with {@link #getTagType(String)}
     * before passing it to {@link #update(TagType)}, or its paths are cleared.</p>
     *
     * @param owner The owner of the tag types.
     * @return A list of tag types without their paths.
     */
    public List<TagType> getTagTypeListByOwner(final String owner) {
        return tagTypeBhv.selectList(cb -> {
            cb.query().setOwner_Term(owner);
            specifyColumnsWithoutPaths(cb);
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.paging(fessConfig.getUserTagMaxTagsAsInteger(), 1);
        });
    }

    /**
     * Count the paths of each tag type of one owner without reading the paths: a terms
     * aggregation on the name, unique per owner, with a value count of the paths.
     *
     * @param owner The owner of the tag types.
     * @return The number of paths keyed by tag name; a tag without paths may be missing.
     */
    public Map<String, Long> getPathCountMapByOwner(final String owner) {
        final int maxTags = fessConfig.getUserTagMaxTagsAsInteger();
        final EsPagingResultBean<TagType> result = (EsPagingResultBean<TagType>) tagTypeBhv.selectPage(cb -> {
            cb.fetchFirst(0);
            cb.query().setOwner_Term(owner);
            cb.aggregation()
                    .setName_Terms(PATH_COUNT_AGGREGATION, op -> op.size(maxTags), ca -> ca.setPaths_Count(PATH_COUNT_AGGREGATION, null));
        });
        final Map<String, Long> pathCountMap = new HashMap<>();
        final Aggregations aggregations = result.getAggregations();
        if (aggregations != null && aggregations.get(PATH_COUNT_AGGREGATION) instanceof final Terms terms) {
            for (final Terms.Bucket bucket : terms.getBuckets()) {
                final ValueCount valueCount = bucket.getAggregations().get(PATH_COUNT_AGGREGATION);
                pathCountMap.put(bucket.getKeyAsString(), valueCount == null ? 0L : valueCount.getValue());
            }
        }
        return pathCountMap;
    }

    /**
     * Count the tag types of one owner.
     *
     * @param owner The owner of the tag types.
     * @return The number of tag types the owner has.
     */
    public long countByOwner(final String owner) {
        return tagTypeBhv.selectCount(cb -> cb.query().setOwner_Term(owner));
    }

    /**
     * Create a tag type. Its id must be set; the write fails when a tag type with that id exists.
     *
     * @param tagType The tag type to create.
     * @throws TagTypeConflictException When a tag type with the same id already exists.
     */
    public void insert(final TagType tagType) {
        try {
            tagTypeBhv.insert(tagType, op -> {
                op.setCreate(true);
                op.setRefreshPolicy(Constants.TRUE);
            });
        } catch (final RuntimeException e) {
            if (isVersionConflict(e)) {
                throw new TagTypeConflictException("The tag type already exists: id=" + tagType.getId(), e);
            }
            throw e;
        }
    }

    /**
     * Update a tag type that was read with {@link #getTagType(String)}. The write fails when the
     * stored tag type changed since it was read.
     *
     * @param tagType The tag type to update, carrying the sequence number and primary term it was read with.
     * @throws IllegalArgumentException When the tag type carries no sequence number or primary term.
     * @throws TagTypeConflictException When the stored tag type changed since it was read.
     */
    public void update(final TagType tagType) {
        final Long seqNo = tagType.getSeqNo();
        final Long primaryTerm = tagType.getPrimaryTerm();
        if (seqNo == null || seqNo.longValue() < 0L || primaryTerm == null || primaryTerm.longValue() <= 0L) {
            throw new IllegalArgumentException("The tag type has no sequence number or primary term: id=" + tagType.getId());
        }
        try {
            tagTypeBhv.update(tagType, op -> {
                op.setIfSeqNo(seqNo);
                op.setIfPrimaryTerm(primaryTerm);
                op.setRefreshPolicy(Constants.TRUE);
            });
        } catch (final RuntimeException e) {
            if (isVersionConflict(e)) {
                throw new TagTypeConflictException("The tag type was changed concurrently: id=" + tagType.getId(), e);
            }
            throw e;
        }
    }

    /**
     * Delete a tag type.
     *
     * @param tagType The tag type to delete.
     */
    public void delete(final TagType tagType) {
        tagTypeBhv.delete(tagType, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
    }

    /**
     * Fetches every field except {@code paths}.
     *
     * @param cb The condition bean.
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
     * Tests whether the throwable, or one of its causes, is a version conflict of the search
     * engine. The in-process client throws a {@code VersionConflictEngineException}; the HTTP
     * client throws a generic exception whose message names the error type.
     *
     * @param t The throwable to inspect.
     * @return true if it is a version conflict.
     */
    protected boolean isVersionConflict(final Throwable t) {
        Throwable cur = t;
        while (cur != null) {
            if (cur.getClass().getName().endsWith("VersionConflictEngineException")) {
                return true;
            }
            final String msg = cur.getMessage();
            if (msg != null && msg.contains("type=version_conflict_engine_exception")) {
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
