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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileUploadByteCountLimitException;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.jakarta.JakartaServletDiskFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.helper.message.ExceptionMessageBuilder;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.di.exception.IORuntimeException;
import org.lastaflute.web.LastaWebKey;
import org.lastaflute.web.exception.Forced404NotFoundException;
import org.lastaflute.web.ruts.config.ModuleConfig;
import org.lastaflute.web.ruts.multipart.MultipartFormFile;
import org.lastaflute.web.ruts.multipart.MultipartRequestHandler;
import org.lastaflute.web.ruts.multipart.MultipartRequestWrapper;
import org.lastaflute.web.ruts.multipart.exception.MultipartExceededException;
import org.lastaflute.web.util.LaServletContextUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class FessMultipartRequestHandler implements MultipartRequestHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger logger = LogManager.getLogger(FessMultipartRequestHandler.class);
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
        final JakartaServletDiskFileUpload upload = createServletFileUpload(request);
        prepareElementsHash();
        try {
            final List<DiskFileItem> items = parseRequest(request, upload);
            mappingParameter(request, items);
        } catch (final FileUploadByteCountLimitException e) {
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
    protected JakartaServletDiskFileUpload createServletFileUpload(final HttpServletRequest request) {
        final DiskFileItemFactory fileItemFactory = createDiskFileItemFactory();
        final JakartaServletDiskFileUpload upload = newServletFileUpload(fileItemFactory);
        final Charset charset = getRequestCharset(request);
        if (charset != null) {
            upload.setHeaderCharset(charset);
        }
        upload.setSizeMax(getSizeMax());
        return upload;
    }

    protected Charset getRequestCharset(final HttpServletRequest request) {
        final String characterEncoding = request.getCharacterEncoding();
        try {
            return Charset.forName(characterEncoding);
        } catch (Exception e) {
            logger.warn("Invalid charset: {}", characterEncoding, e);
        }
        return null;
    }

    protected JakartaServletDiskFileUpload newServletFileUpload(final DiskFileItemFactory fileItemFactory) {
        return new JakartaServletDiskFileUpload(fileItemFactory) {
            @Override
            public byte[] getBoundary(final String contentType) { // for security
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
        return DiskFileItemFactory.builder().setFile(repository).setBufferSize(getSizeThreshold()).get();
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

    protected List<DiskFileItem> parseRequest(final HttpServletRequest request, final JakartaServletDiskFileUpload upload)
            throws FileUploadException {
        return upload.parseRequest(request);
    }

    protected void mappingParameter(final HttpServletRequest request, final List<DiskFileItem> items) {
        showFieldLoggingTitle();
        for (final DiskFileItem item : items) {
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

    protected void showFormFieldParameter(final DiskFileItem item) {
        if (logger.isDebugEnabled()) {
            logger.debug("[param] {}={}", item.getFieldName(), item.getString());
        }
    }

    protected void showFileFieldParameter(final DiskFileItem item) {
        if (logger.isDebugEnabled()) {
            logger.debug("[param] {}:{name={}, size={}}", item.getFieldName(), item.getName(), item.getSize());
        }
    }

    protected void handleSizeLimitExceededException(final HttpServletRequest request, final FileUploadByteCountLimitException e) {
        final long actual = e.getActualSize();
        final long permitted = e.getPermitted();
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
            try {
                formFile.destroy();
            } catch (final Exception e) {
                logger.warn("Failed to destroy {}", formFile, e);
            }
        }
    }

    // ===================================================================================
    //                                                                            Add Text
    //                                                                            ========
    protected void addTextParameter(final HttpServletRequest request, final DiskFileItem item) {
        final String name = item.getFieldName();
        final Charset encoding = getRequestCharset(request);
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
                value = item.getString(StandardCharsets.ISO_8859_1);
            } catch (final Exception uee) {
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

    protected void addFileParameter(final DiskFileItem item) {
        final MultipartFormFile formFile = newActionMultipartFormFile(item);
        elementsFile.put(item.getFieldName(), formFile);
        elementsAll.put(item.getFieldName(), formFile);
    }

    protected ActionMultipartFormFile newActionMultipartFormFile(final DiskFileItem item) {
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
    protected Integer getSizeMax() {
        return ComponentUtil.getFessConfig().getHttpFileuploadMaxSizeAsInteger();
    }

    protected Integer getSizeThreshold() {
        return ComponentUtil.getFessConfig().getHttpFileuploadThresholdSizeAsInteger();
    }

    protected String getRepositoryPath() {
        final File tempDirFile = (File) LaServletContextUtil.getServletContext().getAttribute(ServletContext.TEMPDIR);
        System.out.println("XXX " + tempDirFile);
        if (tempDirFile != null) {
            final String tempDir = tempDirFile.getAbsolutePath();
            System.out.println("XXX " + tempDir);
            if (StringUtil.isNotBlank(tempDir)) {
                System.out.println("XXX " + System.getProperty(JAVA_IO_TMPDIR_KEY));
//                return tempDir;
            }
        }
        return System.getProperty(JAVA_IO_TMPDIR_KEY);
    }

    // ===================================================================================
    //                                                                           Form File
    //                                                                           =========
    protected static class ActionMultipartFormFile implements MultipartFormFile, Serializable {

        private static final long serialVersionUID = 1L;

        protected final DiskFileItem fileItem;

        public ActionMultipartFormFile(final DiskFileItem fileItem) {
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
            try {
                fileItem.delete();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
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
