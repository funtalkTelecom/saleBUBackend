package com.hrtx.web.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;

public interface BaseMapper<T> {
	
	public List<T> queryList(@Param("param")T t);
	
	public Page<Object> queryPageList(@Param("param")T t);
	
	public Page<Object> queryPageList(@Param("param")Map<String, Object> map);
}
