package eu.assina.app.csc.model;

import java.util.List;

/**
 * Represents basic CSC Info for use in properties and payloads.
 * Not a database entity
 * From section 11.1 of the CSC API V_1.0.4.0 spec
 */
public abstract class AbstractInfo implements CSCInfo {

    // required properties
    private String specs;
    private String name;
    private String logo;
    private String region;
    private String lang;
    private String description;
    private List<String> authType;
    private String oauth2;
    private List <String> methods;

    @Override
    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<String> getAuthType() {
        return authType;
    }

    public void setAuthType(List<String> authType) {
        this.authType = authType;
    }

    @Override
    public String getOauth2() {
        return oauth2;
    }

    public void setOauth2(String oauth2) {
        this.oauth2 = oauth2;
    }

    @Override
    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }
}
