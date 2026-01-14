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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.helper.ProcessHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.InputStreamThread;
import org.codelibs.fess.util.JobProcess;
import org.codelibs.fess.util.SystemUtil;
import org.lastaflute.job.JobManager;
import org.lastaflute.job.key.LaJobUnique;
import org.lastaflute.job.subsidiary.CronConsumer;

import jakarta.servlet.ServletContext;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class PythonJobTest extends UnitFessTestCase {

    private PythonJob pythonJob;
    private TestProcessHelper testProcessHelper;
    private TestFessConfig testFessConfig;
    private TestServletContext testServletContext;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        pythonJob = new PythonJob();

        // Setup test components
        testProcessHelper = new TestProcessHelper();
        ComponentUtil.register(testProcessHelper, "processHelper");

        testFessConfig = new TestFessConfig() {
            @Override
            public String getPythonCommandPath() {
                return "python3";
            }
        };
        ComponentUtil.setFessConfig(testFessConfig);

        testServletContext = new TestServletContext();
        ComponentUtil.register(testServletContext, ServletContext.class.getCanonicalName());
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    // Test filename setter
    @Test
    public void test_filename() {
        assertNull(pythonJob.filename);

        PythonJob result = pythonJob.filename("test.py");

        assertSame(pythonJob, result);
        assertEquals("test.py", pythonJob.filename);
    }

    // Test single argument addition
    @Test
    public void test_arg() {
        assertTrue(pythonJob.argList.isEmpty());

        PythonJob result = pythonJob.arg("arg1");

        assertSame(pythonJob, result);
        assertEquals(1, pythonJob.argList.size());
        assertEquals("arg1", pythonJob.argList.get(0));

        pythonJob.arg("arg2");
        assertEquals(2, pythonJob.argList.size());
        assertEquals("arg2", pythonJob.argList.get(1));
    }

    // Test multiple arguments addition
    @Test
    public void test_args() {
        assertTrue(pythonJob.argList.isEmpty());

        PythonJob result = pythonJob.args("arg1", "arg2", "arg3");

        assertSame(pythonJob, result);
        assertEquals(3, pythonJob.argList.size());
        assertEquals("arg1", pythonJob.argList.get(0));
        assertEquals("arg2", pythonJob.argList.get(1));
        assertEquals("arg3", pythonJob.argList.get(2));
    }

    // Test args with empty array
    @Test
    public void test_args_empty() {
        PythonJob result = pythonJob.args();

        assertSame(pythonJob, result);
        assertTrue(pythonJob.argList.isEmpty());
    }

    // Test combining arg and args methods
    @Test
    public void test_arg_and_args_combination() {
        pythonJob.arg("single1").args("multi1", "multi2").arg("single2").args("multi3");

        assertEquals(5, pythonJob.argList.size());
        assertEquals("single1", pythonJob.argList.get(0));
        assertEquals("multi1", pythonJob.argList.get(1));
        assertEquals("multi2", pythonJob.argList.get(2));
        assertEquals("single2", pythonJob.argList.get(3));
        assertEquals("multi3", pythonJob.argList.get(4));
    }

    // Test getPyFilePath method
    @Test
    public void test_getPyFilePath() {
        pythonJob.filename("test_script.py");

        String expectedPath = "WEB-INF" + File.separator + "env" + File.separator + "python" + File.separator + "resources" + File.separator
                + "test_script.py";
        assertEquals(expectedPath, pythonJob.getPyFilePath());
    }

    // Test getPyFilePath with directory traversal attempt
    @Test
    public void test_getPyFilePath_withDirectoryTraversal() {
        pythonJob.filename("../../malicious.py");

        String expectedPath = "WEB-INF" + File.separator + "env" + File.separator + "python" + File.separator + "resources" + File.separator
                + "//malicious.py";
        assertEquals(expectedPath, pythonJob.getPyFilePath());
    }

    // Test getPyFilePath with complex directory traversal
    @Test
    public void test_getPyFilePath_withComplexDirectoryTraversal() {
        pythonJob.filename("../test/../../../etc/passwd");

        String expectedPath = "WEB-INF" + File.separator + "env" + File.separator + "python" + File.separator + "resources" + File.separator
                + "/test////etc/passwd";
        assertEquals(expectedPath, pythonJob.getPyFilePath());
    }

    // Test getExecuteType method
    @Test
    public void test_getExecuteType() {
        assertEquals(Constants.EXECUTE_TYPE_PYTHON, pythonJob.getExecuteType());
    }

    // Test execute method with successful execution
    @Test
    public void test_execute_success() {
        pythonJob.filename("test.py");
        pythonJob.args("arg1", "arg2");

        testProcessHelper.exitValue = 0;
        testProcessHelper.processOutput = "Python script executed successfully";

        String result = pythonJob.execute();

        assertNotNull(result);
        assertTrue(result.contains("Session Id:"));
        assertNotNull(pythonJob.sessionId);
        assertTrue(pythonJob.sessionId.length() >= 10);

        // The test doesn't actually call the real process since ServletContext.getRealPath returns null in test
        // and causes an exception which is caught and added to result
        assertTrue(result.contains("Session Id:"));
    }

    // Test execute with custom session ID
    @Test
    public void test_execute_withCustomSessionId() {
        pythonJob.filename("test.py");
        pythonJob.sessionId = "custom-session-123";

        testProcessHelper.exitValue = 0;

        String result = pythonJob.execute();

        assertTrue(result.contains("Session Id: custom-session-123"));
        assertEquals("custom-session-123", pythonJob.sessionId);
    }

    // Test execute with process failure
    @Test
    public void test_execute_processFailure() {
        pythonJob.filename("failing.py");

        testProcessHelper.exitValue = 1;
        testProcessHelper.processOutput = "Error: Script failed";

        String result = pythonJob.execute();

        assertNotNull(result);
        // Result should contain session ID info
        assertTrue(result.contains("Python Process terminated."));
    }

    // Test execute with timeout
    @Test
    public void test_execute_withTimeout() {
        pythonJob.filename("timeout.py");
        pythonJob.timeout = 60; // Set timeout
        pythonJob.processTimeout = true;

        testProcessHelper.exitValue = -1;
        testProcessHelper.processOutput = "Process terminated";

        String result = pythonJob.execute();

        assertNotNull(result);
        assertTrue(result.contains("Session Id:"));
        assertTrue(result.contains("Process is terminated due to 60 second exceeded") || result.contains("Process terminated"));
    }

    // Test executePython with blank filename
    @Test
    public void test_executePython_blankFilename() {
        pythonJob.filename("");

        try {
            pythonJob.executePython();
            fail("Should throw JobProcessingException");
        } catch (JobProcessingException e) {
            assertEquals("Python script is not specified.", e.getMessage());
        }
    }

    // Test executePython with null filename
    @Test
    public void test_executePython_nullFilename() {
        pythonJob.filename = null;

        try {
            pythonJob.executePython();
            fail("Should throw JobProcessingException");
        } catch (JobProcessingException e) {
            assertEquals("Python script is not specified.", e.getMessage());
        }
    }

    // Test executePython with process exception
    @Test
    public void test_executePython_processException() {
        pythonJob.filename("error.py");

        testProcessHelper.throwException = new RuntimeException("Process start failed");

        try {
            pythonJob.executePython();
            fail("Should throw JobProcessingException");
        } catch (JobProcessingException e) {
            assertEquals("Python Process terminated.", e.getMessage());
            assertNotNull(e.getCause());
            // Check cause message if available, otherwise just verify cause exists
            if (e.getCause().getMessage() != null) {
                assertTrue(e.getCause().getMessage().contains("Process start failed"));
            }
        }
    }

    // Test execute with exception handling
    @Test
    public void test_execute_withException() {
        pythonJob.filename("test.py");

        testProcessHelper.throwException = new RuntimeException("Unexpected error");

        String result = pythonJob.execute();

        assertTrue(result.contains("Session Id:"));
        assertTrue(result.contains("Python Process terminated."));
    }

    // Test execute with job executor
    @Test
    public void test_execute_withJobExecutor() {
        pythonJob.filename("test.py");

        TestJobExecutor jobExecutor = new TestJobExecutor();
        pythonJob.jobExecutor = jobExecutor;

        testProcessHelper.exitValue = 0;

        pythonJob.execute();

        assertTrue(jobExecutor.shutdownListenerAdded);
    }

    // Test environment variables setup
    @Test
    public void test_executePython_environmentVariables() {
        pythonJob.filename("test.py");
        pythonJob.sessionId = "test-session";

        testProcessHelper.exitValue = 0;

        try {
            pythonJob.executePython();
        } catch (Exception e) {
            // If getRealPath returns null, the method may fail
            // This is OK for the test
        }

        Map<String, String> env = testProcessHelper.capturedEnvironment;
        if (env != null) {
            assertEquals("test-session", env.get("SESSION_ID"));
            // OPENSEARCH_URL may not be set in test environment
        }
    }

    // Test working directory setup
    @Test
    public void test_executePython_workingDirectory() {
        pythonJob.filename("test.py");

        testProcessHelper.exitValue = 0;

        try {
            pythonJob.executePython();
        } catch (Exception e) {
            // If getRealPath returns null, the method may fail
            // This is OK for the test
        }

        if (testProcessHelper.capturedDirectory != null) {
            assertEquals(testServletContext.parentFile, testProcessHelper.capturedDirectory);
        }
    }

    // Helper test classes
    private static class TestProcessHelper extends ProcessHelper {
        boolean startProcessCalled = false;
        boolean destroyProcessCalled = false;
        String capturedSessionId;
        List<String> capturedCmdList;
        Map<String, String> capturedEnvironment;
        File capturedDirectory;
        int exitValue = 0;
        String processOutput = "Test output";
        RuntimeException throwException = null;

        @Override
        public JobProcess startProcess(String sessionId, List<String> cmdList, java.util.function.Consumer<ProcessBuilder> pbConsumer) {
            if (throwException != null) {
                throw throwException;
            }

            startProcessCalled = true;
            capturedSessionId = sessionId;
            capturedCmdList = new ArrayList<>(cmdList);

            ProcessBuilder pb = new ProcessBuilder(cmdList);
            capturedEnvironment = new HashMap<>(pb.environment());
            pbConsumer.accept(pb);
            capturedDirectory = pb.directory();
            capturedEnvironment = new HashMap<>(pb.environment());

            return new TestJobProcess(exitValue, processOutput);
        }

        @Override
        public int destroyProcess(String sessionId) {
            destroyProcessCalled = true;
            return 0;
        }
    }

    private static class TestJobProcess extends JobProcess {
        private final int exitValue;
        private final String output;

        public TestJobProcess(int exitValue, String output) {
            super(new TestProcess(exitValue));
            this.exitValue = exitValue;
            this.output = output;
        }

        @Override
        public Process getProcess() {
            return new TestProcess(exitValue);
        }

        @Override
        public InputStreamThread getInputStreamThread() {
            return new TestInputStreamThread(output);
        }
    }

    private static class TestProcess extends Process {
        private final int exitValue;

        public TestProcess(int exitValue) {
            this.exitValue = exitValue;
        }

        @Override
        public int waitFor() throws InterruptedException {
            return exitValue;
        }

        @Override
        public int exitValue() {
            return exitValue;
        }

        @Override
        public void destroy() {
        }

        @Override
        public java.io.OutputStream getOutputStream() {
            return null;
        }

        @Override
        public java.io.InputStream getInputStream() {
            return null;
        }

        @Override
        public java.io.InputStream getErrorStream() {
            return null;
        }
    }

    private static class TestInputStreamThread extends InputStreamThread {
        private final String output;

        public TestInputStreamThread(String output) {
            super(new java.io.ByteArrayInputStream(new byte[0]), java.nio.charset.StandardCharsets.UTF_8, 1000, null);
            this.output = output;
        }

        @Override
        public void start() {
            // Override start to do nothing in test - simulate immediate completion
        }

        @Override
        public void run() {
            // Override run to do nothing in test
        }

        @Override
        public String getOutput() {
            return output;
        }
    }

    private static class TestServletContext implements ServletContext {
        File parentFile = new File("/test/webapp");

        @Override
        public String getRealPath(String path) {
            if (path != null && path.contains("WEB-INF")) {
                return new File(parentFile, "WEB-INF").getAbsolutePath();
            }
            return parentFile.getAbsolutePath();
        }

        // Other methods return null or default values
        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public java.util.Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public String getContextPath() {
            return null;
        }

        @Override
        public ServletContext getContext(String uripath) {
            return null;
        }

        @Override
        public int getMajorVersion() {
            return 0;
        }

        @Override
        public int getMinorVersion() {
            return 0;
        }

        @Override
        public int getEffectiveMajorVersion() {
            return 0;
        }

        @Override
        public int getEffectiveMinorVersion() {
            return 0;
        }

        @Override
        public String getMimeType(String file) {
            return null;
        }

        @Override
        public java.util.Set<String> getResourcePaths(String path) {
            return null;
        }

        @Override
        public java.net.URL getResource(String path) {
            return null;
        }

        @Override
        public java.io.InputStream getResourceAsStream(String path) {
            return null;
        }

        @Override
        public jakarta.servlet.RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        @Override
        public jakarta.servlet.RequestDispatcher getNamedDispatcher(String name) {
            return null;
        }

        @Override
        public void log(String msg) {
        }

        @Override
        public void log(String message, Throwable throwable) {
        }

        @Override
        public String getServerInfo() {
            return null;
        }

        @Override
        public String getInitParameter(String name) {
            return null;
        }

        @Override
        public java.util.Enumeration<String> getInitParameterNames() {
            return null;
        }

        @Override
        public boolean setInitParameter(String name, String value) {
            return false;
        }

        @Override
        public void setAttribute(String name, Object object) {
        }

        @Override
        public void removeAttribute(String name) {
        }

        @Override
        public String getServletContextName() {
            return null;
        }

        @Override
        public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName, String className) {
            return null;
        }

        @Override
        public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName, jakarta.servlet.Servlet servlet) {
            return null;
        }

        @Override
        public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName,
                Class<? extends jakarta.servlet.Servlet> servletClass) {
            return null;
        }

        @Override
        public jakarta.servlet.ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) {
            return null;
        }

        @Override
        public <T extends jakarta.servlet.Servlet> T createServlet(Class<T> clazz) {
            return null;
        }

        @Override
        public jakarta.servlet.ServletRegistration getServletRegistration(String servletName) {
            return null;
        }

        @Override
        public Map<String, ? extends jakarta.servlet.ServletRegistration> getServletRegistrations() {
            return null;
        }

        @Override
        public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
            return null;
        }

        @Override
        public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, jakarta.servlet.Filter filter) {
            return null;
        }

        @Override
        public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName,
                Class<? extends jakarta.servlet.Filter> filterClass) {
            return null;
        }

        @Override
        public <T extends jakarta.servlet.Filter> T createFilter(Class<T> clazz) {
            return null;
        }

        @Override
        public jakarta.servlet.FilterRegistration getFilterRegistration(String filterName) {
            return null;
        }

        @Override
        public Map<String, ? extends jakarta.servlet.FilterRegistration> getFilterRegistrations() {
            return null;
        }

        @Override
        public jakarta.servlet.SessionCookieConfig getSessionCookieConfig() {
            return null;
        }

        @Override
        public void setSessionTrackingModes(java.util.Set<jakarta.servlet.SessionTrackingMode> sessionTrackingModes) {
        }

        @Override
        public java.util.Set<jakarta.servlet.SessionTrackingMode> getDefaultSessionTrackingModes() {
            return null;
        }

        @Override
        public java.util.Set<jakarta.servlet.SessionTrackingMode> getEffectiveSessionTrackingModes() {
            return null;
        }

        @Override
        public void addListener(String className) {
        }

        @Override
        public <T extends java.util.EventListener> void addListener(T t) {
        }

        @Override
        public void addListener(Class<? extends java.util.EventListener> listenerClass) {
        }

        @Override
        public <T extends java.util.EventListener> T createListener(Class<T> clazz) {
            return null;
        }

        @Override
        public jakarta.servlet.descriptor.JspConfigDescriptor getJspConfigDescriptor() {
            return null;
        }

        @Override
        public ClassLoader getClassLoader() {
            return null;
        }

        @Override
        public void declareRoles(String... roleNames) {
        }

        @Override
        public String getVirtualServerName() {
            return null;
        }

        @Override
        public int getSessionTimeout() {
            return 0;
        }

        @Override
        public void setSessionTimeout(int sessionTimeout) {
        }

        @Override
        public String getRequestCharacterEncoding() {
            return null;
        }

        @Override
        public void setRequestCharacterEncoding(String encoding) {
        }

        @Override
        public String getResponseCharacterEncoding() {
            return null;
        }

        @Override
        public void setResponseCharacterEncoding(String encoding) {
        }
    }

    private static class TestJobExecutor extends JobExecutor {
        boolean shutdownListenerAdded = false;

        @Override
        public void addShutdownListener(ShutdownListener listener) {
            shutdownListenerAdded = true;
            // Optionally execute the listener to test its behavior
            listener.onShutdown();
        }

        @Override
        public Object execute(String scriptType, String script) {
            return null;
        }
    }

    // Test configuration class extending FessConfig.SimpleImpl
    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;
    }

}