package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.web.mapper.CorporationMapper;
import com.hrtx.web.pojo.Corporation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CorporationService {
	
	@Autowired
	private CorporationMapper corporationMapper;

	public Result pageCorporation(Corporation corporation) {
		PageHelper.startPage(corporation.getPageNum(),corporation.getLimit());
		Page<Object> ob=this.corporationMapper.queryPageList(corporation);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Corporation findCorporationById(Long id) {
		Corporation c = new Corporation();
		c.setId(id);
		Corporation corporation = corporationMapper.selectOne(c);
		return corporation;
	}
}
