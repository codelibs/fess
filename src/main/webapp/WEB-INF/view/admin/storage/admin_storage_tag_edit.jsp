<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message key="labels.storage_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="storage" />
		</jsp:include>
		<div class="content-wrapper">
			<div class="content-header">
				<div class="container-fluid">
					<div class="row mb-2">
						<div class="col-sm-6">
							<h1>
								<la:message key="labels.storage_configuration" />
							</h1>
						</div>
						<div class="col-sm-6">
							<ol class="breadcrumb float-sm-right">
								<li class="breadcrumb-item"><la:link href="/admin/storage/">
										<la:message key="labels.crud_link_list" />
									</la:link></li>
								<li class="breadcrumb-item active"><la:message key="labels.crud_link_edit" /></li>
							</ol>
						</div>
					</div>
				</div>
			</div>
			<section class="content">
				<la:form action="/admin/storage/">
					<input type="hidden" name="path" value="${f:h(path)}">
					<input type="hidden" name="name" value="${f:h(name)}">
					<div class="row">
						<div class="col-md-12">
							<div class="card card-outline card-success">
								<div class="card-header">
									<h3 class="card-title">
										<la:message key="labels.storage_title_tag" />
										<c:if test="${path!=null}">${f:h(path)}/</c:if>${f:h(name)}
									</h3>
								</div>
								<div class="card-body">
									<div>
										<la:info id="msg" message="true">
											<div class="alert alert-info">${msg}</div>
										</la:info>
										<la:errors property="_global" />
									</div>
									<div class="form-group row">
										<div class="col-sm-6">
											<la:message key="labels.storage_tag_key" />
										</div>
										<div class="col-sm-6">
											<la:message key="labels.storage_tag_value" />
										</div>
									</div>
									<c:forEach var="position" begin="1" end="${savedTags.size()/2}">
									<c:set var="nameKey">name${position}</c:set>
									<c:set var="valueKey">value${position}</c:set>
									<div class="form-group row">
										<div class="col-sm-6">
											<input type="text" id="tags.${f:h(nameKey)}" name="tags.${f:h(nameKey)}" value="${f:h(savedTags.get(nameKey))}" class="form-control" placeholder="Name" >
										</div>
										<div class="col-sm-6">
											<input type="text" id="tags.${f:h(valueKey)}" name="tags.${f:h(valueKey)}" value="${f:h(savedTags.get(valueKey))}" class="form-control" placeholder="Value" >
										</div>
									</div>
									</c:forEach>
									<c:set var="nameKey">name${Double.valueOf(savedTags.size()/2).intValue()+1}</c:set>
									<c:set var="valueKey">value${Double.valueOf(savedTags.size()/2).intValue()+1}</c:set>
									<div class="form-group row">
										<div class="col-sm-6">
											<input type="text" id="tags.${f:h(nameKey)}" name="tags.${f:h(nameKey)}" value="${f:h(savedTags.get(nameKey))}" class="form-control" placeholder="Name" >
										</div>
										<div class="col-sm-6">
											<input type="text" id="tags.${f:h(valueKey)}" name="tags.${f:h(valueKey)}" value="${f:h(savedTags.get(valueKey))}" class="form-control" placeholder="Value" >
										</div>
									</div>
								</div>
								<div class="card-footer">
									<la:link styleClass="btn btn-default" href="list/${parentId}">
										<em class="fa fa-arrow-circle-left"></em>
										<la:message key="labels.crud_button_back" />
									</la:link>
									<c:if test="${editable}">
										<button type="submit" class="btn btn-success" name="updateTags"
											value="<la:message key="labels.crud_button_update" />"
										>
											<em class="fa fa-pencil-alt"></em>
											<la:message key="labels.crud_button_update" />
										</button>
									</c:if>
								</div>
							</div>
						</div>
					</div>
				</la:form>
			</section>
		</div>
		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>

