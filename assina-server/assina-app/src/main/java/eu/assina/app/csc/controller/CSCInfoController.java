package eu.assina.app.csc.controller;

import eu.assina.app.csc.payload.CSCInfoRequest;
import eu.assina.app.csc.payload.CSCInfoResponse;
import eu.assina.app.csc.services.CSCInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * INFO endpoint from:
 * From section 11.1 of the CSC API V_1.0.4.0 spec
 */
@RestController
@RequestMapping(value = "/info")
public class CSCInfoController
{
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
	public CSCInfoResponse info(CSCInfoRequest infoRequest)
	{
		// TODO handle language
		CSCInfoResponse info = new CSCInfoResponse(infoService.getInfo());
		return info;
	}
}
