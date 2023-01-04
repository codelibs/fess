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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.helper.message.ExceptionMessageBuilder;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.LastaWebKey;
import org.lastaflute.web.exception.Forced404NotFoundException;
import org.lastaflute.web.ruts.config.ModuleConfig;
import org.lastaflute.web.ruts.multipart.MultipartFormFile;
import org.lastaflute.web.ruts.multipart.MultipartRequestHandler;
import org.lastaflute.web.ruts.multipart.MultipartRequestWrapper;
import org.lastaflute.web.ruts.multipart.exception.MultipartExceededException;
import org.lastaflute.web.util.LaServletContextUtil;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class FessMultipartRequestHandler implements MultipartRequestHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger logger = LogManager.getLogger(FessMultipartRequestHandler.class);
    protected static final String CONTEXT_TEMPDIR_KEY = "javax.servlet.context.tempdir";
    protected static final String JAVA_IO_TMPDIR_KEY = "java.io.tmpdir";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Map<String, Object> elementsAll;
    protected Map<String, MultipartFormFile> elementsFile;
    protected Map<String, String[]> elementsText;

    // ===================================================================================
    //                                                                      Handle Request
    //                                                                      ==============
    @Override
    public void handleRequest(final HttpServletRequest request) throws ServletException {
        // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // copied from super's method and extends it
        // basically for JVN#14876762
        // thought not all problems are resolved however the main case is safety
        // - - - - - - - - - -/
        final ServletFileUpload upload = createServletFileUpload(request);
        prepareElementsHash();
        try {
            final List<FileItem> items = parseRequest(request, upload);
            mappingParameter(request, items);
        } catch (final SizeLimitExceededException e) {
            handleSizeLimitExceededException(request, e);
        } catch (final FileUploadException e) {
            handleFileUploadException(e);
        }
    }

    protected ModuleConfig getModuleConfig(final HttpServletRequest request) {
        return (ModuleConfig) request.getAttribute(LastaWebKey.MODULE_CONFIG_KEY);
    }

    // ===================================================================================
    //                                                            Create ServletFileUpload
    //                                                            ========================
    protected ServletFileUpload createServletFileUpload(final HttpServletRequest request) {
        final DiskFileItemFactory fileItemFactory = createDiskFileItemFactory();
        final ServletFileUpload upload = newServletFileUpload(fileItemFactory);
        upload.setHeaderEncoding(request.getCharacterEncoding());
        upload.setSizeMax(getSizeMax());
        return upload;
    }

    protected ServletFileUpload newServletFileUpload(final DiskFileItemFactory fileItemFactory) {
        return new ServletFileUpload(fileItemFactory) {
            @Override
            protected byte[] getBoundary(final String contentType) { // for security
                final byte[] boundary = super.getBoundary(contentType);
                checkBoundarySize(contentType, boundary);
                return boundary;
            }
        };
    }

    protected void checkBoundarySize(final String contentType, final byte[] boundary) {
        final int boundarySize = boundary.length;
        final int limitSize = getBoundaryLimitSize();
        if (boundarySize > getBoundaryLimitSize()) {
            throwTooLongBoundarySizeException(contentType, boundarySize, limitSize);
        }
    }

    protected int getBoundaryLimitSize() {
        // one HTTP proxy tool already limits the size (e.g. 3450 bytes)
        // so specify this size for test
        return 2000; // you can override as you like it
    }

    protected void throwTooLongBoundarySizeException(final String contentType, final int boundarySize, final int limitSize) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Too long boundary size so treats it as 404.");
        br.addItem("Advice");
        br.addElement("Against for JVN14876762.");
        br.addElement("Boundary size is limited by Framework.");
        br.addElement("Too long boundary is treated as 404 because it's thought of as attack.");
        br.addElement("");
        br.addElement("While, you can override the boundary limit size");
        br.addElement(" in " + FessMultipartRequestHandler.class.getSimpleName() + ".");
        br.addItem("Content Type");
        br.addElement(contentType);
        br.addItem("Boundary Size");
        br.addElement(boundarySize);
        br.addItem("Limit Size");
        br.addElement(limitSize);
        final String msg = br.buildExceptionMessage();
        throw new Forced404NotFoundException(msg, UserMessages.empty()); // heavy attack!? so give no page to tell wasted action
    }

    protected DiskFileItemFactory createDiskFileItemFactory() {
        final File repository = createRepositoryFile();
        return new DiskFileItemFactory((int) getSizeThreshold(), repository);
    }

    protected File createRepositoryFile() {
        return new File(getRepositoryPath());
    }

    // ===================================================================================
    //                                                                      Handling Parts
    //                                                                      ==============
    protected void prepareElementsHash() {
        elementsText = new Hashtable<>();
        elementsFile = new Hashtable<>();
        elementsAll = new Hashtable<>();
    }

    protected List<FileItem> parseRequest(final HttpServletRequest request, final ServletFileUpload upload) throws FileUploadException {
        return upload.parseRequest(request);
    }

    protected void mappingParameter(final HttpServletRequest request, final List<FileItem> items) {
        showFieldLoggingTitle();
        for (final FileItem item : items) {
            if (item.isFormField()) {
                showFormFieldParameter(item);
                addTextParameter(request, item);
            } else {
                showFileFieldParameter(item);
                final String itemName = item.getName();
                if (itemName != null && !itemName.isEmpty()) {
                    addFileParameter(item);
                }
            }
        }
    }

    protected void showFieldLoggingTitle() {
        // logging filter cannot show the parameters when multi-part so logging here
        if (logger.isDebugEnabled()) {
            logger.debug("[Multipart Request Parameter]");
        }
    }

    protected void showFormFieldParameter(final FileItem item) {
        if (logger.isDebugEnabled()) {
            logger.debug("[param] {}={}", item.getFieldName(), item.getString());
        }
    }

    protected void showFileFieldParameter(final FileItem item) {
        if (logger.isDebugEnabled()) {
            logger.debug("[param] {}:{name={}, size={}}", item.getFieldName(), item.getName(), item.getSize());
        }
    }

    protected void handleSizeLimitExceededException(final HttpServletRequest request, final SizeLimitExceededException e) {
        final long actual = e.getActualSize();
        final long permitted = e.getPermittedSize();
        final String msg = "Exceeded size of the multipart request: actual=" + actual + " permitted=" + permitted;
        request.setAttribute(MAX_LENGTH_EXCEEDED_KEY, new MultipartExceededException(msg, actual, permitted, e));
        try {
            final InputStream is = request.getInputStream();
            try {
                final byte[] buf = new byte[1024];
                while ((is.read(buf)) != -1) {}
            } catch (final Exception ignored) {} finally {
                try {
                    is.close();
                } catch (final Exception ignored) {}
            }
        } catch (final Exception ignored) {}
    }

    protected void handleFileUploadException(final FileUploadException e) throws ServletException {
        // suppress logging because it can be caught by logging filter
        //log.error("Failed to parse multipart request", e);
        throw new ServletException("Failed to upload the file.", e);
    }

    // ===================================================================================
    //                                                                           Roll-back
    //                                                                           =========
    @Override
    public void rollback() {
        for (final MultipartFormFile formFile : elementsFile.values()) {
            formFile.destroy();
        }
    }

    // ===================================================================================
    //                                                                            Add Text
    //                                                                            ========
    protected void addTextParameter(final HttpServletRequest request, final FileItem item) {
        final String name = item.getFieldName();
        final String encoding = request.getCharacterEncoding();
        String value = null;
        boolean haveValue = false;
        if (encoding != null) {
            try {
                value = item.getString(encoding);
                haveValue = true;
            } catch (final Exception e) {}
        }
        if (!haveValue) {
            try {
                value = item.getString("ISO-8859-1");
            } catch (final java.io.UnsupportedEncodingException uee) {
                value = item.getString();
            }
            haveValue = true;
        }
        if (request instanceof final MultipartRequestWrapper wrapper) {
            wrapper.setParameter(name, value);
        }
        final String[] oldArray = elementsText.get(name);
        final String[] newArray;
        if (oldArray != null) {
            newArray = new String[oldArray.length + 1];
            System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
            newArray[oldArray.length] = value;
        } else {
            newArray = new String[] { value };
        }
        elementsText.put(name, newArray);
        elementsAll.put(name, newArray);
    }

    protected void addFileParameter(final FileItem item) {
        final MultipartFormFile formFile = newActionMultipartFormFile(item);
        elementsFile.put(item.getFieldName(), formFile);
        elementsAll.put(item.getFieldName(), formFile);
    }

    protected ActionMultipartFormFile newActionMultipartFormFile(final FileItem item) {
        return new ActionMultipartFormFile(item);
    }

    // ===================================================================================
    //                                                                              Finish
    //                                                                              ======
    @Override
    public void finish() {
        rollback();
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected long getSizeMax() {
        return ComponentUtil.getFessConfig().getHttpFileuploadMaxSizeAsInteger();
    }

    protected long getSizeThreshold() {
        return ComponentUtil.getFessConfig().getHttpFileuploadThresholdSizeAsInteger();
    }

    protected String getRepositoryPath() {
        final File tempDirFile = (File) LaServletContextUtil.getServletContext().getAttribute(CONTEXT_TEMPDIR_KEY);
        String tempDir = tempDirFile.getAbsolutePath();
        if (tempDir == null || tempDir.length() == 0) {
            tempDir = System.getProperty(JAVA_IO_TMPDIR_KEY);
        }
        return tempDir;
    }

    // ===================================================================================
    //                                                                           Form File
    //                                                                           =========
    protected static class ActionMultipartFormFile implements MultipartFormFile, Serializable {

        private static final long serialVersionUID = 1L;

        protected final FileItem fileItem;

        public ActionMultipartFormFile(final FileItem fileItem) {
            this.fileItem = fileItem;
        }

        @Override
        public byte[] getFileData() throws IOException {
            return fileItem.get();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return fileItem.getInputStream();
        }

        @Override
        public String getContentType() {
            return fileItem.getContentType();
        }

        @Override
        public int getFileSize() {
            return (int) fileItem.getSize();
        }

        @Override
        public String getFileName() {
            return getBaseFileName(fileItem.getName());
        }

        protected String getBaseFileName(final String filePath) {
            final String fileName = new File(filePath).getName();
            int colonIndex = fileName.indexOf(':');
            if (colonIndex == -1) {
                colonIndex = fileName.indexOf("\\\\"); // Windows SMB
            }
            final int backslashIndex = fileName.lastIndexOf('\\');
            if (colonIndex > -1 && backslashIndex > -1) {
                return fileName.substring(backslashIndex + 1);
            }
            return fileName;
        }

        @Override
        public void destroy() {
            fileItem.delete();
        }

        @Override
        public String toString() {
            return "formFile:{" + getFileName() + "}";
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    @Override
    public Map<String, Object> getAllElements() {
        return elementsAll;
    }

    @Override
    public Map<String, String[]> getTextElements() {
        return elementsText;
    }

    @Override
    public Map<String, MultipartFormFile> getFileElements() {
        return elementsFile;
    }
}
