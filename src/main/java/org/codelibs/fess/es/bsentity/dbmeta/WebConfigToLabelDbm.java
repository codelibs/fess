package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.exentity.WebConfigToLabel;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

public class WebConfigToLabelDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final WebConfigToLabelDbm _instance = new WebConfigToLabelDbm();

    private WebConfigToLabelDbm() {
    }

    public static WebConfigToLabelDbm getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                    Property Gateway
    //                                                                    ================
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    {
        setupEpg(_epgMap, et -> ((WebConfigToLabel) et).getId(), (et, vl) -> ((WebConfigToLabel) et).setId(DfTypeUtil.toString(vl)), "id");
        setupEpg(_epgMap, et -> ((WebConfigToLabel) et).getLabelTypeId(),
                (et, vl) -> ((WebConfigToLabel) et).setLabelTypeId(DfTypeUtil.toString(vl)), "labelTypeId");
        setupEpg(_epgMap, et -> ((WebConfigToLabel) et).getWebConfigId(),
                (et, vl) -> ((WebConfigToLabel) et).setWebConfigId(DfTypeUtil.toString(vl)), "webConfigId");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnLabelTypeId = cci("labelTypeId", "labelTypeId", null, null, String.class, "labelTypeId", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnWebConfigId = cci("webConfigId", "webConfigId", null, null, String.class, "webConfigId", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnLabelTypeId() {
        return _columnLabelTypeId;
    }

    public ColumnInfo columnWebConfigId() {
        return _columnWebConfigId;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnLabelTypeId());
        ls.add(columnWebConfigId());
        return ls;
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "web_config_to_label";
    protected final String _tableDispName = "web_config_to_label";
    protected final String _tablePropertyName = "WebConfigToLabel";

    public String getTableDbName() {
        return _tableDbName;
    }

    @Override
    public String getTableDispName() {
        return _tableDispName;
    }

    @Override
    public String getTablePropertyName() {
        return _tablePropertyName;
    }

    @Override
    public TableSqlName getTableSqlName() {
        return null;
    }

    @Override
    public String getProjectName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProjectPrefix() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getGenerationGapBasePrefix() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DBDef getCurrentDBDef() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasPrimaryKey() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasCompoundPrimaryKey() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getEntityTypeName() {
        return "org.codelibs.fess.es.exentity.WebConfigToLabel";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.cbean.WebConfigToLabelCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.exbhv.WebConfigToLabelBhv";
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return WebConfigToLabel.class;
    }

    @Override
    public Entity newEntity() {
        return new WebConfigToLabel();
    }

    @Override
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        // TODO Auto-generated method stub

    }

    @Override
    public void acceptAllColumnMap(Entity entity, Map<String, ? extends Object> allColumnMap) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<String, Object> extractPrimaryKeyMap(Entity entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> extractAllColumnMap(Entity entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected UniqueInfo cpui() {
        // TODO Auto-generated method stub
        return null;
    }

}
