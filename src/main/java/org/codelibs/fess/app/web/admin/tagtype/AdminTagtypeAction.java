/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.app.web.admin.tagtype;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.TagTypePager;
import org.codelibs.fess.app.service.RoleTypeService;
import org.codelibs.fess.app.service.TagTypeService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.entity.TagChange;
import org.codelibs.fess.exception.TagTypeConflictException;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.TagTypeHelper;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;

import jakarta.annotation.Resource;

/**
 * Admin action for the tags users own. An administrator manages the tags of every user.
 *
 * <p>The id of a tag is derived from its name and owner, so a change of either replaces the tag
 * with a new one and renames the tag value on the documents. A change of the paths adds the tag
 * to, or removes it from, the documents of the changed URLs. Those document changes are queued
 * only while {@code user.tag.enabled} is true; otherwise the {@code tag_updater} job rebuilds the
 * tag field later. Every write checks that the tag did not change since it was read.</p>
 */
public class AdminTagtypeAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminTagtypeAction() {
    }

    /** The role name for tag type administration. */
    public static final String ROLE = "admin-tagtype";

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(AdminTagtypeAction.class);

    /** Thrown when a tag of the same name and owner exists already. */
    public static class TagTypeExistsException extends TagTypeConflictException {

        private static final long serialVersionUID = 1L;

        /**
         * Creates the exception.
         *
         * @param message the message
         * @param cause the cause
         */
        public TagTypeExistsException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Service for tag type operations. */
    @Resource
    private TagTypeService tagTypeService;

    /** Pager for tag type list pagination. */
    @Resource
    private TagTypePager tagTypePager;

    /** Service for role type operations. */
    @Resource
    private RoleTypeService roleTypeService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameTagtype()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Displays the tag type list page.
     *
     * @param form the search form
     * @return HTML response for the list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final SearchForm form) {
        return asListHtml();
    }

    /**
     * Displays the tag type list with pagination.
     *
     * @param pageNumber the page number
     * @param form the search form
     * @return HTML response for the list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            tagTypePager.setCurrentPageNumber(limitPageNumber(pageNumber.get(), tagTypePager.getPageSize()));
        }).orElse(() -> {
            tagTypePager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminTagtype_AdminTagtypeJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Searches tag types by name and owner.
     *
     * @param form the search form
     * @return HTML response for the search results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, tagTypePager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminTagtype_AdminTagtypeJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Resets the search criteria and displays the default list.
     *
     * @param form the search form
     * @return HTML response for the reset list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        tagTypePager.clear();
        return asHtml(path_AdminTagtype_AdminTagtypeJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Sets up data for search result pagination.
     *
     * @param data the render data
     * @param form the search form
     */
    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "tagTypeItems", tagTypeService.getTagTypeList(tagTypePager)); // page navi

        // restore from pager
        copyBeanToBean(tagTypePager, form, op -> op.include("name", "owner"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    /**
     * Displays the create new tag type page.
     *
     * @return HTML response for the create page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew() {
        saveToken();
        return asHtml(path_AdminTagtype_AdminTagtypeEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        }).renderWith(data -> {
            registerRoleTypeItems(data);
        });
    }

    /**
     * Displays the edit tag type page.
     *
     * @param form the edit form
     * @return HTML response for the edit page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        final String id = form.id;
        tagTypeService.getTagType(id).ifPresent(entity -> {
            copyToForm(entity, form);
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml);
        });
        saveToken();
        if (form.crudMode.intValue() == CrudMode.EDIT) {
            // back
            form.crudMode = CrudMode.DETAILS;
            return asDetailsHtml();
        }
        form.crudMode = CrudMode.EDIT;
        return asEditHtml();
    }

    /**
     * Displays the tag type details page.
     *
     * @param crudMode the CRUD mode
     * @param id the tag type ID
     * @return HTML response for the details page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS, this::asListHtml);
        saveToken();
        final var optEntity = verifyFound(tagTypeService.getTagType(id), id, this::asListHtml);
        return asHtml(path_AdminTagtype_AdminTagtypeDetailsJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                optEntity.ifPresent(entity -> {
                    copyToForm(entity, form);
                    form.crudMode = crudMode;
                });
            });
        }).renderWith(data -> {
            registerRoleTypeItems(data);
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    /**
     * Creates a new tag type.
     *
     * @param form the create form
     * @return HTML response after creation
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE, this::asListHtml);
        validate(form, messages -> validateTagType(form, messages), this::asEditHtml);
        verifyToken(this::asEditHtml);
        try {
            createTagType(tagTypeService, form);
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        } catch (final TagTypeExistsException e) {
            throwValidationError(messages -> messages.addErrorsTagtypeAlreadyExists(GLOBAL), this::asEditHtml);
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)),
                    this::asEditHtml);
        }
        return redirect(getClass());
    }

    /**
     * Updates an existing tag type.
     *
     * @param form the edit form
     * @return HTML response after update
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT, this::asListHtml);
        validate(form, messages -> validateTagType(form, messages), this::asEditHtml);
        verifyToken(this::asEditHtml);
        final OptionalEntity<TagType> result;
        try {
            result = updateTagType(tagTypeService, form);
        } catch (final TagTypeExistsException e) {
            throwValidationError(messages -> messages.addErrorsTagtypeAlreadyExists(GLOBAL), this::asEditHtml);
            return null;
        } catch (final TagTypeConflictException e) {
            throwValidationError(messages -> messages.addErrorsTagtypeChangedConcurrently(GLOBAL), this::asEditHtml);
            return null;
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationError(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)),
                    this::asEditHtml);
            return null;
        }
        result.ifPresent(entity -> {
            saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), this::asEditHtml);
        });
        return redirect(getClass());
    }

    /**
     * Deletes a tag type.
     *
     * @param form the edit form
     * @return HTML response after deletion
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS, this::asListHtml);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        final String id = form.id;
        tagTypeService.getTagType(id).ifPresent(entity -> {
            // delete the tag only as it was shown
            entity.setSeqNo(form.seqNo);
            entity.setPrimaryTerm(form.primaryTerm);
            try {
                deleteTagType(tagTypeService, entity);
                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
            } catch (final TagTypeConflictException e) {
                throwValidationError(messages -> messages.addErrorsTagtypeChangedConcurrently(GLOBAL), this::asDetailsHtml);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asDetailsHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asDetailsHtml);
        });
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    /**
     * Validates the fields of a tag type that the annotations of the form cannot: the name must be
     * a valid tag name, the owner must not be blank and the number of paths must not exceed
     * {@code user.tag.max.paths}.
     *
     * @param form the form
     * @param messages the messages to add the errors to
     */
    public static void validateTagType(final CreateForm form, final FessMessages messages) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (form.name != null && ComponentUtil.getTagTypeHelper().normalizeName(form.name).isEmpty()) {
            messages.addErrorsTagtypeInvalidName("name", String.valueOf(fessConfig.getUserTagNameMaxLengthAsInteger()));
        }
        if (form.owner != null && StringUtil.isBlank(form.owner)) {
            messages.addConstraintsRequiredMessage("owner");
        }
        final int maxPaths = fessConfig.getUserTagMaxPathsAsInteger();
        if (splitPaths(form.paths).length > maxPaths) {
            messages.addErrorsTagtypeTooManyPaths("paths", String.valueOf(maxPaths));
        }
    }

    /**
     * Splits the paths of a form: one URL per line, trimmed, without blank lines and duplicates.
     *
     * @param paths the paths as entered
     * @return the paths
     */
    public static String[] splitPaths(final String paths) {
        if (StringUtil.isBlank(paths)) {
            return new String[0];
        }
        return Arrays.stream(paths.split("\r\n|[\r\n]")).map(String::trim).filter(StringUtil::isNotBlank).distinct().toArray(String[]::new);
    }

    /**
     * Creates a tag type from a validated form and queues adding the tag to the documents of its paths.
     *
     * @param tagTypeService the tag type service
     * @param form the validated form
     * @return the created tag type
     * @throws TagTypeExistsException if a tag of the same name and owner exists
     */
    public static TagType createTagType(final TagTypeService tagTypeService, final CreateForm form) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        final TagType tagType = new TagType();
        tagType.setCreatedBy(username);
        tagType.setCreatedTime(currentTime);
        copyToEntity(form, tagType, username, currentTime);
        try {
            tagTypeService.insert(tagType);
        } catch (final TagTypeConflictException e) {
            throw new TagTypeExistsException("The tag type already exists: id=" + tagType.getId(), e);
        }
        final String value = tagType.getTagValue();
        for (final String path : tagType.getPaths()) {
            enqueue(TagChange.add(value, path));
        }
        return tagType;
    }

    /**
     * Updates a tag type from a validated form and queues the changes of the documents: a rename
     * when the name or the owner changed, then the tag added to the documents of new paths and
     * removed from those of dropped ones. The write fails when the tag type changed since the
     * version the form carries was read.
     *
     * @param tagTypeService the tag type service
     * @param form the validated form
     * @return the updated tag type, or empty when there is no tag type of the id
     * @throws TagTypeExistsException if the name or owner changed to those of another tag
     * @throws TagTypeConflictException if the tag type changed since it was read
     */
    public static OptionalEntity<TagType> updateTagType(final TagTypeService tagTypeService, final EditForm form) {
        return tagTypeService.getTagType(form.id).map(current -> {
            current.setSeqNo(form.seqNo);
            current.setPrimaryTerm(form.primaryTerm);
            final String oldValue = current.getTagValue();
            final String[] oldPaths = current.getPaths() == null ? new String[0] : current.getPaths();
            final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
            final TagType edited = new TagType();
            edited.setCreatedBy(current.getCreatedBy());
            edited.setCreatedTime(current.getCreatedTime());
            copyToEntity(form, edited, systemHelper.getUsername(), systemHelper.getCurrentTimeAsLong());
            if (!current.getOwner().equals(edited.getOwner())) {
                edited.setPermissions(moveOwnerPermission(edited.getPermissions(), current.getOwner(), edited.getOwner()));
            }
            final String newValue = edited.getTagValue();
            if (oldValue.equals(newValue)) {
                edited.setSeqNo(current.getSeqNo());
                edited.setPrimaryTerm(current.getPrimaryTerm());
                tagTypeService.update(edited);
            } else {
                final boolean replaced;
                try {
                    replaced = tagTypeService.replace(current, edited);
                } catch (final TagTypeConflictException e) {
                    throw new TagTypeExistsException("The tag type already exists: id=" + edited.getId(), e);
                }
                if (!replaced) {
                    throw new TagTypeConflictException("The tag type was changed concurrently: id=" + current.getId(), null);
                }
                enqueue(TagChange.rename(oldValue, newValue));
            }
            final Set<String> oldPathSet = new LinkedHashSet<>(Arrays.asList(oldPaths));
            final Set<String> newPathSet = new LinkedHashSet<>(Arrays.asList(edited.getPaths()));
            newPathSet.stream().filter(path -> !oldPathSet.contains(path)).forEach(path -> enqueue(TagChange.add(newValue, path)));
            oldPathSet.stream().filter(path -> !newPathSet.contains(path)).forEach(path -> enqueue(TagChange.remove(newValue, path)));
            return edited;
        });
    }

    /**
     * Replaces the user role of the old owner with that of the new owner, so that the old owner
     * does not keep seeing the tag. The sharing roles and any other role are kept.
     *
     * @param permissions the permissions as entered
     * @param oldOwner the owner the tag had
     * @param newOwner the owner the tag gets
     * @return the permissions
     */
    protected static String[] moveOwnerPermission(final String[] permissions, final String oldOwner, final String newOwner) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final Set<String> oldRoles = new HashSet<>();
        oldRoles.add(systemHelper.getSearchRoleByDirectoryUser(oldOwner));
        oldRoles.add(systemHelper.getSearchRoleByUser(oldOwner));
        final String newRole = systemHelper.getSearchRoleByDirectoryUser(newOwner);
        final Set<String> moved = new LinkedHashSet<>();
        for (final String permission : permissions) {
            moved.add(oldRoles.contains(permission) ? newRole : permission);
        }
        return moved.toArray(String[]::new);
    }

    /**
     * Deletes a tag type and queues removing the tag from the documents.
     *
     * @param tagTypeService the tag type service
     * @param tagType the tag type with the sequence number and primary term it was read with
     * @throws TagTypeConflictException if the tag type changed since it was read
     */
    public static void deleteTagType(final TagTypeService tagTypeService, final TagType tagType) {
        tagTypeService.delete(tagType);
        enqueue(TagChange.delete(tagType.getTagValue()));
    }

    /**
     * Copies a tag type to a form: the paths and the decoded permissions one per line, and the
     * sequence number and primary term that a later write checks.
     *
     * @param entity the tag type
     * @param form the form
     */
    public static void copyToForm(final TagType entity, final EditForm form) {
        form.id = entity.getId();
        form.seqNo = entity.getSeqNo();
        form.primaryTerm = entity.getPrimaryTerm();
        form.name = entity.getName();
        form.owner = entity.getOwner();
        form.paths = entity.getPaths() == null ? null : String.join("\n", entity.getPaths());
        final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
        form.permissions = stream(entity.getPermissions()).get(
                stream -> stream.map(permissionHelper::decode).filter(StringUtil::isNotBlank).distinct().collect(Collectors.joining("\n")));
        form.virtualHost = entity.getVirtualHost();
        form.sortOrder = entity.getSortOrder();
    }

    /**
     * Copies a validated form to a tag type and sets its id. The permissions default to the owner.
     *
     * @param form the form
     * @param tagType the tag type
     * @param username the user who makes the change
     * @param currentTime the time of the change
     */
    protected static void copyToEntity(final CreateForm form, final TagType tagType, final String username, final long currentTime) {
        final TagTypeHelper tagTypeHelper = ComponentUtil.getTagTypeHelper();
        final String owner = form.owner.trim();
        tagType.setName(tagTypeHelper.normalizeName(form.name).orElseThrow(() -> new IllegalArgumentException("invalid tag name")));
        tagType.setOwner(owner);
        tagType.setId(tagTypeHelper.toId(tagType.getTagValue()));
        tagType.setPaths(splitPaths(form.paths));
        final String[] permissions = encodePermissions(form.permissions);
        tagType.setPermissions(permissions.length == 0 ? tagTypeHelper.buildPermissions(owner, false) : permissions);
        tagType.setVirtualHost(StringUtil.isBlank(form.virtualHost) ? StringUtil.EMPTY : form.virtualHost.trim());
        tagType.setSortOrder(form.sortOrder == null ? 0 : form.sortOrder);
        tagType.setUpdatedBy(username);
        tagType.setUpdatedTime(currentTime);
    }

    /**
     * Queues a change of the tag field of the documents while user tags are enabled.
     *
     * @param change the change
     */
    protected static void enqueue(final TagChange change) {
        if (ComponentUtil.getFessConfig().isUserTagEnabled()) {
            ComponentUtil.getTagTypeHelper().enqueue(change);
        }
    }

    /**
     * Registers role type items for the dropdown list.
     *
     * @param data the render data
     */
    protected void registerRoleTypeItems(final RenderData data) {
        RenderDataUtil.register(data, "roleTypeItems", roleTypeService.getRoleTypeList());
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========
    private HtmlResponse asListHtml() {
        return asHtml(path_AdminTagtype_AdminTagtypeJsp).renderWith(data -> {
            RenderDataUtil.register(data, "tagTypeItems", tagTypeService.getTagTypeList(tagTypePager)); // page navi
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(tagTypePager, form, op -> op.include("name", "owner"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminTagtype_AdminTagtypeEditJsp).renderWith(data -> {
            registerRoleTypeItems(data);
        });
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminTagtype_AdminTagtypeDetailsJsp).renderWith(data -> {
            registerRoleTypeItems(data);
        });
    }
}
