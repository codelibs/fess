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

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Tuple4;
import org.codelibs.elasticsearch.runner.net.Curl;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlTagBasedGenerator extends BaseThumbnailGenerator {

    private static final Logger logger = LoggerFactory.getLogger(HtmlTagBasedGenerator.class);

    @Override
    public void destroy() {
    }

    @Override
    public Tuple4<String, String, String, String> createTask(final String path, final Map<String, Object> docMap) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String thumbnailId = DocumentUtil.getValue(docMap, fessConfig.getIndexFieldId(), String.class);
        final String url = DocumentUtil.getValue(docMap, fessConfig.getIndexFieldThumbnail(), String.class);
        if (StringUtil.isBlank(url)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Thumbnail task is not found: " + path + ", " + thumbnailId + ", " + url);
            }
            return null;
        }
        final Tuple4<String, String, String, String> task = new Tuple4<>(getName(), thumbnailId, url, path);
        if (logger.isDebugEnabled()) {
            logger.debug("Create thumbnail task: " + task);
        }
        return task;
    }

    @Override
    public boolean generate(final String thumbnailId, final String url, final File outputFile) {
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

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        Curl.get(url)
                .proxy(fessConfig.getHttpProxy())
                .execute(
                        con -> {
                            boolean created = false;
                            try (ImageInputStream input = ImageIO.createImageInputStream(con.getInputStream())) {
                                switch (saveImage(input, outputFile)) {
                                case OK:
                                    created = true;
                                    break;
                                case FAILED:
                                    logger.warn("Failed to create thumbnail: " + thumbnailId + " -> " + url);
                                    break;
                                case INVALID_SIZE:
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("Invalid thumbnail size: " + thumbnailId + " -> " + url);
                                    }
                                    break;
                                default:
                                    logger.error("Unknown thumbnail result: " + thumbnailId + " -> " + url);
                                    break;
                                }
                            } catch (final Throwable t) {
                                if (logger.isDebugEnabled()) {
                                    logger.warn("Failed to create thumbnail: " + thumbnailId + " -> " + url, t);
                                } else {
                                    logger.warn("Failed to create thumbnail: " + thumbnailId + " -> " + url + " ("
                                            + t.getClass().getCanonicalName() + ": " + t.getMessage() + ")");
                                }
                            } finally {
                                if (!created) {
                                    updateThumbnailField(thumbnailId, url, StringUtil.EMPTY);
                                    if (outputFile.exists() && !outputFile.delete()) {
                                        logger.warn("Failed to delete " + outputFile.getAbsolutePath());
                                    }
                                }
                            }
                        });

        return outputFile.exists();
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
                if (!fessConfig.validateThumbnailSize(width, height)) {
                    return Result.INVALID_SIZE;
                }
                final int samplingWidth = width / fessConfig.getThumbnailHtmlImageThumbnailWidthAsInteger();
                final int samplingHeight = height / fessConfig.getThumbnailHtmlImageThumbnailHeightAsInteger();
                param.setSourceSubsampling(samplingWidth <= 0 ? 1 : samplingWidth, samplingHeight <= 0 ? 1 : samplingHeight, 0, 0);
                param.setSourceRegion(new Rectangle(width, height > width ? width : height));
                final BufferedImage image = reader.read(0, param);
                final int thumbnailWidth = fessConfig.getThumbnailHtmlImageThumbnailWidthAsInteger();
                final int thumbnailHeight =
                        (int) ((height > width ? width : height) * fessConfig.getThumbnailHtmlImageThumbnailWidthAsInteger().floatValue() / width);
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
        OK, FAILED, INVALID_SIZE;
    }
}
