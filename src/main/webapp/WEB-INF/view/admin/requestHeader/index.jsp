<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.request_header_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="requestHeader" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.request_header_title_details" />
		</h3>

		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

		<%-- List: BEGIN --%>
		<div class="list-table">
			<div>
				<c:if test="${displayCreateLink}">
				<ul class="pills">
					<li class="active"><a href="#"><bean:message
								key="labels.request_header_link_list" /></a></li>
					<li><s:link href="createpage">
							<bean:message key="labels.request_header_link_create_new" />
						</s:link></li>
				</ul>
				</c:if>
				<c:if test="${!displayCreateLink}">
					<s:link href="../webConfig/index" styleClass="btn primary">
						<bean:message key="labels.request_header_create_web_config" />
					</s:link>
				</c:if>
			</div>
			<c:if test="${displayCreateLink && requestHeaderPager.allRecordCount == 0}">
				<p class="alert-message warning">
					<bean:message key="labels.list_could_not_find_crud_table" />
				</p>
			</c:if>
			<c:if test="${requestHeaderPager.allRecordCount > 0}">
				<table class="bordered-table zebra-striped">
					<thead>
						<tr>
							<th style="text-align: center; width: 200px;"><bean:message
									key="labels.request_header_list_name" /></th>
							<th style="text-align: center;"><bean:message
									key="labels.request_header_list_web_crawling_config" /></th>
							<th style="text-align: center; width: 150px;">&nbsp;</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="data" varStatus="s" items="${requestHeaderItems}">
							<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}">
								<td>${f:h(data.name)}</td>
								<td>${f:h(data.webConfig.name)}</td>
								<td style="text-align: center;"><s:link
										href="confirmpage/4/${f:u(data.id)}">
										<bean:message key="labels.request_header_link_details" />
									</s:link> <s:link href="editpage/2/${f:u(data.id)}">
										<bean:message key="labels.request_header_link_edit" />
									</s:link> <s:link href="deletepage/3/${f:u(data.id)}">
										<bean:message key="labels.request_header_link_delete" />
									</s:link></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<%-- Page Navigation: BEGIN --%>
				<div class="row center">
					<div class="pagination">
						<ul>
							<c:if test="${requestHeaderPager.existPrePage}">
								<li class="prev"><s:link
										href="list/${requestHeaderPager.currentPageNumber - 1}">
										<bean:message key="labels.request_header_link_prev_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!requestHeaderPager.existPrePage}">
								<li class="prev disabled"><a href="#"><bean:message
											key="labels.request_header_link_prev_page" /></a></li>
							</c:if>
							<c:forEach var="p" varStatus="s"
								items="${requestHeaderPager.pageNumberList}">
								<li
									<c:if test="${p == requestHeaderPager.currentPageNumber}">class="active"</c:if>>
									<s:link href="list/${p}">${p}</s:link>
								</li>
							</c:forEach>
							<c:if test="${requestHeaderPager.existNextPage}">
								<li class="next"><s:link
										href="list/${requestHeaderPager.currentPageNumber + 1}">
										<bean:message key="labels.request_header_link_next_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!requestHeaderPager.existNextPage}">
								<li class="next disabled"><a href="#"><bean:message
											key="labels.request_header_link_next_page" /></a></li>
							</c:if>
						</ul>
					</div>

					<div>
						<span><bean:message key="labels.pagination_page_guide_msg"
								arg0="${f:h(requestHeaderPager.currentPageNumber)}"
								arg1="${f:h(requestHeaderPager.allPageCount)}"
								arg2="${f:h(requestHeaderPager.allRecordCount)}" /></span>
					</div>
				</div>
				<%-- Page Navigation: END --%>
			</c:if>
		</div>
		<%-- List: END --%>

	</tiles:put>
</tiles:insert>
