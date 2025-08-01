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
package org.codelibs.fess.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.codelibs.fess.validation.UriTypeValidator.ProtocolType;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Validation annotation for URI type constraints.
 * This annotation validates URI strings based on specified protocol types.
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = UriTypeValidator.class)
public @interface UriType {

    /**
     * Specifies the protocol type to validate against.
     *
     * @return the protocol type
     */
    ProtocolType protocolType();

    /**
     * The validation error message.
     *
     * @return the error message
     */
    String message() default "{org.lastaflute.validator.constraints.UriType.message}";

    /**
     * The validation groups.
     *
     * @return the validation groups
     */
    Class<?>[] groups() default {};

    /**
     * The payload for constraint.
     *
     * @return the payload
     */
    Class<? extends Payload>[] payload() default {};

}
