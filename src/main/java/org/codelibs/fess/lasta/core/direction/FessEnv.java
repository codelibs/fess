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
package org.codelibs.fess.lasta.core.direction;

import org.dbflute.saflute.core.direction.ObjectiveConfig;

/**
 * @author FreeGen
 */
public interface FessEnv {

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
    String MAIL_SMTP_SERVER_DEFAULT_HOST_AND_PORT = "mail.smtp.server.default.host.and.port";

    /** The key of the configuration. e.g. jdbc:mysql://localhost:3306/maihamadb */
    String JDBC_URL = "jdbc.url";

    /** The key of the configuration. e.g. maihamauser */
    String JDBC_USER = "jdbc.user";

    /** The key of the configuration. e.g. maihamaword */
    String JDBC_PASSWORD = "jdbc.password";

    /** The key of the configuration. e.g. 10 */
    String JDBC_CONNECTION_POOLING_SIZE = "jdbc.connection.pooling.size";

    /**
     * Get the value of property as {@link String}.
     * @param propertyKey The key of the property. (NotNull)
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String get(String propertyKey);

    /**
     * Is the property true?
     * @param propertyKey The key of the property which is boolean type. (NotNull)
     * @return The determination, true or false. (if the property can be true, returns true)
     */
    boolean is(String propertyKey);

    /**
     * Get the value for the key 'development.here'. <br />
     * The value is, e.g. true <br />
     * comment: Is development environment here? (used for various purpose, you should set false if unknown)
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getDevelopmentHere();

    /**
     * Is the property for the key 'development.here' true? <br />
     * The value is, e.g. true <br />
     * comment: Is development environment here? (used for various purpose, you should set false if unknown)
     * @return The determination, true or false. (if the property can be true, returns true)
     */
    boolean isDevelopmentHere();

    /**
     * Get the value for the key 'environment.title'. <br />
     * The value is, e.g. Local Development <br />
     * comment: The title of environment (e.g. local or integartion or production)
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getEnvironmentTitle();

    /**
     * Get the value for the key 'framework.debug'. <br />
     * The value is, e.g. false <br />
     * comment: Does it enable the Framework internal debug? (true only when emergency)
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getFrameworkDebug();

    /**
     * Is the property for the key 'framework.debug' true? <br />
     * The value is, e.g. false <br />
     * comment: Does it enable the Framework internal debug? (true only when emergency)
     * @return The determination, true or false. (if the property can be true, returns true)
     */
    boolean isFrameworkDebug();

    /**
     * Get the value for the key 'time.adjust.time.millis'. <br />
     * The value is, e.g. 0 <br />
     * comment: The milliseconds for (relative or absolute) adjust time (set only when test) @LongType *dynamic in development
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getTimeAdjustTimeMillis();

    /**
     * Get the value for the key 'time.adjust.time.millis' as {@link Long}. <br />
     * The value is, e.g. 0 <br />
     * comment: The milliseconds for (relative or absolute) adjust time (set only when test) @LongType *dynamic in development
     * @return The value of found property. (NullAllowed: if null, not found)
     * @throws NumberFormatException When the property is not long.
     */
    Long getTimeAdjustTimeMillisAsLong();

    /**
     * Get the value for the key 'mail.send.mock'. <br />
     * The value is, e.g. true <br />
     * comment: Does it send mock mail? (true: no send actually, logging only)
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getMailSendMock();

    /**
     * Is the property for the key 'mail.send.mock' true? <br />
     * The value is, e.g. true <br />
     * comment: Does it send mock mail? (true: no send actually, logging only)
     * @return The determination, true or false. (if the property can be true, returns true)
     */
    boolean isMailSendMock();

    /**
     * Get the value for the key 'mail.smtp.server.default.host.and.port'. <br />
     * The value is, e.g. localhost:25 <br />
     * comment: SMTP server settings for default: host:port
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getMailSmtpServerDefaultHostAndPort();

    /**
     * Get the value for the key 'jdbc.url'. <br />
     * The value is, e.g. jdbc:mysql://localhost:3306/maihamadb <br />
     * comment: The URL of database connection for JDBC
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getJdbcUrl();

    /**
     * Get the value for the key 'jdbc.user'. <br />
     * The value is, e.g. maihamauser <br />
     * comment: The user of database connection for JDBC
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getJdbcUser();

    /**
     * Get the value for the key 'jdbc.password'. <br />
     * The value is, e.g. maihamaword <br />
     * comment: @Secure The password of database connection for JDBC
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getJdbcPassword();

    /**
     * Get the value for the key 'jdbc.connection.pooling.size'. <br />
     * The value is, e.g. 10 <br />
     * comment: The (max) pooling size of Seasar's connection pool
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getJdbcConnectionPoolingSize();

    /**
     * Get the value for the key 'jdbc.connection.pooling.size' as {@link Integer}. <br />
     * The value is, e.g. 10 <br />
     * comment: The (max) pooling size of Seasar's connection pool
     * @return The value of found property. (NullAllowed: if null, not found)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getJdbcConnectionPoolingSizeAsInteger();

    /**
     * The simple implementation for configuration.
     * @author FreeGen
     */
    public static class SimpleImpl extends ObjectiveConfig implements FessEnv {

        /** The serial version UID for object serialization. (Default) */
        private static final long serialVersionUID = 1L;

        /** {@inheritDoc} */
        public String getDevelopmentHere() {
            return get(FessEnv.DEVELOPMENT_HERE);
        }

        /** {@inheritDoc} */
        public boolean isDevelopmentHere() {
            return is(FessEnv.DEVELOPMENT_HERE);
        }

        /** {@inheritDoc} */
        public String getEnvironmentTitle() {
            return get(FessEnv.ENVIRONMENT_TITLE);
        }

        /** {@inheritDoc} */
        public String getFrameworkDebug() {
            return get(FessEnv.FRAMEWORK_DEBUG);
        }

        /** {@inheritDoc} */
        public boolean isFrameworkDebug() {
            return is(FessEnv.FRAMEWORK_DEBUG);
        }

        /** {@inheritDoc} */
        public String getTimeAdjustTimeMillis() {
            return get(FessEnv.TIME_ADJUST_TIME_MILLIS);
        }

        /** {@inheritDoc} */
        public Long getTimeAdjustTimeMillisAsLong() {
            return getAsLong(FessEnv.TIME_ADJUST_TIME_MILLIS);
        }

        /** {@inheritDoc} */
        public String getMailSendMock() {
            return get(FessEnv.MAIL_SEND_MOCK);
        }

        /** {@inheritDoc} */
        public boolean isMailSendMock() {
            return is(FessEnv.MAIL_SEND_MOCK);
        }

        /** {@inheritDoc} */
        public String getMailSmtpServerDefaultHostAndPort() {
            return get(FessEnv.MAIL_SMTP_SERVER_DEFAULT_HOST_AND_PORT);
        }

        /** {@inheritDoc} */
        public String getJdbcUrl() {
            return get(FessEnv.JDBC_URL);
        }

        /** {@inheritDoc} */
        public String getJdbcUser() {
            return get(FessEnv.JDBC_USER);
        }

        /** {@inheritDoc} */
        public String getJdbcPassword() {
            return get(FessEnv.JDBC_PASSWORD);
        }

        /** {@inheritDoc} */
        public String getJdbcConnectionPoolingSize() {
            return get(FessEnv.JDBC_CONNECTION_POOLING_SIZE);
        }

        /** {@inheritDoc} */
        public Integer getJdbcConnectionPoolingSizeAsInteger() {
            return getAsInteger(FessEnv.JDBC_CONNECTION_POOLING_SIZE);
        }
    }
}
