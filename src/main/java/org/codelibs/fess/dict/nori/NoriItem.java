/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.dict.nori;

import org.codelibs.fess.dict.DictionaryItem;

public class NoriItem extends DictionaryItem {
    private final String token;

    private final String segmentation;

    private String newToken;

    private String newSegmentation;

    public NoriItem(final long id, final String token, final String segmentation) {
        this.id = id;
        this.token = token;
        this.segmentation = segmentation;

        if (id == 0) {
            // create
            newToken = token;
            newSegmentation = segmentation;
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

    public String getToken() {
        return token;
    }

    public String getSegmentation() {
        return segmentation;
    }

    public boolean isUpdated() {
        return newToken != null;
    }

    public boolean isDeleted() {
        return isUpdated() && newToken.length() == 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (segmentation == null ? 0 : segmentation.hashCode());
        result = prime * result + (token == null ? 0 : token.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NoriItem other = (NoriItem) obj;

        if (segmentation == null) {
            if (other.segmentation != null) {
                return false;
            }
        } else if (!segmentation.equals(other.segmentation)) {
            return false;
        }
        if (token == null) {
            if (other.token != null) {
                return false;
            }
        } else if (!token.equals(other.token)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NoriItem [token=" + token + ", segmentation=" + segmentation + ", newToken=" + newToken + ", newSegmentation="
                + newSegmentation + "]";
    }

    public String toLineString() {
        final StringBuilder buf = new StringBuilder(100);
        if (isUpdated()) {
            buf.append(quoteEscape(newToken));
            if (newSegmentation != null) {
                buf.append(' ');
                buf.append(quoteEscape(newSegmentation));
            }
        } else {
            buf.append(quoteEscape(token));
            if (newSegmentation != null) {
                buf.append(' ');
                buf.append(quoteEscape(newSegmentation));
            }
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
