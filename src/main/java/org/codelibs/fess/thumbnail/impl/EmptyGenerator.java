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

import java.io.File;

public class EmptyGenerator extends BaseThumbnailGenerator {

    @Override
    public boolean generate(final String url, final File outputFile) {
        return false;
    }

    @Override
    public void destroy() {
        // nothing
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
