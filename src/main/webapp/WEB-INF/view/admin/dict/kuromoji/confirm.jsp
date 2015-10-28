<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.dict_kuromoji_configuration" /></title>
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
					<la:message key="labels.dict_kuromoji_title" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="list">
							<la:message key="labels.dict_kuromoji_list_link" />
						</la:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><a href="#"><la:message
									key="labels.dict_kuromoji_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><a href="#"><la:message
									key="labels.dict_kuromoji_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
						<li class="active"><a href="#"><la:message
									key="labels.dict_kuromoji_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
						<li class="active"><a href="#"><la:message
									key="labels.dict_kuromoji_link_confirm" /></a></li>
					</c:if>
				</ol>
			</section>

			<section class="content">

				<%-- Form --%>
				<la:form>
					<la:hidden property="crudMode" />
					<la:hidden property="dictId" />
					<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
						<la:hidden property="id" />
					</c:if>
					<div class="row">
						<div class="col-md-12">
							<div
								class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if><c:if test="${crudMode == 3}">box-danger</c:if><c:if test="${crudMode == 4}">box-primary</c:if>">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<la:message key="labels.dict_kuromoji_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.dict_kuromoji_link_update" />
										</c:if>
										<c:if test="${crudMode == 3}">
											<la:message key="labels.dict_kuromoji_link_delete" />
										</c:if>
										<c:if test="${crudMode == 4}">
											<la:message key="labels.dict_kuromoji_link_confirm" />
										</c:if>
									</h3>
									<div class="box-tools pull-right">
										<span class="label label-default"> <la:link
												href="../list">
												<la:message key="labels.dict_list_link" />
											</la:link>
										</span> <span class="label label-default"> <la:link
												href="list/1?dictId=${f:u(dictId)}">
												<la:message key="labels.dict_kuromoji_list_link" />
											</la:link>
										</span>
										<c:if test="${crudMode == 1}">
											<span class="label label-default"> <a href="#"> <la:message
														key="labels.dict_kuromoji_link_create" />
											</a>
											</span>
										</c:if>
										<c:if test="${crudMode == 2}">
											<span class="label label-default"> <a href="#"> <la:message
														key="labels.dict_kuromoji_link_update" />
											</a>
											</span>
										</c:if>
										<c:if test="${crudMode == 3}">
											<span class="label label-default"> <a href="#"> <la:message
														key="labels.dict_kuromoji_link_delete" />
											</a>
											</span>
										</c:if>
										<c:if test="${crudMode == 4}">
											<span class="label label-default"> <a href="#"> <la:message
														key="labels.dict_kuromoji_link_confirm" />
											</a>
											</span>
										</c:if>
										<span class="label label-default"> <la:link
												href="downloadpage/${f:u(dictId)}">
												<la:message key="labels.dict_kuromoji_link_download" />
											</la:link>
										</span> <span class="label label-default"> <la:link
												href="uploadpage/${f:u(dictId)}">
												<la:message key="labels.dict_kuromoji_link_upload" />
											</la:link>
										</span>
									</div>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
									<%-- Message --%>
									<div>
										<la:info id="msg" message="true">
											<div class="alert alert-info">${msg}</div>
										</la:info>
										<la:errors />
									</div>

									<%-- Form Fields --%>
									<table class="table table-bordered">
										<tbody>
											<tr>
												<th style="width: 150px;"><la:message
														key="labels.dict_kuromoji_token" /></th>
												<td>${f:h(token)}<la:hidden property="token" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.dict_kuromoji_segmentation" /></th>
												<td>${f:h(segmentation)}<la:hidden
														property="segmentation" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.dict_kuromoji_reading" /></th>
												<td>${f:h(reading)}<la:hidden property="reading" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.dict_kuromoji_pos" /></th>
												<td>${f:h(pos)}<la:hidden property="pos" /></td>
											</tr>
										</tbody>
									</table>
								</div>

								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<button type="submit" class="btn" name="editagain"
											value="<la:message key="labels.dict_kuromoji_button_back" />">
											<la:message key="labels.dict_kuromoji_button_back" />
										</button>
										<button type="submit" class="btn btn-success" name="create"
											value="<la:message key="labels.dict_kuromoji_button_create" />">
											<la:message key="labels.dict_kuromoji_button_create" />
										</button>
									</c:if>
									<c:if test="${crudMode == 2}">
										<button type="submit" class="btn" name="editagain"
											value="<la:message key="labels.dict_kuromoji_button_back" />">
											<la:message key="labels.dict_kuromoji_button_back" />
										</button>
										<button type="submit" class="btn btn-warning" name="update"
											value="<la:message key="labels.dict_kuromoji_button_update" />">
											<la:message key="labels.dict_kuromoji_button_update" />
										</button>
									</c:if>
									<c:if test="${crudMode == 3}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.dict_kuromoji_button_back" />">
											<la:message key="labels.dict_kuromoji_button_back" />
										</button>
										<button type="submit" class="btn btn-danger" name="delete"
											value="<la:message key="labels.dict_kuromoji_button_delete" />">
											<la:message key="labels.dict_kuromoji_button_delete" />
										</button>
									</c:if>
									<c:if test="${crudMode == 4}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.dict_kuromoji_button_back" />">
											<la:message key="labels.dict_kuromoji_button_back" />
										</button>
										<button type="submit" class="btn btn-warning"
											name="editfromconfirm"
											value="<la:message key="labels.dict_kuromoji_button_edit" />">
											<la:message key="labels.dict_kuromoji_button_edit" />
										</button>
										<button type="submit" class="btn btn-danger"
											name="deletefromconfirm"
											value="<la:message key="labels.dict_kuromoji_button_delete" />">
											<la:message key="labels.dict_kuromoji_button_delete" />
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
