package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Poster;
import com.hrtx.web.service.PosterService;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public Result posterEdit(Poster poster, @RequestParam(name = "file",required = false) MultipartFile file, HttpServletRequest request){
        if(StringUtils.isBlank(poster.getTitle())) return new Result(Result.ERROR, "海报标题不能为空");
        if(StringUtils.isBlank(poster.getPosition())) return new Result(Result.ERROR, "海报位置不能为空");
        if(StringUtils.isBlank(poster.getUrl())) {
            return new Result(Result.ERROR, "海报url不能为空");
        }/*else{
            String pattern = "^(((http|https):\\/\\/)|(www\\.))+(\\.?)\\w+(\\.{1})\\w+$";

            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(poster.getUrl());
            if(!m.matches()){
                return new Result(Result.ERROR, "url格式错误");
            }
        }*/
        if(poster.getStartTime() == null) return new Result(Result.ERROR, "海报开始时间不能为空");
        if(poster.getEndTime() == null) return new Result(Result.ERROR, "海报结束时间不能为空");
		return posterService.posterEdit(poster, file, request);
	}

    public static void main(String[] args) {
        String pattern = "^(((http|https):\\/\\/)|(www\\.))+(\\.?)\\w+(\\.{1})\\w+$";
        pattern = "[1-9]\\d*";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher("11.");
        System.out.println(m.matches());
    }

	@RequestMapping("/poster-delete")
	@Powers({PowerConsts.POSTERMOUDULE_COMMON_DELETE})
	public void posterDelete(Poster poster){
		returnResult(posterService.posterDelete(poster));
	}
}
