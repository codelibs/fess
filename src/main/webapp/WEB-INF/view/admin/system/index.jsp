<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.system_title_configuration" />
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

			<s:form>
				<div>
					<h3>
						<bean:message key="labels.system_title_system_status" />
					</h3>
					<table class="bordered-table zebra-striped">
						<tbody>
							<tr>
								<th style="width: 200px;"><bean:message
										key="labels.solr_process_running" /></th>
								<td>
									<span style="margin-right:20px;">
									<c:if test="${crawlerRunning}">
										<bean:message key="labels.solr_running" />
										<c:if test="${runningSessionId!=null}">(${f:h(runningSessionId)})</c:if>
									</c:if><c:if test="${!crawlerRunning}">
										<bean:message key="labels.solr_stopped" />
									</c:if>
									</span>
									<c:if test="${crawlerRunning}">
										<input type="submit" class="btn" name="stop"
											value="<bean:message key="labels.solr_button_stop"/>" />
									</c:if> <c:if test="${!crawlerRunning}">
										<input type="submit" class="btn" name="start"
											value="<bean:message key="labels.solr_button_start"/>" />
									</c:if>
								</td>
							</tr>
							<tr>
								<th><bean:message key="labels.solr_current_select_server" />
								</th>
								<td>${f:h(currentServerForSelect)} (<c:if
										test="${currentServerStatusForSelect=='ACTIVE'}">
										<bean:message key="labels.solr_active" />
									</c:if> <c:if test="${currentServerStatusForSelect!='ACTIVE'}">
										<bean:message key="labels.solr_inactive" />
									</c:if>)
								</td>
							</tr>
							<tr>
								<th><bean:message key="labels.solr_current_update_server" />
								</th>
								<td>${f:h(currentServerForUpdate)} (<c:if
										test="${currentServerStatusForUpdate=='ACTIVE'}">
										<bean:message key="labels.solr_active" />
									</c:if> <c:if test="${currentServerStatusForUpdate!='ACTIVE'}">
										<bean:message key="labels.solr_inactive" />
									</c:if>)
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</s:form>

			<s:form>
				<div style="margin-top: 5px;">
					<h3>
						<bean:message key="labels.solr_title_edit" />
					</h3>
					<table class="bordered-table zebra-striped">
						<thead>
							<tr>
								<th><bean:message key="labels.system_group_server_name" /></th>
								<th><bean:message key="labels.system_server_status" /></th>
								<th><bean:message key="labels.system_index_status" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="serverStatus" items="${serverStatusList}">
								<tr>
									<td style="width: 200px;">
									${f:h(serverStatus.groupName)}
									:
									${f:h(serverStatus.serverName)}
									</td>
									<td>
									<html:select name="serverStatusList"
											property="${serverStatus.groupName}/${serverStatus.serverName}/status"
											value="${serverStatus.status}" indexed="true" style="width:100px">
											<html:option value="active">
												<bean:message key="labels.solr_active" />
											</html:option>
											<html:option value="inactive">
												<bean:message key="labels.solr_inactive" />
											</html:option>
									</html:select>
									</td>
									<td>
									<html:select name="serverStatusList"
											property="${serverStatus.groupName}/${serverStatus.serverName}/index"
											value="${serverStatus.index}" indexed="true" style="width:100px">
											<html:option value="ready">
												<bean:message key="labels.solr_ready" />
											</html:option>
											<html:option value="completed">
												<bean:message key="labels.solr_completed" />
											</html:option>
											<html:option value="unfinished">
												<bean:message key="labels.solr_unfinished" />
											</html:option>
									</html:select>
									</td>
								</tr>
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="3"><input type="submit" class="btn"
									name="update"
									value="<bean:message key="labels.solr_button_update"/>" /></td>
							</tr>
						</tfoot>
					</table>
				</div>
			</s:form>


			<div style="margin-top: 5px;">
				<h3>
					<bean:message key="labels.solr_management_title" />
				</h3>
				<table class="bordered-table zebra-striped">
					<tbody>
						<tr>
							<th style="width: 200px;"><bean:message
									key="labels.solr_instance_name" /></th>
							<th style="width: 100px;"><bean:message
									key="labels.solr_instance_status" /></th>
							<th><bean:message key="labels.solr_instance_action" /></th>
						</tr>
						<c:forEach var="solrInstance" items="${solrInstanceList}">
							<tr>
								<td>${f:h(solrInstance.name)}</td>
								<td style="text-align: center;">
									${f:h(solrInstance.status)}</td>
								<td style="text-align: center;"><s:form style="margin-bottom:0;">
										<input type="hidden" name="solrInstanceName"
											value="${f:h(solrInstance.name)}" />
										<c:if test="${solrInstance.status!='running'}">
											<html:submit styleClass="btn" property="startSolrInstance"
												disabled="${crawlerRunning}">
												<bean:message key="labels.solr_instance_start" />
											</html:submit>
										</c:if>
										<c:if test="${solrInstance.status=='running'}">
											<html:submit property="stopSolrInstance"
												disabled="${crawlerRunning}" styleClass="btn">
												<bean:message key="labels.solr_instance_stop" />
											</html:submit>
											<html:submit property="reloadSolrInstance"
												disabled="${crawlerRunning}" styleClass="btn">
												<bean:message key="labels.solr_instance_reload" />
											</html:submit>
										</c:if>
									</s:form></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>

		</div>
		<c:if test="${crawlerRunning}">
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
