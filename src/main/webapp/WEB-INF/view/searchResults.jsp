<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%-- query matched some document --%>
<div id="subheader" class="row">
	<div class="col-md-12">
		<p>
			<la:message key="labels.search_result_status"
				arg0="${f:h(displayQuery)}" arg1="${f:h(allRecordCount)}"
				arg2="${f:h(currentStartRecordNumber)}"
				arg3="${f:h(currentEndRecordNumber)}" />
			<c:if test="${execTime!=null}">
				<la:message key="labels.search_result_time" arg0="${f:h(execTime)}" />
			</c:if>
		</p>
	</div>
</div>
<c:if test="${partialResults}">
	<div class="alert">
		<p>
			<la:message key="labels.process_time_is_exceeded" />
		</p>
	</div>
</c:if>
<div id="result" class="row content">
	<input type="hidden" id="queryId" value="${f:u(queryId)}" /> <input
		type="hidden" id="rt" value="${f:u(requestedTime)}" />
	<div class="col-md-8">
		<ol>
			<c:forEach var="doc" varStatus="s" items="${documentItems}">
				<li id="result${s.index}">
					<h3 class="title ellipsis">
						<a class="link" href="${doc.urlLink}" data-uri="${doc.urlLink}"
							data-id="${doc.doc_id}" data-order="${s.index}">${f:h(doc.contentTitle)}</a>
					</h3>
					<div class="body">
						<div class="description">${doc.contentDescription}</div>
						<div class="site ellipsis">
							<cite>${f:h(doc.sitePath)}</cite>
							<c:if test="${doc.has_cache=='true'}">
								<small class="hidden-md-down"> <la:link
										href="/cache/?docId=${doc.doc_id}${appendHighlightParams}"
										class="cache">
										<la:message key="labels.search_result_cache" />
									</la:link>
								</small>
							</c:if>
						</div>
						<div class="more hidden-md-up">
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
										<span class="br-phone"></span>
										<span class="hidden-phone">-</span>
									</c:if>
									<c:set var="hasInfo" value="true" />
									<la:message key="labels.search_result_last_modified" />
									<fmt:formatDate value="${fe:parseDate(doc.last_modified)}"
										type="BOTH" pattern="yyyy-MM-dd HH:mm" />
								</c:if> <c:if
									test="${doc.content_length!=null && doc.content_length!=''}">
									<c:if test="${hasInfo}">
										<span class="br-phone"></span>
										<span class="hidden-phone">-</span>
									</c:if>
									<c:set var="hasInfo" value="true" />
									<la:message key="labels.search_result_size"
										arg0="${fe:formatNumber(doc.content_length)}" />
								</c:if> <c:if test="${searchLogSupport}">
									<c:if test="${hasInfo}">
										<span class="br-phone"></span>
										<span class="hidden-phone">-</span>
									</c:if>
									<c:set var="hasInfo" value="true" />
									<la:message key="labels.search_click_count"
										arg0="${f:h(doc.click_count)}" />
								</c:if> <c:if test="${favoriteSupport}">
									<c:if test="${hasInfo}">
										<span class="br-phone"></span>
										<span class="hidden-phone">-</span>
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
					</div>
				</li>
			</c:forEach>
		</ol>
	</div>
	<aside class="col-md-4 hidden-sm-down">
		<%-- Side Content --%>
		<c:if test="${screenShotSupport}">
			<div id="screenshot"></div>
		</c:if>
		<c:if test="${facetResponse != null}">
			<c:forEach var="fieldData" items="${facetResponse.fieldList}">
				<c:if
					test="${fieldData.name == 'label' && fieldData.valueCountMap.size() > 0}">
					<ul class="list-group m-b">
						<li class="list-group-item text-uppercase"><la:message
								key="labels.facet_label_title" /></li>
						<c:forEach var="countEntry" items="${fieldData.valueCountMap}">
							<c:if
								test="${countEntry.value != 0 && fe:labelexists(countEntry.key)}">
								<li class="list-group-item"><la:link
										href="/search/search?q=${f:u(q)}&ex_q=label%3a${f:u(countEntry.key)}${fe:pagingQuery(null)}${fe:facetQuery()}${fe:geoQuery()}">
											${f:h(fe:label(countEntry.key))} 
											<span class="label label-default label-pill pull-right">${f:h(countEntry.value)}</span>
									</la:link></li>
							</c:if>
						</c:forEach>
					</ul>
				</c:if>
			</c:forEach>
			<c:forEach var="facetQueryView" items="${fe:facetQueryViewList()}">
				<ul class="list-group m-b">
					<li class="list-group-item text-uppercase"><la:message
							key="${facetQueryView.title}" /></li>
					<c:forEach var="queryEntry" items="${facetQueryView.queryMap}">
						<c:if test="${facetResponse.queryCountMap[queryEntry.value] != 0}">
							<li class="list-group-item p-l-md"><la:link
									href="/search/search?q=${f:u(q)}&ex_q=${f:u(queryEntry.value)}${fe:pagingQuery(queryEntry.value)}${fe:facetQuery()}${fe:geoQuery()}">
									<la:message key="${queryEntry.key}" />
									<span class="label label-default label-pill pull-right">${f:h(facetResponse.queryCountMap[queryEntry.value])}</span>
								</la:link></li>
						</c:if>
					</c:forEach>
				</ul>
			</c:forEach>
			<c:if test="${!empty ex_q}">
				<div class="pull-right">
					<la:link href="/search/search?q=${f:u(q)}"
						styleClass="btn btn-secondary btn-sm">
						<la:message key="labels.facet_label_reset" />
					</la:link>
				</div>
			</c:if>
		</c:if>
	</aside>
