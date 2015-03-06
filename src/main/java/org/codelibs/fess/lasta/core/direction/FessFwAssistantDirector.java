/*
 * Copyright 2014-2015 the original author or authors.
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
package org.codelibs.fess.lasta.core.direction;

import java.util.TimeZone;

import javax.annotation.Resource;

import org.codelibs.fess.lasta.core.direction.sponsor.FessActionAdjustmentProvider;
import org.codelibs.fess.lasta.core.direction.sponsor.FessTimeResourceProvider;
import org.codelibs.fess.lasta.core.direction.sponsor.FessUserLocaleProcessProvider;
import org.codelibs.fess.lasta.core.direction.sponsor.FessUserTimeZoneProcessProvider;
import org.dbflute.saflute.core.direction.BootProcessCallback;
import org.dbflute.saflute.core.direction.CachedFwAssistantDirector;
import org.dbflute.saflute.core.direction.FwAssistantDirector;
import org.dbflute.saflute.core.direction.OptionalAssistDirection;
import org.dbflute.saflute.core.direction.OptionalCoreDirection;
import org.dbflute.saflute.core.security.InvertibleCipher;
import org.dbflute.saflute.core.security.SecurityResourceProvider;
import org.dbflute.saflute.db.dbflute.OptionalDBFluteDirection;
import org.dbflute.saflute.web.action.OptionalActionDirection;
import org.dbflute.saflute.web.servlet.OptionalServletDirection;
import org.dbflute.saflute.web.servlet.cookie.CookieResourceProvider;
import org.dbflute.saflute.web.task.OptionalTaskDirection;
import org.dbflute.system.DBFluteSystem;
import org.dbflute.system.provider.DfFinalTimeZoneProvider;
import org.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class FessFwAssistantDirector extends CachedFwAssistantDirector {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String FESS_CONFIG_FILE = "fess_config.properties";

    public static final String FESS_ENV_FILE = "fess_env.properties";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    protected FessConfig fessConfig;

    // ===================================================================================
    //                                                                              Assist
    //                                                                              ======
    @Override
    protected OptionalAssistDirection prepareOptionalAssistDirection() {
        final OptionalAssistDirection direction = new OptionalAssistDirection();
        prepareConfiguration(direction);
        return direction;
    }

    protected void prepareConfiguration(OptionalAssistDirection direction) {
        direction.directConfiguration(getDomainConfigFile(),
                getExtendsConfigFiles());
    }

    protected String getDomainConfigFile() {
        return FESS_CONFIG_FILE;
    }

    protected String[] getExtendsConfigFiles() {
        return new String[] { FESS_ENV_FILE };
    }

    // ===================================================================================
    //                                                                                Core
    //                                                                                ====
    @Override
    protected OptionalCoreDirection prepareOptionalCoreDirection() {
        final OptionalCoreDirection direction = new OptionalCoreDirection();
        prepareFramework(direction);
        prepareSecurity(direction);
        prepareTime(direction);
        return direction;
    }

    // -----------------------------------------------------
    //                                             Framework
    //                                             ---------
    protected void prepareFramework(OptionalCoreDirection direction) {
        // this configuration is on fess_env.properties
        // because this is true only when development
        direction.directDevelopmentHere(fessConfig.isDevelopmentHere());

        // titles are from configurations
        direction.directLoggingTitle(fessConfig.getDomainTitle(),
                fessConfig.getEnvironmentTitle());

        // this configuration is on sea_env.properties
        // because it has no influence to production
        // even if you set true manually and forget to set false back
        direction.directFrameworkDebug(fessConfig.isFrameworkDebug()); // basically false

        // you can add your own process when your application is booting
        direction.directBootProcessCallback(new BootProcessCallback() {
            public void callback(FwAssistantDirector assistantDirector) {
                processDBFluteSystem();
            }
        });
    }

    protected void processDBFluteSystem() {
        DBFluteSystem.unlock();
        DBFluteSystem.setFinalTimeZoneProvider(new DfFinalTimeZoneProvider() {
            protected final TimeZone provided = FessUserTimeZoneProcessProvider.centralTimeZone;

            public TimeZone provide() {
                return provided;
            }

            @Override
            public String toString() {
                return DfTypeUtil.toClassTitle(this) + ":{" + provided.getID()
                        + "}";
            }
        });
        DBFluteSystem.lock();
    }

    // -----------------------------------------------------
    //                                              Security
    //                                              --------
    protected void prepareSecurity(OptionalCoreDirection direction) {
        final String key = getPrimarySecurityWord();
        final InvertibleCipher primaryInvertibleCipher = InvertibleCipher
                .createAesCipher(key); // AES for now
        direction.directSecurity(new SecurityResourceProvider() {
            public InvertibleCipher providePrimaryInvertibleCipher() {
                return primaryInvertibleCipher;
            }
        });
    }

    protected String getPrimarySecurityWord() {
        return "fess:fess"; // hard coding for now
    }

    // -----------------------------------------------------
    //                                                  Time
    //                                                  ----
    protected void prepareTime(OptionalCoreDirection direction) {
        direction.directTime(createTimeResourceProvider());
    }

    protected FessTimeResourceProvider createTimeResourceProvider() {
        return new FessTimeResourceProvider(fessConfig);
    }

    // ===================================================================================
    //                                                                                  DB
    //                                                                                  ==
    @Override
    protected OptionalDBFluteDirection prepareOptionalDBFluteDirection() {
        final OptionalDBFluteDirection direction = new OptionalDBFluteDirection();
        return direction;
    }

    // ===================================================================================
    //                                                                                 Web
    //                                                                                 ===
    // -----------------------------------------------------
    //                                                Action
    //                                                ------
    @Override
    protected OptionalActionDirection prepareOptionalActionDirection() {
        final OptionalActionDirection direction = new OptionalActionDirection();
        prepareAdjustment(direction);
        prepareMessage(direction);
        return direction;
    }

    protected void prepareAdjustment(OptionalActionDirection direction) {
        direction.directAdjustment(createActionAdjustmentProvider());
    }

    protected FessActionAdjustmentProvider createActionAdjustmentProvider() {
        return new FessActionAdjustmentProvider();
    }

    protected void prepareMessage(OptionalActionDirection direction) {
        direction.directMessage(getDomainMessageName(),
                getExtendsMessageNames());
    }

    protected String getDomainMessageName() {
        return "fess_message";
    }

    protected String[] getExtendsMessageNames() {
        return new String[] {};
    }

    // -----------------------------------------------------
    //                                               Servlet
    //                                               -------
    @Override
    protected OptionalServletDirection prepareOptionalServletDirection() {
        final OptionalServletDirection direction = new OptionalServletDirection();
        prepareRequest(direction);
        prepareCookie(direction);
        return direction;
    }

    protected OptionalServletDirection prepareRequest(
            OptionalServletDirection direction) {
        direction.directRequest(createUserLocaleProcessProvider(),
                createUserTimeZoneProcessProvider());
        return direction;
    }

    protected FessUserLocaleProcessProvider createUserLocaleProcessProvider() {
        return new FessUserLocaleProcessProvider();
    }

    protected FessUserTimeZoneProcessProvider createUserTimeZoneProcessProvider() {
        return new FessUserTimeZoneProcessProvider();
    }

    protected void prepareCookie(OptionalServletDirection direction) {
        final String key = getCookieSecurityWord();
        final String cookieDefaultPath = fessConfig.getCookieDefaultPath();
        final Integer cookieDefaultExpire = fessConfig
                .getCookieDefaultExpireAsInteger();
        final InvertibleCipher cookieCipher = InvertibleCipher
                .createAesCipher(key); // AES for now
        direction.directCookie(new CookieResourceProvider() {
            public String provideDefaultPath() {
                return cookieDefaultPath;
            }

            public Integer provideDefaultExpire() {
                return cookieDefaultExpire;
            }

            public InvertibleCipher provideCipher() {
                return cookieCipher;
            }

            @Override
            public String toString() {
                return "{" + cookieDefaultPath + ", " + cookieDefaultExpire
                        + "}";
            }
        });
    }

    protected String getCookieSecurityWord() {
        return "fess:fess"; // hard coding for now
    }

    // -----------------------------------------------------
    //                                                  Task
    //                                                  ----
    @Override
    protected OptionalTaskDirection prepareOptionalTaskDirection() {
        return new OptionalTaskDirection();
    }
}
