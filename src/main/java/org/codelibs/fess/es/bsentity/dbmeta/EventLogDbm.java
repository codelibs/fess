package org.codelibs.fess.es.bsentity.dbmeta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbway.DBDef;

public class EventLogDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final EventLogDbm _instance = new EventLogDbm();

    private EventLogDbm() {
    }

    public static EventLogDbm getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnCreatedAt = cci("createdAt", "createdAt", null, null, LocalDateTime.class, "createdAt", null, false,
            false, false, "LocalDateTime", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedBy = cci("createdBy", "createdBy", null, null, String.class, "createdBy", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnEventType = cci("eventType", "eventType", null, null, String.class, "eventType", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnMessage = cci("message", "message", null, null, String.class, "message", null, false, false, false,
            "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPath = cci("path", "path", null, null, String.class, "path", null, false, false, false, "String", 0,
            0, null, false, null, null, null, null, null, false);

    public ColumnInfo columnCreatedAt() {
        return _columnCreatedAt;
    }

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnEventType() {
        return _columnEventType;
    }

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnMessage() {
        return _columnMessage;
    }

    public ColumnInfo columnPath() {
        return _columnPath;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnCreatedAt());
        ls.add(columnCreatedBy());
        ls.add(columnEventType());
        ls.add(columnId());
        ls.add(columnMessage());
        ls.add(columnPath());
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
