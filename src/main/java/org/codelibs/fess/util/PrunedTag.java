/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
    private String tag;
    private String id;
    private String css;

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
                    Node classAttr = node.getAttributes().getNamedItem("class");
                    if (classAttr != null) {
                        final String value = classAttr.getNodeValue();
                        if (StringUtil.isNotBlank(value)) {
                            return StreamUtil.split(value, " ").get(stream -> stream.anyMatch(s -> css.equals(s)));
                        }
                    }
                }
            } else {
                Node idAttr = node.getAttributes().getNamedItem("id");
                if (idAttr != null) {
                    final String value = idAttr.getNodeValue();
                    return id.equals(value);
                }
            }
        }
        return false;
    }
}
