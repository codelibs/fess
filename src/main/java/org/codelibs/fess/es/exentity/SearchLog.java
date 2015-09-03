package org.codelibs.fess.es.exentity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.es.bsentity.BsSearchLog;
import org.codelibs.fess.es.exbhv.ClickLogBhv;
import org.codelibs.fess.es.exbhv.SearchFieldLogBhv;
import org.codelibs.fess.es.exbhv.UserInfoBhv;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;

/**
 * @author FreeGen
 */
public class SearchLog extends BsSearchLog {

    private static final long serialVersionUID = 1L;

    private List<ClickLog> clickLogList;

    private List<SearchFieldLog> searchFieldLogList;

    private OptionalEntity<UserInfo> userInfo;

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    @Override
    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    public void setClickLogList(final List<ClickLog> clickLogList) {
        this.clickLogList = clickLogList;

    }

    public String getRequestedTimeForList() {
        final SimpleDateFormat sdf = new SimpleDateFormat(Constants.DEFAULT_DATETIME_FORMAT);
        if (getRequestedTime() != null) {
            return sdf.format(getRequestedTime());
        }
        return null;
    }

    public void addSearchFieldLogValue(final String name, final String value) {
        if (StringUtil.isNotBlank(name) && StringUtil.isNotBlank(value)) {
            final SearchFieldLog fieldLog = new SearchFieldLog();
            fieldLog.setName(name);
            fieldLog.setValue(value);
            if (searchFieldLogList == null) {
                searchFieldLogList = new ArrayList<>();
            }
            searchFieldLogList.add(fieldLog);
        }
    }

    public void setSearchQuery(final String query) {
        addSearchFieldLogValue(Constants.SEARCH_FIELD_LOG_SEARCH_QUERY, query);
    }

    public void setSolrQuery(final String solrQuery) {
        addSearchFieldLogValue(Constants.SEARCH_FIELD_LOG_SOLR_QUERY, solrQuery);
    }

    public OptionalEntity<UserInfo> getUserInfo() {
        if (userInfo == null) {
            final UserInfoBhv userInfoBhv = ComponentUtil.getComponent(UserInfoBhv.class);
            userInfo = userInfoBhv.selectEntity(cb -> {
                cb.query().docMeta().setId_Equal(getUserInfoId());
            });
        }
        return userInfo;
    }

    public void setUserInfo(final OptionalEntity<UserInfo> userInfo) {
        this.userInfo = userInfo;
    }

    public List<ClickLog> getClickLogList() {
        if (clickLogList == null) {
            final ClickLogBhv clickLogBhv = ComponentUtil.getComponent(ClickLogBhv.class);
            clickLogList = clickLogBhv.selectList(cb -> {
                cb.query().setSearchLogId_Equal(getId());
            });
        }
        return clickLogList;
    }

    public List<SearchFieldLog> getSearchFieldLogList() {
        if (searchFieldLogList == null) {
            final SearchFieldLogBhv searchFieldLogBhv = ComponentUtil.getComponent(SearchFieldLogBhv.class);
            searchFieldLogList = searchFieldLogBhv.selectList(cb -> {
                cb.query().setSearchLogId_Equal(getId());
            });
        }
        return searchFieldLogList;
    }

}
