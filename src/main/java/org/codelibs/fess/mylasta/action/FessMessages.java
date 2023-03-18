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
package org.codelibs.fess.mylasta.action;

import org.lastaflute.core.message.UserMessage;

/**
 * The keys for message.
 * @author FreeGen
 */
public class FessMessages extends FessLabels {

    /** The serial version UID for object serialization. (Default) */
    private static final long serialVersionUID = 1L;

    /** The key of the message:  */
    public static final String ERRORS_front_header = "{errors.front_header}";

    /** The key of the message:  */
    public static final String ERRORS_front_footer = "{errors.front_footer}";

    /** The key of the message: &lt;div class="alert alert-warning"&gt; */
    public static final String ERRORS_front_prefix = "{errors.front_prefix}";

    /** The key of the message: &lt;/div&gt; */
    public static final String ERRORS_front_suffix = "{errors.front_suffix}";

    /** The key of the message: &lt;ul class="has-error"&gt; */
    public static final String ERRORS_HEADER = "{errors.header}";

    /** The key of the message: &lt;/ul&gt; */
    public static final String ERRORS_FOOTER = "{errors.footer}";

    /** The key of the message: &lt;li&gt;&lt;i class="fa fa-exclamation-circle"&gt;&lt;/i&gt; */
    public static final String ERRORS_PREFIX = "{errors.prefix}";

    /** The key of the message: &lt;/li&gt; */
    public static final String ERRORS_SUFFIX = "{errors.suffix}";

    /** The key of the message: {item} must be false. */
    public static final String CONSTRAINTS_AssertFalse_MESSAGE = "{constraints.AssertFalse.message}";

    /** The key of the message: {item} must be true. */
    public static final String CONSTRAINTS_AssertTrue_MESSAGE = "{constraints.AssertTrue.message}";

    /** The key of the message: {item} must be less than ${inclusive == true ? 'or equal to ' : ''}{value}. */
    public static final String CONSTRAINTS_DecimalMax_MESSAGE = "{constraints.DecimalMax.message}";

    /** The key of the message: {item} must be greater than ${inclusive == true ? 'or equal to ' : ''}{value}. */
    public static final String CONSTRAINTS_DecimalMin_MESSAGE = "{constraints.DecimalMin.message}";

    /** The key of the message: {item} is numeric value out of bounds (&lt;{integer} digits&gt;.&lt;{fraction} digits&gt; expected). */
    public static final String CONSTRAINTS_Digits_MESSAGE = "{constraints.Digits.message}";

    /** The key of the message: {item} must be in the future. */
    public static final String CONSTRAINTS_Future_MESSAGE = "{constraints.Future.message}";

    /** The key of the message: {item} must be less than or equal to {value}. */
    public static final String CONSTRAINTS_Max_MESSAGE = "{constraints.Max.message}";

    /** The key of the message: {item} must be greater than or equal to {value}. */
    public static final String CONSTRAINTS_Min_MESSAGE = "{constraints.Min.message}";

    /** The key of the message: {item} may not be null. */
    public static final String CONSTRAINTS_NotNull_MESSAGE = "{constraints.NotNull.message}";

    /** The key of the message: {item} must be null. */
    public static final String CONSTRAINTS_Null_MESSAGE = "{constraints.Null.message}";

    /** The key of the message: {item} must be in the past. */
    public static final String CONSTRAINTS_Past_MESSAGE = "{constraints.Past.message}";

    /** The key of the message: {item} must match "{regexp}". */
    public static final String CONSTRAINTS_Pattern_MESSAGE = "{constraints.Pattern.message}";

    /** The key of the message: Size of {item} must be between {min} and {max}. */
    public static final String CONSTRAINTS_Size_MESSAGE = "{constraints.Size.message}";

    /** The key of the message: {item} is invalid credit card number. */
    public static final String CONSTRAINTS_CreditCardNumber_MESSAGE = "{constraints.CreditCardNumber.message}";

    /** The key of the message: {item} is invalid {type} barcode. */
    public static final String CONSTRAINTS_EAN_MESSAGE = "{constraints.EAN.message}";

    /** The key of the message: {item} is not a well-formed email address. */
    public static final String CONSTRAINTS_Email_MESSAGE = "{constraints.Email.message}";

    /** The key of the message: Length of {item} must be between {min} and {max}. */
    public static final String CONSTRAINTS_Length_MESSAGE = "{constraints.Length.message}";

    /** The key of the message: The check digit for ${value} is invalid, Luhn Modulo 10 checksum failed. */
    public static final String CONSTRAINTS_LuhnCheck_MESSAGE = "{constraints.LuhnCheck.message}";

    /** The key of the message: The check digit for ${value} is invalid, Modulo 10 checksum failed. */
    public static final String CONSTRAINTS_Mod10Check_MESSAGE = "{constraints.Mod10Check.message}";

    /** The key of the message: The check digit for ${value} is invalid, Modulo 11 checksum failed. */
    public static final String CONSTRAINTS_Mod11Check_MESSAGE = "{constraints.Mod11Check.message}";

    /** The key of the message: The check digit for ${value} is invalid, ${modType} checksum failed. */
    public static final String CONSTRAINTS_ModCheck_MESSAGE = "{constraints.ModCheck.message}";

    /** The key of the message: {item} may not be empty. */
    public static final String CONSTRAINTS_NotBlank_MESSAGE = "{constraints.NotBlank.message}";

    /** The key of the message: {item} may not be empty. */
    public static final String CONSTRAINTS_NotEmpty_MESSAGE = "{constraints.NotEmpty.message}";

    /** The key of the message: script expression "{script}" didn't evaluate to true. */
    public static final String CONSTRAINTS_ParametersScriptAssert_MESSAGE = "{constraints.ParametersScriptAssert.message}";

    /** The key of the message: {item} must be between {min} and {max}. */
    public static final String CONSTRAINTS_Range_MESSAGE = "{constraints.Range.message}";

    /** The key of the message: {item} may have unsafe html content. */
    public static final String CONSTRAINTS_SafeHtml_MESSAGE = "{constraints.SafeHtml.message}";

    /** The key of the message: script expression "{script}" didn't evaluate to true. */
    public static final String CONSTRAINTS_ScriptAssert_MESSAGE = "{constraints.ScriptAssert.message}";

    /** The key of the message: {item} must be a valid URL. */
    public static final String CONSTRAINTS_URL_MESSAGE = "{constraints.URL.message}";

    /** The key of the message: {item} is required. */
    public static final String CONSTRAINTS_Required_MESSAGE = "{constraints.Required.message}";

    /** The key of the message: {item} should be numeric. */
    public static final String CONSTRAINTS_TypeInteger_MESSAGE = "{constraints.TypeInteger.message}";

    /** The key of the message: {item} should be numeric. */
    public static final String CONSTRAINTS_TypeLong_MESSAGE = "{constraints.TypeLong.message}";

    /** The key of the message: {item} should be numeric. */
    public static final String CONSTRAINTS_TypeFloat_MESSAGE = "{constraints.TypeFloat.message}";

    /** The key of the message: {item} should be numeric. */
    public static final String CONSTRAINTS_TypeDouble_MESSAGE = "{constraints.TypeDouble.message}";

