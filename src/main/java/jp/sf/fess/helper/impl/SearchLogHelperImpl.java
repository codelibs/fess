package jp.sf.fess.helper.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.db.cbean.SearchLogCB;
import jp.sf.fess.db.cbean.UserInfoCB;
import jp.sf.fess.db.exbhv.ClickLogBhv;
import jp.sf.fess.db.exbhv.SearchFieldLogBhv;
import jp.sf.fess.db.exbhv.SearchLogBhv;
import jp.sf.fess.db.exbhv.UserInfoBhv;
import jp.sf.fess.db.exentity.ClickLog;
import jp.sf.fess.db.exentity.SearchLog;
import jp.sf.fess.db.exentity.UserInfo;
import jp.sf.fess.helper.SearchLogHelper;
import jp.sf.fess.service.SearchLogService;
import jp.sf.fess.service.UserInfoService;
import jp.sf.fess.util.FessBeans;

import org.seasar.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchLogHelperImpl extends SearchLogHelper {
    private static final Logger logger = LoggerFactory // NOPMD
            .getLogger(SearchLogHelperImpl.class);

    @Resource
    protected UserInfoService userInfoService;

    @Resource
    protected SearchLogService searchLogService;

    @Resource
    protected SearchLogBhv searchLogBhv;

    @Resource
    protected SearchFieldLogBhv searchFieldLogBhv;

    @Resource
    protected ClickLogBhv clickLogBhv;

    @Resource
    protected UserInfoBhv userInfoBhv;

    @Override
    public void updateUserInfo(final String userCode) {
        final long current = System.currentTimeMillis();
        final Long time = userInfoCache.get(userCode);
        if (time == null || current - time.longValue() > userCheckInterval) {
            UserInfo userInfo = userInfoService.getUserInfo(userCode);
            if (userInfo == null) {
                final Timestamp now = new Timestamp(current);
                userInfo = new UserInfo();
                userInfo.setCode(userCode);
                userInfo.setCreatedTime(now);
                userInfo.setUpdatedTime(now);
                userInfoService.store(userInfo);
            }
            userInfoCache.put(userCode, current);
        }
    }

    @Override
    protected void processSearchLogQueue(final Queue<SearchLog> queue) {
        final List<SearchLog> searchLogList = new ArrayList<SearchLog>();
        final String value = crawlerProperties.getProperty(
                Constants.PURGE_BY_BOTS_PROPERTY, Constants.EMPTY_STRING);
        String[] botNames;
        if (StringUtil.isBlank(value)) {
            botNames = new String[0];
        } else {
            botNames = value.split(",");
        }

        final Map<String, UserInfo> userInfoMap = new HashMap<String, UserInfo>();
        for (final SearchLog searchLog : queue) {
            boolean add = true;
            for (final String botName : botNames) {
                if (searchLog.getUserAgent() != null
                        && searchLog.getUserAgent().indexOf(botName) >= 0) {
                    add = false;
                    break;
                }
            }
            if (add) {
                final UserInfo userInfo = searchLog.getUserInfo();
                if (userInfo != null) {
                    final String code = userInfo.getCode();
                    final UserInfo oldUserInfo = userInfoMap.get(code);
                    if (oldUserInfo != null) {
                        userInfo.setCreatedTime(oldUserInfo.getCreatedTime());
                    }
                    userInfoMap.put(code, userInfo);
                }
                searchLogList.add(searchLog);
            }
        }

        if (!userInfoMap.isEmpty()) {
            final List<UserInfo> insertList = new ArrayList<UserInfo>(
                    userInfoMap.values());
            final List<UserInfo> updateList = new ArrayList<UserInfo>();
            final UserInfoCB cb = new UserInfoCB();
            cb.query().setCode_InScope(userInfoMap.keySet());
            final List<UserInfo> list = userInfoBhv.selectList(cb);
            for (final UserInfo userInfo : list) {
                final String code = userInfo.getCode();
                final UserInfo entity = userInfoMap.get(code);
                FessBeans.copy(userInfo, entity).includes("id", "createdTime")
                        .execute();
                updateList.add(entity);
                insertList.remove(entity);
            }
            userInfoBhv.batchInsert(insertList);
            userInfoBhv.batchUpdate(updateList);
            for (final SearchLog searchLog : searchLogList) {
                final UserInfo userInfo = searchLog.getUserInfo();
                if (userInfo != null) {
                    final UserInfo entity = userInfoMap.get(userInfo.getCode());
                    searchLog.setUserId(entity.getId());
                }
            }
        }

        if (!searchLogList.isEmpty()) {
            searchLogService.store(searchLogList);
        }
    }

    @Override
    protected void processClickLogQueue(final Queue<ClickLog> queue) {
        final List<ClickLog> clickLogList = new ArrayList<ClickLog>();
        for (final ClickLog clickLog : queue) {
            final SearchLogCB cb = new SearchLogCB();
            cb.query().setRequestedTime_Equal(clickLog.getQueryRequestedTime());
            cb.query().setUserSessionId_Equal(clickLog.getUserSessionId());
            final SearchLog entity = searchLogBhv.selectEntity(cb);
            if (entity != null) {
                clickLog.setSearchId(entity.getId());
                clickLogList.add(clickLog);
            } else {
                logger.warn("Not Found[ClickLog]: " + clickLog);
            }
        }
        if (!clickLogList.isEmpty()) {
            clickLogBhv.batchInsert(clickLogList);
        }
    }
}
