<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<c:if test="${crudMode == 1}">
	<button type="submit" class="fads-btn fads-btn-default" name="list"
		value="<la:message key="labels.crud_button_back" />">
		<i class="fa fa-arrow-circle-left" aria-hidden="true"></i>
		<la:message key="labels.crud_button_back" />
	</button>
	<c:if test="${editable}">
	<button type="submit" class="fads-btn fads-btn-success" name="create"
		value="<la:message key="labels.crud_button_create" />">
		<i class="fa fa-plus" aria-hidden="true"></i>
		<la:message key="labels.crud_button_create" />
	</button>
	</c:if>
</c:if>
<c:if test="${crudMode == 2}">
	<button type="submit" class="fads-btn fads-btn-default" name="edit" value="back">
		<i class="fa fa-arrow-circle-left" aria-hidden="true"></i>
		<la:message key="labels.crud_button_back" />
	</button>
	<c:if test="${editable}">
	<button type="submit" class="fads-btn fads-btn-success" name="update"
		value="<la:message key="labels.crud_button_update" />">
		<i class="fa fa-pencil-alt" aria-hidden="true"></i>
		<la:message key="labels.crud_button_update" />
	</button>
	</c:if>
</c:if>
<c:if test="${crudMode == 4}">
	<button type="submit" class="fads-btn fads-btn-default" name="list" value="back">
		<i class="fa fa-arrow-circle-left" aria-hidden="true"></i>
		<la:message key="labels.crud_button_back" />
	</button>
	<c:if test="${editable}">
	<button type="submit" class="fads-btn fads-btn-primary" name="edit"
		value="<la:message key="labels.crud_button_edit" />">
		<i class="fa fa-pencil-alt" aria-hidden="true"></i>
		<la:message key="labels.crud_button_edit" />
	</button>
	<button type="button" class="fads-btn fads-btn-danger" name="delete"
		data-fads-dialog="confirmToDelete"
		value="<la:message key="labels.crud_button_delete" />">
		<i class="fa fa-trash" aria-hidden="true"></i>
		<la:message key="labels.crud_button_delete" />
	</button>
	<div class="fads-dialog-overlay" id="confirmToDelete" tabindex="-1"
		role="dialog" aria-hidden="true">
		<div class="fads-dialog fads-dialog-danger">
			<div class="fads-dialog-header">
				<h4>
					<la:message key="labels.crud_title_delete" />
				</h4>
				<button type="button" class="fads-btn-icon" data-fads-dialog-close
					aria-label="Close">
					<span aria-hidden="true">&#x00D7;</span>
				</button>
			</div>
			<div class="fads-dialog-body">
				<p>
					<la:message key="labels.crud_delete_confirmation" />
				</p>
			</div>
			<div class="fads-dialog-footer">
				<button type="button" class="fads-btn fads-btn-default"
					data-fads-dialog-close>
					<la:message key="labels.crud_button_cancel" />
				</button>
				<button type="submit" class="fads-btn fads-btn-danger"
					name="delete"
					value="<la:message key="labels.crud_button_delete" />">
					<i class="fa fa-trash" aria-hidden="true"></i>
					<la:message key="labels.crud_button_delete" />
				</button>
			</div>
		</div>
	</div>
	</c:if>
</c:if>
