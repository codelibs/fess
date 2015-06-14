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

			<div>
				<h3>
					<bean:message key="labels.system_title_system_status" />
				</h3>
				<table class="bordered-table zebra-striped">
					<tbody>
						<tr>
							<th><bean:message key="labels.es_cluster_name" />
							</th>
							<td>${f:h(clusterName)} (<c:if
									test="${clusterStatus=='ACTIVE'}">
									<bean:message key="labels.es_active" />
								</c:if> <c:if test="${clusterStatus!='ACTIVE'}">
									<bean:message key="labels.es_inactive" />
								</c:if>)
							</td>
						</tr>
					</tbody>
				</table>
			</div>

			<s:form>
				<div>
					<h3>
						<bean:message key="labels.crawler_status_title" />
					</h3>
					<table class="bordered-table zebra-striped">
						<tbody>
							<tr>
								<th style="width: 200px;"><bean:message
										key="labels.crawler_process_running" /></th>
								<td>
									<span style="margin-right:20px;">
									<c:if test="${crawlerRunning}">
										<bean:message key="labels.crawler_running" />
									</c:if><c:if test="${!crawlerRunning}">
										<bean:message key="labels.crawler_stopped" />
									</c:if>
									</span>
								</td>
							</tr>
							<tr>
								<th style="width: 200px;"><bean:message
										key="labels.crawler_process_action" /></th>
								<td>
								<c:if test="${!crawlerRunning}">
									<input type="submit" class="btn" name="start"
										value="<bean:message key="labels.crawler_button_start"/>" />
								</c:if>
								<c:if test="${crawlerRunning}">
									<html:select property="sessionId" style="width:100px">
										<option value=""><bean:message key="labels.crawler_sessionid_all"/></option>
										<c:forEach var="runningSessionId" items="${runningSessionIds}">
										<option value="${f:h(runningSessionId)}">${f:h(runningSessionId)}</option>
										</c:forEach>
									</html:select>
									<input type="submit" class="btn" name="stop"
										value="<bean:message key="labels.crawler_button_stop"/>" />
								</c:if>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</s:form>

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
