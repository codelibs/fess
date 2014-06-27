<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.dict_synonym_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="dict" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.dict_synonym_title" />
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
					<li><s:link href="../index">
							<bean:message key="labels.dict_list_link" />
						</s:link></li>
					<li><s:link href="index?dictId=${f:u(dictId)}">
							<bean:message key="labels.dict_synonym_list_link" />
						</s:link></li>
					<c:if test="${crudMode == 1}">
					<li class="active"><a href="#"><bean:message
								key="labels.dict_synonym_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
					<li class="active"><a href="#"><bean:message
								key="labels.dict_synonym_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
					<li class="active"><a href="#"><bean:message
								key="labels.dict_synonym_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
					<li class="active"><a href="#"><bean:message
								key="labels.dict_synonym_link_confirm" /></a></li>
					</c:if>
					<li><s:link href="downloadpage?dictId=${f:u(dictId)}">
							<bean:message key="labels.dict_synonym_link_download" />
						</s:link></li>
					<li><s:link href="uploadpage?dictId=${f:u(dictId)}">
							<bean:message key="labels.dict_synonym_link_upload" />
						</s:link></li>
				</ul>
			</div>

		<%-- Edit Form: BEGIN --%>
		<s:form>
			<html:hidden property="crudMode" />
			<div>
				<html:hidden property="dictId" />
				<c:if test="${crudMode==2}">
					<html:hidden property="id" />
				</c:if>
				<table class="bordered-table zebra-striped" style="width: 500px;">
					<tbody>
						<tr>
							<th style="width: 150px;"><bean:message
									key="labels.dict_synonym_source" /></th>
							<td><html:textarea property="inputs" rows="5" style="width:98%;" /></td>
						</tr>
						<tr>
							<th><bean:message
									key="labels.dict_synonym_target" /></th>
							<td><html:textarea property="outputs" rows="5" style="width:98%;" /></td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><c:if test="${crudMode == 1}">
									<input type="submit" class="btn small" name="confirmfromcreate"
										value="<bean:message key="labels.dict_synonym_button_create"/>" />
									<input type="submit" class="btn small" name="back"
										value="<bean:message key="labels.dict_synonym_button_back"/>" />
								</c:if> <c:if test="${crudMode == 2}">
									<input type="submit" class="btn small" name="confirmfromupdate"
										value="<bean:message key="labels.dict_synonym_button_confirm"/>" />
									<input type="submit" class="btn small" name="back"
										value="<bean:message key="labels.dict_synonym_button_back"/>" />
								</c:if></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Edit Form: BEGIN --%>

	</tiles:put>
</tiles:insert>