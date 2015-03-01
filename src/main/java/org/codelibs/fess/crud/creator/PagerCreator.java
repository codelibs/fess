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

package org.codelibs.fess.crud.creator;

import org.seasar.framework.container.ComponentCustomizer;
import org.seasar.framework.container.assembler.AutoBindingDefFactory;
import org.seasar.framework.container.creator.ComponentCreatorImpl;
import org.seasar.framework.container.deployer.InstanceDefFactory;
import org.seasar.framework.convention.NamingConvention;

/**
 * @author shinsuke
 */
public class PagerCreator extends ComponentCreatorImpl {
    public static final String SUFFIX = "Pager";

    public PagerCreator(final NamingConvention namingConvention) {
        super(namingConvention);
        setNameSuffix(SUFFIX);
        setInstanceDef(InstanceDefFactory.SESSION);
        setAutoBindingDef(AutoBindingDefFactory.NONE);
    }

    public ComponentCustomizer getPagerCustomizer() {
        return getCustomizer();
    }

    public void setPagerCustomizer(final ComponentCustomizer customizer) {
        setCustomizer(customizer);
    }
}
