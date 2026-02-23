<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<c:if test="${crudMode == 1}">
	<button type="submit" class="btn btn-outline-secondary" name="list"
		value="<la:message key="labels.crud_button_back" />">
		<i class="fa fa-arrow-circle-left" aria-hidden="true"></i>
		<la:message key="labels.crud_button_back" />
	</button>
	<c:if test="${editable}">
	<button type="submit" class="btn btn-primary" name="create"
		value="<la:message key="labels.crud_button_create" />">
		<i class="fa fa-plus" aria-hidden="true"></i>
		<la:message key="labels.crud_button_create" />
	</button>
	</c:if>
</c:if>
<c:if test="${crudMode == 2}">
	<button type="submit" class="btn btn-outline-secondary" name="edit" value="back">
		<i class="fa fa-arrow-circle-left" aria-hidden="true"></i>
		<la:message key="labels.crud_button_back" />
	</button>
	<c:if test="${editable}">
	<button type="submit" class="btn btn-primary" name="update"
		value="<la:message key="labels.crud_button_update" />">
		<i class="fa fa-pencil-alt" aria-hidden="true"></i>
		<la:message key="labels.crud_button_update" />
	</button>
	</c:if>
</c:if>
<c:if test="${crudMode == 4}">
	<button type="submit" class="btn btn-outline-secondary" name="list" value="back">
		<i class="fa fa-arrow-circle-left" aria-hidden="true"></i>
		<la:message key="labels.crud_button_back" />
	</button>
	<c:if test="${editable}">
	<button type="submit" class="btn btn-primary" name="edit"
		value="<la:message key="labels.crud_button_edit" />">
		<i class="fa fa-pencil-alt" aria-hidden="true"></i>
		<la:message key="labels.crud_button_edit" />
	</button>
	<button type="button" class="btn btn-danger" name="delete"
		data-toggle="modal" data-target="#confirmToDelete"
		value="<la:message key="labels.crud_button_delete" />">
		<i class="fa fa-trash" aria-hidden="true"></i>
		<la:message key="labels.crud_button_delete" />
	</button>
	<div class="modal fade" id="confirmToDelete" tabindex="-1"
		role="alertdialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" style="border-bottom: 2px solid #dc3545;">
					<h4 class="modal-title">
						<i class="fa fa-exclamation-triangle text-danger" aria-hidden="true"></i>
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
					<button type="button" class="btn btn-secondary" autofocus
						data-dismiss="modal">
						<la:message key="labels.crud_button_cancel" />
					</button>
					<button type="submit" class="btn btn-danger"
						name="delete"
						value="<la:message key="labels.crud_button_delete" />">
						<i class="fa fa-trash" aria-hidden="true"></i>
						<la:message key="labels.crud_button_delete" />
					</button>
				</div>
			</div>
		</div>
	</div>
	</c:if>
</c:if>
