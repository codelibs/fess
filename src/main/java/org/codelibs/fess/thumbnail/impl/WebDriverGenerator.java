/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.imageio.ImageIO;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverGenerator extends BaseThumbnailGenerator {

    private static final Logger logger = LoggerFactory.getLogger(WebDriverGenerator.class);

    public WebDriver webDriver;

    public Capabilities webDriverCapabilities;

    public int windowWidth = 1200;

    public int windowHeight = 800;

    public int thumbnailWidth = 400;

    public String imageFormatName = "png";

    @PostConstruct
    public void init() {
        try {
            if (webDriver == null) {
                webDriver = webDriverCapabilities == null ? new PhantomJSDriver() : new PhantomJSDriver(webDriverCapabilities);
            }
            webDriver.manage().window().setSize(new Dimension(windowWidth, windowHeight));
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("WebDriver is not available for generating thumbnails.", e);
            } else {
                logger.info("WebDriver is not available for generating thumbnails.");
            }
        }
    }

    @PreDestroy
    public void destroy() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

    @Override
    public void generate(final String url, final File outputFile) {
        if (logger.isDebugEnabled()) {
            logger.debug("Generate Thumbnail: " + url);
        }

        if (outputFile.exists()) {
            if (logger.isDebugEnabled()) {
                logger.debug("The thumbnail file exists: " + outputFile.getAbsolutePath());
            }
            return;
        }

        final File parentFile = outputFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!parentFile.isDirectory()) {
            logger.warn("Not found: " + parentFile.getAbsolutePath());
            return;
        }

        if (webDriver instanceof TakesScreenshot) {
            webDriver.get(url);
            final File thumbnail = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            convert(thumbnail, outputFile);
        } else {
            logger.warn("WebDriver is not instance of TakesScreenshot: " + webDriver);
        }
    }

    @Override
    public boolean isAvailable() {
        if (webDriver == null) {
            return false;
        }
        return super.isAvailable();
    }

    protected void convert(final File inputFile, final File outputFile) {
        try {
            final BufferedImage image = loadImage(inputFile);
            final int thumbnailHeight = thumbnailWidth * image.getHeight() / windowWidth;
            final BufferedImage thumbnailImage = new BufferedImage(thumbnailWidth, thumbnailHeight, image.getType());
            thumbnailImage.getGraphics().drawImage(image.getScaledInstance(thumbnailWidth, thumbnailHeight, Image.SCALE_AREA_AVERAGING), 0,
                    0, thumbnailWidth, thumbnailHeight, null);

            ImageIO.write(thumbnailImage, imageFormatName, outputFile);
        } catch (final Exception e) {
            logger.warn("Failed to convert " + inputFile.getAbsolutePath(), e);
            inputFile.renameTo(outputFile);
        }
    }

    protected BufferedImage loadImage(final File file) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            return ImageIO.read(in);
        }
    }

}