    /** The key of the message: {item} cannot convert as {propertyType}. */
    public static final String CONSTRAINTS_TypeAny_MESSAGE = "{constraints.TypeAny.message}";

    /** The key of the message: {item} has wrong URI. */
    public static final String CONSTRAINTS_UriType_MESSAGE = "{constraints.UriType.message}";

    /** The key of the message: {item} is invalid cron expression. */
    public static final String CONSTRAINTS_CronExpression_MESSAGE = "{constraints.CronExpression.message}";

    /** The key of the message: Login failed. */
    public static final String ERRORS_LOGIN_FAILURE = "{errors.login.failure}";

    /** The key of the message: Please retry because of illegal transition. */
    public static final String ERRORS_APP_ILLEGAL_TRANSITION = "{errors.app.illegal.transition}";

    /** The key of the message: others might be updated, so retry. */
    public static final String ERRORS_APP_DB_ALREADY_DELETED = "{errors.app.db.already.deleted}";

    /** The key of the message: others might be updated, so retry. */
    public static final String ERRORS_APP_DB_ALREADY_UPDATED = "{errors.app.db.already.updated}";

    /** The key of the message: already existing data, so retry. */
    public static final String ERRORS_APP_DB_ALREADY_EXISTS = "{errors.app.db.already.exists}";

    /** The key of the message: Your request might have been processed before this request. Please check and retry it. */
    public static final String ERRORS_APP_DOUBLE_SUBMIT_REQUEST = "{errors.app.double.submit.request}";

    /** The key of the message: Username or Password is not correct. */
    public static final String ERRORS_login_error = "{errors.login_error}";

    /** The key of the message: Failed to process SSO login. */
    public static final String ERRORS_sso_login_error = "{errors.sso_login_error}";

    /** The key of the message: Could not find {0}. */
    public static final String ERRORS_could_not_find_log_file = "{errors.could_not_find_log_file}";

    /** The key of the message: Failed to start a crawl process. */
    public static final String ERRORS_failed_to_start_crawl_process = "{errors.failed_to_start_crawl_process}";

    /** The key of the message: Invalid JSP file. */
    public static final String ERRORS_invalid_design_jsp_file_name = "{errors.invalid_design_jsp_file_name}";

    /** The key of the message: JSP file does not exist. */
    public static final String ERRORS_design_jsp_file_does_not_exist = "{errors.design_jsp_file_does_not_exist}";

    /** The key of the message: The file name is not specified. */
    public static final String ERRORS_design_file_name_is_not_found = "{errors.design_file_name_is_not_found}";

    /** The key of the message: Failed to upload an image file. */
    public static final String ERRORS_failed_to_write_design_image_file = "{errors.failed_to_write_design_image_file}";

    /** The key of the message: Failed to update a jsp file. */
    public static final String ERRORS_failed_to_update_jsp_file = "{errors.failed_to_update_jsp_file}";

    /** The key of the message: The file name is invalid. */
    public static final String ERRORS_design_file_name_is_invalid = "{errors.design_file_name_is_invalid}";

    /** The key of the message: The kind of file is unsupported. */
    public static final String ERRORS_design_file_is_unsupported_type = "{errors.design_file_is_unsupported_type}";

    /** The key of the message: Failed to create a crawling config. */
    public static final String ERRORS_failed_to_create_crawling_config_at_wizard = "{errors.failed_to_create_crawling_config_at_wizard}";

    /** The key of the message: This feature is disabled. */
    public static final String ERRORS_design_editor_disabled = "{errors.design_editor_disabled}";

    /** The key of the message: Not Found: {0} */
    public static final String ERRORS_not_found_on_file_system = "{errors.not_found_on_file_system}";

    /** The key of the message: Could not open {0}. &lt;br/&gt;Please check if the file is associated with an application. */
    public static final String ERRORS_could_not_open_on_system = "{errors.could_not_open_on_system}";

    /** The key of the message: No more results could be displayed. */
    public static final String ERRORS_result_size_exceeded = "{errors.result_size_exceeded}";

    /** The key of the message: {0} file does not exist. */
    public static final String ERRORS_target_file_does_not_exist = "{errors.target_file_does_not_exist}";

    /** The key of the message: Failed to delete {0} file. */
    public static final String ERRORS_failed_to_delete_file = "{errors.failed_to_delete_file}";

    /** The key of the message: Not found Doc ID:{0} */
    public static final String ERRORS_docid_not_found = "{errors.docid_not_found}";

    /** The key of the message: Not found URL of Doc ID:{0} */
    public static final String ERRORS_document_not_found = "{errors.document_not_found}";

    /** The key of the message: Could not load from this server: {0} */
    public static final String ERRORS_not_load_from_server = "{errors.not_load_from_server}";

    /** The key of the message: Failed to start job {0}. */
    public static final String ERRORS_failed_to_start_job = "{errors.failed_to_start_job}";

    /** The key of the message: Failed to stop job {0}. */
    public static final String ERRORS_failed_to_stop_job = "{errors.failed_to_stop_job}";

    /** The key of the message: Failed to download the Synonym file. */
    public static final String ERRORS_failed_to_download_synonym_file = "{errors.failed_to_download_synonym_file}";

    /** The key of the message: Failed to upload the Synonym file. */
    public static final String ERRORS_failed_to_upload_synonym_file = "{errors.failed_to_upload_synonym_file}";

    /** The key of the message: Failed to download the Stemmer Override file. */
    public static final String ERRORS_failed_to_download_stemmeroverride_file = "{errors.failed_to_download_stemmeroverride_file}";

    /** The key of the message: Failed to upload the Stemmer Override file. */
    public static final String ERRORS_failed_to_upload_stemmeroverride_file = "{errors.failed_to_upload_stemmeroverride_file}";

    /** The key of the message: Failed to download the Kuromoji file. */
    public static final String ERRORS_failed_to_download_kuromoji_file = "{errors.failed_to_download_kuromoji_file}";

    /** The key of the message: Failed to upload the Kuromoji file. */
    public static final String ERRORS_failed_to_upload_kuromoji_file = "{errors.failed_to_upload_kuromoji_file}";

    /** The key of the message: Failed to download the Protwords file. */
    public static final String ERRORS_failed_to_download_protwords_file = "{errors.failed_to_download_protwords_file}";

    /** The key of the message: Failed to upload the Protwords file. */
    public static final String ERRORS_failed_to_upload_protwords_file = "{errors.failed_to_upload_protwords_file}";

    /** The key of the message: Failed to download the Stopwords file. */
    public static final String ERRORS_failed_to_download_stopwords_file = "{errors.failed_to_download_stopwords_file}";

    /** The key of the message: Failed to upload the Stopwords file. */
    public static final String ERRORS_failed_to_upload_stopwords_file = "{errors.failed_to_upload_stopwords_file}";

    /** The key of the message: Failed to download the Elevate file. */
    public static final String ERRORS_failed_to_download_elevate_file = "{errors.failed_to_download_elevate_file}";

    /** The key of the message: Failed to upload the Elevate file. */
    public static final String ERRORS_failed_to_upload_elevate_file = "{errors.failed_to_upload_elevate_file}";

    /** The key of the message: Failed to download the Badword file. */
    public static final String ERRORS_failed_to_download_badword_file = "{errors.failed_to_download_badword_file}";

