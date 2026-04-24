package kh.springboot.board.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
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
	
	@GetMapping("write")
	public String writeAttm() {
		return "views/attm/write";
	}
	
	@PostMapping("insert")
	public String insertAttmBoard(@ModelAttribute Board b, @RequestParam("file") ArrayList<MultipartFile> files,
							HttpSession session	) {
//		System.out.println(b);
//		System.out.println(files);
		b.setBoardWriter(((Member)session.getAttribute("loginUser")).getId());
		
		
		ArrayList<Attachment> list = new ArrayList<Attachment>();
		for(int i = 0; i<files.size(); i++) {
			MultipartFile upload = files.get(i);
			//if(upload != null && !upload.isEmpty()) {
			if(!upload.getOriginalFilename().equals("")) {
				String[] returnArr = saveFile(upload);
				if(returnArr[1] != null) {
					Attachment a = new Attachment();
					a.setOriginalName(upload.getOriginalFilename());
					a.setRenameName(returnArr[1]);
					a.setAttmPath(returnArr[0]);
					
					list.add(a);
				}
			}
		}
		
		for(int i=0; i<list.size(); i++) {
			Attachment a = list.get(i);
			if(i==0) {
				a.setAttmLevel(0);
			}else {
				a.setAttmLevel(1);
			}
		}
		
		int result1 = 0;
		int result2 = 0;
		if(list.isEmpty()) {
			b.setBoardType(1);
			result1 = bService.insertBoard(b);
		}else {
			b.setBoardType(2);
			result1 = bService.insertBoard(b);
			result2 = bService.insertAttm(list);
		}
		
		if(result1 + result2 == list.size() + 1) {
			if(result2 == 0) {
				return  "redirect:/board/list";
			}else {
				return "redirect:/attm/list/";
			}
		}else {
			for(Attachment a : list) {
				deleteFile(a.getRenameName());
			}
			throw new BoardException("첨부파일 게시글 등록을 실패했습니다.");
		}

	}
	
	public String[] saveFile(MultipartFile upload) {

		String savePath = "c:\\uploadFiles";
		
		File folder = new File(savePath);
		if(!folder.exists()) {
			folder.mkdirs();
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		int ranNum = (int)(Math.random()*100000);
		String originFileName = upload.getOriginalFilename();
		String renameFileName = sdf.format(new Date()) + ranNum + originFileName.substring(originFileName.lastIndexOf("."));
		
		String renamePath = folder + "\\" + renameFileName;
		try {
			upload.transferTo(new File(renamePath));
		}catch (Exception e){
			System.out.println("파일 전송 에러 : " + e.getMessage());			
		}
		
		String[] returnArr = new String[2];
		returnArr[0] = savePath;
		returnArr[1] = renameFileName;
		
		return returnArr;
	}
	
	public void deleteFile(String renameName) {
		String savePath = "c:\\uploadFiles";
		File f = new File(savePath + "\\" + renameName);
		if(f.exists()) f.delete();
	}
	
	
}
