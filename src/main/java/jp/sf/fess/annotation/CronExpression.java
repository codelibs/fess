/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codelibs.core.util.StringUtil;
import org.seasar.struts.annotation.Arg;
import org.seasar.struts.annotation.Msg;
import org.seasar.struts.annotation.Validator;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Validator("cronExpression")
public @interface CronExpression {
    /**
     * A validation message.
     *
     * @return
     */
    Msg msg() default @Msg(key = "errors.cronexpression");

    /**
     * Argument for a message
     *
     * @return
     */
    Arg arg0() default @Arg(key = StringUtil.EMPTY);

    /**
     * a target name for this validation
     *
     * @return
     */
    String target() default StringUtil.EMPTY;

}
