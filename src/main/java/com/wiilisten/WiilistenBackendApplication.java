
package com.wiilisten;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.wiilisten.utils.ApplicationConstants;

@SpringBootApplication
@PropertySource(value = { ApplicationConstants.PROPERTY_SOURCE_PATH })
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
@EnableScheduling
public class WiilistenBackendApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(WiilistenBackendApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WiilistenBackendApplication.class);
	}
}
