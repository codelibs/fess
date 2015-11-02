<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<ol class="breadcrumb">
	<li><la:link href="list">
			<la:message key="labels.crud_link_list" />
		</la:link></li>
	<c:if test="${crudMode == 1}">
		<li class="active"><a href="#"><la:message
					key="labels.crud_link_create" /></a></li>
	</c:if>
	<c:if test="${crudMode == 2}">
		<li class="active"><a href="#"><la:message
					key="labels.crud_link_edit" /></a></li>
	</c:if>
	<c:if test="${crudMode == 3}">
		<li class="active"><a href="#"><la:message
					key="labels.crud_link_delete" /></a></li>
	</c:if>
	<c:if test="${crudMode == 4}">
		<li class="active"><a href="#"><la:message
					key="labels.crud_link_details" /></a></li>
	</c:if>
</ol>