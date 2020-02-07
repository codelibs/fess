<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<ol class="breadcrumb float-sm-right">
	<c:if test="${crudMode == null}">
		<li class="breadcrumb-item active"><la:message key="labels.crud_link_list" /></li>
	</c:if>
	<c:if test="${crudMode != null}">
		<li class="breadcrumb-item"><la:link href="../list">
				<la:message key="labels.crud_link_list" />
			</la:link></li>
	</c:if>
	<c:if test="${crudMode == 1}">
		<li class="breadcrumb-item active"><la:message key="labels.crud_link_create" /></li>
	</c:if>
	<c:if test="${crudMode == 2}">
		<li class="breadcrumb-item active"><la:message key="labels.crud_link_edit" /></li>
	</c:if>
	<c:if test="${crudMode == 3}">
		<li class="breadcrumb-item active"><la:message key="labels.crud_link_delete" /></li>
	</c:if>
	<c:if test="${crudMode == 4}">
		<li class="breadcrumb-item active"><la:message key="labels.crud_link_details" /></li>
	</c:if>
</ol>