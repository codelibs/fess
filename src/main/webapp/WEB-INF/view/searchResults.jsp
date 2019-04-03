<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%-- query matched some document --%>
<div id="subheader" class="row">
	<div class="col">
		<p>
			<la:message key="labels.search_result_status"
				arg0="${displayQuery}" arg1="${f:h(allRecordCount)}"
				arg2="${f:h(currentStartRecordNumber)}"
				arg3="${f:h(currentEndRecordNumber)}" />
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
	<div class="alert">
		<p>
			<la:message key="labels.process_time_is_exceeded" />
		</p>
	</div>
</c:if>
<div id="result" class="row">
	<input type="hidden" id="queryId" value="${f:u(queryId)}" /> <input
		type="hidden" id="rt" value="${f:u(requestedTime)}" />
	<ol class="list-unstyled col-md-8">
		<c:forEach var="doc" varStatus="s" items="${documentItems}">
			<li id="result${s.index}">
				<h3 class="title text-truncate">
					<a class="link" href="${doc.url_link}" data-uri="${doc.url_link}"
						data-id="${doc.doc_id}" data-order="${s.index}">${doc.content_title}</a>
				</h3>
				<div class="body">
					<c:if test="${thumbnailSupport && !empty doc.thumbnail}">
					<div class="mr-3">
						<a class="link d-none d-sm-flex" href="${doc.url_link}" data-uri="${doc.url_link}" data-id="${doc.doc_id}"
							data-order="${s.index}"
						> <img src="${fe:url('/images/blank.png')}"
							data-src="${fe:url('/thumbnail/')}?docId=${f:u(doc.doc_id)}&queryId=${f:u(queryId)}" class="thumbnail"
						>
						</a>
					</div>
					</c:if>
					<div class="description">${doc.content_description}</div>
				</div>
				<div class="site text-truncate">
					<cite>${f:h(doc.site_path)}</cite>
					<c:if test="${doc.has_cache=='true'}">
						<small class="d-none d-lg-inline-block"> <la:link
								href="/cache/?docId=${doc.doc_id}${appendHighlightParams}"
								class="cache">
								<la:message key="labels.search_result_cache" />
							</la:link>
						</small>
					</c:if>
					<c:if test="${doc.similar_docs_count!=null&&doc.similar_docs_count>1}">
						<small class="d-none d-lg-inline-block"> <la:link
								href="/search?q=${f:u(q)}&ex_q=${f:u(queryEntry.value)}&sdh=${f:u(fe:sdh(doc.similar_docs_hash))}${fe:facetQuery()}${fe:geoQuery()}">
								<la:message key="labels.search_result_similar"
											arg0="${fe:formatNumber(doc.similar_docs_count-1)}" />
							</la:link>
						</small>
					</c:if>
				</div>
				<div class="more">
					<a href="#result${s.index}"><la:message
							key="labels.search_result_more" /></a>
				</div>
				<div class="info">
					<small> <c:if
							test="${doc.created!=null && doc.created!=''}">
							<c:set var="hasInfo" value="true" />
							<la:message key="labels.search_result_created" />
							<fmt:formatDate value="${fe:parseDate(doc.created)}"
								type="BOTH" pattern="yyyy-MM-dd HH:mm" />
						</c:if> <c:if
							test="${doc.last_modified!=null && doc.last_modified!=''}">
							<c:if test="${hasInfo}">
								<div class="d-sm-none"></div>
								<span class="d-none d-sm-inline">-</span>
							</c:if>
							<c:set var="hasInfo" value="true" />
							<la:message key="labels.search_result_last_modified" />
							<fmt:formatDate value="${fe:parseDate(doc.last_modified)}"
								type="BOTH" pattern="yyyy-MM-dd HH:mm" />
						</c:if> <c:if
							test="${doc.content_length!=null && doc.content_length!=''}">
							<c:if test="${hasInfo}">
								<div class="d-sm-none"></div>
								<span class="d-none d-sm-inline">-</span>
							</c:if>
							<c:set var="hasInfo" value="true" />
							<la:message key="labels.search_result_size"
								arg0="${fe:formatNumber(doc.content_length)}" />
						</c:if> <c:if test="${searchLogSupport}">
							<c:if test="${hasInfo}">
								<div class="d-sm-none"></div>
								<span class="d-none d-sm-inline">-</span>
							</c:if>
							<c:set var="hasInfo" value="true" />
							<la:message key="labels.search_click_count"
								arg0="${f:h(doc.click_count)}" />
						</c:if> <c:if test="${favoriteSupport}">
							<c:if test="${hasInfo}">
								<div class="d-sm-none"></div>
								<span class="d-none d-sm-inline">-</span>
							</c:if>
							<c:set var="hasInfo" value="true" />
							<a href="#${doc.doc_id}" class="favorite"><la:message
									key="labels.search_result_favorite" />
								(${f:h(doc.favorite_count)})</a>
							<span class="favorited"><la:message
									key="labels.search_result_favorited" /> <span
								class="favorited-count">(${f:h(doc.favorite_count)})</span></span>
						</c:if>
					</small>
				</div>
			</li>
		</c:forEach>
	</ol>
	<aside class="col-md-4 d-none d-md-block">
		<%-- Side Content --%>
		<c:if test="${facetResponse != null}">
			<c:forEach var="fieldData" items="${facetResponse.fieldList}">
				<c:if
					test="${fieldData.name == 'label' && fieldData.valueCountMap.size() > 0}">
					<ul class="list-group mb-2">
						<li class="list-group-item text-uppercase"><la:message
								key="labels.facet_label_title" /></li>
						<c:forEach var="countEntry" items="${fieldData.valueCountMap}">
							<c:if
								test="${countEntry.value != 0 && fe:labelexists(countEntry.key)}">
								<li class="list-group-item"><la:link
										href="/search?q=${f:u(q)}&ex_q=label%3a${f:u(countEntry.key)}&sdh=${f:u(fe:sdh(sh))}${fe:pagingQuery(null)}${fe:facetQuery()}${fe:geoQuery()}">
											${f:h(fe:label(countEntry.key))} 
											<span class="badge badge-secondary badge-pill float-right">${f:h(countEntry.value)}</span>
									</la:link></li>
							</c:if>
						</c:forEach>
					</ul>
				</c:if>
			</c:forEach>
			<c:forEach var="facetQueryView" items="${fe:facetQueryViewList()}">
				<ul class="list-group mb-2">
					<li class="list-group-item text-uppercase"><la:message
							key="${facetQueryView.title}" /></li>
					<c:set var="facetFound" value="F"/>
					<c:forEach var="queryEntry" items="${facetQueryView.queryMap}">
						<c:if test="${facetResponse.queryCountMap[queryEntry.value] != 0}">
							<li class="list-group-item"><la:link
									href="/search?q=${f:u(q)}&ex_q=${f:u(queryEntry.value)}&sdh=${f:u(fe:sdh(sdh))}${fe:pagingQuery(queryEntry.value)}${fe:facetQuery()}${fe:geoQuery()}">
									<la:message key="${queryEntry.key}" />
									<span class="badge badge-secondary badge-pill float-right">${f:h(facetResponse.queryCountMap[queryEntry.value])}</span>
								</la:link></li>
							<c:set var="facetFound" value="T"/>
						</c:if>
					</c:forEach>
					<c:if test="${facetFound == 'F'}">
						<li class="list-group-item"><la:message key="labels.facet_is_not_found" /></li>
					</c:if>
				</ul>
			</c:forEach>
			<c:if test="${!empty ex_q}">
				<div class="float-right">
					<la:link href="/search?q=${f:u(q)}"
						styleClass="btn btn-link btn-sm">
						<la:message key="labels.facet_label_reset" />
					</la:link>
				</div>
			</c:if>
		</c:if>
	</aside>
