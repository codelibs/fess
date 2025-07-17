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
package org.codelibs.fess.app.web.admin.failureurl;

import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * Form class for editing failure URL records in the admin interface.
 * This form handles the editing of URLs that failed during crawling operations,
 * providing details about the failure for debugging and troubleshooting purposes.
 */
public class EditForm {

    /**
     * Creates a new EditForm instance.
     */
    public EditForm() {
        super();
    }

    /**
     * The name of the web configuration associated with this failure URL.
     * Used to identify which web crawling configuration encountered the failure.
     */
    public String webConfigName;

    /**
     * The name of the file configuration associated with this failure URL.
     * Used to identify which file crawling configuration encountered the failure.
     */
    public String fileConfigName;

    /**
     * The current page number for pagination in the failure URL list.
     * Used for navigating through multiple pages of failure records.
     */
    @ValidateTypeFailure
    public String pageNumber;

    /**
     * The CRUD operation mode for this form.
     * Indicates whether this is a create, read, update, or delete operation.
     */
    @ValidateTypeFailure
    public Integer crudMode;

    /**
     * The unique identifier of the failure URL record being edited.
     * This is a required field for identifying which failure record to update.
     */
    @Required
    @Size(max = 1000)
    public String id;

    /**
     * The URL that failed during crawling.
     * This is a required field containing the actual URL that encountered an error.
     */
    @Required
    public String url;

    /**
     * The name of the crawler thread that encountered the failure.
     * This is a required field used for identifying which crawler process failed.
     */
    @Required
    public String threadName;

    /**
     * The name or type of the error that occurred.
     * This field provides a categorization of the failure type.
     */
    public String errorName;

    /**
     * The detailed error log or stack trace for the failure.
     * This field contains the full error information for debugging purposes.
     */
    public String errorLog;

    /**
     * The number of times this URL has failed.
     * This is a required field that tracks repeated failures for the same URL.
     */
    @Required
    @ValidateTypeFailure
    public String errorCount;

    /**
     * The timestamp of the last access attempt for this URL.
     * This is a required field indicating when the failure last occurred.
     */
    @Required
    public String lastAccessTime;

    /**
     * The configuration ID associated with this failure URL.
     * Links the failure to a specific crawling configuration.
     */
    @Size(max = 1000)
    public String configId;

    /**
     * Returns the current page number for pagination.
     * This method provides access to the current page number for UI display.
     *
     * @return The current page number as a string
     */
    public String getCurrentPageNumber() {
        return pageNumber;
    }

    /**
     * Initializes the form with default null values.
     * This method resets all fields to their default state for creating a new entry.
     */
    public void initialize() {
        id = null;
        url = null;
        threadName = null;
        errorName = null;
        errorLog = null;
        errorCount = null;
        lastAccessTime = null;
        configId = null;
    }

}
