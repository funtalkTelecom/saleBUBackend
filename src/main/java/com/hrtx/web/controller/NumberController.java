package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.pojo.Number;
import com.hrtx.web.service.NumberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/number")
public class NumberController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private NumberService numberService;

	@RequestMapping("/number-query")
	@Powers({PowerConsts.NUMBERMOUDULE_COMMON_QUEYR})
	public ModelAndView numberQuery(Number number){
		return new ModelAndView("admin/number/number-query");
	}

	@RequestMapping("/number-list")
	@Powers({PowerConsts.NUMBERMOUDULE_COMMON_QUEYR})
	public Result listNumber(Number number, HttpServletRequest request){
		Map param = new HashMap();

		//标签
		String tags = request.getParameter("num_tags")==null?"": request.getParameter("num_tags");
		tags = "'"+ tags.replaceAll(",", "','") +"'";
		param.put("tags", tags);

		//号码(多号码)
		String numbers = request.getParameter("numbers")==null?"": request.getParameter("numbers");
		numbers = "'"+ numbers.replaceAll(",", "','") +"'";
		param.put("numbers", numbers);

		//号段
		String numberBlock = request.getParameter("numberBlock")==null?"": request.getParameter("numberBlock");
		param.put("numberBlock", numberBlock);

		//状态
		String qstatus = request.getParameter("qstatus")==null?"": request.getParameter("qstatus");
		param.put("qstatus", qstatus);

		//地市
		String city = request.getParameter("gSaleCity")==null?"": request.getParameter("gSaleCity");
		city = "'"+ city.replaceAll(",", "','") +"'";
		param.put("city", city);

		return numberService.pageNumber(number, param);
	}

	@RequestMapping("/numRule-addTags")
	@Powers({PowerConsts.NUMBERMOUDULE_COMMON_ADDTAG})
	public void addTags(Number number, HttpServletRequest request){
		returnResult(numberService.addTags(number, request));
	}
}
