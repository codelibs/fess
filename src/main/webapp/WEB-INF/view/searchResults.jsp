<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%-- query matched some document --%>
<div id="subheader" class="row">
	<div class="span12">
		<p>
			<la:message key="labels.search_result_status"
				arg0="${f:h(displayQuery)}" arg1="${f:h(allRecordCount)}"
				arg2="${f:h(currentStartRecordNumber)}"
				arg3="${f:h(currentEndRecordNumber)}" />
			<c:if test="${execTime!=null}">
				<la:message key="labels.search_result_time"
					arg0="${f:h(execTime)}" />
			</c:if>
		</p>
	</div>
</div>
<c:if test="${partialResults}">
	<div class="alert">
		<p>
			<la:message key="errors.process_time_is_exceeded" />
		</p>
	</div>
</c:if>
<div id="result" class="row content">
	<input type="hidden" id="queryId" value="${f:u(queryId)}" />
	<input type="hidden" id="rt" value="${f:u(rt)}" />
	<div class="span8">
		<ol>
			<c:forEach var="doc" varStatus="s" items="${documentItems}">
				<li id="result${s.index}">
					<h3 class="title ellipsis">
						<a class="link" href="${doc.urlLink}" data-uri="${doc.urlLink}" data-id="${doc.doc_id}">${f:h(doc.contentTitle)}</a>
					</h3>
					<div class="body">
						<div class="description">${doc.contentDescription}</div>
						<div class="site ellipsis">
							<cite>${f:h(doc.sitePath)}</cite>
							<c:if test="${doc.hasCache_s_s=='true'}">
								<la:link href="/cache/?docId=${doc.doc_id}${appendHighlightQueries}" class="cache"><la:message
										key="labels.search_result_cache" /></la:link>
							</c:if>
						</div>
						<div class="more visible-phone">
							<a href="#result${s.index}"><la:message key="labels.search_result_more" /></a>
						</div>
						<div class="info">
							<c:if test="${doc.created!=null && doc.created!=''}">
								<c:set var="hasInfo" value="true"/>
								<la:message key="labels.search_result_created" />
								<fmt:formatDate value="${fe:date(doc.created)}" type="BOTH" />
							</c:if>
							<c:if test="${doc.last_modified!=null && doc.last_modified!=''}">
								<c:if test="${hasInfo}"><span class="br-phone"></span><span class="hidden-phone">-</span></c:if><c:set var="hasInfo" value="true"/>
								<la:message key="labels.search_result_last_modified" />
								<fmt:formatDate value="${fe:date(doc.last_modified)}" type="BOTH" />
							</c:if>
							<c:if test="${doc.content_length!=null && doc.content_length!=''}">
								<c:if test="${hasInfo}"><span class="br-phone"></span><span class="hidden-phone">-</span></c:if><c:set var="hasInfo" value="true"/>
								<la:message key="labels.search_result_size"
									arg0="${f:h(doc.content_length)}" />
							</c:if>
							<c:if test="${searchLogSupport}">
								<c:if test="${hasInfo}"><span class="br-phone"></span><span class="hidden-phone">-</span></c:if><c:set var="hasInfo" value="true"/>
								<la:message key="labels.search_click_count"
									arg0="${f:h(doc.click_count)}" />
							</c:if>
							<c:if test="${favoriteSupport}">
								<c:if test="${hasInfo}"><span class="br-phone"></span><span class="hidden-phone">-</span></c:if><c:set var="hasInfo" value="true"/>
								<a href="#${doc.doc_id}" class="favorite"><la:message
										key="labels.search_result_favorite" /> (${f:h(doc.favorite_count)})</a>
								<span class="favorited"><la:message
										key="labels.search_result_favorited"/> <span class="favorited-count">(${f:h(doc.favoriteCount_l_x_dv)})</span></span>
							</c:if>
						</div>
					</div>
				</li>
			</c:forEach>
		</ol>
	</div>
	<div class="span4 visible-desktop visible-tablet">
		<%-- Side Content --%>
		<c:if test="${screenShotSupport}">
			<div id="screenshot"></div>
		</c:if>
		<c:if test="${facetResponse != null}">
			<div class="well span3">
				<ul class="nav nav-list">
					<c:forEach var="fieldData" items="${facetResponse.fieldList}">
						<c:if test="${fieldData.name == 'label' && fieldData.valueCountMap.size() > 0}">
					<li class="nav-header"><la:message key="label.facet_label_title" /></li>
							<c:forEach var="countEntry" items="${fieldData.valueCountMap}">
								<c:if test="${countEntry.value != 0 && fe:labelexists(countEntry.key)}">
					<li><la:link
							href="/search/search?query=${f:u(query)}&additional=label:${f:u(countEntry.key)}${pagingQuery}${fe:facetQuery()}${fe:geoQuery()}">
							${f:h(fe:label(countEntry.key))} (${f:h(countEntry.value)})</la:link></li>
								</c:if>
							</c:forEach>
						</c:if>
					</c:forEach>
					<c:forEach var="facetQueryView" items="${fe:facetQueryViewList()}">
					<li class="nav-header"><la:message key="${facetQueryView.title}" /></li>
						<c:forEach var="queryEntry" items="${facetQueryView.queryMap}">
								<c:if test="${facetResponse.queryCountMap[queryEntry.value] != 0}">
					<li><la:link
							href="/search/search?query=${f:u(query)}&additional=${f:u(queryEntry.value)}${pagingQuery}${fe:facetQuery()}${fe:geoQuery()}">
							<la:message key="${queryEntry.key}" /> (${f:h(facetResponse.queryCountMap[queryEntry.value])})</la:link></li>
								</c:if>
						</c:forEach>
					</c:forEach>
				</ul>
				<c:if test="${!empty additional}">
				<ul class="nav nav-list">
					<li class="reset">
						<la:link
							href="/search/search?query=${f:u(query)}"><la:message key="label.facet_label_reset" /></la:link>
					</li>
				</ul>
				</c:if>
			</div>
		</c:if>
	</div>
