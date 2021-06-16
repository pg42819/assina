package eu.assina.app.common.config;

import eu.assina.app.crypto.CryptoConfig;
import eu.assina.app.csc.model.AbstractInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "csc")
public class CSCProperties {

    // properties mapping to the CSC /info request
    private final Info info = new Info();

    // properties for controlling the API
    private final Api api = new Api();

    // properties for controlling crypto algos, signing etc for CSC
    private final Crypto crypto = new Crypto();

    // All CSC info properties are in the YAML file or environment
    public static class Info extends AbstractInfo {
    }

    public static class Crypto extends CryptoConfig {
    }

    public static class Api {
        private int pageSize;
        private int maxPageSize;

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getMaxPageSize() {
            return maxPageSize;
        }

        public void setMaxPageSize(int maxPageSize) {
            this.maxPageSize = maxPageSize;
        }
    }

    public CryptoConfig getCrypto() {
        return crypto;
    }

    public Api getApi() {
        return api;
    }

    public Info getInfo() {
        return info;
    }
}
