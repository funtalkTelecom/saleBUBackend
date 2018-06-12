package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
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

	/**
	 * 海报接口-根据海报位置查询有效海报
	 * @param poster
	 * @param request
	 * @return
	 */
	@GetMapping("/poster")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result posterList(Poster poster, HttpServletRequest request){
		PageInfo<Object> pm = null;
		try {
			poster.setPageNum(request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum")));
			poster.setLimit(request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit")));

			PageHelper.startPage(poster.getPageNum(),poster.getLimit());
			Page<Object> ob=this.posterMapper.queryPageListApi(poster);
			pm = new PageInfo<Object>(ob);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
			return new Result(Result.ERROR, pm);
		}

		return new Result(Result.OK, pm);
	}
}
