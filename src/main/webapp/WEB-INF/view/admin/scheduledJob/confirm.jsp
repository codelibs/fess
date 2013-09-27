<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.scheduledjob_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="scheduledJob" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.scheduledjob_title_confirm" />
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
							<bean:message key="labels.scheduledjob_link_list" />
						</s:link></li>
					<c:if test="${crudMode == 1}">
					<li class="active"><a href="#"><bean:message
								key="labels.scheduledjob_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
					<li class="active"><a href="#"><bean:message
								key="labels.scheduledjob_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
					<li class="active"><a href="#"><bean:message
								key="labels.scheduledjob_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
					<li class="active"><a href="#"><bean:message
								key="labels.scheduledjob_link_confirm" /></a></li>
					</c:if>
				</ul>
			</div>

		<%-- Confirm Form: BEGIN --%>
		<s:form>
			<html:hidden property="crudMode" />
			<div>
				<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
					<html:hidden property="id" />
					<html:hidden property="versionNo" />
				</c:if>
				<html:hidden property="createdBy" />
				<html:hidden property="createdTime" />
				<table class="bordered-table zebra-striped">
					<tbody>
						<tr>
							<th style="width: 150px;"><bean:message
									key="labels.scheduledjob_name" /></th>
							<td>${f:h(name)}<html:hidden property="name" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.scheduledjob_target" /></th>
							<td>${f:h(target)}<html:hidden property="target" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.scheduledjob_cronExpression" /></th>
							<td>${f:h(cronExpression)}<html:hidden property="cronExpression" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.scheduledjob_scriptType" /></th>
							<td>${f:h(scriptType)}<html:hidden property="scriptType" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.scheduledjob_scriptData" /></th>
							<td>${f:br(f:h(scriptData))}<html:hidden property="scriptData" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.scheduledjob_jobLogging" /></th>
							<td>
								<c:if test="${jobLogging=='on'}"><bean:message key="labels.enabled"/></c:if>
								<c:if test="${jobLogging!='on'}"><bean:message key="labels.disabled"/></c:if>
								<html:hidden property="jobLogging" />
							</td>
						</tr>
						<tr>
							<th><bean:message key="labels.scheduledjob_crawler" /></th>
							<td>
								<c:if test="${crawler=='on'}"><bean:message key="labels.enabled"/></c:if>
								<c:if test="${crawler!='on'}"><bean:message key="labels.disabled"/></c:if>
								<html:hidden property="crawler" />
							</td>
						</tr>
						<tr>
							<th><bean:message key="labels.sortOrder" /></th>
							<td>${f:h(sortOrder)}<html:hidden property="sortOrder" /></td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><c:if test="${crudMode == 1}">
									<input type="submit" class="btn mini" name="create"
										value="<bean:message key="labels.scheduledjob_button_create"/>" />
									<input type="submit" class="btn mini" name="editagain"
										value="<bean:message key="labels.scheduledjob_button_back"/>" />
								</c:if> <c:if test="${crudMode == 2}">
									<input type="submit" class="btn mini" name="update"
										value="<bean:message key="labels.scheduledjob_button_update"/>" />
									<input type="submit" class="btn mini" name="editagain"
										value="<bean:message key="labels.scheduledjob_button_back"/>" />
								</c:if> <c:if test="${crudMode == 3}">
									<input type="submit" class="btn mini" name="delete"
										value="<bean:message key="labels.scheduledjob_button_delete"/>" />
									<input type="submit" class="btn mini" name="back"
										value="<bean:message key="labels.scheduledjob_button_back"/>" />
								</c:if> <c:if test="${crudMode == 4}">
									<input type="submit" class="btn mini" name="back"
										value="<bean:message key="labels.scheduledjob_button_back"/>" />
									<input type="submit" class="btn mini" name="editfromconfirm"
										value="<bean:message key="labels.scheduledjob_button_edit"/>" />
									<input type="submit" class="btn mini" name="deletefromconfirm"
										value="<bean:message key="labels.scheduledjob_button_delete"/>" />
								</c:if></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Confirm Form: BEGIN --%>

	</tiles:put>
</tiles:insert>
