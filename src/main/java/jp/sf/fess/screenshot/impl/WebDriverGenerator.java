package jp.sf.fess.screenshot.impl;

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

    private static final Logger logger = LoggerFactory
            .getLogger(WebDriverGenerator.class);

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
        webDriver.manage().window()
                .setSize(new Dimension(windowWidth, windowHeight));
    }

    @DestroyMethod
    public void destroy() {
        webDriver.close();
    }

    @Override
    public void generate(String url, File outputFile) {
        if (logger.isDebugEnabled()) {
            logger.debug("Generate ScreenShot: " + url);
        }

        if (outputFile.exists()) {
            if (logger.isDebugEnabled()) {
                logger.debug("The screenshot file exists: "
                        + outputFile.getAbsolutePath());
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
            final File screenshot = ((TakesScreenshot) webDriver)
                    .getScreenshotAs(OutputType.FILE);
            convert(screenshot, outputFile);
        } else {
            logger.warn("WebDriver is not instance of TakesScreenshot: "
                    + webDriver);
        }
    }

    protected void convert(File inputFile, File outputFile) {
        try {
            final BufferedImage image = loadImage(inputFile);
            final int screenShotHeight = screenShotWidth * image.getHeight()
                    / windowWidth;
            final BufferedImage screenShotImage = new BufferedImage(
                    screenShotWidth, screenShotHeight, image.getType());
            screenShotImage.getGraphics().drawImage(
                    image.getScaledInstance(screenShotWidth, screenShotHeight,
                            Image.SCALE_AREA_AVERAGING), 0, 0, screenShotWidth,
                    screenShotHeight, null);

            ImageIO.write(screenShotImage, imageFormatName, outputFile);
        } catch (final Exception e) {
            logger.warn("Failed to convert " + inputFile.getAbsolutePath(), e);
            inputFile.renameTo(outputFile);
        }
    }

    protected BufferedImage loadImage(File file) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            return ImageIO.read(in);
        }
    }

}
