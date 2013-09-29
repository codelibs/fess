/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.helper;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletContext;

import jp.sf.fess.Constants;
import jp.sf.fess.FessSystemException;
import jp.sf.fess.db.exentity.RoleType;
import jp.sf.fess.exec.Crawler;
import jp.sf.fess.job.JobExecutor;
import jp.sf.fess.service.RoleTypeService;
import jp.sf.fess.util.InputStreamThread;
import jp.sf.fess.util.ResourceUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.codelibs.solr.lib.SolrGroup;
import org.codelibs.solr.lib.policy.QueryType;
import org.codelibs.solr.lib.policy.StatusPolicy;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.util.FileUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.util.CharUtil;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ServletContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory
            .getLogger(SystemHelper.class);

    private final ConcurrentHashMap<String, Process> runningProcessMap = new ConcurrentHashMap<String, Process>();

    private final ConcurrentHashMap<Long, JobExecutor> runningJobExecutorMap = new ConcurrentHashMap<Long, JobExecutor>();

    private String adminRole = "fess";

    private String[] crawlerJavaOptions = new String[] {
            "-Djava.awt.headless=true", "-server", "-Xmx512m",
            "-XX:MaxPermSize=128m", "-XX:-UseGCOverheadLimit",
            "-XX:+UseConcMarkSweepGC", "-XX:CMSInitiatingOccupancyFraction=75",
            "-XX:+CMSIncrementalMode", "-XX:+CMSIncrementalPacing",
            "-XX:CMSIncrementalDutyCycleMin=0", "-XX:+UseParNewGC",
            "-XX:+UseStringCache", "-XX:+UseTLAB", "-XX:+DisableExplicitGC" };

    private String logFilePath = System.getProperty("fess.log.file");

    private String solrHome = System.getProperty("solr.solr.home");

    private String solrDataDirName = "fess.solr.data.dir";

    private String javaCommandPath = "java";

    private String filterPathEncoding = Constants.UTF_8;

    private boolean useOwnTmpDir = true;

    private String baseHelpLink = "http://fess.codelibs.org/{lang}/"
            + Constants.MAJOR_VERSION + ".0/admin/";

    private String[] supportedHelpLangs = new String[] { "ja" };

    private final Map<String, String> designJspFileNameMap = new HashMap<String, String>();

    private String[] supportedUploadedJSExtentions = new String[] { "js" };

    private String[] supportedUploadedCssExtentions = new String[] { "css" };

    private String[] supportedUploadedMediaExtentions = new String[] { "jpg",
            "jpeg", "gif", "png", "swf" };

    private String jarDir = "/jar/";

    private String launcherFileNamePrefix = "fess-launcher-";

    private String launcherJarPath;

    private String launcherJnlpPath;

    private final AtomicBoolean forceStop = new AtomicBoolean(false);

    @InitMethod
    public void init() {
        final File[] files = ResourceUtil.getJarFiles(launcherFileNamePrefix);
        if (files.length > 0) {
            final String fileName = files[0].getName();
            final String jarPath = ServletContextUtil.getServletContext()
                    .getRealPath(jarDir);
            final File[] jarFiles = new File(jarPath)
                    .listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(final File dir, final String name) {
                            return name.startsWith(launcherFileNamePrefix);
                        }
                    });
            if (jarFiles != null) {
                for (final File jarFile : jarFiles) {
                    if (jarFile.exists() && !jarFile.delete()) {
                        logger.warn("Could not delete "
                                + jarFile.getAbsolutePath());
                    }
                }
            }
            final File launcherJarFile = new File(jarPath, fileName);
            final File parentLauncherJarFile = launcherJarFile.getParentFile();
            if (!parentLauncherJarFile.exists()
                    && !parentLauncherJarFile.mkdirs()) {
                logger.warn("Could not create "
                        + parentLauncherJarFile.getAbsolutePath());
            }
            FileUtil.copy(files[0], launcherJarFile);
            launcherJarPath = jarDir + fileName;
            launcherJnlpPath = launcherJarPath.replace(".jar", ".jnlp");
            final File launcherJnlpFile = new File(launcherJarFile
                    .getAbsolutePath().replace(".jar", ".jnlp"));
            final File jnlpTemplateFile = new File(
                    ResourceUtil.getOrigPath("jnlp/fess-launcher.jnlp"));
            if (!jnlpTemplateFile.isFile()) {
                throw new FessSystemException(
                        jnlpTemplateFile.getAbsolutePath() + " is not found.");
            }
            try {
                String content = new String(
                        FileUtil.getBytes(jnlpTemplateFile), Constants.UTF_8);
                content = content.replace("${launcherJarFile}", fileName);
                FileUtil.write(launcherJnlpFile.getAbsolutePath(),
                        content.getBytes(Constants.UTF_8));
            } catch (final UnsupportedEncodingException e) {
                throw new FessSystemException("Could not write "
                        + jnlpTemplateFile.getAbsolutePath(), e);
            }
        }
    }

    @DestroyMethod
    public void destroy() {
        for (final String sessionId : runningProcessMap.keySet()) {
            destroyCrawlerProcess(sessionId);
        }
    }

    public void executeCrawler(final String sessionId,
            final String[] webConfigIds, final String[] fileConfigIds,
            final String[] dataConfigIds, final String operation) {
        final List<String> crawlerCmdList = new ArrayList<String>();
        final String cpSeparator = SystemUtils.IS_OS_WINDOWS ? ";" : ":";
        final ServletContext servletContext = SingletonS2Container
                .getComponent(ServletContext.class);

        crawlerCmdList.add(javaCommandPath);

        // -cp
        crawlerCmdList.add("-cp");
        final StringBuilder buf = new StringBuilder();
        // WEB-INF/cmd/resources
        buf.append("WEB-INF");
        buf.append(File.separator);
        buf.append("cmd");
        buf.append(File.separator);
        buf.append("resources");
        buf.append(cpSeparator);
        // WEB-INF/classes
        buf.append("WEB-INF");
        buf.append(File.separator);
        buf.append("classes");
        // WEB-INF/lib
        appendJarFile(cpSeparator, servletContext, buf, "/WEB-INF/lib",
                "WEB-INF" + File.separator + "lib" + File.separator);
        // WEB-INF/cmd/lib
        appendJarFile(cpSeparator, servletContext, buf, "/WEB-INF/cmd/lib",
                "WEB-INF" + File.separator + "cmd" + File.separator + "lib"
                        + File.separator);
        crawlerCmdList.add(buf.toString());

        final String solrDataDir = System.getProperty(solrDataDirName);

        crawlerCmdList.add("-Dfess.crawler.process=true");
        crawlerCmdList.add("-Dsolr.solr.home=" + solrHome);
        if (solrDataDir != null) {
            crawlerCmdList.add("-Dsolr.data.dir=" + solrDataDir);
        } else {
            logger.warn("-D" + solrDataDirName + " is not found.");
        }
        crawlerCmdList.add("-Dfess.log.file=" + logFilePath);
        if (crawlerJavaOptions != null) {
            for (final String value : crawlerJavaOptions) {
                crawlerCmdList.add(value);
            }
        }

        File ownTmpDir = null;
        if (useOwnTmpDir) {
            final String tmpDir = System.getProperty("java.io.tmpdir");
            if (StringUtil.isNotBlank(tmpDir)) {
                ownTmpDir = new File(tmpDir, "fessTmpDir_" + sessionId);
                if (ownTmpDir.mkdirs()) {
                    crawlerCmdList.add("-Djava.io.tmpdir="
                            + ownTmpDir.getAbsolutePath());
                } else {
                    ownTmpDir = null;
                }
            }
        }

        crawlerCmdList.add(Crawler.class.getCanonicalName());

        crawlerCmdList.add("--sessionId");
        crawlerCmdList.add(sessionId);
        crawlerCmdList.add("--name");
        crawlerCmdList.add(Constants.CRAWLING_SESSION_SYSTEM_NAME);

        if (webConfigIds != null && webConfigIds.length > 0) {
            crawlerCmdList.add("-w");
            crawlerCmdList.add(StringUtils.join(webConfigIds, ','));
        }
        if (fileConfigIds != null && fileConfigIds.length > 0) {
            crawlerCmdList.add("-f");
            crawlerCmdList.add(StringUtils.join(fileConfigIds, ','));
        }
        if (dataConfigIds != null && dataConfigIds.length > 0) {
            crawlerCmdList.add("-d");
            crawlerCmdList.add(StringUtils.join(dataConfigIds, ','));
        }
        if (StringUtil.isNotBlank(operation)) {
            crawlerCmdList.add("-o");
            crawlerCmdList.add(operation);
        }

        final File baseDir = new File(servletContext.getRealPath("/"));

        if (logger.isInfoEnabled()) {
            logger.info("Crawler: \nDirectory=" + baseDir + "\nOptions="
                    + crawlerCmdList);
        }

        final ProcessBuilder pb = new ProcessBuilder(crawlerCmdList);
        pb.directory(baseDir);
        pb.redirectErrorStream(true);

        destroyCrawlerProcess(sessionId);

        try {
            final Process currentProcess = pb.start();
            destroyCrawlerProcess(runningProcessMap.putIfAbsent(sessionId,
                    currentProcess));

            final InputStreamThread it = new InputStreamThread(
                    currentProcess.getInputStream(), Constants.UTF_8);
            it.start();

            currentProcess.waitFor();
            it.join(5000);

            final int exitValue = currentProcess.exitValue();

            if (logger.isInfoEnabled()) {
                logger.info("Crawler: Exit Code=" + exitValue
                        + " - Crawler Process Output:\n" + it.getOutput());
            }
        } catch (final InterruptedException e) {
            logger.warn("Crawler Process interrupted.");
        } catch (final Exception e) {
            throw new FessSystemException("Crawler Process terminated.", e);
        } finally {
            destroyCrawlerProcess(sessionId);
            if (ownTmpDir != null && !ownTmpDir.delete()) {
                logger.warn("Could not delete a temp dir: "
                        + ownTmpDir.getAbsolutePath());
            }
        }
    }

    public void destroyCrawlerProcess(final String sessionId) {
        final Process process = runningProcessMap.remove(sessionId);
        destroyCrawlerProcess(process);
    }

    protected void destroyCrawlerProcess(final Process process) {
        if (process != null) {
            try {
                IOUtils.closeQuietly(process.getInputStream());
            } catch (final Exception e) {
            }
            try {
                IOUtils.closeQuietly(process.getErrorStream());
            } catch (final Exception e) {
            }
            try {
                IOUtils.closeQuietly(process.getOutputStream());
            } catch (final Exception e) {
            }
            try {
                process.destroy();
            } catch (final Exception e) {
            }
        }
    }

    private void appendJarFile(final String cpSeparator,
            final ServletContext servletContext, final StringBuilder buf,
            final String libDirPath, final String basePath) {
        final File libDir = new File(servletContext.getRealPath(libDirPath));
        final File[] jarFiles = libDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.toLowerCase().endsWith(".jar");
            }
        });
        if (jarFiles != null) {
            for (final File file : jarFiles) {
                buf.append(cpSeparator);
                buf.append(basePath);
                buf.append(file.getName());
            }
        }
    }

    public String getUsername() {
        String username = RequestUtil.getRequest().getRemoteUser();
        if (StringUtil.isBlank(username)) {
            username = "guest";
        }
        return username;
    }

    public Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public boolean isCrawlProcessRunning() {
        return !runningProcessMap.isEmpty();
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(final String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public String encodeUrlFilter(final String path) {
        if (filterPathEncoding == null || path == null) {
            return path;
        }

        try {
            final StringBuilder buf = new StringBuilder();
            for (int i = 0; i < path.length(); i++) {
                final char c = path.charAt(i);
                if (CharUtil.isUrlChar(c) || c == '^' || c == '{' || c == '}'
                        || c == '|' || c == '\\') {
                    buf.append(c);
                } else {
                    buf.append(URLEncoder.encode(String.valueOf(c),
                            filterPathEncoding));
                }
            }
            return buf.toString();
        } catch (final UnsupportedEncodingException e) {
            return path;
        }
    }

    public String getHelpLink(final String name) {
        final Locale locale = RequestUtil.getRequest().getLocale();
        if (locale != null) {
            final String lang = locale.getLanguage();
            for (final String l : supportedHelpLangs) {
                if (l.equals(lang)) {
                    final String url = baseHelpLink + name + "-guide.html";
                    return url.replaceAll("\\{lang\\}", lang);
                }
            }
        }
        return null;
    }

    public void addDesignJspFileName(final String key, final String value) {
        designJspFileNameMap.put(key, value);
    }

    public String getDesignJspFileName(final String fileName) {
        return designJspFileNameMap.get(fileName);
    }

    public String getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(final String adminRole) {
        this.adminRole = adminRole;
    }

    public List<String> getAuthenticatedRoleList() {
        final RoleTypeService roleTypeService = SingletonS2Container
                .getComponent(RoleTypeService.class);
        final List<RoleType> roleTypeList = roleTypeService.getRoleTypeList();
        final List<String> roleList = new ArrayList<String>(roleTypeList.size());
        for (final RoleType roleType : roleTypeList) {
            roleList.add(roleType.getValue());
        }
        return roleList;
    }

    public String[] getCrawlerJavaOptions() {
        return crawlerJavaOptions;
    }

    public void setCrawlerJavaOptions(final String[] crawlerJavaOptions) {
        this.crawlerJavaOptions = crawlerJavaOptions;
    }

    public String getSolrHome() {
        return solrHome;
    }

    public void setSolrHome(final String solrHome) {
        this.solrHome = solrHome;
    }

    public String getSolrDataDirName() {
        return solrDataDirName;
    }

    public void setSolrDataDirName(final String solrDataDirName) {
        this.solrDataDirName = solrDataDirName;
    }

    public String getJavaCommandPath() {
        return javaCommandPath;
    }

    public void setJavaCommandPath(final String javaCommandPath) {
        this.javaCommandPath = javaCommandPath;
    }

    /**
     * @return the filterPathEncoding
     */
    public String getFilterPathEncoding() {
        return filterPathEncoding;
    }

    /**
     * @param filterPathEncoding the filterPathEncoding to set
     */
    public void setFilterPathEncoding(final String filterPathEncoding) {
        this.filterPathEncoding = filterPathEncoding;
    }

    /**
     * @return the useOwnTmpDir
     */
    public boolean isUseOwnTmpDir() {
        return useOwnTmpDir;
    }

    /**
     * @param useOwnTmpDir the useOwnTmpDir to set
     */
    public void setUseOwnTmpDir(final boolean useOwnTmpDir) {
        this.useOwnTmpDir = useOwnTmpDir;
    }

    /**
     * @return the baseHelpLink
     */
    public String getBaseHelpLink() {
        return baseHelpLink;
    }

    /**
     * @param baseHelpLink the baseHelpLink to set
     */
    public void setBaseHelpLink(final String baseHelpLink) {
        this.baseHelpLink = baseHelpLink;
    }

    /**
     * @return the supportedHelpLangs
     */
    public String[] getSupportedHelpLangs() {
        return supportedHelpLangs;
    }

    /**
     * @param supportedHelpLangs the supportedHelpLangs to set
     */
    public void setSupportedHelpLangs(final String[] supportedHelpLangs) {
        this.supportedHelpLangs = supportedHelpLangs;
    }

    public String[] getSupportedUploadedJSExtentions() {
        return supportedUploadedJSExtentions;
    }

    public void setSupportedUploadedJSExtentions(
            final String[] supportedUploadedJSExtentions) {
        this.supportedUploadedJSExtentions = supportedUploadedJSExtentions;
    }

    public String[] getSupportedUploadedCssExtentions() {
        return supportedUploadedCssExtentions;
    }

    public void setSupportedUploadedCssExtentions(
            final String[] supportedUploadedCssExtentions) {
        this.supportedUploadedCssExtentions = supportedUploadedCssExtentions;
    }

    public String[] getSupportedUploadedMediaExtentions() {
        return supportedUploadedMediaExtentions;
    }

    public void setSupportedUploadedMediaExtentions(
            final String[] supportedUploadedMediaExtentions) {
        this.supportedUploadedMediaExtentions = supportedUploadedMediaExtentions;
    }

    public String getJarDir() {
        return jarDir;
    }

    public void setJarDir(final String jarDir) {
        this.jarDir = jarDir;
    }

    public String getLauncherFileNamePrefix() {
        return launcherFileNamePrefix;
    }

    public void setLauncherFileNamePrefix(final String launcherFileNamePrefix) {
        this.launcherFileNamePrefix = launcherFileNamePrefix;
    }

    public String getLauncherJarPath() {
        return launcherJarPath;
    }

    public String getLauncherJnlpPath() {
        return launcherJnlpPath;
    }

    public void updateStatus(final SolrGroup solrGroup,
            final QueryType queryType) {
        final StatusPolicy statusPolicy = solrGroup.getStatusPolicy();
        for (final String serverName : solrGroup.getServerNames()) {
            statusPolicy.activate(queryType, serverName);
        }

    }

    public Set<String> getRunningSessionIdSet() {
        return runningProcessMap.keySet();
    }

    public boolean isForceStop() {
        return forceStop.get();
    }

    public void setForceStop(final boolean b) {
        forceStop.set(true);
    }

    public void addRunningJobExecutoer(final Long id,
            final JobExecutor jobExecutor) {
        runningJobExecutorMap.put(id, jobExecutor);
    }

    public void removeRunningJobExecutoer(final Long id) {
        runningJobExecutorMap.remove(id);
    }

    public boolean isRunningJobExecutoer(final Long id) {
        return runningJobExecutorMap.containsKey(id);
    }
}
