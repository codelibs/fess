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

import org.lastaflute.core.direction.ObjectiveConfig;
import org.lastaflute.core.direction.exception.ConfigPropertyNotFoundException;

/**
 * @author FreeGen
 */
public interface FessEnv {

    /** The key of the configuration. e.g. warm */
    String lasta_di_SMART_DEPLOY_MODE = "lasta_di.smart.deploy.mode";

    /** The key of the configuration. e.g. true */
    String DEVELOPMENT_HERE = "development.here";

    /** The key of the configuration. e.g. Local Development */
    String ENVIRONMENT_TITLE = "environment.title";

    /** The key of the configuration. e.g. false */
    String FRAMEWORK_DEBUG = "framework.debug";

    /** The key of the configuration. e.g. 0 */
    String TIME_ADJUST_TIME_MILLIS = "time.adjust.time.millis";

    /** The key of the configuration. e.g. true */
    String MAIL_SEND_MOCK = "mail.send.mock";

    /** The key of the configuration. e.g. localhost:25 */
    String MAIL_SMTP_SERVER_MAIN_HOST_AND_PORT = "mail.smtp.server.main.host.and.port";

    /** The key of the configuration. e.g. [Test] */
    String MAIL_SUBJECT_TEST_PREFIX = "mail.subject.test.prefix";

    /** The key of the configuration. e.g. root@localhost */
    String MAIL_RETURN_PATH = "mail.return.path";

    /**
     * Get the value of property as {@link String}.
     * @param propertyKey The key of the property. (NotNull)
     * @return The value of found property. (NotNull: if not found, exception)
     * @throws ConfigPropertyNotFoundException When the property is not found.
     */
    String get(String propertyKey);

    /**
     * Is the property true?
     * @param propertyKey The key of the property which is boolean type. (NotNull)
     * @return The determination, true or false. (if not found, exception)
     * @throws ConfigPropertyNotFoundException When the property is not found.
     */
    boolean is(String propertyKey);

    /**
     * Get the value for the key 'lasta_di.smart.deploy.mode'. <br>
     * The value is, e.g. warm <br>
     * comment: The mode of Lasta Di's smart-deploy, should be cool in production (e.g. hot, cool, warm)
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLastaDiSmartDeployMode();

    /**
     * Get the value for the key 'development.here'. <br>
     * The value is, e.g. true <br>
     * comment: Is development environment here? (used for various purpose, you should set false if unknown)
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getDevelopmentHere();

    /**
     * Is the property for the key 'development.here' true? <br>
     * The value is, e.g. true <br>
     * comment: Is development environment here? (used for various purpose, you should set false if unknown)
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isDevelopmentHere();

    /**
     * Get the value for the key 'environment.title'. <br>
     * The value is, e.g. Local Development <br>
     * comment: The title of environment (e.g. local or integration or production)
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getEnvironmentTitle();

    /**
     * Get the value for the key 'framework.debug'. <br>
     * The value is, e.g. false <br>
     * comment: Does it enable the Framework internal debug? (true only when emergency)
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getFrameworkDebug();

    /**
     * Is the property for the key 'framework.debug' true? <br>
     * The value is, e.g. false <br>
     * comment: Does it enable the Framework internal debug? (true only when emergency)
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isFrameworkDebug();

    /**
     * Get the value for the key 'time.adjust.time.millis'. <br>
     * The value is, e.g. 0 <br>
     * comment: <br>
     * one day: 86400000, three days: 259200000, five days: 432000000, one week: 604800000, one year: 31556926000<br>
     * special script :: absolute mode: $(2014/07/10), relative mode: addDay(3).addMonth(4)<br>
     * The milliseconds for (relative or absolute) adjust time (set only when test) @LongType *dynamic in development
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getTimeAdjustTimeMillis();

    /**
     * Get the value for the key 'time.adjust.time.millis' as {@link Long}. <br>
     * The value is, e.g. 0 <br>
     * comment: <br>
     * one day: 86400000, three days: 259200000, five days: 432000000, one week: 604800000, one year: 31556926000<br>
     * special script :: absolute mode: $(2014/07/10), relative mode: addDay(3).addMonth(4)<br>
     * The milliseconds for (relative or absolute) adjust time (set only when test) @LongType *dynamic in development
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not long.
     */
    Long getTimeAdjustTimeMillisAsLong();

