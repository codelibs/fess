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
package org.codelibs.fess.mylasta.direction;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.mylasta.direction.sponsor.FessActionAdjustmentProvider;
import org.codelibs.fess.mylasta.direction.sponsor.FessApiFailureHook;
import org.codelibs.fess.mylasta.direction.sponsor.FessCookieResourceProvider;
import org.codelibs.fess.mylasta.direction.sponsor.FessCurtainBeforeHook;
import org.codelibs.fess.mylasta.direction.sponsor.FessCurtainFinallyHook;
import org.codelibs.fess.mylasta.direction.sponsor.FessJsonResourceProvider;
import org.codelibs.fess.mylasta.direction.sponsor.FessListedClassificationProvider;
import org.codelibs.fess.mylasta.direction.sponsor.FessMailDeliveryDepartmentCreator;
import org.codelibs.fess.mylasta.direction.sponsor.FessMultipartRequestHandler;
import org.codelibs.fess.mylasta.direction.sponsor.FessSecurityResourceProvider;
import org.codelibs.fess.mylasta.direction.sponsor.FessTimeResourceProvider;
import org.codelibs.fess.mylasta.direction.sponsor.FessUserLocaleProcessProvider;
import org.codelibs.fess.mylasta.direction.sponsor.FessUserTimeZoneProcessProvider;
import org.lastaflute.core.direction.CachedFwAssistantDirector;
import org.lastaflute.core.direction.FwAssistDirection;
import org.lastaflute.core.direction.FwCoreDirection;
import org.lastaflute.core.security.InvertibleCryptographer;
import org.lastaflute.core.security.OneWayCryptographer;
import org.lastaflute.db.dbflute.classification.ListedClassificationProvider;
import org.lastaflute.db.direction.FwDbDirection;
import org.lastaflute.web.direction.FwWebDirection;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.ruts.renderer.JspHtmlRenderingProvider;

/**
 * @author jflute
 */
public class FessFwAssistantDirector extends CachedFwAssistantDirector {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    protected FessConfig fessConfig;

    // ===================================================================================
    //                                                                              Assist
    //                                                                              ======
    @Override
    protected void prepareAssistDirection(final FwAssistDirection direction) {
        direction.directConfig(nameList -> nameList.add("fess_config.properties"), "fess_env.properties");
    }

    // ===================================================================================
    //                                                                               Core
    //                                                                              ======
    @Override
    protected void prepareCoreDirection(final FwCoreDirection direction) {
        // this configuration is on fess_env.properties because this is true only when development
        direction.directDevelopmentHere(fessConfig.isDevelopmentHere());

        // titles of the application for logging are from configurations
        direction.directLoggingTitle(fessConfig.getDomainTitle(), fessConfig.getEnvironmentTitle());

        // this configuration is on sea_env.properties because it has no influence to production
        // even if you set true manually and forget to set false back
        direction.directFrameworkDebug(fessConfig.isFrameworkDebug()); // basically false

        // you can add your own process when your application is booting or closing
        direction.directCurtainBefore(createCurtainBeforeHook());
        direction.directCurtainFinally(createCurtainFinallyHook()); // when destroy

        direction.directSecurity(createSecurityResourceProvider());
        direction.directTime(createTimeResourceProvider());
        direction.directMail(createFessMailDeliveryDepartmentCreator().create());
        direction.directJson(createJsonResourceProvider());
    }

    protected FessJsonResourceProvider createJsonResourceProvider() {
        return new FessJsonResourceProvider();
    }

    protected FessCurtainBeforeHook createCurtainBeforeHook() {
        return new FessCurtainBeforeHook();
    }

    protected FessCurtainFinallyHook createCurtainFinallyHook() {
        return new FessCurtainFinallyHook();
    }

    protected FessSecurityResourceProvider createSecurityResourceProvider() {
        final InvertibleCryptographer inver;
        final String cipherAlgorism = fessConfig.getAppCipherAlgorism();
        if ("blowfish".equalsIgnoreCase(cipherAlgorism)) {
            inver = InvertibleCryptographer.createBlowfishCipher(fessConfig.getAppCipherKey());
        } else if ("des".equalsIgnoreCase(cipherAlgorism)) {
            inver = InvertibleCryptographer.createDesCipher(fessConfig.getAppCipherKey());
        } else if ("rsa".equalsIgnoreCase(cipherAlgorism)) {
            inver = InvertibleCryptographer.createRsaCipher(fessConfig.getAppCipherKey());
        } else {
            inver = InvertibleCryptographer.createAesCipher(fessConfig.getAppCipherKey());
        }

        final OneWayCryptographer oneWay;
        final String digestAlgorism = fessConfig.getAppDigestAlgorism();
        if ("sha512".equalsIgnoreCase(digestAlgorism)) {
            oneWay = OneWayCryptographer.createSha512Cryptographer();
        } else if ("md5".equalsIgnoreCase(digestAlgorism)) {
            oneWay = new OneWayCryptographer("MD5", OneWayCryptographer.ENCODING_UTF8);
        } else {
            oneWay = OneWayCryptographer.createSha256Cryptographer();
        }

        return new FessSecurityResourceProvider(inver, oneWay);
    }

    protected FessTimeResourceProvider createTimeResourceProvider() {
        return new FessTimeResourceProvider(fessConfig);
    }

    protected FessMailDeliveryDepartmentCreator createFessMailDeliveryDepartmentCreator() {
        return new FessMailDeliveryDepartmentCreator(fessConfig);
    }

    // ===================================================================================
    //                                                                                 DB
    //                                                                                ====
    @Override
    protected void prepareDbDirection(final FwDbDirection direction) {
        direction.directClassification(createListedClassificationProvider());
    }

    protected ListedClassificationProvider createListedClassificationProvider() {
        return new FessListedClassificationProvider();
    }

    // ===================================================================================
    //                                                                                Web
    //                                                                               =====
    @Override
    protected void prepareWebDirection(final FwWebDirection direction) {
        direction.directRequest(createUserLocaleProcessProvider(), createUserTimeZoneProcessProvider());
        direction.directCookie(createCookieResourceProvider());
        direction.directAdjustment(createActionAdjustmentProvider());
        direction.directMessage(createMessageNameList(), "fess_label");
        direction.directApiCall(createApiFailureHook());
        direction.directMultipart(FessMultipartRequestHandler::new);
        direction.directHtmlRendering(new JspHtmlRenderingProvider() {
            @Override
            protected String getShowErrorsForwardPath(final ActionRuntime runtime) {
                if (FessAdminAction.class.isAssignableFrom(runtime.getActionType())) {
                    return "/admin/error/error.jsp";
                }
                return "/error/system.jsp";
            }
        });
    }

    protected Consumer<List<String>> createMessageNameList() {
        return nameList -> nameList.add("fess_message");
    }

    protected FessUserLocaleProcessProvider createUserLocaleProcessProvider() {
        return new FessUserLocaleProcessProvider();
    }

    protected FessUserTimeZoneProcessProvider createUserTimeZoneProcessProvider() {
        return new FessUserTimeZoneProcessProvider();
    }

    protected FessCookieResourceProvider createCookieResourceProvider() { // #change_it_first
        final InvertibleCryptographer cr = InvertibleCryptographer.createAesCipher("*unused@");
        return new FessCookieResourceProvider(fessConfig, cr);
    }

    protected FessActionAdjustmentProvider createActionAdjustmentProvider() {
        return new FessActionAdjustmentProvider();
    }

    protected FessApiFailureHook createApiFailureHook() {
        return new FessApiFailureHook();
    }
}
