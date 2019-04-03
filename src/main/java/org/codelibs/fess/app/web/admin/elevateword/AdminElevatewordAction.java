/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.concurrent.CommonPoolUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
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

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index() {
        return asListHtml();
    }

    @Execute
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
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, elevateWordPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminElevateword_AdminElevatewordJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
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
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, () -> asListHtml());
        final String id = form.id;
        elevateWordService
                .getElevateWord(id)
                .ifPresent(
                        entity -> {
                            copyBeanToBean(entity, form, copyOp -> {
                                copyOp.excludeNull();
                                copyOp.exclude(Constants.PERMISSIONS);
                            });
                            final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
                            form.permissions =
                                    stream(entity.getPermissions()).get(
                                            stream -> stream.map(s -> permissionHelper.decode(s)).filter(StringUtil::isNotBlank).distinct()
                                                    .collect(Collectors.joining("\n")));
                        }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), () -> asListHtml());
                });
        saveToken();
        if (form.crudMode.intValue() == CrudMode.EDIT) {
            // back
            form.crudMode = CrudMode.DETAILS;
            return asDetailsHtml();
        } else {
            form.crudMode = CrudMode.EDIT;
            return asEditHtml();
        }
    }

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    @Execute
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
        return asHtml(path_AdminElevateword_AdminElevatewordDetailsJsp).useForm(
                EditForm.class,
                op -> op.setup(form -> {
                    elevateWordService
                            .getElevateWord(id)
                            .ifPresent(
                                    entity -> {
                                        copyBeanToBean(entity, form, copyOp -> {
                                            copyOp.excludeNull();
                                            copyOp.exclude(Constants.PERMISSIONS);
                                        });
                                        form.permissions =
                                                stream(entity.getPermissions()).get(
                                                        stream -> stream.map(permissionHelper::decode).filter(StringUtil::isNotBlank)
                                                                .distinct().collect(Collectors.joining("\n")));
                                        form.crudMode = crudMode;
                                    })
                            .orElse(() -> throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id),
                                    () -> asListHtml()));
                })).renderWith(data -> registerLabels(data));
    }

    // -----------------------------------------------------
    //                                              Download
    //                                               -------
    @Execute
    public HtmlResponse downloadpage() {
        saveToken();
        return asDownloadHtml();
    }

    @Execute
    public ActionResponse download(final DownloadForm form) {
        verifyToken(() -> asDownloadHtml());

        return asStream("elevate.csv").contentTypeOctetStream().stream(out -> {
            final Path tempFile = Files.createTempFile(null, null);
            try {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(tempFile), getCsvEncoding()))) {
                    elevateWordService.exportCsv(writer);
                } catch (final Exception e) {
                    throwValidationError(messages -> messages.addErrorsFailedToDownloadElevateFile(GLOBAL), () -> asDownloadHtml());
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
    public HtmlResponse uploadpage() {
        saveToken();
        return asUploadHtml();
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, () -> asEditHtml());
        verifyToken(() -> asEditHtml());
        getElevateWord(form).ifPresent(
                entity -> {
                    try {
                        elevateWordService.store(entity);
                        suggestHelper.addElevateWord(entity.getSuggestWord(), entity.getReading(), entity.getLabelTypeValues(),
                                entity.getPermissions(), entity.getBoost(), false);
                        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
                    } catch (final Exception e) {
                        throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)),
                                () -> asEditHtml());
                    }
                }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL), () -> asEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, () -> asEditHtml());
        verifyToken(() -> asEditHtml());
        getElevateWord(form).ifPresent(
                entity -> {
                    try {
                        elevateWordService.store(entity);
                        suggestHelper.deleteAllElevateWord(false);
                        suggestHelper.storeAllElevateWords(false);
                        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
                    } catch (final Exception e) {
                        throwValidationError(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)),
                                () -> asEditHtml());
                    }
                }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), () -> asEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, () -> asDetailsHtml());
        verifyToken(() -> asDetailsHtml());
        final String id = form.id;
        elevateWordService
                .getElevateWord(id)
                .ifPresent(
                        entity -> {
                            try {
                                elevateWordService.delete(entity);
                                suggestHelper.deleteElevateWord(entity.getSuggestWord(), false);
                                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
                            } catch (final Exception e) {
                                throwValidationError(
                                        messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)),
                                        () -> asEditHtml());
                            }
                        })
                .orElse(() -> throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id),
                        () -> asDetailsHtml()));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse upload(final UploadForm form) {
        validate(form, messages -> {}, () -> asUploadHtml());
        verifyToken(() -> asUploadHtml());
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

        return getEntity(form, username, currentTime).map(
                entity -> {
                    entity.setUpdatedBy(username);
                    entity.setUpdatedTime(currentTime);
                    BeanUtil.copyBeanToBean(form, entity, op -> op.exclude(Stream.concat(Stream.of(Constants.COMMON_CONVERSION_RULE),
                            Stream.of(Constants.PERMISSIONS)).toArray(n -> new String[n])));
                    final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
                    entity.setPermissions(split(form.permissions, "\n").get(
                            stream -> stream.map(permissionHelper::encode).filter(StringUtil::isNotBlank).distinct()
                                    .toArray(n -> new String[n])));
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
            }, () -> asListHtml());
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
