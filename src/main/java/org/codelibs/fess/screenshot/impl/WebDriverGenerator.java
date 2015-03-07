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

package org.codelibs.fess.screenshot.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverGenerator extends BaseScreenShotGenerator {

    private static final Logger logger = LoggerFactory.getLogger(WebDriverGenerator.class);

    public WebDriver webDriver;

    public int windowWidth = 1200;

    public int windowHeight = 800;

    public int screenShotWidth = 400;

    public String imageFormatName = "png";

    @InitMethod
    public void init() {
        if (webDriver == null) {
            webDriver = new PhantomJSDriver();
        }
        webDriver.manage().window().setSize(new Dimension(windowWidth, windowHeight));
    }

    @DestroyMethod
    public void destroy() {
        webDriver.close();
    }

    @Override
    public void generate(final String url, final File outputFile) {
        if (logger.isDebugEnabled()) {
            logger.debug("Generate ScreenShot: " + url);
        }

        if (outputFile.exists()) {
            if (logger.isDebugEnabled()) {
                logger.debug("The screenshot file exists: " + outputFile.getAbsolutePath());
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
            final File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            convert(screenshot, outputFile);
        } else {
            logger.warn("WebDriver is not instance of TakesScreenshot: " + webDriver);
        }
    }

    protected void convert(final File inputFile, final File outputFile) {
        try {
            final BufferedImage image = loadImage(inputFile);
            final int screenShotHeight = screenShotWidth * image.getHeight() / windowWidth;
            final BufferedImage screenShotImage = new BufferedImage(screenShotWidth, screenShotHeight, image.getType());
            screenShotImage.getGraphics().drawImage(image.getScaledInstance(screenShotWidth, screenShotHeight, Image.SCALE_AREA_AVERAGING),
                    0, 0, screenShotWidth, screenShotHeight, null);

            ImageIO.write(screenShotImage, imageFormatName, outputFile);
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
