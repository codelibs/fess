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
package org.codelibs.fess.storage;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.exception.StorageException;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;

/**
 * S3-compatible storage client implementation using AWS SDK v2.
 * Supports Amazon S3, MinIO, and other S3-compatible storage systems.
 */
public class S3StorageClient implements StorageClient {

    private static final Logger logger = LogManager.getLogger(S3StorageClient.class);

    private final S3Client s3Client;
    private final String bucket;

    /**
     * Creates a new S3StorageClient instance.
     *
     * @param endpoint the S3 endpoint URL (null for AWS default)
     * @param accessKey the AWS access key
     * @param secretKey the AWS secret key
     * @param bucket the bucket name
     * @param region the AWS region
     */
    public S3StorageClient(final String endpoint, final String accessKey, final String secretKey, final String bucket,
            final String region) {
        this.bucket = bucket;

        final AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        final S3ClientBuilder builder =
                S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(credentials)).region(Region.of(getRegion(region)));

        // For non-AWS endpoints (MinIO, etc.), set custom endpoint with path-style access
        if (StringUtil.isNotBlank(endpoint)) {
            builder.endpointOverride(URI.create(endpoint)).forcePathStyle(true);
        }

        this.s3Client = builder.build();
    }

    private String getRegion(final String region) {
        return StringUtil.isNotBlank(region) ? region : "us-east-1";
    }

    @Override
    public void uploadObject(final String objectName, final InputStream inputStream, final long size, final String contentType) {
        try {
            final PutObjectRequest request =
                    PutObjectRequest.builder().bucket(bucket).key(objectName).contentType(contentType).contentLength(size).build();
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, size));
        } catch (final Exception e) {
            throw new StorageException("Failed to upload " + objectName, e);
        }
    }

    @Override
    public void downloadObject(final String objectName, final OutputStream outputStream) {
        try {
            final GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(objectName).build();
            try (InputStream in = s3Client.getObject(request)) {
                in.transferTo(outputStream);
            }
        } catch (final Exception e) {
            throw new StorageException("Failed to download " + objectName, e);
        }
    }

    @Override
    public void deleteObject(final String objectName) {
        try {
            final DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucket).key(objectName).build();
            s3Client.deleteObject(request);
        } catch (final Exception e) {
            throw new StorageException("Failed to delete " + objectName, e);
        }
    }

    @Override
    public List<StorageItem> listObjects(final String prefix, final int maxItems) {
        final List<StorageItem> items = new ArrayList<>();

        try {
            final ListObjectsV2Request.Builder requestBuilder =
                    ListObjectsV2Request.builder().bucket(bucket).delimiter("/").maxKeys(maxItems);

            if (StringUtil.isNotBlank(prefix)) {
                final String normalizedPrefix = prefix.endsWith("/") ? prefix : prefix + "/";
                requestBuilder.prefix(normalizedPrefix);
            }

            final ListObjectsV2Response response = s3Client.listObjectsV2(requestBuilder.build());

            // Process common prefixes (directories)
            for (final CommonPrefix commonPrefix : response.commonPrefixes()) {
                final String dirName = getName(commonPrefix.prefix());
                if (StringUtil.isNotBlank(dirName)) {
                    items.add(new StorageItem(dirName, prefix, true, 0, null, encodeId(commonPrefix.prefix())));
                }
            }

            // Process objects (files)
            for (final S3Object s3Object : response.contents()) {
                final String objectKey = s3Object.key();
                // Skip directory markers (objects ending with /)
                if (!objectKey.endsWith("/")) {
                    final String fileName = getName(objectKey);
                    final ZonedDateTime lastModified =
                            s3Object.lastModified() != null ? s3Object.lastModified().atZone(java.time.ZoneId.systemDefault()) : null;
                    items.add(new StorageItem(fileName, prefix, false, s3Object.size(), lastModified, encodeId(objectKey)));
                }
            }
        } catch (final NoSuchBucketException e) {
            logger.info("Bucket does not exist: {}", bucket);
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to list objects in {}", bucket, e);
            }
        }

        return items;
    }

    @Override
    public Map<String, String> getObjectTags(final String objectName) {
        try {
            final GetObjectTaggingRequest request = GetObjectTaggingRequest.builder().bucket(bucket).key(objectName).build();
            final GetObjectTaggingResponse response = s3Client.getObjectTagging(request);
            return response.tagSet().stream().collect(Collectors.toMap(Tag::key, Tag::value));
        } catch (final Exception e) {
            throw new StorageException("Failed to get tags from " + objectName, e);
        }
    }

    @Override
    public void setObjectTags(final String objectName, final Map<String, String> tags) {
        try {
            final List<Tag> tagList = tags.entrySet()
                    .stream()
                    .map(e -> Tag.builder().key(e.getKey()).value(e.getValue()).build())
                    .collect(Collectors.toList());

            final PutObjectTaggingRequest request = PutObjectTaggingRequest.builder()
                    .bucket(bucket)
                    .key(objectName)
                    .tagging(Tagging.builder().tagSet(tagList).build())
                    .build();
            s3Client.putObjectTagging(request);
        } catch (final Exception e) {
            throw new StorageException("Failed to update tags for " + objectName, e);
        }
    }

    @Override
    public void ensureBucketExists() {
        try {
            final HeadBucketRequest request = HeadBucketRequest.builder().bucket(bucket).build();
            s3Client.headBucket(request);
        } catch (final NoSuchBucketException e) {
            try {
                final CreateBucketRequest createRequest = CreateBucketRequest.builder().bucket(bucket).build();
                s3Client.createBucket(createRequest);
                logger.info("Created storage bucket: {}", bucket);
            } catch (final Exception e1) {
                logger.warn("Failed to create storage bucket: {}", bucket, e1);
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to check bucket: {}", bucket, e);
            }
        }
    }

    @Override
    public boolean isAvailable() {
        try {
            final HeadBucketRequest request = HeadBucketRequest.builder().bucket(bucket).build();
            s3Client.headBucket(request);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
    }

    /**
     * Extracts the file/directory name from a full object path.
     *
     * @param objectName the full object path
     * @return the name portion of the path
     */
    private String getName(final String objectName) {
        if (StringUtil.isBlank(objectName)) {
            return StringUtil.EMPTY;
        }
        // Remove trailing slash if present
        String name = objectName;
        if (name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
        }
        final String[] values = name.split("/");
        if (values.length == 0) {
            return StringUtil.EMPTY;
        }
        return values[values.length - 1];
    }

    /**
     * Encodes an object name to a URL-safe base64 string.
     *
     * @param objectName the object name to encode
     * @return base64 encoded string
     */
    private String encodeId(final String objectName) {
        if (objectName == null) {
            return StringUtil.EMPTY;
        }
        return new String(Base64.getUrlEncoder().encode(objectName.getBytes(Constants.UTF_8_CHARSET)), Constants.UTF_8_CHARSET);
    }
}