    /** The key of the message: Failed to upload the Badword file. */
    public static final String ERRORS_failed_to_upload_badword_file = "{errors.failed_to_upload_badword_file}";

    /** The key of the message: Failed to download the Mapping file. */
    public static final String ERRORS_failed_to_download_mapping_file = "{errors.failed_to_download_mapping_file}";

    /** The key of the message: Failed to upload the Mapping file. */
    public static final String ERRORS_failed_to_upload_mapping_file = "{errors.failed_to_upload_mapping_file}";

    /** The key of the message: {0} is invalid. */
    public static final String ERRORS_invalid_kuromoji_token = "{errors.invalid_kuromoji_token}";

    /** The key of the message: The number of segmentations {0} does not the match number of readings {1}. */
    public static final String ERRORS_invalid_kuromoji_segmentation = "{errors.invalid_kuromoji_segmentation}";

    /** The key of the message: "{1}" in "{0}" is invalid. */
    public static final String ERRORS_invalid_str_is_included = "{errors.invalid_str_is_included}";

    /** The key of the message: Password is required. */
    public static final String ERRORS_blank_password = "{errors.blank_password}";

    /** The key of the message: Confirm Password does not match. */
    public static final String ERRORS_invalid_confirm_password = "{errors.invalid_confirm_password}";

    /** The key of the message: Crawler is running. The document cannot be deleted. */
    public static final String ERRORS_cannot_delete_doc_because_of_running = "{errors.cannot_delete_doc_because_of_running}";

    /** The key of the message: Failed to delete document. */
    public static final String ERRORS_failed_to_delete_doc_in_admin = "{errors.failed_to_delete_doc_in_admin}";

    /** The key of the message: Failed to send the test mail. */
    public static final String ERRORS_failed_to_send_testmail = "{errors.failed_to_send_testmail}";

    /** The key of the message: Could not find index for backup. */
    public static final String ERRORS_could_not_find_backup_index = "{errors.could_not_find_backup_index}";

    /** The key of the message: The current password is incorrect. */
    public static final String ERRORS_no_user_for_changing_password = "{errors.no_user_for_changing_password}";

    /** The key of the message: Failed to change your password. */
    public static final String ERRORS_failed_to_change_password = "{errors.failed_to_change_password}";

    /** The key of the message: Unknown version information. */
    public static final String ERRORS_unknown_version_for_upgrade = "{errors.unknown_version_for_upgrade}";

    /** The key of the message: Failed to upgrade from {0}: {1} */
    public static final String ERRORS_failed_to_upgrade_from = "{errors.failed_to_upgrade_from}";

    /** The key of the message: Failed to start reindexing from {0} to {1} */
    public static final String ERRORS_failed_to_reindex = "{errors.failed_to_reindex}";

    /** The key of the message: Failed to read request file: {0} */
    public static final String ERRORS_failed_to_read_request_file = "{errors.failed_to_read_request_file}";

    /** The key of the message: Invalid header: {0} */
    public static final String ERRORS_invalid_header_for_request_file = "{errors.invalid_header_for_request_file}";

    /** The key of the message: Could not delete logged in user. */
    public static final String ERRORS_could_not_delete_logged_in_user = "{errors.could_not_delete_logged_in_user}";

    /** The key of the message: Unauthorized request. */
    public static final String ERRORS_unauthorized_request = "{errors.unauthorized_request}";

    /** The key of the message: Failed to print thread dump. */
    public static final String ERRORS_failed_to_print_thread_dump = "{errors.failed_to_print_thread_dump}";

    /** The key of the message: {0} is not supported. */
    public static final String ERRORS_file_is_not_supported = "{errors.file_is_not_supported}";

    /** The key of the message: {0} is not found. */
    public static final String ERRORS_plugin_file_is_not_found = "{errors.plugin_file_is_not_found}";

    /** The key of the message: Failed to install {0}. */
    public static final String ERRORS_failed_to_install_plugin = "{errors.failed_to_install_plugin}";

    /** The key of the message: Failed to access available plugins. */
    public static final String ERRORS_failed_to_find_plugins = "{errors.failed_to_find_plugins}";

    /** The key of the message: Failed to process the request: {0} */
    public static final String ERRORS_failed_to_process_sso_request = "{errors.failed_to_process_sso_request}";

    /** The key of the message: The given query has unknown condition. */
    public static final String ERRORS_invalid_query_unknown = "{errors.invalid_query_unknown}";

    /** The key of the message: The given query is invalid. */
    public static final String ERRORS_invalid_query_parse_error = "{errors.invalid_query_parse_error}";

    /** The key of the message: The given sort ({0}) is invalid. */
    public static final String ERRORS_invalid_query_sort_value = "{errors.invalid_query_sort_value}";

    /** The key of the message: The given sort ({0}) is not supported. */
    public static final String ERRORS_invalid_query_unsupported_sort_field = "{errors.invalid_query_unsupported_sort_field}";

    /** The key of the message: The given sort order ({0}) is not supported. */
    public static final String ERRORS_invalid_query_unsupported_sort_order = "{errors.invalid_query_unsupported_sort_order}";

    /** The key of the message: The given query could not be processed. */
    public static final String ERRORS_invalid_query_cannot_process = "{errors.invalid_query_cannot_process}";

    /** The key of the message: Invalid mode(expected value is {0}, but it's {1}). */
    public static final String ERRORS_crud_invalid_mode = "{errors.crud_invalid_mode}";

    /** The key of the message: Failed to create a new data. */
    public static final String ERRORS_crud_failed_to_create_instance = "{errors.crud_failed_to_create_instance}";

    /** The key of the message: Failed to create a new data. ({0}) */
    public static final String ERRORS_crud_failed_to_create_crud_table = "{errors.crud_failed_to_create_crud_table}";

    /** The key of the message: Failed to update the data. ({0}) */
    public static final String ERRORS_crud_failed_to_update_crud_table = "{errors.crud_failed_to_update_crud_table}";

    /** The key of the message: Failed to delete the data. ({0}) */
    public static final String ERRORS_crud_failed_to_delete_crud_table = "{errors.crud_failed_to_delete_crud_table}";

    /** The key of the message: Could not find the data({0}). */
    public static final String ERRORS_crud_could_not_find_crud_table = "{errors.crud_could_not_find_crud_table}";

    /** The key of the message: {0} is required. */
    public static final String ERRORS_property_required = "{errors.property_required}";

    /** The key of the message: {0} should be numeric. */
    public static final String ERRORS_property_type_integer = "{errors.property_type_integer}";

    /** The key of the message: {0} should be numeric. */
    public static final String ERRORS_property_type_long = "{errors.property_type_long}";

    /** The key of the message: {0} should be numeric. */
    public static final String ERRORS_property_type_float = "{errors.property_type_float}";

    /** The key of the message: {0} should be numeric. */
    public static final String ERRORS_property_type_double = "{errors.property_type_double}";

    /** The key of the message: {0} should be date. */
    public static final String ERRORS_property_type_date = "{errors.property_type_date}";

    /** The key of the message: Failed to upload {0}. */
    public static final String ERRORS_storage_file_upload_failure = "{errors.storage_file_upload_failure}";

