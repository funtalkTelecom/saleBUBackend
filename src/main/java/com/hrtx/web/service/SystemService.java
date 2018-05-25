package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.SystemParam;
import com.hrtx.web.mapper.SystemMapper;
import com.hrtx.web.pojo.System;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class SystemService {
	
	@Autowired
	private SystemMapper systemMapper;

	public Result pageSystem(System system) {
		PageHelper.startPage(system.getPageNum(),system.getLimit());
		Page<Object> ob=this.systemMapper.queryPageList(system);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public System findSystemById(Long id) {
		System system = systemMapper.findSystemInfo(id);
		return system;
	}

	public Result systemEdit(System system) {
		if (system.getId() != null && system.getId() > 0) {
			if (!SystemKeyIdIsExist(system)) {
				systemMapper.systemEdit(system);
			} else {
				return new Result(Result.ERROR, "key_id已存在");
			}
		} else {
			List<System> list = new ArrayList<System>();
			list.add(system);
			systemMapper.insertBatch(list);
		}

		return new Result(Result.OK, "提交成功");
	}

	private boolean SystemKeyIdIsExist(System system) {
		boolean b = true;
		int num = this.systemMapper.checkSystemKeyIdIsExist(system);
		if(num <= 0) b=false;

		return b;
	}

	public Result systemDelete(System system) {
		systemMapper.systemDelete(system);
		return new Result(Result.OK, "删除成功");
	}

	public Result systemAudit(System system) {
		systemMapper.systemAudit(system);
		SystemParam.load();
		return new Result(Result.OK, "审核成功");
	}

	public List<Map> findSystemParam() {
		return systemMapper.findSystemParam();
	}
}
