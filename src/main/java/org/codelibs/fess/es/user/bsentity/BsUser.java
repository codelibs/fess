/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.time.*;
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
    /** name */
    protected String name;

    /** password */
    protected String password;

    /** surname */
    protected String surname;

    /** givenName */
    protected String givenName;

    /** employeeNumber */
    protected String employeeNumber;

    /** mail */
    protected String mail;

    /** telephoneNumber */
    protected String telephoneNumber;

    /** homePhone */
    protected String homePhone;

    /** homePostalAddress */
    protected String homePostalAddress;

    /** labeledURI */
    protected String labeledURI;

    /** roomNumber */
    protected String roomNumber;

    /** description */
    protected String description;

    /** title */
    protected String title;

    /** pager */
    protected String pager;

    /** street */
    protected String street;

    /** postalCode */
    protected String postalCode;

    /** physicalDeliveryOfficeName */
    protected String physicalDeliveryOfficeName;

    /** destinationIndicator */
    protected String destinationIndicator;

    /** internationaliSDNNumber */
    protected String internationaliSDNNumber;

    /** state */
    protected String state;

    /** employeeType */
    protected String employeeType;

    /** facsimileTelephoneNumber */
    protected String facsimileTelephoneNumber;

    /** postOfficeBox */
    protected String postOfficeBox;

    /** initials */
    protected String initials;

    /** carLicense */
    protected String carLicense;

    /** mobile */
    protected String mobile;

    /** postalAddress */
    protected String postalAddress;

    /** city */
    protected String city;

    /** teletexTerminalIdentifier */
    protected String teletexTerminalIdentifier;

    /** x121Address */
    protected String x121Address;

    /** businessCategory */
    protected String businessCategory;

    /** registeredAddress */
    protected String registeredAddress;

    /** displayName */
    protected String displayName;

    /** preferredLanguage */
    protected String preferredLanguage;

    /** departmentNumber */
    protected String departmentNumber;

    /** uidNumber */
    protected Long uidNumber;

    /** gidNumber */
    protected Long gidNumber;

    /** homeDirectory */
    protected String homeDirectory;

    /** groups */
    protected String[] groups;

    /** roles */
    protected String[] roles;

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
        if (name != null) {
            sourceMap.put("name", name);
        }
        if (password != null) {
            sourceMap.put("password", password);
        }
        if (surname != null) {
            sourceMap.put("surname", surname);
        }
        if (givenName != null) {
            sourceMap.put("givenName", givenName);
        }
        if (employeeNumber != null) {
            sourceMap.put("employeeNumber", employeeNumber);
        }
        if (mail != null) {
            sourceMap.put("mail", mail);
        }
        if (telephoneNumber != null) {
            sourceMap.put("telephoneNumber", telephoneNumber);
        }
        if (homePhone != null) {
            sourceMap.put("homePhone", homePhone);
        }
        if (homePostalAddress != null) {
            sourceMap.put("homePostalAddress", homePostalAddress);
        }
        if (labeledURI != null) {
            sourceMap.put("labeledURI", labeledURI);
        }
        if (roomNumber != null) {
            sourceMap.put("roomNumber", roomNumber);
        }
        if (description != null) {
            sourceMap.put("description", description);
        }
        if (title != null) {
            sourceMap.put("title", title);
        }
        if (pager != null) {
            sourceMap.put("pager", pager);
        }
        if (street != null) {
            sourceMap.put("street", street);
        }
        if (postalCode != null) {
            sourceMap.put("postalCode", postalCode);
        }
        if (physicalDeliveryOfficeName != null) {
            sourceMap.put("physicalDeliveryOfficeName", physicalDeliveryOfficeName);
        }
        if (destinationIndicator != null) {
            sourceMap.put("destinationIndicator", destinationIndicator);
        }
        if (internationaliSDNNumber != null) {
            sourceMap.put("internationaliSDNNumber", internationaliSDNNumber);
        }
        if (state != null) {
            sourceMap.put("state", state);
        }
        if (employeeType != null) {
            sourceMap.put("employeeType", employeeType);
        }
        if (facsimileTelephoneNumber != null) {
            sourceMap.put("facsimileTelephoneNumber", facsimileTelephoneNumber);
        }
        if (postOfficeBox != null) {
            sourceMap.put("postOfficeBox", postOfficeBox);
        }
        if (initials != null) {
            sourceMap.put("initials", initials);
        }
        if (carLicense != null) {
            sourceMap.put("carLicense", carLicense);
        }
        if (mobile != null) {
            sourceMap.put("mobile", mobile);
        }
        if (postalAddress != null) {
            sourceMap.put("postalAddress", postalAddress);
        }
        if (city != null) {
            sourceMap.put("city", city);
        }
        if (teletexTerminalIdentifier != null) {
            sourceMap.put("teletexTerminalIdentifier", teletexTerminalIdentifier);
        }
        if (x121Address != null) {
            sourceMap.put("x121Address", x121Address);
        }
        if (businessCategory != null) {
            sourceMap.put("businessCategory", businessCategory);
        }
        if (registeredAddress != null) {
            sourceMap.put("registeredAddress", registeredAddress);
        }
        if (displayName != null) {
            sourceMap.put("displayName", displayName);
        }
        if (preferredLanguage != null) {
            sourceMap.put("preferredLanguage", preferredLanguage);
        }
        if (departmentNumber != null) {
            sourceMap.put("departmentNumber", departmentNumber);
        }
        if (uidNumber != null) {
            sourceMap.put("uidNumber", uidNumber);
        }
        if (gidNumber != null) {
            sourceMap.put("gidNumber", gidNumber);
        }
        if (homeDirectory != null) {
            sourceMap.put("homeDirectory", homeDirectory);
        }
        if (groups != null) {
            sourceMap.put("groups", groups);
        }
        if (roles != null) {
            sourceMap.put("roles", roles);
        }
        return sourceMap;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    protected String doBuildColumnString(String dm) {
        StringBuilder sb = new StringBuilder();
        sb.append(dm).append(name);
        sb.append(dm).append(password);
        sb.append(dm).append(surname);
        sb.append(dm).append(givenName);
        sb.append(dm).append(employeeNumber);
        sb.append(dm).append(mail);
        sb.append(dm).append(telephoneNumber);
        sb.append(dm).append(homePhone);
        sb.append(dm).append(homePostalAddress);
        sb.append(dm).append(labeledURI);
        sb.append(dm).append(roomNumber);
        sb.append(dm).append(description);
        sb.append(dm).append(title);
        sb.append(dm).append(pager);
        sb.append(dm).append(street);
        sb.append(dm).append(postalCode);
        sb.append(dm).append(physicalDeliveryOfficeName);
        sb.append(dm).append(destinationIndicator);
        sb.append(dm).append(internationaliSDNNumber);
        sb.append(dm).append(state);
        sb.append(dm).append(employeeType);
        sb.append(dm).append(facsimileTelephoneNumber);
        sb.append(dm).append(postOfficeBox);
        sb.append(dm).append(initials);
        sb.append(dm).append(carLicense);
        sb.append(dm).append(mobile);
        sb.append(dm).append(postalAddress);
        sb.append(dm).append(city);
        sb.append(dm).append(teletexTerminalIdentifier);
        sb.append(dm).append(x121Address);
        sb.append(dm).append(businessCategory);
        sb.append(dm).append(registeredAddress);
        sb.append(dm).append(displayName);
        sb.append(dm).append(preferredLanguage);
        sb.append(dm).append(departmentNumber);
        sb.append(dm).append(uidNumber);
        sb.append(dm).append(gidNumber);
        sb.append(dm).append(homeDirectory);
        sb.append(dm).append(groups);
        sb.append(dm).append(roles);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getName() {
        checkSpecifiedProperty("name");
        return convertEmptyToNull(name);
    }

    public void setName(String value) {
        registerModifiedProperty("name");
        this.name = value;
    }

    public String getPassword() {
        checkSpecifiedProperty("password");
        return convertEmptyToNull(password);
    }

    public void setPassword(String value) {
        registerModifiedProperty("password");
        this.password = value;
    }

    public String getSurname() {
        checkSpecifiedProperty("surname");
        return convertEmptyToNull(surname);
    }

    public void setSurname(String value) {
        registerModifiedProperty("surname");
        this.surname = value;
    }

    public String getGivenName() {
        checkSpecifiedProperty("givenName");
        return convertEmptyToNull(givenName);
    }

    public void setGivenName(String value) {
        registerModifiedProperty("givenName");
        this.givenName = value;
    }

    public String getEmployeeNumber() {
        checkSpecifiedProperty("employeeNumber");
        return convertEmptyToNull(employeeNumber);
    }

    public void setEmployeeNumber(String value) {
        registerModifiedProperty("employeeNumber");
        this.employeeNumber = value;
    }

    public String getMail() {
        checkSpecifiedProperty("mail");
        return convertEmptyToNull(mail);
    }

    public void setMail(String value) {
        registerModifiedProperty("mail");
        this.mail = value;
    }

    public String getTelephoneNumber() {
        checkSpecifiedProperty("telephoneNumber");
        return convertEmptyToNull(telephoneNumber);
    }

    public void setTelephoneNumber(String value) {
        registerModifiedProperty("telephoneNumber");
        this.telephoneNumber = value;
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

    public String getLabeledURI() {
        checkSpecifiedProperty("labeledURI");
        return convertEmptyToNull(labeledURI);
    }

    public void setLabeledURI(String value) {
        registerModifiedProperty("labeledURI");
        this.labeledURI = value;
    }

    public String getRoomNumber() {
        checkSpecifiedProperty("roomNumber");
        return convertEmptyToNull(roomNumber);
    }

    public void setRoomNumber(String value) {
        registerModifiedProperty("roomNumber");
        this.roomNumber = value;
    }

    public String getDescription() {
        checkSpecifiedProperty("description");
        return convertEmptyToNull(description);
    }

    public void setDescription(String value) {
        registerModifiedProperty("description");
        this.description = value;
    }

    public String getTitle() {
        checkSpecifiedProperty("title");
        return convertEmptyToNull(title);
    }

    public void setTitle(String value) {
        registerModifiedProperty("title");
        this.title = value;
    }

    public String getPager() {
        checkSpecifiedProperty("pager");
        return convertEmptyToNull(pager);
    }

    public void setPager(String value) {
        registerModifiedProperty("pager");
        this.pager = value;
    }

    public String getStreet() {
        checkSpecifiedProperty("street");
        return convertEmptyToNull(street);
    }

    public void setStreet(String value) {
        registerModifiedProperty("street");
        this.street = value;
    }

    public String getPostalCode() {
        checkSpecifiedProperty("postalCode");
        return convertEmptyToNull(postalCode);
    }

    public void setPostalCode(String value) {
        registerModifiedProperty("postalCode");
        this.postalCode = value;
    }

    public String getPhysicalDeliveryOfficeName() {
        checkSpecifiedProperty("physicalDeliveryOfficeName");
        return convertEmptyToNull(physicalDeliveryOfficeName);
    }

    public void setPhysicalDeliveryOfficeName(String value) {
        registerModifiedProperty("physicalDeliveryOfficeName");
        this.physicalDeliveryOfficeName = value;
    }

    public String getDestinationIndicator() {
        checkSpecifiedProperty("destinationIndicator");
        return convertEmptyToNull(destinationIndicator);
    }

    public void setDestinationIndicator(String value) {
        registerModifiedProperty("destinationIndicator");
        this.destinationIndicator = value;
    }

    public String getInternationaliSDNNumber() {
        checkSpecifiedProperty("internationaliSDNNumber");
        return convertEmptyToNull(internationaliSDNNumber);
    }

    public void setInternationaliSDNNumber(String value) {
        registerModifiedProperty("internationaliSDNNumber");
        this.internationaliSDNNumber = value;
    }

    public String getState() {
        checkSpecifiedProperty("state");
        return convertEmptyToNull(state);
    }

    public void setState(String value) {
        registerModifiedProperty("state");
        this.state = value;
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

    public String getPostOfficeBox() {
        checkSpecifiedProperty("postOfficeBox");
        return convertEmptyToNull(postOfficeBox);
    }

    public void setPostOfficeBox(String value) {
        registerModifiedProperty("postOfficeBox");
        this.postOfficeBox = value;
    }

    public String getInitials() {
        checkSpecifiedProperty("initials");
        return convertEmptyToNull(initials);
    }

    public void setInitials(String value) {
        registerModifiedProperty("initials");
        this.initials = value;
    }

    public String getCarLicense() {
        checkSpecifiedProperty("carLicense");
        return convertEmptyToNull(carLicense);
    }

    public void setCarLicense(String value) {
        registerModifiedProperty("carLicense");
        this.carLicense = value;
    }

    public String getMobile() {
        checkSpecifiedProperty("mobile");
        return convertEmptyToNull(mobile);
    }

    public void setMobile(String value) {
        registerModifiedProperty("mobile");
        this.mobile = value;
    }

    public String getPostalAddress() {
        checkSpecifiedProperty("postalAddress");
        return convertEmptyToNull(postalAddress);
    }

    public void setPostalAddress(String value) {
        registerModifiedProperty("postalAddress");
        this.postalAddress = value;
    }

    public String getCity() {
        checkSpecifiedProperty("city");
        return convertEmptyToNull(city);
    }

    public void setCity(String value) {
        registerModifiedProperty("city");
        this.city = value;
    }

    public String getTeletexTerminalIdentifier() {
        checkSpecifiedProperty("teletexTerminalIdentifier");
        return convertEmptyToNull(teletexTerminalIdentifier);
    }

    public void setTeletexTerminalIdentifier(String value) {
        registerModifiedProperty("teletexTerminalIdentifier");
        this.teletexTerminalIdentifier = value;
    }

    public String getX121Address() {
        checkSpecifiedProperty("x121Address");
        return convertEmptyToNull(x121Address);
    }

    public void setX121Address(String value) {
        registerModifiedProperty("x121Address");
        this.x121Address = value;
    }

    public String getBusinessCategory() {
        checkSpecifiedProperty("businessCategory");
        return convertEmptyToNull(businessCategory);
    }

    public void setBusinessCategory(String value) {
        registerModifiedProperty("businessCategory");
        this.businessCategory = value;
    }

    public String getRegisteredAddress() {
        checkSpecifiedProperty("registeredAddress");
        return convertEmptyToNull(registeredAddress);
    }

    public void setRegisteredAddress(String value) {
        registerModifiedProperty("registeredAddress");
        this.registeredAddress = value;
    }

    public String getDisplayName() {
        checkSpecifiedProperty("displayName");
        return convertEmptyToNull(displayName);
    }

    public void setDisplayName(String value) {
        registerModifiedProperty("displayName");
        this.displayName = value;
    }

    public String getPreferredLanguage() {
        checkSpecifiedProperty("preferredLanguage");
        return convertEmptyToNull(preferredLanguage);
    }

    public void setPreferredLanguage(String value) {
        registerModifiedProperty("preferredLanguage");
        this.preferredLanguage = value;
    }

    public String getDepartmentNumber() {
        checkSpecifiedProperty("departmentNumber");
        return convertEmptyToNull(departmentNumber);
    }

    public void setDepartmentNumber(String value) {
        registerModifiedProperty("departmentNumber");
        this.departmentNumber = value;
    }

    public Long getUidNumber() {
        checkSpecifiedProperty("uidNumber");
        return uidNumber;
    }

    public void setUidNumber(Long value) {
        registerModifiedProperty("uidNumber");
        this.uidNumber = value;
    }

    public Long getGidNumber() {
        checkSpecifiedProperty("gidNumber");
        return gidNumber;
    }

    public void setGidNumber(Long value) {
        registerModifiedProperty("gidNumber");
        this.gidNumber = value;
    }

    public String getHomeDirectory() {
        checkSpecifiedProperty("homeDirectory");
        return convertEmptyToNull(homeDirectory);
    }

    public void setHomeDirectory(String value) {
        registerModifiedProperty("homeDirectory");
        this.homeDirectory = value;
    }

    public String[] getGroups() {
        checkSpecifiedProperty("groups");
        return groups;
    }

    public void setGroups(String[] value) {
        registerModifiedProperty("groups");
        this.groups = value;
    }

    public String[] getRoles() {
        checkSpecifiedProperty("roles");
        return roles;
    }

    public void setRoles(String[] value) {
        registerModifiedProperty("roles");
        this.roles = value;
    }
}
