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
package org.codelibs.fess.app.web.admin.searchlist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.net.URLUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.validation.VaMessenger;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Admin action for Search List.
 *
 * @author shinsuke
 */
public class AdminSearchlistAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminSearchlistAction() {
        // Default constructor
    }

    /** Role name for admin search list operations */
    public static final String ROLE = "admin-searchlist";

    // ===================================================================================
    // Constant
    //
    private static final Logger logger = LogManager.getLogger(AdminSearchlistAction.class);

    // ===================================================================================
    // Attribute
    // =========

    /** Client for interacting with the search engine. */
    @Resource
    protected SearchEngineClient searchEngineClient;

    /** Helper for building and parsing search queries. */
    @Resource
    protected QueryHelper queryHelper;

    /** Helper for executing search operations. */
    @Resource
    protected SearchHelper searchHelper;

    /** HTTP servlet request for accessing request parameters. */
    @Resource
    protected HttpServletRequest request;

    /** List of document items returned from search */
    public List<Map<String, Object>> documentItems;

    /** Number of results per page */
    public String pageSize;

    /** Current page number for pagination */
    public String currentPageNumber;

    /** Total number of records found */
    public String allRecordCount;

    /** Total number of pages for pagination */
    public String allPageCount;

    /** Flag indicating if there is a next page */
    public boolean existNextPage;

    /** Flag indicating if there is a previous page */
    public boolean existPrevPage;

    /** Starting record number for current page */
    public String currentStartRecordNumber;

    /** Ending record number for current page */
    public String currentEndRecordNumber;

    /** List of page numbers for pagination */
    public List<String> pageNumberList;

    /** Search execution time in milliseconds */
    public String execTime;

    // ===================================================================================
    // Hook
    // ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);

        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameSearchlist()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    // Search Execute
    // ==============
    /**
     * Displays the search list management index page.
     *
     * @param form the list form for filtering
     * @return HTML response for the search list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final ListForm form) {
        saveToken();
        validate(form, messages -> {}, () -> asHtml(path_AdminError_AdminErrorJsp));
        return asListHtml();
    }

    /**
     * Performs a search operation with the provided form data.
     *
     * @param form the list form containing search criteria
     * @return HTML response with search results
     */
    protected HtmlResponse doSearch(final ListForm form) {
        validate(form, messages -> {}, this::asListHtml);

        if (StringUtil.isBlank(form.q)) {
            // query matches on all documents.
            form.q = Constants.MATCHES_ALL_QUERY;
        }
        final WebRenderData renderData = new WebRenderData();
        form.initialize();
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, Constants.SEARCH_LOG_ACCESS_TYPE_ADMIN);
        try {
            searchHelper.search(form, renderData, getUserBean());
            return asListHtml().renderWith(data -> {
                renderData.register(data);
            });
        } catch (final InvalidQueryException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid query: {}", form.q, e);
            }
            throwValidationError(e.getMessageCode(), this::asListHtml);
        } catch (final ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid offset: {}", form.offset, e);
            }
            throwValidationError(messages -> messages.addErrorsResultSizeExceeded(GLOBAL), this::asListHtml);
        }

        throwValidationError(messages -> messages.addErrorsInvalidQueryUnknown(GLOBAL), this::asListHtml);
        return null; // ignore
    }

    /**
     * Executes a search based on the provided search criteria.
     *
     * @param form the list form containing search criteria
     * @return HTML response with search results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final ListForm form) {
        saveToken();
        return doSearch(form);
    }

    /**
     * Navigates to the previous page of search results.
     *
     * @param form the list form containing current search criteria
     * @return HTML response with previous page results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse prev(final ListForm form) {
        saveToken();
        return doMove(form, -1);
    }

    /**
     * Navigates to the next page of search results.
     *
     * @param form the list form containing current search criteria
     * @return HTML response with next page results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse next(final ListForm form) {
        saveToken();
        return doMove(form, 1);
    }

    /**
     * Navigates to a specific page of search results.
     *
     * @param form the list form containing target page information
     * @return HTML response with specified page results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse move(final ListForm form) {
        saveToken();
        return doMove(form, 0);
    }

    /**
     * Handles pagination movement logic.
     *
     * @param form the list form containing current state
     * @param move the direction to move (-1 for previous, 0 for specific page, 1 for next)
     * @return HTML response with moved page results
     */
    protected HtmlResponse doMove(final ListForm form, final int move) {
        form.initialize();
        Integer pageNumber = form.pn;
        if (pageNumber != null && pageNumber > 0) {
            pageNumber = pageNumber + move;
            if (pageNumber < 1) {
                pageNumber = 1;
            }
            form.start = (pageNumber - 1) * form.getPageSize();
        }
        return doSearch(form);
    }

    // -----------------------------------------------------
    // Confirm
    // -------

    /**
     * Deletes a single document from the search index.
     *
     * @param form the delete form containing document ID
     * @return HTML response redirecting to the list page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final DeleteForm form) {
        validate(form, messages -> {}, this::asListHtml);
        verifyToken(this::asListHtml);
        final String docId = form.docId;
        try {
            final QueryBuilder query = QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), docId);
            searchEngineClient.deleteByQuery(fessConfig.getIndexDocumentUpdateIndex(), query);
            saveInfo(messages -> messages.addSuccessDeleteDocFromIndex(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationError(messages -> messages.addErrorsFailedToDeleteDocInAdmin(GLOBAL), this::asListHtml);
        }
        return asListHtml();
    }

    /**
     * Deletes all documents matching the current search query.
     *
     * @param form the list form containing search criteria
     * @return HTML response redirecting to the list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse deleteall(final ListForm form) {
        validate(form, messages -> {}, this::asListHtml);
        verifyToken(this::asListHtml);
        try {
            searchHelper.deleteByQuery(request, form);
            saveInfo(messages -> messages.addSuccessDeleteDocFromIndex(GLOBAL));
        } catch (final InvalidQueryException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid query: {}", form.q, e);
            }
            throwValidationError(e.getMessageCode(), this::asListHtml);
        }
        return asListHtml();
    }

    /**
     * Displays the form for creating a new document.
     *
     * @param form the create form
     * @return HTML response for the document creation form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew(final CreateForm form) {
        saveToken();
        form.initialize();
        form.crudMode = CrudMode.CREATE;
        getDoc(form).ifPresent(entity -> {
            form.doc = fessConfig.convertToEditableDoc(entity);
        });
        return asEditHtml();
    }

    /**
     * Displays the form for editing an existing document.
     *
     * @param form the edit form containing document ID
     * @return HTML response for the document edit form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        getDoc(form).ifPresent(entity -> {
            form.doc = fessConfig.convertToEditableDoc(entity);
            form.id = (String) entity.remove(fessConfig.getIndexFieldId());
            form.seqNo = (Long) entity.remove(fessConfig.getIndexFieldSeqNo());
            form.primaryTerm = (Long) entity.remove(fessConfig.getIndexFieldPrimaryTerm());
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), this::asListHtml);
        });
        saveToken();
        return asEditHtml();
    }

    /**
     * Creates a new document in the search index.
     *
     * @param form the create form containing document data
     * @return HTML response redirecting to the list page after creation
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, this::asEditHtml);
        validateFields(form.doc, v -> throwValidationError(v, this::asEditHtml));
        verifyToken(this::asEditHtml);
        getDoc(form).ifPresent(entity -> {
            try {
                entity.putAll(fessConfig.convertToStorableDoc(form.doc));

                final String newId = ComponentUtil.getCrawlingInfoHelper().generateId(entity);
                entity.put(fessConfig.getIndexFieldId(), newId);

                final String index = fessConfig.getIndexDocumentUpdateIndex();
                searchEngineClient.store(index, entity);
                saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to add {}", entity, e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL), this::asEditHtml);
        });
        return redirect(getClass());
    }

    /**
     * Updates an existing document in the search index.
     *
     * @param form the edit form containing updated document data
     * @return HTML response redirecting to the search results after update
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, this::asEditHtml);
        validateFields(form.doc, v -> throwValidationError(v, this::asEditHtml));
        verifyToken(this::asEditHtml);
        getDoc(form).ifPresent(entity -> {
            final String index = fessConfig.getIndexDocumentUpdateIndex();
            try {
                entity.putAll(fessConfig.convertToStorableDoc(form.doc));

                final String newId = ComponentUtil.getCrawlingInfoHelper().generateId(entity);
                final String oldId = (String) entity.get(fessConfig.getIndexFieldId());
                if (!newId.equals(oldId)) {
                    entity.put(fessConfig.getIndexFieldId(), newId);
                    entity.remove(fessConfig.getIndexFieldVersion());
                    final Long seqNo = (Long) entity.remove(fessConfig.getIndexFieldSeqNo());
                    final Long primaryTerm = (Long) entity.remove(fessConfig.getIndexFieldPrimaryTerm());
                    if (seqNo != null && primaryTerm != null && oldId != null) {
                        searchEngineClient.delete(index, oldId, seqNo, primaryTerm);
                    }
                }

                searchEngineClient.store(index, entity);
                saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to update {}", entity, e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), this::asEditHtml);
        });
        return redirectWith(getClass(), moreUrl("search").params("q", URLUtil.encode(form.q, Constants.UTF_8)));
    }

    // ===================================================================================
    //                                                                       Validation
    //                                                                           =========
    /**
     * Validates document fields according to index field requirements.
     *
     * @param doc the document to validate
     * @param throwError consumer to handle validation errors
     */
    public static void validateFields(final Map<String, Object> doc, final Consumer<VaMessenger<FessMessages>> throwError) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        try {
            if (!fessConfig.validateIndexRequiredFields(doc)) {
                throwError.accept(messages -> fessConfig.invalidIndexRequiredFields(doc).stream().map(s -> "doc." + s)
                        .forEach(s -> messages.addErrorsPropertyRequired(s, s)));
            }

            if (!fessConfig.validateIndexArrayFields(doc)) {
                throwError.accept(messages -> fessConfig.invalidIndexArrayFields(doc).stream().map(s -> "doc." + s)
                        .forEach(s -> messages.addErrorsPropertyRequired(s, s)));
            }
            if (!fessConfig.validateIndexDateFields(doc)) {
                throwError.accept(messages -> fessConfig.invalidIndexDateFields(doc).stream().map(s -> "doc." + s)
                        .forEach(s -> messages.addErrorsPropertyTypeDate(s, s)));
            }
            if (!fessConfig.validateIndexIntegerFields(doc)) {
                throwError.accept(messages -> fessConfig.invalidIndexIntegerFields(doc).stream().map(s -> "doc." + s)
                        .forEach(s -> messages.addErrorsPropertyTypeInteger(s, s)));
            }
            if (!fessConfig.validateIndexLongFields(doc)) {
                throwError.accept(messages -> fessConfig.invalidIndexLongFields(doc).stream().map(s -> "doc." + s)
                        .forEach(s -> messages.addErrorsPropertyTypeLong(s, s)));
            }
            if (!fessConfig.validateIndexFloatFields(doc)) {
                throwError.accept(messages -> fessConfig.invalidIndexFloatFields(doc).stream().map(s -> "doc." + s)
                        .forEach(s -> messages.addErrorsPropertyTypeFloat(s, s)));
            }
            if (!fessConfig.validateIndexDoubleFields(doc)) {
                throwError.accept(messages -> fessConfig.invalidIndexDoubleFields(doc).stream().map(s -> "doc." + s)
                        .forEach(s -> messages.addErrorsPropertyTypeDouble(s, s)));
            }
        } catch (final Exception e) {
            throwError.accept(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, e.getMessage()));
        }
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========
    /**
     * Verifies that the CRUD mode matches the expected mode.
     *
     * @param crudMode the actual CRUD mode
     * @param expectedMode the expected CRUD mode
     */
    protected void verifyCrudMode(final int crudMode, final int expectedMode) {
        if (crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(crudMode));
            }, this::asListHtml);
        }
    }

    /**
     * Retrieves or creates a document entity based on the form data.
     *
     * @param form the form containing document information
     * @return optional entity containing the document data, or empty if not found
     */
    public static OptionalEntity<Map<String, Object>> getDoc(final CreateForm form) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        switch (form.crudMode) {
        case CrudMode.CREATE:
            final Map<String, Object> entity = new HashMap<>();
            entity.put(fessConfig.getIndexFieldDocId(), systemHelper.generateDocId(entity));
            return OptionalEntity.of(entity);
        case CrudMode.EDIT:
            final String docId;
            if (form.doc != null) {
                docId = (String) form.doc.get(fessConfig.getIndexFieldDocId());
            } else {
                docId = null;
            }
            if (StringUtil.isNotBlank(docId)) {
                return searchEngineClient.getDocument(fessConfig.getIndexDocumentUpdateIndex(), builder -> {
                    builder.setQuery(QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), docId));
                    return true;
                });
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminSearchlist_AdminSearchlistJsp);
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminSearchlist_AdminSearchlistEditJsp);
    }

    /**
     * Web-specific implementation of SearchRenderData for rendering search results in the admin interface.
     */
    protected static class WebRenderData extends SearchRenderData {

        /**
         * Default constructor.
         */
        protected WebRenderData() {
            super();
        }

        /**
         * Registers all search-related data for rendering in the web interface.
         *
         * @param data the render data to populate
         */
        public void register(final RenderData data) {
            RenderDataUtil.register(data, "queryId", queryId);
            RenderDataUtil.register(data, "documentItems", documentItems);
            RenderDataUtil.register(data, "facetResponse", facetResponse);
            RenderDataUtil.register(data, "appendHighlightParams", appendHighlightParams);
            RenderDataUtil.register(data, "execTime", execTime);
            RenderDataUtil.register(data, "pageSize", pageSize);
            RenderDataUtil.register(data, "currentPageNumber", currentPageNumber);
            RenderDataUtil.register(data, "allRecordCount", allRecordCount);
            RenderDataUtil.register(data, "allRecordCountRelation", allRecordCountRelation);
            RenderDataUtil.register(data, "allPageCount", allPageCount);
            RenderDataUtil.register(data, "existNextPage", existNextPage);
            RenderDataUtil.register(data, "existPrevPage", existPrevPage);
            RenderDataUtil.register(data, "currentStartRecordNumber", currentStartRecordNumber);
            RenderDataUtil.register(data, "currentEndRecordNumber", currentEndRecordNumber);
            RenderDataUtil.register(data, "pageNumberList", pageNumberList);
            RenderDataUtil.register(data, "partialResults", partialResults);
            RenderDataUtil.register(data, "queryTime", queryTime);
            RenderDataUtil.register(data, "searchQuery", searchQuery);
            RenderDataUtil.register(data, "requestedTime", requestedTime);
        }
    }
}
