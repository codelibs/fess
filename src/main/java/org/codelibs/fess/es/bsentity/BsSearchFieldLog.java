package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.SearchFieldLogDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsSearchFieldLog extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public SearchFieldLogDbm asDBMeta() {
        return SearchFieldLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "search_field_log";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** id */
    protected String id;

    /** name */
    protected String name;

    /** searchLogId */
    protected String searchLogId;

    /** value */
    protected String value;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getSearchLogId() {
        return searchLogId;
    }

    public void setSearchLogId(String value) {
        searchLogId = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        value = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (id != null) {
            sourceMap.put("id", id);
        }
        if (name != null) {
            sourceMap.put("name", name);
        }
        if (searchLogId != null) {
            sourceMap.put("searchLogId", searchLogId);
        }
        if (value != null) {
            sourceMap.put("value", value);
        }
        return sourceMap;
    }
}
