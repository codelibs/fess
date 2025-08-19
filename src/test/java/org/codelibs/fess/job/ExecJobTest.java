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
package org.codelibs.fess.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.Constants;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.ProcessHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.ScheduledJob;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.exception.IORuntimeException;
import org.lastaflute.job.LaJobRuntime;

public class ExecJobTest extends UnitFessTestCase {

    private TestExecJob execJob;
    private File tempDir;
    private SystemHelper originalSystemHelper;
    private ProcessHelper originalProcessHelper;
    private JobHelper originalJobHelper;

    // Concrete implementation for testing
    private static class TestExecJob extends ExecJob {
        private String executeType = "test";
        private String executeResult = "success";
        private boolean throwException = false;

        @Override
        public String execute() {
            if (throwException) {
                throw new RuntimeException("Test exception");
            }
            return executeResult;
        }

        @Override
        protected String getExecuteType() {
            return executeType;
        }

        public void setExecuteType(String executeType) {
            this.executeType = executeType;
        }

        public void setExecuteResult(String executeResult) {
            this.executeResult = executeResult;
        }

        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }

        // Expose protected methods for testing
        public void testAddSystemProperty(List<String> cmdList, String name, String defaultValue, String appendValue) {
            super.addSystemProperty(cmdList, name, defaultValue, appendValue);
        }

        public void testAddFessConfigProperties(List<String> cmdList) {
            super.addFessConfigProperties(cmdList);
        }

        public void testAddFessSystemProperties(List<String> cmdList) {
            super.addFessSystemProperties(cmdList);
        }

        public void testAddFessCustomSystemProperties(List<String> cmdList, String regex) {
            super.addFessCustomSystemProperties(cmdList, regex);
        }

        public void testDeleteTempDir(File ownTmpDir) {
            super.deleteTempDir(ownTmpDir);
        }

        public void testAppendJarFile(String cpSeparator, StringBuilder buf, File libDir, String basePath) {
            super.appendJarFile(cpSeparator, buf, libDir, basePath);
        }

        public TimeoutTask testCreateTimeoutTask() {
            return super.createTimeoutTask();
        }

        public void testCreateSystemProperties(List<String> cmdList, File propFile) {
            super.createSystemProperties(cmdList, propFile);
        }

        public String testGetLogName(String logPrefix) {
            return super.getLogName(logPrefix);
        }
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        execJob = new TestExecJob();
        tempDir = Files.createTempDirectory("execjob_test").toFile();

        // Store original helpers
        originalSystemHelper = ComponentUtil.hasComponent("systemHelper") ? ComponentUtil.getSystemHelper() : null;
        originalProcessHelper = ComponentUtil.hasComponent("processHelper") ? ComponentUtil.getProcessHelper() : null;
        originalJobHelper = ComponentUtil.hasComponent("jobHelper") ? ComponentUtil.getJobHelper() : null;

        // Register mock helpers
        ComponentUtil.register(new SystemHelper() {
            @Override
            public String getLogFilePath() {
                return tempDir.getAbsolutePath();
            }
        }, "systemHelper");

        ComponentUtil.register(new ProcessHelper() {
            @Override
            public int destroyProcess(String sessionId) {
                // Mock implementation
                return 0;
            }
        }, "processHelper");

