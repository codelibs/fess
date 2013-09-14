<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.search_list_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="searchList" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

		<div id="header">
			<s:form action="search" method="get">
				<div class="input">
					<html:text styleClass="query" property="query" title="Search"
						size="50" maxlength="1000" />
					<input class="btn" type="submit"
						value="<bean:message key="labels.search"/>" name="search" />
				</div>
			</s:form>
		</div>
		<c:choose>
			<c:when test="${allRecordCount == null}">
				<div id="subheader"></div>
				<div id="result">
					<p>
						<bean:message key="labels.search_list_index_page" />
					</p>
				</div>
			</c:when>
			<c:when test="${f:h(allRecordCount) != 0}">
				<div id="subheader">
					<p>
						<bean:message key="labels.search_result_status"
							arg0="${f:h(query)}" arg1="${f:h(allRecordCount)}"
							arg2="${f:h(currentStartRecordNumber)}"
							arg3="${f:h(currentEndRecordNumber)}" />
						<c:if test="${execTime!=null}">
							<bean:message key="labels.search_result_time"
								arg0="${f:h(execTime)}" />
						</c:if>
					</p>
				</div>

				<div id="result">
					<div>
						<ol>
							<c:forEach var="doc" varStatus="s" items="${documentItems}">
								<li>
									<h3 class="title">
										<a href="${doc.urlLink}">${f:h(doc.contentTitle)}</a>
									</h3>
									<div class="body">
										${doc.contentDescription}
										<div style="text-align: right;">
											<c:if test="${!solrProcessRunning}">
												<html:link
													href="confirmDelete?query=${f:u(query)}&id=${f:u(doc.id)}">
													<bean:message key="labels.search_list_delete_link" />
												</html:link>
											</c:if>
											<c:if test="${solrProcessRunning}">
												<bean:message key="labels.search_list_delete_link" />
											</c:if>
										</div>
									</div>
								</li>
							</c:forEach>
						</ol>
					</div>
				</div>

				<div class="row center">
					<div class="pagination">
						<ul>
							<c:if test="${existPrePage}">
								<li class="prev"><s:link
										href="prev?query=${f:u(query)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}&labelTypeValue=${f:u(labelTypeValue)}"><bean:message
											key="labels.prev_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!existPrePage}">
								<li class="prev disabled"><a href="#"><bean:message
											key="labels.prev_page" /></a></li>
							</c:if>
							<c:forEach var="pageNumber" varStatus="s"
								items="${pageNumberList}">
								<li
									<c:if test="${pageNumber == currentPageNumber}">class="active"</c:if>>
									<s:link
										href="move?query=${f:u(query)}&pn=${f:u(pageNumber)}&num=${f:u(pageSize)}&labelTypeValue=${f:u(labelTypeValue)}">${f:h(pageNumber)}</s:link>
								</li>
							</c:forEach>
							<c:if test="${existNextPage}">
								<li class="next<c:if test="${!existNextPage}"> disabled</c:if>">
									<s:link
										href="next?query=${f:u(query)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}&labelTypeValue=${f:u(labelTypeValue)}">
										<bean:message key="labels.next_page" />
								</s:link>
								</li>
							</c:if>
							<c:if test="${!existNextPage}">
								<li class="next disabled"><a href="#"><bean:message
											key="labels.next_page" /></a></li>
							</c:if>
						</ul>
					</div>
					<div>
						<span> ${currentPageNumber}/${allPageCount}
							(${allRecordCount}) </span>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div id="subheader"></div>
				<div id="result">
					<p>
						<bean:message key="labels.did_not_match"
							arg0="${f:h(query)}" />
					</p>
				</div>
			</c:otherwise>
		</c:choose>

	</tiles:put>
</tiles:insert>
