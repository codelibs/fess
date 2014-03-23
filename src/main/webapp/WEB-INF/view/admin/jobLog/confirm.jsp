<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.joblog_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="jobLog" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.joblog_title_confirm" />
		</h3>
		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

			<div>
				<ul class="pills">
					<li><s:link href="index">
							<bean:message key="labels.joblog_link_list" />
						</s:link></li>
					<c:if test="${crudMode == 1}">
					<li class="active"><a href="#"><bean:message
								key="labels.joblog_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
					<li class="active"><a href="#"><bean:message
								key="labels.joblog_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
					<li class="active"><a href="#"><bean:message
								key="labels.joblog_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
					<li class="active"><a href="#"><bean:message
								key="labels.joblog_link_confirm" /></a></li>
					</c:if>
				</ul>
			</div>

		<%-- Confirm Form: BEGIN --%>
		<s:form>
			<html:hidden property="crudMode" />
			<div>
				<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
					<html:hidden property="id" />
				</c:if>
				<table class="bordered-table zebra-striped">
					<tbody>
						<tr>
							<th style="width: 150px;"><bean:message
									key="labels.joblog_jobName" /></th>
							<td>${f:h(jobName)}<html:hidden property="jobName" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.joblog_jobStatus" /></th>
							<td style="text-transform: uppercase;">${f:h(jobStatus)}<html:hidden property="jobStatus" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.joblog_target" /></th>
							<td>${f:h(target)}<html:hidden property="target" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.joblog_startTime" /></th>
							<td>${f:h(startTime)}<html:hidden property="startTime" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.joblog_endTime" /></th>
							<td>${f:h(endTime)}<html:hidden property="endTime" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.joblog_scriptType" /></th>
							<td>${f:h(scriptType)}<html:hidden property="scriptType" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.joblog_scriptData" /></th>
							<td>${f:br(f:h(scriptData))}<html:hidden property="scriptData" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.joblog_scriptResult" /></th>
							<td>${f:br(f:h(scriptResult))}<html:hidden property="scriptResult" /></td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><c:if test="${crudMode == 3}">
									<input type="submit" class="btn small" name="delete"
										value="<bean:message key="labels.joblog_button_delete"/>" />
									<input type="submit" class="btn small" name="back"
										value="<bean:message key="labels.joblog_button_back"/>" />
								</c:if> <c:if test="${crudMode == 4}">
									<input type="submit" class="btn small" name="back"
										value="<bean:message key="labels.joblog_button_back"/>" />
									<input type="submit" class="btn small" name="deletefromconfirm"
										value="<bean:message key="labels.joblog_button_delete"/>" />
								</c:if></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Confirm Form: BEGIN --%>

	</tiles:put>
</tiles:insert>
