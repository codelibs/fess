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

package jp.sf.fess.action.admin.dict;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.util.StringUtil;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.codelibs.solr.lib.SolrGroupManager;
import org.codelibs.solr.lib.response.CoreReloadResponse;
import org.seasar.framework.beans.util.Beans;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.seasar.struts.util.ResponseUtil;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.dict.DictionaryExpiredException;
import jp.sf.fess.dict.synonym.SynonymFile;
import jp.sf.fess.dict.synonym.SynonymItem;
import jp.sf.fess.form.admin.dict.SynonymForm;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.pager.SynonymPager;
import jp.sf.fess.service.SynonymService;

public class SynonymAction {

    private static final Log log = LogFactory.getLog(SynonymAction.class);

    @Resource
    @ActionForm
    protected SynonymForm synonymForm;

    @Resource
    protected SynonymService synonymService;

    @Resource
    protected SynonymPager synonymPager;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected SolrGroupManager solrGroupManager;

    public List<SynonymItem> synonymItemItems;

    public String filename;

    public String coreName;

    public String getHelpLink() {
        return systemHelper.getHelpLink("dict");
    }

    protected String displayList(final boolean redirect) {
        // page navi
        synonymItemItems = synonymService.getSynonymList(synonymForm.dictId,
                synonymPager);

        // restore from pager
        Beans.copy(synonymPager, synonymForm.searchParams)
                .excludes(CommonConstants.PAGER_CONVERSION_RULE).execute();

        if (redirect) {
            return "index?dictId=" + synonymForm.dictId + "&redirect=true";
        } else {
            return "index.jsp";
        }
    }

