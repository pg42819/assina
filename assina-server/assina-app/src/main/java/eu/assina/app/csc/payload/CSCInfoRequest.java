package eu.assina.app.csc.payload;

/**
 * Body for request of Info
 * From section 11.1 of the CSC API V_1.0.4.0 spec
 */
public class CSCInfoRequest {

    // optional property
    private String lang;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
