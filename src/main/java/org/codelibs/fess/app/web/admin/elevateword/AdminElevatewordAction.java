/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.elevateword;

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.concurrent.CommonPoolUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.ElevateWordPager;
import org.codelibs.fess.app.service.ElevateWordService;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.config.exentity.ElevateWord;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;

/**
 * @author Keiichi Watanabe
 */
public class AdminElevatewordAction extends FessAdminAction {

    public static final String ROLE = "admin-elevateword";

    private static final Logger logger = LogManager.getLogger(AdminElevatewordAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private ElevateWordService elevateWordService;
    @Resource
    private ElevateWordPager elevateWordPager;
    @Resource
    protected SuggestHelper suggestHelper;
    @Resource
    private LabelTypeService labelTypeService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameElevateword()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        return asListHtml();
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            elevateWordPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            elevateWordPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminElevateword_AdminElevatewordJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, elevateWordPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminElevateword_AdminElevatewordJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        elevateWordPager.clear();
        return asHtml(path_AdminElevateword_AdminElevatewordJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "elevateWordItems", elevateWordService.getElevateWordList(elevateWordPager)); // page navi

        // restore from pager
        copyBeanToBean(elevateWordPager, form, op -> op.include("id"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew() {
        saveToken();
        return asHtml(path_AdminElevateword_AdminElevatewordEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        }).renderWith(data -> {
            registerLabels(data);
        });
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        final String id = form.id;
        elevateWordService.getElevateWord(id).ifPresent(entity -> {
            copyBeanToBean(entity, form, copyOp -> {
                copyOp.excludeNull();
                copyOp.exclude(Constants.PERMISSIONS);
            });
            final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
            form.permissions = stream(entity.getPermissions()).get(stream -> stream.map(s -> permissionHelper.decode(s))
                    .filter(StringUtil::isNotBlank).distinct().collect(Collectors.joining("\n")));
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

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
        return asHtml(path_AdminElevateword_AdminElevatewordDetailsJsp).useForm(EditForm.class, op -> op.setup(form -> {
            elevateWordService.getElevateWord(id).ifPresent(entity -> {
                copyBeanToBean(entity, form, copyOp -> {
                    copyOp.excludeNull();
                    copyOp.exclude(Constants.PERMISSIONS);
                });
                form.permissions = stream(entity.getPermissions()).get(stream -> stream.map(permissionHelper::decode)
                        .filter(StringUtil::isNotBlank).distinct().collect(Collectors.joining("\n")));
                form.crudMode = crudMode;
            }).orElse(() -> throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml));
        })).renderWith(this::registerLabels);
    }

    // -----------------------------------------------------
    //                                              Download
    //                                               -------
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse downloadpage() {
        saveToken();
        return asDownloadHtml();
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public ActionResponse download(final DownloadForm form) {
        verifyToken(this::asDownloadHtml);

        return asStream("elevate.csv").contentTypeOctetStream().stream(out -> {
            final Path tempFile = ComponentUtil.getSystemHelper().createTempFile("fess-elevate-", ".csv").toPath();
            try {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(tempFile), getCsvEncoding()))) {
                    elevateWordService.exportCsv(writer);
                } catch (final Exception e) {
                    logger.warn("Failed to process a request.", e);
                    throwValidationError(messages -> messages.addErrorsFailedToDownloadElevateFile(GLOBAL), this::asDownloadHtml);
                }
                try (InputStream in = Files.newInputStream(tempFile)) {
                    out.write(in);
                }
            } finally {
                Files.delete(tempFile);
            }
        });
    }

    // -----------------------------------------------------
    //                                                Upload
    //                                               -------
    @Execute
    @Secured({ ROLE })
    public HtmlResponse uploadpage() {
        saveToken();
        return asUploadHtml();
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getElevateWord(form).ifPresent(entity -> {
            try {
                elevateWordService.store(entity);
                suggestHelper.addElevateWord(entity.getSuggestWord(), entity.getReading(), entity.getLabelTypeValues(),
                        entity.getPermissions(), entity.getBoost(), false);
                saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL), this::asEditHtml);
        });
        return redirect(getClass());
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getElevateWord(form).ifPresent(entity -> {
            try {
                elevateWordService.store(entity);
                suggestHelper.deleteAllElevateWord(false);
                suggestHelper.storeAllElevateWords(false);
                saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), this::asEditHtml);
        });
        return redirect(getClass());
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        final String id = form.id;
        elevateWordService.getElevateWord(id).ifPresent(entity -> {
            try {
                elevateWordService.delete(entity);
                suggestHelper.deleteElevateWord(entity.getSuggestWord(), false);
                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asDetailsHtml));
        return redirect(getClass());
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse upload(final UploadForm form) {
        validate(form, messages -> {}, this::asUploadHtml);
        verifyToken(this::asUploadHtml);
        CommonPoolUtil.execute(() -> {
            try (Reader reader = new BufferedReader(new InputStreamReader(form.elevateWordFile.getInputStream(), getCsvEncoding()))) {
                elevateWordService.importCsv(reader);
                suggestHelper.deleteAllElevateWord(false);
                suggestHelper.storeAllElevateWords(false);
            } catch (final Exception e) {
                throw new FessSystemException("Failed to import data.", e);
            }
        });
        saveInfo(messages -> messages.addSuccessUploadElevateWord(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    public static OptionalEntity<ElevateWord> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new ElevateWord()).map(entity -> {
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(ElevateWordService.class).getElevateWord(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    public static OptionalEntity<ElevateWord> getElevateWord(final CreateForm form) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();

        return getEntity(form, username, currentTime).map(entity -> {
            entity.setUpdatedBy(username);
            entity.setUpdatedTime(currentTime);
            BeanUtil.copyBeanToBean(form, entity, op -> op.exclude(Stream
                    .concat(Stream.of(Constants.COMMON_CONVERSION_RULE), Stream.of(Constants.PERMISSIONS)).toArray(n -> new String[n])));
            final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
            entity.setPermissions(split(form.permissions, "\n").get(
                    stream -> stream.map(permissionHelper::encode).filter(StringUtil::isNotBlank).distinct().toArray(n -> new String[n])));
            return entity;
        });
    }

    protected void registerLabels(final RenderData data) {
        RenderDataUtil.register(data, "labelTypeItems", labelTypeService.getLabelTypeList());
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final int crudMode, final int expectedMode) {
        if (crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(crudMode));
            }, this::asListHtml);
        }
    }

    private String getCsvEncoding() {
        return fessConfig.getCsvFileEncoding();
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========
    private HtmlResponse asListHtml() {
        return asHtml(path_AdminElevateword_AdminElevatewordJsp).renderWith(data -> {
            RenderDataUtil.register(data, "elevateWordItems", elevateWordService.getElevateWordList(elevateWordPager)); // page navi
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(elevateWordPager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminElevateword_AdminElevatewordEditJsp).renderWith(data -> {
            registerLabels(data);
        });
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminElevateword_AdminElevatewordDetailsJsp).renderWith(data -> {
            registerLabels(data);
        });
    }

    private HtmlResponse asUploadHtml() {
        return asHtml(path_AdminElevateword_AdminElevatewordUploadJsp).useForm(UploadForm.class);
    }

    private HtmlResponse asDownloadHtml() {
        return asHtml(path_AdminElevateword_AdminElevatewordDownloadJsp).useForm(DownloadForm.class);
    }
}