</div>
<div class="row center">
	<nav id="subfooter">
		<ul class="pagination">
			<c:if test="${existPrevPage}">
				<li class="prev"><la:link aria-label="Previous"
						href="/search/prev?q=${f:u(q)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}${fe:pagingQuery(null)}${fe:facetQuery()}${fe:geoQuery()}">
						<span aria-hidden="true">&laquo;</span>
						<span class="sr-only"><la:message key="labels.prev_page" /></span>
					</la:link></li>
			</c:if>
			<c:if test="${!existPrevPage}">
				<li class="prev disabled" aria-label="Previous"><a href="#">
						<span aria-hidden="true">&laquo;</span> <span class="sr-only"><la:message
								key="labels.prev_page" /></span>
				</a></li>
			</c:if>
			<c:forEach var="pageNumber" varStatus="s" items="${pageNumberList}">
				<li
					<c:if test="${pageNumber < currentPageNumber - 2 || pageNumber > currentPageNumber + 2}">class="hidden-phone"</c:if>
					<c:if test="${pageNumber == currentPageNumber && pageNumber >= currentPageNumber - 2 && pageNumber <= currentPageNumber + 2}">class="active"</c:if>>
					<la:link
						href="/search/move?q=${f:u(q)}&pn=${f:u(pageNumber)}&num=${f:u(pageSize)}${fe:pagingQuery(null)}${fe:facetQuery()}${fe:geoQuery()}">${f:h(pageNumber)}</la:link>
				</li>
			</c:forEach>
			<c:if test="${existNextPage}">
				<li class="next"><la:link aria-label="Next"
						href="/search/next?q=${f:u(q)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}${fe:pagingQuery(null)}${fe:facetQuery()}${fe:geoQuery()}">
						<span class="sr-only"><la:message key="labels.next_page" /></span>
						<span aria-hidden="true">&raquo;</span>
					</la:link></li>
			</c:if>
			<c:if test="${!existNextPage}">
				<li class="next disabled" aria-label="Next"><a href="#"> <span
						class="sr-only"><la:message key="labels.next_page" /></span> <span
						aria-hidden="true">&raquo;</span>
				</a></li>
			</c:if>
		</ul>
	</nav>
</div>
