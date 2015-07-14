package org.codelibs.fess.es.exentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.auth.NTLMScheme;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.es.bsentity.BsDataConfig;
import org.codelibs.fess.es.exbhv.FileConfigToLabelBhv;
import org.codelibs.fess.es.exbhv.FileConfigToRoleBhv;
import org.codelibs.fess.es.exbhv.LabelTypeBhv;
import org.codelibs.fess.es.exbhv.RoleTypeBhv;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;
import org.codelibs.robot.client.S2RobotClientFactory;
import org.codelibs.robot.client.http.Authentication;
import org.codelibs.robot.client.http.HcHttpClient;
import org.codelibs.robot.client.http.impl.AuthenticationImpl;
import org.codelibs.robot.client.http.ntlm.JcifsEngine;
import org.codelibs.robot.client.smb.SmbAuthentication;
import org.codelibs.robot.client.smb.SmbClient;
import org.dbflute.cbean.result.ListResultBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author FreeGen
 */
public class DataConfig extends BsDataConfig implements CrawlingConfig {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(DataConfig.class);

    private static final String S2ROBOT_WEB_HEADER_PREFIX = "s2robot.web.header.";

    private static final String S2ROBOT_WEB_AUTH = "s2robot.web.auth";

    private static final String S2ROBOT_USERAGENT = "s2robot.useragent";

    private static final String S2ROBOT_PARAM_PREFIX = "s2robot.param.";

    private static final Object S2ROBOT_FILE_AUTH = "s2robot.file.auth";

    private String[] labelTypeIds;

    private String[] roleTypeIds;

    protected Pattern[] includedDocPathPatterns;

    protected Pattern[] excludedDocPathPatterns;

    private Map<String, String> handlerParameterMap;

    private Map<String, String> handlerScriptMap;

    private List<LabelType> labelTypeList;

    private List<RoleType> roleTypeList;

    public DataConfig() {
        super();
        setBoost(1.0f);
    }

    public String[] getLabelTypeIds() {
        if (labelTypeIds == null) {
            return StringUtil.EMPTY_STRINGS;
        }
        return labelTypeIds;
    }

    public void setLabelTypeIds(final String[] labelTypeIds) {
        this.labelTypeIds = labelTypeIds;
    }

    public List<LabelType> getLabelTypeList() {
        if (labelTypeList == null) {
            synchronized (this) {
                if (labelTypeList == null) {
                    final FileConfigToLabelBhv fileConfigToLabelBhv = ComponentUtil.getComponent(FileConfigToLabelBhv.class);
                    final ListResultBean<FileConfigToLabel> mappingList = fileConfigToLabelBhv.selectList(cb -> {
                        cb.query().setFileConfigId_Equal(getId());
                        cb.specify().columnLabelTypeId();
                    });
                    final List<String> labelIdList = new ArrayList<>();
                    for (final FileConfigToLabel mapping : mappingList) {
                        labelIdList.add(mapping.getLabelTypeId());
                    }
                    final LabelTypeBhv labelTypeBhv = ComponentUtil.getComponent(LabelTypeBhv.class);
                    labelTypeList = labelTypeBhv.selectList(cb -> {
                        cb.query().setId_InScope(labelIdList);
                        cb.query().addOrderBy_SortOrder_Asc();
                    });
                }
            }
        }
        return labelTypeList;
    }

    @Override
    public String[] getLabelTypeValues() {
        final List<LabelType> list = getLabelTypeList();
        final List<String> labelValueList = new ArrayList<>(list.size());
        for (final LabelType labelType : list) {
            labelValueList.add(labelType.getValue());
        }
        return labelValueList.toArray(new String[labelValueList.size()]);
    }

    public String[] getRoleTypeIds() {
        if (roleTypeIds == null) {
            return StringUtil.EMPTY_STRINGS;
        }
        return roleTypeIds;
    }

    public void setRoleTypeIds(final String[] roleTypeIds) {
        this.roleTypeIds = roleTypeIds;
    }

    public List<RoleType> getRoleTypeList() {
        if (roleTypeList == null) {
            synchronized (this) {
                if (roleTypeList == null) {
                    final FileConfigToRoleBhv fileConfigToRoleBhv = ComponentUtil.getComponent(FileConfigToRoleBhv.class);
                    final ListResultBean<FileConfigToRole> mappingList = fileConfigToRoleBhv.selectList(cb -> {
                        cb.query().setFileConfigId_Equal(getId());
                        cb.specify().columnRoleTypeId();
                    });
                    final List<String> roleIdList = new ArrayList<>();
                    for (final FileConfigToRole mapping : mappingList) {
                        roleIdList.add(mapping.getRoleTypeId());
                    }
                    final RoleTypeBhv roleTypeBhv = ComponentUtil.getComponent(RoleTypeBhv.class);
                    roleTypeList = roleTypeBhv.selectList(cb -> {
                        cb.query().setId_InScope(roleIdList);
                        cb.query().addOrderBy_SortOrder_Asc();
                    });
                }
            }
        }
        return roleTypeList;
    }

