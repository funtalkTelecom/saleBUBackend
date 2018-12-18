package com.hrtx.web.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

public interface BaseMapper<T> {
	
	public List<T> queryList(@Param("param")T t);
	
	public Page<Object> queryPageList(@Param("param")T t);
	
	public Page<Object> queryPageList(@Param("param")Map<String, Object> map);

	@Select("select `nextval`('hk') ")
	@Options(useCache =false)
	public int getId();
}
