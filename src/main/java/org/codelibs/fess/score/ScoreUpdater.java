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
package org.codelibs.fess.score;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreUpdater {
    private static final Logger logger = LogManager.getLogger(ScoreUpdater.class);

    private final List<ScoreBooster> scoreBoosterList = new ArrayList<>();

    public String execute() {
        final StringBuilder resultBuf = new StringBuilder();
        scoreBoosterList.forEach(b -> {
            try {
                final long count = b.process();
                resultBuf.append(b.getClass().getSimpleName()).append(" : ").append(count).append('\n');
            } catch (final Exception e) {
                logger.warn("Failed to update scores.", e);
                resultBuf.append(e.getMessage()).append('\n');
            }
        });
        return resultBuf.toString();
    }

    protected void addScoreBooster(final ScoreBooster scoreBooster) {
        scoreBoosterList.add(scoreBooster);
        scoreBoosterList.sort((b1, b2) -> b2.getPriority() - b1.getPriority());
    }
}
