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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.lastaflute.di.helper.message.MessageResourceBundle;
import org.lastaflute.web.ruts.message.objective.ObjectiveMessageResources;

/**
 * The message resources of Fess.
 *
 * <p>The first lookup in a locale loads the application bundle ({@code fess_message}) and links the
 * bundle it extends ({@code fess_label}) to it. {@link ObjectiveMessageResources} does that while
 * another request may already be reading the bundle: it treats a bundle as linked as soon as the
 * first link is in place, and the linking takes the chain apart before it puts it together again.
 * A request that arrived in the meantime found neither the labels nor, for a moment, the messages,
 * and failed with a {@code MessageKeyNotFoundException} for a key that exists. This class lets a
 * request use a bundle only after the linking has finished.</p>
 */
public class FessMessageResources extends ObjectiveMessageResources {

    private static final long serialVersionUID = 1L;

    /** The application bundles whose extends are completely linked. A bundle is added only after its linking has finished. */
    protected final transient Set<MessageResourceBundle> linkedBundles = ConcurrentHashMap.newKeySet();

    /**
     * Creates the message resources.
     */
    public FessMessageResources() {
        super();
    }

    @Override
    protected MessageResourceBundle getBundleResolvedExtends(final String appMessageName, final List<String> extendsNameList,
            final Locale locale) {
        final MessageResourceBundle appBundle = findBundleSimply(appMessageName, locale);
        if (extendsNameList.isEmpty() || linkedBundles.contains(appBundle)) {
            return appBundle;
        }
        synchronized (this) {
            if (!linkedBundles.contains(appBundle)) {
                if (!isAlreadyExtends(appBundle)) {
                    setupExtendsReferences(appMessageName, extendsNameList, locale, appBundle);
                }
                linkedBundles.add(appBundle);
            }
        }
        return appBundle;
    }

    @Override
    public void dispose() {
        super.dispose();
        linkedBundles.clear();
    }
}
