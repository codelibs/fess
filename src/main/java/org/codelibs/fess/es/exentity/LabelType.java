package org.codelibs.fess.es.exentity;

import java.util.ArrayList;
import java.util.List;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.es.bsentity.BsLabelType;
import org.codelibs.fess.es.exbhv.LabelToRoleBhv;
import org.codelibs.fess.es.exbhv.RoleTypeBhv;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.ListResultBean;

/**
 * @author FreeGen
 */
public class LabelType extends BsLabelType {

    private static final long serialVersionUID = 1L;
    private String[] roleTypeIds;

    private List<RoleType> roleTypeList;

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
                    final LabelToRoleBhv labelToRoleBhv = ComponentUtil.getComponent(LabelToRoleBhv.class);
                    final ListResultBean<LabelToRole> mappingList = labelToRoleBhv.selectList(cb -> {
                        cb.query().setLabelTypeId_Equal(getId());
                        cb.specify().columnRoleTypeId();
                    });
                    final List<String> roleIdList = new ArrayList<>();
                    for (final LabelToRole mapping : mappingList) {
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

    public List<String> getRoleValueList() {
        final List<RoleType> list = getRoleTypeList();
        final List<String> roleValueList = new ArrayList<>(list.size());
        for (final RoleType roleType : list) {
            roleValueList.add(roleType.getValue());
        }
        return roleValueList;
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
