package eu.assina.app.common.config;

import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

// TODO split this into application and configurer
// See https://spring.io/guides/tutorials/spring-boot-oauth2/ for the starting source of the spring oauth2 security
// Spring Boot attaches special meaning to a WebSecurityConfigurerAdapter on the class annotated with @SpringBootApplication:
//   It uses it to configure the security filter chain that carries the OAuth 2.0 authentication processor.
//@EnableWebSecurity(debug = true)
public class SecurityConfig {// extends WebSecurityConfigurerAdapter {
	// https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/
	// The default security configuration is implemented in SecurityAutoConfiguration and
	// UserDetailsServiceAutoConfiguration.
	// SecurityAutoConfiguration imports SpringBootWebSecurityConfiguration for web security and
	// UserDetailsServiceAutoConfiguration configures authentication, which is also relevant in non-web applications.
	// To switch off the default web application security configuration completely or to combine multiple Spring Security
	// components such as OAuth2 Client and Resource Server, add a bean of type SecurityFilterChain
	// (doing so does not disable the UserDetailsService configuration or Actuator’s security).
	//
	// To also switch off the UserDetailsService configuration, you can add a bean of type
	// UserDetailsService, AuthenticationProvider, or AuthenticationManager.

	// Access rules can be overridden by adding a custom SecurityFilterChain or WebSecurityConfigurerAdapter bean.
	// Spring Boot provides convenience methods that can be used to override access rules for actuator endpoints and
	// static resources. EndpointRequest can be used to create a RequestMatcher that is based on the
	// management.endpoints.web.base-path property.
	// PathRequest can be used to create a RequestMatcher for resources in commonly used locations.



	// TODO try this for testing but in a special test version
	// https://stackoverflow.com/questions/41824885/use-withmockuser-with-springboottest-inside-an-oauth2-resource-server-applic
//	@Override
//	public void configure(ResourceServerSecurityConfigurer security) throws Exception {
//		security.stateless(false);
//	}

//	@Override
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
