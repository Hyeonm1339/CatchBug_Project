package com.catchbug.biz.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.catchbug.biz.vo.BoardVO;
import com.catchbug.biz.vo.NotiVO;

@Controller
public class BoardController {

	@Autowired
	private BoardService boardService;

	@RequestMapping("/notice_Board_Write.do")
	public String NoticeBoardWrite() {
		return "board/notice_board_write";
	}

	@RequestMapping("/freeBoard.do")
	public String FreeBoard() {
		return "board/free_board";
	}

	@RequestMapping("/free_Board_Write.do")
	public String FreeBoardWrite() {
		return "board/free_board_write";
	}

	// 자유게시판 글쓰기 처리컨트롤러
	@PostMapping("/writeFreeBoard.do")
	public String FreeBoardWriteAction(BoardVO vo) {
		boardService.freeBoardWrite(vo);
		return "redirect:freeBoard.do";
	}

	@RequestMapping("/QnABoard.do")
	public String QnABoard() {
		return "board/qna_board";
	}

	@RequestMapping("/QnA_Board_Write.do")
	public String QnABoardWrite() {
		return "board/qna_board_write";
	}

	@RequestMapping("/FAQBoard.do")
	public String FAQBoard() {
		return "board/faq_board";
	}

	@RequestMapping("/ViewChat.do")
	public String ViewBoard() {
		return "board/chat";
	}
		
		//공지 리스트
	@RequestMapping("/notice_Board.do")
	public String notice_Board_list(Model model, NotiVO vo) {
		System.out.println("boardController");
		List<NotiVO> list = boardService.get_Noti_list();
		model.addAttribute("list", list);
		return "board/notice_board";
	}

	// 공지 쓰기
	@RequestMapping("insertNoti.do")
	public String insert_noti(NotiVO vo) {
		System.out.println("공지 컨트롤러");
		System.out.println(vo.toString());
		boardService.insert_noti(vo);
		return "redirect:notice_Board.do";
	}

	// 공지 상세보기 + 조회수 증가
	@RequestMapping("noti_detail.do")
	public String noti_detail(NotiVO vo, Model model) {
		System.out.println("Noti_detail 컨트롤러");

		// 조회수
		boardService.noti_cnt_Count(vo);
		// 상세보기
		NotiVO noti = boardService.detail_noti(vo);
		model.addAttribute("notiInfo", noti);
		return "board/notice_board_detail";
	}

	// 공지사항 업데이트
	@RequestMapping("updateNoti.do")
	public String noti_update(NotiVO vo) {
		System.out.println("Noti_update 컨트롤러");
		boardService.update_noti(vo);

		return "redirect:notice_Board.do";
	}

	// 공지사항 삭제
	@RequestMapping("delete_noti.do")
	public String noti_delete(NotiVO vo) {
		System.out.println("공지사항 삭제");
		boardService.noti_delete(vo);

		return "redirect:notice_Board.do";
	}

}
