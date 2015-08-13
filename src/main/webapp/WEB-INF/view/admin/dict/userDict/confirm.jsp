<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<la:message key="labels.dict_userdict_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="dict" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<la:message key="labels.dict_userdict_title" />
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
					<li><la:link href="../index">
							<la:message key="labels.dict_list_link" />
						</la:link></li>
					<li><la:link href="index?dictId=${f:u(dictId)}">
							<la:message key="labels.dict_userdict_list_link" />
						</la:link></li>
					<c:if test="${crudMode == 1}">
					<li class="active"><a href="#"><la:message
								key="labels.dict_userdict_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
					<li class="active"><a href="#"><la:message
								key="labels.dict_userdict_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
					<li class="active"><a href="#"><la:message
								key="labels.dict_userdict_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
					<li class="active"><a href="#"><la:message
								key="labels.dict_userdict_link_confirm" /></a></li>
					</c:if>
					<li><la:link href="downloadpage?dictId=${f:u(dictId)}">
							<la:message key="labels.dict_userdict_link_download" />
						</la:link></li>
					<li><la:link href="uploadpage?dictId=${f:u(dictId)}">
							<la:message key="labels.dict_userdict_link_upload" />
						</la:link></li>
				</ul>
			</div>

		<%-- Confirm Form: BEGIN --%>
		<s:form>
			<html:hidden property="crudMode" />
			<div>
				<html:hidden property="dictId" />
				<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
					<html:hidden property="id" />
				</c:if>
				<table class="bordered-table zebra-striped" style="width: 500px;">
					<tbody>
						<tr>
							<th style="width: 150px;"><la:message
									key="labels.dict_userdict_token" /></th>
							<td>${f:h(token)}<html:hidden property="token" /></td>
						</tr>
						<tr>
							<th><la:message key="labels.dict_userdict_segmentation" /></th>
							<td>${f:h(segmentation)}<html:hidden property="segmentation" /></td>
						</tr>
						<tr>
							<th><la:message key="labels.dict_userdict_reading" /></th>
							<td>${f:h(reading)}<html:hidden property="reading" /></td>
						</tr>
						<tr>
							<th><la:message key="labels.dict_userdict_pos" /></th>
							<td>${f:h(pos)}<html:hidden property="pos" /></td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><c:if test="${crudMode == 1}">
									<input type="submit" class="btn small" name="create"
										value="<la:message key="labels.dict_userdict_button_create"/>" />
									<input type="submit" class="btn small" name="editagain"
										value="<la:message key="labels.dict_userdict_button_back"/>" />
								</c:if> <c:if test="${crudMode == 2}">
									<input type="submit" class="btn small" name="update"
										value="<la:message key="labels.dict_userdict_button_update"/>" />
									<input type="submit" class="btn small" name="editagain"
										value="<la:message key="labels.dict_userdict_button_back"/>" />
								</c:if> <c:if test="${crudMode == 3}">
									<input type="submit" class="btn small" name="delete"
										value="<la:message key="labels.dict_userdict_button_delete"/>" />
									<input type="submit" class="btn small" name="back"
										value="<la:message key="labels.dict_userdict_button_back"/>" />
								</c:if> <c:if test="${crudMode == 4}">
									<input type="submit" class="btn small" name="back"
										value="<la:message key="labels.dict_userdict_button_back"/>" />
									<input type="submit" class="btn small" name="editfromconfirm"
										value="<la:message key="labels.dict_userdict_button_edit"/>" />
									<input type="submit" class="btn small" name="deletefromconfirm"
										value="<la:message key="labels.dict_userdict_button_delete"/>" />
								</c:if></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Confirm Form: BEGIN --%>

	</tiles:put>
</tiles:insert>
