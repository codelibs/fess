<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%-- query matched some document --%>
<div id="subheader" class="fads-row">
	<div class="fads-col">
		<p>
			<c:if test="${allRecordCountRelation=='EQUAL_TO'}">
				<la:message key="labels.search_result_status"
					arg0="${displayQuery}" arg1="${fe:formatNumber(allRecordCount,'###,###')}"
					arg2="${f:h(currentStartRecordNumber)}"
					arg3="${f:h(currentEndRecordNumber)}" />
			</c:if><c:if test="${allRecordCountRelation!='EQUAL_TO'}">
				<la:message key="labels.search_result_status_over"
					arg0="${displayQuery}" arg1="${fe:formatNumber(allRecordCount,'###,###')}"
					arg2="${f:h(currentStartRecordNumber)}"
					arg3="${f:h(currentEndRecordNumber)}" />
			</c:if>
			<c:if test="${execTime!=null}">
				<la:message key="labels.search_result_time" arg0="${f:h(execTime)}" />
			</c:if>
		</p>
		<c:if test="${! empty sdh }">
		<p>
			<la:message key="labels.similar_doc_result_status" />
		</p>
		</c:if>
	</div>
</div>
<c:if test="${partialResults}">
	<div class="fads-banner">
		<p>
			<la:message key="labels.process_time_is_exceeded" />
		</p>
	</div>
