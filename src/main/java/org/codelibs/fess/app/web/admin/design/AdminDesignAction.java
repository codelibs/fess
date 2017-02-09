/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.design;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.exception.FessSystemException;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codelibs
 * @author jflute
 */
public class AdminDesignAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(AdminDesignAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    public ActionResponse hookBefore(final ActionRuntime runtime) {
        checkEditorStatus(runtime);
        return super.hookBefore(runtime);
    }

    private void checkEditorStatus(final ActionRuntime runtime) {
        if (!editable()) {
            throwValidationError(messages -> messages.addErrorsDesignEditorDisabled(GLOBAL), () -> asListHtml());
        }
    }

    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("editable", editable());
        runtime.registerData("fileNameItems", loadFileNameItems());
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameDesign()));
    }

    private boolean editable() {
        return fessConfig.isWebDesignEditorEnabled();
    }

    private List<String> loadFileNameItems() {
        final File baseDir = new File(getServletContext().getRealPath("/"));
        final List<String> fileNameItems = new ArrayList<>();
        final List<File> fileList = getAccessibleFileList(baseDir);
        final int length = baseDir.getAbsolutePath().length();
        for (final File file : fileList) {
            fileNameItems.add(file.getAbsolutePath().substring(length));
        }
        return fileNameItems;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index() {
        saveToken();
        return asHtml(path_AdminDesign_AdminDesignJsp).useForm(DesignForm.class);
    }

    @Execute
    public HtmlResponse back() {
        saveToken();
        return asHtml(path_AdminDesign_AdminDesignJsp).useForm(DesignForm.class);
    }

    @Execute
    public HtmlResponse upload(final UploadForm form) {
        validate(form, messages -> {}, () -> asListHtml(form));
        verifyToken(() -> asListHtml());
        final String uploadedFileName = form.designFile.getFileName();
        String fileName = form.designFileName;
        if (StringUtil.isBlank(fileName)) {
            fileName = uploadedFileName;
            try {
                int pos = fileName.indexOf('/');
                if (pos >= 0) {
                    fileName = fileName.substring(pos + 1);
                }
                pos = fileName.indexOf('\\');
                if (pos >= 0) {
                    fileName = fileName.substring(pos + 1);
                }
            } catch (final Exception e) {
                throwValidationError(messages -> messages.addErrorsDesignFileNameIsInvalid("designFile"), () -> asListHtml());
            }
        }
        if (StringUtil.isBlank(fileName)) {
            throwValidationError(messages -> messages.addErrorsDesignFileNameIsNotFound("designFile"), () -> asListHtml());
        }

        File uploadFile;
        // normalize filename
        if (checkFileType(fileName, fessConfig.getSupportedUploadedMediaExtentionsAsArray())
                && checkFileType(uploadedFileName, fessConfig.getSupportedUploadedMediaExtentionsAsArray())) {
            uploadFile = new File(getServletContext().getRealPath("/images/" + fileName));
        } else if (checkFileType(fileName, fessConfig.getSupportedUploadedCssExtentionsAsArray())
                && checkFileType(uploadedFileName, fessConfig.getSupportedUploadedCssExtentionsAsArray())) {
            uploadFile = new File(getServletContext().getRealPath("/css/" + fileName));
        } else if (checkFileType(fileName, fessConfig.getSupportedUploadedJsExtentionsAsArray())
                && checkFileType(uploadedFileName, fessConfig.getSupportedUploadedJsExtentionsAsArray())) {
            uploadFile = new File(getServletContext().getRealPath("/js/" + fileName));
        } else if (fessConfig.isSupportedUploadedFile(fileName) || fessConfig.isSupportedUploadedFile(uploadedFileName)) {
            uploadFile = ResourceUtil.getResourceAsFileNoException(fileName);
            if (uploadFile == null) {
                throwValidationError(messages -> messages.addErrorsDesignFileNameIsNotFound("designFileName"), () -> asListHtml());
                return null;
            }
        } else {
            throwValidationError(messages -> messages.addErrorsDesignFileIsUnsupportedType("designFileName"), () -> asListHtml());
            return null;
        }

        final File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            logger.warn("Could not create " + parentFile.getAbsolutePath());
        }

        try {
            write(uploadFile.getAbsolutePath(), form.designFile.getFileData());
            final String currentFileName = fileName;
            saveInfo(messages -> messages.addSuccessUploadDesignFile(GLOBAL, currentFileName));
        } catch (final Exception e) {
            logger.error("Failed to write an image file: {}", fileName, e);
            throwValidationError(messages -> messages.addErrorsFailedToWriteDesignImageFile(GLOBAL), () -> asListHtml());
        }
        return redirect(getClass());
    }

    private boolean checkFileType(final String fileName, final String[] exts) {
        if (fileName == null) {
            return false;
        }
        final String lFileName = fileName.toLowerCase(Locale.ENGLISH);
        for (final String ext : exts) {
            if (lFileName.endsWith("." + ext)) {
                return true;
            }
        }
        return false;
    }

    @Execute
    public StreamResponse download(final FileAccessForm form) {
        final File file = getTargetFile(form.fileName).get();
        if (file == null) {
            throwValidationError(messages -> messages.addErrorsTargetFileDoesNotExist(GLOBAL, form.fileName), () -> asListHtml());
            return null;
        }
        validate(form, messages -> {}, () -> asListHtml());
        verifyToken(() -> asListHtml());
        return asStream(file.getName()).contentTypeOctetStream().stream(out -> {
            try (FileInputStream fis = new FileInputStream(file)) {
                out.write(fis);
            }
        });
    }

    @Execute
    public HtmlResponse delete(final FileAccessForm form) {
        getTargetFile(form.fileName).ifPresent(file -> {
            if (!file.delete()) {
                logger.error("Failed to delete {}", file.getAbsolutePath());
                throwValidationError(messages -> messages.addErrorsFailedToDeleteFile(GLOBAL, form.fileName), () -> asListHtml());
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsTargetFileDoesNotExist(GLOBAL, form.fileName), () -> asListHtml());
        });
        saveInfo(messages -> messages.addSuccessDeleteFile(GLOBAL, form.fileName));
        validate(form, messages -> {}, () -> asListHtml());
        verifyToken(() -> asListHtml());
        return redirect(getClass());
    }

    // -----------------------------------------------------
    //                                                 Edit
    //                                                ------
    @Execute
    public HtmlResponse edit(final EditForm form) {
        final String jspType = "view";
        final File jspFile = getJspFile(form.fileName, jspType);
        try {
            form.content = new String(FileUtil.readBytes(jspFile), Constants.UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new FessSystemException("Invalid encoding", e);
        }
        saveToken();
        return asEditHtml();
    }

    @Execute
    public HtmlResponse editAsUseDefault(final EditForm form) {
        final String jspType = "orig/view";
        final File jspFile = getJspFile(form.fileName, jspType);
        try {
            form.content = new String(FileUtil.readBytes(jspFile), Constants.UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new FessSystemException("Invalid encoding", e);
        }
        saveToken();
        return asEditHtml();
    }

    @Execute
    public HtmlResponse update(final EditForm form) {
        final String jspType = "view";
        final File jspFile = getJspFile(form.fileName, jspType);

        if (form.content == null) {
            form.content = StringUtil.EMPTY;
        }

        validate(form, messages -> {}, () -> asEditHtml());
        verifyToken(() -> asEditHtml());
        try {
            write(jspFile.getAbsolutePath(), form.content.getBytes(Constants.UTF_8));
            saveInfo(messages -> messages.addSuccessUpdateDesignJspFile(GLOBAL, systemHelper.getDesignJspFileName(form.fileName)));
        } catch (final Exception e) {
            logger.error("Failed to update {}", form.fileName, e);
            throwValidationError(messages -> messages.addErrorsFailedToUpdateJspFile(GLOBAL), () -> asListHtml());
        }
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private OptionalEntity<File> getTargetFile(final String fileName) {
        final File baseDir = new File(getServletContext().getRealPath("/"));
        final File targetFile = new File(getServletContext().getRealPath(fileName));
        final List<File> fileList = getAccessibleFileList(baseDir);
        for (final File file : fileList) {
            if (targetFile.equals(file)) {
                return OptionalEntity.of(targetFile);
            }
        }
        return OptionalEntity.empty();
    }

    private List<File> getAccessibleFileList(final File baseDir) {
        final List<File> fileList = new ArrayList<>();
        fileList.addAll(FileUtils.listFiles(new File(baseDir, "images"), fessConfig.getSupportedUploadedMediaExtentionsAsArray(), true));
        fileList.addAll(FileUtils.listFiles(new File(baseDir, "css"), fessConfig.getSupportedUploadedCssExtentionsAsArray(), true));
        fileList.addAll(FileUtils.listFiles(new File(baseDir, "js"), fessConfig.getSupportedUploadedJsExtentionsAsArray(), true));
        return fileList;
    }

    private File getJspFile(final String fileName, final String jspType) {
        final String jspFileName = systemHelper.getDesignJspFileName(fileName);
        if (jspFileName == null) {
            throwValidationError(messages -> messages.addErrorsInvalidDesignJspFileName(GLOBAL), () -> asListHtml());
        }
        final File jspFile = new File(getServletContext().getRealPath("/WEB-INF/" + jspType + "/" + jspFileName));
        if (!jspFile.exists()) {
            throwValidationError(messages -> messages.addErrorsDesignJspFileDoesNotExist(GLOBAL), () -> asListHtml());
        }
        return jspFile;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminDesign_AdminDesignJsp).useForm(DesignForm.class);
    }

    private HtmlResponse asListHtml(final UploadForm uploadForm) {
        return asHtml(path_AdminDesign_AdminDesignJsp).useForm(DesignForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(uploadForm, form, op -> op.include("designFile", "designFileName"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminDesign_AdminDesignEditJsp);
    }
}
