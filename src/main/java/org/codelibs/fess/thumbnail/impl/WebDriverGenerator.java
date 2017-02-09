/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.thumbnail.impl;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.browserlaunchers.Proxies;
import org.openqa.selenium.os.CommandLine;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.phantomjs.PhantomJSDriverService.Builder;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.SessionNotFoundException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverGenerator extends BaseThumbnailGenerator {

    private static final Logger logger = LoggerFactory.getLogger(WebDriverGenerator.class);

    private static final String PHANTOMJS_DEFAULT_EXECUTABLE = "phantomjs";

    protected WebDriver webDriver;

    protected Capabilities webDriverCapabilities;

    protected long previousCheckTime = 0;

    @PostConstruct
    public void init() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.isThumbnailHtmlPhantomjsEnabled() && super.isAvailable()) {
            startWebDriver();
        }
    }

    protected void startWebDriver() {
        try {
            if (webDriver == null) {
                if (webDriverCapabilities == null) {
                    webDriver = new PhantomJSDriver();
                } else {
                    if (webDriverCapabilities instanceof DesiredCapabilities) {
                        final DesiredCapabilities capabilities = (DesiredCapabilities) webDriverCapabilities;
                        webDriverCapabilities.asMap().entrySet().stream()
                                .filter(e -> e.getValue() instanceof String && filePathMap.containsKey(e.getValue().toString()))
                                .forEach(e -> capabilities.setCapability(e.getKey(), filePathMap.get(e.getValue().toString())));
                    }
                    webDriver = new PhantomJSDriver(createDriverService(webDriverCapabilities), webDriverCapabilities);
                }
            }
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            webDriver
                    .manage()
                    .window()
                    .setSize(
                            new Dimension(fessConfig.getThumbnailHtmlPhantomjsWindowWidthAsInteger(), fessConfig
                                    .getThumbnailHtmlPhantomjsWindowHeightAsInteger()));
            previousCheckTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("WebDriver is not available for generating thumbnails.", e);
            } else {
                logger.info("WebDriver is not available for generating thumbnails.");
            }
        }
    }

    @Override
    public void destroy() {
        if (webDriver != null) {
            synchronized (this) {
                try {
                    webDriver.quit();
                } catch (final Throwable t) {
                    logger.debug("Failed to quit webDriver.", t);
                }
                webDriver = null;
            }
        }
    }

    @Override
    public boolean generate(final String url, final File outputFile) {
        if (logger.isDebugEnabled()) {
            logger.debug("Generate Thumbnail: " + url);
        }

        if (outputFile.exists()) {
            if (logger.isDebugEnabled()) {
                logger.debug("The thumbnail file exists: " + outputFile.getAbsolutePath());
            }
            return true;
        }

        final File parentFile = outputFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!parentFile.isDirectory()) {
            logger.warn("Not found: " + parentFile.getAbsolutePath());
            return false;
        }

        if (webDriver instanceof TakesScreenshot) {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            synchronized (this) {
                try {
                    webDriver.get(url);
                    if (webDriver instanceof JavascriptExecutor) {
                        Dimension dim = webDriver.findElement(By.tagName("body")).getSize();
                        if (dim.height >= fessConfig.getThumbnailHtmlPhantomjsMaxHeightAsInteger()) {
                            if (logger.isInfoEnabled()) {
                                logger.info("Skpped Thumbnail generation " + dim + " for " + url);
                            }
                            return false;
                        }
                    }
                    final File thumbnail = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
                    convert(thumbnail, outputFile);
                    return true;
                } catch (final UnreachableBrowserException | SessionNotFoundException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("WebDriver is not available.", e);
                    }
                    previousCheckTime = 0;
                } finally {
                    final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
                    if (now - previousCheckTime > fessConfig.getThumbnailHtmlPhantomjsKeepAliveAsInteger().longValue()) {
                        destroy();
                        startWebDriver();
                    }
                }
            }
        } else {
            logger.warn("WebDriver is not instance of TakesScreenshot: " + webDriver);
        }
        return false;
    }

    @Override
    public boolean isAvailable() {
        if (webDriver == null) {
            return false;
        }
        return super.isAvailable();
    }

    protected void convert(final File inputFile, final File outputFile) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        try (ImageInputStream input = ImageIO.createImageInputStream(inputFile)) {
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (readers.hasNext()) {
                final ImageReader reader = readers.next();
                try {
                    reader.setInput(input);
                    final ImageReadParam param = reader.getDefaultReadParam();
                    final int samplingWidth = reader.getWidth(0) / fessConfig.getThumbnailHtmlPhantomjsThumbnailWidthAsInteger();
                    final int samplingHeight = reader.getHeight(0) / fessConfig.getThumbnailHtmlPhantomjsThumbnailHeightAsInteger();
                    param.setSourceSubsampling(samplingWidth, samplingHeight, 0, 0);
                    param.setSourceRegion(new Rectangle(fessConfig.getThumbnailHtmlPhantomjsWindowWidthAsInteger(), fessConfig
                            .getThumbnailHtmlPhantomjsThumbnailHeightAsInteger()
                            * reader.getHeight(0)
                            / fessConfig.getThumbnailHtmlPhantomjsThumbnailWidthAsInteger()));
                    final BufferedImage image = reader.read(0, param);
                    ImageIO.write(image, fessConfig.getThumbnailHtmlPhantomjsFormat(), outputFile);
                    image.flush();
                } finally {
                    reader.dispose();
                }
            }
        } catch (final Throwable t) {
            logger.warn("Failed to convert " + inputFile.getAbsolutePath(), t);
            inputFile.renameTo(outputFile);
        }
    }

    protected BufferedImage loadImage(final File file) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            return ImageIO.read(in);
        }
    }

    @SuppressWarnings("deprecation")
    protected PhantomJSDriverService createDriverService(final Capabilities desiredCapabilities) {
        // Look for Proxy configuration within the Capabilities
        Proxy proxy = null;
        if (desiredCapabilities != null) {
            proxy = Proxies.extractProxy(desiredCapabilities);
        }

        // Find PhantomJS executable
        String phantomjspath = null;
        if (desiredCapabilities != null
                && desiredCapabilities.getCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY) != null) {
            phantomjspath = (String) desiredCapabilities.getCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY);
        } else {
            phantomjspath = CommandLine.find(PHANTOMJS_DEFAULT_EXECUTABLE);
            phantomjspath = System.getProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjspath);
        }

        final File phantomjsfile = new File(phantomjspath);

        // Build & return service
        return new Builder()//
                .usingPhantomJSExecutable(phantomjsfile).usingAnyFreePort()//
                .withProxy(proxy)//
                .withLogFile(new File(ComponentUtil.getSystemHelper().getLogFilePath(), "phantomjs.log"))//
                .usingCommandLineArguments(findCLIArgumentsFromCaps(desiredCapabilities, PhantomJSDriverService.PHANTOMJS_CLI_ARGS))//
                .build();
    }

    private static String[] findCLIArgumentsFromCaps(final Capabilities desiredCapabilities, final String capabilityName) {
        if (desiredCapabilities != null) {
            final Object cap = desiredCapabilities.getCapability(capabilityName);
            if (cap != null) {
                if (cap instanceof String[]) {
                    return (String[]) cap;
                } else if (cap instanceof Collection) {
                    try {
                        @SuppressWarnings("unchecked")
                        final Collection<String> capCollection = (Collection<String>) cap;
                        return capCollection.toArray(new String[capCollection.size()]);
                    } catch (final Exception e) {
                        // If casting fails, log an error and assume no CLI arguments are provided
                        logger.warn(String.format("Unable to set Capability '%s' as it was neither a String[] or a Collection<String>",
                                capabilityName));
                    }
                }
            }
        }
        return new String[] {}; // nothing found: return an empty array of arguments
    }

    public void setWebDriver(final WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void setWebDriverCapabilities(final Capabilities webDriverCapabilities) {
        this.webDriverCapabilities = webDriverCapabilities;
    }

}
