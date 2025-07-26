package com.wiilisten.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@OpenAPIDefinition(
	security = @SecurityRequirement(name = "AppSecurity")
)
@SecurityScheme(name = "AppSecurity", 
	in = SecuritySchemeIn.HEADER, 
	type = SecuritySchemeType.HTTP, 
	bearerFormat = "JWT", 
	scheme = "bearer")
@Configuration
public class SwaggerConfig {

	@Value("${domain.url}")
	private String domainURL;
	
	@Value("${server.servlet.context-path}")
	private String contextPath;
	
	@Bean
	public OpenAPI configureOpenApi() {
		Info info = new Info();
		info.setTitle("WiiListen App");
		info.setDescription("WiiListen API documentation");
		info.setVersion("V1");
		
		Server server = new Server();
		server.setDescription("Development");
		server.setUrl(domainURL +  contextPath);
		
		return new OpenAPI().info(info).servers(List.of(server));
		
	}

}
