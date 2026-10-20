package com.test.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Clean page URLs (/login, /home, /todo, ...) for the static pages
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/login");
		registry.addViewController("/login").setViewName("forward:/login.html");
		registry.addViewController("/register").setViewName("forward:/register.html");
		registry.addViewController("/home").setViewName("forward:/home.html");
		registry.addViewController("/about").setViewName("forward:/about.html");
		registry.addViewController("/profile").setViewName("forward:/profile.html");
		registry.addViewController("/login-history").setViewName("forward:/login-history.html");
		registry.addViewController("/todo").setViewName("forward:/todo.html");
		registry.addViewController("/contact").setViewName("forward:/contact.html");
	}
}