</c:if>
<div id="result" class="fads-row">
	<input type="hidden" id="queryId" value="${f:h(queryId)}" /> <input
		type="hidden" id="rt" value="${f:h(requestedTime)}" />
	<ol class="fads-list-unstyled fads-col-md-8">
		<c:forEach var="doc" varStatus="s" items="${documentItems}">
			<li id="result${s.index}">
				<h3 class="title fads-text-truncate">
					<a class="link" href="${doc.url_link}" data-uri="${doc.url_link}"
						data-id="${doc.doc_id}" data-order="${s.index}">${doc.content_title}</a>
				</h3>
				<div class="body">
					<c:if test="${thumbnailSupport && !empty doc.thumbnail}">
					<div class="fads-me-3">
						<a class="link fads-d-none fads-d-sm-flex" href="${doc.url_link}" data-uri="${doc.url_link}" data-id="${doc.doc_id}"
							data-order="${s.index}"
						> <img src="${fe:url('/images/blank.png')}" alt="thumbnail"
							data-src="${fe:url('/thumbnail/')}?docId=${f:u(doc.doc_id)}&queryId=${f:u(queryId)}" class="thumbnail"
						>
						</a>
					</div>
					</c:if>
					<div class="description">${doc.content_description}</div>
				</div>
				<div class="site fads-text-truncate">
					<c:if test="${clipboardCopyIcon}"><i class="far fa-copy url-copy fads-d-print-none" data-clipboard-text="${doc.url_link}" aria-hidden="true"></i></c:if>
					<cite>${f:h(doc.site_path)}</cite>
				</div>
				<div class="more">
					<a href="#result${s.index}"><la:message
							key="labels.search_result_more" /></a>
				</div>
				<div class="info">
					<fmt:formatDate value="${fe:parseDate(doc.last_modified)}" type="BOTH" pattern="yyyy-MM-dd HH:mm" />
					<c:if test="${doc.last_modified==null || doc.last_modified==''}">
						<fmt:formatDate value="${fe:parseDate(doc.created)}" type="BOTH" pattern="yyyy-MM-dd HH:mm" />
					</c:if>
					<c:if test="${doc.content_length!=null && doc.content_length!=''}">
						<div class="fads-d-sm-none"></div>
						<span class="fads-d-none fads-d-sm-inline-block">&nbsp;</span>
						<la:message key="labels.search_result_size"
							arg0="${fe:formatFileSize(doc.content_length)}" />
					</c:if>
					<c:if test="${searchLogSupport && doc.click_count!=null && doc.click_count>0}">
						<div class="fads-d-sm-none"></div>
						<span class="fads-d-none fads-d-sm-inline-block">&nbsp;</span>
						<la:message key="labels.search_click_views"
							arg0="${f:h(doc.click_count)}" />
					</c:if>
					<c:if test="${doc.has_cache=='true'}">
						<div class="fads-d-sm-none"></div>
						<span class="fads-d-none fads-d-sm-inline-block">&nbsp;</span>
						<la:link href="/cache/?docId=${doc.doc_id}${appendHighlightParams}"
								styleClass="cache fads-d-print-none">
							<la:message key="labels.search_result_cache" />
						</la:link>
					</c:if>
					<c:if test="${doc.similar_docs_count!=null&&doc.similar_docs_count>1}">
						<div class="fads-d-sm-none"></div>
						<span class="fads-d-none fads-d-sm-inline-block">&nbsp;</span>
						<la:link href="/search?q=${f:u(q)}&ex_q=${f:u(queryEntry.value)}&sdh=${f:u(fe:sdh(doc.similar_docs_hash))}${fe:facetQuery()}${fe:geoQuery()}">
							<la:message key="labels.search_result_similar"
										arg0="${fe:formatFileSize(doc.similar_docs_count-1)}" />
						</la:link>
					</c:if>
					<c:if test="${favoriteSupport}">
						<div class="fads-d-sm-none"></div>
						<span class="fads-d-none fads-d-sm-inline-block">&nbsp;</span>
						<a href="#${doc.doc_id}" class="favorite"><i class="far fa-star" aria-hidden="true"></i></a>
						<span class="favorited"><i class="fas fa-star" aria-hidden="true"></i></span>
					</c:if>
				</div>
			</li>
		</c:forEach>
	</ol>
	<aside class="fads-col-md-4 fads-d-none fads-d-md-block">
		<%-- Side Content --%>
		<c:if test="${facetResponse != null}">
			<c:forEach var="fieldData" items="${facetResponse.fieldList}">
				<c:if
					test="${fieldData.name == 'label' && fieldData.valueCountMap.size() > 0}">
					<ul class="fads-list-group fads-mb-2">
						<li class="fads-list-group-item fads-text-uppercase"><la:message
								key="labels.facet_label_title" /></li>
						<c:forEach var="countEntry" items="${fieldData.valueCountMap}">
							<c:if
								test="${countEntry.value != 0 && fe:labelexists(countEntry.key)}">
								<li class="fads-list-group-item"><la:link
										href="/search?q=${f:u(q)}&ex_q=label%3a${f:u(countEntry.key)}&sdh=${f:u(fe:sdh(sh))}${fe:pagingQuery(null)}${fe:facetQuery()}${fe:geoQuery()}">
											${f:h(fe:label(countEntry.key))}
											<span class="fads-lozenge fads-lozenge-secondary fads-float-end">${f:h(countEntry.value)}</span>
									</la:link></li>
							</c:if>
						</c:forEach>
					</ul>
				</c:if>
			</c:forEach>
			<c:forEach var="facetQueryView" items="${fe:facetQueryViewList()}">
				<ul class="fads-list-group fads-mb-2">
					<li class="fads-list-group-item fads-text-uppercase"><la:message
							key="${facetQueryView.title}" /></li>
					<c:set var="facetFound" value="F"/>
					<c:forEach var="queryEntry" items="${facetQueryView.queryMap}">
						<c:if test="${facetResponse.queryCountMap[queryEntry.value] > 0}">
							<li class="fads-list-group-item"><la:link
									href="/search?q=${f:u(q)}&ex_q=${f:u(queryEntry.value)}&sdh=${f:u(fe:sdh(sdh))}${fe:pagingQuery(queryEntry.value)}${fe:facetQuery()}${fe:geoQuery()}">
									<c:if test="${fn:startsWith(queryEntry.key, 'labels.')}"><la:message key="${queryEntry.key}" /></c:if>
									<c:if test="${not fn:startsWith(queryEntry.key, 'labels.')}">${f:h(queryEntry.key)}</c:if>
									<span class="fads-lozenge fads-lozenge-secondary fads-float-end">${f:h(facetResponse.queryCountMap[queryEntry.value])}</span>
								</la:link></li>
							<c:set var="facetFound" value="T"/>
						</c:if>
					</c:forEach>
					<c:if test="${facetFound == 'F'}">
						<li class="fads-list-group-item"><la:message key="labels.facet_is_not_found" /></li>
					</c:if>
				</ul>
			</c:forEach>
			<c:if test="${!empty ex_q}">
				<div class="fads-d-flex fads-justify-content-end">
					<la:link href="/search?q=${f:u(q)}"
						styleClass="fads-btn fads-btn-link fads-btn-sm">
						<la:message key="labels.facet_label_reset" />
					</la:link>
				</div>
			</c:if>
		</c:if>
	</aside>
