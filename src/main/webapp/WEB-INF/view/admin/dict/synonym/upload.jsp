<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title"/> | <la:message key="labels.dict_synonym_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
	<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
	<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
		<jsp:param name="menuCategoryType" value="system" />
		<jsp:param name="menuType" value="dict" />
	</jsp:include>

	<div class="content-wrapper">

		<%-- Content Header --%>
		<section class="content-header">
		<h1>
			<la:message key="labels.dict_synonym_title" />
		</h1>
		<ol class="breadcrumb">
			<li class="active"><la:link href="list/0?dictId=${dictId}">
			<la:message key="labels.dict_synonym_link_upload" />
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
					<la:message key="labels.dict_synonym_link_upload" />
				</h3>
				<div class="box-tools pull-right">
					<span class="label label-default">
					<la:link href="/admin/dict/">
						<la:message key="labels.dict_list_link" />
					</la:link>
					</span>
					<span class="label label-default">
					<la:link href="list/0/?dictId=${f:u(dictId)}">
						<la:message key="labels.dict_synonym_list_link" />
					</la:link>
					</span>
					<span class="label label-default">
					<la:link href="createpage/${f:u(dictId)}">
						<la:message key="labels.dict_synonym_link_create" />
					</la:link>
					</span>
					<span class="label label-default">
					<la:link href="downloadpage/${f:u(dictId)}">
						<la:message key="labels.dict_synonym_link_download" />
					</la:link>
					</span>
					<span class="label label-default">
					<a href="#">
						<la:message key="labels.dict_synonym_link_upload" />
					</a>
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
							<la:message key="labels.dict_synonym_file" />
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
								   value="<la:message key="labels.dict_synonym_button_upload"/>" />
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

	<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
