package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.exentity.DataConfigToLabel;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

public class DataConfigToLabelDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final DataConfigToLabelDbm _instance = new DataConfigToLabelDbm();

    private DataConfigToLabelDbm() {
    }

    public static DataConfigToLabelDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((DataConfigToLabel) et).getDataConfigId(),
                (et, vl) -> ((DataConfigToLabel) et).setDataConfigId(DfTypeUtil.toString(vl)), "dataConfigId");
        setupEpg(_epgMap, et -> ((DataConfigToLabel) et).getId(), (et, vl) -> ((DataConfigToLabel) et).setId(DfTypeUtil.toString(vl)), "id");
        setupEpg(_epgMap, et -> ((DataConfigToLabel) et).getLabelTypeId(),
                (et, vl) -> ((DataConfigToLabel) et).setLabelTypeId(DfTypeUtil.toString(vl)), "labelTypeId");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnDataConfigId = cci("dataConfigId", "dataConfigId", null, null, String.class, "dataConfigId", null,
            false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnLabelTypeId = cci("labelTypeId", "labelTypeId", null, null, String.class, "labelTypeId", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);

    public ColumnInfo columnDataConfigId() {
        return _columnDataConfigId;
    }

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnLabelTypeId() {
        return _columnLabelTypeId;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnDataConfigId());
        ls.add(columnId());
        ls.add(columnLabelTypeId());
        return ls;
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "data_config_to_label";
    protected final String _tableDispName = "data_config_to_label";
    protected final String _tablePropertyName = "DataConfigToLabel";

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
        return "org.codelibs.fess.es.exentity.DataConfigToLabel";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.cbean.DataConfigToLabelCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.exbhv.DataConfigToLabelBhv";
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return DataConfigToLabel.class;
    }

    @Override
    public Entity newEntity() {
        return new DataConfigToLabel();
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
