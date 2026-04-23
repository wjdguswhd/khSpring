package kh.springboot.board.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.common.Pagination;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/attm")
@RequiredArgsConstructor
public class AttachmentController {
	
	private final BoardService bService;
	
	@GetMapping("list")
	public String selectList(@RequestParam(value="page", defaultValue="1") int currentPage,
			Model model,HttpServletRequest request) {
		
		int listCount = bService.getListCount(2);
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 9);
		ArrayList<Board> bList = bService.selectBoardList(pi, 2);
		ArrayList<Attachment> aList = bService.selectAttmBoardList(); 
		
		if(bList != null) {
			model.addAttribute("loc",request.getRequestURI());
			model.addAttribute("bList",bList).addAttribute("pi",pi).addAttribute("aList",aList);
			return "views/attm/list";
		}else {
			throw new BoardException("첨부파일 게시글 조회를 실패했습니다.");
		}
		

	}
	
}