</div>
<div class="row center">
	<div id="subfooter" class="pagination">
		<ul>
			<c:if test="${existPrevPage}">
				<li class="prev"><la:link
						href="/search/prev?query=${f:u(query)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}${pagingQuery}${fe:facetQuery()}${fe:geoQuery()}">
						<la:message key="labels.prev_page" />
					</la:link></li>
			</c:if>
			<c:if test="${!existPrevPage}">
				<li class="prev disabled"><a href="#"><la:message
							key="labels.prev_page" /></a></li>
			</c:if>
			<c:forEach var="pageNumber" varStatus="s" items="${pageNumberList}">
				<li
					<c:if test="${pageNumber < currentPageNumber - 2 || pageNumber > currentPageNumber + 2}">class="hidden-phone"</c:if>
					<c:if test="${pageNumber == currentPageNumber && pageNumber >= currentPageNumber - 2 && pageNumber <= currentPageNumber + 2}">class="active"</c:if>
					>
					<la:link
						href="/search/move?query=${f:u(query)}&pn=${f:u(pageNumber)}&num=${f:u(pageSize)}${pagingQuery}${fe:facetQuery()}${fe:geoQuery()}">${f:h(pageNumber)}</la:link>
				</li>
			</c:forEach>
			<c:if test="${existNextPage}">
				<li class="next"><la:link
						href="/search/next?query=${f:u(query)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}${pagingQuery}${fe:facetQuery()}${fe:geoQuery()}">
						<la:message key="labels.next_page" />
					</la:link></li>
			</c:if>
			<c:if test="${!existNextPage}">
				<li class="next disabled"><a href="#"><la:message
							key="labels.next_page" /></a></li>
			</c:if>
		</ul>
	</div>
</div>
