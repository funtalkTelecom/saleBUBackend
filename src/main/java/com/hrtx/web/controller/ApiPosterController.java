package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.web.mapper.PosterMapper;
import com.hrtx.web.pojo.Poster;
import com.hrtx.web.service.ApiPosterService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiPosterController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ApiPosterService apiPosterService;

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
		return apiPosterService.posterList(poster, request);
	}
}
