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

package jp.sf.fess.action.admin;

import java.io.Serializable;

import javax.annotation.Resource;

import jp.sf.fess.dict.DictionaryFile;
import jp.sf.fess.dict.DictionaryManager;
import jp.sf.fess.form.admin.DictForm;
import jp.sf.fess.helper.SystemHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;

public class DictAction implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(DictAction.class);

    @Resource
    @ActionForm
    protected DictForm dictForm;

    @Resource
    protected DictionaryManager dictionaryManager;

    @Resource
    protected SystemHelper systemHelper;

    public DictionaryFile[] dictFiles;

    public String getHelpLink() {
        return systemHelper.getHelpLink("dict");
    }

    @Execute(validator = false, input = "error.jsp")
    public String index() {
        dictFiles = dictionaryManager.getDictionaryFiles();
        return "index.jsp";
    }
}
