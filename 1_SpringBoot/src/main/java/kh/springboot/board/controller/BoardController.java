package kh.springboot.board.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")

public class BoardController {
	private final BoardService bService;
	
	@GetMapping("list")
	public ModelAndView selectList(@RequestParam(value="page", defaultValue="1") int currentPage, ModelAndView mv,
									HttpServletRequest request) {
		int listCount = bService.getListCount(1);
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);
		ArrayList<Board> list = bService.selectBoardList(pi,1);
		
		//request.getRequestURI() : /board/list
		//request.getRequestURL() : http://localhost:8080/board/list
		mv.addObject("loc",request.getRequestURI());
		mv.addObject("list",list).addObject("pi",pi).setViewName("list");
		return mv;
		
	}
	
	@GetMapping("write")
	public String writeBoard() {
		return "write";
	}
	
	@PostMapping("insert")
	public String insertBoard(@ModelAttribute Board b, /*Model model*/ HttpSession session) {
		String boardWriter = ((Member)session.getAttribute("loginUser")).getId();
		b.setBoardWriter(boardWriter);
		b.setBoardType(1);
		
		int result = bService.insertBoard(b);
		if(result>0) {
			return "redirect:/board/list";
		}else {
			throw new BoardException("게시글 작성을 실패했습니다.");
		}
	}
	
}
