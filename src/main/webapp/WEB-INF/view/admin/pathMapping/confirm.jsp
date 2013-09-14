<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.path_mapping_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="pathMapping" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">


		<h3>
			<bean:message key="labels.path_mapping_title_confirm" />
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
							<bean:message key="labels.path_mapping_link_list" />
						</s:link></li>
					<c:if test="${crudMode == 1}">
					<li class="active"><a href="#"><bean:message
								key="labels.path_mapping_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
					<li class="active"><a href="#"><bean:message
								key="labels.path_mapping_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
					<li class="active"><a href="#"><bean:message
								key="labels.path_mapping_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
					<li class="active"><a href="#"><bean:message
								key="labels.path_mapping_link_confirm" /></a></li>
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
				<table class="bordered-table zebra-striped" style="width: 500px;">
					<tbody>

						<tr>
							<th style="width: 150px;"><bean:message key="labels.regex" /></th>
							<td>${f:h(regex)}<html:hidden property="regex" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.replacement" /></th>
							<td>${f:h(replacement)}<html:hidden property="replacement" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.processType" /></th>
							<td><c:if test="${processType=='C'}">
									<bean:message key="labels.path_mapping_pt_crawling" />
								</c:if> <c:if test="${processType=='D'}">
									<bean:message key="labels.path_mapping_pt_displaying" />
								</c:if> <c:if test="${processType=='B'}">
									<bean:message key="labels.path_mapping_pt_both" />
								</c:if> <html:hidden property="processType" /></td>
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
										value="<bean:message key="labels.path_mapping_button_create"/>" />
									<input type="submit" class="btn mini" name="editagain"
										value="<bean:message key="labels.path_mapping_button_back"/>" />
								</c:if> <c:if test="${crudMode == 2}">
									<input type="submit" class="btn mini" name="update"
										value="<bean:message key="labels.path_mapping_button_update"/>" />
									<input type="submit" class="btn mini" name="editagain"
										value="<bean:message key="labels.path_mapping_button_back"/>" />
								</c:if> <c:if test="${crudMode == 3}">
									<input type="submit" class="btn mini" name="delete"
										value="<bean:message key="labels.path_mapping_button_delete"/>" />
									<input type="submit" class="btn mini" name="back"
										value="<bean:message key="labels.path_mapping_button_back"/>" />
								</c:if> <c:if test="${crudMode == 4}">
									<input type="submit" class="btn mini" name="back"
										value="<bean:message key="labels.path_mapping_button_back"/>" />
									<input type="submit" class="btn mini" name="editfromconfirm"
										value="<bean:message key="labels.path_mapping_button_edit"/>" />
									<input type="submit" class="btn mini" name="deletefromconfirm"
										value="<bean:message key="labels.path_mapping_button_delete"/>" />
								</c:if></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Confirm Form: BEGIN --%>


	</tiles:put>
</tiles:insert>
