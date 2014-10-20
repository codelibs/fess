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

package jp.sf.fess.action.admin;

import java.sql.Timestamp;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.crud.action.admin.BsSuggestBadWordAction;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.SuggestBadWord;
import jp.sf.fess.helper.SuggestHelper;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.util.FessBeans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

public class SuggestBadWordAction extends BsSuggestBadWordAction {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(SuggestBadWordAction.class);

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected SuggestHelper suggestHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("suggestBadWord");
    }

    @Override
    protected void loadSuggestBadWord() {

        final SuggestBadWord suggestBadWord = suggestBadWordService
                .getSuggestBadWord(createKeyMap());
        if (suggestBadWord == null) {
            // throw an exception
            throw new SSCActionMessagesException(
                    "errors.crud_could_not_find_crud_table",
                    new Object[] { suggestBadWordForm.id });
        }

        FessBeans.copy(suggestBadWord, suggestBadWordForm)
        .commonColumnDateConverter().excludes("searchParams", "mode")
        .execute();
    }

    @Override
    protected SuggestBadWord createSuggestBadWord() {
        SuggestBadWord suggestBadWord;
        final String username = systemHelper.getUsername();
        final Timestamp timestamp = systemHelper.getCurrentTimestamp();
        if (suggestBadWordForm.crudMode == CommonConstants.EDIT_MODE) {
            suggestBadWord = suggestBadWordService
                    .getSuggestBadWord(createKeyMap());
            if (suggestBadWord == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { suggestBadWordForm.id });
            }
        } else {
            suggestBadWord = new SuggestBadWord();
            suggestBadWord.setCreatedBy(username);
            suggestBadWord.setCreatedTime(timestamp);
        }
        suggestBadWord.setUpdatedBy(username);
        suggestBadWord.setUpdatedTime(timestamp);
        FessBeans.copy(suggestBadWordForm, suggestBadWord)
        .excludesCommonColumns().execute();

        return suggestBadWord;
    }

    @Override
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (suggestBadWordForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                    suggestBadWordForm.crudMode });
        }

        try {
            final SuggestBadWord suggestBadWord = suggestBadWordService
                    .getSuggestBadWord(createKeyMap());
            if (suggestBadWord == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { suggestBadWordForm.id });
            }

            //           suggestBadWordService.delete(suggestBadWord);
            final String username = systemHelper.getUsername();
            final Timestamp timestamp = systemHelper.getCurrentTimestamp();
            suggestBadWord.setDeletedBy(username);
            suggestBadWord.setDeletedTime(timestamp);
            suggestBadWordService.store(suggestBadWord);
            suggestHelper.deleteAllBadWord();
            suggestHelper.updateSolrBadwordFile();
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, e.getMessageId(),
                    e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e,
                    "errors.crud_failed_to_delete_crud_table");
        }
    }

    @Override
    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final SuggestBadWord suggestBadWord = createSuggestBadWord();
            suggestBadWordService.store(suggestBadWord);
            suggestHelper.deleteAllBadWord();
            suggestHelper.updateSolrBadwordFile();
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

    @Override
    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String update() {
        try {
            final SuggestBadWord suggestBadWord = createSuggestBadWord();
            suggestBadWordService.store(suggestBadWord);
            suggestHelper.deleteAllBadWord();
            suggestHelper.updateSolrBadwordFile();
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

}
