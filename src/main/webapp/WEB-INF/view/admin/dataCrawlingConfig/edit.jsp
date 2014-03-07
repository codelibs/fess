<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.data_crawling_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="dataCrawlingConfig" />
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

			<%-- Edit Form: BEGIN --%>
			<s:form>
				<html:hidden property="crudMode" />
				<div>
					<c:if test="${crudMode==2}">
						<html:hidden property="id" />
						<html:hidden property="versionNo" />
					</c:if>
					<html:hidden property="createdBy" />
					<html:hidden property="createdTime" />
					<html:hidden property="sortOrder" />
					<h3>
						<bean:message key="labels.data_crawling_title_details" />
					</h3>
			<div>
				<ul class="pills">
					<li><s:link href="index">
							<bean:message key="labels.data_crawling_link_list" />
						</s:link></li>
					<c:if test="${crudMode == 1}">
					<li class="active"><a href="#"><bean:message
								key="labels.data_crawling_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
					<li class="active"><a href="#"><bean:message
								key="labels.data_crawling_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
					<li class="active"><a href="#"><bean:message
								key="labels.data_crawling_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
					<li class="active"><a href="#"><bean:message
								key="labels.data_crawling_link_confirm" /></a></li>
					</c:if>
				</ul>
			</div>
					<table class="bordered-table zebra-striped">
						<tbody>
							<tr>
								<th style="width: 150px;"><bean:message key="labels.name" /></th>
								<td><html:text property="name"  styleClass="xlarge"/></td>
							</tr>
							<tr>
								<th style="width: 150px;"><bean:message
										key="labels.handler_name" /></th>
								<td><html:select property="handlerName" size="1"
										>
										<c:forEach var="hn" varStatus="s" items="${handlerNameItems}">
											<html:option value="${f:u(hn.value)}">${f:h(hn.label)}</html:option>
										</c:forEach>
									</html:select></td>
							</tr>
							<tr>
								<th style="width: 150px;"><bean:message
										key="labels.handler_parameter" /></th>
								<td><html:textarea property="handlerParameter"
										 rows="5" styleClass="xlarge"/></td>
							</tr>
							<tr>
								<th style="width: 150px;"><bean:message
										key="labels.handler_script" /></th>
								<td><html:textarea property="handlerScript"
										 rows="5" styleClass="xlarge"/></td>
							</tr>
							<tr>
								<th><bean:message key="labels.boost" /></th>
								<td><html:text property="boost" styleClass="xlarge" /></td>
							</tr>
							<tr>
								<th><bean:message key="labels.role_type" /></th>
								<td><html:select property="roleTypeIds" multiple="true"
										styleClass="xlarge">
										<c:forEach var="rt" varStatus="s" items="${roleTypeItems}">
											<html:option value="${f:u(rt.id)}">${f:h(rt.name)}</html:option>
										</c:forEach>
									</html:select></td>
							</tr>
							<tr>
								<th><bean:message key="labels.label_type" /></th>
								<td><html:select property="labelTypeIds" multiple="true"
										styleClass="xlarge">
										<c:forEach var="l" varStatus="s" items="${labelTypeItems}">
											<html:option value="${f:u(l.id)}">${f:h(l.name)}</html:option>
										</c:forEach>
									</html:select></td>
							</tr>
							<tr>
								<th><bean:message key="labels.available" /></th>
								<td><html:select property="available" styleClass="small">
										<html:option value="T">
											<bean:message key="labels.enabled" />
										</html:option>
										<html:option value="F">
											<bean:message key="labels.disabled" />
										</html:option>
									</html:select></td>
							</tr>

						</tbody>
						<tfoot>
							<tr>
								<td colspan="2"><c:if test="${crudMode == 1}">
										<input type="submit" class="btn mini" name="confirmfromcreate"
											value="<bean:message key="labels.data_crawling_button_create"/>" />
										<input type="submit" class="btn mini" name="back"
											value="<bean:message key="labels.data_crawling_button_back"/>" />
									</c:if> <c:if test="${crudMode == 2}">
										<input type="submit" class="btn mini" name="confirmfromupdate"
											value="<bean:message key="labels.data_crawling_button_confirm"/>" />
										<input type="submit" class="btn mini" name="back"
											value="<bean:message key="labels.data_crawling_button_back"/>" />
									</c:if></td>
							</tr>
						</tfoot>
					</table>
				</div>
			</s:form>
			<%-- Edit Form: BEGIN --%>

		</div>

	</tiles:put>
</tiles:insert>
