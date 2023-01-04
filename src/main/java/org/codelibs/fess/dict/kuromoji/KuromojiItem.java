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
package org.codelibs.fess.dict.kuromoji;

import java.util.Objects;

import org.codelibs.fess.dict.DictionaryItem;

public class KuromojiItem extends DictionaryItem {
    private final String token;

    private final String segmentation;

    private final String reading;

    private final String pos;

    private String newToken;

    private String newSegmentation;

    private String newReading;

    private String newPos;

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

    public String getNewToken() {
        return newToken;
    }

    public void setNewToken(final String newToken) {
        this.newToken = newToken;
    }

    public String getNewSegmentation() {
        return newSegmentation;
    }

    public void setNewSegmentation(final String newSegmentation) {
        this.newSegmentation = newSegmentation;
    }

    public String getNewReading() {
        return newReading;
    }

    public void setNewReading(final String newReading) {
        this.newReading = newReading;
    }

    public String getNewPos() {
        return newPos;
    }

    public void setNewPos(final String newPos) {
        this.newPos = newPos;
    }

    public String getToken() {
        return token;
    }

    public String getSegmentation() {
        return segmentation;
    }

    public String getReading() {
        return reading;
    }

    public String getPos() {
        return pos;
    }

    public boolean isUpdated() {
        return newToken != null;
    }

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
        if ((obj == null) || (getClass() != obj.getClass())) {
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
