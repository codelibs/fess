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
package org.codelibs.fess.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.opensearch.config.cbean.LabelTypeCB;
import org.codelibs.fess.opensearch.config.exbhv.LabelTypeBhv;
import org.codelibs.fess.opensearch.config.exentity.LabelType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.junit.jupiter.api.Test;

public class LabelTypeServiceTest extends UnitFessTestCase {

    /** The behavior that holds the stored label types and only answers a cursor, never a page. */
    private static class StubBhv extends LabelTypeBhv {
        final List<LabelType> rows = new ArrayList<>();

        final AtomicReference<LabelTypeCB> cursorCb = new AtomicReference<>();

        @Override
        public void selectCursor(final CBCall<LabelTypeCB> cbLambda, final EntityRowHandler<LabelType> entityLambda) {
            final LabelTypeCB cb = new LabelTypeCB();
            cbLambda.callback(cb);
            cursorCb.set(cb);
            for (final LabelType row : rows) {
                entityLambda.handle(row);
            }
        }
    }

    @Test
    public void test_getLabelTypeList_returnsEveryLabelType() {
        final StubBhv bhv = new StubBhv();
        // more than the 1,000 that one page holds (page.labeltype.max.fetch.size)
        for (int i = 0; i < 1500; i++) {
            final LabelType labelType = new LabelType();
            labelType.setName("label-" + i);
            labelType.setValue("v" + i);
            bhv.rows.add(labelType);
        }
        final LabelTypeService service = new LabelTypeService();
        service.labelTypeBhv = bhv;

        final List<LabelType> list = service.getLabelTypeList();

        assertEquals(1500, list.size());
        assertEquals("label-0", list.get(0).getName());
        assertEquals("label-1499", list.get(1499).getName());
        assertNotNull(bhv.cursorCb.get());
    }

    @Test
    public void test_getLabelTypeList_noLabelType() {
        final LabelTypeService service = new LabelTypeService();
        service.labelTypeBhv = new StubBhv();

        assertTrue(service.getLabelTypeList().isEmpty());
    }
}
