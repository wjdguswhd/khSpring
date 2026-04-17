package kh.springboot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@GetMapping("/home") //get요청이 일 때 getmapping으로 연결, 핸들러매핑
	public String homeMain() {
		//ModelAndView : 데이터 + 화면을 담는 객체
		//Model : 데이터를 담는 객체 (request.setAttribute())
		//springboot에서 view만 전달하고 싶을 때는 String 반환 이용
		
		//springboot의 viewResolver 기본 설정
		//prefix : classpath:templates/
		//suffix : .html
		return "views/home";
	}
}
