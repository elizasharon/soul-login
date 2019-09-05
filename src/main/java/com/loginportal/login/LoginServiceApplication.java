package com.loginportal.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })
public class LoginServiceApplication { //because of actuator, you have to exclude this extra class

	public static void main(String[] args) {
		SpringApplication.run(LoginServiceApplication.class, args);
	}

}