    /** The key of the message: The target file is not found in Storage. */
    public static final String ERRORS_storage_file_not_found = "{errors.storage_file_not_found}";

    /** The key of the message: Failed to download {0}. */
    public static final String ERRORS_storage_file_download_failure = "{errors.storage_file_download_failure}";

    /** The key of the message: Storage access error: {0} */
    public static final String ERRORS_storage_access_error = "{errors.storage_access_error}";

    /** The key of the message: Upload file is required. */
    public static final String ERRORS_storage_no_upload_file = "{errors.storage_no_upload_file}";

    /** The key of the message: Directory name is invalid. */
    public static final String ERRORS_storage_directory_name_is_invalid = "{errors.storage_directory_name_is_invalid}";

    /** The key of the message: Failed to update tags for {0} */
    public static final String ERRORS_storage_tags_update_failure = "{errors.storage_tags_update_failure}";

    /** The key of the message: Updated parameters. */
    public static final String SUCCESS_update_crawler_params = "{success.update_crawler_params}";

    /** The key of the message: Started a process to delete the document from index. */
    public static final String SUCCESS_delete_doc_from_index = "{success.delete_doc_from_index}";

    /** The key of the message: Deleted session data. */
    public static final String SUCCESS_crawling_info_delete_all = "{success.crawling_info_delete_all}";

    /** The key of the message: Started a crawl process. */
    public static final String SUCCESS_start_crawl_process = "{success.start_crawl_process}";

    /** The key of the message: Uploaded {0}. */
    public static final String SUCCESS_upload_design_file = "{success.upload_design_file}";

    /** The key of the message: Updated {0}. */
    public static final String SUCCESS_update_design_jsp_file = "{success.update_design_jsp_file}";

    /** The key of the message: Created a crawling config ({0}). */
    public static final String SUCCESS_create_crawling_config_at_wizard = "{success.create_crawling_config_at_wizard}";

    /** The key of the message: Deleted failure urls. */
    public static final String SUCCESS_failure_url_delete_all = "{success.failure_url_delete_all}";

    /** The key of the message: Deleted {0} file. */
    public static final String SUCCESS_delete_file = "{success.delete_file}";

    /** The key of the message: Started job {0}. */
    public static final String SUCCESS_job_started = "{success.job_started}";

    /** The key of the message: Stopped job {0}. */
    public static final String SUCCESS_job_stopped = "{success.job_stopped}";

    /** The key of the message: Uploaded Synonym file. */
    public static final String SUCCESS_upload_synonym_file = "{success.upload_synonym_file}";

    /** The key of the message: Uploaded Protwords file. */
    public static final String SUCCESS_upload_protwords_file = "{success.upload_protwords_file}";

    /** The key of the message: Uploaded Stopwords file. */
    public static final String SUCCESS_upload_stopwords_file = "{success.upload_stopwords_file}";

    /** The key of the message: Uploaded Stemmer Override file. */
    public static final String SUCCESS_upload_stemmeroverride_file = "{success.upload_stemmeroverride_file}";

    /** The key of the message: Uploaded Kuromoji file. */
    public static final String SUCCESS_upload_kuromoji_file = "{success.upload_kuromoji_file}";

    /** The key of the message: Uploaded Additional Word file. */
    public static final String SUCCESS_upload_elevate_word = "{success.upload_elevate_word}";

    /** The key of the message: Uploaded Bad Word file. */
    public static final String SUCCESS_upload_bad_word = "{success.upload_bad_word}";

    /** The key of the message: Uploaded Mapping file. */
    public static final String SUCCESS_upload_mapping_file = "{success.upload_mapping_file}";

    /** The key of the message: Sent the test mail. */
    public static final String SUCCESS_send_testmail = "{success.send_testmail}";

    /** The key of the message: Deleted job logs. */
    public static final String SUCCESS_job_log_delete_all = "{success.job_log_delete_all}";

    /** The key of the message: Changed your password. */
    public static final String SUCCESS_changed_password = "{success.changed_password}";

    /** The key of the message: Started data update process. */
    public static final String SUCCESS_started_data_update = "{success.started_data_update}";

    /** The key of the message: Started reindexing. */
    public static final String SUCCESS_reindex_started = "{success.reindex_started}";

    /** The key of the message: Bulk process is started. */
    public static final String SUCCESS_bulk_process_started = "{success.bulk_process_started}";

    /** The key of the message: Printed thread dump to log file. */
    public static final String SUCCESS_print_thread_dump = "{success.print_thread_dump}";

    /** The key of the message: Installing {0} plugin. */
    public static final String SUCCESS_install_plugin = "{success.install_plugin}";

    /** The key of the message: Deleting {0} plugin. */
    public static final String SUCCESS_delete_plugin = "{success.delete_plugin}";

    /** The key of the message: Uploaded {0} */
    public static final String SUCCESS_upload_file_to_storage = "{success.upload_file_to_storage}";

    /** The key of the message: Logged out. */
    public static final String SUCCESS_sso_logout = "{success.sso_logout}";

    /** The key of the message: Updated tags for {0}. */
    public static final String SUCCESS_update_storage_tags = "{success.update_storage_tags}";

    /** The key of the message: Created data. */
    public static final String SUCCESS_crud_create_crud_table = "{success.crud_create_crud_table}";

    /** The key of the message: Updated data. */
    public static final String SUCCESS_crud_update_crud_table = "{success.crud_update_crud_table}";

    /** The key of the message: Deleted data. */
    public static final String SUCCESS_crud_delete_crud_table = "{success.crud_delete_crud_table}";