</div>
<div class="row">
	<nav id="subfooter" class="mx-auto">
		<ul class="pagination">
			<c:if test="${existPrevPage}">
				<li class="page-item"><la:link styleClass="page-link" aria-label="Previous"
						href="/search/prev?q=${f:u(q)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}&sdh=${f:u(fe:sdh(sdh))}${fe:pagingQuery(null)}${fe:facetQuery()}${fe:geoQuery()}">
						<span aria-hidden="true">&laquo;</span>
						<span class="sr-only"><la:message key="labels.prev_page" /></span>
					</la:link></li>
			</c:if>
			<c:if test="${!existPrevPage}">
				<li class="page-item disabled" aria-label="Previous"><a class="page-link" href="#">
						<span aria-hidden="true">&laquo;</span> <span class="sr-only"><la:message
								key="labels.prev_page" /></span>
				</a></li>
			</c:if>
			<c:forEach var="pageNumber" varStatus="s" items="${pageNumberList}">
				<li
					<c:choose>
						<c:when test="${pageNumber < currentPageNumber - 2 || pageNumber > currentPageNumber + 2}">class="page-item d-none d-sm-inline"</c:when>
						<c:when test="${pageNumber == currentPageNumber && pageNumber >= currentPageNumber - 2 && pageNumber <= currentPageNumber + 2}">class="page-item active"</c:when>
						<c:otherwise>class="page-item"</c:otherwise>
					</c:choose>>
					<la:link styleClass="page-link"
						href="/search/move?q=${f:u(q)}&pn=${f:u(pageNumber)}&num=${f:u(pageSize)}&sdh=${f:u(fe:sdh(sdh))}${fe:pagingQuery(null)}${fe:facetQuery()}${fe:geoQuery()}">${f:h(pageNumber)}</la:link>
				</li>
			</c:forEach>
			<c:if test="${existNextPage}">
				<li class="page-item"><la:link styleClass="page-link" aria-label="Next"
						href="/search/next?q=${f:u(q)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}&sdh=${f:u(fe:sdh(sdh))}${fe:pagingQuery(null)}${fe:facetQuery()}${fe:geoQuery()}">
						<span class="sr-only"><la:message key="labels.next_page" /></span>
						<span aria-hidden="true">&raquo;</span>
					</la:link></li>
			</c:if>
			<c:if test="${!existNextPage}">
				<li class="page-item disabled" aria-label="Next"><a class="page-link" href="#"> <span
						class="sr-only"><la:message key="labels.next_page" /></span> <span
						aria-hidden="true">&raquo;</span>
				</a></li>
			</c:if>
		</ul>
	</nav>
</div>
