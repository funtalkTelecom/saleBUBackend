package com.hrtx.web.service;

import com.hrtx.web.mapper.NumMapper;
import com.hrtx.web.pojo.Num;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoundService {

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private NumMapper numMapper;

	public Num findNumById(Long Id) {
		return  numMapper.selectByPrimaryKey(Id.toString());
	}
}
