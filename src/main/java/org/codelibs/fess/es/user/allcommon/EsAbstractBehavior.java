/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.user.allcommon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Resource;

import org.codelibs.fess.es.user.allcommon.EsAbstractEntity.DocMeta;
import org.codelibs.fess.es.user.allcommon.EsAbstractEntity.RequestOptionCall;
import org.dbflute.Entity;
import org.dbflute.bhv.AbstractBehaviorWritable;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.bhv.writable.DeleteOption;
import org.dbflute.bhv.writable.InsertOption;
import org.dbflute.bhv.writable.UpdateOption;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.coption.CursorSelectOption;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.exception.FetchingOverSafetySizeException;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.util.DfTypeUtil;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

/**
 * @param <ENTITY> The type of entity.
 * @param <CB> The type of condition-bean.
 * @author ESFlute (using FreeGen)
 */
public abstract class EsAbstractBehavior<ENTITY extends Entity, CB extends ConditionBean> extends AbstractBehaviorWritable<ENTITY, CB> {

    @Resource
    private Client client;

    protected int sizeForDelete = 100;
    protected String scrollForDelete = "1m";
    protected int sizeForCursor = 100;
    protected String scrollForCursor = "1m";
    protected String searchTimeout = "3m";
    protected String indexTimeout = "3m";
    protected String scrollSearchTimeout = "3m";
    protected String bulkTimeout = "3m";
    protected String deleteTimeout = "3m";
    protected String refreshTimeout = "1m";

    protected abstract String asEsIndex();

    protected abstract String asEsIndexType();

    protected abstract String asEsSearchType();

