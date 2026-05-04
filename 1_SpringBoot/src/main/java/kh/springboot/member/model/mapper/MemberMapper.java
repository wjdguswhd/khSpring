package kh.springboot.member.model.mapper;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

import kh.springboot.member.model.vo.Member;
import kh.springboot.member.model.vo.TodoList;

@Mapper //MyBatis매퍼 등록 = xml 매퍼 파일 sql과 연결
public interface MemberMapper {

	Member login(Member m);

	int insertMember(Member m);

	ArrayList<HashMap<String, Object>> selectMyList(String id);

	int updateMember(Member m);

	int updatePassword(Member m);

	int deleteMember(String id);

//	int checkId(String id);
//
//	int checkNickName(String nickName);

	int checkValue(HashMap<String, String> map);

	int updateProfile(Member m);

	ArrayList<TodoList> selectTodoList(String id);

	int insertTodo(TodoList todo);
	
	
}
