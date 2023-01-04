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
package org.codelibs.fess.es.user.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.user.allcommon.EsAbstractEntity;
import org.codelibs.fess.es.user.bsentity.dbmeta.UserDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsUser extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** businessCategory */
    protected String businessCategory;

    /** carLicense */
    protected String carLicense;

    /** city */
    protected String city;

    /** departmentNumber */
    protected String departmentNumber;

    /** description */
    protected String description;

    /** destinationIndicator */
    protected String destinationIndicator;

    /** displayName */
    protected String displayName;

    /** employeeNumber */
    protected String employeeNumber;

    /** employeeType */
    protected String employeeType;

    /** facsimileTelephoneNumber */
    protected String facsimileTelephoneNumber;

    /** gidNumber */
    protected Long gidNumber;

    /** givenName */
    protected String givenName;

    /** groups */
    protected String[] groups;

    /** homeDirectory */
    protected String homeDirectory;

    /** homePhone */
    protected String homePhone;

    /** homePostalAddress */
    protected String homePostalAddress;

    /** initials */
    protected String initials;

    /** internationaliSDNNumber */
    protected String internationaliSDNNumber;

    /** labeledURI */
    protected String labeledURI;

    /** mail */
    protected String mail;

    /** mobile */
    protected String mobile;

    /** name */
    protected String name;

    /** pager */
    protected String pager;

    /** password */
    protected String password;

    /** physicalDeliveryOfficeName */
    protected String physicalDeliveryOfficeName;

    /** postOfficeBox */
    protected String postOfficeBox;

    /** postalAddress */
    protected String postalAddress;

    /** postalCode */
    protected String postalCode;

    /** preferredLanguage */
    protected String preferredLanguage;

    /** registeredAddress */
    protected String registeredAddress;

    /** roles */
    protected String[] roles;

    /** roomNumber */
    protected String roomNumber;

    /** state */
    protected String state;

    /** street */
    protected String street;

    /** surname */
    protected String surname;

    /** telephoneNumber */
    protected String telephoneNumber;

    /** teletexTerminalIdentifier */
    protected String teletexTerminalIdentifier;

    /** title */
    protected String title;

    /** uidNumber */
    protected Long uidNumber;

    /** x121Address */
    protected String x121Address;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public UserDbm asDBMeta() {
        return UserDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "user";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (businessCategory != null) {
            addFieldToSource(sourceMap, "businessCategory", businessCategory);
        }
        if (carLicense != null) {
            addFieldToSource(sourceMap, "carLicense", carLicense);
        }
        if (city != null) {
            addFieldToSource(sourceMap, "city", city);
        }
        if (departmentNumber != null) {
            addFieldToSource(sourceMap, "departmentNumber", departmentNumber);
        }
        if (description != null) {
            addFieldToSource(sourceMap, "description", description);
        }
        if (destinationIndicator != null) {
            addFieldToSource(sourceMap, "destinationIndicator", destinationIndicator);
        }
        if (displayName != null) {
            addFieldToSource(sourceMap, "displayName", displayName);
        }
        if (employeeNumber != null) {
            addFieldToSource(sourceMap, "employeeNumber", employeeNumber);
        }
        if (employeeType != null) {
            addFieldToSource(sourceMap, "employeeType", employeeType);
        }
        if (facsimileTelephoneNumber != null) {
            addFieldToSource(sourceMap, "facsimileTelephoneNumber", facsimileTelephoneNumber);
        }
        if (gidNumber != null) {
            addFieldToSource(sourceMap, "gidNumber", gidNumber);
        }
        if (givenName != null) {
            addFieldToSource(sourceMap, "givenName", givenName);
        }
        if (groups != null) {
            addFieldToSource(sourceMap, "groups", groups);
        }
        if (homeDirectory != null) {
            addFieldToSource(sourceMap, "homeDirectory", homeDirectory);
        }
        if (homePhone != null) {
            addFieldToSource(sourceMap, "homePhone", homePhone);
        }
        if (homePostalAddress != null) {
            addFieldToSource(sourceMap, "homePostalAddress", homePostalAddress);
        }
        if (initials != null) {
            addFieldToSource(sourceMap, "initials", initials);
        }
        if (internationaliSDNNumber != null) {
            addFieldToSource(sourceMap, "internationaliSDNNumber", internationaliSDNNumber);
        }
        if (labeledURI != null) {
            addFieldToSource(sourceMap, "labeledURI", labeledURI);
        }
        if (mail != null) {
            addFieldToSource(sourceMap, "mail", mail);
        }
        if (mobile != null) {
            addFieldToSource(sourceMap, "mobile", mobile);
        }
        if (name != null) {
            addFieldToSource(sourceMap, "name", name);
        }
        if (pager != null) {
            addFieldToSource(sourceMap, "pager", pager);
        }
        if (password != null) {
            addFieldToSource(sourceMap, "password", password);
        }
        if (physicalDeliveryOfficeName != null) {
            addFieldToSource(sourceMap, "physicalDeliveryOfficeName", physicalDeliveryOfficeName);
        }
        if (postOfficeBox != null) {
            addFieldToSource(sourceMap, "postOfficeBox", postOfficeBox);
        }
        if (postalAddress != null) {
            addFieldToSource(sourceMap, "postalAddress", postalAddress);
        }
        if (postalCode != null) {
            addFieldToSource(sourceMap, "postalCode", postalCode);
        }
        if (preferredLanguage != null) {
            addFieldToSource(sourceMap, "preferredLanguage", preferredLanguage);
        }
        if (registeredAddress != null) {
            addFieldToSource(sourceMap, "registeredAddress", registeredAddress);
        }
        if (roles != null) {
            addFieldToSource(sourceMap, "roles", roles);
        }
        if (roomNumber != null) {
            addFieldToSource(sourceMap, "roomNumber", roomNumber);
        }
        if (state != null) {
            addFieldToSource(sourceMap, "state", state);
        }
        if (street != null) {
            addFieldToSource(sourceMap, "street", street);
        }
        if (surname != null) {
            addFieldToSource(sourceMap, "surname", surname);
        }
        if (telephoneNumber != null) {
            addFieldToSource(sourceMap, "telephoneNumber", telephoneNumber);
        }
        if (teletexTerminalIdentifier != null) {
            addFieldToSource(sourceMap, "teletexTerminalIdentifier", teletexTerminalIdentifier);
        }
        if (title != null) {
            addFieldToSource(sourceMap, "title", title);
        }
        if (uidNumber != null) {
            addFieldToSource(sourceMap, "uidNumber", uidNumber);
        }
        if (x121Address != null) {
            addFieldToSource(sourceMap, "x121Address", x121Address);
        }
        return sourceMap;
    }

    protected void addFieldToSource(Map<String, Object> sourceMap, String field, Object value) {
        sourceMap.put(field, value);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    protected String doBuildColumnString(String dm) {
        StringBuilder sb = new StringBuilder();
        sb.append(dm).append(businessCategory);
        sb.append(dm).append(carLicense);
        sb.append(dm).append(city);
        sb.append(dm).append(departmentNumber);
        sb.append(dm).append(description);
        sb.append(dm).append(destinationIndicator);
        sb.append(dm).append(displayName);
        sb.append(dm).append(employeeNumber);
        sb.append(dm).append(employeeType);
        sb.append(dm).append(facsimileTelephoneNumber);
        sb.append(dm).append(gidNumber);
        sb.append(dm).append(givenName);
        sb.append(dm).append(groups);
        sb.append(dm).append(homeDirectory);
        sb.append(dm).append(homePhone);
        sb.append(dm).append(homePostalAddress);
        sb.append(dm).append(initials);
        sb.append(dm).append(internationaliSDNNumber);
        sb.append(dm).append(labeledURI);
        sb.append(dm).append(mail);
        sb.append(dm).append(mobile);
        sb.append(dm).append(name);
        sb.append(dm).append(pager);
        sb.append(dm).append(password);
        sb.append(dm).append(physicalDeliveryOfficeName);
        sb.append(dm).append(postOfficeBox);
        sb.append(dm).append(postalAddress);
        sb.append(dm).append(postalCode);
        sb.append(dm).append(preferredLanguage);
        sb.append(dm).append(registeredAddress);
        sb.append(dm).append(roles);
        sb.append(dm).append(roomNumber);
        sb.append(dm).append(state);
        sb.append(dm).append(street);
        sb.append(dm).append(surname);
        sb.append(dm).append(telephoneNumber);
        sb.append(dm).append(teletexTerminalIdentifier);
        sb.append(dm).append(title);
        sb.append(dm).append(uidNumber);
        sb.append(dm).append(x121Address);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getBusinessCategory() {
        checkSpecifiedProperty("businessCategory");
        return convertEmptyToNull(businessCategory);
    }

    public void setBusinessCategory(String value) {
        registerModifiedProperty("businessCategory");
        this.businessCategory = value;
    }

    public String getCarLicense() {
        checkSpecifiedProperty("carLicense");
        return convertEmptyToNull(carLicense);
    }

    public void setCarLicense(String value) {
        registerModifiedProperty("carLicense");
        this.carLicense = value;
    }

    public String getCity() {
        checkSpecifiedProperty("city");
        return convertEmptyToNull(city);
    }

    public void setCity(String value) {
        registerModifiedProperty("city");
        this.city = value;
    }

    public String getDepartmentNumber() {
        checkSpecifiedProperty("departmentNumber");
        return convertEmptyToNull(departmentNumber);
    }

    public void setDepartmentNumber(String value) {
        registerModifiedProperty("departmentNumber");
        this.departmentNumber = value;
    }

    public String getDescription() {
        checkSpecifiedProperty("description");
        return convertEmptyToNull(description);
    }

    public void setDescription(String value) {
        registerModifiedProperty("description");
        this.description = value;
    }

    public String getDestinationIndicator() {
        checkSpecifiedProperty("destinationIndicator");
        return convertEmptyToNull(destinationIndicator);
    }

    public void setDestinationIndicator(String value) {
        registerModifiedProperty("destinationIndicator");
        this.destinationIndicator = value;
    }

    public String getDisplayName() {
        checkSpecifiedProperty("displayName");
        return convertEmptyToNull(displayName);
    }

    public void setDisplayName(String value) {
        registerModifiedProperty("displayName");
        this.displayName = value;
    }

    public String getEmployeeNumber() {
        checkSpecifiedProperty("employeeNumber");
        return convertEmptyToNull(employeeNumber);
    }

    public void setEmployeeNumber(String value) {
        registerModifiedProperty("employeeNumber");
        this.employeeNumber = value;
    }

    public String getEmployeeType() {
        checkSpecifiedProperty("employeeType");
        return convertEmptyToNull(employeeType);
    }

    public void setEmployeeType(String value) {
        registerModifiedProperty("employeeType");
        this.employeeType = value;
    }

    public String getFacsimileTelephoneNumber() {
        checkSpecifiedProperty("facsimileTelephoneNumber");
        return convertEmptyToNull(facsimileTelephoneNumber);
    }

    public void setFacsimileTelephoneNumber(String value) {
        registerModifiedProperty("facsimileTelephoneNumber");
        this.facsimileTelephoneNumber = value;
    }

    public Long getGidNumber() {
        checkSpecifiedProperty("gidNumber");
        return gidNumber;
    }

    public void setGidNumber(Long value) {
        registerModifiedProperty("gidNumber");
        this.gidNumber = value;
    }

    public String getGivenName() {
        checkSpecifiedProperty("givenName");
        return convertEmptyToNull(givenName);
    }

    public void setGivenName(String value) {
        registerModifiedProperty("givenName");
        this.givenName = value;
    }

    public String[] getGroups() {
        checkSpecifiedProperty("groups");
        return groups;
    }

    public void setGroups(String[] value) {
        registerModifiedProperty("groups");
        this.groups = value;
    }

    public String getHomeDirectory() {
        checkSpecifiedProperty("homeDirectory");
        return convertEmptyToNull(homeDirectory);
    }

    public void setHomeDirectory(String value) {
        registerModifiedProperty("homeDirectory");
        this.homeDirectory = value;
    }

    public String getHomePhone() {
        checkSpecifiedProperty("homePhone");
        return convertEmptyToNull(homePhone);
    }

    public void setHomePhone(String value) {
        registerModifiedProperty("homePhone");
        this.homePhone = value;
    }

    public String getHomePostalAddress() {
        checkSpecifiedProperty("homePostalAddress");
        return convertEmptyToNull(homePostalAddress);
    }

    public void setHomePostalAddress(String value) {
        registerModifiedProperty("homePostalAddress");
        this.homePostalAddress = value;
    }

    public String getInitials() {
        checkSpecifiedProperty("initials");
        return convertEmptyToNull(initials);
    }

    public void setInitials(String value) {
        registerModifiedProperty("initials");
        this.initials = value;
    }

    public String getInternationaliSDNNumber() {
        checkSpecifiedProperty("internationaliSDNNumber");
        return convertEmptyToNull(internationaliSDNNumber);
    }

    public void setInternationaliSDNNumber(String value) {
        registerModifiedProperty("internationaliSDNNumber");
        this.internationaliSDNNumber = value;
    }

    public String getLabeledURI() {
        checkSpecifiedProperty("labeledURI");
        return convertEmptyToNull(labeledURI);
    }

    public void setLabeledURI(String value) {
        registerModifiedProperty("labeledURI");
        this.labeledURI = value;
    }

    public String getMail() {
        checkSpecifiedProperty("mail");
        return convertEmptyToNull(mail);
    }

    public void setMail(String value) {
        registerModifiedProperty("mail");
        this.mail = value;
    }

    public String getMobile() {
        checkSpecifiedProperty("mobile");
        return convertEmptyToNull(mobile);
    }

    public void setMobile(String value) {
        registerModifiedProperty("mobile");
        this.mobile = value;
    }

    public String getName() {
        checkSpecifiedProperty("name");
        return convertEmptyToNull(name);
    }

    public void setName(String value) {
        registerModifiedProperty("name");
        this.name = value;
    }

    public String getPager() {
        checkSpecifiedProperty("pager");
        return convertEmptyToNull(pager);
    }

    public void setPager(String value) {
        registerModifiedProperty("pager");
        this.pager = value;
    }

    public String getPassword() {
        checkSpecifiedProperty("password");
        return convertEmptyToNull(password);
    }

    public void setPassword(String value) {
        registerModifiedProperty("password");
        this.password = value;
    }

    public String getPhysicalDeliveryOfficeName() {
        checkSpecifiedProperty("physicalDeliveryOfficeName");
        return convertEmptyToNull(physicalDeliveryOfficeName);
    }

    public void setPhysicalDeliveryOfficeName(String value) {
        registerModifiedProperty("physicalDeliveryOfficeName");
        this.physicalDeliveryOfficeName = value;
    }

    public String getPostOfficeBox() {
        checkSpecifiedProperty("postOfficeBox");
        return convertEmptyToNull(postOfficeBox);
    }

    public void setPostOfficeBox(String value) {
        registerModifiedProperty("postOfficeBox");
        this.postOfficeBox = value;
    }

    public String getPostalAddress() {
        checkSpecifiedProperty("postalAddress");
        return convertEmptyToNull(postalAddress);
    }

    public void setPostalAddress(String value) {
        registerModifiedProperty("postalAddress");
        this.postalAddress = value;
    }

    public String getPostalCode() {
        checkSpecifiedProperty("postalCode");
        return convertEmptyToNull(postalCode);
    }

    public void setPostalCode(String value) {
        registerModifiedProperty("postalCode");
        this.postalCode = value;
    }

    public String getPreferredLanguage() {
        checkSpecifiedProperty("preferredLanguage");
        return convertEmptyToNull(preferredLanguage);
    }

    public void setPreferredLanguage(String value) {
        registerModifiedProperty("preferredLanguage");
        this.preferredLanguage = value;
    }

    public String getRegisteredAddress() {
        checkSpecifiedProperty("registeredAddress");
        return convertEmptyToNull(registeredAddress);
    }

    public void setRegisteredAddress(String value) {
        registerModifiedProperty("registeredAddress");
        this.registeredAddress = value;
    }

    public String[] getRoles() {
        checkSpecifiedProperty("roles");
        return roles;
    }

    public void setRoles(String[] value) {
        registerModifiedProperty("roles");
        this.roles = value;
    }

    public String getRoomNumber() {
        checkSpecifiedProperty("roomNumber");
        return convertEmptyToNull(roomNumber);
    }

    public void setRoomNumber(String value) {
        registerModifiedProperty("roomNumber");
        this.roomNumber = value;
    }

    public String getState() {
        checkSpecifiedProperty("state");
        return convertEmptyToNull(state);
    }

    public void setState(String value) {
        registerModifiedProperty("state");
        this.state = value;
    }

    public String getStreet() {
        checkSpecifiedProperty("street");
        return convertEmptyToNull(street);
    }

    public void setStreet(String value) {
        registerModifiedProperty("street");
        this.street = value;
    }

    public String getSurname() {
        checkSpecifiedProperty("surname");
        return convertEmptyToNull(surname);
    }

    public void setSurname(String value) {
        registerModifiedProperty("surname");
        this.surname = value;
    }

    public String getTelephoneNumber() {
        checkSpecifiedProperty("telephoneNumber");
        return convertEmptyToNull(telephoneNumber);
    }

    public void setTelephoneNumber(String value) {
        registerModifiedProperty("telephoneNumber");
        this.telephoneNumber = value;
    }

    public String getTeletexTerminalIdentifier() {
        checkSpecifiedProperty("teletexTerminalIdentifier");
        return convertEmptyToNull(teletexTerminalIdentifier);
    }

    public void setTeletexTerminalIdentifier(String value) {
        registerModifiedProperty("teletexTerminalIdentifier");
        this.teletexTerminalIdentifier = value;
    }

    public String getTitle() {
        checkSpecifiedProperty("title");
        return convertEmptyToNull(title);
    }

    public void setTitle(String value) {
        registerModifiedProperty("title");
        this.title = value;
    }

    public Long getUidNumber() {
        checkSpecifiedProperty("uidNumber");
        return uidNumber;
    }

    public void setUidNumber(Long value) {
        registerModifiedProperty("uidNumber");
        this.uidNumber = value;
    }

    public String getX121Address() {
        checkSpecifiedProperty("x121Address");
        return convertEmptyToNull(x121Address);
    }

    public void setX121Address(String value) {
        registerModifiedProperty("x121Address");
        this.x121Address = value;
    }
}
