package kh.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.security.core.userdetails.UserDetails;

//이 클래스가 main클래스임을 springboot에게 알려주는 어노테이션
@SpringBootApplication(exclude = {
		SecurityAutoConfiguration.class,
		UserDetailsServiceAutoConfiguration.class
}) 
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
