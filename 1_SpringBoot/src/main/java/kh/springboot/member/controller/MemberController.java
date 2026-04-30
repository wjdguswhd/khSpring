package kh.springboot.member.controller;


import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import kh.springboot.member.model.exception.MemberException;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor //DI 생성자 주입
@SessionAttributes("loginUser")
@RequestMapping("/member")
public class MemberController {
	
	// DI 필드 주입
	//@Autowired 
    //private MemberService mService;
	
	
	//DI 생성자 주입
	private final MemberService mService;
	private final BCryptPasswordEncoder bcrypt;
	private final JavaMailSender mailSender;
	
	@GetMapping("signIn")
	public String signIn() {
//		System.out.println(bcrypt.encode("1234"));
//		System.out.println(bcrypt.encode("pass01"));
//		System.out.println(bcrypt.encode("pass02"));
		return "login";
		
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
	// 해당 클래스 내부에 기본 생성자와 setter메소드가 무조건 있어야 동작
	//파라미터와 세터의 이름이 같으면 알아서 mapping
//	@PostMapping("/member/signIn")
//	public void login(@ModelAttribute Member m) {
//		System.out.println(mService);
//		mService.login(m);
//	}
	
	//5. @ModelAttribute 생략
//	@PostMapping("/member/signIn")
//	public String login(Member m, HttpSession session) {
//		Member loginUser = mService.login(m);
//		if(loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
//			session.setAttribute("loginUser", loginUser);
////			return "views/home";
//			return "redirect:/home";
//		}else {
//			throw new MemberException("로그인을 실패하였습니다.");
//		}
//	}
	
//	@GetMapping("/member/logout")
//	public String logout(HttpSession session) {
//		session.invalidate();
//		return "redirect:/home";
//	}
	
	@GetMapping("/enroll")
	public String enroll() {
		return "enroll";
	}
	
	@PostMapping("/enroll")
	public String enroll(@ModelAttribute Member m, @RequestParam("emailId") String emailId, @RequestParam("emailDomain") String emailDomain) {
		String email = null;
		if(!emailId.trim().equals("")) {
			m.setEmail(emailId + "@" + emailDomain);
		}
		
		//bcypt : 랜덤 salt값을 이용하여 암호화 진행
		m.setPwd(bcrypt.encode(m.getPwd()));
		
		int result = mService.insertMember(m);
		if(result > 0) {
			return "redirect:/home";
		}else {
			throw new MemberException("회원가입을 실패하였습니다.");
		}
	}
	
	/***** view에 전달하고자 하는 데이터가 있을때에 대한 방법******/
	//1. Model 사용
	// 데이터를 맵 형식(key, value)으로 담을 때 사용, requestScope
//	@GetMapping("/member/myInfo")
//	public String myInfo(HttpSession session, Model model) {
//	    Member loginUser = (Member)session.getAttribute("loginUser");
//	    if(loginUser != null) {
//	        String id = loginUser.getId();
//	        ArrayList<HashMap<String, Object>> list = mService.selectMyList(id);
//	        model.addAttribute("list", list);
//	    }
//	    return "views/member/myInfo";
//	}
	
	//2. ModelAndView 사용
	@GetMapping("/myInfo")
	public ModelAndView myInfo(HttpSession session, ModelAndView mv) {
	    Member loginUser = (Member)session.getAttribute("loginUser");
	    if(loginUser != null) {
	        String id = loginUser.getId();
	        ArrayList<HashMap<String, Object>> list = mService.selectMyList(id);
	        
	        mv.addObject("list",list);
	        mv.setViewName("myInfo");
	    }
	    return mv;
	}
	
	// 3. @SessionAttributes 사용
	//  Model에 atrribute가 추가될 때 자동으로 키 값을 찾아 세션에 등록하는 기능 제공
	@PostMapping("/signIn")
	public String login(Member m, Model model) {
		Member loginUser = mService.login(m);
		if(loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
			model.addAttribute("loginUser", loginUser);
//			return "views/home";
			return "redirect:/home";
		}else {
			throw new MemberException("로그인을 실패하였습니다.");
		}
	}
	
	@GetMapping("/logout")
	public String logout(SessionStatus status) {
		status.setComplete();
		return "redirect:/home";
	}
	
	@GetMapping("/edit")
	public String edit() {
		return "edit";	
		
	}
	
	@PostMapping("/edit")
	public String edit(@ModelAttribute Member m, Model model, @RequestParam("emailId") String emailId, @RequestParam("emailDomain") String emailDomain) {
		
		if(!emailId.trim().equals("")) {
			m.setEmail(emailId + "@" + emailDomain);
		}
		
		int result = mService.updateMember(m);
		if(result > 0) {
			model.addAttribute("loginUser",mService.login(m));
			return "redirect:/member/myInfo";
		}else {
			throw new MemberException("회원 정보 수정을 실패하였습니다.");
		}
		
	}

	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam("currentPwd") String pwd, @RequestParam("newPwd") String newPwd,
						/*HttpSession session*/ Model model) {
		
		/* Member m = (Member)session.getAttribute("loginUser"); */
		Member m = (Member)model.getAttribute("loginUser");
		
		if(bcrypt.matches(pwd, m.getPwd())) {
			m.setPwd(bcrypt.encode(newPwd));
			int result = mService.updatePassword(m);
			if(result > 0){
				model.addAttribute("loginUser",m);
				return "redirect:/home";
			}else {
			throw new MemberException("비밀번호 수정을 실패했습니다.");
			}
		}else {
			throw new MemberException("비밀번호 수정을 실패했습니다.");
		}

	}	
	
