<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.search_log_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="searchLog" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">


		<h3>
			<bean:message key="labels.search_log_title_confirm" />
		</h3>
		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

		<%-- Confirm Form: BEGIN --%>
		<s:form>
			<html:hidden property="crudMode" />
			<div>
				<table class="bordered-table zebra-striped">
					<tbody>



						<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
							<tr>
								<th style="width: 150px;"><bean:message
										key="labels.search_log_id" /></th>
								<td style="width: 350px;">${f:h(id)}<html:hidden
										property="id" /></td>
							</tr>
						</c:if>
						<tr>
							<th><bean:message key="labels.search_log_search_word" /></th>
							<td><div style="width: 350px; overflow-x: auto;">
									${f:h(searchWord)}
									<html:hidden property="searchWord" />
								</div></td>
						</tr>
						<tr>
							<th><bean:message key="labels.search_log_requested_time" /></th>
							<td>${f:h(requestedTime)}<html:hidden
									property="requestedTime" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.search_log_response_time" /></th>
							<td>${f:h(responseTime)}<html:hidden property="responseTime" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.search_log_hit_count" /></th>
							<td>${f:h(hitCount)}<html:hidden property="hitCount" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.search_log_query_offset" /></th>
							<td>${f:h(queryOffset)}<html:hidden property="queryOffset" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.search_log_query_page_size" /></th>
							<td>${f:h(queryPageSize)}<html:hidden
									property="queryPageSize" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.search_log_user_agent" /></th>
							<td><div style="width: 350px; overflow-x: auto;">
									${f:h(userAgent)}
									<html:hidden property="userAgent" />
								</div></td>
						</tr>
						<tr>
							<th><bean:message key="labels.search_log_referer" /></th>
							<td><div style="width: 350px; overflow-x: auto;">
									${f:h(referer)}
									<html:hidden property="referer" />
								</div></td>
						</tr>
						<tr>
							<th><bean:message key="labels.search_log_client_ip" /></th>
							<td><div style="width: 350px; overflow-x: auto;">
									${f:h(clientIp)}
									<html:hidden property="clientIp" />
								</div></td>
						</tr>
						<tr>
							<th><bean:message key="labels.search_log_session_id" /></th>
							<td><div style="width: 350px; overflow-x: auto;">
									${f:h(userSessionId)}
									<html:hidden property="userSessionId" />
								</div></td>
						</tr>
						<tr>
							<th><bean:message key="labels.search_log_access_type" /></th>
							<td><div style="width: 350px; overflow-x: auto;">
									${f:h(accessType)}
									<html:hidden property="accessType" />
								</div></td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><c:if test="${crudMode == 3}">
									<input type="submit" class="btn mini" name="delete"
										value="<bean:message key="labels.crud_button_delete"/>" />
									<input type="submit" class="btn mini" name="back"
										value="<bean:message key="labels.crud_button_back"/>" />
								</c:if> <c:if test="${crudMode == 4}">
									<input type="submit" class="btn mini" name="back"
										value="<bean:message key="labels.crud_button_back"/>" />
									<input type="submit" class="btn mini" name="deletefromconfirm"
										value="<bean:message key="labels.crud_button_delete"/>" />
								</c:if></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Confirm Form: END --%>

		<c:if test="${hasClickLog}">
			<%-- List: BEGIN --%>
			<div class="list-table" style="margin-top: 5px;">
				<h3>
					<bean:message key="labels.search_log_click_log_title" />
				</h3>
				<table border="1">
					<thead>
						<tr>
							<th style="text-align: center; width: 350px;"><bean:message
									key="labels.search_log_click_log_url" /></th>
							<th style="text-align: center; width: 150px;"><bean:message
									key="labels.search_log_click_log_requestedTime" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="data" varStatus="s" items="${clickLogList}">
							<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}">
								<td><div style="width: 345px; overflow-x: auto;">${f:h(data.url)}</div></td>
								<td>${f:h(data.requestedTimeForList)}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</c:if>

	</tiles:put>
</tiles:insert>
