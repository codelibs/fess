<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.crawler_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="crawl" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.crawler_title_edit" />
				</h1>
			</section>

			<section class="content">

				<%-- Form --%>
				<s:form>
					<div class="row">
						<div class="col-md-12">
							<div class="box">
								<%-- Box Header --%>
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
									<div class="form-group">
											<label for="searchLog"><la:message key="labels.search_log_enabled" /></label>
											<div styleClass="form-inline" >
													<html:checkbox property="searchLog" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="userInfo"><la:message key="labels.user_info_enabled" /></label>
											<div styleClass="form-inline" >
													<html:checkbox property="userInfo" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="userFavorite"><la:message key="labels.user_favorite_enabled" /></label>
											<div styleClass="form-inline" >
													<html:checkbox property="userFavorite" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="appendQueryParameter"><la:message key="labels.append_query_param_enabled" /></label>
											<div styleClass="form-inline" >
													<html:checkbox property="appendQueryParameter" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="webApiXml"><la:message key="labels.web_api_xml_enabled" /></label>
											<div styleClass="form-inline" >
													<html:checkbox property="webApiXml" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="webApiJson"><la:message key="labels.web_api_json_enabled" /></label>
											<div styleClass="form-inline" >
													<html:checkbox property="webApiJson" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="defaultLabelValue"><la:message key="labels.default_label_value" /></label>
											<html:textarea property="defaultLabelValue" styleClass="form-control" />
									</div>
									<div class="form-group">
											<label for="supportedSearch"><la:message key="labels.supported_search_feature" /></label>
											<div class="form-inline">
													<html:select property="supportedSearch" styleClass="form-control">
															<c:forEach var="item" items="${supportedSearchItems}">
																	<html:option value="${f:u(item.value)}">${f:h(item.label)}</html:option>
															</c:forEach>
													</html:select>
											</div>
									</div>
									<div class="form-group">
											<label for="hotSearchWord"><la:message key="labels.hot_search_word_enabled" /></label>
											<div styleClass="form-inline" >
													<html:checkbox property="hotSearchWord" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="purgeSearchLogDay"><la:message key="labels.purge_search_log_day" /></label>
											<div class="form-inline">
													<html:text property="purgeSearchLogDay" styleClass="form-control" />
											</div>
									</div>
									<div class="form-group">
											<label for="purgeJobLogDay"><la:message key="labels.purge_job_log_day" /></label>
											<div class="form-inline">
													<html:text property="purgeJobLogDay" styleClass="form-control" />
											</div>
									</div>
									<div class="form-group">
											<label for="purgeUserInfoDay"><la:message key="labels.purge_user_info_day" /></label>
											<div class="form-inline">
													<html:text property="purgeUserInfoDay" styleClass="form-control" />
											</div>
									</div>
									<div class="form-group">
											<label for="purgeByBots"><la:message key="labels.purge_by_bots" /></label>
											<html:text property="purgeByBots" styleClass="form-control" />
									</div>
									<div class="form-group">
											<label for="notificationTo"><la:message key="labels.notification_to" /></label>
											<html:text property="notificationTo" styleClass="form-control" />
									</div>
									<div class="form-group">
											<label for="csvFileEncoding"><la:message key="labels.csv_file_encoding" /></label>
											<div class="form-inline">
													<html:text property="csvFileEncoding" styleClass="form-control" />
											</div>
									</div>
									<div class="form-group">
											<label for="diffCrawling"><la:message key="labels.diff_crawling" /></label>
											<div styleClass="form-inline" >
													<html:checkbox property="diffCrawling" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="useAclAsRole"><la:message key="labels.use_acl_as_role" /></label>
											<div styleClass="form-inline" >
													<html:checkbox property="useAclAsRole" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="serverRotation"><la:message key="labels.server_rotation" /></label>
											<div styleClass="form-inline" >
													<html:checkbox property="serverRotation" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="crawlingThreadCount"><la:message key="labels.crawling_thread_count" /></label>
											<div class="form-inline">
													<html:text property="crawlingThreadCount" styleClass="form-control" />
											</div>
									</div>
									<div class="form-group">
											<label for="dayForCleanup"><la:message key="labels.day_for_cleanup" /></label>
											<div class="form-inline">
													<html:select property="dayForCleanup" styleClass="form-control">
															<html:option value="-1"><la:message key="labels.none"/></html:option>
															<c:forEach var="d" items="${dayItems}">
																	<html:option value="${f:h(d)}">${f:h(d)}</html:option>
															</c:forEach>
													</html:select>
													<la:message key="labels.day"/>
											</div>
									</div>
									<div class="form-group">
											<label for="ignoreFailureType"><la:message key="labels.ignore_failure_type" /></label>
											<html:text property="ignoreFailureType" styleClass="form-control" />
									</div>
									<div class="form-group">
											<label for="failureCountThreshold"><la:message key="labels.failure_count_threshold" /></label>
											<div class="form-inline">
													<html:text property="failureCountThreshold" styleClass="form-control" />
											</div>
									</div>
									<div class="form-group">
											<label for="suggestSearchLog"><la:message key="labels.suggest_search_log_enabled" /></label>
											<div styleClass="form-inline" >
													<html:checkbox property="suggestSearchLog" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="purgeSuggestSearchLogDay"><la:message key="labels.purge_suggest_search_log_day" /></label>
											<div class="form-inline">
													<html:text property="purgeSuggestSearchLogDay" styleClass="form-control" />
											</div>
									</div>

								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
										<input type="submit" class="btn small btn-primary" name="update"
													 value="<la:message key="labels.crawl_button_update"/>"
										/>
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
