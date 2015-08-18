<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <bean:message key="labels.dict_userdict_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
	<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
	<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
		<jsp:param name="menuCategoryType" value="crawl" />
		<jsp:param name="menuType" value="dict" />
	</jsp:include>

	<div class="content-wrapper">

		<%-- Content Header --%>
		<section class="content-header">
		<h1>
			<bean:message key="labels.dict_userdict_title" />
		</h1>
		<ol class="breadcrumb">
			<li><s:link href="index">
			<bean:message key="labels.dict_userdict_list_link" />
			</s:link></li>
			<c:if test="${crudMode == 1}">
			<li class="active"><a href="#"><bean:message key="labels.dict_userdict_link_create" /></a></li>
			</c:if>
			<c:if test="${crudMode == 2}">
			<li class="active"><a href="#"><bean:message key="labels.dict_userdict_link_update" /></a></li>
			</c:if>
			<c:if test="${crudMode == 3}">
			<li class="active"><a href="#"><bean:message key="labels.dict_userdict_link_delete" /></a></li>
			</c:if>
			<c:if test="${crudMode == 4}">
			<li class="active"><a href="#"><bean:message key="labels.dict_userdict_link_confirm" /></a></li>
			</c:if>
		</ol>
		</section>

		<section class="content">

		<%-- Form --%>
		<s:form>
			<html:hidden property="crudMode" />
			<div class="row">
			<div class="col-md-12">
				<div class="box">
				<%-- Box Header --%>
				<div class="box-header with-border">
					<h3 class="box-title">
					<c:if test="${crudMode == 1}">
						<bean:message key="labels.dict_userdict_link_create" />
					</c:if>
					<c:if test="${crudMode == 2}">
						<bean:message key="labels.dict_userdict_link_update" />
					</c:if>
					<c:if test="${crudMode == 3}">
						<bean:message key="labels.dict_userdict_link_delete" />
					</c:if>
					<c:if test="${crudMode == 4}">
						<bean:message key="labels.dict_userdict_link_confirm" />
					</c:if>
					</h3>
					<div class="box-tools pull-right">
					<span class="label label-default">
						<s:link href="../index">
						<bean:message key="labels.dict_list_link" />
						</s:link>
					</span>
					<span class="label label-default">
						<s:link href="index?dictId=${f:u(dictId)}">
						<bean:message key="labels.dict_userdict_list_link" />
						</s:link>
					</span>
					<c:if test="${crudMode == 1}">
						<span class="label label-default">
						<a href="#">
							<bean:message key="labels.dict_userdict_link_create" />
						</a>
						</span>
					</c:if>
					<c:if test="${crudMode == 2}">
						<span class="label label-default">
						<a href="#">
							<bean:message key="labels.dict_userdict_link_update" />
						</a>
						</span>
					</c:if>
					<c:if test="${crudMode == 3}">
						<span class="label label-default">
						<a href="#">
							<bean:message key="labels.dict_userdict_link_delete" />
						</a>
						</span>
					</c:if>
					<c:if test="${crudMode == 4}">
						<span class="label label-default">
						<a href="#">
							<bean:message key="labels.dict_userdict_link_confirm" />
						</a>
						</span>
					</c:if>
					<span class="label label-default">
						<s:link href="downloadpage?dictId=${f:u(dictId)}">
						<bean:message key="labels.dict_userdict_link_download" />
						</s:link>
					</span>
					<span class="label label-default">
						<s:link href="uploadpage?dictId=${f:u(dictId)}">
						<bean:message key="labels.dict_userdict_link_upload" />
						</s:link>
					</span>
					</div>
				</div>
				<%-- Box Body --%>
				<div class="box-body">
					<%-- Message --%>
					<div>
					<html:messages id="msg" message="true">
						<div class="alert-message info">
						<bean:write name="msg" ignore="true" />
						</div>
					</html:messages>
					<html:errors />
					</div>

					<%-- Form Fields --%>
					<table class="table table-bordered">
					    <tbody>
						<tr>
							<th style="width: 150px;"><bean:message
									key="labels.dict_userdict_token" /></th>
							<td>${f:h(token)}<html:hidden property="token" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.dict_userdict_segmentation" /></th>
							<td>${f:h(segmentation)}<html:hidden property="segmentation" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.dict_userdict_reading" /></th>
							<td>${f:h(reading)}<html:hidden property="reading" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.dict_userdict_pos" /></th>
							<td>${f:h(pos)}<html:hidden property="pos" /></td>
						</tr>
					    </tbody>
					</table>
				</div>

				<%-- Box Footer --%>
				<div class="box-footer">
					<c:if test="${crudMode == 1}">
					<input type="submit" class="btn" name="editagain" value="<bean:message key="labels.dict_userdict_button_back"/>" />
					<input type="submit" class="btn btn-primary" name="create"
					       value="<bean:message key="labels.dict_userdict_button_create"/>"
					/>
					</c:if>
					<c:if test="${crudMode == 2}">
					<input type="submit" class="btn" name="editagain" value="<bean:message key="labels.dict_userdict_button_back"/>" />
					<input type="submit" class="btn btn-primary" name="update"
					       value="<bean:message key="labels.dict_userdict_button_update"/>"
					/>
					</c:if>
					<c:if test="${crudMode == 3}">
					<input type="submit" class="btn" name="back" value="<bean:message key="labels.dict_userdict_button_back"/>" />
					<input type="submit" class="btn btn-primary" name="delete"
					       value="<bean:message key="labels.dict_userdict_button_delete"/>"
					/>
					</c:if>
					<c:if test="${crudMode == 4}">
					<input type="submit" class="btn" name="back" value="<bean:message key="labels.dict_userdict_button_back"/>" />
					<input type="submit" class="btn" name="editfromconfirm"
					       value="<bean:message key="labels.dict_userdict_button_edit"/>"
					/>
					<input type="submit" class="btn" name="deletefromconfirm"
					       value="<bean:message key="labels.dict_userdict_button_delete"/>"
					/>
					</c:if>
				</div>
				</div>
			</div>
			</div>
		</s:form>

		</section>
	</div>

	<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin2/foot.jsp"></jsp:include>
</body>
</html>
