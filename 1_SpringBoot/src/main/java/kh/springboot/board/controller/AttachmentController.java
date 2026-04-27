package kh.springboot.board.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
		ArrayList<Attachment> aList = bService.selectAttmBoardList(null); 
		
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
	@Transactional 
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
			//System.out.println(b);
			for(Attachment a : list) {
				a.setRefBoardId(b.getBoardId());
			}
			result2 = bService.insertAttm(list);
		}
		
		if(result1 + result2 == list.size() + 1) {
			if(result2 == 0) {
				return  "redirect:/board/list";
			}else {
				return "redirect:/attm/list";
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
	
	@GetMapping("/{id}/{page}")
	public String selectAttm(@PathVariable("id") int bId, @PathVariable("page") int page,HttpSession session,
			Model model) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		String id = null;
		if(loginUser != null) {
			id = loginUser.getId();
		}
		Board b = bService.selectBoard(bId,id);
		ArrayList<Attachment> list = bService.selectAttmBoardList((Integer)bId);
		
		if( b!=null) {
			model.addAttribute("b",b).addAttribute("page",page).addAttribute("list",list);
			return "views/attm/detail";
		}else {
			throw new BoardException("첨부파일 게시글 상세보기를 실패했습니다.");
		}

	}
	
	@PostMapping("updForm")
	public String updateForm(@RequestParam("boardId") int bId, @RequestParam("page") int page,Model model) {
		Board b = bService.selectBoard(bId, null);
		ArrayList<Attachment> list = bService.selectAttmBoardList(bId);
		model.addAttribute("b",b);
		model.addAttribute("list",list);
		model.addAttribute("page",page);
		return "views/attm/edit";
		
	}
	
	@PostMapping("update")
	public String updateBoard(@ModelAttribute Board b, @RequestParam("page") int page,
									@RequestParam("deleteAttm") String[] deleteAttm, 
									@RequestParam("file") ArrayList<MultipartFile> files) {
//		System.out.println(b);
//		System.out.println(Arrays.toString(deleteAttm));
//		System.out.println(files);
		
		/*
		  	1. 새파일 o
		  		(1) 기존 파일 모두 삭제 -> 새 파일 중에서 level 0, 1 지정
		  		(2) 기존 파일 일부 삭제 -> 삭제할 파일의 level 검사 후, level이 0인 파일이 삭제되면 다른 기존 파일의 레벨을 0으로 지정
		  							  새 파일의 레벨은 모두 1로 지정
		  		(3) 기존 파일 모두 유지 -> 새 파일의 레벨은 모두 1로 지정
		  		
		  		
		  	2. 새파일  x
		  		(1) 기존 파일 모두 삭제 -> 일반 게시판으로 이동 : board_type = 1
		  		(2) 기존 파일 일부 삭제 -> 삭제할 파일의 level 검사 후, level이 0인 파일이 삭제되면 다른 기존 파일의 레벨을 0으로 지정
		  		(3) 기존 파일 모두 유지 -> board만 수정
		 */
		
		b.setBoardType(2);
		
		//새로 넣는 파일은 list에 옮기기
		ArrayList<Attachment> list = new ArrayList<Attachment>();
		for(int i=0; i<files.size(); i++) {
			MultipartFile upload = files.get(i);
			if(!upload.getOriginalFilename().equals("")) {
				String[] returnArr = saveFile(upload);
				if(returnArr[1] != null) {
					Attachment a = new Attachment();
					a.setOriginalName(upload.getOriginalFilename());
					a.setRenameName(returnArr[1]);
					a.setAttmPath(returnArr[0]);
					a.setRefBoardId(b.getBoardId());
					
					list.add(a);
				}
			}
		}
		
		//삭제하는 파일이 있다면 삭제할 파일의 이름과 레벨 옮겨담기
		ArrayList<String> delRename = new ArrayList<>();
		ArrayList<Integer> delLevel = new ArrayList<>();
		for(String rename:deleteAttm) {
			if(!rename.equals("")) {
				String[] split = rename.split("/");
				delRename.add(split[0]);
				delLevel.add(Integer.parseInt(split[1]));
			}
		}
		
		int deleteAttmResult = 0;
		boolean existBeforeAttm = true;
		
		if(!delRename.isEmpty()) {
			deleteAttmResult = bService.deleteAttm(delRename);
			if(deleteAttmResult > 0) {
				for(String rename : delRename) {
					deleteFile(rename);
				}
			}
			
			if(delRename.size() == deleteAttm.length) {
				existBeforeAttm = false;
				if(list.isEmpty()) {
					b.setBoardType(1);
				}
			}else {
				for(int level : delLevel) {
					if(level == 0) {
						bService.updateAttmLevel(b.getBoardId());
						break;
					}
				}
			}
		
		}
		
		for(int i=0; i<list.size(); i++) {
			Attachment a = list.get(i);
			if(existBeforeAttm) {
				a.setAttmLevel(1);
			}else {
				if(i == 0) {
					a.setAttmLevel(0);
				}else {
					a.setAttmLevel(1);
				}
			}
		}
		
		int updateBoardResult = bService.updateBoard(b);
		int updateAttmResult = 0;
		if(!list.isEmpty()) {
			updateAttmResult = bService.insertAttm(list);
		}
		
		if(updateBoardResult + updateAttmResult == list.size() + 1) {
			if(deleteAttm.length != 0 && delRename.size() == deleteAttm.length && updateAttmResult ==0) {
				
				return "redirect:/board/list";
			}else {
				
				return String.format("redirect:/attm/%d/%d", b.getBoardId(), page);		
			}
		}else {
			throw new BoardException("첨부파일 게시글 수정을 실패했습니다.");
		}
		
	}
	
	
//	@PostMapping("delete")
//	public String deleteBoard(@RequestParam("boardId") int bId) {
//		int result1 = bService.deleteBoard(bId);
//		int result2 = bService.statusNAttm(bId);
//		
//		if(result1 > 0 && result2 >0) {
//			return "redirect:/attm/list";
//		}else {
//			throw new BoardException("첨부파일 게시글 삭제를 실패했습니다.");
//		}
//	}
	
	
	
}
