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

/**
 * HTML tag-based thumbnail generator that creates thumbnails from image content
 * referenced in HTML documents. This generator extracts images from HTML content
 * and processes them to create thumbnail images based on configured dimensions
 * and format settings.
 *
 * <p>The generator validates image MIME types, processes image data through
 * ImageIO operations, and applies scaling and cropping to generate thumbnails
 * that meet the specified size requirements.</p>
 *
 */
public class HtmlTagBasedGenerator extends BaseThumbnailGenerator {

    private static final Logger logger = LogManager.getLogger(HtmlTagBasedGenerator.class);

    /**
     * Default constructor for HtmlTagBasedGenerator.
     */
    public HtmlTagBasedGenerator() {
        super();
    }

    /**
     * Destroys this thumbnail generator and releases any resources.
     * This implementation is empty as no cleanup is required.
     */
    @Override
    public void destroy() {
    }

    /**
     * Creates a thumbnail generation task for the specified document.
     *
     * @param path the file path or URL of the document
     * @param docMap the document metadata map containing field values
     * @return a tuple containing the generator name, thumbnail ID, and path
     */
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

    /**
     * Generates a thumbnail image and saves it to the specified output file.
     *
     * <p>This method processes the image content associated with the thumbnail ID,
     * validates the image format, applies scaling and cropping transformations,
     * and writes the resulting thumbnail to the output file.</p>
     *
     * @param thumbnailId the unique identifier for the thumbnail
     * @param outputFile the file where the generated thumbnail will be saved
     * @return true if the thumbnail was successfully generated, false otherwise
     */
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

    /**
     * Checks if the response data contains an image MIME type that can be processed
     * for thumbnail generation.
     *
     * @param responseData the response data containing MIME type information
     * @return true if the MIME type represents a supported image format, false otherwise
     */
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

    /**
     * Processes and saves an image from the input stream to the output file as a thumbnail.
     *
     * <p>This method reads image data, validates dimensions, applies subsampling and scaling
     * transformations based on configuration settings, and writes the processed thumbnail
     * to the specified output file.</p>
     *
     * @param input the image input stream containing the source image data
     * @param outputFile the file where the processed thumbnail will be saved
     * @return the result of the image processing operation
     * @throws IOException if an error occurs during image reading or writing operations
     */
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
                thumbnail.getGraphics()
                        .drawImage(image.getScaledInstance(thumbnailWidth, thumbnailHeight, Image.SCALE_AREA_AVERAGING), 0, 0,
                                thumbnailWidth, thumbnailHeight, null);
                ImageIO.write(thumbnail, fessConfig.getThumbnailHtmlImageFormat(), outputFile);
                image.flush();
                return Result.OK;
            } finally {
                reader.dispose();
            }
        }
        return Result.FAILED;
    }

    /**
     * Enumeration representing the possible results of thumbnail image processing.
     */
    protected enum Result {
        /** Thumbnail was successfully generated */
        OK,
        /** Thumbnail generation failed due to processing errors */
        FAILED,
        /** Image dimensions do not meet validation requirements */
        INVALID_SIZE,
        /** No valid image data was found in the input */
        NO_IMAGE;
    }
}
