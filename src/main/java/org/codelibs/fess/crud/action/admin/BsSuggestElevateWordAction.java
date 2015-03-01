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

package org.codelibs.fess.crud.action.admin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.db.exentity.SuggestElevateWord;
import org.codelibs.fess.form.admin.SuggestElevateWordForm;
import org.codelibs.fess.pager.SuggestElevateWordPager;
import org.codelibs.fess.service.SuggestElevateWordService;
import org.codelibs.sastruts.core.annotation.Token;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

public class BsSuggestElevateWordAction implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(BsSuggestElevateWordAction.class);

    // for list

    public List<SuggestElevateWord> suggestElevateWordItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected SuggestElevateWordForm suggestElevateWordForm;

    @Resource
    protected SuggestElevateWordService suggestElevateWordService;

    @Resource
    protected SuggestElevateWordPager suggestElevateWordPager;

    protected String displayList(final boolean redirect) {
        // page navi
        suggestElevateWordItems = suggestElevateWordService
                .getSuggestElevateWordList(suggestElevateWordPager);

        // restore from pager
        Beans.copy(suggestElevateWordPager, suggestElevateWordForm.searchParams)
                .excludes(CommonConstants.PAGER_CONVERSION_RULE)

                .execute();

        if (redirect) {
            return "index?redirect=true";
        } else {
            return "index.jsp";
        }
    }

    @Execute(validator = false, input = "error.jsp")
    public String index() {
        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp", urlPattern = "list/{pageNumber}")
    public String list() {
        // page navi
        if (StringUtil.isNotBlank(suggestElevateWordForm.pageNumber)) {
            try {
                suggestElevateWordPager.setCurrentPageNumber(Integer
                        .parseInt(suggestElevateWordForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid value: "
                            + suggestElevateWordForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(suggestElevateWordForm.searchParams, suggestElevateWordPager)
                .excludes(CommonConstants.PAGER_CONVERSION_RULE)

                .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        suggestElevateWordPager.clear();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String back() {
        return displayList(false);
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editagain() {
        return "edit.jsp";
    }

    @Execute(validator = false, input = "error.jsp", urlPattern = "confirmpage/{crudMode}/{id}")
    public String confirmpage() {
        if (suggestElevateWordForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.CONFIRM_MODE,
                            suggestElevateWordForm.crudMode });
        }

        loadSuggestElevateWord();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        suggestElevateWordForm.initialize();
        suggestElevateWordForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (suggestElevateWordForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.EDIT_MODE,
                            suggestElevateWordForm.crudMode });
        }

        loadSuggestElevateWord();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        suggestElevateWordForm.crudMode = CommonConstants.EDIT_MODE;

        loadSuggestElevateWord();

        return "edit.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    @Execute(validator = true, input = "edit.jsp")
    public String confirmfromcreate() {
        return "confirm.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    @Execute(validator = true, input = "edit.jsp")
    public String confirmfromupdate() {
        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "deletepage/{crudMode}/{id}")
    public String deletepage() {
        if (suggestElevateWordForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            suggestElevateWordForm.crudMode });
        }

        loadSuggestElevateWord();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        suggestElevateWordForm.crudMode = CommonConstants.DELETE_MODE;

        loadSuggestElevateWord();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final SuggestElevateWord suggestElevateWord = createSuggestElevateWord();
            suggestElevateWordService.store(suggestElevateWord);
            SAStrutsUtil.addSessionMessage("success.crud_create_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(
                    "errors.crud_failed_to_create_crud_table");
        }
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String update() {
        try {
            final SuggestElevateWord suggestElevateWord = createSuggestElevateWord();
            suggestElevateWordService.store(suggestElevateWord);
            SAStrutsUtil.addSessionMessage("success.crud_update_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(
                    "errors.crud_failed_to_update_crud_table");
        }
    }

    @Token(save = false, validate = true)
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (suggestElevateWordForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            suggestElevateWordForm.crudMode });
        }

        try {
            final SuggestElevateWord suggestElevateWord = suggestElevateWordService
                    .getSuggestElevateWord(createKeyMap());
            if (suggestElevateWord == null) {
                // throw an exception
                throw new ActionMessagesException(
                        "errors.crud_could_not_find_crud_table",

                        new Object[] { suggestElevateWordForm.id });

            }

            suggestElevateWordService.delete(suggestElevateWord);
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(
                    "errors.crud_failed_to_delete_crud_table");
        }
    }

    protected void loadSuggestElevateWord() {

        final SuggestElevateWord suggestElevateWord = suggestElevateWordService
                .getSuggestElevateWord(createKeyMap());
        if (suggestElevateWord == null) {
            // throw an exception
            throw new ActionMessagesException(
                    "errors.crud_could_not_find_crud_table",

                    new Object[] { suggestElevateWordForm.id });

        }

        Beans.copy(suggestElevateWord, suggestElevateWordForm)
                .excludes("searchParams", "mode")

                .execute();
    }

    protected SuggestElevateWord createSuggestElevateWord() {
        SuggestElevateWord suggestElevateWord;
        if (suggestElevateWordForm.crudMode == CommonConstants.EDIT_MODE) {
            suggestElevateWord = suggestElevateWordService
                    .getSuggestElevateWord(createKeyMap());
            if (suggestElevateWord == null) {
                // throw an exception
                throw new ActionMessagesException(
                        "errors.crud_could_not_find_crud_table",

                        new Object[] { suggestElevateWordForm.id });

            }
        } else {
            suggestElevateWord = new SuggestElevateWord();
        }
        Beans.copy(suggestElevateWordForm, suggestElevateWord)
                .excludes("searchParams", "mode")

                .execute();

        return suggestElevateWord;
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", suggestElevateWordForm.id);

        return keys;
    }
}
