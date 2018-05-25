package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Poster;
import com.hrtx.web.service.PosterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/poster")
public class PosterController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PosterService posterService;

	@RequestMapping("/poster-query")
	@Powers({PowerConsts.POSTERMOUDULE_COMMON_QUEYR})
	public ModelAndView posterQuery(Poster poster){
		return new ModelAndView("admin/poster/poster-query");
	}

	@RequestMapping("/poster-list")
	@Powers({PowerConsts.POSTERMOUDULE_COMMON_QUEYR})
	public Result listPoster(Poster poster){
		return posterService.pagePoster(poster);
	}

	@RequestMapping("/poster-info")
	@ResponseBody
	@Powers({PowerConsts.POSTERMOUDULE_COMMON_QUEYR})
	public Map posterInfo(Poster poster, HttpServletRequest request){
		Map<String, Object> map = new HashMap<String, Object>();
		poster = posterService.findPosterById(poster.getId());
		map.put("code", Result.OK);
		map.put("data", poster);
		request.setAttribute("bean", poster);
		return map;
	}

	@RequestMapping("/poster-edit")
	@Powers({PowerConsts.POSTERMOUDULE_COMMON_EDIT})
	public void posterEdit(Poster poster, @RequestParam(name = "file",required = false) MultipartFile file, HttpServletRequest request){
		returnResult(posterService.posterEdit(poster, file, request));
	}

	@RequestMapping("/poster-delete")
	@Powers({PowerConsts.POSTERMOUDULE_COMMON_DELETE})
	public void posterDelete(Poster poster){
		returnResult(posterService.posterDelete(poster));
	}
}
