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
package org.codelibs.fess.mylasta.action;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.lastaflute.core.message.UserMessage;

public class FessMessagesTest extends UnitFessTestCase {

    private FessMessages messages;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        messages = new FessMessages();
    }

    // Test constant fields
    public void test_constantFields() {
        // Test error message constants
        assertEquals("{errors.front_header}", FessMessages.ERRORS_front_header);
        assertEquals("{errors.front_footer}", FessMessages.ERRORS_front_footer);
        assertEquals("{errors.front_prefix}", FessMessages.ERRORS_front_prefix);
        assertEquals("{errors.front_suffix}", FessMessages.ERRORS_front_suffix);
        assertEquals("{errors.header}", FessMessages.ERRORS_HEADER);
        assertEquals("{errors.footer}", FessMessages.ERRORS_FOOTER);
        assertEquals("{errors.prefix}", FessMessages.ERRORS_PREFIX);
        assertEquals("{errors.suffix}", FessMessages.ERRORS_SUFFIX);

        // Test constraint message constants
        assertEquals("{constraints.AssertFalse.message}", FessMessages.CONSTRAINTS_AssertFalse_MESSAGE);
        assertEquals("{constraints.AssertTrue.message}", FessMessages.CONSTRAINTS_AssertTrue_MESSAGE);
        assertEquals("{constraints.DecimalMax.message}", FessMessages.CONSTRAINTS_DecimalMax_MESSAGE);
        assertEquals("{constraints.DecimalMin.message}", FessMessages.CONSTRAINTS_DecimalMin_MESSAGE);
        assertEquals("{constraints.Digits.message}", FessMessages.CONSTRAINTS_Digits_MESSAGE);
        assertEquals("{constraints.Future.message}", FessMessages.CONSTRAINTS_Future_MESSAGE);
        assertEquals("{constraints.Max.message}", FessMessages.CONSTRAINTS_Max_MESSAGE);
        assertEquals("{constraints.Min.message}", FessMessages.CONSTRAINTS_Min_MESSAGE);
        assertEquals("{constraints.NotNull.message}", FessMessages.CONSTRAINTS_NotNull_MESSAGE);
        assertEquals("{constraints.Null.message}", FessMessages.CONSTRAINTS_Null_MESSAGE);
        assertEquals("{constraints.Past.message}", FessMessages.CONSTRAINTS_Past_MESSAGE);
        assertEquals("{constraints.Pattern.message}", FessMessages.CONSTRAINTS_Pattern_MESSAGE);
        assertEquals("{constraints.Size.message}", FessMessages.CONSTRAINTS_Size_MESSAGE);
        assertEquals("{constraints.CreditCardNumber.message}", FessMessages.CONSTRAINTS_CreditCardNumber_MESSAGE);
        assertEquals("{constraints.EAN.message}", FessMessages.CONSTRAINTS_EAN_MESSAGE);
        assertEquals("{constraints.Email.message}", FessMessages.CONSTRAINTS_Email_MESSAGE);
        assertEquals("{constraints.Length.message}", FessMessages.CONSTRAINTS_Length_MESSAGE);
        assertEquals("{constraints.NotBlank.message}", FessMessages.CONSTRAINTS_NotBlank_MESSAGE);
        assertEquals("{constraints.NotEmpty.message}", FessMessages.CONSTRAINTS_NotEmpty_MESSAGE);
        assertEquals("{constraints.Range.message}", FessMessages.CONSTRAINTS_Range_MESSAGE);
        assertEquals("{constraints.SafeHtml.message}", FessMessages.CONSTRAINTS_SafeHtml_MESSAGE);
        assertEquals("{constraints.URL.message}", FessMessages.CONSTRAINTS_URL_MESSAGE);
        assertEquals("{constraints.Required.message}", FessMessages.CONSTRAINTS_Required_MESSAGE);
        assertEquals("{constraints.TypeInteger.message}", FessMessages.CONSTRAINTS_TypeInteger_MESSAGE);
        assertEquals("{constraints.TypeLong.message}", FessMessages.CONSTRAINTS_TypeLong_MESSAGE);
        assertEquals("{constraints.TypeFloat.message}", FessMessages.CONSTRAINTS_TypeFloat_MESSAGE);
        assertEquals("{constraints.TypeDouble.message}", FessMessages.CONSTRAINTS_TypeDouble_MESSAGE);
        assertEquals("{constraints.TypeAny.message}", FessMessages.CONSTRAINTS_TypeAny_MESSAGE);
        assertEquals("{constraints.UriType.message}", FessMessages.CONSTRAINTS_UriType_MESSAGE);
        assertEquals("{constraints.CronExpression.message}", FessMessages.CONSTRAINTS_CronExpression_MESSAGE);

        // Test error message constants
        assertEquals("{errors.login.failure}", FessMessages.ERRORS_LOGIN_FAILURE);
        assertEquals("{errors.app.illegal.transition}", FessMessages.ERRORS_APP_ILLEGAL_TRANSITION);
        assertEquals("{errors.app.db.already.deleted}", FessMessages.ERRORS_APP_DB_ALREADY_DELETED);
        assertEquals("{errors.app.db.already.updated}", FessMessages.ERRORS_APP_DB_ALREADY_UPDATED);
        assertEquals("{errors.app.db.already.exists}", FessMessages.ERRORS_APP_DB_ALREADY_EXISTS);
        assertEquals("{errors.app.double.submit.request}", FessMessages.ERRORS_APP_DOUBLE_SUBMIT_REQUEST);
        assertEquals("{errors.login_error}", FessMessages.ERRORS_login_error);
        assertEquals("{errors.sso_login_error}", FessMessages.ERRORS_sso_login_error);

        // Test success message constants
        assertEquals("{success.update_crawler_params}", FessMessages.SUCCESS_update_crawler_params);
        assertEquals("{success.delete_doc_from_index}", FessMessages.SUCCESS_delete_doc_from_index);
        assertEquals("{success.crawling_info_delete_all}", FessMessages.SUCCESS_crawling_info_delete_all);
        assertEquals("{success.start_crawl_process}", FessMessages.SUCCESS_start_crawl_process);
        assertEquals("{success.upload_design_file}", FessMessages.SUCCESS_upload_design_file);
        assertEquals("{success.crud_create_crud_table}", FessMessages.SUCCESS_crud_create_crud_table);
        assertEquals("{success.crud_update_crud_table}", FessMessages.SUCCESS_crud_update_crud_table);
        assertEquals("{success.crud_delete_crud_table}", FessMessages.SUCCESS_crud_delete_crud_table);
    }

    // Test serialization
    public void test_serialVersionUID() {
        assertEquals(1L, getSerialVersionUID());
    }

    private long getSerialVersionUID() {
        try {
            java.lang.reflect.Field field = FessMessages.class.getDeclaredField("serialVersionUID");
            field.setAccessible(true);
            return field.getLong(null);
        } catch (Exception e) {
            fail("Failed to get serialVersionUID: " + e.getMessage());
            return -1;
        }
    }

    // Test error message methods without parameters
    public void test_addErrorsFrontHeader() {
        String property = "testProperty";
        FessMessages result = messages.addErrorsFrontHeader(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsFrontFooter() {
        String property = "testProperty";
        FessMessages result = messages.addErrorsFrontFooter(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsFrontPrefix() {
        String property = "testProperty";
        FessMessages result = messages.addErrorsFrontPrefix(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsFrontSuffix() {
        String property = "testProperty";
        FessMessages result = messages.addErrorsFrontSuffix(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsHeader() {
        String property = "testProperty";
        FessMessages result = messages.addErrorsHeader(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsFooter() {
        String property = "testProperty";
        FessMessages result = messages.addErrorsFooter(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsPrefix() {
        String property = "testProperty";
        FessMessages result = messages.addErrorsPrefix(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsSuffix() {
        String property = "testProperty";
        FessMessages result = messages.addErrorsSuffix(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    // Test constraint message methods without parameters
    public void test_addConstraintsAssertFalseMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsAssertFalseMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsAssertTrueMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsAssertTrueMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsDigitsMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsDigitsMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsFutureMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsFutureMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsNotNullMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsNotNullMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsNullMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsNullMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsPastMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsPastMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    // Test constraint message methods with single parameter
    public void test_addConstraintsDecimalMaxMessage() {
        String property = "testProperty";
        String value = "100";
        FessMessages result = messages.addConstraintsDecimalMaxMessage(property, value);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsDecimalMinMessage() {
        String property = "testProperty";
        String value = "0";
        FessMessages result = messages.addConstraintsDecimalMinMessage(property, value);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsMaxMessage() {
        String property = "testProperty";
        String value = "100";
        FessMessages result = messages.addConstraintsMaxMessage(property, value);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsMinMessage() {
        String property = "testProperty";
        String value = "0";
        FessMessages result = messages.addConstraintsMinMessage(property, value);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsPatternMessage() {
        String property = "testProperty";
        String regexp = "[0-9]+";
        FessMessages result = messages.addConstraintsPatternMessage(property, regexp);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    // Test constraint message methods with multiple parameters
    public void test_addConstraintsSizeMessage() {
        String property = "testProperty";
        String min = "1";
        String max = "10";
        FessMessages result = messages.addConstraintsSizeMessage(property, min, max);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsLengthMessage() {
        String property = "testProperty";
        String min = "5";
        String max = "20";
        FessMessages result = messages.addConstraintsLengthMessage(property, min, max);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsRangeMessage() {
        String property = "testProperty";
        String min = "10";
        String max = "100";
        FessMessages result = messages.addConstraintsRangeMessage(property, min, max);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsModCheckMessage() {
        String property = "testProperty";
        String modType = "Modulo10";
        String value = "12345";
        FessMessages result = messages.addConstraintsModCheckMessage(property, modType, value);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    // Test error message methods
    public void test_addErrorsLoginFailure() {
        String property = "testProperty";
        FessMessages result = messages.addErrorsLoginFailure(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsAppIllegalTransition() {
        String property = "testProperty";
        FessMessages result = messages.addErrorsAppIllegalTransition(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsAppDbAlreadyDeleted() {
        String property = "testProperty";
        FessMessages result = messages.addErrorsAppDbAlreadyDeleted(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsAppDbAlreadyUpdated() {
        String property = "testProperty";
        FessMessages result = messages.addErrorsAppDbAlreadyUpdated(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsAppDbAlreadyExists() {
        String property = "testProperty";
        FessMessages result = messages.addErrorsAppDbAlreadyExists(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsAppDoubleSubmitRequest() {
        String property = "testProperty";
        FessMessages result = messages.addErrorsAppDoubleSubmitRequest(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    // Test error message methods with parameters
    public void test_addErrorsCouldNotFindLogFile() {
        String property = "testProperty";
        String arg0 = "test.log";
        FessMessages result = messages.addErrorsCouldNotFindLogFile(property, arg0);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsNotFoundOnFileSystem() {
        String property = "testProperty";
        String arg0 = "file.txt";
        FessMessages result = messages.addErrorsNotFoundOnFileSystem(property, arg0);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsCouldNotOpenOnSystem() {
        String property = "testProperty";
        String arg0 = "document.pdf";
        FessMessages result = messages.addErrorsCouldNotOpenOnSystem(property, arg0);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsTargetFileDoesNotExist() {
        String property = "testProperty";
        String arg0 = "target.txt";
        FessMessages result = messages.addErrorsTargetFileDoesNotExist(property, arg0);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsFailedToDeleteFile() {
        String property = "testProperty";
        String arg0 = "delete.txt";
        FessMessages result = messages.addErrorsFailedToDeleteFile(property, arg0);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsInvalidKuromojiSegmentation() {
        String property = "testProperty";
        String arg0 = "token1";
        String arg1 = "token2";
        FessMessages result = messages.addErrorsInvalidKuromojiSegmentation(property, arg0, arg1);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addErrorsInvalidStrIsIncluded() {
        String property = "testProperty";
        String arg0 = "field";
        String arg1 = "value";
        FessMessages result = messages.addErrorsInvalidStrIsIncluded(property, arg0, arg1);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    // Test success message methods
    public void test_addSuccessUpdateCrawlerParams() {
        String property = "testProperty";
        FessMessages result = messages.addSuccessUpdateCrawlerParams(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addSuccessDeleteDocFromIndex() {
        String property = "testProperty";
        FessMessages result = messages.addSuccessDeleteDocFromIndex(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addSuccessCrawlingInfoDeleteAll() {
        String property = "testProperty";
        FessMessages result = messages.addSuccessCrawlingInfoDeleteAll(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addSuccessStartCrawlProcess() {
        String property = "testProperty";
        FessMessages result = messages.addSuccessStartCrawlProcess(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    // Test success message methods with parameters
    public void test_addSuccessUploadDesignFile() {
        String property = "testProperty";
        String arg0 = "design.css";
        FessMessages result = messages.addSuccessUploadDesignFile(property, arg0);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addSuccessUpdateDesignJspFile() {
        String property = "testProperty";
        String arg0 = "index.jsp";
        FessMessages result = messages.addSuccessUpdateDesignJspFile(property, arg0);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addSuccessJobStarted() {
        String property = "testProperty";
        String arg0 = "crawler";
        FessMessages result = messages.addSuccessJobStarted(property, arg0);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addSuccessJobStopped() {
        String property = "testProperty";
        String arg0 = "crawler";
        FessMessages result = messages.addSuccessJobStopped(property, arg0);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addSuccessInstallPlugin() {
        String property = "testProperty";
        String arg0 = "plugin-name";
        FessMessages result = messages.addSuccessInstallPlugin(property, arg0);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addSuccessDeletePlugin() {
        String property = "testProperty";
        String arg0 = "plugin-name";
        FessMessages result = messages.addSuccessDeletePlugin(property, arg0);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    // Test null property handling
    public void test_addErrorsFrontHeader_nullProperty() {
        try {
            messages.addErrorsFrontHeader(null);
            fail("Should throw exception for null property");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void test_addConstraintsDecimalMaxMessage_nullProperty() {
        try {
            messages.addConstraintsDecimalMaxMessage(null, "100");
            fail("Should throw exception for null property");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void test_addConstraintsSizeMessage_nullProperty() {
        try {
            messages.addConstraintsSizeMessage(null, "1", "10");
            fail("Should throw exception for null property");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    // Test inheritance
    public void test_inheritance() {
        assertTrue(messages instanceof FessLabels);
        assertTrue(messages instanceof org.lastaflute.core.message.UserMessages);
    }

    // Test method chaining
    public void test_methodChaining() {
        String property1 = "prop1";
        String property2 = "prop2";
        String property3 = "prop3";

        FessMessages result =
                messages.addErrorsFrontHeader(property1).addConstraintsNotNullMessage(property2).addSuccessStartCrawlProcess(property3);

        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property1));
        assertTrue(messages.hasMessageOf(property2));
        assertTrue(messages.hasMessageOf(property3));
    }

    // Test CRUD success messages
    public void test_addSuccessCrudCreateCrudTable() {
        String property = "testProperty";
        FessMessages result = messages.addSuccessCrudCreateCrudTable(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addSuccessCrudUpdateCrudTable() {
        String property = "testProperty";
        FessMessages result = messages.addSuccessCrudUpdateCrudTable(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addSuccessCrudDeleteCrudTable() {
        String property = "testProperty";
        FessMessages result = messages.addSuccessCrudDeleteCrudTable(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    // Test special message types
    public void test_addConstraintsCreditCardNumberMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsCreditCardNumberMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsEmailMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsEmailMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsUrlMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsUrlMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsCronExpressionMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsCronExpressionMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    // Test type validation messages
    public void test_addConstraintsTypeIntegerMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsTypeIntegerMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsTypeLongMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsTypeLongMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsTypeFloatMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsTypeFloatMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsTypeDoubleMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsTypeDoubleMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsTypeAnyMessage() {
        String property = "testProperty";
        String propertyType = "Date";
        FessMessages result = messages.addConstraintsTypeAnyMessage(property, propertyType);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    // Test required/not empty messages
    public void test_addConstraintsRequiredMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsRequiredMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsNotBlankMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsNotBlankMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }

    public void test_addConstraintsNotEmptyMessage() {
        String property = "testProperty";
        FessMessages result = messages.addConstraintsNotEmptyMessage(property);
        assertNotNull(result);
        assertSame(messages, result);
        assertTrue(messages.hasMessageOf(property));
    }
}