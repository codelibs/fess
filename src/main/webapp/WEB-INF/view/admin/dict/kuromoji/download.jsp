<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.dict_userdict_configuration" /></title>
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
			<la:message key="labels.dict_userdict_title" />
		</h1>
		<ol class="breadcrumb">
			<li class="active"><la:link href="index">
			<la:message key="labels.dict_userdict_link_download" />
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
					<la:message key="labels.dict_userdict_link_download" />
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
				<la:form>
					<div>
					<la:hidden property="dictId" />
					<table class="table table-bordered table-striped">
						<tbody>
						<tr>
							<th style="">${f:h(filename)}</th>
							<td style="form-control">
							<input type="submit"
								   class="btn small" name="download"
								   value="<la:message key="labels.dict_userdict_button_download"/>" />
							</td>
						</tr>
						</tbody>
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