    @Execute(validator = false, input = "error.jsp")
    public String index() {
        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp", urlPattern = "list/{dictId}/{pageNumber}")
    public String list() {
        // page navi
        if (StringUtil.isNotBlank(synonymForm.pageNumber)) {
            try {
                synonymPager.setCurrentPageNumber(Integer
                        .parseInt(synonymForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid value: " + synonymForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(synonymForm.searchParams, synonymPager)
                .excludes(CommonConstants.PAGER_CONVERSION_RULE).execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        synonymPager.clear();

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

    @Execute(validator = false, input = "error.jsp", urlPattern = "confirmpage/{dictId}/{crudMode}/{id}")
    public String confirmpage() {
        if (synonymForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.CONFIRM_MODE,
                            synonymForm.crudMode });
        }

        loadSynonym();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        synonymForm.initialize();
        synonymForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{dictId}/{crudMode}/{id}")
    public String editpage() {
        if (synonymForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.EDIT_MODE,
                            synonymForm.crudMode });
        }

        loadSynonym();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        synonymForm.crudMode = CommonConstants.EDIT_MODE;

        loadSynonym();

        return "edit.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    @Execute(validator = true, input = "edit.jsp")
    public String confirmfromcreate() {
        final String[] newInputs = splitLine(synonymForm.inputs);
        validateSynonymString(newInputs);
        final String[] newOutputs = splitLine(synonymForm.outputs);
        validateSynonymString(newOutputs);

        return "confirm.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    @Execute(validator = true, input = "edit.jsp")
    public String confirmfromupdate() {
        final String[] newInputs = splitLine(synonymForm.inputs);
        validateSynonymString(newInputs);
        final String[] newOutputs = splitLine(synonymForm.outputs);
        validateSynonymString(newOutputs);

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "deletepage/{dictId}/{crudMode}/{id}")
    public String deletepage() {
        if (synonymForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            synonymForm.crudMode });
        }

        loadSynonym();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        synonymForm.crudMode = CommonConstants.DELETE_MODE;

        loadSynonym();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final SynonymItem synonymItem = createSynonym();
            synonymService.store(synonymForm.dictId, synonymItem);
            SAStrutsUtil.addSessionMessage("success.crud_create_crud_table");

            return displayList(true);
        } catch (final DictionaryExpiredException e) {
            throw e;
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
                    "errors.crud_failed_to_create_crud_table");
        }
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String update() {
        try {
            final SynonymItem synonymItem = createSynonym();
            synonymService.store(synonymForm.dictId, synonymItem);
            SAStrutsUtil.addSessionMessage("success.crud_update_crud_table");

            return displayList(true);
        } catch (final DictionaryExpiredException e) {
            throw e;
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
                    "errors.crud_failed_to_update_crud_table");
        }
    }

    @Token(save = false, validate = true)
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (synonymForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            synonymForm.crudMode });
        }

        try {
            final SynonymItem synonymItem = synonymService.getSynonym(
                    synonymForm.dictId, createKeyMap());
            if (synonymItem == null) {
                // throw an exception
                throw new ActionMessagesException(
                        "errors.crud_could_not_find_crud_table",

                        new Object[] { synonymForm.id });

            }

            synonymService.delete(synonymForm.dictId, synonymItem);
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final DictionaryExpiredException e) {
            throw e;
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

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "downloadpage")
    public String downloadpage() {
        final SynonymFile synonymFile = synonymService
                .getSynonymFile(synonymForm.dictId);
        if (synonymFile == null) {
            throw new SSCActionMessagesException(
                    "errors.synonym_file_is_not_found");
        }
        filename = synonymFile.getCoreName() + ":"
                + synonymFile.getSimpleName();
        return "download.jsp";
    }

    @Token(save = true, validate = true)
    @Execute(validator = false, input = "downloadpage")
    public String download() {
        final SynonymFile synonymFile = synonymService
                .getSynonymFile(synonymForm.dictId);
        if (synonymFile == null) {
            throw new SSCActionMessagesException(
                    "errors.synonym_file_is_not_found");
        }
        try (InputStream in = synonymFile.getInputStream()) {
            ResponseUtil.download(synonymFile.getSimpleName(), in);
        } catch (final IOException e) {
            throw new SSCActionMessagesException(
                    "errors.failed_to_download_synonym_file");
        }

        return null;
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "uploadpage")
    public String uploadpage() {
        final SynonymFile synonymFile = synonymService
                .getSynonymFile(synonymForm.dictId);
        if (synonymFile == null) {
            throw new SSCActionMessagesException(
                    "errors.synonym_file_is_not_found");
        }
        filename = synonymFile.getCoreName() + ":"
                + synonymFile.getSimpleName();
        return "upload.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "uploadpage")
    public String upload() {
        final SynonymFile synonymFile = synonymService
                .getSynonymFile(synonymForm.dictId);
        if (synonymFile == null) {
            throw new SSCActionMessagesException(
                    "errors.synonym_file_is_not_found");
        }
        try (InputStream in = synonymForm.synonymFile.getInputStream()) {
            synonymFile.update(in);
        } catch (final IOException e) {
            throw new SSCActionMessagesException(
                    "errors.failed_to_upload_synonym_file");
        }

        SAStrutsUtil.addSessionMessage("success.upload_synonym_file");

        return "uploadpage?dictId=" + synonymForm.dictId + "&redirect=true";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "reloadpage")
    public String reloadpage() {
        final SynonymFile synonymFile = synonymService
                .getSynonymFile(synonymForm.dictId);
        if (synonymFile == null) {
            throw new SSCActionMessagesException(
                    "errors.synonym_file_is_not_found");
        }
        coreName = synonymFile.getCoreName();
        return "reload.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "reloadpage")
    public String reload() {
        final SynonymFile synonymFile = synonymService
                .getSynonymFile(synonymForm.dictId);
        if (synonymFile == null) {
            throw new SSCActionMessagesException(
                    "errors.synonym_file_is_not_found");
        }

        CoreReloadResponse response = solrGroupManager.reloadCore(synonymFile
                .getCoreName());
        if (response.getStatus() == 0) {
            SAStrutsUtil.addSessionMessage("success.reload_core");
        } else {
            throw new SSCActionMessagesException("errors.failed_to_reload_core");
        }

        return "reloadpage?dictId=" + synonymForm.dictId + "&redirect=true";
    }

    protected void loadSynonym() {

        final SynonymItem synonymItem = synonymService.getSynonym(
                synonymForm.dictId, createKeyMap());
        if (synonymItem == null) {
            // throw an exception
            throw new ActionMessagesException(
                    "errors.crud_could_not_find_crud_table",
                    new Object[] { synonymForm.id });

        }

        synonymForm.id = Long.toString(synonymItem.getId());
        synonymForm.inputs = StringUtils.join(synonymItem.getInputs(), "\n");
        synonymForm.outputs = StringUtils.join(synonymItem.getOutputs(), "\n");
    }

    protected SynonymItem createSynonym() {
        SynonymItem synonymItem;
        if (synonymForm.crudMode == CommonConstants.EDIT_MODE) {
            synonymItem = synonymService.getSynonym(synonymForm.dictId,
                    createKeyMap());
            if (synonymItem == null) {
                // throw an exception
                throw new ActionMessagesException(
                        "errors.crud_could_not_find_crud_table",

                        new Object[] { synonymForm.id });

            }
        } else {
            synonymItem = new SynonymItem(0, StringUtil.EMPTY_STRINGS,
                    StringUtil.EMPTY_STRINGS);
        }

        final String[] newInputs = splitLine(synonymForm.inputs);
        validateSynonymString(newInputs);
        synonymItem.setNewInputs(newInputs);
        final String[] newOutputs = splitLine(synonymForm.outputs);
        validateSynonymString(newOutputs);
        synonymItem.setNewOutputs(newOutputs);

        return synonymItem;
    }

    private void validateSynonymString(String[] values) {
        if (values.length == 0) {
            return;
        }
        for (String value : values) {
            if (value.indexOf(',') >= 0) {
                throw new SSCActionMessagesException(
                        "errors.invalid_str_is_included", value, ",");
            }
            if (value.indexOf("=>") >= 0) {
                throw new SSCActionMessagesException(
                        "errors.invalid_str_is_included", value, "=>");
            }
        }
    }

    private String[] splitLine(final String value) {
        if (StringUtil.isBlank(value)) {
            return StringUtil.EMPTY_STRINGS;
        }
        final String[] values = value.split("[\r\n]");
        final List<String> list = new ArrayList<String>(values.length);
        for (final String line : values) {
            if (StringUtil.isNotBlank(line)) {
                list.add(line.trim());
            }
        }
        return list.toArray(new String[list.size()]);
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", synonymForm.id);
        return keys;
    }

}
