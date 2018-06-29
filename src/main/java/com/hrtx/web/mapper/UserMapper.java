package com.hrtx.web.mapper;

import java.util.List;
import java.util.Map;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface UserMapper extends Mapper<User>,BaseMapper<User>{

	public User findUserInfo();
	
	public Page<Object> selectByPageAndSelections();

	public List<Map> getPower(Long id);

	public List<String> findRoles(Long id);


    void test();

	void test1();
}
