<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp" flush="true">
	<tiles:put name="title"><bean:message key="labels.data_configuration" /></tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="data" />
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
        <h3><bean:message key="labels.backup_title_edit"/></h3>
		<table class="bordered-table zebra-striped">
			<tbody>
				<tr>
					<th style="width:200px;">
						<bean:message key="labels.backup_config"/>
					</th>
					<td>
						<s:link href="download"><bean:message key="labels.download_data"/></s:link>
					</td>
				</tr>
				<tr>
					<th style="width:200px;">
						<bean:message key="labels.backup_session_info"/>
					</th>
					<td>
						<s:link href="downloadCrawlingSession"><bean:message key="labels.download_data_csv"/></s:link>
					</td>
				</tr>
				<tr>
					<th style="width:200px;">
						<bean:message key="labels.backup_search_log"/>
					</th>
					<td>
						<s:link href="downloadSearchLog"><bean:message key="labels.download_data_csv"/></s:link>
					</td>
				</tr>
				<tr>
					<th style="width:200px;">
						<bean:message key="labels.backup_click_log"/>
					</th>
					<td>
						<s:link href="downloadClickLog"><bean:message key="labels.download_data_csv"/></s:link>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	</s:form>
	<s:form action="upload" enctype="multipart/form-data" style="margin-top:10px;">
	<div>
        <h3><bean:message key="labels.restore_title_edit"/></h3>
		<table >
            <tbody>
				<tr>
					<th>
						<bean:message key="labels.restore"/>
					</th>
					<td>
						<input type="file" name="uploadedFile" />
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.overwrite"/>
					</th>
					<td>
						<html:checkbox property="overwrite"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<input type="submit" class="btn medium" name="upload" value="<bean:message key="labels.upload_button"/>"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</div>
	</s:form>

</div>

	</tiles:put>
</tiles:insert>