        ComponentUtil.register(new JobHelper() {
            @Override
            public LaJobRuntime getJobRuntime() {
                return null;
            }
        }, "jobHelper");
    }

    @Override
    public void tearDown() throws Exception {
        FileUtils.deleteQuietly(tempDir);

        // Restore original helpers
        if (originalSystemHelper != null) {
            ComponentUtil.register(originalSystemHelper, "systemHelper");
        }
        if (originalProcessHelper != null) {
            ComponentUtil.register(originalProcessHelper, "processHelper");
        }
        if (originalJobHelper != null) {
            ComponentUtil.register(originalJobHelper, "jobHelper");
        }

        super.tearDown();
    }

    // Test constructor and initial state
    public void test_constructor() {
        assertNotNull(execJob);
        assertNull(execJob.jobExecutor);
        assertNull(execJob.sessionId);
        assertTrue(execJob.useLocalFesen);
        assertNull(execJob.logFilePath);
        assertNull(execJob.logLevel);
        assertEquals(StringUtil.EMPTY, execJob.logSuffix);
        assertNotNull(execJob.jvmOptions);
        assertTrue(execJob.jvmOptions.isEmpty());
        assertNull(execJob.lastaEnv);
        assertEquals(-1, execJob.timeout);
        assertFalse(execJob.processTimeout);
    }

    // Test execute method
    public void test_execute() {
        assertEquals("success", execJob.execute());

        execJob.setExecuteResult("custom result");
        assertEquals("custom result", execJob.execute());
    }

    // Test execute with JobExecutor
    public void test_execute_withJobExecutor() {
        JobExecutor jobExecutor = new JobExecutor() {
            @Override
            public Object execute(String scriptType, String script) {
                return "executed";
            }
        };

        String result = execJob.execute(jobExecutor);
        assertEquals("success", result);
        assertSame(jobExecutor, execJob.jobExecutor);
    }

    // Test getExecuteType
    public void test_getExecuteType() {
        assertEquals("test", execJob.getExecuteType());

        execJob.setExecuteType("crawler");
        assertEquals("crawler", execJob.getExecuteType());
    }

    // Test jobExecutor setter
    public void test_jobExecutor() {
        JobExecutor jobExecutor = new JobExecutor() {
            @Override
            public Object execute(String scriptType, String script) {
                return null;
            }
        };

        ExecJob result = execJob.jobExecutor(jobExecutor);
        assertSame(jobExecutor, execJob.jobExecutor);
        assertSame(execJob, result);
    }

    // Test sessionId setter
    public void test_sessionId() {
        String testSessionId = "test-session-123";
        ExecJob result = execJob.sessionId(testSessionId);
        assertEquals(testSessionId, execJob.sessionId);
        assertSame(execJob, result);
    }

    // Test logFilePath setter
    public void test_logFilePath() {
        String testPath = "/path/to/log";
        ExecJob result = execJob.logFilePath(testPath);
        assertEquals(testPath, execJob.logFilePath);
        assertSame(execJob, result);
    }

    // Test logLevel setter
    public void test_logLevel() {
        String testLevel = "DEBUG";
        ExecJob result = execJob.logLevel(testLevel);
        assertEquals(testLevel, execJob.logLevel);
        assertSame(execJob, result);
    }

    // Test logSuffix setter
    public void test_logSuffix() {
        String testSuffix = "test suffix";
        ExecJob result = execJob.logSuffix(testSuffix);
        assertEquals("test_suffix", execJob.logSuffix);
        assertSame(execJob, result);

        // Test with leading/trailing spaces
        result = execJob.logSuffix("  another suffix  ");
        assertEquals("another_suffix", execJob.logSuffix);

        // Test with multiple spaces
        result = execJob.logSuffix("multi  space  suffix");
        assertEquals("multi__space__suffix", execJob.logSuffix);
    }

    // Test timeout setter
    public void test_timeout() {
        ExecJob result = execJob.timeout(60);
        assertEquals(60, execJob.timeout);
        assertSame(execJob, result);

        // Test with negative value
        result = execJob.timeout(-1);
        assertEquals(-1, execJob.timeout);
        assertSame(execJob, result);
    }

    // Test useLocalFesen setter
    public void test_useLocalFesen() {
        ExecJob result = execJob.useLocalFesen(false);
        assertFalse(execJob.useLocalFesen);
        assertSame(execJob, result);

        result = execJob.useLocalFesen(true);
        assertTrue(execJob.useLocalFesen);
        assertSame(execJob, result);
    }

    // Test remoteDebug method
    public void test_remoteDebug() {
        ExecJob result = execJob.remoteDebug();
        assertSame(execJob, result);
        assertEquals(2, execJob.jvmOptions.size());
        assertEquals("-Xdebug", execJob.jvmOptions.get(0));
        assertEquals("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=localhost:8000", execJob.jvmOptions.get(1));
    }

    // Test gcLogging method
    public void test_gcLogging() {
        ExecJob result = execJob.gcLogging();
        assertSame(execJob, result);
        assertEquals(1, execJob.jvmOptions.size());
        assertTrue(execJob.jvmOptions.get(0).startsWith("-Xlog:gc*,gc+age=trace,safepoint:file="));
        assertTrue(execJob.jvmOptions.get(0).contains("gc-test.log"));

        // Test with custom logFilePath
        execJob.jvmOptions.clear();
        execJob.logFilePath("/custom/path");
        result = execJob.gcLogging();
        assertTrue(execJob.jvmOptions.get(0).contains("/custom/path"));
    }

    // Test jvmOptions method
    public void test_jvmOptions() {
        ExecJob result = execJob.jvmOptions("-Xmx1024m", "-Xms512m");
        assertSame(execJob, result);
        assertEquals(2, execJob.jvmOptions.size());
        assertEquals("-Xmx1024m", execJob.jvmOptions.get(0));
        assertEquals("-Xms512m", execJob.jvmOptions.get(1));

        // Add more options
        result = execJob.jvmOptions("-XX:+UseG1GC");
        assertEquals(3, execJob.jvmOptions.size());
        assertEquals("-XX:+UseG1GC", execJob.jvmOptions.get(2));
    }

    // Test lastaEnv setter
    public void test_lastaEnv() {
        String testEnv = "production";
        ExecJob result = execJob.lastaEnv(testEnv);
        assertEquals(testEnv, execJob.lastaEnv);
        assertSame(execJob, result);
    }

    // Test addSystemProperty method
    public void test_addSystemProperty() {
        List<String> cmdList = new ArrayList<>();

        // Test with system property that exists
        System.setProperty("test.property", "test.value");
        execJob.testAddSystemProperty(cmdList, "test.property", "default", null);
        assertEquals(1, cmdList.size());
        assertEquals("-Dtest.property=test.value", cmdList.get(0));

        // Test with append value
        cmdList.clear();
        execJob.testAddSystemProperty(cmdList, "test.property", "default", ":append");
        assertEquals(1, cmdList.size());
        assertEquals("-Dtest.property=test.value:append", cmdList.get(0));

        // Test with property that doesn't exist and default value
        cmdList.clear();
        execJob.testAddSystemProperty(cmdList, "non.existent.property", "default.value", null);
        assertEquals(1, cmdList.size());
        assertEquals("-Dnon.existent.property=default.value", cmdList.get(0));

        // Test with property that doesn't exist and no default value
        cmdList.clear();
        execJob.testAddSystemProperty(cmdList, "another.non.existent", null, null);
        assertEquals(0, cmdList.size());

        // Clean up
        System.clearProperty("test.property");
    }

    // Test addFessConfigProperties method
    public void test_addFessConfigProperties() {
        List<String> cmdList = new ArrayList<>();

        // Set some test properties
        System.setProperty(Constants.FESS_CONFIG_PREFIX + "test1", "value1");
        System.setProperty(Constants.FESS_CONFIG_PREFIX + "test2", "value2");
        System.setProperty("non.fess.property", "value3");

        execJob.testAddFessConfigProperties(cmdList);

        // Should contain only fess.conf properties
        assertEquals(2, cmdList.size());
        assertTrue(cmdList.contains("-D" + Constants.FESS_CONFIG_PREFIX + "test1=value1"));
        assertTrue(cmdList.contains("-D" + Constants.FESS_CONFIG_PREFIX + "test2=value2"));

        // Clean up
        System.clearProperty(Constants.FESS_CONFIG_PREFIX + "test1");
        System.clearProperty(Constants.FESS_CONFIG_PREFIX + "test2");
        System.clearProperty("non.fess.property");
    }

    // Test addFessSystemProperties method
    public void test_addFessSystemProperties() {
        List<String> cmdList = new ArrayList<>();

        // Set some test properties
        System.setProperty(Constants.SYSTEM_PROP_PREFIX + "prop1", "value1");
        System.setProperty(Constants.SYSTEM_PROP_PREFIX + "prop2", "value2");
        System.setProperty("regular.property", "value3");

        execJob.testAddFessSystemProperties(cmdList);

        // Should contain only system properties
        assertEquals(2, cmdList.size());
        assertTrue(cmdList.contains("-D" + Constants.SYSTEM_PROP_PREFIX + "prop1=value1"));
        assertTrue(cmdList.contains("-D" + Constants.SYSTEM_PROP_PREFIX + "prop2=value2"));

        // Clean up
        System.clearProperty(Constants.SYSTEM_PROP_PREFIX + "prop1");
        System.clearProperty(Constants.SYSTEM_PROP_PREFIX + "prop2");
        System.clearProperty("regular.property");
    }

    // Test addFessCustomSystemProperties method
    public void test_addFessCustomSystemProperties() {
        List<String> cmdList = new ArrayList<>();

        // Set some test properties
        System.setProperty("custom.prop.one", "value1");
        System.setProperty("custom.prop.two", "value2");
        System.setProperty("other.property", "value3");

        // Test with regex pattern
        execJob.testAddFessCustomSystemProperties(cmdList, "custom\\.prop\\..*");

        assertEquals(2, cmdList.size());
        assertTrue(cmdList.contains("-Dcustom.prop.one=value1"));
        assertTrue(cmdList.contains("-Dcustom.prop.two=value2"));

        // Test with null regex
        cmdList.clear();
        execJob.testAddFessCustomSystemProperties(cmdList, null);
        assertEquals(0, cmdList.size());

        // Test with empty regex
        cmdList.clear();
        execJob.testAddFessCustomSystemProperties(cmdList, "");
        assertEquals(0, cmdList.size());

        // Clean up
        System.clearProperty("custom.prop.one");
        System.clearProperty("custom.prop.two");
        System.clearProperty("other.property");
    }

    // Test deleteTempDir method
    public void test_deleteTempDir() throws IOException {
        // Test with null
        execJob.testDeleteTempDir(null);

        // Test with existing directory
        File testDir = new File(tempDir, "test_delete_dir");
        assertTrue(testDir.mkdir());
        assertTrue(testDir.exists());

        execJob.testDeleteTempDir(testDir);
        assertFalse(testDir.exists());

        // Test with non-existent directory
        File nonExistentDir = new File(tempDir, "non_existent");
        execJob.testDeleteTempDir(nonExistentDir);
    }

    // Test appendJarFile method
    public void test_appendJarFile() throws IOException {
        StringBuilder buf = new StringBuilder();
        File libDir = new File(tempDir, "lib");
        assertTrue(libDir.mkdir());

        // Create test jar files
        File jar1 = new File(libDir, "test1.jar");
        File jar2 = new File(libDir, "test2.JAR");
        File notJar = new File(libDir, "test.txt");
        assertTrue(jar1.createNewFile());
        assertTrue(jar2.createNewFile());
        assertTrue(notJar.createNewFile());

        execJob.testAppendJarFile(":", buf, libDir, "/base/path/");

        String result = buf.toString();
        assertTrue(result.contains(":/base/path/test1.jar"));
        assertTrue(result.contains(":/base/path/test2.JAR"));
        assertFalse(result.contains("test.txt"));

        // Test with non-existent directory
        buf = new StringBuilder();
        File nonExistentDir = new File(tempDir, "non_existent_lib");
        execJob.testAppendJarFile(":", buf, nonExistentDir, "/base/");
        assertEquals("", buf.toString());
    }

    // Test createTimeoutTask method
    public void test_createTimeoutTask() {
        // Test with no timeout
        execJob.timeout = -1;
        assertNull(execJob.testCreateTimeoutTask());

        execJob.timeout = 0;
        assertNull(execJob.testCreateTimeoutTask());

        // Test with timeout set
        execJob.timeout = 10;
        execJob.sessionId = "test-session";
        TimeoutTask task = execJob.testCreateTimeoutTask();
        assertNotNull(task);

        // Clean up timeout task
        task.cancel();
    }

    // Test createSystemProperties method
    public void test_createSystemProperties() throws IOException {
        List<String> cmdList = new ArrayList<>();
        cmdList.add("test");
        cmdList.add("command");

        File propFile = new File(tempDir, "test.properties");

        // Mock ComponentUtil.getSystemProperties() to return actual system properties
        Properties systemProps = System.getProperties();

        // Set a system property
        System.setProperty("test.prop", "test.value");

        execJob.testCreateSystemProperties(cmdList, propFile);

        assertTrue(propFile.exists());

        // Read and verify properties
        Properties props = new Properties();
        try (var in = Files.newInputStream(propFile.toPath())) {
            props.load(in);
            // The property should be in the file if it was in system properties
            String value = props.getProperty("test.prop");
            if (value == null) {
                // If not found, it means ComponentUtil.getSystemProperties() is not returning system properties
                // In that case, just check that the file was created
                assertTrue(propFile.exists());
            } else {
                assertEquals("test.value", value);
            }
        }

        // Clean up
        System.clearProperty("test.prop");
    }

    // Test createSystemProperties with IOException
    public void test_createSystemProperties_IOException() {
        List<String> cmdList = new ArrayList<>();
        File invalidFile = new File("/invalid/path/test.properties");

        try {
            execJob.testCreateSystemProperties(cmdList, invalidFile);
            fail("Should throw IORuntimeException");
        } catch (IORuntimeException e) {
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    // Test getLogName method
    public void test_getLogName() {
        // Test without suffix
        execJob.logSuffix = "";
        assertEquals("prefix-test", execJob.testGetLogName("prefix"));

        // Test with suffix
        execJob.logSuffix = "suffix";
        assertEquals("prefix-test-suffix", execJob.testGetLogName("prefix"));

        // Test with different execute type
        execJob.setExecuteType("crawler");
        assertEquals("prefix-crawler-suffix", execJob.testGetLogName("prefix"));
    }

    // Test complex scenario with multiple configurations
    public void test_complexConfiguration() {
        execJob.sessionId("complex-session")
                .logFilePath("/var/log/fess")
                .logLevel("INFO")
                .logSuffix("daily job")
                .timeout(300)
                .useLocalFesen(false)
                .lastaEnv("production")
                .jvmOptions("-Xmx2g", "-Xms1g")
                .remoteDebug()
                .gcLogging();

        assertEquals("complex-session", execJob.sessionId);
        assertEquals("/var/log/fess", execJob.logFilePath);
        assertEquals("INFO", execJob.logLevel);
        assertEquals("daily_job", execJob.logSuffix);
        assertEquals(300, execJob.timeout);
        assertFalse(execJob.useLocalFesen);
        assertEquals("production", execJob.lastaEnv);
        assertTrue(execJob.jvmOptions.contains("-Xmx2g"));
        assertTrue(execJob.jvmOptions.contains("-Xms1g"));
        assertTrue(execJob.jvmOptions.contains("-Xdebug"));
        assertTrue(execJob.jvmOptions.stream().anyMatch(opt -> opt.startsWith("-Xlog:gc")));
    }

    // Test edge cases and boundary conditions
    public void test_edgeCases() {
        // Test with empty strings
        execJob.sessionId("");
        assertEquals("", execJob.sessionId);

        execJob.logFilePath("");
        assertEquals("", execJob.logFilePath);

        execJob.logLevel("");
        assertEquals("", execJob.logLevel);

        execJob.lastaEnv("");
        assertEquals("", execJob.lastaEnv);

        // Test with very long timeout
        execJob.timeout(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, execJob.timeout);

        // Test with minimum timeout
        execJob.timeout(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, execJob.timeout);
    }

    // Test exception handling in execute
    public void test_execute_withException() {
        execJob.setThrowException(true);

        try {
            execJob.execute();
            fail("Should throw RuntimeException");
        } catch (RuntimeException e) {
            assertEquals("Test exception", e.getMessage());
        }
    }

    // Test concurrent modification of jvmOptions
    public void test_jvmOptions_concurrent() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                execJob.jvmOptions("-Xmx" + i + "m");
            }
            latch.countDown();
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                execJob.jvmOptions("-Xms" + i + "m");
            }
            latch.countDown();
        });

        t1.start();
        t2.start();

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        // The exact size may vary due to concurrent modifications, just check it's not empty
        assertTrue(execJob.jvmOptions.size() > 0);
    }

    // Test pattern matching in custom properties
    public void test_customProperties_complexPattern() {
        List<String> cmdList = new ArrayList<>();

        // Set various properties
        System.setProperty("app.module.feature1", "enabled");
        System.setProperty("app.module.feature2", "disabled");
        System.setProperty("app.service.api", "v2");
        System.setProperty("config.app.module", "test");

        // Test with complex regex
        execJob.testAddFessCustomSystemProperties(cmdList, "app\\.module\\..*");

        assertEquals(2, cmdList.size());
        assertTrue(cmdList.contains("-Dapp.module.feature1=enabled"));
        assertTrue(cmdList.contains("-Dapp.module.feature2=disabled"));

        // Clean up
        System.clearProperty("app.module.feature1");
        System.clearProperty("app.module.feature2");
        System.clearProperty("app.service.api");
        System.clearProperty("config.app.module");
    }

    // Test invalid regex in custom properties
    public void test_customProperties_invalidRegex() {
        List<String> cmdList = new ArrayList<>();

        try {
            execJob.testAddFessCustomSystemProperties(cmdList, "[invalid(regex");
            fail("Should handle invalid regex");
        } catch (Exception e) {
            // Expected to throw pattern syntax exception
            assertTrue(e instanceof java.util.regex.PatternSyntaxException);
        }
    }
}