package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.mapper.NumberMapper;
import com.hrtx.web.pojo.Number;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class ApiNumberController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private NumberMapper numberMapper;

	@GetMapping("/number")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public String numberList(Number number, HttpServletRequest request){
		PageInfo<Object> pm = null;
		try {
			number.setPageNum(request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum")));
			number.setLimit(request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit")));

			PageHelper.startPage(number.getPageNum(),number.getLimit());
			Page<Object> ob=this.numberMapper.queryPageListApi(number);
			pm = new PageInfo<Object>(ob);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
		}

		return JSONObject.fromObject(pm).toString();
	}
}
