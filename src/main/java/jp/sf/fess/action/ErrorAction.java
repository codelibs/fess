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

package jp.sf.fess.action;

import javax.annotation.Resource;

import jp.sf.fess.form.ErrorForm;

import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;

public class ErrorAction {

    @ActionForm
    @Resource
    protected ErrorForm errorForm;

    @Execute(validator = false)
    public String index() {
        return "notFound.jsp";
    }

    @Execute(validator = false)
    public String systemError() {
        return "system.jsp";
    }

    @Execute(validator = false)
    public String badRequest() {
        return "badRequest.jsp";
    }

    @Execute(validator = false)
    public String notFound() {
        return "notFound.jsp";
    }
}