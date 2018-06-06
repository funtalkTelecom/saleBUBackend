package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SessionUtil;
import com.hrtx.web.mapper.PosterMapper;
import com.hrtx.web.pojo.Poster;
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
public class ApiPosterController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PosterMapper posterMapper;

	@GetMapping("/poster")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public String posterList(Poster poster, HttpServletRequest request){
		poster.setPageNum(request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum")));
		poster.setLimit(request.getParameter("limit")==null?1: Integer.parseInt(request.getParameter("limit")));

		PageHelper.startPage(poster.getPageNum(),poster.getLimit());
		Page<Object> ob=this.posterMapper.queryPageListApi(poster);
		PageInfo<Object> pm = new PageInfo<Object>(ob);

		return JSONObject.fromObject(pm).toString();
	}
}
