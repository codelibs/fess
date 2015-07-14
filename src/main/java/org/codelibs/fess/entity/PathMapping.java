/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.entity;

public class PathMapping {

    private String id;

    /** URL_EXPR: {NotNull, VARCHAR(4000)} */
    private String urlExpr;

    /** BOOST_EXPR: {NotNull, VARCHAR(4000)} */
    private String boostExpr;

    /** SORT_ORDER: {NotNull, INTEGER(10)} */
    private Integer sortOrder;

    /** CREATED_BY: {NotNull, VARCHAR(255)} */
    private String createdBy;

    private Long createdTime;

    /** UPDATED_BY: {VARCHAR(255)} */
    private String updatedBy;

    private Long updatedTime;

    private Long version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlExpr() {
        return urlExpr;
    }

    public void setUrlExpr(String urlExpr) {
        this.urlExpr = urlExpr;
    }

    public String getBoostExpr() {
        return boostExpr;
    }

    public void setBoostExpr(String boostExpr) {
        this.boostExpr = boostExpr;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}