    @Override
    public String[] getRoleTypeValues() {
        final List<RoleType> list = getRoleTypeList();
        final List<String> roleValueList = new ArrayList<>(list.size());
        for (final RoleType roleType : list) {
            roleValueList.add(roleType.getValue());
        }
        return roleValueList.toArray(new String[roleValueList.size()]);
    }

    @Override
    public String getDocumentBoost() {
        return Float.valueOf(getBoost().floatValue()).toString();
    }

    public String getBoostValue() {
        if (boost != null) {
            return boost.toString();
        }
        return null;
    }

    public void setBoostValue(final String value) {
        if (value != null) {
            try {
                boost = Float.parseFloat(value);
            } catch (final Exception e) {}
        }
    }

    @Override
    public String getIndexingTarget(final String input) {
        // always return true
        return Constants.TRUE;
    }

    @Override
    public String getConfigId() {
        return ConfigType.DATA.getConfigId(getId());
    }

    public Map<String, String> getHandlerParameterMap() {
        if (handlerParameterMap == null) {
            handlerParameterMap = ParameterUtil.parse(getHandlerParameter());
        }
        return handlerParameterMap;
    }

    public Map<String, String> getHandlerScriptMap() {
        if (handlerScriptMap == null) {
            handlerScriptMap = ParameterUtil.parse(getHandlerScript());
        }
        return handlerScriptMap;
    }

    @Override
    public void initializeClientFactory(final S2RobotClientFactory robotClientFactory) {
        final Map<String, String> paramMap = getHandlerParameterMap();

        final Map<String, Object> factoryParamMap = new HashMap<String, Object>();
        robotClientFactory.setInitParameterMap(factoryParamMap);

        // parameters
        for (final Map.Entry<String, String> entry : paramMap.entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith(S2ROBOT_PARAM_PREFIX)) {
                factoryParamMap.put(key.substring(S2ROBOT_PARAM_PREFIX.length()), entry.getValue());
            }
        }

        // user agent
        final String userAgent = paramMap.get(S2ROBOT_USERAGENT);
        if (StringUtil.isNotBlank(userAgent)) {
            factoryParamMap.put(HcHttpClient.USER_AGENT_PROPERTY, userAgent);
        }

        // web auth
        final String webAuthStr = paramMap.get(S2ROBOT_WEB_AUTH);
        if (StringUtil.isNotBlank(webAuthStr)) {
            final String[] webAuthNames = webAuthStr.split(",");
            final List<Authentication> basicAuthList = new ArrayList<Authentication>();
            for (final String webAuthName : webAuthNames) {
                final String scheme = paramMap.get(S2ROBOT_WEB_AUTH + "." + webAuthName + ".scheme");
                final String hostname = paramMap.get(S2ROBOT_WEB_AUTH + "." + webAuthName + ".host");
                final String port = paramMap.get(S2ROBOT_WEB_AUTH + "." + webAuthName + ".port");
                final String realm = paramMap.get(S2ROBOT_WEB_AUTH + "." + webAuthName + ".realm");
                final String username = paramMap.get(S2ROBOT_WEB_AUTH + "." + webAuthName + ".username");
                final String password = paramMap.get(S2ROBOT_WEB_AUTH + "." + webAuthName + ".password");

                if (StringUtil.isEmpty(username)) {
                    logger.warn("username is empty. webAuth:" + webAuthName);
                    continue;
                }

                AuthScheme authScheme = null;
                if (Constants.BASIC.equals(scheme)) {
                    authScheme = new BasicScheme();
                } else if (Constants.DIGEST.equals(scheme)) {
                    authScheme = new DigestScheme();
                } else if (Constants.NTLM.equals(scheme)) {
                    authScheme = new NTLMScheme(new JcifsEngine());
                }

                AuthScope authScope;
                if (StringUtil.isBlank(hostname)) {
                    authScope = AuthScope.ANY;
                } else {
                    int p = AuthScope.ANY_PORT;
                    if (StringUtil.isNotBlank(port)) {
                        try {
                            p = Integer.parseInt(port);
                        } catch (final NumberFormatException e) {
                            logger.warn("Failed to parse " + port, e);
                        }
                    }

                    String r = realm;
                    if (StringUtil.isBlank(realm)) {
                        r = AuthScope.ANY_REALM;
                    }

                    String s = scheme;
                    if (StringUtil.isBlank(scheme) || Constants.NTLM.equals(scheme)) {
                        s = AuthScope.ANY_SCHEME;
                    }
                    authScope = new AuthScope(hostname, p, r, s);
                }

                Credentials credentials;
                if (Constants.NTLM.equals(scheme)) {
                    final String workstation = paramMap.get(S2ROBOT_WEB_AUTH + "." + webAuthName + ".workstation");
                    final String domain = paramMap.get(S2ROBOT_WEB_AUTH + "." + webAuthName + ".domain");
                    credentials =
                            new NTCredentials(username, password == null ? StringUtil.EMPTY : password,
                                    workstation == null ? StringUtil.EMPTY : workstation, domain == null ? StringUtil.EMPTY : domain);
                } else {
                    credentials = new UsernamePasswordCredentials(username, password == null ? StringUtil.EMPTY : password);
                }

                basicAuthList.add(new AuthenticationImpl(authScope, credentials, authScheme));
            }
            factoryParamMap.put(HcHttpClient.BASIC_AUTHENTICATIONS_PROPERTY,
                    basicAuthList.toArray(new Authentication[basicAuthList.size()]));
        }

