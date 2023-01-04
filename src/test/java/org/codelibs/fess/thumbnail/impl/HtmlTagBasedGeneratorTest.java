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
package org.codelibs.fess.thumbnail.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.unit.UnitFessTestCase;

public class HtmlTagBasedGeneratorTest extends UnitFessTestCase {
    private static final Logger logger = LogManager.getLogger(HtmlTagBasedGeneratorTest.class);

    public void test_saveImage() throws Exception {
        HtmlTagBasedGenerator generator = new HtmlTagBasedGenerator();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File outputFile = File.createTempFile("generator_", ".png");

        String imagePath = "thumbnail/600x400.png";
        try (ImageInputStream input = ImageIO.createImageInputStream(classLoader.getResourceAsStream(imagePath))) {
            generator.saveImage(input, outputFile);
        }
        assertImageSize(outputFile, 100, 66);

        imagePath = "thumbnail/600x400.gif";
        try (ImageInputStream input = ImageIO.createImageInputStream(classLoader.getResourceAsStream(imagePath))) {
            generator.saveImage(input, outputFile);
        }
        assertImageSize(outputFile, 100, 66);

        imagePath = "thumbnail/600x400.jpg";
        try (ImageInputStream input = ImageIO.createImageInputStream(classLoader.getResourceAsStream(imagePath))) {
            generator.saveImage(input, outputFile);
        }
        assertImageSize(outputFile, 100, 66);

        imagePath = "thumbnail/400x400.png";
        try (ImageInputStream input = ImageIO.createImageInputStream(classLoader.getResourceAsStream(imagePath))) {
            generator.saveImage(input, outputFile);
        }
        assertImageSize(outputFile, 100, 100);

        imagePath = "thumbnail/400x400.gif";
        try (ImageInputStream input = ImageIO.createImageInputStream(classLoader.getResourceAsStream(imagePath))) {
            generator.saveImage(input, outputFile);
        }
        assertImageSize(outputFile, 100, 100);

        imagePath = "thumbnail/400x400.jpg";
        try (ImageInputStream input = ImageIO.createImageInputStream(classLoader.getResourceAsStream(imagePath))) {
            generator.saveImage(input, outputFile);
        }
        assertImageSize(outputFile, 100, 100);

        imagePath = "thumbnail/400x600.png";
        try (ImageInputStream input = ImageIO.createImageInputStream(classLoader.getResourceAsStream(imagePath))) {
            generator.saveImage(input, outputFile);
        }
        assertImageSize(outputFile, 100, 100);

        imagePath = "thumbnail/400x600.gif";
        try (ImageInputStream input = ImageIO.createImageInputStream(classLoader.getResourceAsStream(imagePath))) {
            generator.saveImage(input, outputFile);
        }
        assertImageSize(outputFile, 100, 100);

        imagePath = "thumbnail/400x600.jpg";
        try (ImageInputStream input = ImageIO.createImageInputStream(classLoader.getResourceAsStream(imagePath))) {
            generator.saveImage(input, outputFile);
        }
        assertImageSize(outputFile, 100, 100);

    }

    private void assertImageSize(File file, int width, int height) throws IOException {
        BufferedImage img = ImageIO.read(file);
        logger.debug("width: {}, height: {}", img.getWidth(), img.getHeight());
        assertEquals("Image Width", width, img.getWidth());
        assertEquals("Image Height", height, img.getHeight());
    }
}
