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
package org.codelibs.fess.app.web.admin.user;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

/**
 * @author shinsuke
 * @author Keiichi Watanabe
 */
public class CreateForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @ValidateTypeFailure
    public Integer crudMode;

    @Required
    @Size(max = 100)
    public String name;

    @Size(max = 100)
    public String password;

    @Size(max = 100)
    public String confirmPassword;

    @Required
    @Size(max = 1000)
    public String surname;

    @Size(max = 1000)
    public String givenName;

    @Email
    @Size(max = 1000)
    public String mail;

    @Size(max = 1000)
    public String employeeNumber;

    @Size(max = 1000)
    public String telephoneNumber;

    @Size(max = 1000)
    public String homePhone;

    @Size(max = 1000)
    public String homePostalAddress;

    @Size(max = 1000)
    public String labeledURI;

    @Size(max = 1000)
    public String roomNumber;

    @Size(max = 1000)
    public String description;

    @Size(max = 1000)
    public String title;

    @Size(max = 1000)
    public String pager;

    @Size(max = 1000)
    public String street;

    @Size(max = 1000)
    public String postalCode;

    @Size(max = 1000)
    public String physicalDeliveryOfficeName;

    @Size(max = 1000)
    public String destinationIndicator;

    @Size(max = 1000)
    public String internationaliSDNNumber;

    @Size(max = 1000)
    public String state;

    @Size(max = 1000)
    public String employeeType;

    @Size(max = 1000)
    public String facsimileTelephoneNumber;

    @Size(max = 1000)
    public String postOfficeBox;

    @Size(max = 1000)
    public String initials;

    @Size(max = 1000)
    public String carLicense;

    @Size(max = 1000)
    public String mobile;

    @Size(max = 1000)
    public String postalAddress;

    @Size(max = 1000)
    public String city;

    @Size(max = 1000)
    public String teletexTerminalIdentifier;

    @Size(max = 1000)
    public String x121Address;

    @Size(max = 1000)
    public String businessCategory;

    @Size(max = 1000)
    public String registeredAddress;

    @Size(max = 1000)
    public String displayName;

    @Size(max = 1000)
    public String preferredLanguage;

    @Size(max = 1000)
    public String departmentNumber;

    @Size(max = 1000)
    public String uidNumber;

    @Size(max = 1000)
    public String gidNumber;

    @Size(max = 1000)
    public String homeDirectory;

    public String[] roles;

    public String[] groups;

    public void initialize() {
    }
}
