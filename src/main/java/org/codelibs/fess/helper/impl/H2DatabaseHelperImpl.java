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

package org.codelibs.fess.helper.impl;

import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.helper.DatabaseHelper;
import org.codelibs.fess.util.ResourceUtil;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H2DatabaseHelperImpl implements DatabaseHelper {
    private static final Logger logger = LoggerFactory
            .getLogger(H2DatabaseHelperImpl.class);

    public String databaseName = "robot";

    public String databasePath = ResourceUtil.getDbPath(StringUtil.EMPTY);

    public String user = "sa";

    public String password = StringUtil.EMPTY;

    public String sqlFilePath = ResourceUtil.getClassesPath("sql/robot.ddl");

    public boolean printLogOnError = false;

    /* (non-Javadoc)
     * @see org.codelibs.fess.helper.impl.DatabaseHelper#optimize()
     */
    @Override
    public synchronized void optimize() {
        final String url = "jdbc:h2:" + databasePath + "/" + databaseName;
        try {
            DeleteDbFiles.execute(databasePath, databaseName, false);
            RunScript.execute(url, user, password, sqlFilePath, null, false);
        } catch (final Exception e) {
            if (printLogOnError) {
                logger.error("Failed to optimize H2 Database: " + databasePath
                        + "/" + databaseName, e);
            }
        }
    }
}