</div>
<div class="fads-row">
	<nav id="subfooter" style="margin:0 auto">
		<ul class="fads-pagination fads-justify-content-center">
			<c:if test="${existPrevPage}">
				<li class="fads-page-item"><la:link styleClass="fads-page-link" aria-label="Previous"
						href="/search/prev?q=${f:u(q)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}&sdh=${f:u(fe:sdh(sdh))}${fe:pagingQuery(null)}${fe:facetQuery()}${fe:geoQuery()}">
						<span aria-hidden="true">&laquo;</span>
						<span class="fads-visually-hidden"><la:message key="labels.prev_page" /></span>
					</la:link></li>
			</c:if>
			<c:if test="${!existPrevPage}">
				<li class="fads-page-item disabled" aria-label="Previous"><a class="fads-page-link" href="#">
						<span aria-hidden="true">&laquo;</span> <span class="fads-visually-hidden"><la:message
								key="labels.prev_page" /></span>
				</a></li>
			</c:if>
			<c:forEach var="pageNumber" varStatus="s" items="${pageNumberList}">
				<li
					<c:choose>
						<c:when test="${pageNumber < currentPageNumber - 2 || pageNumber > currentPageNumber + 2}">class="fads-page-item fads-d-none fads-d-sm-inline-block"</c:when>
						<c:when test="${pageNumber == currentPageNumber && pageNumber >= currentPageNumber - 2 && pageNumber <= currentPageNumber + 2}">class="fads-page-item active"</c:when>
						<c:otherwise>class="fads-page-item"</c:otherwise>
					</c:choose>>
					<la:link styleClass="fads-page-link"
						href="/search/move?q=${f:u(q)}&pn=${f:u(pageNumber)}&num=${f:u(pageSize)}&sdh=${f:u(fe:sdh(sdh))}${fe:pagingQuery(null)}${fe:facetQuery()}${fe:geoQuery()}">${f:h(pageNumber)}</la:link>
				</li>
			</c:forEach>
			<c:if test="${existNextPage}">
				<li class="fads-page-item"><la:link styleClass="fads-page-link" aria-label="Next"
						href="/search/next?q=${f:u(q)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}&sdh=${f:u(fe:sdh(sdh))}${fe:pagingQuery(null)}${fe:facetQuery()}${fe:geoQuery()}">
						<span class="fads-visually-hidden"><la:message key="labels.next_page" /></span>
						<span aria-hidden="true">&raquo;</span>
					</la:link></li>
			</c:if>
			<c:if test="${!existNextPage}">
				<li class="fads-page-item disabled" aria-label="Next"><a class="fads-page-link" href="#"> <span
						class="fads-visually-hidden"><la:message key="labels.next_page" /></span> <span
						aria-hidden="true">&raquo;</span>
				</a></li>
			</c:if>
		</ul>
	</nav>
</div>