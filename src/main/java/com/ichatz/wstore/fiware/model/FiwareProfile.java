package com.ichatz.wstore.fiware.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class FiwareProfile {
    private String id;
    private String email;
    private String name;
    private String displayName;
    @JsonProperty("app_id")
    private String appId;
    List<FiwareRole> roles;
    List<FiwareOrganization> organizations;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public List<FiwareRole> getRoles() {
        return roles;
    }

    public void setRoles(List<FiwareRole> roles) {
        this.roles = roles;
    }

    public List<FiwareOrganization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<FiwareOrganization> organizations) {
        this.organizations = organizations;
    }
}
