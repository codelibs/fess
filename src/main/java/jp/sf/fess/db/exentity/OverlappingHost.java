/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package jp.sf.fess.db.exentity;

import jp.sf.fess.db.bsentity.BsOverlappingHost;

/**
 * The entity of OVERLAPPING_HOST.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class OverlappingHost extends BsOverlappingHost {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    public String convert(final String url) {
        final String targetStr = getOverlappingName()
                .replaceAll("\\.", "\\\\.");
        return url.replaceFirst("://" + targetStr + "$",
                "://" + getRegularName()).replaceFirst(
                "://" + targetStr + "([:/])", "://" + getRegularName() + "$1");
    }
}
