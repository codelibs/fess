<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%-- query did not match any document --%>
<div id="result" class="row">
	<div class="col-md-8">
		<div class="text-center py-4">
			<p class="mb-3"><i class="fa fa-search fa-3x text-muted" aria-hidden="true"></i></p>
			<p class="mb-2"><la:message key="labels.did_not_match" arg0="${displayQuery}" /></p>
			<p class="text-muted"><la:message key="labels.did_not_match_suggestion" /></p>
			<c:if test="${!empty popularWords}">
				<div class="mt-3">
					<p class="mb-1"><la:message key="labels.search_popular_word_word" /></p>
					<p>
						<c:forEach var="item" varStatus="s" items="${popularWords}">
							<la:link href="/search?q=${f:u(item)}${fe:facetQuery()}${fe:geoQuery()}"
								styleClass="btn btn-outline-secondary btn-sm me-1 mb-1">${f:h(item)}</la:link>
						</c:forEach>
					</p>
				</div>
			</c:if>
		</div>
	</div>
	<div class="col-md-4"><%-- Side Content --%></div>
</div>
