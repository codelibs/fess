/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import org.codelibs.fess.es.config.exentity.BoostDocumentRule;
import org.codelibs.fess.util.GroovyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocBoostMatcher {
    private static final Logger logger = LoggerFactory.getLogger(DocBoostMatcher.class);

    private String boostExpression = "0";

    private String matchExpression;

    public DocBoostMatcher() {
        // nothing
    }

    public DocBoostMatcher(final BoostDocumentRule rule) {
        matchExpression = rule.getUrlExpr();
        boostExpression = rule.getBoostExpr();
    }

    public boolean match(final Map<String, Object> map) {

        if (map == null || map.isEmpty() || matchExpression == null) {
            return false;
        }

        final Object value = GroovyUtil.evaluate(matchExpression, map);
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }

        return false;
    }

    public float getValue(final Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return 0.0f;
        }

        final Object value = GroovyUtil.evaluate(boostExpression, map);
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
