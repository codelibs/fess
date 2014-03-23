<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.failure_url_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="failureUrl" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.failure_url_title_confirm" />
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
										key="labels.failure_url_id" /></th>
								<td style="width: 350px;">${f:h(id)}<html:hidden
										property="id" /></td>
							</tr>
						</c:if>
						<tr>
							<th><bean:message key="labels.failure_url_url" /></th>
							<td><div style="width: 350px; overflow-x: auto;">
									${f:h(url)}
									<html:hidden property="url" />
								</div></td>
						</tr>
						<tr>
							<th><bean:message key="labels.failure_url_thread_name" /></th>
							<td>${f:h(threadName)}<html:hidden property="threadName" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.failure_url_error_name" /></th>
							<td>${f:h(errorName)}<html:hidden property="errorName" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.failure_url_error_log" /></th>
							<td><div style="width: 350px; overflow-x: auto;">
									${f:br(f:nbsp(f:h(errorLog)))}
									<html:hidden property="errorLog" />
								</div></td>
						</tr>
						<tr>
							<th><bean:message key="labels.failure_url_error_count" /></th>
							<td>${f:h(errorCount)}<html:hidden property="errorCount" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.failure_url_last_access_time" /></th>
							<td>${f:h(lastAccessTime)}<html:hidden
									property="lastAccessTime" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.failure_url_web_config_name" /></th>
							<td>${f:h(webConfigName)}</td>
						</tr>
						<tr>
							<th><bean:message key="labels.failure_url_file_config_name" /></th>
							<td>${f:h(fileConfigName)}</td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><c:if test="${crudMode == 3}">
									<input type="submit" class="btn small" name="delete"
										value="<bean:message key="labels.crud_button_delete"/>" />
									<input type="submit" class="btn small" name="back"
										value="<bean:message key="labels.crud_button_back"/>" />
								</c:if> <c:if test="${crudMode == 4}">
									<input type="submit" class="btn small" name="back"
										value="<bean:message key="labels.crud_button_back"/>" />
									<input type="submit" class="btn small" name="deletefromconfirm"
										value="<bean:message key="labels.crud_button_delete"/>" />
								</c:if></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Confirm Form: BEGIN --%>

	</tiles:put>
</tiles:insert>
