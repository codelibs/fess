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

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Validation constraint to ensure that a string holds valid regular expressions.
 * The value is read the way its consumer reads it, see {@link Syntax}.
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = RegexPatternValidator.class)
public @interface RegexPattern {

    /**
     * How the value is read.
     * @return the syntax of the value
     */
    Syntax value() default Syntax.CONFIG_PATH;

    /**
     * The ways a configured pattern is read before it is compiled.
     */
    enum Syntax {
        /**
         * A list of crawl filters, one per line, as {@code WebFsIndexHelper#toFilterPatterns} reads the included
         * and excluded URLs and paths of a crawl configuration.
         */
        CRAWL_FILTER,
        /**
         * A list of configured paths, one per line, as the document filters of a web configuration read them:
         * blank and comment lines are skipped and the prefixes of the Google Search Appliance style
         * ({@code regexp:}, {@code contains:}) are resolved before a line is compiled.
         */
        CONFIG_PATH,
        /**
         * As {@link #CONFIG_PATH}, and the resolved line is then URL-encoded, as the document filters of a file
         * configuration read them.
         */
        FILE_DOC_PATH,
        /**
         * The paths of a label type: each line is resolved as in {@link #CONFIG_PATH} and the lines are joined
         * with {@code |} into the one expression that is compiled.
         */
        LABEL_PATH,
        /**
         * A single regular expression that is compiled as it is.
         */
        PLAIN
    }

    /**
     * The error message when validation fails.
     * @return the error message
     */
    String message() default "{org.lastaflute.validator.constraints.RegexPattern.message}";

    /**
     * The validation groups this constraint belongs to.
     * @return the groups
     */
    Class<?>[] groups() default {};

    /**
     * The payload associated with this constraint.
     * @return the payload
     */
    Class<? extends Payload>[] payload() default {};

}
