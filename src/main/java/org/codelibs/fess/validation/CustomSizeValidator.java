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
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

public class CustomSizeValidator implements ConstraintValidator<CustomSize, CharSequence> {
    private int min = 0;
    private int max = Integer.MAX_VALUE;
    private String message;

    @Override
    public void initialize(final CustomSize constraintAnnotation) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String minKey = constraintAnnotation.minKey();
        if (StringUtil.isNotBlank(minKey)) {
            min = Integer.parseInt(fessConfig.get(minKey));
        }
        final String maxKey = constraintAnnotation.maxKey();
        if (StringUtil.isNotBlank(maxKey)) {
            max = Integer.parseInt(fessConfig.get(maxKey));
        }
        message = constraintAnnotation.message();
        validateParameters();
    }

    @Override
    public boolean isValid(final CharSequence value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        final HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
        hibernateContext.disableDefaultConstraintViolation();
        hibernateContext.addMessageParameter("min", min).addMessageParameter("max", max).buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
        final int length = value.length();
        return length >= min && length <= max;
    }

    private void validateParameters() {
        if (min < 0) {
            throw new IllegalArgumentException("The min parameter cannot be negative.");
        }
        if (max < 0) {
            throw new IllegalArgumentException("The max parameter cannot be negative.");
        }
        if (max < min) {
            throw new IllegalArgumentException("The length cannot be negative.");
        }
    }
}
