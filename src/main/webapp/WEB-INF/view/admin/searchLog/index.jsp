<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.search_log_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="searchLog" />
	<tiles:put name="headerScript" type="string">
		<link rel="stylesheet"
			href="${f:url('/css/admin/smoothness/jquery-ui-1.9.2.css')}"
			type="text/css" media="all" />
		<script type="text/javascript"
			src="${f:url('/js/admin/jquery-ui-1.9.2.min.js')}"></script>
		<script type="text/javascript"
			src="${f:url('/js/admin/searchLog.js')}"></script>
	</tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.search_log_title" />
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
									key="labels.search_log_search_word_search" /></th>
							<td style="width: 300px;"><html:text
									property="searchParams.searchWord" styleClass="large"></html:text></td>
						</tr>
						<tr>
							<th style="width: 100px;"><bean:message
									key="labels.search_log_user_code_search" /></th>
							<td style="width: 300px;"><html:text
									property="searchParams.userCode" styleClass="large"></html:text></td>
						</tr>
						<tr>
							<th rowspan="2"><bean:message
									key="labels.search_log_search_term" /></th>
							<td><bean:message key="labels.search_log_search_term_from" />
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
							<td><bean:message key="labels.search_log_search_term_to" />
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
						<tr>
							<th><bean:message key="labels.search_log_search_start_page" /></th>
							<td>&nbsp;<html:checkbox property="searchParams.startPage">
									<bean:message key="labels.enabled" />
								</html:checkbox></td>
						</tr>
					</tbody>
				</table>
				<div class="row">
					<div class="span8 offset4">
						<input type="submit" class="btn small primary" name="search"
							value="<bean:message key="labels.search_log_button_search"/>" />
						<input type="submit" class="btn small" name="reset"
							value="<bean:message key="labels.search_log_button_reset"/>" />
					</div>
				</div>
			</s:form>
		</div>
		<%-- Search: END --%>

		<div style="margin-top: 5px;">
			<s:link href="download">
				<bean:message key="labels.search_log_download_csv" />
			</s:link>
		</div>

		<%-- List: BEGIN --%>
		<div class="list-table" style="margin-top: 5px;">
			<c:if test="${searchLogPager.allRecordCount == 0}">
				<p class="alert-message warning">
					<bean:message key="labels.list_could_not_find_crud_table" />
				</p>
			</c:if>
			<c:if test="${searchLogPager.allRecordCount > 0}">
				<table class="bordered-table zebra-striped">
					<thead>
						<tr>
							<th style="text-align: center; width: 150px;"><s:link
									href="search?searchParams.sortField=requestedTime&searchParams.sortOrder=${searchParams.reverseSortOrder}">
									<bean:message key="labels.search_log_requested_time" />
								</s:link> <c:if test="${searchParams.sortField=='requestedTime'}">
									<c:if test="${searchParams.sortOrder=='asc'}">
										<bean:message key="labels.search_log_sort_up" />
									</c:if>
									<c:if test="${searchParams.sortOrder=='desc'}">
										<bean:message key="labels.search_log_sort_down" />
									</c:if>
								</c:if></th>
							<th style="text-align: center;"><s:link
									href="search?searchParams.sortField=searchWord&searchParams.sortOrder=${searchParams.reverseSortOrder}">
									<bean:message key="labels.search_log_search_word" />
								</s:link> <c:if test="${searchParams.sortField=='searchWord'}">
									<c:if test="${searchParams.sortOrder=='asc'}">
										<bean:message key="labels.search_log_sort_up" />
									</c:if>
									<c:if test="${searchParams.sortOrder=='desc'}">
										<bean:message key="labels.search_log_sort_down" />
									</c:if>
								</c:if></th>
							<th style="text-align: center; width: 80px;"><s:link
									href="search?searchParams.sortField=responseTime&searchParams.sortOrder=${searchParams.reverseSortOrder}">
									<bean:message key="labels.search_log_response_time" />
								</s:link> <c:if test="${searchParams.sortField=='responseTime'}">
									<c:if test="${searchParams.sortOrder=='asc'}">
										<bean:message key="labels.search_log_sort_up" />
									</c:if>
									<c:if test="${searchParams.sortOrder=='desc'}">
										<bean:message key="labels.search_log_sort_down" />
									</c:if>
								</c:if></th>
							<th style="text-align: center; width: 80px;"><s:link
									href="search?searchParams.sortField=hitCount&searchParams.sortOrder=${searchParams.reverseSortOrder}">
									<bean:message key="labels.search_log_hit_count" />
								</s:link> <c:if test="${searchParams.sortField=='hitCount'}">
									<c:if test="${searchParams.sortOrder=='asc'}">
										<bean:message key="labels.search_log_sort_up" />
									</c:if>
									<c:if test="${searchParams.sortOrder=='desc'}">
										<bean:message key="labels.search_log_sort_down" />
									</c:if>
								</c:if></th>
							<th style="text-align: center; width: 100px;">&nbsp;</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="data" varStatus="s" items="${searchLogItems}">
							<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}">
								<td>${f:h(data.requestedTimeForList)}</td>
								<td>${f:h(data.searchWord)}</td>
								<td style="text-align: right;">${f:h(data.responseTime)}</td>
								<td style="text-align: right;">${f:h(data.hitCount)}</td>
								<td style="text-align: center;"><s:link
										href="confirmpage/4/${f:u(data.id)}">
										<bean:message key="labels.search_log_link_details" />
									</s:link> <s:link href="deletepage/3/${f:u(data.id)}">
										<bean:message key="labels.search_log_link_delete" />
									</s:link></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>

				<div class="span12 center">
					<script>
			<!--
				function confirmToDeleteAll() {
					if (confirm('<bean:message key="labels.search_log_delete_all_confirmation"/>')) {
						return true;
					} else {
						return false;
					}
				}
			// -->
			</script>
					<s:link href="deleteall" onclick="return confirmToDeleteAll();" styleClass="btn">
						<bean:message key="labels.search_log_delete_all_link" />
					</s:link>
				</div>

				<%-- Page Navigation: BEGIN --%>
				<div class="row center">
					<div class="pagination">
						<ul>
							<c:if test="${searchLogPager.existPrePage}">
								<li class="prev"><s:link
										href="list/${searchLogPager.currentPageNumber - 1}">
										<bean:message key="labels.crud_link_prev_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!searchLogPager.existPrePage}">
								<li class="prev disabled"><a href="#"><bean:message
											key="labels.crud_link_prev_page" /></a></li>
							</c:if>
							<c:forEach var="p" varStatus="s"
								items="${searchLogPager.pageNumberList}">
								<li
									<c:if test="${p == searchLogPager.currentPageNumber}">class="active"</c:if>>
									<s:link href="list/${p}">${p}</s:link>
								</li>
							</c:forEach>
							<c:if test="${searchLogPager.existNextPage}">
								<li class="next"><s:link
										href="list/${searchLogPager.currentPageNumber + 1}">
										<bean:message key="labels.crud_link_next_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!searchLogPager.existNextPage}">
								<li class="next disabled"><a href="#"><bean:message
											key="labels.crud_link_next_page" /></a></li>
							</c:if>
						</ul>
					</div>
					<div>
						<span><bean:message key="labels.pagination_page_guide_msg"
								arg0="${f:h(searchLogPager.currentPageNumber)}"
								arg1="${f:h(searchLogPager.allPageCount)}"
								arg2="${f:h(searchLogPager.allRecordCount)}" /></span>
					</div>
				</div>
				<%-- Page Navigation: END --%>
			</c:if>
		</div>
		<%-- List: END --%>

	</tiles:put>
</tiles:insert>
