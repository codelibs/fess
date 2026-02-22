<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-200)">
	<div>
		<la:message key="labels.pagination_page_guide_msg"
			arg0="${f:h(pager.currentPageNumber)}"
			arg1="${f:h(pager.allPageCount)}"
			arg2="${f:h(pager.allRecordCount)}" />
	</div>
	<ul class="fads-pagination" style="margin-left:auto">
		<c:if test="${pager.existPrePage}">
			<li><la:link
					styleClass=""
					href="list/${pager.currentPageNumber - 1}">
				<la:message key="labels.prev_page" />
			</la:link></li>
		</c:if>
		<c:if test="${!pager.existPrePage}">
			<li class="disabled"><span><la:message
					key="labels.prev_page" /></span></li>
		</c:if>
		<c:forEach var="p" varStatus="s"
			items="${pager.pageNumberList}">
			<li
				<c:if test="${p == pager.currentPageNumber}">class="active"</c:if>><la:link
					styleClass=""
					href="list/${p}">${p}</la:link></li>
		</c:forEach>
		<c:if test="${pager.existNextPage}">
			<li><la:link
					styleClass=""
					href="list/${pager.currentPageNumber + 1}">
					<la:message key="labels.next_page" />
				</la:link></li>
		</c:if>
		<c:if test="${!pager.existNextPage}">
			<li class="disabled"><span><la:message
						key="labels.next_page" /></span></li>
		</c:if>
	</ul>
</div>
