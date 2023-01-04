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
package org.codelibs.fess.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.codelibs.core.lang.StringUtil;
import org.lastaflute.job.util.LaCronUtil;

public class CronExpressionValidator implements ConstraintValidator<CronExpression, String> {

    @Override
    public void initialize(final CronExpression constraintAnnotation) {
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return determineValid(value);
    }

    protected boolean determineValid(final String value) {
        if (StringUtil.isNotBlank(value) && !LaCronUtil.isCronExpValid(value)) {
            return false;
        }
        return true;
    }

}
