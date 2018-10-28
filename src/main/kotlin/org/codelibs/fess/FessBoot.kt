/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess

import org.codelibs.core.lang.StringUtil
import org.dbflute.tomcat.TomcatBoot
import java.io.File

class FessBoot(port: Int, contextPath: String) : TomcatBoot(port, contextPath) {

    override fun getMarkDir(): String {
        return File(System.getProperty(JAVA_IO_TMPDIR), "fessboot").absolutePath
    }

    override fun prepareWebappPath(): String {
        val value = System.getProperty(FESS_WEBAPP_PATH)
        return value ?: super.prepareWebappPath()
    }

    companion object {

        private const val LOGGING_PROPERTIES = "logging.properties"

        private const val FESS_CONTEXT_PATH = "fess.context.path"

        private const val FESS_PORT = "fess.port"

        private const val FESS_TEMP_PATH = "fess.temp.path"

        private const val FESS_WEBAPP_PATH = "fess.webapp.path"

        private const val JAVA_IO_TMPDIR = "java.io.tmpdir"

        private const val TOMCAT_CONFIG_PATH = "tomcat.config.path"

        // ===================================================================================
        //                                                                        main
        //                                                                        ============

        @JvmStatic
        fun main(args: Array<String>) {
            // update java.io.tmpdir
            val tempPath = System.getProperty(FESS_TEMP_PATH)
            if (tempPath != null) {
                System.setProperty(JAVA_IO_TMPDIR, tempPath)
            }

            val tomcatBoot = FessBoot(port, contextPath) //
                    .useTldDetect() // for JSP
            if (tempPath != null) {
                tomcatBoot.atBaseDir(File(tempPath, "webapp").absolutePath)
            }
            val tomcatConfigPath = tomcatConfigPath
            if (tomcatConfigPath != null) {
                tomcatBoot.configure(tomcatConfigPath) // e.g. URIEncoding
            }
            tomcatBoot.logging(LOGGING_PROPERTIES) { op ->
                op.ignoreNoFile()
                val fessLogPath: String = System.getProperty("fess.log.path") ?: "../../logs"
                op.replace("fess.log.path", fessLogPath.replace("\\", "/"))
            } // uses jdk14logger
                    .asDevelopment(isNoneEnv).bootAwait()
        }

        @JvmStatic
        fun shutdown(args: Array<String>) {
            System.exit(0)
        }

        private val isNoneEnv: Boolean
            get() = System.getProperty("lasta.env") == null

        private val port: Int
            get() {
                val value = System.getProperty(FESS_PORT)
                return if (value != null) {
                    Integer.parseInt(value)
                } else 8080
            }

        private val contextPath: String
            get() {
                val value = System.getProperty(FESS_CONTEXT_PATH)
                return if (value != null) {
                    if ("/" == value) {
                        StringUtil.EMPTY
                    } else {
                        value
                    }
                } else StringUtil.EMPTY
            }

        private val tomcatConfigPath: String?
            get() = System.getProperty(TOMCAT_CONFIG_PATH)
    }
}