    /**
     * Get the value for the key 'mail.send.mock'. <br>
     * The value is, e.g. true <br>
     * comment: Does it send mock mail? (true: no send actually, logging only)
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getMailSendMock();

    /**
     * Is the property for the key 'mail.send.mock' true? <br>
     * The value is, e.g. true <br>
     * comment: Does it send mock mail? (true: no send actually, logging only)
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isMailSendMock();

    /**
     * Get the value for the key 'mail.smtp.server.main.host.and.port'. <br>
     * The value is, e.g. localhost:25 <br>
     * comment: SMTP server settings for main: host:port
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getMailSmtpServerMainHostAndPort();

    /**
     * Get the value for the key 'mail.subject.test.prefix'. <br>
     * The value is, e.g. [Test] <br>
     * comment: The prefix of subject to show test environment or not
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getMailSubjectTestPrefix();

    /**
     * Get the value for the key 'mail.return.path'. <br>
     * The value is, e.g. root@localhost <br>
     * comment: The common return path of all mail
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getMailReturnPath();

    /**
     * The simple implementation for configuration.
     * @author FreeGen
     */
    public static class SimpleImpl extends ObjectiveConfig implements FessEnv {

        /** The serial version UID for object serialization. (Default) */
        private static final long serialVersionUID = 1L;

        public String getLastaDiSmartDeployMode() {
            return get(FessEnv.lasta_di_SMART_DEPLOY_MODE);
        }

        public String getDevelopmentHere() {
            return get(FessEnv.DEVELOPMENT_HERE);
        }

        public boolean isDevelopmentHere() {
            return is(FessEnv.DEVELOPMENT_HERE);
        }

        public String getEnvironmentTitle() {
            return get(FessEnv.ENVIRONMENT_TITLE);
        }

        public String getFrameworkDebug() {
            return get(FessEnv.FRAMEWORK_DEBUG);
        }

        public boolean isFrameworkDebug() {
            return is(FessEnv.FRAMEWORK_DEBUG);
        }

        public String getTimeAdjustTimeMillis() {
            return get(FessEnv.TIME_ADJUST_TIME_MILLIS);
        }

        public Long getTimeAdjustTimeMillisAsLong() {
            return getAsLong(FessEnv.TIME_ADJUST_TIME_MILLIS);
        }

        public String getMailSendMock() {
            return get(FessEnv.MAIL_SEND_MOCK);
        }

        public boolean isMailSendMock() {
            return is(FessEnv.MAIL_SEND_MOCK);
        }

        public String getMailSmtpServerMainHostAndPort() {
            return get(FessEnv.MAIL_SMTP_SERVER_MAIN_HOST_AND_PORT);
        }

        public String getMailSubjectTestPrefix() {
            return get(FessEnv.MAIL_SUBJECT_TEST_PREFIX);
        }

        public String getMailReturnPath() {
            return get(FessEnv.MAIL_RETURN_PATH);
        }

        @Override
        protected java.util.Map<String, String> prepareGeneratedDefaultMap() {
            java.util.Map<String, String> defaultMap = super.prepareGeneratedDefaultMap();
            defaultMap.put(FessEnv.lasta_di_SMART_DEPLOY_MODE, "warm");
            defaultMap.put(FessEnv.DEVELOPMENT_HERE, "true");
            defaultMap.put(FessEnv.ENVIRONMENT_TITLE, "Local Development");
            defaultMap.put(FessEnv.FRAMEWORK_DEBUG, "false");
            defaultMap.put(FessEnv.TIME_ADJUST_TIME_MILLIS, "0");
            defaultMap.put(FessEnv.MAIL_SEND_MOCK, "true");
            defaultMap.put(FessEnv.MAIL_SMTP_SERVER_MAIN_HOST_AND_PORT, "localhost:25");
            defaultMap.put(FessEnv.MAIL_SUBJECT_TEST_PREFIX, "[Test]");
            defaultMap.put(FessEnv.MAIL_RETURN_PATH, "root@localhost");
            return defaultMap;
        }
    }
}
