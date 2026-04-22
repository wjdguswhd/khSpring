package kh.springboot.board.model.vo;

import java.sql.Date;

public class Board {
	private int boardId;
	private int boardTitle;
	private String boardWriter;
	private String nickName;
	private String boardContent;
	private int boardCount;
	private Date createDate;
	private Date modifyDate;
	private String status;
	private int boardType;
}
