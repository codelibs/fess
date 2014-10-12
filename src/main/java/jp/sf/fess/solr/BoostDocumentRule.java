/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.solr;

import java.util.Map;

import org.seasar.framework.util.OgnlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoostDocumentRule {
    private static final Logger logger = LoggerFactory
            .getLogger(BoostDocumentRule.class);

    private String boostExpression = "0";

    private String matchExpression;

    public BoostDocumentRule() {
        // nothing
    }

    public BoostDocumentRule(final jp.sf.fess.db.exentity.BoostDocumentRule rule) {
        matchExpression = rule.getUrlExpr();
        boostExpression = rule.getBoostExpr();
    }

    public boolean match(final Map<String, Object> map) {

        if (map == null || map.isEmpty() || matchExpression == null) {
            return false;
        }

        try {
            final Object exp = OgnlUtil.parseExpression(matchExpression);
            final Object value = OgnlUtil.getValue(exp, map);

            if (value instanceof Boolean) {
                return ((Boolean) value).booleanValue();
            }
        } catch (final Exception e) {
            logger.warn("Failed to parse a doc for boost: " + map, e);
        }

        return false;
    }

    public float getValue(final Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return 0.0f;
        }

        try {
            final Object exp = OgnlUtil.parseExpression(boostExpression);
            final Object value = OgnlUtil.getValue(exp, map);

            if (value instanceof Integer) {
                return ((Integer) value).floatValue();
            } else if (value instanceof Long) {
                return ((Long) value).floatValue();
            } else if (value instanceof Float) {
                return ((Float) value).floatValue();
            } else if (value instanceof Double) {
                return ((Double) value).floatValue();
            } else if (value != null) {
                return Float.parseFloat(value.toString());
            }
        } catch (final Exception e) {
            logger.warn("Failed to parse a doc for boost: " + map, e);
        }

        return 0.0f;
    }

    public String getBoostExpression() {
        return boostExpression;
    }

    public void setBoostExpression(final String expression) {
        boostExpression = expression;
    }

    public String getMatchExpression() {
        return matchExpression;
    }

    public void setMatchExpression(final String expression) {
        matchExpression = expression;
    }

}
