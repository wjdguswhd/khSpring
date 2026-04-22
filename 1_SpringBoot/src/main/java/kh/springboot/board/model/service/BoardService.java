package kh.springboot.board.model.service;

import org.springframework.stereotype.Service;

import kh.springboot.board.model.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class BoardService {
	private final BoardMapper mapper;

	public int getListCount(int i) {
		return mapper.getListCount(i);
	}
}
