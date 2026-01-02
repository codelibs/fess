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
package org.codelibs.fess.app.web.admin.design;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

/**
 * Admin action for Design management.
 *
 */
public class AdminDesignAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminDesignAction() {
        super();
    }

    private static final String CACHE_AND_SESSION_INVALIDATE_STATEMENT = "<!--CACHE_AND_SESSION_INVALIDATE-->";

    private static final String TRY_STATEMENT = "<!--TRY-->";

    /** The role for this action. */
    public static final String ROLE = "admin-design";

    private static final Logger logger = LogManager.getLogger(AdminDesignAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======

    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("fileNameItems", loadFileNameItems());
        runtime.registerData("jspFileNameItems", loadJspFileNameItems());
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameDesign()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    private List<Pair<String, String>> loadJspFileNameItems() {
        final List<Pair<String, String>> jspItems = new ArrayList<>();
        for (final Pair<String, String> p : systemHelper.getDesignJspFileNames()) {
            jspItems.add(new Pair<>(":" + p.getFirst(), "/" + p.getSecond()));
        }
        for (String key : ComponentUtil.getVirtualHostHelper().getVirtualHostPaths()) {
            if (StringUtil.isBlank(key)) {
                key = "/";
            }
            for (final Pair<String, String> p : systemHelper.getDesignJspFileNames()) {
                jspItems.add(new Pair<>(key + ":" + p.getFirst(), key + "/" + p.getSecond()));
            }
        }
        return jspItems;
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
    /**
     * Show the index page.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        saveToken();
        return asHtml(path_AdminDesign_AdminDesignJsp).useForm(DesignForm.class);
    }

    /**
     * Go back to the index page.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse back() {
        saveToken();
        return asHtml(path_AdminDesign_AdminDesignJsp).useForm(DesignForm.class);
    }

    /**
     * Upload a design file.
     * @param form The upload form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse upload(final UploadForm form) {
        validate(form, messages -> {}, () -> asListHtml(form));
        verifyToken(this::asListHtml);
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
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsDesignFileNameIsInvalid("designFile"), this::asListHtml);
            }
        }
        if (StringUtil.isBlank(fileName)) {
            throwValidationError(messages -> messages.addErrorsDesignFileNameIsNotFound("designFile"), this::asListHtml);
        }

        final File baseDir = new File(getServletContext().getRealPath("/"));
        File uploadFile;
        File expectedBaseDir;
        // normalize filename
        if (checkFileType(fileName, fessConfig.getSupportedUploadedMediaExtentionsAsArray())
                && checkFileType(uploadedFileName, fessConfig.getSupportedUploadedMediaExtentionsAsArray())) {
            expectedBaseDir = new File(baseDir, "images");
            uploadFile = new File(getServletContext().getRealPath("/images/" + fileName));
        } else if (checkFileType(fileName, fessConfig.getSupportedUploadedCssExtentionsAsArray())
                && checkFileType(uploadedFileName, fessConfig.getSupportedUploadedCssExtentionsAsArray())) {
            expectedBaseDir = new File(baseDir, "css");
            uploadFile = new File(getServletContext().getRealPath("/css/" + fileName));
        } else if (checkFileType(fileName, fessConfig.getSupportedUploadedJsExtentionsAsArray())
                && checkFileType(uploadedFileName, fessConfig.getSupportedUploadedJsExtentionsAsArray())) {
            expectedBaseDir = new File(baseDir, "js");
            uploadFile = new File(getServletContext().getRealPath("/js/" + fileName));
        } else if (fessConfig.isSupportedUploadedFile(fileName) || fessConfig.isSupportedUploadedFile(uploadedFileName)) {
            uploadFile = ResourceUtil.getResourceAsFileNoException(fileName);
            if (uploadFile == null) {
                throwValidationError(messages -> messages.addErrorsDesignFileNameIsNotFound("designFileName"), this::asListHtml);
                return null;
            }
            expectedBaseDir = null; // Skip path traversal check for resource files
        } else {
            throwValidationError(messages -> messages.addErrorsDesignFileIsUnsupportedType("designFileName"), this::asListHtml);
            return null;
        }

        // Validate path to prevent path traversal attacks
        if (expectedBaseDir != null && !isValidUploadPath(uploadFile, expectedBaseDir)) {
            logger.warn("Path traversal attempt detected: fileName={}", fileName);
            throwValidationError(messages -> messages.addErrorsDesignFileNameIsInvalid("designFileName"), this::asListHtml);
            return null;
        }

        final File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            logger.warn("Could not create directory: {}", parentFile.getAbsolutePath());
        }

        try {
            write(uploadFile.getAbsolutePath(), form.designFile.getFileData());
            final String currentFileName = fileName;
            saveInfo(messages -> messages.addSuccessUploadDesignFile(GLOBAL, currentFileName));
        } catch (final Exception e) {
            logger.error("Failed to write an image file: {}", fileName, e);
            throwValidationError(messages -> messages.addErrorsFailedToWriteDesignImageFile(GLOBAL), this::asListHtml);
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

    /**
     * Download a design file.
     * @param form The file access form.
     * @return The stream response.
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public StreamResponse download(final FileAccessForm form) {
        final File file = getTargetFile(form.fileName).get();
        if (file == null) {
            throwValidationError(messages -> messages.addErrorsTargetFileDoesNotExist(GLOBAL, form.fileName), this::asListHtml);
            return null;
        }
        validate(form, messages -> {}, this::asListHtml);
        verifyTokenKeep(this::asListHtml);
        return asStream(file.getName()).contentTypeOctetStream().stream(out -> {
            try (FileInputStream fis = new FileInputStream(file)) {
                out.write(fis);
            }
        });
    }

    /**
     * Delete a design file.
     * @param form The file access form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final FileAccessForm form) {
        getTargetFile(form.fileName).ifPresent(file -> {
            if (!file.delete()) {
                logger.error("Failed to delete design file: {}", file.getAbsolutePath());
                throwValidationError(messages -> messages.addErrorsFailedToDeleteFile(GLOBAL, form.fileName), this::asListHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsTargetFileDoesNotExist(GLOBAL, form.fileName), this::asListHtml);
        });
        saveInfo(messages -> messages.addSuccessDeleteFile(GLOBAL, form.fileName));
        validate(form, messages -> {}, this::asListHtml);
        verifyToken(this::asListHtml);
        return redirect(getClass());
    }

    // -----------------------------------------------------
    //                                                 Edit
    //                                                ------
    /**
     * Show the edit page.
     * @param form The edit form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        final String jspType = "view";
        final File jspFile = getJspFile(form.fileName, jspType);
        try {
            form.content = encodeJsp(new String(FileUtil.readBytes(jspFile), Constants.UTF_8));
        } catch (final UnsupportedEncodingException e) {
            throw new FessSystemException("Invalid encoding", e);
        }
        saveToken();
        return asEditHtml(form);
    }

    /**
     * Show the edit page with the default content.
     * @param form The edit form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse editAsUseDefault(final EditForm form) {
        final String jspType = "orig/view";
        final File jspFile = getJspFile(form.fileName, jspType);
        try {
            form.content = encodeJsp(new String(FileUtil.readBytes(jspFile), Constants.UTF_8));
        } catch (final UnsupportedEncodingException e) {
            throw new FessSystemException("Invalid encoding", e);
        }
        saveToken();
        return asEditHtml(form);
    }

    /**
     * Update a design file.
     * @param form The edit form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        final String jspType = "view";
        final File jspFile = getJspFile(form.fileName, jspType);

        if (form.content == null) {
            form.content = StringUtil.EMPTY;
        }

        validate(form, messages -> {}, () -> asEditHtml(form));
        verifyToken(() -> asEditHtml(form));
        try {
            write(jspFile.getAbsolutePath(), decodeJsp(form.content).getBytes(Constants.UTF_8));
            saveInfo(messages -> messages.addSuccessUpdateDesignJspFile(GLOBAL, jspFile.getAbsolutePath()));
        } catch (final Exception e) {
            logger.warn("Failed to update {}", form.fileName, e);
            throwValidationError(messages -> messages.addErrorsFailedToUpdateJspFile(GLOBAL), this::asListHtml);
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
        final List<File> fileList = new ArrayList<>(
                FileUtils.listFiles(new File(baseDir, "images"), fessConfig.getSupportedUploadedMediaExtentionsAsArray(), true));
        fileList.addAll(FileUtils.listFiles(new File(baseDir, "css"), fessConfig.getSupportedUploadedCssExtentionsAsArray(), true));
        fileList.addAll(FileUtils.listFiles(new File(baseDir, "js"), fessConfig.getSupportedUploadedJsExtentionsAsArray(), true));
        return fileList;
    }

    private boolean isValidUploadPath(final File file, final File baseDir) {
        try {
            final String canonicalFilePath = file.getCanonicalPath();
            final String canonicalBasePath = baseDir.getCanonicalPath() + File.separator;
            return canonicalFilePath.startsWith(canonicalBasePath);
        } catch (final IOException e) {
            logger.warn("Failed to validate upload path: file={}", file.getAbsolutePath(), e);
            return false;
        }
    }

    private File getJspFile(final String fileName, final String jspType) {
        try {
            final String[] values = URLDecoder.decode(fileName, Constants.UTF_8).split(":");
            if (values.length != 2) {
                throwValidationError(messages -> messages.addErrorsInvalidDesignJspFileName(GLOBAL), this::asListHtml);
            }

            // Validate virtual host path to prevent path traversal
            final String virtualHostPath = values[0];
            if (!isValidVirtualHostPath(virtualHostPath)) {
                logger.warn("Invalid virtual host path detected: path={}", virtualHostPath);
                throwValidationError(messages -> messages.addErrorsInvalidDesignJspFileName(GLOBAL), this::asListHtml);
            }

            final String jspFileName = systemHelper.getDesignJspFileName(values[1]);
            if (jspFileName == null) {
                throwValidationError(messages -> messages.addErrorsInvalidDesignJspFileName(GLOBAL), this::asListHtml);
            }
            String path;
            if ("view".equals(jspType)) {
                path = "/WEB-INF/" + jspType + virtualHostPath + "/" + jspFileName;
            } else {
                path = "/WEB-INF/" + jspType + "/" + jspFileName;
            }
            final File jspFile = new File(getServletContext().getRealPath(path));

            // Validate canonical path to prevent path traversal
            final File webInfDir = new File(getServletContext().getRealPath("/WEB-INF"));
            if (!isValidUploadPath(jspFile, webInfDir)) {
                logger.warn("Path traversal attempt detected in JSP file path: path={}", path);
                throwValidationError(messages -> messages.addErrorsInvalidDesignJspFileName(GLOBAL), this::asListHtml);
            }

            if (!jspFile.exists()) {
                throwValidationError(messages -> messages.addErrorsDesignJspFileDoesNotExist(GLOBAL), this::asListHtml);
            }
            return jspFile;
        } catch (final UnsupportedEncodingException e) {
            throw new FessSystemException("Failed to decode " + fileName, e);
        }
    }

    private boolean isValidVirtualHostPath(final String path) {
        // Empty path is valid (default host)
        if (StringUtil.isBlank(path)) {
            return true;
        }
        // Path must match one of the configured virtual host paths
        for (final String validPath : ComponentUtil.getVirtualHostHelper().getVirtualHostPaths()) {
            if (path.equals(validPath)) {
                return true;
            }
        }
        // Also allow "/" as a valid path (used in loadJspFileNameItems for blank keys)
        return "/".equals(path);
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

    private HtmlResponse asEditHtml(final EditForm form) {
        return asHtml(path_AdminDesign_AdminDesignEditJsp).renderWith(data -> {
            data.register("displayFileName", getJspFile(form.fileName, "view").getAbsolutePath());
        });
    }

    /**
     * Decode the JSP content.
     * @param value The value.
     * @return The decoded value.
     */
    public static String decodeJsp(final String value) {
        return value.replaceAll("<%(?![@-])([\\s\\S]*?)%>", "&lt;%$1%&gt;")
                .replaceAll("<%=([\\s\\S]*?)%>", "&lt;%=$1%&gt;")
                .replace(TRY_STATEMENT, "<% try{ %>")
                .replace(CACHE_AND_SESSION_INVALIDATE_STATEMENT, "<% }catch(Exception e){session.invalidate();} %>");
    }

    /**
     * Encode the JSP content.
     * @param value The value.
     * @return The encoded value.
     */
    public static String encodeJsp(final String value) {
        return value.replace("<% try{ %>", TRY_STATEMENT)
                .replace("<% }catch(Exception e){session.invalidate();} %>", CACHE_AND_SESSION_INVALIDATE_STATEMENT);
    }
}
