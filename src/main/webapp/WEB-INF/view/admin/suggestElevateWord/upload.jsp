<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.suggest_elevate_word_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="suggestElevateWord" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.suggest_elevate_word_title_list" />
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
						<bean:message key="labels.suggest_elevate_word_link_list" />
					</s:link></li>
					<li><s:link href="createpage">
						<bean:message key="labels.suggest_elevate_word_link_create_new" />
					</s:link></li>
					<li><s:link href="downloadpage">
						<bean:message key="labels.suggest_elevate_word_link_download" />
					</s:link></li>
					<li class="active"><a href="#">
						<bean:message key="labels.suggest_elevate_word_link_upload" />
					</a></li>
				</ul>
			</div>

 	 	<%-- Edit Form: BEGIN --%>
		<s:form action="upload" enctype="multipart/form-data">
			<div>
				<table class="bordered-table zebra-striped" style="width: 500px;">
					<tbody>
						<tr>
							<th style="width: 150px; vertical-align: middle;"><bean:message
									key="labels.suggest_elevate_word_file" /></th>
							<td><input type="file"
									   name="suggestElevateWordFile" style="width: 98%;" /></td>
						</tr>
 	 	 	 	 	</tbody>
 	 	 	 	 	<tfoot>
						<tr>
							<td colspan="2">
								<input type="submit" class="btn small" name="upload"
									   value="<bean:message key="labels.suggest_elevate_word_button_upload"/>" />
							</td>
						</tr>
 	 	 	 	 	</tfoot>
 	 	 	 	</table>
 	 	 	</div>
 	 	</s:form>
 	 	<%-- Edit Form: BEGIN --%>
	</tiles:put>
</tiles:insert>