	@GetMapping("/delete")
	public String delete(Model model) {
		int result = mService.deleteMember(((Member)model.getAttribute("loginUser")).getId());
		if(result > 0) {
			return "redirect:/member/logout";
		}else {
			throw new MemberException("회원탈퇴를 실패했습니다.");
		}
	}
	
//	@GetMapping("checkId")
//	public void checkId(@RequestParam("id") String id, PrintWriter out) {
//		int count = mService.checkId(id);
//		out.print(count);
//	}
//	
//	@GetMapping("checkNickName")
//	@ResponseBody
//	public String checkNickName(@RequestParam("nickName") String nickName) {
//		int count = mService.checkNickName(nickName);
//		return count == 0 ? "usable" : "unusable";
//		
//	}
	
	@GetMapping("checkValue")
	@ResponseBody
	public int checkValue(@RequestParam("column") String col,@RequestParam("value") String val){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("col", col);
		map.put("val", val);
		int count = mService.checkValue(map);
		return count;
	}
	
	@GetMapping("echeck")
	@ResponseBody
	public String checkEmail(@RequestParam("email") String email) {
		//SimpleMailMessage : 문자열 형식의 text만 전송 가능
		//MimeMessage : 문자열 형식 + html형식 전송 가능
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		
		String subject = "[StringBoot] 이메일 확인";
		String body = "<h1 align='center'>SpringBoot 이메일 확인</h1><br/>"	;
		body += "<div style='border: 3px solid skyblue; text-align: center; font-size:15px;'>";
		body += "본 메일은 이메일을 확인하기 위해 발송되었습니다.<br/>";
		body += "아래 숫자를 인증번호 확인란에 작성하여 확인해주시기 바랍니다.<br/><br/>";
		
		
		
		String random = "";
		for(int i=0; i<5; i++) {
			random += (int)(Math.random()*10);
		}
		
		body += "<span style='font-size: 30px; text-decoration: underline;'><b>"+ random + "</b></span><br/></div>";
		
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
		try {
			mimeMessageHelper.setTo(email);
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(body,true);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		mailSender.send(mimeMessage); // Email 전송
		return random;
	}
}
