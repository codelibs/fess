/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.unit;

import java.util.Date;

import javax.annotation.Resource;

import org.dbflute.saflute.core.direction.FwAssistantDirector;
import org.dbflute.saflute.core.direction.OptionalCoreDirection;
import org.dbflute.saflute.core.magic.ThreadCacheContext;
import org.dbflute.saflute.core.magic.TransactionTimeContext;
import org.dbflute.saflute.core.time.TimeManager;
import org.dbflute.utflute.seasar.ContainerTestCase;

/**
 * Use like this:
 * <pre>
 * YourTest extends {@link UnitFessContainerTestCase} {
 * 
 *     public void test_yourMethod() {
 *         <span style="color: #3F7E5E">// ## Arrange ##</span>
 *         YourAction action = new YourAction();
 *         <span style="color: #FD4747">inject</span>(action);
 * 
 *         <span style="color: #3F7E5E">// ## Act ##</span>
 *         action.submit();
 * 
 *         <span style="color: #3F7E5E">// ## Assert ##</span>
 *         assertTrue(action...);
 *     }
 * }
 * </pre>
 * @author jflute
 */
public abstract class UnitFessContainerTestCase extends ContainerTestCase {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    protected FwAssistantDirector assistantDirector;

    @Resource
    protected TimeManager timeManager;

    // ===================================================================================
    //                                                                            Settings
    //                                                                            ========
    @Override
    public void setUp() throws Exception {
        super.setUp();
        initializeThreadCacheContext();
        initializeTransactionTime();
        initializeAssistantDirector();
    }

    protected void initializeThreadCacheContext() {
        ThreadCacheContext.initialize();
    }

    protected void initializeTransactionTime() {
        // because of non-UserTransaction transaction in UTFlute
        final Date transactionTime = timeManager.getFlashDate();
        TransactionTimeContext.setTransactionTime(transactionTime);
    }

    protected void initializeAssistantDirector() {
        OptionalCoreDirection direction = assistantDirector.assistOptionalCoreDirection();
        direction.assistBootProcessCallback().callback(assistantDirector);
    }

    @Override
    public void tearDown() throws Exception {
        TransactionTimeContext.clear();
        ThreadCacheContext.clear();
        super.tearDown();
    }
}
