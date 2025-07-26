package com.wiilisten.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.wiilisten.enums.UserRoleEnum;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.JwtTokenFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private static final String[] SWAGGER_API_DOC_URIS = { "/v3/api-docs/**", "/v2/api-docs", "/swagger-resources/**",
			"/swagger-ui/**", "/webjars/**" };

	@Autowired
	private JwtTokenFilter jwtTokenFilter;

	@Bean
	public SecurityFilterChain appSecurity(HttpSecurity http) throws Exception {

		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(request -> request
//						.requestMatchers(ApplicationURIConstants.API + ApplicationURIConstants.V1
//								+ ApplicationURIConstants.ADMIN + ApplicationURIConstants.ALL)
//						.hasAnyRole(ApplicationConstants.ADMIN, ApplicationConstants.SUBADMIN)
				.requestMatchers(
						ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.AUTH
								+ ApplicationURIConstants.LOGIN,
						ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.FAQ,
						ApplicationURIConstants.API + ApplicationURIConstants.V1
								+ ApplicationURIConstants.OPEN_ENDED_QUESTIONS,
						ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.AUTH
								+ ApplicationURIConstants.CHECK_USERNAME,
						ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.AUTH
								+ ApplicationURIConstants.REGISTER,
						ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.AUTH
								+ ApplicationURIConstants.FORGOT_PASSWORD,
						ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.AUTH
								+ ApplicationURIConstants.SEND_OTP,
						ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.AUTH
								+ ApplicationURIConstants.VERIFY_OTP,
						ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.AUTH
								+ ApplicationURIConstants.NEW_PASSWORD,
						ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.PAGE_CONTENT
								+ ApplicationURIConstants.LIST,
						ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.CONTACT_US
								+ ApplicationURIConstants.ADD,
						ApplicationURIConstants.API + ApplicationURIConstants.V1
								+ ApplicationURIConstants.NEWS_LETTER_SUBSCRIBE,
						ApplicationURIConstants.PAGE_CONTENT + ApplicationURIConstants.ALL)
				.permitAll().requestMatchers(SWAGGER_API_DOC_URIS).permitAll().anyRequest().authenticated())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
		configuration.setAllowedMethods(
				Collections.unmodifiableList(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH")));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(
				Collections.unmodifiableList(Arrays.asList("Authorization", "Cache-Control", "Content-Type")));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
