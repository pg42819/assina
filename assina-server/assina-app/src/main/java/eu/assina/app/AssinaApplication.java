package eu.assina.app;

import eu.assina.app.spring.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.reactive.function.client.WebClient;

// TODO split this into application and configurer
// See https://spring.io/guides/tutorials/spring-boot-oauth2/ for the starting source of the spring oauth2 security
// Spring Boot attaches special meaning to a WebSecurityConfigurerAdapter on the class annotated with @SpringBootApplication:
//   It uses it to configure the security filter chain that carries the OAuth 2.0 authentication processor.
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class AssinaApplication { //extends WebSecurityConfigurerAdapter {
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
//	@Bean
//	public WebClient rest(ClientRegistrationRepository clients, OAuth2AuthorizedClientRepository authz) {
//		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
//				new ServletOAuth2AuthorizedClientExchangeFilterFunction(clients, authz);
//		return WebClient.builder()
//				.filter(oauth2).build();
//	}

//	@Bean
//	public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(WebClient rest) {
//		DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
//		return request -> {
//			OAuth2User user = delegate.loadUser(request);
//			if (!"github".equals(request.getClientRegistration().getRegistrationId())) {
//				return user;
//			}

//			 TODO expand this for some other sort of authorizatio if needed
//			OAuth2AuthorizedClient client = new OAuth2AuthorizedClient
//					(request.getClientRegistration(), user.getName(), request.getAccessToken());
//			String url = user.getAttribute("organizations_url");
//			List<Map<String, Object>> orgs = rest
//					.get().uri(url)
//					.attributes(oauth2AuthorizedClient(client))
//					.retrieve()
//					.bodyToMono(List.class)
//					.block();
//
//			if (orgs.stream().anyMatch(org -> "spring-projects".equals(org.get("login")))) {
//				return user;
//			}

//			throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token", "Not in Spring Team", ""));
//		};
//	}
//
	// TODO try this for testing but in a special test version
	// https://stackoverflow.com/questions/41824885/use-withmockuser-with-springboottest-inside-an-oauth2-resource-server-applic
//	@Override
//	public void configure(ResourceServerSecurityConfigurer security) throws Exception {
//		security.stateless(false);
//	}

//	@Override
	/*
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
*/
	public static void main(String[] args) {
		SpringApplication.run(AssinaApplication.class, args);
	}

}
