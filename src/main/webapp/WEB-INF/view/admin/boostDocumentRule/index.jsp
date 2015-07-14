<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <bean:message key="labels.boost_document_rule_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="boostDocumentRule" />
		</jsp:include>
		
		<div class="content-wrapper">
			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<bean:message key="labels.boost_document_rule_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><s:link href="index">
							<bean:message key="labels.boost_document_rule_link_list" />
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
									<bean:message key="labels.boost_document_rule_link_list" />
								</h3>
								<div class="box-tools pull-right">
									<span class="label label-default"><s:link href="createpage">
											<bean:message key="labels.boost_document_rule_link_create_new" />
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
								<%-- List --%>
								<c:if test="${boostDocumentRulePager.allRecordCount == 0}">
									<p class="alert-message warning">
										<bean:message key="labels.list_could_not_find_crud_table" />
									</p>
								</c:if>
								<c:if test="${boostDocumentRulePager.allRecordCount > 0}">
									<table class="table table-bordered table-striped">
										<thead>
											<tr>
												<th><bean:message key="labels.boost_document_rule_list_url_expr" /></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="data" varStatus="s" items="${boostDocumentRuleItems}">
												<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}" data-href="confirmpage/4/${f:u(data.id)}">
													<td>${f:h(data.urlExpr)}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</c:if>

							</div>
							<%-- Box Footer --%>
							<div class="box-footer">
								<%-- Paging Info --%>
								<span><bean:message key="labels.pagination_page_guide_msg" arg0="${f:h(boostDocumentRulePager.currentPageNumber)}"
										arg1="${f:h(boostDocumentRulePager.allPageCount)}" arg2="${f:h(boostDocumentRulePager.allRecordCount)}"
									/></span>

								<%-- Paging Navigation --%>
								<ul class="pagination pagination-sm no-margin pull-right">
									<c:if test="${boostDocumentRulePager.existPrePage}">
										<li class="prev"><s:link href="list/${boostDocumentRulePager.currentPageNumber - 1}">
												<bean:message key="labels.boost_document_rule_link_prev_page" />
											</s:link></li>
									</c:if>
									<c:if test="${!boostDocumentRulePager.existPrePage}">
										<li class="prev disabled"><a href="#"><bean:message key="labels.boost_document_rule_link_prev_page" /></a></li>
									</c:if>
									<c:forEach var="p" varStatus="s" items="${boostDocumentRulePager.pageNumberList}">
										<li <c:if test="${p == boostDocumentRulePager.currentPageNumber}">class="active"</c:if>><s:link href="list/${p}">${p}</s:link>
										</li>
									</c:forEach>
									<c:if test="${boostDocumentRulePager.existNextPage}">
										<li class="next"><s:link href="list/${boostDocumentRulePager.currentPageNumber + 1}">
												<bean:message key="labels.boost_document_rule_link_next_page" />
											</s:link></li>
									</c:if>
									<c:if test="${!boostDocumentRulePager.existNextPage}">
										<li class="next disabled"><a href="#"><bean:message key="labels.boost_document_rule_link_next_page" /></a></li>
									</c:if>
								</ul>

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
