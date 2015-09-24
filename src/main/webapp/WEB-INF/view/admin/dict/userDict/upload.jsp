<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.dict_userdict_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="dict" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.dict_userdict_title" />
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
							<bean:message key="labels.dict_userdict_list_link" />
						</s:link></li>
					<li><s:link href="createpage?dictId=${f:u(dictId)}">
							<bean:message key="labels.dict_userdict_link_create" />
						</s:link></li>
					<li><s:link href="downloadpage?dictId=${f:u(dictId)}">
							<bean:message key="labels.dict_userdict_link_download" />
						</s:link></li>
					<li class="active"><a href="#">
							<bean:message key="labels.dict_userdict_link_upload" />
						</a></li>
					<li><s:link href="reloadpage?dictId=${f:u(dictId)}">
							<bean:message key="labels.dict_userdict_link_reload" />
						</s:link></li>
				</ul>
			</div>

		<%-- Edit Form: BEGIN --%>
		<s:form action="upload" enctype="multipart/form-data">
			<div>
				<html:hidden property="dictId" />
				<table class="bordered-table zebra-striped" style="width: 500px;">
					<tbody>
						<tr>
							<th colspan="2">${f:h(filename)}</th>
						</tr>
						<tr>
							<th style="width: 150px;"><bean:message
									key="labels.dict_userdict_file" /></th>
							<td><input type="file"
										name="userDictFile" style="width: 98%;" /></td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2" style="text-align: center;">
									<input type="submit" class="btn small" name="upload"
										value="<bean:message key="labels.dict_userdict_button_upload"/>" />
							</td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Edit Form: BEGIN --%>

	</tiles:put>
</tiles:insert>
