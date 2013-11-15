<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.labeltype_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="labelType" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.labeltype_title_confirm" />
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
							<bean:message key="labels.labeltype_link_list" />
						</s:link></li>
					<c:if test="${crudMode == 1}">
					<li class="active"><a href="#"><bean:message
								key="labels.labeltype_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
					<li class="active"><a href="#"><bean:message
								key="labels.labeltype_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
					<li class="active"><a href="#"><bean:message
								key="labels.labeltype_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
					<li class="active"><a href="#"><bean:message
								key="labels.labeltype_link_confirm" /></a></li>
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
									key="labels.labeltype_name" /></th>
							<td>${f:h(name)}<html:hidden property="name" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.labeltype_value" /></th>
							<td>${f:h(value)}<html:hidden property="value" /></td>
						</tr>
						<tr>
							<th style="width: 150px;"><bean:message
									key="labels.labeltype_included_paths" /></th>
							<td style="width: 345px; word-break: break-all;">${f:br(f:h(includedPaths))}<html:hidden
									property="includedPaths" /></td>
						</tr>
						<tr>
							<th style="width: 150px;"><bean:message
									key="labels.labeltype_excluded_paths" /></th>
							<td style="width: 345px; word-break: break-all;">${f:br(f:h(excludedPaths))}<html:hidden
									property="excludedPaths" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.role_type" /></th>
							<td><c:forEach var="rt" varStatus="s"
									items="${roleTypeItems}">
									<c:forEach var="rtid" varStatus="s" items="${roleTypeIds}">
										<c:if test="${rtid==rt.id}">${f:h(rt.name)}<br />
										</c:if>
									</c:forEach>
								</c:forEach> <html:select property="roleTypeIds" multiple="true"
									style="display:none;">
									<c:forEach var="rt" varStatus="s" items="${roleTypeItems}">
										<html:option value="${f:u(rt.id)}">${f:h(rt.name)}</html:option>
									</c:forEach>
								</html:select></td>
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
										value="<bean:message key="labels.labeltype_button_create"/>" />
									<input type="submit" class="btn mini" name="editagain"
										value="<bean:message key="labels.labeltype_button_back"/>" />
								</c:if> <c:if test="${crudMode == 2}">
									<input type="submit" class="btn mini" name="update"
										value="<bean:message key="labels.labeltype_button_update"/>" />
									<input type="submit" class="btn mini" name="editagain"
										value="<bean:message key="labels.labeltype_button_back"/>" />
								</c:if> <c:if test="${crudMode == 3}">
									<input type="submit" class="btn mini" name="delete"
										value="<bean:message key="labels.labeltype_button_delete"/>" />
									<input type="submit" class="btn mini" name="back"
										value="<bean:message key="labels.labeltype_button_back"/>" />
								</c:if> <c:if test="${crudMode == 4}">
									<input type="submit" class="btn mini" name="back"
										value="<bean:message key="labels.labeltype_button_back"/>" />
									<input type="submit" class="btn mini" name="editfromconfirm"
										value="<bean:message key="labels.labeltype_button_edit"/>" />
									<input type="submit" class="btn mini" name="deletefromconfirm"
										value="<bean:message key="labels.labeltype_button_delete"/>" />
								</c:if></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Confirm Form: BEGIN --%>

	</tiles:put>
</tiles:insert>
