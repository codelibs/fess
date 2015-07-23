/*
 * Copyright 2012 the CodeLibs Project and the Others.
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
package org.codelibs.sastruts.core.upload;

import java.io.File;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.lastaflute.web.ruts.config.ModuleConfig;
import org.lastaflute.web.ruts.multipart.MultipartRequestHandler;

/**
 * This class supports a multipart request handler to support commons-fileupload
 * 1.2.2.
 * 
 * @author shinsuke
 * 
 */
public class SSCMultipartRequestHandler extends S2MultipartRequestHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public void handleRequest(final HttpServletRequest request) throws ServletException {
        final ModuleConfig ac = (ModuleConfig) request.getAttribute(Globals.MODULE_KEY);
        final FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(ServletContextUtil.getServletContext());
        final DiskFileItemFactory fileItemFactory = new DiskFileItemFactory((int) getSizeThreshold(ac), new File(getRepositoryPath(ac)));
        request.setAttribute("fileItemFactory", fileItemFactory);
        fileItemFactory.setFileCleaningTracker(fileCleaningTracker);
        final ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
        upload.setHeaderEncoding(request.getCharacterEncoding());
        upload.setSizeMax(getSizeMax(ac));
        elementsText = new Hashtable();
        elementsFile = new Hashtable();
        elementsAll = new Hashtable();
        List items = null;
        try {
            items = upload.parseRequest(request);
        } catch (final SizeLimitExceededException e) {
            request.setAttribute(MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED, Boolean.TRUE);
            request.setAttribute(SIZE_EXCEPTION_KEY, e);
            try {
                final InputStream is = request.getInputStream();
                try {
                    final byte[] buf = new byte[1024];
                    while ((is.read(buf)) != -1) { // NOPMD
                    }
                } catch (final Exception ignore) {} finally {
                    try {
                        is.close();
                    } catch (final Exception ignore) {}
                }
            } catch (final Exception ignore) {}
            return;
        } catch (final FileUploadException e) {
            log.error("Failed to parse multipart request", e);
            throw new ServletException(e);
        }

        // Partition the items into form fields and files.
        final Iterator iter = items.iterator();
        while (iter.hasNext()) {
            final FileItem item = (FileItem) iter.next();

            if (item.isFormField()) {
                addTextParameter(request, item);
            } else {
                addFileParameter(item);
            }
        }
    }
}
