package com.hrtx.web.service;

import com.hrtx.web.mapper.IccidMapper;
import com.hrtx.web.mapper.MealMapper;
import com.hrtx.web.mapper.NumMapper;
import com.hrtx.web.pojo.Iccid;
import com.hrtx.web.pojo.Meal;
import com.hrtx.web.pojo.Num;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoundService {

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private NumMapper numMapper;
	@Autowired
	private IccidMapper iccidMapper;

	public Num findNumById(Long Id) {
		return numMapper.selectByPrimaryKey(Id);
	}

	public Iccid findIccidById(Long Id) {
		return iccidMapper.selectByPrimaryKey(Id);
	}

	public void bindNum(Num num) {
		numMapper.updateBoundNum(num);
	}

	public void iccidEditStatus(Iccid iccid) {
		iccidMapper.iccidEditStatus(iccid);
	}
}
