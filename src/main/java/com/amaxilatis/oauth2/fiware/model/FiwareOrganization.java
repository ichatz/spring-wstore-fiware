package com.amaxilatis.oauth2.fiware.model;

import java.util.List;

/**
 * @author Dimitrios Amaxilatis.
 */
public class FiwareOrganization {
    long id;
    String name;
    List<FiwareRole> roles;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FiwareRole> getRoles() {
        return roles;
    }

    public void setRoles(List<FiwareRole> roles) {
        this.roles = roles;
    }
}