        // request header
        final List<org.codelibs.robot.client.http.RequestHeader> rhList = new ArrayList<org.codelibs.robot.client.http.RequestHeader>();
        int count = 1;
        String headerName = paramMap.get(S2ROBOT_WEB_HEADER_PREFIX + count + ".name");
        while (StringUtil.isNotBlank(headerName)) {
            final String headerValue = paramMap.get(S2ROBOT_WEB_HEADER_PREFIX + count + ".value");
            rhList.add(new org.codelibs.robot.client.http.RequestHeader(headerName, headerValue));
            count++;
            headerName = paramMap.get(S2ROBOT_WEB_HEADER_PREFIX + count + ".name");
        }
        if (!rhList.isEmpty()) {
            factoryParamMap.put(HcHttpClient.REQUERT_HEADERS_PROPERTY,
                    rhList.toArray(new org.codelibs.robot.client.http.RequestHeader[rhList.size()]));
        }

        // file auth
        final String fileAuthStr = paramMap.get(S2ROBOT_FILE_AUTH);
        if (StringUtil.isNotBlank(fileAuthStr)) {
            final String[] fileAuthNames = fileAuthStr.split(",");
            final List<SmbAuthentication> smbAuthList = new ArrayList<SmbAuthentication>();
            for (final String fileAuthName : fileAuthNames) {
                final String scheme = paramMap.get(S2ROBOT_FILE_AUTH + "." + fileAuthName + ".scheme");
                if (Constants.SAMBA.equals(scheme)) {
                    final String domain = paramMap.get(S2ROBOT_FILE_AUTH + "." + fileAuthName + ".domain");
                    final String hostname = paramMap.get(S2ROBOT_FILE_AUTH + "." + fileAuthName + ".host");
                    final String port = paramMap.get(S2ROBOT_FILE_AUTH + "." + fileAuthName + ".port");
                    final String username = paramMap.get(S2ROBOT_FILE_AUTH + "." + fileAuthName + ".username");
                    final String password = paramMap.get(S2ROBOT_FILE_AUTH + "." + fileAuthName + ".password");

                    if (StringUtil.isEmpty(username)) {
                        logger.warn("username is empty. fileAuth:" + fileAuthName);
                        continue;
                    }

                    final SmbAuthentication smbAuth = new SmbAuthentication();
                    smbAuth.setDomain(domain == null ? StringUtil.EMPTY : domain);
                    smbAuth.setServer(hostname);
                    if (StringUtil.isNotBlank(port)) {
                        try {
                            smbAuth.setPort(Integer.parseInt(port));
                        } catch (final NumberFormatException e) {
                            logger.warn("Failed to parse " + port, e);
                        }
                    }
                    smbAuth.setUsername(username);
                    smbAuth.setPassword(password == null ? StringUtil.EMPTY : password);
                    smbAuthList.add(smbAuth);
                }
            }
            if (!smbAuthList.isEmpty()) {
                factoryParamMap.put(SmbClient.SMB_AUTHENTICATIONS_PROPERTY, smbAuthList.toArray(new SmbAuthentication[smbAuthList.size()]));
            }
        }

    }

    @Override
    public Map<String, String> getConfigParameterMap(final ConfigName name) {
        return Collections.emptyMap();
    }

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    @Override
    public void setId(String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(Long version) {
        asDocMeta().version(version);
    }
}
