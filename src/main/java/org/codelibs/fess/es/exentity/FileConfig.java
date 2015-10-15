package org.codelibs.fess.es.exentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.FileAuthenticationService;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.client.smb.SmbAuthentication;
import org.codelibs.fess.crawler.client.smb.SmbClient;
import org.codelibs.fess.es.bsentity.BsFileConfig;
import org.codelibs.fess.es.exbhv.FileConfigToLabelBhv;
import org.codelibs.fess.es.exbhv.FileConfigToRoleBhv;
import org.codelibs.fess.es.exbhv.LabelTypeBhv;
import org.codelibs.fess.es.exbhv.RoleTypeBhv;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;
import org.dbflute.cbean.result.ListResultBean;
import org.lastaflute.di.core.SingletonLaContainer;

/**
 * @author FreeGen
 */
public class FileConfig extends BsFileConfig implements CrawlingConfig {

    private static final long serialVersionUID = 1L;

    private String[] labelTypeIds;

    private String[] roleTypeIds;

    protected volatile Pattern[] includedDocPathPatterns;

    protected volatile Pattern[] excludedDocPathPatterns;

    protected volatile Map<ConfigName, Map<String, String>> configParameterMap;

    private List<LabelType> labelTypeList;

    private List<RoleType> roleTypeList;

    public FileConfig() {
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

    @Override
    public String getIndexingTarget(final String input) {
        if (includedDocPathPatterns == null || excludedDocPathPatterns == null) {
            initDocPathPattern();
        }

        if (includedDocPathPatterns.length == 0 && excludedDocPathPatterns.length == 0) {
            return Constants.TRUE;
        }

        for (final Pattern pattern : includedDocPathPatterns) {
            if (pattern.matcher(input).matches()) {
                return Constants.TRUE;
            }
        }

        for (final Pattern pattern : excludedDocPathPatterns) {
            if (pattern.matcher(input).matches()) {
                return Constants.FALSE;
            }
        }

        return Constants.TRUE;

    }

    protected synchronized void initDocPathPattern() {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();

        if (includedDocPathPatterns == null) {
            if (StringUtil.isNotBlank(getIncludedDocPaths())) {
                final List<Pattern> pathPatterList = new ArrayList<Pattern>();
                final String[] paths = getIncludedDocPaths().split("[\r\n]");
                for (final String u : paths) {
                    if (StringUtil.isNotBlank(u) && !u.trim().startsWith("#")) {
                        pathPatterList.add(Pattern.compile(systemHelper.encodeUrlFilter(u.trim())));
                    }
                }
                includedDocPathPatterns = pathPatterList.toArray(new Pattern[pathPatterList.size()]);
            } else {
                includedDocPathPatterns = new Pattern[0];
            }
        }

        if (excludedDocPathPatterns == null) {
            if (StringUtil.isNotBlank(getExcludedDocPaths())) {
                final List<Pattern> pathPatterList = new ArrayList<Pattern>();
                final String[] paths = getExcludedDocPaths().split("[\r\n]");
                for (final String u : paths) {
                    if (StringUtil.isNotBlank(u) && !u.trim().startsWith("#")) {
                        pathPatterList.add(Pattern.compile(systemHelper.encodeUrlFilter(u.trim())));
                    }
                }
                excludedDocPathPatterns = pathPatterList.toArray(new Pattern[pathPatterList.size()]);
            } else if (includedDocPathPatterns.length > 0) {
                excludedDocPathPatterns = new Pattern[] { Pattern.compile(".*") };
            } else {
                excludedDocPathPatterns = new Pattern[0];
            }
        }
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
    public String getConfigId() {
        return ConfigType.FILE.getConfigId(getId());
    }

    @Override
    public void initializeClientFactory(final CrawlerClientFactory clientFactory) {
        final FileAuthenticationService fileAuthenticationService = SingletonLaContainer.getComponent(FileAuthenticationService.class);

        //  Parameters
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        clientFactory.setInitParameterMap(paramMap);

        final Map<String, String> clientConfigMap = getConfigParameterMap(ConfigName.CLIENT);
        if (clientConfigMap != null) {
            paramMap.putAll(clientConfigMap);
        }

        // auth params
        final List<FileAuthentication> fileAuthList = fileAuthenticationService.getFileAuthenticationList(getId());
        final List<SmbAuthentication> smbAuthList = new ArrayList<SmbAuthentication>();
        for (final FileAuthentication fileAuth : fileAuthList) {
            if (Constants.SAMBA.equals(fileAuth.getProtocolScheme())) {
                final SmbAuthentication smbAuth = new SmbAuthentication();
                final Map<String, String> map = ParameterUtil.parse(fileAuth.getParameters());
                final String domain = map.get("domain");
                smbAuth.setDomain(domain == null ? StringUtil.EMPTY : domain);
                smbAuth.setServer(fileAuth.getHostname());
                smbAuth.setPort(fileAuth.getPort());
                smbAuth.setUsername(fileAuth.getUsername());
                smbAuth.setPassword(fileAuth.getPassword());
                smbAuthList.add(smbAuth);
            }
        }
        paramMap.put(SmbClient.SMB_AUTHENTICATIONS_PROPERTY, smbAuthList.toArray(new SmbAuthentication[smbAuthList.size()]));

    }

    @Override
    public Map<String, String> getConfigParameterMap(final ConfigName name) {
        if (configParameterMap == null) {
            configParameterMap = ParameterUtil.createConfigParameterMap(getConfigParameter());
        }

        final Map<String, String> configMap = configParameterMap.get(name);
        if (configMap == null) {
            return Collections.emptyMap();
        }
        return configMap;
    }

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
}
