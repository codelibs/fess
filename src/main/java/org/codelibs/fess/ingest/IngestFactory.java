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
package org.codelibs.fess.ingest;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IngestFactory {
    private static final Logger logger = LogManager.getLogger(IngestFactory.class);

    private Ingester[] ingesters = {};

    public synchronized void add(final Ingester ingester) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded {}", ingester.getClass().getSimpleName());
        }
        final Ingester[] newIngesters = Arrays.copyOf(ingesters, ingesters.length + 1);
        newIngesters[ingesters.length] = ingester;
        Arrays.sort(newIngesters, (o1, o2) -> o1.priority - o2.priority);
        ingesters = newIngesters;
    }

    public Ingester[] getIngesters() {
        return ingesters;
    }
}
