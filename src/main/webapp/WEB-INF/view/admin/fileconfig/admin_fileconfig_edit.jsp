<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.file_crawling_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="fileConfig" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.file_crawling_title_details" />
				</h1>
				<jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
			</section>
			<section class="content">
				<la:form action="/admin/fileconfig/" styleClass="form-horizontal">
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2}">
						<la:hidden property="id" />
						<la:hidden property="versionNo" />
					</c:if>
					<la:hidden property="createdBy" />
					<la:hidden property="createdTime" />
					<la:hidden property="sortOrder" />
					<div class="row">
						<div class="col-md-12">
							<div
								class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if>">
								<div class="box-header with-border">
									<jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
								</div>
								<!-- /.box-header -->
								<div class="box-body">
									<div>
										<la:info id="msg" message="true">
											<div class="alert alert-info">${msg}</div>
										</la:info>
										<la:errors property="_global" />
									</div>
									<div class="form-group">
										<label for="name" class="col-sm-3 control-label"><la:message
												key="labels.name" /></label>
										<div class="col-sm-9">
											<la:errors property="name" />
											<la:text styleId="name" property="name" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="paths" class="col-sm-3 control-label"><la:message
												key="labels.paths" /></label>
										<div class="col-sm-9">
											<la:errors property="paths" />
											<la:textarea styleId="paths" property="paths" styleClass="form-control"
												rows="5" />
										</div>
									</div>
									<div class="form-group">
										<label for="includedPaths" class="col-sm-3 control-label"><la:message
												key="labels.included_paths" /></label>
										<div class="col-sm-9">
											<la:errors property="includedPaths" />
											<la:textarea styleId="includedPaths" property="includedPaths"
												styleClass="form-control" rows="5" />
										</div>
									</div>
									<div class="form-group">
										<label for="excludedPaths" class="col-sm-3 control-label"><la:message
												key="labels.excluded_paths" /></label>
										<div class="col-sm-9">
											<la:errors property="excludedPaths" />
											<la:textarea styleId="excludedPaths" property="excludedPaths"
												styleClass="form-control" rows="5" />
										</div>
									</div>
									<div class="form-group">
										<label for="includedDocPaths" class="col-sm-3 control-label"><la:message
												key="labels.included_doc_paths" /></label>
										<div class="col-sm-9">
											<la:errors property="includedDocPaths" />
											<la:textarea styleId="includedDocPaths" property="includedDocPaths"
												styleClass="form-control" rows="5" />
										</div>
									</div>
									<div class="form-group">
										<label for="excludedDocPaths" class="col-sm-3 control-label"><la:message
												key="labels.excluded_doc_paths" /></label>
										<div class="col-sm-9">
											<la:errors property="excludedDocPaths" />
											<la:textarea styleId="excludedDocPaths" property="excludedDocPaths"
												styleClass="form-control" rows="5" />
										</div>
									</div>
									<div class="form-group">
										<label for="configParameter" class="col-sm-3 control-label"><la:message
												key="labels.config_parameter" /></label>
										<div class="col-sm-9">
											<la:errors property="configParameter" />
											<la:textarea styleId="configParameter" property="configParameter"
												styleClass="form-control" rows="5" />
										</div>
									</div>
									<div class="form-group">
										<label for="depth" class="col-sm-3 control-label"><la:message
												key="labels.depth" /></label>
										<div class="form-inline col-sm-9">
											<la:errors property="depth" />
											<input type="number" name="depth" id="depth"
												value="${f:h(depth)}" class="form-control"
												min="0" max="10000">
										</div>
									</div>
									<div class="form-group">
										<label for="maxAccessCount" class="col-sm-3 control-label"><la:message
												key="labels.max_access_count" /></label>
										<div class="form-inline col-sm-9">
											<la:errors property="maxAccessCount" />
											<input type="number" name="maxAccessCount" id="maxAccessCount"
												value="${f:h(maxAccessCount)}" class="form-control"
												min="0" max="1000000000">
										</div>
									</div>
									<div class="form-group">
										<label for="numOfThread" class="col-sm-3 control-label"><la:message
												key="labels.number_of_thread" /></label>
										<div class="form-inline col-sm-9">
											<la:errors property="numOfThread" />
											<input type="number" name="numOfThread" id="numOfThread"
												value="${f:h(numOfThread)}" class="form-control"
												min="1" max="1000">
										</div>
									</div>
									<div class="form-group">
										<label for="intervalTime" class="col-sm-3 control-label"><la:message
												key="labels.interval_time" /></label>
										<div class="form-inline col-sm-9">
											<la:errors property="intervalTime" />
											<input type="number" name="intervalTime" id="intervalTime"
												value="${f:h(intervalTime)}" class="form-control"
												min="0">
											<la:message key="labels.millisec" />
										</div>
									</div>
									<div class="form-group">
										<label for="boost" class="col-sm-3 control-label"><la:message
												key="labels.boost" /></label>
										<div class="col-sm-9">
											<la:errors property="boost" />
											<la:text styleId="boost" property="boost" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="permissions" class="col-sm-3 control-label"><la:message
												key="labels.permissions" /></label>
										<div class="col-sm-9">
											<la:errors property="permissions" />
											<la:textarea styleId="permissions" property="permissions" styleClass="form-control"
												rows="5" />
										</div>
									</div>
									<div class="form-group">
										<label for="virtualHosts" class="col-sm-3 control-label"><la:message
												key="labels.virtual_hosts" /></label>
										<div class="col-sm-9">
											<la:errors property="virtualHosts" />
											<la:textarea styleId="virtualHosts" property="virtualHosts" styleClass="form-control"
												rows="5" />
										</div>
									</div>
									<div class="form-group"<c:if test="${!labelSettingEnabled}"> style="display:none"</c:if>>
										<label for="labelTypeIds" class="col-sm-3 control-label"><la:message
												key="labels.label_type" /></label>
										<div class="col-sm-9">
											<la:errors property="labelTypeIds" />
											<la:select styleId="labelTypeIds" property="labelTypeIds" multiple="true"
												styleClass="form-control">
												<c:forEach var="l" varStatus="s" items="${labelTypeItems}">
													<la:option value="${f:u(l.id)}">${f:h(l.name)}</la:option>
												</c:forEach>
											</la:select>
										</div>
									</div>
									<div class="form-group">
										<label for="available" class="col-sm-3 control-label"><la:message
												key="labels.available" /></label>
										<div class="col-sm-9">
											<la:errors property="available" />
											<la:select styleId="available" property="available" styleClass="form-control">
												<la:option value="true">
													<la:message key="labels.enabled" />
												</la:option>
												<la:option value="false">
													<la:message key="labels.disabled" />
												</la:option>
											</la:select>
										</div>
									</div>
									<div class="form-group">
										<label for="permissions" class="col-sm-3 control-label"><la:message
												key="labels.description" /></label>
										<div class="col-sm-9">
											<la:errors property="description" />
											<la:textarea styleId="description" property="description" styleClass="form-control"
												rows="5" />
										</div>
									</div>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<jsp:include page="/WEB-INF/view/common/admin/crud/buttons.jsp"></jsp:include>
								</div>
								<!-- /.box-footer -->
							</div>
							<!-- /.box -->
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
