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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.List;
import java.util.Locale;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.mail.send.SMailDeliveryDepartment;
import org.dbflute.mail.send.SMailPostalMotorbike;
import org.dbflute.mail.send.SMailPostalParkingLot;
import org.dbflute.mail.send.SMailPostalPersonnel;
import org.dbflute.mail.send.embedded.personnel.SMailDogmaticPostalPersonnel;
import org.dbflute.mail.send.embedded.receptionist.SMailConventionReceptionist;
import org.dbflute.mail.send.supplement.async.SMailAsyncStrategy;
import org.dbflute.mail.send.supplement.filter.SMailSubjectFilter;
import org.dbflute.mail.send.supplement.label.SMailLabelStrategy;
import org.dbflute.optional.OptionalThing;
import org.dbflute.util.DfStringUtil;
import org.lastaflute.core.magic.async.AsyncManager;
import org.lastaflute.core.magic.async.ConcurrentAsyncCall;
import org.lastaflute.core.message.MessageManager;
import org.lastaflute.core.util.ContainerUtil;

/**
 * @author jflute
 */
public class FessMailDeliveryDepartmentCreator {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final FessConfig fessConfig;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public FessMailDeliveryDepartmentCreator(final FessConfig fessConfig) {
        this.fessConfig = fessConfig;
    }

    // ===================================================================================
    //                                                                              Create
    //                                                                              ======
    public SMailDeliveryDepartment create() {
        return new SMailDeliveryDepartment(createPostalParkingLot(), createPostalPersonnel());
    }

    // -----------------------------------------------------
    //                                    Postal Parking Lot
    //                                    ------------------
    protected SMailPostalParkingLot createPostalParkingLot() {
        final SMailPostalParkingLot parkingLot = new SMailPostalParkingLot();
        final SMailPostalMotorbike motorbike = new SMailPostalMotorbike();
        final String hostAndPort = fessConfig.getMailSmtpServerMainHostAndPort();
        final List<String> hostPortList = DfStringUtil.splitListTrimmed(hostAndPort, ":");
        motorbike.registerConnectionInfo(hostPortList.get(0), Integer.parseInt(hostPortList.get(1)));
        motorbike.registerReturnPath(fessConfig.getMailReturnPath());
        parkingLot.registerMotorbikeAsMain(motorbike);
        return parkingLot;
    }

    // -----------------------------------------------------
    //                                      Postal Personnel
    //                                      ----------------
    protected SMailPostalPersonnel createPostalPersonnel() {
        final SMailDogmaticPostalPersonnel personnel = createDogmaticPostalPersonnel();
        return fessConfig.isMailSendMock() ? personnel.asTraining() : personnel;
    }

    protected SMailDogmaticPostalPersonnel createDogmaticPostalPersonnel() { // #ext_point e.g. locale, database
        final AsyncManager asyncManager = getAsyncManager();
        final MessageManager messageManager = getMessageManager();
        return new SMailDogmaticPostalPersonnel() {

            // *if you need user locale switching or templates from database,
            // override createConventionReceptionist() (see the method for the details)
            @Override
            protected SMailConventionReceptionist createConventionReceptionist() {
                return super.createConventionReceptionist().asReceiverLocale(postcard -> OptionalThing.empty());
            }

            @Override
            protected OptionalThing<SMailSubjectFilter> createSubjectFilter() {
                return OptionalThing.of((view, subject) -> subject);
            }

            @Override
            protected OptionalThing<SMailAsyncStrategy> createAsyncStrategy() {
                return OptionalThing.of((view, runnable) -> async(asyncManager, runnable));
            }

            @Override
            protected OptionalThing<SMailLabelStrategy> createLabelStrategy() {
                return OptionalThing.of((view, locale, label) -> resolveLabelIfNeeds(messageManager, locale, label));
            }
        };
    }

    // ===================================================================================
    //                                                                        Asynchronous
    //                                                                        ============
    protected void async(final AsyncManager asyncManager, final Runnable runnable) {
        asyncManager.async(new ConcurrentAsyncCall() {
            @Override
            public void callback() {
                runnable.run();
            }

            @Override
            public boolean asPrimary() {
                return true; // mail is primary business
            }
        });
    }

    // ===================================================================================
    //                                                                       Resolve Label
    //                                                                       =============
    protected String resolveLabelIfNeeds(final MessageManager messageManager, final Locale locale, final String label) {
        return label.startsWith("labels.") ? messageManager.getMessage(locale, label) : label;
    }

    // ===================================================================================
    //                                                                           Component
    //                                                                           =========
    protected MessageManager getMessageManager() {
        return ContainerUtil.getComponent(MessageManager.class);
    }

    protected AsyncManager getAsyncManager() {
        return ContainerUtil.getComponent(AsyncManager.class);
    }
}
