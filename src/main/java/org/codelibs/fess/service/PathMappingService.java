/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.service;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.client.FessEsClient;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.entity.PathMapping;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.pager.PathMappingPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.seasar.framework.beans.util.Beans;

public class PathMappingService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Resource
	protected FessEsClient fessEsClient;

	@Resource
	protected FieldHelper fieldHelper;

	public List<PathMapping> getPathMappingList(final PathMappingPager pathMappingPager) {

		final PagingResultBean<PathMapping> pathMappingList = 
				fessEsClient.search(getIndex(),getType(),searchRequestBuilder -> {
					if (pathMappingPager.id != null) {
						searchRequestBuilder.setQuery(QueryBuilders.idsQuery(getType()).addIds(pathMappingPager.id));
					}
					searchRequestBuilder.addSort(SortBuilders.fieldSort("sortOrder").order(SortOrder.ASC));
					searchRequestBuilder.setVersion(true);

					final int size = pathMappingPager.getPageSize();
					final int pageNum = pathMappingPager.getCurrentPageNumber();
					// TODO modify size/pageNum
						searchRequestBuilder.setFrom(size * (pageNum - 1));
						searchRequestBuilder.setSize(size);
						return true;
					}, (searchRequestBuilder, execTime, searchResponse) -> {
						return searchResponse.map(response -> {
							final PagingResultBean<PathMapping> list = new PagingResultBean<>();
							list.setTableDbName(getType());
							final SearchHits searchHits = response
									.getHits();
							searchHits.forEach(hit -> {
								list.add(createEntity(response, hit));
							});
							list.setAllRecordCount((int) searchHits.totalHits());
							list.setPageSize(pathMappingPager.getPageSize());
							list.setCurrentPageNumber(pathMappingPager.getCurrentPageNumber());
							return list;
						}).orElseGet(() -> {
							final PagingResultBean<PathMapping> emptyList = new PagingResultBean<>();
							emptyList.setTableDbName(getType());
							emptyList.setAllRecordCount(0);
							emptyList.setPageSize(pathMappingPager.getPageSize());
							emptyList.setCurrentPageNumber(1);
							return emptyList;
						});
					});

		// update pager
		Beans.copy(pathMappingList, pathMappingPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
		pathMappingPager.setPageNumberList(pathMappingList.pageRange(op -> {
			op.rangeSize(5);
		}).createPageNumberList());

		return pathMappingList;
	}
	
	public PathMapping getPathMapping(final String id) {
		return fessEsClient.getDocument(getIndex(), getType(), searchRequestBuilder -> {
			searchRequestBuilder.setQuery(QueryBuilders.idsQuery(getType()).addIds(id));
			searchRequestBuilder.setVersion(true);
			return true;
		}, this::createEntity).get();
	}

	protected String getType() {
		return fieldHelper.pathMappingType;
	}
	
	protected String getIndex() {
		return fieldHelper.configIndex;
	}
	
	public void store(final PathMapping pathMapping) throws CrudMessageException {
		fessEsClient.store(getIndex(), getType(), pathMapping);
	}
	
	public void delete(final PathMapping pathMapping) throws CrudMessageException {
		fessEsClient.delete(getIndex(), getType(), pathMapping.getId(), pathMapping.getVersion());
	}
	
	public List<PathMapping> getAvailablePathMappingList() {
		return fessEsClient.getDocumentList(getIndex(), getType(), searchRequestBuilder -> {
			return true;
		}, this::createEntity);
	}

	protected void setupDeleteCondition(final PathMapping pathMapping) {

		// setup condition

	}
	
	protected PathMapping createEntity(SearchResponse response, SearchHit hit) {
		final PathMapping pathMapping = BeanUtil.copyMapToNewBean(hit.getSource(), PathMapping.class);
		pathMapping.setId(hit.getId());
		pathMapping.setVersion(hit.getVersion());
		return pathMapping;
	}

}
