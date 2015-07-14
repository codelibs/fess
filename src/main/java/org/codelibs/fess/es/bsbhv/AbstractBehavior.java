package org.codelibs.fess.es.bsbhv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Resource;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.DocMeta;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.cbean.bs.AbstractConditionBean;
import org.codelibs.fess.es.cbean.result.EsPagingResultBean;
import org.dbflute.Entity;
import org.dbflute.bhv.AbstractBehaviorWritable;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.bhv.writable.DeleteOption;
import org.dbflute.bhv.writable.InsertOption;
import org.dbflute.bhv.writable.UpdateOption;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.coption.CursorSelectOption;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

/**
 * @author FreeGen
 */
public abstract class AbstractBehavior<ENTITY extends Entity, CB extends ConditionBean> extends AbstractBehaviorWritable<ENTITY, CB> {

    @Resource
    protected Client client;

    protected int sizeForDelete = 100;

    protected String scrollForDelete = "1m";

    protected int sizeForCursor = 100;

    protected String scrollForCursor = "1m";

    protected abstract String asEsIndex();

    protected abstract String asEsIndexType();

    protected abstract String asEsSearchType();

    protected abstract <RESULT extends ENTITY> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType);

    @Override
    protected int delegateSelectCountUniquely(final ConditionBean cb) {
        // TODO check response and cast problem
        final CountRequestBuilder builder = client.prepareCount(asEsIndex()).setTypes(asEsSearchType());
        return (int) ((AbstractConditionBean) cb).build(builder).execute().actionGet().getCount();
    }

    @Override
    protected <RESULT extends ENTITY> List<RESULT> delegateSelectList(final ConditionBean cb, final Class<? extends RESULT> entityType) {
        // TODO check response
        final SearchRequestBuilder builder = client.prepareSearch(asEsIndex()).setTypes(asEsSearchType());
        if (cb.isFetchScopeEffective()) {
            builder.setFrom(cb.getFetchStartIndex());
            builder.setSize(cb.getFetchSize());
        }
        ((AbstractConditionBean) cb).request().build(builder);
        final SearchResponse response = ((AbstractConditionBean) cb).build(builder).execute().actionGet();

        final EsPagingResultBean<RESULT> list = new EsPagingResultBean<>();
        final SearchHits searchHits = response.getHits();
        searchHits.forEach(hit -> {
            final Map<String, Object> source = hit.getSource();
            final RESULT entity = createEntity(source, entityType);
            final DocMeta docMeta = ((AbstractEntity) entity).asDocMeta();
            docMeta.id(hit.getId());
            docMeta.version(hit.getVersion());
            list.add(entity);
        });

        list.setAllRecordCount((int) searchHits.totalHits());
        list.setPageSize(cb.getFetchSize());
        list.setCurrentPageNumber(cb.getFetchPageNumber());

        list.setTook(response.getTookInMillis());
        list.setTotalShards(response.getTotalShards());
        list.setSuccessfulShards(response.getSuccessfulShards());
        list.setFailedShards(response.getFailedShards());
        // TODO others

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
                final Map<String, Object> source = hit.getSource();
                final RESULT entity = createEntity(source, entityType);
                final DocMeta docMeta = ((AbstractEntity) entity).asDocMeta();
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
                final Map<String, Object> source = hit.getSource();
                final RESULT entity = createEntity(source, entityType);
                final DocMeta docMeta = ((AbstractEntity) entity).asDocMeta();
                docMeta.id(hit.getId());
                docMeta.version(hit.getVersion());
                list.add(entity);
            });

            handler.handle(list);
            return !handler.isBreakCursor();
        });
    }

    protected void delegateBulkRequest(final ConditionBean cb, Function<SearchHits, Boolean> handler) {
        final SearchRequestBuilder builder =
                client.prepareSearch(asEsIndex()).setTypes(asEsIndexType()).setSearchType(SearchType.SCAN).setScroll(scrollForCursor)
                        .setSize(sizeForCursor);
        ((AbstractConditionBean) cb).request().build(builder);
        final SearchResponse response = ((AbstractConditionBean) cb).build(builder).execute().actionGet();

        String scrollId = response.getScrollId();
        while (scrollId != null) {
            final SearchResponse scrollResponse = client.prepareSearchScroll(scrollId).setScroll(scrollForDelete).execute().actionGet();
            scrollId = scrollResponse.getScrollId();
            final SearchHits searchHits = scrollResponse.getHits();
            final SearchHit[] hits = searchHits.getHits();
            if (hits.length == 0) {
                scrollId = null;
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

    @Override
    protected int delegateInsert(final Entity entity, final InsertOption<? extends ConditionBean> option) {
        final AbstractEntity esEntity = (AbstractEntity) entity;
        IndexRequestBuilder builder = createInsertRequest(esEntity);

        final IndexResponse response = builder.execute().actionGet();
        esEntity.asDocMeta().id(response.getId());
        return response.isCreated() ? 1 : 0;
    }

    protected IndexRequestBuilder createInsertRequest(final AbstractEntity esEntity) {
        final IndexRequestBuilder builder = client.prepareIndex(asEsIndex(), asEsIndexType()).setSource(esEntity.toSource());
        final RequestOptionCall<IndexRequestBuilder> indexOption = esEntity.asDocMeta().indexOption();
        if (indexOption != null) {
            indexOption.callback(builder);
        }
        return builder;
    }

    @Override
    protected int delegateUpdate(final Entity entity, final UpdateOption<? extends ConditionBean> option) {
        final AbstractEntity esEntity = (AbstractEntity) entity;
        final IndexRequestBuilder builder = createUpdateRequest(esEntity);

        final IndexResponse response = builder.execute().actionGet();
        long version = response.getVersion();
        if (version != -1) {
            esEntity.asDocMeta().version(version);
        }
        return 1;
    }

    protected IndexRequestBuilder createUpdateRequest(final AbstractEntity esEntity) {
        final IndexRequestBuilder builder =
                client.prepareIndex(asEsIndex(), asEsIndexType(), esEntity.asDocMeta().id()).setSource(esEntity.toSource());
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

    @Override
    protected int delegateDelete(final Entity entity, final DeleteOption<? extends ConditionBean> option) {
        final AbstractEntity esEntity = (AbstractEntity) entity;
        final DeleteRequestBuilder builder = createDeleteRequest(esEntity);

        final DeleteResponse response = builder.execute().actionGet();
        return response.isFound() ? 1 : 0;
    }

    protected DeleteRequestBuilder createDeleteRequest(final AbstractEntity esEntity) {
        final DeleteRequestBuilder builder = client.prepareDelete(asEsIndex(), asEsIndexType(), esEntity.asDocMeta().id());
        final RequestOptionCall<DeleteRequestBuilder> deleteOption = esEntity.asDocMeta().deleteOption();
        if (deleteOption != null) {
            deleteOption.callback(builder);
        }
        return builder;
    }

    @Override
    protected int delegateQueryDelete(final ConditionBean cb, final DeleteOption<? extends ConditionBean> option) {
        final SearchRequestBuilder builder =
                client.prepareSearch(asEsIndex()).setTypes(asEsIndexType()).setSearchType(SearchType.SCAN).setScroll(scrollForDelete)
                        .setSize(sizeForDelete);
        ((AbstractConditionBean) cb).request().build(builder);
        final SearchResponse response = ((AbstractConditionBean) cb).build(builder).execute().actionGet();

        int count = 0;
        String scrollId = response.getScrollId();
        while (scrollId != null) {
            final SearchResponse scrollResponse = client.prepareSearchScroll(scrollId).setScroll(scrollForDelete).execute().actionGet();
            scrollId = scrollResponse.getScrollId();
            final SearchHits searchHits = scrollResponse.getHits();
            final SearchHit[] hits = searchHits.getHits();
            if (hits.length == 0) {
                scrollId = null;
                break;
            }

            final BulkRequestBuilder bulkRequest = client.prepareBulk();
            for (final SearchHit hit : hits) {
                bulkRequest.add(client.prepareDelete(asEsIndex(), asEsIndexType(), hit.getId()));
            }
            count += hits.length;
            final BulkResponse bulkResponse = bulkRequest.execute().actionGet();
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

    protected <BUILDER> int[] delegateBatchRequest(final List<? extends Entity> entityList, Function<AbstractEntity, BUILDER> call) {
        final BulkList<? extends Entity> bulkList = (BulkList<? extends Entity>) entityList;
        final BulkRequestBuilder bulkBuilder = client.prepareBulk();
        for (final Entity entity : entityList) {
            final AbstractEntity esEntity = (AbstractEntity) entity;
            BUILDER builder = call.apply(esEntity);
            if (builder instanceof IndexRequestBuilder) {
                bulkBuilder.add((IndexRequestBuilder) builder);
            } else if (builder instanceof UpdateRequestBuilder) {
                bulkBuilder.add((UpdateRequestBuilder) builder);
            } else if (builder instanceof DeleteRequestBuilder) {
                bulkBuilder.add((DeleteRequestBuilder) builder);
            }
        }
        RequestOptionCall<BulkRequestBuilder> builderCall = bulkList.getCall();
        if (builderCall != null) {
            builderCall.callback(bulkBuilder);
        }
        BulkResponse response = bulkBuilder.execute().actionGet();

        List<Integer> resultList = new ArrayList<>();
        for (BulkItemResponse itemResponse : response.getItems()) {
            resultList.add(itemResponse.isFailed() ? 0 : 1);
        }
        int[] results = new int[resultList.size()];
        for (int i = 0; i < resultList.size(); i++) {
            results[i] = resultList.get(i);
        }
        return results;
    }

    protected static String toString(final Object value) {
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    protected static Short toShort(final Object value) {
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        } else if (value instanceof String) {
            return Short.parseShort(value.toString());
        } else {
            return null;
        }
    }

    protected static Integer toInteger(final Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            return Integer.parseInt(value.toString());
        } else {
            return null;
        }
    }

    protected static Long toLong(final Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            return Long.parseLong(value.toString());
        } else {
            return null;
        }
    }

    protected static Float toFloat(final Object value) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else if (value instanceof String) {
            return Float.parseFloat(value.toString());
        } else {
            return null;
        }
    }

    protected static Double toDouble(final Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            return Double.parseDouble(value.toString());
        } else {
            return null;
        }
    }

    protected static Boolean toBoolean(final Object value) {
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        } else if (value instanceof String) {
            return Boolean.parseBoolean(value.toString());
        } else {
            return null;
        }
    }

    public static class BulkList<E> implements List<E> {

        private final List<E> parent;

        private final RequestOptionCall<BulkRequestBuilder> call;

        public BulkList(final List<E> parent, final RequestOptionCall<BulkRequestBuilder> call) {
            this.parent = parent;
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
    }
}
