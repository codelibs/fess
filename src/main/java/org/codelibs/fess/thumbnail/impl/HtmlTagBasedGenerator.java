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

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;

public class HtmlTagBasedGenerator extends BaseThumbnailGenerator {

    private static final Logger logger = LogManager.getLogger(HtmlTagBasedGenerator.class);

    @Override
    public void destroy() {
    }

    @Override
    public Tuple3<String, String, String> createTask(final String path, final Map<String, Object> docMap) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String thumbnailId = DocumentUtil.getValue(docMap, fessConfig.getIndexFieldId(), String.class);
        final Tuple3<String, String, String> task = new Tuple3<>(getName(), thumbnailId, path);
        if (logger.isDebugEnabled()) {
            logger.debug("Create thumbnail task: {}", task);
        }
        return task;
    }

    @Override
    public boolean generate(final String thumbnailId, final File outputFile) {
        if (logger.isDebugEnabled()) {
            logger.debug("Generate Thumbnail: {}", thumbnailId);
        }

        if (outputFile.exists()) {
            if (logger.isDebugEnabled()) {
                logger.debug("The thumbnail file exists: {}", outputFile.getAbsolutePath());
            }
            return true;
        }

        final File parentFile = outputFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!parentFile.isDirectory()) {
            logger.warn("Not found: {}", parentFile.getAbsolutePath());
            return false;
        }

        return process(thumbnailId, responseData -> {
            if (!isImageMimeType(responseData)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Thumbnail is not image: {} : {}", thumbnailId, responseData.getUrl());
                }
                updateThumbnailField(thumbnailId, StringUtil.EMPTY);
                return false;
            }
            boolean created = false;
            try (ImageInputStream input = ImageIO.createImageInputStream(responseData.getResponseBody())) {
                switch (saveImage(input, outputFile)) {
                case OK:
                    created = true;
                    break;
                case FAILED:
                    logger.warn("Failed to create thumbnail: {} -> {}", thumbnailId, responseData.getUrl());
                    break;
                case INVALID_SIZE:
                    logger.info("Unmatched thumbnail size: {} -> {}", thumbnailId, responseData.getUrl());
                    break;
                case NO_IMAGE:
                    if (logger.isDebugEnabled()) {
                        logger.debug("No thumbnail: {} -> {}", thumbnailId, responseData.getUrl());
                    }
                    break;
                default:
                    logger.error("Unknown thumbnail result: {} -> {}", thumbnailId, responseData.getUrl());
                    break;
                }
            } catch (final Throwable t) {
                logger.warn("Failed to create thumbnail: {} -> {} ({}:{})", thumbnailId, responseData.getUrl(),
                        t.getClass().getCanonicalName(), t.getMessage());
                if (logger.isDebugEnabled()) {
                    logger.debug("Details for failed thumbnail creation.", t);
                }
            } finally {
                if (!created) {
                    updateThumbnailField(thumbnailId, StringUtil.EMPTY);
                    if (outputFile.exists() && !outputFile.delete()) {
                        logger.warn("Failed to delete {}", outputFile.getAbsolutePath());
                    }
                }
            }
            return outputFile.exists();
        });

    }

    protected boolean isImageMimeType(final ResponseData responseData) {
        final String mimeType = responseData.getMimeType();
        if (mimeType == null) {
            return true;
        }

        return switch (mimeType) {
        case "image/png", "image/gif", "image/jpeg", "image/bmp" -> true;
        default -> false;
        };
    }

    protected Result saveImage(final ImageInputStream input, final File outputFile) throws IOException {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
        if (readers.hasNext()) {
            final ImageReader reader = readers.next();
            try {
                reader.setInput(input);
                final ImageReadParam param = reader.getDefaultReadParam();
                final int width = reader.getWidth(0);
                final int height = reader.getHeight(0);
                if (width <= 0 || height <= 0) {
                    return Result.NO_IMAGE;
                }
                if (!fessConfig.validateThumbnailSize(width, height)) {
                    return Result.INVALID_SIZE;
                }
                final int samplingWidth = width / fessConfig.getThumbnailHtmlImageThumbnailWidthAsInteger();
                final int samplingHeight = height / fessConfig.getThumbnailHtmlImageThumbnailHeightAsInteger();
                param.setSourceSubsampling(samplingWidth <= 0 ? 1 : samplingWidth, samplingHeight <= 0 ? 1 : samplingHeight, 0, 0);
                param.setSourceRegion(new Rectangle(width, height > width ? width : height));
                final BufferedImage image = reader.read(0, param);
                final int thumbnailWidth = fessConfig.getThumbnailHtmlImageThumbnailWidthAsInteger();
                final int thumbnailHeight = (int) ((height > width ? width : height)
                        * fessConfig.getThumbnailHtmlImageThumbnailWidthAsInteger().floatValue() / width);
                final BufferedImage thumbnail = new BufferedImage(thumbnailWidth, thumbnailHeight, image.getType());
                thumbnail.getGraphics().drawImage(image.getScaledInstance(thumbnailWidth, thumbnailHeight, Image.SCALE_AREA_AVERAGING), 0,
                        0, thumbnailWidth, thumbnailHeight, null);
                ImageIO.write(thumbnail, fessConfig.getThumbnailHtmlImageFormat(), outputFile);
                image.flush();
                return Result.OK;
            } finally {
                reader.dispose();
            }
        }
        return Result.FAILED;
    }

    protected enum Result {
        OK, FAILED, INVALID_SIZE, NO_IMAGE;
    }
}
