package kh.springboot.common.handler;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import kh.springboot.member.model.exception.MemberException;

@ControllerAdvice //전체 컨트롤러에 유효할 수 있도록 보조
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MemberException.class) //특정 예외가 발생했을 때 처리할 메소드 지정
	public String handlerException(MemberException e, Model model) {
		model.addAttribute("message",e.getMessage());
		return "error/500";
	}
}
