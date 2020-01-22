<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<h3 class="card-title">
	<c:if test="${crudMode == null}">
		<la:message key="labels.crud_title_list" />
	</c:if>
	<c:if test="${crudMode == 1}">
		<la:message key="labels.crud_title_create" />
	</c:if>
	<c:if test="${crudMode == 2}">
		<la:message key="labels.crud_title_edit" />
	</c:if>
	<c:if test="${crudMode == 3}">
		<la:message key="labels.crud_title_delete" />
	</c:if>
	<c:if test="${crudMode == 4}">
		<la:message key="labels.crud_title_details" />
	</c:if>
</h3>
<div class="card-tools">
	<c:choose>
		<c:when test="${crudMode == null}">
			<la:link href="createnew" styleClass="btn btn-success btn-xs ${f:h(editableClass)}">
				<em class="fa fa-plus"></em>
				<la:message key="labels.crud_link_create" />
			</la:link>
		</c:when>
		<c:otherwise>
			<la:link href="../list" styleClass="btn btn-primary btn-xs">
				<em class="fa fa-th-list"></em>
				<la:message key="labels.crud_link_list" />
			</la:link>
		</c:otherwise>
	</c:choose>
</div>
