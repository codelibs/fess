<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.dict_userdict_configuration" /></title>
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
			<la:message key="labels.dict_userdict_title" />
		</h1>
		<ol class="breadcrumb">
			<li class="active"><la:link href="index">
			<la:message key="labels.dict_userdict_link_upload" />
			</la:link></li>
		</ol>
		</section>

		<section class="content">

		<div class="row">
			<div class="col-md-12">
			<div class="box">
				<%-- Box Header --%>
				<div class="box-header with-border">
				<h3 class="box-title">
					<la:message key="labels.dict_userdict_link_upload" />
				</h3>
				<div class="box-tools pull-right">
					<span class="label label-default">
					<la:link href="../index">
						<la:message key="labels.dict_list_link" />
					</la:link>
					</span>
					<span class="label label-default">
					<a href="#">
						<la:message key="labels.dict_userdict_list_link" />
					</a>
					</span>
					<span class="label label-default">
					<la:link href="createpage?dictId=${f:u(dictId)}">
						<la:message key="labels.dict_userdict_link_create" />
					</la:link>
					</span>
					<span class="label label-default">
					<la:link href="downloadpage?dictId=${f:u(dictId)}">
						<la:message key="labels.dict_userdict_link_download" />
					</la:link>
					</span>
					<span class="label label-default">
					<la:link href="uploadpage?dictId=${f:u(dictId)}">
						<la:message key="labels.dict_userdict_link_upload" />
					</la:link>
					</span>
				</div>
				</div>
				<%-- Box Body --%>
				<div class="box-body">
				<%-- Message --%>
				<div>
					<la:info id="msg" message="true">
					<div class="alert-message info">
						${msg}
					</div>
					</la:info>
					<la:errors />
				</div>

				<%-- Edit Form: BEGIN --%>
				<la:form action="upload" enctype="multipart/form-data">
					<div>
					<la:hidden property="dictId" />
					<table class="table table-bordered table-striped" >
						<tbody>
						<tr>
							<th colspan="2">${f:h(filename)}</th>
						</tr>
						<tr>
							<th>
							<la:message key="labels.dict_userdict_file" />
							</th>
							<td>
							<input type="file" name="userdictFile"  />
							</td>
						</tr>
						</tbody>
						<tfoot>
						<tr>
							<td colspan="2">
							<input type="submit" class="btn small" name="upload"
								   value="<la:message key="labels.dict_userdict_button_upload"/>" />
							</td>
						</tr>
						</tfoot>
					</table>
					</div>
				</la:form>
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
