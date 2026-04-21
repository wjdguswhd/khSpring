package kh.springboot.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import kh.springboot.member.model.vo.Member;

@Mapper //MyBatis매퍼 등록 = xml 매퍼 파일 sql과 연결
public interface MemberMapper {

	Member login(Member m);

	int insertMember(Member m);
	
}
