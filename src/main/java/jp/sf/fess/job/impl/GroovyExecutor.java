/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.job.impl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import jp.sf.fess.job.JobExecutor;

import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class GroovyExecutor extends JobExecutor {

    @Override
    public Object execute(final String script) {
        final Binding binding = new Binding();
        binding.setVariable("container",
                SingletonS2ContainerFactory.getContainer());
        binding.setVariable("executor", this);

        final GroovyShell shell = new GroovyShell(binding);
        return shell.evaluate(script);
    }

}