    /**
     * Add the created action message for the key 'errors.front_header' with parameters.
     * <pre>
     * message:
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFrontHeader(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_front_header));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.front_footer' with parameters.
     * <pre>
     * message:
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFrontFooter(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_front_footer));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.front_prefix' with parameters.
     * <pre>
     * message: &lt;div class="alert alert-warning"&gt;
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFrontPrefix(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_front_prefix));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.front_suffix' with parameters.
     * <pre>
     * message: &lt;/div&gt;
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFrontSuffix(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_front_suffix));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.header' with parameters.
     * <pre>
     * message: &lt;ul class="has-error"&gt;
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsHeader(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_HEADER));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.footer' with parameters.
     * <pre>
     * message: &lt;/ul&gt;
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFooter(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_FOOTER));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.prefix' with parameters.
     * <pre>
     * message: &lt;li&gt;&lt;i class="fa fa-exclamation-circle"&gt;&lt;/i&gt;
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsPrefix(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_PREFIX));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.suffix' with parameters.
     * <pre>
     * message: &lt;/li&gt;
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsSuffix(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_SUFFIX));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.AssertFalse.message' with parameters.
     * <pre>
     * message: {item} must be false.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsAssertFalseMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_AssertFalse_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.AssertTrue.message' with parameters.
     * <pre>
     * message: {item} must be true.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsAssertTrueMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_AssertTrue_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.DecimalMax.message' with parameters.
     * <pre>
     * message: {item} must be less than ${inclusive == true ? 'or equal to ' : ''}{value}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param value The parameter value for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsDecimalMaxMessage(String property, String value) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_DecimalMax_MESSAGE, value));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.DecimalMin.message' with parameters.
     * <pre>
     * message: {item} must be greater than ${inclusive == true ? 'or equal to ' : ''}{value}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param value The parameter value for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsDecimalMinMessage(String property, String value) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_DecimalMin_MESSAGE, value));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.Digits.message' with parameters.
     * <pre>
     * message: {item} is numeric value out of bounds (&lt;{integer} digits&gt;.&lt;{fraction} digits&gt; expected).
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param fraction The parameter fraction for message. (NotNull)
     * @param integer The parameter integer for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsDigitsMessage(String property, String fraction, String integer) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_Digits_MESSAGE, fraction, integer));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.Future.message' with parameters.
     * <pre>
     * message: {item} must be in the future.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsFutureMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_Future_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.Max.message' with parameters.
     * <pre>
     * message: {item} must be less than or equal to {value}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param value The parameter value for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsMaxMessage(String property, String value) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_Max_MESSAGE, value));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.Min.message' with parameters.
     * <pre>
     * message: {item} must be greater than or equal to {value}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param value The parameter value for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsMinMessage(String property, String value) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_Min_MESSAGE, value));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.NotNull.message' with parameters.
     * <pre>
     * message: {item} may not be null.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsNotNullMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_NotNull_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.Null.message' with parameters.
     * <pre>
     * message: {item} must be null.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsNullMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_Null_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.Past.message' with parameters.
     * <pre>
     * message: {item} must be in the past.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsPastMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_Past_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.Pattern.message' with parameters.
     * <pre>
     * message: {item} must match "{regexp}".
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param regexp The parameter regexp for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsPatternMessage(String property, String regexp) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_Pattern_MESSAGE, regexp));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.Size.message' with parameters.
     * <pre>
     * message: Size of {item} must be between {min} and {max}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param min The parameter min for message. (NotNull)
     * @param max The parameter max for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsSizeMessage(String property, String min, String max) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_Size_MESSAGE, min, max));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.CreditCardNumber.message' with parameters.
     * <pre>
     * message: {item} is invalid credit card number.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsCreditCardNumberMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_CreditCardNumber_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.EAN.message' with parameters.
     * <pre>
     * message: {item} is invalid {type} barcode.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param type The parameter type for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsEanMessage(String property, String type) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_EAN_MESSAGE, type));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.Email.message' with parameters.
     * <pre>
     * message: {item} is not a well-formed email address.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsEmailMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_Email_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.Length.message' with parameters.
     * <pre>
     * message: Length of {item} must be between {min} and {max}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param min The parameter min for message. (NotNull)
     * @param max The parameter max for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsLengthMessage(String property, String min, String max) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_Length_MESSAGE, min, max));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.LuhnCheck.message' with parameters.
     * <pre>
     * message: The check digit for ${value} is invalid, Luhn Modulo 10 checksum failed.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param value The parameter value for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsLuhnCheckMessage(String property, String value) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_LuhnCheck_MESSAGE, value));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.Mod10Check.message' with parameters.
     * <pre>
     * message: The check digit for ${value} is invalid, Modulo 10 checksum failed.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param value The parameter value for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsMod10CheckMessage(String property, String value) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_Mod10Check_MESSAGE, value));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.Mod11Check.message' with parameters.
     * <pre>
     * message: The check digit for ${value} is invalid, Modulo 11 checksum failed.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param value The parameter value for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsMod11CheckMessage(String property, String value) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_Mod11Check_MESSAGE, value));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.ModCheck.message' with parameters.
     * <pre>
     * message: The check digit for ${value} is invalid, ${modType} checksum failed.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param modType The parameter modType for message. (NotNull)
     * @param value The parameter value for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsModCheckMessage(String property, String modType, String value) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_ModCheck_MESSAGE, modType, value));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.NotBlank.message' with parameters.
     * <pre>
     * message: {item} may not be empty.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsNotBlankMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_NotBlank_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.NotEmpty.message' with parameters.
     * <pre>
     * message: {item} may not be empty.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsNotEmptyMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_NotEmpty_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.ParametersScriptAssert.message' with parameters.
     * <pre>
     * message: script expression "{script}" didn't evaluate to true.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param script The parameter script for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsParametersScriptAssertMessage(String property, String script) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_ParametersScriptAssert_MESSAGE, script));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.Range.message' with parameters.
     * <pre>
     * message: {item} must be between {min} and {max}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param min The parameter min for message. (NotNull)
     * @param max The parameter max for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsRangeMessage(String property, String min, String max) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_Range_MESSAGE, min, max));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.SafeHtml.message' with parameters.
     * <pre>
     * message: {item} may have unsafe html content.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsSafeHtmlMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_SafeHtml_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.ScriptAssert.message' with parameters.
     * <pre>
     * message: script expression "{script}" didn't evaluate to true.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param script The parameter script for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsScriptAssertMessage(String property, String script) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_ScriptAssert_MESSAGE, script));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.URL.message' with parameters.
     * <pre>
     * message: {item} must be a valid URL.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsUrlMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_URL_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.Required.message' with parameters.
     * <pre>
     * message: {item} is required.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsRequiredMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_Required_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.TypeInteger.message' with parameters.
     * <pre>
     * message: {item} should be numeric.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsTypeIntegerMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_TypeInteger_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.TypeLong.message' with parameters.
     * <pre>
     * message: {item} should be numeric.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsTypeLongMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_TypeLong_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.TypeFloat.message' with parameters.
     * <pre>
     * message: {item} should be numeric.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsTypeFloatMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_TypeFloat_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.TypeDouble.message' with parameters.
     * <pre>
     * message: {item} should be numeric.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsTypeDoubleMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_TypeDouble_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.TypeAny.message' with parameters.
     * <pre>
     * message: {item} cannot convert as {propertyType}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param propertyType The parameter propertyType for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsTypeAnyMessage(String property, String propertyType) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_TypeAny_MESSAGE, propertyType));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.UriType.message' with parameters.
     * <pre>
     * message: {item} has wrong URI.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsUriTypeMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_UriType_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'constraints.CronExpression.message' with parameters.
     * <pre>
     * message: {item} is invalid cron expression.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addConstraintsCronExpressionMessage(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(CONSTRAINTS_CronExpression_MESSAGE));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.login.failure' with parameters.
     * <pre>
     * message: Login failed.
     * comment:
     * /- - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     * five framework-embedded messages (don't change key names)
     * - - - - - - - - - -/
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsLoginFailure(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_LOGIN_FAILURE));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.app.illegal.transition' with parameters.
     * <pre>
     * message: Please retry because of illegal transition.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsAppIllegalTransition(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_APP_ILLEGAL_TRANSITION));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.app.db.already.deleted' with parameters.
     * <pre>
     * message: others might be updated, so retry.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsAppDbAlreadyDeleted(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_APP_DB_ALREADY_DELETED));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.app.db.already.updated' with parameters.
     * <pre>
     * message: others might be updated, so retry.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsAppDbAlreadyUpdated(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_APP_DB_ALREADY_UPDATED));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.app.db.already.exists' with parameters.
     * <pre>
     * message: already existing data, so retry.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsAppDbAlreadyExists(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_APP_DB_ALREADY_EXISTS));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.app.double.submit.request' with parameters.
     * <pre>
     * message: Your request might have been processed before this request. Please check and retry it.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsAppDoubleSubmitRequest(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_APP_DOUBLE_SUBMIT_REQUEST));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.login_error' with parameters.
     * <pre>
     * message: Username or Password is not correct.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsLoginError(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_login_error));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.sso_login_error' with parameters.
     * <pre>
     * message: Failed to process SSO login.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsSsoLoginError(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_sso_login_error));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.could_not_find_log_file' with parameters.
     * <pre>
     * message: Could not find {0}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsCouldNotFindLogFile(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_could_not_find_log_file, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_start_crawl_process' with parameters.
     * <pre>
     * message: Failed to start a crawl process.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToStartCrawlProcess(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_start_crawl_process));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.invalid_design_jsp_file_name' with parameters.
     * <pre>
     * message: Invalid JSP file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsInvalidDesignJspFileName(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_invalid_design_jsp_file_name));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.design_jsp_file_does_not_exist' with parameters.
     * <pre>
     * message: JSP file does not exist.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsDesignJspFileDoesNotExist(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_design_jsp_file_does_not_exist));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.design_file_name_is_not_found' with parameters.
     * <pre>
     * message: The file name is not specified.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsDesignFileNameIsNotFound(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_design_file_name_is_not_found));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_write_design_image_file' with parameters.
     * <pre>
     * message: Failed to upload an image file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToWriteDesignImageFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_write_design_image_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_update_jsp_file' with parameters.
     * <pre>
     * message: Failed to update a jsp file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToUpdateJspFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_update_jsp_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.design_file_name_is_invalid' with parameters.
     * <pre>
     * message: The file name is invalid.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsDesignFileNameIsInvalid(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_design_file_name_is_invalid));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.design_file_is_unsupported_type' with parameters.
     * <pre>
     * message: The kind of file is unsupported.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsDesignFileIsUnsupportedType(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_design_file_is_unsupported_type));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_create_crawling_config_at_wizard' with parameters.
     * <pre>
     * message: Failed to create a crawling config.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToCreateCrawlingConfigAtWizard(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_create_crawling_config_at_wizard));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.design_editor_disabled' with parameters.
     * <pre>
     * message: This feature is disabled.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsDesignEditorDisabled(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_design_editor_disabled));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.not_found_on_file_system' with parameters.
     * <pre>
     * message: Not Found: {0}
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsNotFoundOnFileSystem(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_not_found_on_file_system, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.could_not_open_on_system' with parameters.
     * <pre>
     * message: Could not open {0}. &lt;br/&gt;Please check if the file is associated with an application.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsCouldNotOpenOnSystem(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_could_not_open_on_system, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.result_size_exceeded' with parameters.
     * <pre>
     * message: No more results could be displayed.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsResultSizeExceeded(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_result_size_exceeded));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.target_file_does_not_exist' with parameters.
     * <pre>
     * message: {0} file does not exist.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsTargetFileDoesNotExist(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_target_file_does_not_exist, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_delete_file' with parameters.
     * <pre>
     * message: Failed to delete {0} file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToDeleteFile(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_delete_file, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.docid_not_found' with parameters.
     * <pre>
     * message: Not found Doc ID:{0}
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsDocidNotFound(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_docid_not_found, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.document_not_found' with parameters.
     * <pre>
     * message: Not found URL of Doc ID:{0}
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsDocumentNotFound(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_document_not_found, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.not_load_from_server' with parameters.
     * <pre>
     * message: Could not load from this server: {0}
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsNotLoadFromServer(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_not_load_from_server, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_start_job' with parameters.
     * <pre>
     * message: Failed to start job {0}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToStartJob(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_start_job, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_stop_job' with parameters.
     * <pre>
     * message: Failed to stop job {0}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToStopJob(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_stop_job, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_download_synonym_file' with parameters.
     * <pre>
     * message: Failed to download the Synonym file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToDownloadSynonymFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_download_synonym_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_upload_synonym_file' with parameters.
     * <pre>
     * message: Failed to upload the Synonym file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToUploadSynonymFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_upload_synonym_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_download_stemmeroverride_file' with parameters.
     * <pre>
     * message: Failed to download the Stemmer Override file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToDownloadStemmeroverrideFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_download_stemmeroverride_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_upload_stemmeroverride_file' with parameters.
     * <pre>
     * message: Failed to upload the Stemmer Override file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToUploadStemmeroverrideFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_upload_stemmeroverride_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_download_kuromoji_file' with parameters.
     * <pre>
     * message: Failed to download the Kuromoji file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToDownloadKuromojiFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_download_kuromoji_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_upload_kuromoji_file' with parameters.
     * <pre>
     * message: Failed to upload the Kuromoji file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToUploadKuromojiFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_upload_kuromoji_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_download_protwords_file' with parameters.
     * <pre>
     * message: Failed to download the Protwords file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToDownloadProtwordsFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_download_protwords_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_upload_protwords_file' with parameters.
     * <pre>
     * message: Failed to upload the Protwords file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToUploadProtwordsFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_upload_protwords_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_download_stopwords_file' with parameters.
     * <pre>
     * message: Failed to download the Stopwords file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToDownloadStopwordsFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_download_stopwords_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_upload_stopwords_file' with parameters.
     * <pre>
     * message: Failed to upload the Stopwords file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToUploadStopwordsFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_upload_stopwords_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_download_elevate_file' with parameters.
     * <pre>
     * message: Failed to download the Elevate file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToDownloadElevateFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_download_elevate_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_upload_elevate_file' with parameters.
     * <pre>
     * message: Failed to upload the Elevate file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToUploadElevateFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_upload_elevate_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_download_badword_file' with parameters.
     * <pre>
     * message: Failed to download the Badword file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToDownloadBadwordFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_download_badword_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_upload_badword_file' with parameters.
     * <pre>
     * message: Failed to upload the Badword file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToUploadBadwordFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_upload_badword_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_download_mapping_file' with parameters.
     * <pre>
     * message: Failed to download the Mapping file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToDownloadMappingFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_download_mapping_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_upload_mapping_file' with parameters.
     * <pre>
     * message: Failed to upload the Mapping file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToUploadMappingFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_upload_mapping_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.invalid_kuromoji_token' with parameters.
     * <pre>
     * message: {0} is invalid.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsInvalidKuromojiToken(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_invalid_kuromoji_token, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.invalid_kuromoji_segmentation' with parameters.
     * <pre>
     * message: The number of segmentations {0} does not the match number of readings {1}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @param arg1 The parameter arg1 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsInvalidKuromojiSegmentation(String property, String arg0, String arg1) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_invalid_kuromoji_segmentation, arg0, arg1));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.invalid_str_is_included' with parameters.
     * <pre>
     * message: "{1}" in "{0}" is invalid.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @param arg1 The parameter arg1 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsInvalidStrIsIncluded(String property, String arg0, String arg1) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_invalid_str_is_included, arg0, arg1));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.blank_password' with parameters.
     * <pre>
     * message: Password is required.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsBlankPassword(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_blank_password));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.invalid_confirm_password' with parameters.
     * <pre>
     * message: Confirm Password does not match.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsInvalidConfirmPassword(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_invalid_confirm_password));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.cannot_delete_doc_because_of_running' with parameters.
     * <pre>
     * message: Crawler is running. The document cannot be deleted.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsCannotDeleteDocBecauseOfRunning(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_cannot_delete_doc_because_of_running));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_delete_doc_in_admin' with parameters.
     * <pre>
     * message: Failed to delete document.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToDeleteDocInAdmin(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_delete_doc_in_admin));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_send_testmail' with parameters.
     * <pre>
     * message: Failed to send the test mail.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToSendTestmail(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_send_testmail));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.could_not_find_backup_index' with parameters.
     * <pre>
     * message: Could not find index for backup.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsCouldNotFindBackupIndex(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_could_not_find_backup_index));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.no_user_for_changing_password' with parameters.
     * <pre>
     * message: The current password is incorrect.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsNoUserForChangingPassword(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_no_user_for_changing_password));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_change_password' with parameters.
     * <pre>
     * message: Failed to change your password.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToChangePassword(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_change_password));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.unknown_version_for_upgrade' with parameters.
     * <pre>
     * message: Unknown version information.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsUnknownVersionForUpgrade(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_unknown_version_for_upgrade));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_upgrade_from' with parameters.
     * <pre>
     * message: Failed to upgrade from {0}: {1}
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @param arg1 The parameter arg1 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToUpgradeFrom(String property, String arg0, String arg1) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_upgrade_from, arg0, arg1));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_reindex' with parameters.
     * <pre>
     * message: Failed to start reindexing from {0} to {1}
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @param arg1 The parameter arg1 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToReindex(String property, String arg0, String arg1) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_reindex, arg0, arg1));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_read_request_file' with parameters.
     * <pre>
     * message: Failed to read request file: {0}
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToReadRequestFile(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_read_request_file, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.invalid_header_for_request_file' with parameters.
     * <pre>
     * message: Invalid header: {0}
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsInvalidHeaderForRequestFile(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_invalid_header_for_request_file, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.could_not_delete_logged_in_user' with parameters.
     * <pre>
     * message: Could not delete logged in user.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsCouldNotDeleteLoggedInUser(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_could_not_delete_logged_in_user));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.unauthorized_request' with parameters.
     * <pre>
     * message: Unauthorized request.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsUnauthorizedRequest(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_unauthorized_request));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_print_thread_dump' with parameters.
     * <pre>
     * message: Failed to print thread dump.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToPrintThreadDump(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_print_thread_dump));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.file_is_not_supported' with parameters.
     * <pre>
     * message: {0} is not supported.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFileIsNotSupported(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_file_is_not_supported, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.plugin_file_is_not_found' with parameters.
     * <pre>
     * message: {0} is not found.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsPluginFileIsNotFound(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_plugin_file_is_not_found, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_install_plugin' with parameters.
     * <pre>
     * message: Failed to install {0}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToInstallPlugin(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_install_plugin, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_find_plugins' with parameters.
     * <pre>
     * message: Failed to access available plugins.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToFindPlugins(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_find_plugins));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.failed_to_process_sso_request' with parameters.
     * <pre>
     * message: Failed to process the request: {0}
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsFailedToProcessSsoRequest(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_failed_to_process_sso_request, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.invalid_query_unknown' with parameters.
     * <pre>
     * message: The given query has unknown condition.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsInvalidQueryUnknown(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_invalid_query_unknown));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.invalid_query_parse_error' with parameters.
     * <pre>
     * message: The given query is invalid.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsInvalidQueryParseError(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_invalid_query_parse_error));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.invalid_query_sort_value' with parameters.
     * <pre>
     * message: The given sort ({0}) is invalid.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsInvalidQuerySortValue(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_invalid_query_sort_value, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.invalid_query_unsupported_sort_field' with parameters.
     * <pre>
     * message: The given sort ({0}) is not supported.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsInvalidQueryUnsupportedSortField(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_invalid_query_unsupported_sort_field, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.invalid_query_unsupported_sort_order' with parameters.
     * <pre>
     * message: The given sort order ({0}) is not supported.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsInvalidQueryUnsupportedSortOrder(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_invalid_query_unsupported_sort_order, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.invalid_query_cannot_process' with parameters.
     * <pre>
     * message: The given query could not be processed.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsInvalidQueryCannotProcess(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_invalid_query_cannot_process));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.crud_invalid_mode' with parameters.
     * <pre>
     * message: Invalid mode(expected value is {0}, but it's {1}).
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @param arg1 The parameter arg1 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsCrudInvalidMode(String property, String arg0, String arg1) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_crud_invalid_mode, arg0, arg1));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.crud_failed_to_create_instance' with parameters.
     * <pre>
     * message: Failed to create a new data.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsCrudFailedToCreateInstance(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_crud_failed_to_create_instance));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.crud_failed_to_create_crud_table' with parameters.
     * <pre>
     * message: Failed to create a new data. ({0})
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsCrudFailedToCreateCrudTable(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_crud_failed_to_create_crud_table, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.crud_failed_to_update_crud_table' with parameters.
     * <pre>
     * message: Failed to update the data. ({0})
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsCrudFailedToUpdateCrudTable(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_crud_failed_to_update_crud_table, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.crud_failed_to_delete_crud_table' with parameters.
     * <pre>
     * message: Failed to delete the data. ({0})
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsCrudFailedToDeleteCrudTable(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_crud_failed_to_delete_crud_table, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.crud_could_not_find_crud_table' with parameters.
     * <pre>
     * message: Could not find the data({0}).
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsCrudCouldNotFindCrudTable(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_crud_could_not_find_crud_table, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.property_required' with parameters.
     * <pre>
     * message: {0} is required.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsPropertyRequired(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_property_required, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.property_type_integer' with parameters.
     * <pre>
     * message: {0} should be numeric.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsPropertyTypeInteger(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_property_type_integer, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.property_type_long' with parameters.
     * <pre>
     * message: {0} should be numeric.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsPropertyTypeLong(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_property_type_long, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.property_type_float' with parameters.
     * <pre>
     * message: {0} should be numeric.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsPropertyTypeFloat(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_property_type_float, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.property_type_double' with parameters.
     * <pre>
     * message: {0} should be numeric.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsPropertyTypeDouble(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_property_type_double, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.property_type_date' with parameters.
     * <pre>
     * message: {0} should be date.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsPropertyTypeDate(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_property_type_date, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.storage_file_upload_failure' with parameters.
     * <pre>
     * message: Failed to upload {0}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsStorageFileUploadFailure(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_storage_file_upload_failure, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.storage_file_not_found' with parameters.
     * <pre>
     * message: The target file is not found in Storage.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsStorageFileNotFound(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_storage_file_not_found));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.storage_file_download_failure' with parameters.
     * <pre>
     * message: Failed to download {0}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsStorageFileDownloadFailure(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_storage_file_download_failure, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.storage_access_error' with parameters.
     * <pre>
     * message: Storage access error: {0}
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsStorageAccessError(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_storage_access_error, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.storage_no_upload_file' with parameters.
     * <pre>
     * message: Upload file is required.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsStorageNoUploadFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_storage_no_upload_file));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.storage_directory_name_is_invalid' with parameters.
     * <pre>
     * message: Directory name is invalid.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsStorageDirectoryNameIsInvalid(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_storage_directory_name_is_invalid));
        return this;
    }

    /**
     * Add the created action message for the key 'errors.storage_tags_update_failure' with parameters.
     * <pre>
     * message: Failed to update tags for {0}
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addErrorsStorageTagsUpdateFailure(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(ERRORS_storage_tags_update_failure, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'success.update_crawler_params' with parameters.
     * <pre>
     * message: Updated parameters.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessUpdateCrawlerParams(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_update_crawler_params));
        return this;
    }

    /**
     * Add the created action message for the key 'success.delete_doc_from_index' with parameters.
     * <pre>
     * message: Started a process to delete the document from index.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessDeleteDocFromIndex(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_delete_doc_from_index));
        return this;
    }

    /**
     * Add the created action message for the key 'success.crawling_info_delete_all' with parameters.
     * <pre>
     * message: Deleted session data.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessCrawlingInfoDeleteAll(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_crawling_info_delete_all));
        return this;
    }

    /**
     * Add the created action message for the key 'success.start_crawl_process' with parameters.
     * <pre>
     * message: Started a crawl process.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessStartCrawlProcess(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_start_crawl_process));
        return this;
    }

    /**
     * Add the created action message for the key 'success.upload_design_file' with parameters.
     * <pre>
     * message: Uploaded {0}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessUploadDesignFile(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_upload_design_file, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'success.update_design_jsp_file' with parameters.
     * <pre>
     * message: Updated {0}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessUpdateDesignJspFile(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_update_design_jsp_file, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'success.create_crawling_config_at_wizard' with parameters.
     * <pre>
     * message: Created a crawling config ({0}).
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessCreateCrawlingConfigAtWizard(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_create_crawling_config_at_wizard, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'success.failure_url_delete_all' with parameters.
     * <pre>
     * message: Deleted failure urls.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessFailureUrlDeleteAll(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_failure_url_delete_all));
        return this;
    }

    /**
     * Add the created action message for the key 'success.delete_file' with parameters.
     * <pre>
     * message: Deleted {0} file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessDeleteFile(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_delete_file, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'success.job_started' with parameters.
     * <pre>
     * message: Started job {0}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessJobStarted(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_job_started, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'success.job_stopped' with parameters.
     * <pre>
     * message: Stopped job {0}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessJobStopped(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_job_stopped, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'success.upload_synonym_file' with parameters.
     * <pre>
     * message: Uploaded Synonym file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessUploadSynonymFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_upload_synonym_file));
        return this;
    }

    /**
     * Add the created action message for the key 'success.upload_protwords_file' with parameters.
     * <pre>
     * message: Uploaded Protwords file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessUploadProtwordsFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_upload_protwords_file));
        return this;
    }

    /**
     * Add the created action message for the key 'success.upload_stopwords_file' with parameters.
     * <pre>
     * message: Uploaded Stopwords file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessUploadStopwordsFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_upload_stopwords_file));
        return this;
    }

    /**
     * Add the created action message for the key 'success.upload_stemmeroverride_file' with parameters.
     * <pre>
     * message: Uploaded Stemmer Override file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessUploadStemmeroverrideFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_upload_stemmeroverride_file));
        return this;
    }

    /**
     * Add the created action message for the key 'success.upload_kuromoji_file' with parameters.
     * <pre>
     * message: Uploaded Kuromoji file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessUploadKuromojiFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_upload_kuromoji_file));
        return this;
    }

    /**
     * Add the created action message for the key 'success.upload_elevate_word' with parameters.
     * <pre>
     * message: Uploaded Additional Word file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessUploadElevateWord(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_upload_elevate_word));
        return this;
    }

    /**
     * Add the created action message for the key 'success.upload_bad_word' with parameters.
     * <pre>
     * message: Uploaded Bad Word file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessUploadBadWord(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_upload_bad_word));
        return this;
    }

    /**
     * Add the created action message for the key 'success.upload_mapping_file' with parameters.
     * <pre>
     * message: Uploaded Mapping file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessUploadMappingFile(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_upload_mapping_file));
        return this;
    }

    /**
     * Add the created action message for the key 'success.send_testmail' with parameters.
     * <pre>
     * message: Sent the test mail.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessSendTestmail(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_send_testmail));
        return this;
    }

    /**
     * Add the created action message for the key 'success.job_log_delete_all' with parameters.
     * <pre>
     * message: Deleted job logs.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessJobLogDeleteAll(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_job_log_delete_all));
        return this;
    }

    /**
     * Add the created action message for the key 'success.changed_password' with parameters.
     * <pre>
     * message: Changed your password.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessChangedPassword(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_changed_password));
        return this;
    }

    /**
     * Add the created action message for the key 'success.started_data_update' with parameters.
     * <pre>
     * message: Started data update process.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessStartedDataUpdate(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_started_data_update));
        return this;
    }

    /**
     * Add the created action message for the key 'success.reindex_started' with parameters.
     * <pre>
     * message: Started reindexing.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessReindexStarted(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_reindex_started));
        return this;
    }

    /**
     * Add the created action message for the key 'success.bulk_process_started' with parameters.
     * <pre>
     * message: Bulk process is started.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessBulkProcessStarted(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_bulk_process_started));
        return this;
    }

    /**
     * Add the created action message for the key 'success.print_thread_dump' with parameters.
     * <pre>
     * message: Printed thread dump to log file.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessPrintThreadDump(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_print_thread_dump));
        return this;
    }

    /**
     * Add the created action message for the key 'success.install_plugin' with parameters.
     * <pre>
     * message: Installing {0} plugin.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessInstallPlugin(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_install_plugin, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'success.delete_plugin' with parameters.
     * <pre>
     * message: Deleting {0} plugin.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessDeletePlugin(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_delete_plugin, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'success.upload_file_to_storage' with parameters.
     * <pre>
     * message: Uploaded {0}
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessUploadFileToStorage(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_upload_file_to_storage, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'success.sso_logout' with parameters.
     * <pre>
     * message: Logged out.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessSsoLogout(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_sso_logout));
        return this;
    }

    /**
     * Add the created action message for the key 'success.update_storage_tags' with parameters.
     * <pre>
     * message: Updated tags for {0}.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @param arg0 The parameter arg0 for message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessUpdateStorageTags(String property, String arg0) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_update_storage_tags, arg0));
        return this;
    }

    /**
     * Add the created action message for the key 'success.crud_create_crud_table' with parameters.
     * <pre>
     * message: Created data.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessCrudCreateCrudTable(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_crud_create_crud_table));
        return this;
    }

    /**
     * Add the created action message for the key 'success.crud_update_crud_table' with parameters.
     * <pre>
     * message: Updated data.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessCrudUpdateCrudTable(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_crud_update_crud_table));
        return this;
    }

    /**
     * Add the created action message for the key 'success.crud_delete_crud_table' with parameters.
     * <pre>
     * message: Deleted data.
     * </pre>
     * @param property The property name for the message. (NotNull)
     * @return this. (NotNull)
     */
    public FessMessages addSuccessCrudDeleteCrudTable(String property) {
        assertPropertyNotNull(property);
        add(property, new UserMessage(SUCCESS_crud_delete_crud_table));
        return this;
    }
}
