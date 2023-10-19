package com.example.server.imgen.config;

import java.util.Arrays;
import org.springframework.context.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration config = new CorsConfiguration();

		config.setAllowCredentials(true);
		// your client url here: config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		config.setAllowedHeaders(Arrays.asList("*"));
		config.setAllowedMethods(Arrays.asList("*"));
		config.setMaxAge(300L);
		config.setExposedHeaders(Arrays.asList("Access-Control-Allow-Headers", "Authorization, x-xsrf-token, Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, " +
				"Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Content-Disposition"));
		source.registerCorsConfiguration("/**", config);
		
		return new CorsFilter(source);
	}
}
