package com.lms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan(basePackages = { "com.lms.*", "com.core.jwt" })
@EntityScan(basePackages = {"com.lms.repository.model", "com.core.jwt.models"})
@EnableJpaRepositories(basePackages = {"com.lms.repository", "com.core.jwt.repository"})
public class Application extends SpringBootServletInitializer implements CommandLineRunner {

	@Override
	public void run(String... arg0) throws Exception {
		if (arg0.length > 0 && arg0[0].equals("exitcode")) {
			throw new ExitException();
		}
	}

	public static void main(String[] args) throws Exception {
		new SpringApplication(Application.class).run(args);
	}

	class ExitException extends RuntimeException implements ExitCodeGenerator {
		private static final long serialVersionUID = 1L;

		@Override
		public int getExitCode() {
			return 10;
		}

	}
}
