package eu.assina.app.api.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

// See https://spring.io/guides/tutorials/spring-boot-oauth2/ for the starting source of the spring oauth2 security

/**
 * Manages the current user information for a user logged in via OAuth 2
 */
//@Controller
public class OAuthUserController {

//	@GetMapping("/user")
//	@ResponseBody
	public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal,
																	HttpServletRequest request) {
		String name = principal.getAttribute("name");
		if (name == null) {
			// name can be null with github
			name = principal.getAttribute("login");
		}
		return Collections.singletonMap("name", name);
	}

//	@GetMapping("/error")
//	@ResponseBody
	public String error(HttpServletRequest request) {
		String message = (String) request.getSession().getAttribute("error.message");
		request.getSession().removeAttribute("error.message");
		return message;
	}
}