    protected abstract <RESULT extends ENTITY> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType);

    // ===================================================================================
    //                                                                       Elasticsearch
    //                                                                              ======
    public RefreshResponse refresh() {
        return client.admin().indices().prepareRefresh(asEsIndex()).execute().actionGet(refreshTimeout);
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    @Override
    protected int delegateSelectCountUniquely(final ConditionBean cb) {
        // #pending check response and cast problem
        final SearchRequestBuilder builder = client.prepareSearch(asEsIndex()).setTypes(asEsSearchType());
        final EsAbstractConditionBean esCb = (EsAbstractConditionBean) cb;
        if (esCb.getPreference() != null) {
            builder.setPreference(esCb.getPreference());
        }
        return (int) esCb.build(builder).execute().actionGet(searchTimeout).getHits().getTotalHits();
    }

    @Override
    protected <RESULT extends ENTITY> RESULT delegateSelectEntity(final ConditionBean cb, final Class<? extends RESULT> entityType) {
        final List<? extends RESULT> list = delegateSelectList(cb, entityType);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() >= 2) {
            String msg = "The size of selected list is over 1: " + list.size();
            throw new FetchingOverSafetySizeException(msg, 1); // immediately caught by caller and translated
        }
        return list.get(0);
    }

    @Override
    protected <RESULT extends ENTITY> List<RESULT> delegateSelectList(final ConditionBean cb, final Class<? extends RESULT> entityType) {
        // #pending check response
        final SearchRequestBuilder builder = client.prepareSearch(asEsIndex()).setTypes(asEsSearchType());
        final int from;
        final int size;
        if (cb.isFetchScopeEffective()) {
            from = cb.getPageStartIndex();
            size = cb.getFetchSize();
        } else {
            from = 0;
            size = 10;
        }
        builder.setFrom(from);
        builder.setSize(size);
        final EsAbstractConditionBean esCb = (EsAbstractConditionBean) cb;
        if (esCb.getPreference() != null) {
            builder.setPreference(esCb.getPreference());
        }
        esCb.request().build(builder);
        final SearchResponse response = esCb.build(builder).execute().actionGet(searchTimeout);

        final EsPagingResultBean<RESULT> list = new EsPagingResultBean<>(builder);
        final SearchHits searchHits = response.getHits();
        searchHits.forEach(hit -> {
            final Map<String, Object> source = hit.getSourceAsMap();
            final RESULT entity = createEntity(source, entityType);
            final DocMeta docMeta = ((EsAbstractEntity) entity).asDocMeta();
            docMeta.id(hit.getId());
            docMeta.version(hit.getVersion());
            list.add(entity);
        });

        list.setPageSize(size);
        list.setAllRecordCount((int) searchHits.getTotalHits());
        list.setCurrentPageNumber(cb.getFetchPageNumber());

        list.setTook(response.getTook().getMillis());
        list.setTotalShards(response.getTotalShards());
        list.setSuccessfulShards(response.getSuccessfulShards());
        list.setFailedShards(response.getFailedShards());

        list.setAggregation(response.getAggregations());

        // #pending others

        return list;
    }

    @Override
    protected <RESULT extends ENTITY> void helpSelectCursorHandlingByPaging(CB cb, EntityRowHandler<RESULT> handler,
            Class<? extends RESULT> entityType, CursorSelectOption option) {
        delegateSelectCursor(cb, handler, entityType);
    }

    @Override
    protected <RESULT extends ENTITY> void delegateSelectCursor(final ConditionBean cb, final EntityRowHandler<RESULT> handler,
            final Class<? extends RESULT> entityType) {
        delegateBulkRequest(cb, searchHits -> {
            searchHits.forEach(hit -> {
                if (handler.isBreakCursor()) {
                    return;
                }
                final Map<String, Object> source = hit.getSourceAsMap();
                final RESULT entity = createEntity(source, entityType);
                final DocMeta docMeta = ((EsAbstractEntity) entity).asDocMeta();
                docMeta.id(hit.getId());
                docMeta.version(hit.getVersion());
                handler.handle(entity);
            });

            return !handler.isBreakCursor();
        });
    }

    protected <RESULT extends ENTITY> void delegateSelectBulk(final ConditionBean cb, final EntityRowHandler<List<RESULT>> handler,
            final Class<? extends RESULT> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler", handler);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        assertObjectNotNull("entityRowHandler", handler);
        delegateBulkRequest(cb, searchHits -> {
            List<RESULT> list = new ArrayList<>();
            searchHits.forEach(hit -> {
                final Map<String, Object> source = hit.getSourceAsMap();
                final RESULT entity = createEntity(source, entityType);
                final DocMeta docMeta = ((EsAbstractEntity) entity).asDocMeta();
                docMeta.id(hit.getId());
                docMeta.version(hit.getVersion());
                list.add(entity);
            });

            handler.handle(list);
            return !handler.isBreakCursor();
        });
    }

    protected void delegateBulkRequest(final ConditionBean cb, Function<SearchHits, Boolean> handler) {
        SearchResponse response = null;
        while (true) {
            if (response == null) {
                final SearchRequestBuilder builder =
                        client.prepareSearch(asEsIndex()).setTypes(asEsIndexType()).setScroll(scrollForCursor).setSize(sizeForCursor);
                final EsAbstractConditionBean esCb = (EsAbstractConditionBean) cb;
                if (esCb.getPreference() != null) {
                    builder.setPreference(esCb.getPreference());
                }
                esCb.request().build(builder);
                response = esCb.build(builder).execute().actionGet(scrollSearchTimeout);
            } else {
                final String scrollId = response.getScrollId();
                response = client.prepareSearchScroll(scrollId).setScroll(scrollForDelete).execute().actionGet(scrollSearchTimeout);
            }
            final SearchHits searchHits = response.getHits();
            final SearchHit[] hits = searchHits.getHits();
            if (hits.length == 0) {
                break;
            }

            if (!handler.apply(searchHits)) {
                break;
            }
        }
    }

    @Override
    protected Number doReadNextVal() {
        final String msg = "This table is NOT related to sequence: " + asEsIndexType();
        throw new UnsupportedOperationException(msg);
    }

    @Override
    protected <RESULT extends Entity> ListResultBean<RESULT> createListResultBean(final ConditionBean cb, final List<RESULT> selectedList) {
        if (selectedList instanceof EsPagingResultBean) {
            return (ListResultBean<RESULT>) selectedList;
        }
        throw new IllegalBehaviorStateException("selectedList is not EsPagingResultBean.");
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    @Override
    protected int delegateInsert(final Entity entity, final InsertOption<? extends ConditionBean> option) {
        final EsAbstractEntity esEntity = (EsAbstractEntity) entity;
        IndexRequestBuilder builder = createInsertRequest(esEntity);

        final IndexResponse response = builder.execute().actionGet(indexTimeout);
        esEntity.asDocMeta().id(response.getId());
        return response.getResult() == Result.CREATED ? 1 : 0;
    }

    protected IndexRequestBuilder createInsertRequest(final EsAbstractEntity esEntity) {
        final IndexRequestBuilder builder = client.prepareIndex(asEsIndex(), asEsIndexType()).setSource(toSource(esEntity));
        final String id = esEntity.asDocMeta().id();
        if (id != null) {
            builder.setId(id);
        }
        final RequestOptionCall<IndexRequestBuilder> indexOption = esEntity.asDocMeta().indexOption();
        if (indexOption != null) {
            indexOption.callback(builder);
        }
        return builder;
    }

    @Override
    protected int delegateUpdate(final Entity entity, final UpdateOption<? extends ConditionBean> option) {
        final EsAbstractEntity esEntity = (EsAbstractEntity) entity;
        final IndexRequestBuilder builder = createUpdateRequest(esEntity);

        final IndexResponse response = builder.execute().actionGet(indexTimeout);
        long version = response.getVersion();
        if (version != -1) {
            esEntity.asDocMeta().version(version);
        }
        return 1;
    }

    protected IndexRequestBuilder createUpdateRequest(final EsAbstractEntity esEntity) {
        final IndexRequestBuilder builder =
                client.prepareIndex(asEsIndex(), asEsIndexType(), esEntity.asDocMeta().id()).setSource(toSource(esEntity));
        final RequestOptionCall<IndexRequestBuilder> indexOption = esEntity.asDocMeta().indexOption();
        if (indexOption != null) {
            indexOption.callback(builder);
        }
        final Long version = esEntity.asDocMeta().version();
        if (version != null && version.longValue() != -1) {
            builder.setVersion(version);
        }
        return builder;
    }

    protected Map<String, Object> toSource(final EsAbstractEntity esEntity) {
        return esEntity.toSource();
    }

    @Override
    protected int delegateDelete(final Entity entity, final DeleteOption<? extends ConditionBean> option) {
        final EsAbstractEntity esEntity = (EsAbstractEntity) entity;
        final DeleteRequestBuilder builder = createDeleteRequest(esEntity);

        final DeleteResponse response = builder.execute().actionGet(deleteTimeout);
        return response.getResult() == Result.DELETED ? 1 : 0;
    }

    protected DeleteRequestBuilder createDeleteRequest(final EsAbstractEntity esEntity) {
        final DeleteRequestBuilder builder = client.prepareDelete(asEsIndex(), asEsIndexType(), esEntity.asDocMeta().id());
        final RequestOptionCall<DeleteRequestBuilder> deleteOption = esEntity.asDocMeta().deleteOption();
        if (deleteOption != null) {
            deleteOption.callback(builder);
        }
        return builder;
    }

    @Override
    protected int delegateQueryDelete(final ConditionBean cb, final DeleteOption<? extends ConditionBean> option) {
        SearchResponse response = null;
        int count = 0;
        while (true) {
            if (response == null) {
                final SearchRequestBuilder builder =
                        client.prepareSearch(asEsIndex()).setTypes(asEsIndexType()).setScroll(scrollForDelete).setSize(sizeForDelete);
                final EsAbstractConditionBean esCb = (EsAbstractConditionBean) cb;
                if (esCb.getPreference() != null) {
                    esCb.setPreference(esCb.getPreference());
                }
                esCb.request().build(builder);
                response = esCb.build(builder).execute().actionGet(scrollSearchTimeout);
            } else {
                final String scrollId = response.getScrollId();
                response = client.prepareSearchScroll(scrollId).setScroll(scrollForDelete).execute().actionGet(scrollSearchTimeout);
            }
            final SearchHits searchHits = response.getHits();
            final SearchHit[] hits = searchHits.getHits();
            if (hits.length == 0) {
                break;
            }

            final BulkRequestBuilder bulkRequest = client.prepareBulk();
            for (final SearchHit hit : hits) {
                bulkRequest.add(client.prepareDelete(asEsIndex(), asEsIndexType(), hit.getId()));
            }
            count += hits.length;
            final BulkResponse bulkResponse = bulkRequest.execute().actionGet(bulkTimeout);
            if (bulkResponse.hasFailures()) {
                throw new IllegalBehaviorStateException(bulkResponse.buildFailureMessage());
            }
        }
        return count;
    }

    protected int[] delegateBatchInsert(final List<? extends Entity> entityList, final InsertOption<? extends ConditionBean> option) {
        if (entityList.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchRequest(entityList, esEntity -> {
            return createInsertRequest(esEntity);
        });
    }

    protected int[] delegateBatchUpdate(List<? extends Entity> entityList, UpdateOption<? extends ConditionBean> option) {
        if (entityList.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchRequest(entityList, esEntity -> {
            return createUpdateRequest(esEntity);
        });
    }

    protected int[] delegateBatchDelete(List<? extends Entity> entityList, DeleteOption<? extends ConditionBean> option) {
        if (entityList.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchRequest(entityList, esEntity -> {
            return createDeleteRequest(esEntity);
        });
    }

    protected <BUILDER> int[] delegateBatchRequest(final List<? extends Entity> entityList, Function<EsAbstractEntity, BUILDER> call) {
        @SuppressWarnings("unchecked")
        final BulkList<? extends Entity, BUILDER> bulkList = (BulkList<? extends Entity, BUILDER>) entityList;
        final RequestOptionCall<BUILDER> builderEntityCall = bulkList.getEntityCall();
        final BulkRequestBuilder bulkBuilder = client.prepareBulk();
        for (final Entity entity : entityList) {
            final EsAbstractEntity esEntity = (EsAbstractEntity) entity;
            BUILDER builder = call.apply(esEntity);
            if (builder instanceof IndexRequestBuilder) {
                if (builderEntityCall != null) {
                    builderEntityCall.callback(builder);
                }
                bulkBuilder.add((IndexRequestBuilder) builder);
            } else if (builder instanceof UpdateRequestBuilder) {
                if (builderEntityCall != null) {
                    builderEntityCall.callback(builder);
                }
                bulkBuilder.add((UpdateRequestBuilder) builder);
            } else if (builder instanceof DeleteRequestBuilder) {
                if (builderEntityCall != null) {
                    builderEntityCall.callback(builder);
                }
                bulkBuilder.add((DeleteRequestBuilder) builder);
            }
        }
        final RequestOptionCall<BulkRequestBuilder> builderCall = bulkList.getCall();
        if (builderCall != null) {
            builderCall.callback(bulkBuilder);
        }

        final BulkResponse response = bulkBuilder.execute().actionGet(bulkTimeout);
        final BulkItemResponse[] itemResponses = response.getItems();
        if (itemResponses.length != entityList.size()) {
            throw new IllegalStateException("Invalid response size: " + itemResponses.length + " != " + entityList.size());
        }
        final int[] results = new int[itemResponses.length];
        for (int i = 0; i < itemResponses.length; i++) {
            final BulkItemResponse itemResponse = itemResponses[i];
            final Entity entity = entityList.get(i);
            if (entity instanceof EsAbstractEntity) {
                ((EsAbstractEntity) entity).asDocMeta().id(itemResponse.getId());
            }
            results[i] = itemResponse.isFailed() ? 0 : 1;
        }
        return results;
    }

    // to suppress xacceptUpdateColumnModifiedPropertiesIfNeeds()'s specify process
    @Override
    protected UpdateOption<CB> createPlainUpdateOption() {
        UpdateOption<CB> updateOption = new UpdateOption<CB>();
        updateOption.xtoBeCompatibleBatchUpdateDefaultEveryColumn();
        return updateOption;
    }

    protected boolean isCompatibleBatchInsertDefaultEveryColumn() {
        return true;
    }

    public void setSizeForDelete(int sizeForDelete) {
        this.sizeForDelete = sizeForDelete;
    }

    public void setScrollForDelete(String scrollForDelete) {
        this.scrollForDelete = scrollForDelete;
    }

    public void setSizeForCursor(int sizeForCursor) {
        this.sizeForCursor = sizeForCursor;
    }

    public void setScrollForCursor(String scrollForCursor) {
        this.scrollForCursor = scrollForCursor;
    }

    public void setSearchTimeout(String searchTimeout) {
        this.searchTimeout = searchTimeout;
    }

    public void setIndexTimeout(String indexTimeout) {
        this.indexTimeout = indexTimeout;
    }

    public void setScrollSearchTimeout(String scrollSearchTimeout) {
        this.scrollSearchTimeout = scrollSearchTimeout;
    }

    public void setBulkTimeout(String bulkTimeout) {
        this.bulkTimeout = bulkTimeout;
    }

    public void setDeleteTimeout(String deleteTimeout) {
        this.deleteTimeout = deleteTimeout;
    }

    public void setRefreshTimeout(String refreshTimeout) {
        this.refreshTimeout = refreshTimeout;
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected String[] toStringArray(final Object value) {
        if (value instanceof String[]) {
            return (String[]) value;
        } else if (value instanceof List) {
            return ((List<?>) value).stream().map(v -> v.toString()).toArray(n -> new String[n]);
        }
        String str = DfTypeUtil.toString(value);
        if (str == null) {
            return null;
        }
        return new String[] { str };
    }

    protected LocalDateTime toLocalDateTime(Object value) {
        return DfTypeUtil.toLocalDateTime(value);
    }

    protected Date toDate(Object value) {
        return DfTypeUtil.toDate(value);
    }

    public static class BulkList<E, B> implements List<E> {

        private final List<E> parent;

        private final RequestOptionCall<BulkRequestBuilder> call;

        private final RequestOptionCall<B> entityCall;

        public BulkList(final List<E> parent, final RequestOptionCall<BulkRequestBuilder> call, final RequestOptionCall<B> entityCall) {
            this.parent = parent;
            this.entityCall = entityCall;
            this.call = call;
        }

        public int size() {
            return parent.size();
        }

        public boolean isEmpty() {
            return parent.isEmpty();
        }

        public boolean contains(final Object o) {
            return parent.contains(o);
        }

        public Iterator<E> iterator() {
            return parent.iterator();
        }

        public Object[] toArray() {
            return parent.toArray();
        }

        public <T> T[] toArray(final T[] a) {
            return parent.toArray(a);
        }

        public boolean add(final E e) {
            return parent.add(e);
        }

        public boolean remove(final Object o) {
            return parent.remove(o);
        }

        public boolean containsAll(final Collection<?> c) {
            return parent.containsAll(c);
        }

        public boolean addAll(final Collection<? extends E> c) {
            return parent.addAll(c);
        }

        public boolean addAll(final int index, final Collection<? extends E> c) {
            return parent.addAll(index, c);
        }

        public boolean removeAll(final Collection<?> c) {
            return parent.removeAll(c);
        }

        public boolean retainAll(final Collection<?> c) {
            return parent.retainAll(c);
        }

        public void clear() {
            parent.clear();
        }

        public boolean equals(final Object o) {
            return parent.equals(o);
        }

        public int hashCode() {
            return parent.hashCode();
        }

        public E get(final int index) {
            return parent.get(index);
        }

        public E set(final int index, final E element) {
            return parent.set(index, element);
        }

        public void add(final int index, final E element) {
            parent.add(index, element);
        }

        public E remove(final int index) {
            return parent.remove(index);
        }

        public int indexOf(final Object o) {
            return parent.indexOf(o);
        }

        public int lastIndexOf(final Object o) {
            return parent.lastIndexOf(o);
        }

        public ListIterator<E> listIterator() {
            return parent.listIterator();
        }

        public ListIterator<E> listIterator(final int index) {
            return parent.listIterator(index);
        }

        public List<E> subList(final int fromIndex, final int toIndex) {
            return parent.subList(fromIndex, toIndex);
        }

        public RequestOptionCall<BulkRequestBuilder> getCall() {
            return call;
        }

        public RequestOptionCall<B> getEntityCall() {
            return entityCall;
        }
    }
}
