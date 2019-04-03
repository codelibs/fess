<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<h3 class="box-title">
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
<div class="btn-group pull-right">
	<c:choose>
		<c:when test="${crudMode == null}">
			<la:link href="createnew" styleClass="btn btn-success btn-xs">
				<i class="fa fa-plus"></i>
				<la:message key="labels.crud_link_create" />
			</la:link>
		</c:when>
		<c:otherwise>
			<la:link href="../list" styleClass="btn btn-primary btn-xs">
				<i class="fa fa-th-list"></i>
				<la:message key="labels.crud_link_list" />
			</la:link>
		</c:otherwise>
	</c:choose>
</div>