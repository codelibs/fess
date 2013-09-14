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
			<bean:message key="labels.path_mapping_title_details" />
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
				<table class="bordered-table zebra-striped" style="width: 500px;">
					<tbody>

						<tr>
							<th style="width: 150px;"><bean:message key="labels.regex" /></th>
							<td><html:text property="regex" style="width:98%" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.replacement" /></th>
							<td><html:text property="replacement" style="width:98%" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.processType" /></th>
							<td><html:select property="processType">
									<html:option value="C">
										<bean:message key="labels.path_mapping_pt_crawling" />
									</html:option>
									<html:option value="D">
										<bean:message key="labels.path_mapping_pt_displaying" />
									</html:option>
									<html:option value="B">
										<bean:message key="labels.path_mapping_pt_both" />
									</html:option>
								</html:select></td>
						</tr>
						<tr>
							<th><bean:message key="labels.sortOrder" /></th>
							<td><html:text property="sortOrder" style="width:98%" /></td>
						</tr>


					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><c:if test="${crudMode == 1}">
									<input type="submit" class="btn mini" name="confirmfromcreate"
										value="<bean:message key="labels.path_mapping_button_create"/>" />
									<input type="submit"  class="btn mini"name="back"
										value="<bean:message key="labels.path_mapping_button_back"/>" />
								</c:if> <c:if test="${crudMode == 2}">
									<input type="submit" class="btn mini" name="confirmfromupdate"
										value="<bean:message key="labels.path_mapping_button_confirm"/>" />
									<input type="submit" class="btn mini" name="back"
										value="<bean:message key="labels.path_mapping_button_back"/>" />
								</c:if></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Edit Form: BEGIN --%>


	</tiles:put>
</tiles:insert>
