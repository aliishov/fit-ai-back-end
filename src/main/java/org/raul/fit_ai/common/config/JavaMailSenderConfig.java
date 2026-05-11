package org.raul.fit_ai.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class JavaMailSenderConfig {

	@Bean
	public JavaMailSender mailSender() {
		return new JavaMailSenderImpl();
	}
}
