/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.es.user.bsentity.dbmeta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.user.exentity.User;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

/**
 * @author ESFlute (using FreeGen)
 */
public class UserDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final UserDbm _instance = new UserDbm();

    private UserDbm() {
    }

    public static UserDbm getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                       Current DBDef
    //                                                                       =============
    @Override
    public String getProjectName() {
        return null;
    }

    @Override
    public String getProjectPrefix() {
        return null;
    }

    @Override
    public String getGenerationGapBasePrefix() {
        return null;
    }

    @Override
    public DBDef getCurrentDBDef() {
        return null;
    }

    // ===================================================================================
    //                                                                    Property Gateway
    //                                                                    ================
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    {
        setupEpg(_epgMap, et -> ((User) et).getBusinessCategory(), (et, vl) -> ((User) et).setBusinessCategory(DfTypeUtil.toString(vl)),
                "businessCategory");
        setupEpg(_epgMap, et -> ((User) et).getCarLicense(), (et, vl) -> ((User) et).setCarLicense(DfTypeUtil.toString(vl)), "carLicense");
        setupEpg(_epgMap, et -> ((User) et).getCity(), (et, vl) -> ((User) et).setCity(DfTypeUtil.toString(vl)), "city");
        setupEpg(_epgMap, et -> ((User) et).getDepartmentNumber(), (et, vl) -> ((User) et).setDepartmentNumber(DfTypeUtil.toString(vl)),
                "departmentNumber");
        setupEpg(_epgMap, et -> ((User) et).getDescription(), (et, vl) -> ((User) et).setDescription(DfTypeUtil.toString(vl)),
                "description");
        setupEpg(_epgMap, et -> ((User) et).getDestinationIndicator(),
                (et, vl) -> ((User) et).setDestinationIndicator(DfTypeUtil.toString(vl)), "destinationIndicator");
        setupEpg(_epgMap, et -> ((User) et).getDisplayName(), (et, vl) -> ((User) et).setDisplayName(DfTypeUtil.toString(vl)),
                "displayName");
        setupEpg(_epgMap, et -> ((User) et).getEmployeeNumber(), (et, vl) -> ((User) et).setEmployeeNumber(DfTypeUtil.toString(vl)),
                "employeeNumber");
        setupEpg(_epgMap, et -> ((User) et).getEmployeeType(), (et, vl) -> ((User) et).setEmployeeType(DfTypeUtil.toString(vl)),
                "employeeType");
        setupEpg(_epgMap, et -> ((User) et).getFacsimileTelephoneNumber(),
                (et, vl) -> ((User) et).setFacsimileTelephoneNumber(DfTypeUtil.toString(vl)), "facsimileTelephoneNumber");
        setupEpg(_epgMap, et -> ((User) et).getGidNumber(), (et, vl) -> ((User) et).setGidNumber(DfTypeUtil.toLong(vl)), "gidNumber");
        setupEpg(_epgMap, et -> ((User) et).getGivenName(), (et, vl) -> ((User) et).setGivenName(DfTypeUtil.toString(vl)), "givenName");
        setupEpg(_epgMap, et -> ((User) et).getGroups(), (et, vl) -> ((User) et).setGroups((String[]) vl), "groups");
        setupEpg(_epgMap, et -> ((User) et).getHomeDirectory(), (et, vl) -> ((User) et).setHomeDirectory(DfTypeUtil.toString(vl)),
                "homeDirectory");
        setupEpg(_epgMap, et -> ((User) et).getHomePhone(), (et, vl) -> ((User) et).setHomePhone(DfTypeUtil.toString(vl)), "homePhone");
        setupEpg(_epgMap, et -> ((User) et).getHomePostalAddress(), (et, vl) -> ((User) et).setHomePostalAddress(DfTypeUtil.toString(vl)),
                "homePostalAddress");
        setupEpg(_epgMap, et -> ((User) et).getInitials(), (et, vl) -> ((User) et).setInitials(DfTypeUtil.toString(vl)), "initials");
        setupEpg(_epgMap, et -> ((User) et).getInternationaliSDNNumber(),
                (et, vl) -> ((User) et).setInternationaliSDNNumber(DfTypeUtil.toString(vl)), "internationaliSDNNumber");
        setupEpg(_epgMap, et -> ((User) et).getLabeledURI(), (et, vl) -> ((User) et).setLabeledURI(DfTypeUtil.toString(vl)), "labeledURI");
        setupEpg(_epgMap, et -> ((User) et).getMail(), (et, vl) -> ((User) et).setMail(DfTypeUtil.toString(vl)), "mail");
        setupEpg(_epgMap, et -> ((User) et).getMobile(), (et, vl) -> ((User) et).setMobile(DfTypeUtil.toString(vl)), "mobile");
        setupEpg(_epgMap, et -> ((User) et).getName(), (et, vl) -> ((User) et).setName(DfTypeUtil.toString(vl)), "name");
        setupEpg(_epgMap, et -> ((User) et).getPager(), (et, vl) -> ((User) et).setPager(DfTypeUtil.toString(vl)), "pager");
        setupEpg(_epgMap, et -> ((User) et).getPassword(), (et, vl) -> ((User) et).setPassword(DfTypeUtil.toString(vl)), "password");
        setupEpg(_epgMap, et -> ((User) et).getPhysicalDeliveryOfficeName(),
                (et, vl) -> ((User) et).setPhysicalDeliveryOfficeName(DfTypeUtil.toString(vl)), "physicalDeliveryOfficeName");
        setupEpg(_epgMap, et -> ((User) et).getPostOfficeBox(), (et, vl) -> ((User) et).setPostOfficeBox(DfTypeUtil.toString(vl)),
                "postOfficeBox");
        setupEpg(_epgMap, et -> ((User) et).getPostalAddress(), (et, vl) -> ((User) et).setPostalAddress(DfTypeUtil.toString(vl)),
                "postalAddress");
        setupEpg(_epgMap, et -> ((User) et).getPostalCode(), (et, vl) -> ((User) et).setPostalCode(DfTypeUtil.toString(vl)), "postalCode");
        setupEpg(_epgMap, et -> ((User) et).getPreferredLanguage(), (et, vl) -> ((User) et).setPreferredLanguage(DfTypeUtil.toString(vl)),
                "preferredLanguage");
        setupEpg(_epgMap, et -> ((User) et).getRegisteredAddress(), (et, vl) -> ((User) et).setRegisteredAddress(DfTypeUtil.toString(vl)),
                "registeredAddress");
        setupEpg(_epgMap, et -> ((User) et).getRoles(), (et, vl) -> ((User) et).setRoles((String[]) vl), "roles");
        setupEpg(_epgMap, et -> ((User) et).getRoomNumber(), (et, vl) -> ((User) et).setRoomNumber(DfTypeUtil.toString(vl)), "roomNumber");
        setupEpg(_epgMap, et -> ((User) et).getState(), (et, vl) -> ((User) et).setState(DfTypeUtil.toString(vl)), "state");
        setupEpg(_epgMap, et -> ((User) et).getStreet(), (et, vl) -> ((User) et).setStreet(DfTypeUtil.toString(vl)), "street");
        setupEpg(_epgMap, et -> ((User) et).getSurname(), (et, vl) -> ((User) et).setSurname(DfTypeUtil.toString(vl)), "surname");
        setupEpg(_epgMap, et -> ((User) et).getTelephoneNumber(), (et, vl) -> ((User) et).setTelephoneNumber(DfTypeUtil.toString(vl)),
                "telephoneNumber");
        setupEpg(_epgMap, et -> ((User) et).getTeletexTerminalIdentifier(),
                (et, vl) -> ((User) et).setTeletexTerminalIdentifier(DfTypeUtil.toString(vl)), "teletexTerminalIdentifier");
        setupEpg(_epgMap, et -> ((User) et).getTitle(), (et, vl) -> ((User) et).setTitle(DfTypeUtil.toString(vl)), "title");
        setupEpg(_epgMap, et -> ((User) et).getUidNumber(), (et, vl) -> ((User) et).setUidNumber(DfTypeUtil.toLong(vl)), "uidNumber");
        setupEpg(_epgMap, et -> ((User) et).getX121Address(), (et, vl) -> ((User) et).setX121Address(DfTypeUtil.toString(vl)),
                "x121Address");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "user";
    protected final String _tableDispName = "user";
    protected final String _tablePropertyName = "User";

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

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnBusinessCategory = cci("businessCategory", "businessCategory", null, null, String.class,
            "businessCategory", null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCarLicense = cci("carLicense", "carLicense", null, null, String.class, "carLicense", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCity = cci("city", "city", null, null, String.class, "city", null, false, false, false, "keyword", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnDepartmentNumber = cci("departmentNumber", "departmentNumber", null, null, String.class,
            "departmentNumber", null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnDescription = cci("description", "description", null, null, String.class, "description", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnDestinationIndicator = cci("destinationIndicator", "destinationIndicator", null, null, String.class,
            "destinationIndicator", null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnDisplayName = cci("displayName", "displayName", null, null, String.class, "displayName", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnEmployeeNumber = cci("employeeNumber", "employeeNumber", null, null, String.class, "employeeNumber",
            null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnEmployeeType = cci("employeeType", "employeeType", null, null, String.class, "employeeType", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnFacsimileTelephoneNumber =
            cci("facsimileTelephoneNumber", "facsimileTelephoneNumber", null, null, String.class, "facsimileTelephoneNumber", null, false,
                    false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnGidNumber = cci("gidNumber", "gidNumber", null, null, Long.class, "gidNumber", null, false, false,
            false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnGivenName = cci("givenName", "givenName", null, null, String.class, "givenName", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnGroups = cci("groups", "groups", null, null, String[].class, "groups", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHomeDirectory = cci("homeDirectory", "homeDirectory", null, null, String.class, "homeDirectory", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHomePhone = cci("homePhone", "homePhone", null, null, String.class, "homePhone", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHomePostalAddress = cci("homePostalAddress", "homePostalAddress", null, null, String.class,
            "homePostalAddress", null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnInitials = cci("initials", "initials", null, null, String.class, "initials", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnInternationaliSDNNumber =
            cci("internationaliSDNNumber", "internationaliSDNNumber", null, null, String.class, "internationaliSDNNumber", null, false,
                    false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnLabeledURI = cci("labeledURI", "labeledURI", null, null, String.class, "labeledURI", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnMail = cci("mail", "mail", null, null, String.class, "mail", null, false, false, false, "keyword", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnMobile = cci("mobile", "mobile", null, null, String.class, "mobile", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnName = cci("name", "name", null, null, String.class, "name", null, false, false, false, "keyword", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPager = cci("pager", "pager", null, null, String.class, "pager", null, false, false, false, "keyword",
            0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPassword = cci("password", "password", null, null, String.class, "password", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPhysicalDeliveryOfficeName =
            cci("physicalDeliveryOfficeName", "physicalDeliveryOfficeName", null, null, String.class, "physicalDeliveryOfficeName", null,
                    false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPostOfficeBox = cci("postOfficeBox", "postOfficeBox", null, null, String.class, "postOfficeBox", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPostalAddress = cci("postalAddress", "postalAddress", null, null, String.class, "postalAddress", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPostalCode = cci("postalCode", "postalCode", null, null, String.class, "postalCode", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPreferredLanguage = cci("preferredLanguage", "preferredLanguage", null, null, String.class,
            "preferredLanguage", null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnRegisteredAddress = cci("registeredAddress", "registeredAddress", null, null, String.class,
            "registeredAddress", null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnRoles = cci("roles", "roles", null, null, String[].class, "roles", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnRoomNumber = cci("roomNumber", "roomNumber", null, null, String.class, "roomNumber", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnState = cci("state", "state", null, null, String.class, "state", null, false, false, false, "keyword",
            0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnStreet = cci("street", "street", null, null, String.class, "street", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnSurname = cci("surname", "surname", null, null, String.class, "surname", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnTelephoneNumber = cci("telephoneNumber", "telephoneNumber", null, null, String.class,
            "telephoneNumber", null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnTeletexTerminalIdentifier =
            cci("teletexTerminalIdentifier", "teletexTerminalIdentifier", null, null, String.class, "teletexTerminalIdentifier", null,
                    false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnTitle = cci("title", "title", null, null, String.class, "title", null, false, false, false, "keyword",
            0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUidNumber = cci("uidNumber", "uidNumber", null, null, Long.class, "uidNumber", null, false, false,
            false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnX121Address = cci("x121Address", "x121Address", null, null, String.class, "x121Address", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnBusinessCategory() {
        return _columnBusinessCategory;
    }

    public ColumnInfo columnCarLicense() {
        return _columnCarLicense;
    }

    public ColumnInfo columnCity() {
        return _columnCity;
    }

    public ColumnInfo columnDepartmentNumber() {
        return _columnDepartmentNumber;
    }

    public ColumnInfo columnDescription() {
        return _columnDescription;
    }

    public ColumnInfo columnDestinationIndicator() {
        return _columnDestinationIndicator;
    }

    public ColumnInfo columnDisplayName() {
        return _columnDisplayName;
    }

    public ColumnInfo columnEmployeeNumber() {
        return _columnEmployeeNumber;
    }

    public ColumnInfo columnEmployeeType() {
        return _columnEmployeeType;
    }

    public ColumnInfo columnFacsimileTelephoneNumber() {
        return _columnFacsimileTelephoneNumber;
    }

    public ColumnInfo columnGidNumber() {
        return _columnGidNumber;
    }

    public ColumnInfo columnGivenName() {
        return _columnGivenName;
    }

    public ColumnInfo columnGroups() {
        return _columnGroups;
    }

    public ColumnInfo columnHomeDirectory() {
        return _columnHomeDirectory;
    }

    public ColumnInfo columnHomePhone() {
        return _columnHomePhone;
    }

    public ColumnInfo columnHomePostalAddress() {
        return _columnHomePostalAddress;
    }

    public ColumnInfo columnInitials() {
        return _columnInitials;
    }

    public ColumnInfo columnInternationaliSDNNumber() {
        return _columnInternationaliSDNNumber;
    }

    public ColumnInfo columnLabeledURI() {
        return _columnLabeledURI;
    }

    public ColumnInfo columnMail() {
        return _columnMail;
    }

    public ColumnInfo columnMobile() {
        return _columnMobile;
    }

    public ColumnInfo columnName() {
        return _columnName;
    }

    public ColumnInfo columnPager() {
        return _columnPager;
    }

    public ColumnInfo columnPassword() {
        return _columnPassword;
    }

    public ColumnInfo columnPhysicalDeliveryOfficeName() {
        return _columnPhysicalDeliveryOfficeName;
    }

    public ColumnInfo columnPostOfficeBox() {
        return _columnPostOfficeBox;
    }

    public ColumnInfo columnPostalAddress() {
        return _columnPostalAddress;
    }

    public ColumnInfo columnPostalCode() {
        return _columnPostalCode;
    }

    public ColumnInfo columnPreferredLanguage() {
        return _columnPreferredLanguage;
    }

    public ColumnInfo columnRegisteredAddress() {
        return _columnRegisteredAddress;
    }

    public ColumnInfo columnRoles() {
        return _columnRoles;
    }

    public ColumnInfo columnRoomNumber() {
        return _columnRoomNumber;
    }

    public ColumnInfo columnState() {
        return _columnState;
    }

    public ColumnInfo columnStreet() {
        return _columnStreet;
    }

    public ColumnInfo columnSurname() {
        return _columnSurname;
    }

    public ColumnInfo columnTelephoneNumber() {
        return _columnTelephoneNumber;
    }

    public ColumnInfo columnTeletexTerminalIdentifier() {
        return _columnTeletexTerminalIdentifier;
    }

    public ColumnInfo columnTitle() {
        return _columnTitle;
    }

    public ColumnInfo columnUidNumber() {
        return _columnUidNumber;
    }

    public ColumnInfo columnX121Address() {
        return _columnX121Address;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnBusinessCategory());
        ls.add(columnCarLicense());
        ls.add(columnCity());
        ls.add(columnDepartmentNumber());
        ls.add(columnDescription());
        ls.add(columnDestinationIndicator());
        ls.add(columnDisplayName());
        ls.add(columnEmployeeNumber());
        ls.add(columnEmployeeType());
        ls.add(columnFacsimileTelephoneNumber());
        ls.add(columnGidNumber());
        ls.add(columnGivenName());
        ls.add(columnGroups());
        ls.add(columnHomeDirectory());
        ls.add(columnHomePhone());
        ls.add(columnHomePostalAddress());
        ls.add(columnInitials());
        ls.add(columnInternationaliSDNNumber());
        ls.add(columnLabeledURI());
        ls.add(columnMail());
        ls.add(columnMobile());
        ls.add(columnName());
        ls.add(columnPager());
        ls.add(columnPassword());
        ls.add(columnPhysicalDeliveryOfficeName());
        ls.add(columnPostOfficeBox());
        ls.add(columnPostalAddress());
        ls.add(columnPostalCode());
        ls.add(columnPreferredLanguage());
        ls.add(columnRegisteredAddress());
        ls.add(columnRoles());
        ls.add(columnRoomNumber());
        ls.add(columnState());
        ls.add(columnStreet());
        ls.add(columnSurname());
        ls.add(columnTelephoneNumber());
        ls.add(columnTeletexTerminalIdentifier());
        ls.add(columnTitle());
        ls.add(columnUidNumber());
        ls.add(columnX121Address());
        return ls;
    }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    @Override
    public boolean hasPrimaryKey() {
        return false;
    }

    @Override
    public boolean hasCompoundPrimaryKey() {
        return false;
    }

    @Override
    protected UniqueInfo cpui() {
        return null;
    }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    @Override
    public String getEntityTypeName() {
        return "org.codelibs.fess.es.user.exentity.User";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.user.cbean.UserCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.user.exbhv.UserBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return User.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new User();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
    }

    @Override
    public void acceptAllColumnMap(Entity entity, Map<String, ? extends Object> allColumnMap) {
    }

    @Override
    public Map<String, Object> extractPrimaryKeyMap(Entity entity) {
        return null;
    }

    @Override
    public Map<String, Object> extractAllColumnMap(Entity entity) {
        return null;
    }
}
