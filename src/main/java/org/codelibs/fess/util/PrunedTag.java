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
package org.codelibs.fess.util;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.w3c.dom.Node;

public class PrunedTag {
    private final String tag;
    private final String id;
    private final String css;

    public PrunedTag(final String tag, final String id, final String css) {
        this.tag = tag;
        this.id = id;
        this.css = css;

    }

    public boolean matches(final Node node) {
        if (tag.equalsIgnoreCase(node.getNodeName())) {
            if (id == null) {
                if (css == null) {
                    return true;
                } else {
                    final Node classAttr = node.getAttributes().getNamedItem("class");
                    if (classAttr != null) {
                        final String value = classAttr.getNodeValue();
                        if (StringUtil.isNotBlank(value)) {
                            return StreamUtil.split(value, " ").get(stream -> stream.anyMatch(s -> css.equals(s)));
                        }
                    }
                }
            } else {
                final Node idAttr = node.getAttributes().getNamedItem("id");
                if (idAttr != null) {
                    final String value = idAttr.getNodeValue();
                    return id.equals(value);
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "PrunedTag [tag=" + tag + ", id=" + id + ", css=" + css + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((css == null) ? 0 : css.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((tag == null) ? 0 : tag.hashCode());
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
        final PrunedTag other = (PrunedTag) obj;
        if (css == null) {
            if (other.css != null) {
                return false;
            }
        } else if (!css.equals(other.css)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (tag == null) {
            if (other.tag != null) {
                return false;
            }
        } else if (!tag.equals(other.tag)) {
            return false;
        }
        return true;
    }
}
