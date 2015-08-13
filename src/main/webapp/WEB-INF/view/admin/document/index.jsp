<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<la:message key="labels.system_title_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="system" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<div id="main">
		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

<c:if test="${empty groupActionItems}">
<div class="alert-message error"><la:message key="labels.no_available_solr_servers" /></div>
</c:if>
<c:if test="${!empty groupActionItems}">
			<div>
				<h3>
					<la:message key="labels.es_title_action" />
				</h3>
				<table class="bordered-table zebra-striped">
					<tbody>
						<c:forEach var="groupAction" items="${groupActionItems}">
							<s:form>
							<tr>
								<th style="width:200px;">${f:h(groupAction.groupName)}</th>
								<td>
									<html:hidden property="groupName"
										value="${f:u(groupAction.groupName)}" />
									<html:submit
										property="commit" disabled="${solrProcessRunning}"
										styleClass="btn">
										<la:message key="labels.es_action_commit" />
									</html:submit>
									<html:submit property="optimize"
										disabled="${solrProcessRunning}" styleClass="btn">
										<la:message key="labels.es_action_optimize" />
									</html:submit>
								</td>
							</tr>
							</s:form>
						</c:forEach>
					</tbody>
				</table>
			</div>

			<div style="margin-top: 5px;">
				<h3>
					<la:message key="labels.es_title_delete" />
				</h3>
				<table class="bordered-table zebra-striped">
					<tbody>
						<c:forEach var="groupAction" items="${groupActionItems}">
							<s:form>
							<tr>
								<th style="width:200px;" rowspan="2">${f:h(groupAction.groupName)}</th>
								<td>
									<html:select property="sessionId"
										disabled="${solrProcessRunning}">
										<html:option value="">
											<la:message key="labels.es_action_none" />
										</html:option>
										<c:forEach var="sessionIdItem"
											items="${groupAction.sessionIdItems}">
											<html:option value="${f:u(sessionIdItem.value)}">${f:h(sessionIdItem.label)}</html:option>
										</c:forEach>
										<html:option value="*">
											<la:message key="labels.es_action_all" /> (${f:h(groupAction.totalCount)})</html:option>
									</html:select>
									<html:submit styleClass="btn" property="delete"
										disabled="${solrProcessRunning}">
										<la:message key="labels.es_action_delete" />
									</html:submit>
									<html:hidden property="groupName"
										value="${f:u(groupAction.groupName)}" />
								</td>
							</tr>
							<tr>
								<td style="vertical-align: middle;">
									<la:message key="labels.es_action_url_delete" />
									<html:text property="deleteUrl" style="width:150px;"
										disabled="${solrProcessRunning}"></html:text> <html:submit
										property="confirmByUrl" styleClass="btn"
										disabled="${solrProcessRunning}">
										<la:message key="labels.es_action_confirm_list" />
									</html:submit>
									<html:submit property="deleteByUrl"
										disabled="${solrProcessRunning}" styleClass="btn">
										<la:message key="labels.es_action_delete" />
									</html:submit>
								</td>
							</tr>
							</s:form>
						</c:forEach>
					</tbody>
				</table>
			</div>

			<div style="margin-top: 5px;">
				<h3>
					<la:message key="labels.es_document_title" />
				</h3>
				<table class="bordered-table zebra-striped">
					<thead>
						<tr>
							<th style="width:200px;"><la:message key="labels.es_group_name" /></th>
							<th><la:message key="labels.session_name" /></th>
							<th><la:message key="labels.es_num_of_docs" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="groupAction" items="${groupActionItems}">
							<tr>
								<td>${f:h(groupAction.groupName)}</td>
								<td align="center"><la:message
										key="labels.system_document_all" /></td>
								<td align="center">${f:h(groupAction.totalCount)}</td>
							</tr>
						</c:forEach>
						<c:forEach var="groupAction" items="${groupActionItems}">
							<c:forEach var="sessionIdItem"
								items="${groupAction.sessionIdItems}">
								<tr>
									<td>${f:h(groupAction.groupName)}</td>
									<td align="center"><html:link
											href="${f:url('/admin/searchList/search')}?query=segment:${f:u(sessionIdItem.value)}">${f:h(sessionIdItem.value)}</html:link></td>
									<td align="center">${f:h(sessionIdItem.count)}</td>
								</tr>
							</c:forEach>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div style="margin-top: 5px;">
				<h3>
					<la:message key="labels.suggest_document_title" />
				</h3>
				<table class="bordered-table zebra-striped">
					<thead>
						<tr>
							<th style="width:200px;"><la:message key="labels.suggest_type" /></th>
							<th><la:message key="labels.es_num_of_docs" /></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td style="vertical-align: middle;"><la:message key="labels.suggest_type_all" /></td>
							<td align="center" style="vertical-align: middle;">${suggestDocumentNums.all}</td>
							<td align="center">
							</td>
						</tr>
						<tr>
							<td style="vertical-align: middle;"><la:message key="labels.suggest_type_content" /></td>
							<td align="center" style="vertical-align: middle;">${suggestDocumentNums.content}</td>
							<td align="center">
								<s:form style="margin-bottom:0;">
									<html:hidden property="deleteSuggestType"
										value="content" />
									<html:submit styleClass="btn" property="deleteSuggest"
										disabled="${solrProcessRunning}">
										<la:message key="labels.es_action_delete" />
									</html:submit>
								</s:form>
							</td>
						</tr>
						<tr>
							<td style="vertical-align: middle;"><la:message key="labels.suggest_type_searchlog" /></td>
							<td align="center" style="vertical-align: middle;">${suggestDocumentNums.searchLog}</td>
							<td align="center">
								<s:form style="margin-bottom:0;">
									<html:hidden property="deleteSuggestType"
										value="searchLog" />
									<html:submit styleClass="btn" property="deleteSuggest"
										disabled="${solrProcessRunning}">
										<la:message key="labels.es_action_delete" />
									</html:submit>
								</s:form>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			</c:if>
		</div>

		<c:if test="${solrProcessRunning}">
			<script type="text/javascript">
			<!--
				setTimeout(function() {
					window.location.reload();
				}, 15000);
			// -->
			</script>
		</c:if>
	</tiles:put>
</tiles:insert>
