<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <bean:message key="labels.file_crawling_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="fileConfig" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<bean:message key="labels.file_crawling_title_details" />
				</h1>
				<ol class="breadcrumb">
					<li><s:link href="index">
							<bean:message key="labels.file_crawling_link_list" />
						</s:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><a href="#"><bean:message key="labels.file_crawling_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><a href="#"><bean:message key="labels.file_crawling_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
						<li class="active"><a href="#"><bean:message key="labels.file_crawling_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
						<li class="active"><a href="#"><bean:message key="labels.file_crawling_link_confirm" /></a></li>
					</c:if>
				</ol>
			</section>

			<section class="content">

				<%-- Form --%>
				<s:form>
					<html:hidden property="crudMode" />
					<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
						<html:hidden property="id" />
						<html:hidden property="versionNo" />
					</c:if>
					<html:hidden property="createdBy" />
					<html:hidden property="createdTime" />
					<div class="row">
						<div class="col-md-12">
							<div class="box">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<bean:message key="labels.file_crawling_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<bean:message key="labels.file_crawling_link_update" />
										</c:if>
										<c:if test="${crudMode == 3}">
											<bean:message key="labels.file_crawling_link_delete" />
										</c:if>
										<c:if test="${crudMode == 4}">
											<bean:message key="labels.file_crawling_link_confirm" />
										</c:if>
									</h3>
									<div class="box-tools pull-right">
										<span class="label label-default"><s:link href="index">
												<bean:message key="labels.file_crawling_link_list" />
											</s:link></span>
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
											<c:if test="${id != null}"><tr>
												<th class="col-xs-3"><bean:message key="labels.id" /></th>
												<td>${f:h(id)}</td>
											</tr></c:if>
											<tr>
												<th class="col-xs-3"><bean:message key="labels.name" /></th>
												<td>${f:h(name)}<html:hidden property="name" /></td>
											</tr>
											<tr>
												<th><bean:message key="labels.paths" /></th>
												<td>${f:br(f:h(paths))}<html:hidden property="paths" /></td>
											</tr>
											<tr>
												<th><bean:message key="labels.included_paths" /></th>
												<td>${f:br(f:h(includedPaths))}<html:hidden property="includedPaths" /></td>
											</tr>
											<tr>
												<th><bean:message key="labels.excluded_paths" /></th>
												<td>${f:br(f:h(excludedPaths))}<html:hidden property="excludedPaths" /></td>
											</tr>
											<tr>
												<th><bean:message key="labels.included_doc_paths" /></th>
												<td>${f:br(f:h(includedDocPaths))}<html:hidden property="includedDocPaths" /></td>
											</tr>
											<tr>
												<th><bean:message key="labels.excluded_doc_paths" /></th>
												<td>${f:br(f:h(excludedDocPaths))}<html:hidden property="excludedDocPaths" /></td>
											</tr>
											<tr>
												<th><bean:message key="labels.config_parameter" /></th>
												<td>${f:br(f:h(configParameter))}<html:hidden property="configParameter" /></td>
											</tr>
											<tr>
												<th><bean:message key="labels.depth" /></th>
												<td>${f:h(depth)}<html:hidden property="depth" /></td>
											</tr>
											<tr>
												<th><bean:message key="labels.max_access_count" /></th>
												<td>${f:h(maxAccessCount)}<html:hidden property="maxAccessCount" /></td>
											</tr>
											<tr>
												<th><bean:message key="labels.number_of_thread" /></th>
												<td>${f:h(numOfThread)}<html:hidden property="numOfThread" /></td>
											</tr>
											<tr>
												<th><bean:message key="labels.interval_time" /></th>
												<td>${f:h(intervalTime)}<html:hidden property="intervalTime" /><bean:message key="labels.millisec"/></td>
											</tr>
											<tr>
												<th><bean:message key="labels.boost" /></th>
												<td>${f:h(boost)}<html:hidden property="boost" /></td>
											</tr>
											<tr>
												<th><bean:message key="labels.role_type" /></th>
													<td>
														<c:forEach var="rt" varStatus="s" items="${roleTypeItems}">
															<c:forEach var="rtid" varStatus="s" items="${roleTypeIds}">
																<c:if test="${rtid==rt.id}">
																	${f:h(rt.name)}<br/>
																</c:if>
															</c:forEach>
														</c:forEach>
														<html:select property="roleTypeIds" multiple="true" style="display:none;">
															<c:forEach var="rt" varStatus="s" items="${roleTypeItems}">
																<html:option value="${f:u(rt.id)}">${f:h(rt.name)}</html:option>
															</c:forEach>
														</html:select>
													</td>
											</tr>
											<tr>
												<th><bean:message key="labels.label_type" /></th>
												<td>
													<c:forEach var="l" varStatus="s" items="${labelTypeItems}">
														<c:forEach var="ltid" varStatus="s" items="${labelTypeIds}">
															<c:if test="${ltid==l.id}">
																${f:h(l.name)}<br/>
															</c:if>
														</c:forEach>
													</c:forEach>
													<html:select property="labelTypeIds" multiple="true" style="display:none;">
														<c:forEach var="l" varStatus="s" items="${labelTypeItems}">
															<html:option value="${f:u(l.id)}">${f:h(l.name)}</html:option>
														</c:forEach>
													</html:select>
												</td>
											</tr>
											<tr>
												<th><bean:message key="labels.available"/></th>
												<td><html:hidden property="available"/>
													<c:if test="${available=='true'}"><bean:message key="labels.enabled"/></c:if>
													<c:if test="${available=='false'}"><bean:message key="labels.disabled"/></c:if>
												</td>
											</tr>
										</tbody>
									</table>
								</div>

								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<input type="submit" class="btn" name="editagain" value="<bean:message key="labels.file_crawling_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="create"
											value="<bean:message key="labels.file_crawling_button_create"/>"
										/>
									</c:if>
									<c:if test="${crudMode == 2}">
										<input type="submit" class="btn" name="editagain" value="<bean:message key="labels.file_crawling_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="update"
											value="<bean:message key="labels.file_crawling_button_update"/>"
										/>
									</c:if>
									<c:if test="${crudMode == 3}">
										<input type="submit" class="btn" name="back" value="<bean:message key="labels.file_crawling_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="delete"
											value="<bean:message key="labels.file_crawling_button_delete"/>"
										/>
									</c:if>
									<c:if test="${crudMode == 4}">
										<input type="submit" class="btn" name="back" value="<bean:message key="labels.file_crawling_button_back"/>" />
										<input type="submit" class="btn" name="editfromconfirm"
											value="<bean:message key="labels.file_crawling_button_edit"/>"
										/>
										<input type="submit" class="btn" name="deletefromconfirm"
											value="<bean:message key="labels.file_crawling_button_delete"/>"
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

