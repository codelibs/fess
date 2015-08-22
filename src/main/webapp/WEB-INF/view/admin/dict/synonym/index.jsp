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
			<li class="active"><la:link href="index">
			<bean:message key="labels.dict_synonym_list_link" />
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
					<bean:message key="labels.dict_synonym_list_link" />
				</h3>
				<div class="box-tools pull-right">
					<span class="label label-default">
					<la:link href="../index">
						<bean:message key="labels.dict_list_link" />
					</la:link>
					</span>
					<span class="label label-default">
					<a href="#">
						<bean:message key="labels.dict_synonym_list_link" />
					</a>
					</span>
					<span class="label label-default">
					<la:link href="createpage?dictId=${f:u(dictId)}">
						<bean:message key="labels.dict_synonym_link_create" />
					</la:link>
					</span>
					<span class="label label-default">
					<la:link href="downloadpage?dictId=${f:u(dictId)}">
						<bean:message key="labels.dict_synonym_link_download" />
					</la:link>
					</span>
					<span class="label label-default">
					<la:link href="uploadpage?dictId=${f:u(dictId)}">
						<bean:message key="labels.dict_synonym_link_upload" />
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
						<bean:write name="msg" ignore="true" />
					</div>
					</la:info>
					<la:errors />
				</div>
				<%-- List --%>
				<c:if test="${synonymPager.allRecordCount == 0}">
					<p class="alert-message warning">
					<bean:message key="labels.list_could_not_find_crud_table" />
					</p>
				</c:if>
				<c:if test="${synonymPager.allRecordCount > 0}">
					<table class="table table-bordered table-striped">
					<thead>
						<tr>
						<th> <bean:message key="labels.dict_synonym_source" /> </th>
						<th> <bean:message key="labels.dict_synonym_target" /> </th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="data" varStatus="s" items="${synonymItemItems}">
						<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}" data-href="confirmpage/${f:u(dictId)}/4/${f:u(data.id)}">
							<td>${f:h(data.inputs)}</td>
							<td>${f:h(data.outputs)}</td>
						</tr>
						</c:forEach>
					</tbody>
					</table>
				</c:if>
				</div>
				<%-- Box Footer --%>
				<div class="box-footer">
				<%-- Paging Info --%>
				<span><bean:message key="labels.pagination_page_guide_msg" arg0="${f:h(synonymPager.currentPageNumber)}"
							arg1="${f:h(synonymPager.allPageCount)}" arg2="${f:h(synonymPager.allRecordCount)}"
					  /></span>

				<%-- Paging Navigation --%>
				<ul class="pagination pagination-sm no-margin pull-right">
					<c:if test="${synonymPager.existPrePage}">
					<li class="prev"><la:link href="list/${synonymPager.currentPageNumber - 1}">
						<bean:message key="labels.dict_link_prev_page" />
					</la:link></li>
					</c:if>
					<c:if test="${!synonymPager.existPrePage}">
					<li class="prev disabled"><a href="#"><bean:message key="labels.dict_link_prev_page" /></a></li>
					</c:if>
					<c:forEach var="p" varStatus="s" items="${synonymPager.pageNumberList}">
					<li <c:if test="${p == synonymPager.currentPageNumber}">class="active"</c:if>><la:link href="list/${p}">${p}</la:link>
					</li>
					</c:forEach>
					<c:if test="${synonymPager.existNextPage}">
					<li class="next"><la:link href="list/${synonymPager.currentPageNumber + 1}">
						<bean:message key="labels.dict_link_next_page" />
					</la:link></li>
					</c:if>
					<c:if test="${!synonymPager.existNextPage}">
					<li class="next disabled"><a href="#"><bean:message key="labels.dict_link_next_page" /></a></li>
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
