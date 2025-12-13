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

import java.time.ZonedDateTime;

/**
 * Represents a storage item (file or directory).
 */
public class StorageItem {

    private final String name;
    private final String path;
    private final boolean directory;
    private final long size;
    private final ZonedDateTime lastModified;
    private final String encodedId;

    /**
     * Creates a new StorageItem instance.
     *
     * @param name the name of the item
     * @param path the path of the item
     * @param directory true if this item is a directory
     * @param size the size of the item in bytes
     * @param lastModified the last modified timestamp
     * @param encodedId the base64-encoded ID of the item
     */
    public StorageItem(final String name, final String path, final boolean directory, final long size, final ZonedDateTime lastModified,
            final String encodedId) {
        this.name = name;
        this.path = path;
        this.directory = directory;
        this.size = size;
        this.lastModified = lastModified;
        this.encodedId = encodedId;
    }

    /**
     * Returns the name of the item.
     *
     * @return the item name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the path of the item.
     *
     * @return the item path
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns whether this item is a directory.
     *
     * @return true if this item is a directory, false otherwise
     */
    public boolean isDirectory() {
        return directory;
    }

    /**
     * Returns the size of the item in bytes.
     *
     * @return the item size
     */
    public long getSize() {
        return size;
    }

    /**
     * Returns the last modified timestamp.
     *
     * @return the last modified timestamp
     */
    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    /**
     * Returns the base64-encoded ID of the item.
     *
     * @return the encoded ID
     */
    public String getEncodedId() {
        return encodedId;
    }
}
