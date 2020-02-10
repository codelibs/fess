<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<c:if test="${crudMode == 1}">
	<button type="submit" class="btn btn-default" name="list"
		value="<la:message key="labels.crud_button_back" />">
		<em class="fa fa-arrow-circle-left"></em>
		<la:message key="labels.crud_button_back" />
	</button>
	<c:if test="${editable}">
	<button type="submit" class="btn btn-success" name="create"
		value="<la:message key="labels.crud_button_create" />">
		<em class="fa fa-plus"></em>
		<la:message key="labels.crud_button_create" />
	</button>
	</c:if>
</c:if>
<c:if test="${crudMode == 2}">
	<button type="submit" class="btn btn-default" name="edit" value="back">
		<em class="fa fa-arrow-circle-left"></em>
		<la:message key="labels.crud_button_back" />
	</button>
	<c:if test="${editable}">
	<button type="submit" class="btn btn-success" name="update"
		value="<la:message key="labels.crud_button_update" />">
		<em class="fa fa-pencil-alt"></em>
		<la:message key="labels.crud_button_update" />
	</button>
	</c:if>
</c:if>
<c:if test="${crudMode == 4}">
	<button type="submit" class="btn btn-default" name="list" value="back">
		<em class="fa fa-arrow-circle-left"></em>
		<la:message key="labels.crud_button_back" />
	</button>
	<c:if test="${editable}">
	<button type="submit" class="btn btn-primary" name="edit"
		value="<la:message key="labels.crud_button_edit" />">
		<em class="fa fa-pencil-alt"></em>
		<la:message key="labels.crud_button_edit" />
	</button>
	<button type="button" class="btn btn-danger" name="delete"
		data-toggle="modal" data-target="#confirmToDelete"
		value="<la:message key="labels.crud_button_delete" />">
		<em class="fa fa-trash"></em>
		<la:message key="labels.crud_button_delete" />
	</button>
	<div class="modal fade" id="confirmToDelete" tabindex="-1"
		role="dialog">
		<div class="modal-dialog">
			<div class="modal-content bg-danger">
				<div class="modal-header">
					<h4 class="modal-title">
						<la:message key="labels.crud_title_delete" />
					</h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<p>
						<la:message key="labels.crud_delete_confirmation" />
					</p>
				</div>
				<div class="modal-footer justify-content-between">
					<button type="button" class="btn btn-outline-light"
						data-dismiss="modal">
						<la:message key="labels.crud_button_cancel" />
					</button>
					<button type="submit" class="btn btn-outline-light"
						name="delete"
						value="<la:message key="labels.crud_button_delete" />">
						<em class="fa fa-trash"></em>
						<la:message key="labels.crud_button_delete" />
					</button>
				</div>
			</div>
		</div>
	</div>
	</c:if>
</c:if>
