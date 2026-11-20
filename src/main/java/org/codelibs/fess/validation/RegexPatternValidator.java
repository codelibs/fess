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

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.WebFsIndexHelper;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the RegexPattern constraint.
 * See {@link RegexPattern.Syntax} for how the value is read.
 */
public class RegexPatternValidator implements ConstraintValidator<RegexPattern, String> {

    /**
     * Default constructor.
     */
    public RegexPatternValidator() {
        // Empty constructor
    }

    private RegexPattern.Syntax syntax = RegexPattern.Syntax.CONFIG_PATH;

    @Override
    public void initialize(final RegexPattern constraintAnnotation) {
        syntax = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return determineValid(value);
    }

    /**
     * Determines if the given value holds only valid regular expressions.
     * @param value the value to validate
     * @return true if valid or blank, false otherwise
     */
    protected boolean determineValid(final String value) {
        if (StringUtil.isBlank(value)) {
            return true;
        }
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        return switch (syntax) {
        case PLAIN -> compiles(value);
        case CONFIG_PATH -> configPathsCompile(value, systemHelper, false);
        case FILE_DOC_PATH -> configPathsCompile(value, systemHelper, true);
        case LABEL_PATH -> compiles(joinLabelPaths(value, systemHelper));
        case CRAWL_FILTER -> WebFsIndexHelper.toFilterPatterns(value, systemHelper).stream().allMatch(RegexPatternValidator::compiles);
        };
    }

    private static boolean configPathsCompile(final String value, final SystemHelper systemHelper, final boolean encode) {
        for (final String line : value.split("[\r\n]")) {
            final String pattern = systemHelper.normalizeConfigPath(line);
            if (StringUtil.isNotBlank(pattern) && !compiles(encode ? systemHelper.encodeUrlFilter(pattern) : pattern)) {
                return false;
            }
        }
        return true;
    }

    /** Builds the expression that the label type patterns compile from the paths of a label type. */
    private static String joinLabelPaths(final String value, final SystemHelper systemHelper) {
        final StringBuilder buf = new StringBuilder(100);
        boolean first = true;
        for (final String path : value.split("\n")) {
            if (first) {
                first = false;
            } else {
                buf.append('|');
            }
            final String normalized = systemHelper.normalizeConfigPath(path);
            if (StringUtil.isNotBlank(normalized)) {
                buf.append(normalized);
            }
        }
        return buf.toString();
    }

    private static boolean compiles(final String pattern) {
        try {
            Pattern.compile(pattern);
            return true;
        } catch (final PatternSyntaxException e) {
            return false;
        }
    }

}
