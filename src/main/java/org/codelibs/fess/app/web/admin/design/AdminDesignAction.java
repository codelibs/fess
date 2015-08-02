/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.validation.VaErrorHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codelibs
 * @author jflute
 */
public class AdminDesignAction extends FessAdminAction implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminDesignAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private DynamicProperties crawlerProperties;
    @Resource
    private SystemHelper systemHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    public ActionResponse hookBefore(ActionRuntime runtime) {
        checkEditorStatus(runtime);
        return super.hookBefore(runtime);
    }

    private void checkEditorStatus(ActionRuntime runtime) {
        if (cannotEdit()) {
            throwValidationError(messages -> messages.addErrorsDesignEditorDisabled(GLOBAL), toMainHtml());
        }
    }

    @Override
    protected void setupHtmlData(ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("editable", cannotEdit());
        runtime.registerData("fileNameItems", loadFileNameItems());
        runtime.registerData("helpLink", systemHelper.getHelpLink("design"));
    }

    private boolean cannotEdit() {
        return Constants.FALSE.equals(crawlerProperties.getProperty(Constants.WEB_DESIGN_EDITOR_PROPERTY, Constants.TRUE));
    }

    private List<String> loadFileNameItems() {
        final File baseDir = new File(getServletContext().getRealPath("/"));
        final List<String> fileNameItems = new ArrayList<String>();
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
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse index() {
        return asHtml(path_AdminDesign_AdminDesignJsp).useForm(DesignForm.class);
    }

    @Execute
    public HtmlResponse upload(DesignForm form) {
        validate(form, messages -> {} , toMainHtml());
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
                throwValidationError(messages -> messages.addErrorsDesignFileNameIsInvalid(GLOBAL), toMainHtml());
            }
        }
        if (StringUtil.isBlank(fileName)) {
            throwValidationError(messages -> messages.addErrorsDesignFileNameIsNotFound(GLOBAL), toMainHtml());
        }

        String baseDir = null;
        // normalize filename
        if (checkFileType(fileName, systemHelper.getSupportedUploadedMediaExtentions())
                && checkFileType(uploadedFileName, systemHelper.getSupportedUploadedMediaExtentions())) {
            baseDir = "/images/";
        } else if (checkFileType(fileName, systemHelper.getSupportedUploadedCssExtentions())
                && checkFileType(uploadedFileName, systemHelper.getSupportedUploadedCssExtentions())) {
            baseDir = "/css/";
        } else if (checkFileType(fileName, systemHelper.getSupportedUploadedJSExtentions())
                && checkFileType(uploadedFileName, systemHelper.getSupportedUploadedJSExtentions())) {
            baseDir = "/js/";
        } else {
            throwValidationError(messages -> messages.addErrorsDesignFileIsUnsupportedType(GLOBAL), toMainHtml());
        }

        final File uploadFile = new File(getServletContext().getRealPath(baseDir + fileName));
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
            throwValidationError(messages -> messages.addErrorsFailedToWriteDesignImageFile(GLOBAL), toMainHtml());
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
    public StreamResponse download(DesignForm form) {
        final File file = getTargetFile(form);
        if (file == null) {
            throwValidationError(messages -> messages.addErrorsTargetFileDoesNotExist(GLOBAL, form.fileName), toMainHtml());
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (final FileNotFoundException e) {
            logger.error("Not found the file: {}", file.getAbsolutePath(), e);
            throwValidationError(messages -> messages.addErrorsFailedToDownloadFile(GLOBAL, form.fileName), toMainHtml());
        }
        return asStream(file.getName()).stream(fis);
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse delete(DesignForm form) {
        final File file = getTargetFile(form);
        if (file == null) {
            throwValidationError(messages -> messages.addErrorsTargetFileDoesNotExist(GLOBAL, form.fileName), toMainHtml());
        }
        if (!file.delete()) {
            logger.error("Failed to delete {}", file.getAbsolutePath());
            throwValidationError(messages -> messages.addErrorsFailedToDeleteFile(GLOBAL, form.fileName), toMainHtml());
        }
        SAStrutsUtil.addSessionMessage("success.delete_file", form.fileName);
        return redirect(getClass());
    }

    // -----------------------------------------------------
    //                                                 Edit 
    //                                                ------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse edit(DesignEditForm form) {
        final String jspType = "view";
        final File jspFile = getJspFile(form.fileName, jspType);
        try {
            form.content = new String(FileUtil.readBytes(jspFile), Constants.UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new FessSystemException("Invalid encoding", e);
        }
        return asHtml(path_AdminDesign_AdminDesignEditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editAsUseDefault(DesignEditForm form) {
        final String jspType = "orig/view";
        final File jspFile = getJspFile(form.fileName, jspType);
        try {
            form.content = new String(FileUtil.readBytes(jspFile), Constants.UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new FessSystemException("Invalid encoding", e);
        }
        return asHtml(path_AdminDesign_AdminDesignEditJsp);
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(DesignEditForm form) {
        final String jspType = "view";
        final File jspFile = getJspFile(form.fileName, jspType);

        if (form.content == null) {
            form.content = StringUtil.EMPTY;
        }

        try {
            write(jspFile.getAbsolutePath(), form.content.getBytes(Constants.UTF_8));
            saveInfo(messages -> messages.addSuccessUpdateDesignJspFile(GLOBAL, systemHelper.getDesignJspFileName(form.fileName)));
        } catch (final Exception e) {
            logger.error("Failed to update {}", form.fileName, e);
            throwValidationError(messages -> messages.addErrorsFailedToUpdateJspFile(GLOBAL), toMainHtml());
        }
        return redirect(getClass());
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse back() {
        return asHtml(path_AdminDesign_AdminDesignJsp).useForm(DesignForm.class);
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private File getTargetFile(DesignForm form) {
        final File baseDir = new File(getServletContext().getRealPath("/"));
        final File targetFile = new File(getServletContext().getRealPath(form.fileName));
        final List<File> fileList = getAccessibleFileList(baseDir);
        boolean exist = false;
        for (final File file : fileList) {
            if (targetFile.equals(file)) {
                exist = true;
                break;
            }
        }
        if (exist) {
            return targetFile;
        }
        return null;
    }

    private List<File> getAccessibleFileList(final File baseDir) {
        final List<File> fileList = new ArrayList<File>();
        fileList.addAll(FileUtils.listFiles(new File(baseDir, "images"), systemHelper.getSupportedUploadedMediaExtentions(), true));
        fileList.addAll(FileUtils.listFiles(new File(baseDir, "css"), systemHelper.getSupportedUploadedCssExtentions(), true));
        fileList.addAll(FileUtils.listFiles(new File(baseDir, "js"), systemHelper.getSupportedUploadedJSExtentions(), true));
        return fileList;
    }

    private File getJspFile(String fileName, String jspType) {
        final String jspFileName = systemHelper.getDesignJspFileName(fileName);
        if (jspFileName == null) {
            throwValidationError(messages -> messages.addErrorsInvalidDesignJspFileName(GLOBAL), toMainHtml());
        }
        final File jspFile = new File(getServletContext().getRealPath("/WEB-INF/" + jspType + "/" + jspFileName));
        if (jspFile == null || !jspFile.exists()) {
            throwValidationError(messages -> messages.addErrorsDesignJspFileDoesNotExist(GLOBAL), toMainHtml());
        }
        return jspFile;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    private VaErrorHook toMainHtml() {
        return () -> {
            return asHtml(path_AdminDesign_AdminDesignJsp);
        };
    }
}