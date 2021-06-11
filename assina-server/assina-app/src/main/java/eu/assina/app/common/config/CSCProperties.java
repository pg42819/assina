package eu.assina.app.common.config;

import eu.assina.app.csc.model.AbstractInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "csc")
public class CSCProperties {
    private final Info info = new Info();

    // All CSC info properties are in the YAML file or environment
    public static class Info extends AbstractInfo {
    }

    public Info getInfo() {
        return info;
    }
}
