<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <bean:message key="labels.dict_synonym_configuration" /></title>
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
			<bean:message key="labels.dict_synonym_title" />
		</h1>
		<ol class="breadcrumb">
			<li class="active"><s:link href="index">
			<bean:message key="labels.dict_synonym_link_upload" />
			</s:link></li>
		</ol>
		</section>

		<section class="content">

		<div class="row">
			<div class="col-md-12">
			<div class="box">
				<%-- Box Header --%>
				<div class="box-header with-border">
				<h3 class="box-title">
					<bean:message key="labels.dict_synonym_link_upload" />
				</h3>
				<div class="box-tools pull-right">
					<span class="label label-default">
					<s:link href="../index">
						<bean:message key="labels.dict_list_link" />
					</s:link>
					</span>
					<span class="label label-default">
					<a href="#">
						<bean:message key="labels.dict_synonym_list_link" />
					</a>
					</span>
					<span class="label label-default">
					<s:link href="createpage?dictId=${f:u(dictId)}">
						<bean:message key="labels.dict_synonym_link_create" />
					</s:link>
					</span>
					<span class="label label-default">
					<s:link href="downloadpage?dictId=${f:u(dictId)}">
						<bean:message key="labels.dict_synonym_link_download" />
					</s:link>
					</span>
					<span class="label label-default">
					<s:link href="uploadpage?dictId=${f:u(dictId)}">
						<bean:message key="labels.dict_synonym_link_upload" />
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

				<%-- Edit Form: BEGIN --%>
				<s:form action="upload" enctype="multipart/form-data">
					<div>
					<html:hidden property="dictId" />
					<table class="table table-bordered table-striped" >
						<tbody>
						<tr>
							<th colspan="2">${f:h(filename)}</th>
						</tr>
						<tr>
							<th>
							<bean:message key="labels.dict_synonym_file" />
							</th>
							<td>
							<input type="file" name="synonymFile"  />
							</td>
						</tr>
						</tbody>
						<tfoot>
						<tr>
							<td colspan="2">
							<input type="submit" class="btn small" name="upload"
								   value="<bean:message key="labels.dict_synonym_button_upload"/>" />
							</td>
						</tr>
						</tfoot>
					</table>
					</div>
				</s:form>
				<%-- Edit Form: BEGIN --%>

				</div>
			</div>
			</div>
		</div>

		</section>
	</div>

	<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin2/foot.jsp"></jsp:include>
</body>
</html>
