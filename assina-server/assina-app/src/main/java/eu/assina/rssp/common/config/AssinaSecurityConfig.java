package eu.assina.rssp.common.config;

import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class AssinaSecurityConfig {

	protected void configure(HttpSecurity http) throws Exception {
		SimpleUrlAuthenticationFailureHandler handler = new SimpleUrlAuthenticationFailureHandler("/");

		// @formatter:off
		http.antMatcher("/**")
			.authorizeRequests(a -> a
				// whitelist / for main entry page, error for errors and
				// webjars to load the static javascript from claspath
				.antMatchers("/", "/error", "/webjars/**").permitAll()
				// all other endpoints must be authenticated with the oauth2 token:
				.anyRequest().authenticated()
			)
			.exceptionHandling(e -> e
					// make sure we send an ajaxy 401 instead of popping
					// a login dialog in the browser (autheticationEntryPoint)
				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
			)
				// TODO resinstate CSRF: disabling because csrf causes 403 when calling from POSTMAN
				.csrf().disable()
//			.csrf(c -> c
//				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//			)
			.logout(l -> l
				.logoutSuccessUrl("/").permitAll()
			)
			.oauth2Login(o -> o
				.failureHandler((request, response, exception) -> {
					request.getSession().setAttribute("error.message", exception.getMessage());
					handler.onAuthenticationFailure(request, response, exception);
				})
			);
		// @formatter:on
	}

}
