package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbway.DBDef;

public class FileConfigToLabelDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final FileConfigToLabelDbm _instance = new FileConfigToLabelDbm();

    private FileConfigToLabelDbm() {
    }

    public static FileConfigToLabelDbm getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnFileConfigId = cci("fileConfigId", "fileConfigId", null, null, String.class, "fileConfigId", null,
            false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnLabelTypeId = cci("labelTypeId", "labelTypeId", null, null, String.class, "labelTypeId", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);

    public ColumnInfo columnFileConfigId() {
        return _columnFileConfigId;
    }

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnLabelTypeId() {
        return _columnLabelTypeId;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnFileConfigId());
        ls.add(columnId());
        ls.add(columnLabelTypeId());
        return ls;
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
    public String getTableDbName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableDispName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTablePropertyName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TableSqlName getTableSqlName() {
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getConditionBeanTypeName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getBehaviorTypeName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entity newEntity() {
        // TODO Auto-generated method stub
        return null;
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
