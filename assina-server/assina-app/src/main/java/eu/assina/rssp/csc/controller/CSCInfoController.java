package eu.assina.rssp.csc.controller;

import eu.assina.csc.model.AssinaCSCConstants;
import eu.assina.csc.payload.CSCInfoRequest;
import eu.assina.csc.payload.CSCInfoResponse;
import eu.assina.rssp.csc.services.CSCInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * INFO endpoint from:
 * From section 11.1 of the CSC API V_1.0.4.0 spec
 */
@RestController
@RequestMapping(value = "/info")
public class CSCInfoController
{

	private static final Logger log = LoggerFactory.getLogger(CSCInfoController.class);
	private CSCInfoService infoService;

	@Autowired
	public CSCInfoController(CSCInfoService infoService) {
		this.infoService = infoService;
	}

	/**
	 * Example request:
	 *   POST /csc/v1/info HTTP/1.1 Host: service.domain.org Content-Type: application/json
	 *   {}
	 *
	 * Example response:
	 * HTTP/1.1 200 OK
	 * Content-Type: application/json;charset=UTF-8
	 * {
	 *    "specs": "1.0.3.0",
	 *    "name": "ACME Trust Services",
	 *    "logo": "https://service.domain.org/images/logo.png",
	 *    "region": "IT",
	 *    "lang": "en-US",
	 *    "description": "An efficient remote signature service",
	 *    "authType": ["basic", "oauth2code"],
	 *    "oauth2": "https://www.domain.org/",
	 *    "methods": ["auth/login", "auth/revoke", "credentials/list",
	 *    "credentials/info",
	 *    "credentials/authorize",
	 *    "credentials/sendOTP",
	 *    "signatures/signHash"]
	 * }
	 *
	 * @param infoRequest
	 */
	@PostMapping("")
	@ResponseStatus(HttpStatus.OK)
	public CSCInfoResponse info(@RequestBody(required = false) CSCInfoRequest infoRequest)
	{
		if (infoRequest != null) {
			// we ignore lang but log it if its different from ours
			String lang = infoRequest.getLang();
			if (StringUtils.hasText(lang)) {
				if (!lang.equals(AssinaCSCConstants.CSC_LANG)) {
					log.warn("Unsupported lang in request: {}. Assina only supports ", lang, AssinaCSCConstants.CSC_LANG);
				}
			}
		}

		CSCInfoResponse info = new CSCInfoResponse(infoService.getInfo());
		return info;
	}
}
