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
package org.codelibs.fess.rank.fusion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * Keyword-only searcher, kept as a stable name for the behavior Fess had before rank fusion
 * could be delegated to the search engine.
 *
 * <p>It adds nothing to {@link AbstractDocumentSearcher}: its whole purpose is to give
 * "keyword search that never participates in fusion" a name that
 * {@code rank.fusion.searchers} can select, so an operator can pin that behavior without
 * having to reason about which other searchers happen to be registered.</p>
 *
 * <p>Unlike the other searchers this one registers only when it is named explicitly. If it
 * registered unconditionally it would join the fusion set of every deployment that has not
 * configured {@code rank.fusion.searchers}, because an empty allow-list means "every
 * registered searcher".</p>
 */
public class LegacySearcher extends AbstractDocumentSearcher {

    private static final Logger logger = LogManager.getLogger(LegacySearcher.class);

    /** JVM property naming the searchers that may run, read the same way {@code RankFusionProcessor} reads it. */
    protected static final String SEARCHERS_PROPERTY = "rank.fusion.searchers";

    /**
     * Creates a new instance.
     */
    public LegacySearcher() {
    }

    /**
     * Registers this searcher only when {@code rank.fusion.searchers} names it. Called by the
     * DI container.
     */
    @PostConstruct
    public void register() {
        if (!isNamedInAllowList()) {
            if (logger.isDebugEnabled()) {
                logger.debug("{} is not named in {}; it is not registered with the rank fusion processor.", getName(), SEARCHERS_PROPERTY);
            }
            return;
        }
        logger.info("Load {}", getClass().getSimpleName());
        ComponentUtil.getRankFusionProcessor().register(this);
    }

    /**
     * Determines whether {@code rank.fusion.searchers} names this searcher. The property is read
     * at startup, so naming it later requires a restart, matching how
     * {@code content_chunker.search.enabled} gates the semantic searcher.
     *
     * @return true if this searcher is named in the allow-list
     */
    protected boolean isNamedInAllowList() {
        final String value = System.getProperty(SEARCHERS_PROPERTY);
        if (StringUtil.isBlank(value)) {
            return false;
        }
        final String name = getName();
        for (final String s : value.split(",")) {
            if (name.equals(s.trim())) {
                return true;
            }
        }
        return false;
    }
}
