package kh.springboot.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import kh.springboot.member.model.vo.Member;

@Controller
public class MemberController {
	
	@GetMapping("/member/signIn")
	public String signIn() {
		return "views/member/login";
		
	}
	
	
	/***** 파라미터 받아오기 *****/
	//1. HttpServletRequest 사용 (Servlet 방식)
//	@PostMapping("member/signIn")
//	public void login(HttpServletRequest request) {
//		String id = request.getParameter("id");
//		String pwd = request.getParameter("pwd");
//		System.out.println(id);
//		System.out.println(pwd);
//	}
	
	
	//2. @RequestParam 사용
	// value			속성을 생략하면 기본적으로 value속성
	//					view에서 받아오는 파라미터 이름(view의 name속성)이 들어가는 곳
	
	// defaultValue		값이 null이거나 들어오지 않았을 때 기본적으로 들어갈 데이터 지정
	// required			기본 값이 true, 지정한 파라미터가 꼭 필요한(필수적인) 변수인지 설정
//	@PostMapping("member/signIn")
//	public void login(@RequestParam(value="id",defaultValue="hello") String id,
//					  @RequestParam(value="pwd",defaultValue="world") String pwd,
//					  @RequestParam(value="test", required=false) String test) {
//		System.out.println(id);
//		System.out.println(pwd);
//	}
	
	//3. @RequestParam 생략
//	@PostMapping("/member/signIn")
//	public void login(String id, String pwd) {
//		System.out.println(id);
//		System.out.println(pwd);
//		
//	}
	
	//4. @ModelAttribute 사용
	// 해당 클래슨 내부에 기본 생성자와 setter메소드가 무조건 있어야 동작
	//파라미터와 세터의 이름이 같으면 알아서 mapping
	@PostMapping("/member/signIn")
	public void login(@ModelAttribute Member m) {
		System.out.println(m);
		
	}
	
	
}
