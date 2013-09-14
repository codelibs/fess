<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.stats_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="stats" />
	<tiles:put name="headerScript" type="string">
		<link rel="stylesheet"
			href="${f:url('/css/admin/smoothness/jquery-ui-1.9.2.css')}"
			type="text/css" media="all" />
		<script type="text/javascript"
			src="${f:url('/js/admin/jquery-ui-1.9.2.min.js')}"></script>
		<script type="text/javascript" src="${f:url('/js/admin/stats.js')}"></script>
	</tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.stats_configuration" />
		</h3>

		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

		<%-- Search: BEGIN --%>
		<div>
			<s:form>
				<table class="bordered-table zebra-striped">
					<tbody>
						<tr>
							<th style="width: 100px;"><bean:message
									key="labels.stats_search_report_type" /></th>
							<td style="width: 200px;"><html:select
									property="searchParams.reportType" styleClass="medium">
									<html:option value="searchWord">
										<bean:message key="labels.stats_search_word" />
									</html:option>
									<html:option value="query">
										<bean:message key="labels.stats_search_query" />
									</html:option>
									<html:option value="solrQuery">
										<bean:message key="labels.stats_solr_query" />
									</html:option>
									<html:option value="userAgent">
										<bean:message key="labels.stats_user_agent" />
									</html:option>
									<html:option value="referer">
										<bean:message key="labels.stats_referer" />
									</html:option>
									<html:option value="clientIp">
										<bean:message key="labels.stats_client_ip" />
									</html:option>
									<html:option value="clickUrl">
										<bean:message key="labels.stats_click_url" />
									</html:option>
									<html:option value="favoriteUrl">
										<bean:message key="labels.stats_favorite_url" />
									</html:option>
									<c:forEach var="reportTypeLabel" varStatus="s" items="${reportTypeItems}">
									<html:option value="${f:h(reportTypeLabel)}">${f:h(reportTypeLabel)}</html:option>
									</c:forEach>
								</html:select></td>
						</tr>
						<tr>
							<th rowspan="2"><bean:message key="labels.stats_search_term" /></th>
							<td><bean:message key="labels.stats_search_term_from" />
								<html:text property="searchParams.startDate"
									styleId="searchStartDate" size="10" styleClass="small" autocomplete="off"></html:text>
								<html:select property="searchParams.startHour"
									styleClass="small">
									<html:option value=""></html:option>
									<c:forEach begin="0" end="23" step="1" varStatus="status">
										<html:option value="${status.index}">${status.index}</html:option>
									</c:forEach>
								</html:select> : <html:select property="searchParams.startMin"
									styleClass="small">
									<html:option value=""></html:option>
									<c:forEach begin="0" end="59" step="1" varStatus="status">
										<html:option value="${status.index}">${status.index}</html:option>
									</c:forEach>
								</html:select></td>
						</tr>
						<tr>
							<td><bean:message key="labels.stats_search_term_to" />
								<html:text property="searchParams.endDate"
									styleId="searchEndDate" size="10" styleClass="small" autocomplete="off"></html:text>
								<html:select property="searchParams.endHour" styleClass="small">
									<html:option value=""></html:option>
									<c:forEach begin="0" end="23" step="1" varStatus="status">
										<html:option value="${status.index}">${status.index}</html:option>
									</c:forEach>
								</html:select> : <html:select property="searchParams.endMin"
									styleClass="small">
									<html:option value=""></html:option>
									<c:forEach begin="0" end="59" step="1" varStatus="status">
										<html:option value="${status.index}">${status.index}</html:option>
									</c:forEach>
								</html:select></td>
						</tr>
					</tbody>
				</table>
				<div class="row">
					<div class="span8 offset4">
						<input type="submit" class="btn mini primary" name="search"
							value="<bean:message key="labels.stats_button_search"/>" /> <input
							type="submit" class="btn small" name="reset"
							value="<bean:message key="labels.stats_button_reset"/>" />
					</div>
				</div>

			</s:form>
		</div>
		<%-- Search: END --%>

		<%-- List: BEGIN --%>
		<div class="list-table" style="margin-top: 5px;">
			<c:if test="${statsPager.allRecordCount == 0}">
				<p class="alert-message warning">
					<bean:message key="labels.list_could_not_find_crud_table" />
				</p>
			</c:if>
			<c:if test="${statsPager.allRecordCount > 0}">
				<table class="bordered-table zebra-striped">
					<thead>
						<tr>
							<th style="text-align: center; width: 400px;"><c:if
									test="${searchParams.reportType=='searchWord'}">
									<bean:message key="labels.stats_search_word" />
								</c:if> <c:if test="${searchParams.reportType=='query'}">
									<bean:message key="labels.stats_search_query" />
								</c:if> <c:if test="${searchParams.reportType=='solrQuery'}">
									<bean:message key="labels.stats_solr_query" />
								</c:if> <c:if test="${searchParams.reportType=='userAgent'}">
									<bean:message key="labels.stats_user_agent" />
								</c:if> <c:if test="${searchParams.reportType=='referer'}">
									<bean:message key="labels.stats_referer" />
								</c:if> <c:if test="${searchParams.reportType=='clientIp'}">
									<bean:message key="labels.stats_client_ip" />
								</c:if></th>
							<th style="text-align: center; width: 80px;"><bean:message
									key="labels.stats_count" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="data" varStatus="s" items="${statsItems}">
							<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}">
								<td><div style="width: 550px; overflow-x: auto;">${f:h(data.name)}</div></td>
								<td style="text-align: right;">${f:h(data.cnt)}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<%-- Page Navigation: BEGIN --%>
				<div class="row center">
					<div class="pagination">
						<ul>
							<c:if test="${statsPager.existPrePage}">
								<li class="prev"><s:link
										href="list/${statsPager.currentPageNumber - 1}">
										<bean:message key="labels.crud_link_prev_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!statsPager.existPrePage}">
								<li class="prev disabled"><a href="#"><bean:message
											key="labels.crud_link_prev_page" /></a></li>
							</c:if>
							<c:forEach var="p" varStatus="s"
								items="${statsPager.pageNumberList}">
								<li
									<c:if test="${p == statsPager.currentPageNumber}">class="active"</c:if>>
									<s:link href="list/${p}">${p}</s:link>
								</li>
							</c:forEach>
							<c:if test="${statsPager.existNextPage}">
								<li class="next"><s:link
										href="list/${statsPager.currentPageNumber + 1}">
										<bean:message key="labels.crud_link_next_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!statsPager.existNextPage}">
								<li class="next disabled"><a href="#"><bean:message
											key="labels.crud_link_next_page" /></a></li>
							</c:if>
						</ul>
					</div>
					<div>
						<span><bean:message key="labels.pagination_page_guide_msg"
								arg0="${f:h(statsPager.currentPageNumber)}"
								arg1="${f:h(statsPager.allPageCount)}"
								arg2="${f:h(statsPager.allRecordCount)}" /></span>
					</div>
				</div>
				<%-- Page Navigation: END --%>
			</c:if>
		</div>
		<%-- List: END --%>
	</tiles:put>
</tiles:insert>
