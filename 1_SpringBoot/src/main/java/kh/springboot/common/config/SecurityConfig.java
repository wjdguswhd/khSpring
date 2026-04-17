package kh.springboot.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // bean생성과 동시에 객체생성 및 설정하는 클래스라는 것을 알려줌
public class SecurityConfig {
	@Bean //return값을 bean에 올리는 것
	public SecurityFilterChain filterChain(HttpSecurity http) {
		http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		return http.build();
	}
}
