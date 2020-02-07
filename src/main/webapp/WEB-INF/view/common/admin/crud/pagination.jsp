<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="row">
	<div class="col-sm-2">
		<la:message key="labels.pagination_page_guide_msg"
			arg0="${f:h(pager.currentPageNumber)}"
			arg1="${f:h(pager.allPageCount)}"
			arg2="${f:h(pager.allRecordCount)}" />
	</div>
	<nav aria-label="...">
	</nav>
	<div class="col-sm-10">
		<ul class="pagination pagination-sm m-0 float-right">
			<c:if test="${pager.existPrePage}">
				<li class="page-item"><la:link
						styleClass="page-link"
						href="list/${pager.currentPageNumber - 1}">
					<la:message key="labels.prev_page" />
				</la:link></li>
			</c:if>
			<c:if test="${!pager.existPrePage}">
				<li class="page-item disabled"><a
						class="page-link" href="#"><la:message
						key="labels.prev_page" /></a></li>
			</c:if>
			<c:forEach var="p" varStatus="s"
				items="${pager.pageNumberList}">
				<li
					<c:if test="${p == pager.currentPageNumber}">class="page-item active"</c:if>><la:link
						styleClass="page-link"
						href="list/${p}">${p}</la:link></li>
			</c:forEach>
			<c:if test="${pager.existNextPage}">
				<li class="page-item"><la:link
						styleClass="page-link"
						href="list/${pager.currentPageNumber + 1}">
						<la:message key="labels.next_page" />
					</la:link></li>
			</c:if>
			<c:if test="${!pager.existNextPage}">
				<li class="page-item disabled"><a
						class="page-link" href="#"><la:message
							key="labels.next_page" /></a></li>
			</c:if>
		</ul>
	</div>
</div>
