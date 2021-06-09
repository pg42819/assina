package eu.assina.app.util;

import eu.assina.app.csc.model.AbstractInfo;

public class MockCSCInfo extends AbstractInfo {
    public MockCSCInfo() {
        super();
        setAuthType(null);
        setName("mock");
        setDescription("mock description");
        setLang("US_en");
        setLogo(null);
        setMethods(null);
        setOauth2(null);
        setSpecs(null);
    }

}
