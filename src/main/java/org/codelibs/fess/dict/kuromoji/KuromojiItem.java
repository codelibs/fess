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
package org.codelibs.fess.dict.kuromoji;

import java.util.Objects;

import org.codelibs.fess.dict.DictionaryItem;

/**
 * An item in a Kuromoji dictionary.
 */
public class KuromojiItem extends DictionaryItem {
    private final String token;

    private final String segmentation;

    private final String reading;

    private final String pos;

    private String newToken;

    private String newSegmentation;

    private String newReading;

    private String newPos;

    /**
     * Constructs a new Kuromoji item.
     *
     * @param id The ID of the item.
     * @param token The token.
     * @param segmentation The segmentation.
     * @param reading The reading.
     * @param pos The part of speech.
     */
    public KuromojiItem(final long id, final String token, final String segmentation, final String reading, final String pos) {
        this.id = id;
        this.token = token;
        this.segmentation = segmentation;
        this.reading = reading;
        this.pos = pos;

        if (id == 0) {
            // create
            newToken = token;
            newSegmentation = segmentation;
            newReading = reading;
            newPos = pos;
        }
    }

    /**
     * Returns the new token.
     *
     * @return The new token.
     */
    public String getNewToken() {
        return newToken;
    }

    /**
     * Sets the new token.
     *
     * @param newToken The new token.
     */
    public void setNewToken(final String newToken) {
        this.newToken = newToken;
    }

    /**
     * Returns the new segmentation.
     *
     * @return The new segmentation.
     */
    public String getNewSegmentation() {
        return newSegmentation;
    }

    /**
     * Sets the new segmentation.
     *
     * @param newSegmentation The new segmentation.
     */
    public void setNewSegmentation(final String newSegmentation) {
        this.newSegmentation = newSegmentation;
    }

    /**
     * Returns the new reading.
     *
     * @return The new reading.
     */
    public String getNewReading() {
        return newReading;
    }

    /**
     * Sets the new reading.
     *
     * @param newReading The new reading.
     */
    public void setNewReading(final String newReading) {
        this.newReading = newReading;
    }

    /**
     * Returns the new part of speech.
     *
     * @return The new part of speech.
     */
    public String getNewPos() {
        return newPos;
    }

    /**
     * Sets the new part of speech.
     *
     * @param newPos The new part of speech.
     */
    public void setNewPos(final String newPos) {
        this.newPos = newPos;
    }

    /**
     * Returns the token.
     *
     * @return The token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Returns the segmentation.
     *
     * @return The segmentation.
     */
    public String getSegmentation() {
        return segmentation;
    }

    /**
     * Returns the reading.
     *
     * @return The reading.
     */
    public String getReading() {
        return reading;
    }

    /**
     * Returns the part of speech.
     *
     * @return The part of speech.
     */
    public String getPos() {
        return pos;
    }

    /**
     * Returns true if the item has been updated.
     *
     * @return True if the item has been updated.
     */
    public boolean isUpdated() {
        return newToken != null;
    }

    /**
     * Returns true if the item has been deleted.
     *
     * @return True if the item has been deleted.
     */
    public boolean isDeleted() {
        return isUpdated() && newToken.length() == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, reading, segmentation, token);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final KuromojiItem other = (KuromojiItem) obj;
        if (!Objects.equals(pos, other.pos) || !Objects.equals(reading, other.reading) || !Objects.equals(segmentation, other.segmentation)
                || !Objects.equals(token, other.token)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KuromojiItem [token=" + token + ", segmentation=" + segmentation + ", reading=" + reading + ", pos=" + pos + ", newToken="
                + newToken + ", newSegmentation=" + newSegmentation + ", newReading=" + newReading + ", newPos=" + newPos + "]";
    }

    /**
     * Returns the item as a line string.
     *
     * @return The item as a line string.
     */
    public String toLineString() {
        final StringBuilder buf = new StringBuilder(100);
        if (isUpdated()) {
            buf.append(quoteEscape(newToken));
            buf.append(',');
            buf.append(quoteEscape(newSegmentation));
            buf.append(',');
            buf.append(quoteEscape(newReading));
            buf.append(',');
            buf.append(quoteEscape(newPos));
        } else {
            buf.append(quoteEscape(token));
            buf.append(',');
            buf.append(quoteEscape(segmentation));
            buf.append(',');
            buf.append(quoteEscape(reading));
            buf.append(',');
            buf.append(quoteEscape(pos));
        }
        return buf.toString();
    }

    private static String quoteEscape(final String original) {
        String result = original;

        if (result.indexOf('\"') >= 0) {
            result = result.replace("\"", "\"\"");
        }
        if (result.indexOf(',') >= 0) {
            return "\"" + result + "\"";
        }
        return result;
    }
}
