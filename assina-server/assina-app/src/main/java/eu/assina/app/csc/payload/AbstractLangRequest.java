package eu.assina.app.csc.payload;

import eu.assina.app.common.config.AssinaConstants;
import eu.assina.app.csc.model.AssinaCSCConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Base class for CSC Requests that specify a language via the lang tag.
 * Assina does not currently support any languages other than en-US
 *
 * From the CSC Spec:
 *   Request a preferred language according to RFC 5646 [9]. If specified, the authorization
 *   server SHOULD render the authorization web page in this language, if supported. If omitted
 *   and an Accept-Language header is passed, the authorization server SHOULD render the
 *   authorization web page in the language declared by the header value, if supported.
 *   The authorization server SHALL render the web page in its own preferred language otherwise.
 */
public abstract class AbstractLangRequest implements CSCRequest {
    private static final Logger log = LoggerFactory.getLogger(AbstractLangRequest.class);

    // OPTIONAL
    // The lang as defined in the Input parameter table in section 11.1.
    private String lang;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    /**
     * Log a warning if an unsupported language is requested
     */
    public void validate() {
        if (StringUtils.hasText(lang)) {
            if (!lang.equals(AssinaCSCConstants.CSC_LANG)) {
                log.warn("Unsupported lang in request: {}. Assina only supports ",
                        lang, AssinaCSCConstants.CSC_LANG);
            }
        }
    }

}
