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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileUploadByteCountLimitException;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.helper.message.ExceptionMessageBuilder;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.exception.Forced404NotFoundException;
import org.lastaflute.web.ruts.multipart.MultipartFormFile;
import org.lastaflute.web.ruts.multipart.MultipartRequestHandler;
import org.lastaflute.web.ruts.multipart.MultipartRequestWrapper;
import org.lastaflute.web.ruts.multipart.exception.MultipartExceededException;
import org.lastaflute.web.util.LaServletContextUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The handler of multipart request (fileupload request). <br>
 * This instance is created per one multipart request.
 * @author modified by jflute (originated in Seasar)
 */
public class FessMultipartRequestHandler implements MultipartRequestHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger logger = LogManager.getLogger(FessMultipartRequestHandler.class);

    // -----------------------------------------------------
    //                                   Temporary Directory
    //                                   -------------------
    // used as repository for requested parameters
    protected static final String CONTEXT_TEMPDIR_KEY = "javax.servlet.context.tempdir"; // prior
    protected static final String JAVA_IO_TMPDIR_KEY = "java.io.tmpdir"; // secondary

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // keeping parsed request parameters, normal texts or uploaded files
    // keys are requested parameter names (treated as field name here)
    protected Map<String, Object> elementsAll; // lazy-loaded, then after not null
    protected Map<String, MultipartFormFile> elementsFile; // me too
    protected Map<String, String[]> elementsText; // me too

    // ===================================================================================
    //                                                                      Handle Request
    //                                                                      ==============
    @Override
    public void handleRequest(final HttpServletRequest request) throws ServletException {
        final JakartaServletDiskFileUpload upload = createDiskFileUpload(request);
        prepareElementsHash();
        try {
            final List<DiskFileItem> items = parseRequest(request, upload);
            mappingParameter(request, items);
        } catch (final FileUploadByteCountLimitException e) { // special handling
            handleSizeLimitExceededException(request, e);
        } catch (final FileUploadException e) { // contains fileCount exceeded
            handleFileUploadException(e);
        }
    }

    protected void prepareElementsHash() { // traditional name
        // #thinking jflute might lazy-loaded be unneeded? because created per request (2024/09/08)
        elementsAll = new HashMap<>();
        elementsText = new HashMap<>();
        elementsFile = new HashMap<>();
    }

    protected List<DiskFileItem> parseRequest(final HttpServletRequest request, final JakartaServletDiskFileUpload upload)
            throws FileUploadException {
        return upload.parseRequest(request);
    }

    // ===================================================================================
    //                                                                   ServletFileUpload
    //                                                                   =================
    protected JakartaServletDiskFileUpload createDiskFileUpload(final HttpServletRequest request) {
        final DiskFileItemFactory fileItemFactory = createDiskFileItemFactory();
        final JakartaServletDiskFileUpload upload = newServletFileUpload(fileItemFactory);
        setupServletFileUpload(upload, request);
        return upload;
    }

    // -----------------------------------------------------
    //                          DiskFileItemFactory Settings
    //                          ----------------------------
    protected DiskFileItemFactory createDiskFileItemFactory() {
        final int sizeThreshold = getSizeThreshold();
        final File repository = createRepositoryFile();
        return DiskFileItemFactory.builder().setBufferSize(sizeThreshold).setFile(repository).get();
    }

    protected int getSizeThreshold() {
        return ComponentUtil.getFessConfig().getHttpFileuploadThresholdSizeAsInteger();
    }

    protected File createRepositoryFile() {
        return new File(getRepositoryPath());
    }

    protected String getRepositoryPath() {
        final ServletContext servletContext = LaServletContextUtil.getServletContext();
        if (servletContext.getAttribute(CONTEXT_TEMPDIR_KEY) instanceof final File tempDirFile) {
            final String tempDir = tempDirFile.getAbsolutePath();
            if (tempDir != null && tempDir.length() > 0) {
                return tempDir;
            }
        }
        return System.getProperty(JAVA_IO_TMPDIR_KEY);
    }

    // -----------------------------------------------------
    //                            ServletFileUpload Settings
    //                            --------------------------
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

    // #for_now jflute to suppress CVE-2014-0050 even if commons-fileupload is older than safety version (2024/09/08)
    // but if you use safety version, this extension is basically unneeded (or you can use it as double check)
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
        br.addElement("Against for CVE-2014-0050 (JVN14876762).");
        br.addElement("Boundary size is limited by Framework.");
        br.addElement("Too long boundary is treated as 404 because it's thought of as attack.");
        br.addElement("");
        br.addElement("While, you can override the boundary limit size");
        br.addElement(" in " + getClass().getSimpleName() + ".");
        br.addItem("Content Type");
        br.addElement(contentType);
        br.addItem("Boundary Size");
        br.addElement(boundarySize);
        br.addItem("Limit Size");
        br.addElement(limitSize);
        final String msg = br.buildExceptionMessage();
        throw new Forced404NotFoundException(msg, UserMessages.empty()); // heavy attack!? so give no page to tell wasted action
    }

    protected void setupServletFileUpload(final JakartaServletDiskFileUpload upload, final HttpServletRequest request) {
        upload.setHeaderCharset(Charset.forName(request.getCharacterEncoding()));
        upload.setSizeMax(getSizeMax());
        upload.setFileCountMax(getFileCountMax()); // since commons-fileupload-1.5
    }

    protected long getSizeMax() {
        return ComponentUtil.getFessConfig().getHttpFileuploadMaxSizeAsInteger().longValue();
    }

    protected long getFileCountMax() {
        return ComponentUtil.getFessConfig().getHttpFileuploadMaxFileCountAsInteger().longValue();
    }

    // ===================================================================================
    //                                                                   Parameter Mapping
    //                                                                   =================
    protected void mappingParameter(final HttpServletRequest request, final List<DiskFileItem> items) {
        showFieldLoggingTitle();
        for (DiskFileItem item : items) {
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

    // -----------------------------------------------------
    //                                     Parameter Logging
    //                                     -----------------
    // logging filter cannot show the parameters when multi-part so logging here
    protected void showFieldLoggingTitle() {
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

    // ===================================================================================
    //                                                                       Add Parameter
    //                                                                       =============
    protected void addTextParameter(final HttpServletRequest request, final DiskFileItem item) {
        final String fieldName = item.getFieldName();
        final Charset encoding = Charset.forName(request.getCharacterEncoding());
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
                value = item.getString(Charset.forName("ISO-8859-1"));
            } catch (final java.io.UnsupportedEncodingException uee) {
                value = item.getString();
            } catch (final IOException e) {
                throw new IllegalStateException("Failed to get string from the item: " + item, e);
            }
            haveValue = true;
        }
        if (request instanceof final MultipartRequestWrapper wrapper) {
            wrapper.setParameter(fieldName, value);
        }
        final String[] oldArray = elementsText.get(fieldName);
        final String[] newArray;
        if (oldArray != null) {
            newArray = new String[oldArray.length + 1];
            System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
            newArray[oldArray.length] = value;
        } else {
            newArray = new String[] { value };
        }
        elementsAll.put(fieldName, newArray);
        elementsText.put(fieldName, newArray);
    }

    protected void addFileParameter(final DiskFileItem item) {
        final String fieldName = item.getFieldName();
        final MultipartFormFile formFile = newActionMultipartFormFile(item);
        elementsAll.put(fieldName, formFile);
        elementsFile.put(fieldName, formFile);
    }

    protected ActionMultipartFormFile newActionMultipartFormFile(final DiskFileItem item) {
        return new ActionMultipartFormFile(item);
    }

    // ===================================================================================
    //                                                                  Exception Handling
    //                                                                  ==================
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
        for (MultipartFormFile formFile : elementsFile.values()) {
            formFile.destroy();
        }
    }

    // ===================================================================================
    //                                                                              Finish
    //                                                                              ======
    @Override
    public void finish() {
        rollback();
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
            int colonIndex = fileName.indexOf(":");
            if (colonIndex == -1) {
                colonIndex = fileName.indexOf("\\\\"); // Windows SMB
            }
            final int backslashIndex = fileName.lastIndexOf("\\");
            if (colonIndex > -1 && backslashIndex > -1) {
                return fileName.substring(backslashIndex + 1);
            }
            return fileName;
        }

        @Override
        public void destroy() {
            try {
                fileItem.delete();
            } catch (final IOException e) {
                throw new IllegalStateException("Failed to delete the fileItem: " + fileItem, e);
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
    public Map<String, Object> getAllElements() { // not null after parsing
        return elementsAll;
    }

    @Override
    public Map<String, String[]> getTextElements() { // me too
        return elementsText;
    }

    @Override
    public Map<String, MultipartFormFile> getFileElements() { // me too
        return elementsFile;
    }
}
