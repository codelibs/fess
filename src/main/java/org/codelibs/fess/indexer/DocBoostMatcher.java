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
package org.codelibs.fess.indexer;

import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.opensearch.config.exentity.BoostDocumentRule;
import org.codelibs.fess.util.ComponentUtil;

/**
 * A matcher class for applying document boost values based on configurable expressions.
 * This class evaluates match and boost expressions against document data to determine
 * if a document should receive a boost and what boost value to apply. It supports
 * script-based expressions for flexible document scoring.
 *
 */
public class DocBoostMatcher {

    /** The expression used to calculate the boost value (defaults to "0") */
    private String boostExpression = "0";

    /** The expression used to match documents for boosting */
    private String matchExpression;

    /** The script engine type used for expression evaluation */
    private final String scriptType;

    /**
     * Default constructor that creates a DocBoostMatcher with default script type.
     * Uses the default script engine as defined in Constants.DEFAULT_SCRIPT.
     */
    public DocBoostMatcher() {
        scriptType = Constants.DEFAULT_SCRIPT;
    }

    /**
     * Constructor that creates a DocBoostMatcher from a BoostDocumentRule.
     *
     * @param rule the boost document rule containing match and boost expressions
     */
    public DocBoostMatcher(final BoostDocumentRule rule) {
        matchExpression = rule.getUrlExpr();
        boostExpression = rule.getBoostExpr();
        scriptType = ComponentUtil.getFessConfig().getCrawlerDefaultScript();
    }

    /**
     * Determines if the given document data matches the configured match expression.
     *
     * @param map the document data as a map of field names to values
     * @return true if the document matches the expression, false otherwise
     */
    public boolean match(final Map<String, Object> map) {

        if (map == null || map.isEmpty() || matchExpression == null) {
            return false;
        }

        final Object value = ComponentUtil.getScriptEngineFactory().getScriptEngine(scriptType).evaluate(matchExpression, map);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        return false;
    }

    /**
     * Calculates the boost value for the given document data using the boost expression.
     * The method evaluates the boost expression and converts the result to a float value.
     * Supports Integer, Long, Float, Double, and String representations of numbers.
     *
     * @param map the document data as a map of field names to values
     * @return the calculated boost value as a float, or 0.0f if evaluation fails
     */
    public float getValue(final Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return 0.0f;
        }

        final Object value = ComponentUtil.getScriptEngineFactory().getScriptEngine(scriptType).evaluate(boostExpression, map);
        if (value instanceof Integer) {
            return ((Integer) value).floatValue();
        }
        if (value instanceof Long) {
            return ((Long) value).floatValue();
        }
        if (value instanceof Float) {
            return (Float) value;
        }
        if (value instanceof Double) {
            return ((Double) value).floatValue();
        }
        if (value != null) {
            return Float.parseFloat(value.toString());
        }

        return 0.0f;
    }

    /**
     * Gets the current boost expression.
     *
     * @return the boost expression string
     */
    public String getBoostExpression() {
        return boostExpression;
    }

    /**
     * Sets the boost expression used to calculate boost values.
     *
     * @param expression the boost expression string
     */
    public void setBoostExpression(final String expression) {
        boostExpression = expression;
    }

    /**
     * Gets the current match expression.
     *
     * @return the match expression string
     */
    public String getMatchExpression() {
        return matchExpression;
    }

    /**
     * Sets the match expression used to determine if documents should be boosted.
     *
     * @param expression the match expression string
     */
    public void setMatchExpression(final String expression) {
        matchExpression = expression;
    }

}
