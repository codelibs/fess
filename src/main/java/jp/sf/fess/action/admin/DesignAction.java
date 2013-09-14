/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.action.admin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.FessSystemException;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.form.admin.DesignForm;
import jp.sf.fess.helper.SystemHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.framework.util.FileUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.ResponseUtil;
import org.seasar.struts.util.ServletContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DesignAction implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory
            .getLogger(DesignAction.class);

    @ActionForm
    @Resource
    protected DesignForm designForm;

    @Resource
    protected DynamicProperties crawlerProperties;

    public boolean editable = true;

    @Resource
    protected SystemHelper systemHelper;

    public List<String> fileNameItems;

    public String getHelpLink() {
        return systemHelper.getHelpLink("design");
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "index")
    public String index() {
        checkEditorStatus();
        loadFileNameItems();
        return "index.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false)
    public String back() {
        checkEditorStatus();
        loadFileNameItems();
        return "index.jsp";
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

    @Execute(validator = true, input = "index")
    public String upload() {
        checkEditorStatus();
        final String uploadedFileName = designForm.designFile.getFileName();
        String fileName = designForm.designFileName;
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
                throw new SSCActionMessagesException(e,
                        "errors.design_file_name_is_invalid");
            }
        }
        if (StringUtil.isBlank(fileName)) {
            throw new SSCActionMessagesException(
                    "errors.design_file_name_is_not_found");
        }

        String baseDir = null;
        // normalize filename
        if (checkFileType(fileName,
                systemHelper.getSupportedUploadedMediaExtentions())
                && checkFileType(uploadedFileName,
                        systemHelper.getSupportedUploadedMediaExtentions())) {
            baseDir = "/images/";
        } else if (checkFileType(fileName,
                systemHelper.getSupportedUploadedCssExtentions())
                && checkFileType(uploadedFileName,
                        systemHelper.getSupportedUploadedCssExtentions())) {
            baseDir = "/css/";
        } else if (checkFileType(fileName,
                systemHelper.getSupportedUploadedJSExtentions())
                && checkFileType(uploadedFileName,
                        systemHelper.getSupportedUploadedJSExtentions())) {
            baseDir = "/js/";
        } else {
            throw new SSCActionMessagesException(
                    "errors.design_file_is_unsupported_type");
        }

        final File uploadFile = new File(ServletContextUtil.getServletContext()
                .getRealPath(baseDir + fileName));
        final File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            logger.warn("Could not create " + parentFile.getAbsolutePath());
        }

        try {
            FileUtil.write(uploadFile.getAbsolutePath(),
                    designForm.designFile.getFileData());
            SAStrutsUtil.addSessionMessage("success.upload_design_file",
                    fileName);
            return "index?redirect=true";
        } catch (final Exception e) {
            logger.error("Failed to write an image file.", e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_write_design_image_file");
        }

    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "index")
    public String edit() {
        checkEditorStatus();
        final String jspType = "view";
        final File jspFile = getJspFile(jspType);

        try {
            designForm.content = new String(FileUtil.getBytes(jspFile),
                    Constants.UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new FessSystemException("Invalid encoding", e);
        }

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "index")
    public String editAsUseDefault() {
        checkEditorStatus();
        final String jspType = "orig/view";
        final File jspFile = getJspFile(jspType);

        try {
            designForm.content = new String(FileUtil.getBytes(jspFile),
                    Constants.UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new FessSystemException("Invalid encoding", e);
        }

        return "edit.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = false, input = "index")
    public String update() {
        checkEditorStatus();
        final String jspType = "view";
        final File jspFile = getJspFile(jspType);

        if (designForm.content == null) {
            designForm.content = "";
        }

        try {
            FileUtil.write(jspFile.getAbsolutePath(),
                    designForm.content.getBytes(Constants.UTF_8));
            SAStrutsUtil.addSessionMessage("success.update_design_jsp_file",
                    systemHelper.getDesignJspFileName(designForm.fileName));
            return "index?redirect=true";
        } catch (final Exception e) {
            logger.error("Failed to update " + designForm.fileName, e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_update_jsp_file");
        }
    }

    @Execute(validator = false, input = "index")
    public String download() {
        checkEditorStatus();

        final File file = getTargetFile();
        if (file == null) {
            throw new SSCActionMessagesException(
                    "errors.target_file_does_not_exist", designForm.fileName);
        }

        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            ResponseUtil.download(file.getName(), bis);
        } catch (final Exception e) {
            logger.error("Failed to download " + file.getAbsolutePath(), e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_download_file", designForm.fileName);
        } finally {
            IOUtils.closeQuietly(bis);
        }
        return null;
    }

    @Token(save = false, validate = true)
    @Execute(validator = false, input = "index")
    public String delete() {
        checkEditorStatus();

        final File file = getTargetFile();
        if (file == null) {
            throw new SSCActionMessagesException(
                    "errors.target_file_does_not_exist", designForm.fileName);
        }

        if (file.delete()) {
            SAStrutsUtil.addSessionMessage("success.delete_file",
                    designForm.fileName);
            return "index?redirect=true";
        } else {
            logger.error("Failed to delete " + file.getAbsolutePath());
            throw new SSCActionMessagesException(
                    "errors.failed_to_delete_file", designForm.fileName);
        }
    }

    private File getJspFile(final String jspType) {
        final String jspFileName = systemHelper
                .getDesignJspFileName(designForm.fileName);
        if (jspFileName == null) {
            throw new SSCActionMessagesException(
                    "errors.invalid_design_jsp_file_name");
        }
        final File jspFile = new File(ServletContextUtil.getServletContext()
                .getRealPath("/WEB-INF/" + jspType + "/" + jspFileName));
        if (jspFile == null || !jspFile.exists()) {
            throw new SSCActionMessagesException(
                    "errors.design_jsp_file_does_not_exist");
        }
        return jspFile;
    }

    private void checkEditorStatus() {
        if (Constants.FALSE.equals(crawlerProperties.getProperty(
                Constants.WEB_DESIGN_EDITOR_PROPERTY, Constants.TRUE))) {
            editable = false;
            throw new SSCActionMessagesException(
                    "errors.design_editor_disabled");
        }
    }

    private void loadFileNameItems() {
        final File baseDir = new File(ServletContextUtil.getServletContext()
                .getRealPath("/"));
        fileNameItems = new ArrayList<String>();
        final List<File> fileList = getAccessibleFileList(baseDir);
        final int length = baseDir.getAbsolutePath().length();
        for (final File file : fileList) {
            fileNameItems.add(file.getAbsolutePath().substring(length));
        }
    }

    private List<File> getAccessibleFileList(final File baseDir) {
        final List<File> fileList = new ArrayList<File>();
        fileList.addAll(FileUtils.listFiles(new File(baseDir, "images"),
                systemHelper.getSupportedUploadedMediaExtentions(), true));
        fileList.addAll(FileUtils.listFiles(new File(baseDir, "css"),
                systemHelper.getSupportedUploadedCssExtentions(), true));
        fileList.addAll(FileUtils.listFiles(new File(baseDir, "js"),
                systemHelper.getSupportedUploadedJSExtentions(), true));
        return fileList;
    }

    private File getTargetFile() {
        final File baseDir = new File(ServletContextUtil.getServletContext()
                .getRealPath("/"));
        final File targetFile = new File(ServletContextUtil.getServletContext()
                .getRealPath(designForm.fileName));
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
}