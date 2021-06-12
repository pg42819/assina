package eu.assina.app.common.util;

import java.util.Locale;

public interface Constants {
    String CSC_VERSION = "v1";
    String API_VERSION = "v1";
    String CSC_URL_ROOT = "/csc/" +  CSC_VERSION;
    String API_URL_ROOT = "/api/" + API_VERSION;

    // language supported by Assina - in CSC requests
    String AssinaLang = Locale.US.toLanguageTag();
}
