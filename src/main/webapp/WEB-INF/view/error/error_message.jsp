<c:import url="${viewPrefix}/common/header.jsp">
	<c:param name="title" value="エラー通知"/>
</c:import>
<div class="contents">
	<h2><bean:message key="labels.header.title.error.message"/></h2>
	<html:errors/>
	<div class="listback">
		<s:link href="/member/list/">会員一覧へ</s:link>
	</div>
</div>
<c:import url="${viewPrefix}/common/footer.jsp"/>