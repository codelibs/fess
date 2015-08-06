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

package org.codelibs.fess.validator;

import javax.servlet.http.HttpServletRequest;

import org.codelibs.core.lang.StringUtil;
import org.lastaflute.web.ruts.message.ActionMessages;
import org.quartz.CronExpression;

//TODO replace with hibernate validator
public class CronExpressionChecks /*extends S2FieldChecks*/{

    private static final long serialVersionUID = 1L;
    /*
        public static boolean validateCronExpression(final Object bean, final ValidatorAction validatorAction, final Field field,
                final ActionMessages errors, final Validator validator, final HttpServletRequest request) {
            final String value = getValueAsString(bean, field);
            if (StringUtil.isNotBlank(value) && !CronExpression.isValidExpression(value)) {
                addError(errors, field, validator, validatorAction, request);
                return false;
            }
            return true;
        }
    */
}
