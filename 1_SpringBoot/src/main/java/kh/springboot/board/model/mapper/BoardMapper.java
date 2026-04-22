package kh.springboot.board.model.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {

	int getListCount(int i);

}
