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
package org.codelibs.fess.app.web.api.admin;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.app.pager.RolePager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

public class BaseSearchBodyTest extends UnitFessTestCase {

    private BaseSearchBody newBody(final int resultWindow) {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getPagingPageSizeAsInteger() {
                return 25;
            }

            @Override
            public Integer getIndexerMaxResultWindowSizeAsInteger() {
                return resultWindow;
            }
        });
        return new BaseSearchBody();
    }

    @Test
    public void test_defaults() {
        final BaseSearchBody body = newBody(10000);
        assertEquals(25, body.getPageSize());
        assertEquals(1, body.getCurrentPageNumber());
    }

    @Test
    public void test_valuesInsideTheWindowAreKept() {
        final BaseSearchBody body = newBody(10000);
        body.size = 10000;
        body.page = 1;
        assertEquals(10000, body.getPageSize());
        assertEquals(1, body.getCurrentPageNumber());

        body.size = 100;
        body.page = 100;
        assertEquals(100, body.getPageSize());
        assertEquals(100, body.getCurrentPageNumber());
    }

    @Test
    public void test_sizeBeyondTheWindowIsCut() {
        final BaseSearchBody body = newBody(10000);
        body.size = 10001;
        assertEquals(10000, body.getPageSize());
        body.size = Integer.MAX_VALUE;
        assertEquals(10000, body.getPageSize());
    }

    @Test
    public void test_pageBeyondTheWindowIsReplacedByTheLastPageInside() {
        final BaseSearchBody body = newBody(10000);
        body.size = 10;
        body.page = 1000;
        assertEquals(1000, body.getCurrentPageNumber());
        body.page = 1001;
        assertEquals(1000, body.getCurrentPageNumber());
        body.page = Integer.MAX_VALUE;
        assertEquals(1000, body.getCurrentPageNumber());

        // a page of the whole window leaves room for page 1 only
        body.size = 10000;
        body.page = 2;
        assertEquals(1, body.getCurrentPageNumber());
    }

    @Test
    public void test_theWindowComesFromTheConfiguration() {
        final BaseSearchBody body = newBody(100);
        body.size = 60;
        assertEquals(60, body.getPageSize());
        body.size = 101;
        assertEquals(100, body.getPageSize());
        body.size = 40;
        body.page = 3;
        assertEquals(2, body.getCurrentPageNumber());
    }

    @Test
    public void test_zeroAndNegativeValuesStayForThePagerDefaults() {
        final BaseSearchBody body = newBody(10000);
        body.size = 0;
        body.page = 0;
        assertEquals(0, body.getPageSize());
        assertEquals(0, body.getCurrentPageNumber());
        body.size = -5;
        body.page = -5;
        assertEquals(-5, body.getPageSize());
        assertEquals(-5, body.getCurrentPageNumber());

        // with a size the pager replaces, the page is still bounded by the configured paging size
        body.size = 0;
        body.page = 100000;
        assertEquals(400, body.getCurrentPageNumber());
    }

    @Test
    public void test_nullValuesFallBackToTheDefaults() {
        final BaseSearchBody body = newBody(10000);
        body.size = null;
        body.page = null;
        assertEquals(25, body.getPageSize());
        assertEquals(1, body.getCurrentPageNumber());
    }

    @Test
    public void test_thePagerTakesTheBoundedValues() {
        // the list actions build their pager with BeanUtil.copyBeanToNewBean(body, XxxPager.class)
        final BaseSearchBody body = newBody(10000);
        body.size = 10001;
        body.page = 100000;
        final RolePager pager = BeanUtil.copyBeanToNewBean(body, RolePager.class);
        assertEquals(10000, pager.getPageSize());
        assertEquals(1, pager.getCurrentPageNumber());

        body.size = 10;
        pager.setPageSize(0);
        final RolePager pager2 = BeanUtil.copyBeanToNewBean(body, RolePager.class);
        assertEquals(10, pager2.getPageSize());
        assertEquals(1000, pager2.getCurrentPageNumber());
    }
}
