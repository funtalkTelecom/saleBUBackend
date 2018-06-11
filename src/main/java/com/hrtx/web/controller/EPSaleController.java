package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.DeliveryAddress;
import com.hrtx.web.pojo.EPSale;
import com.hrtx.web.service.CityService;
import com.hrtx.web.service.EPSaleService;
import com.hrtx.web.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/api")
public class EPSaleController extends BaseReturn{

	@Resource
	private CityService cityService;
	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EPSaleService epSaleService;
	@Autowired
	private FileService fileService;

	@RequestMapping("/epSale/epSale-query")
	@Powers({PowerConsts.EPSALEMOUDULE})
	public ModelAndView epSaleQuery(EPSale epSale){
		return new ModelAndView("admin/epSale/epSale-query");
	}

	@RequestMapping("/epSale/epSale-list")
	@Powers({PowerConsts.EPSALEMOUDULE_COMMON_QUEYR})
	public Result listEPSale(EPSale epSale){
		return epSaleService.pageEPSale(epSale);
	}

	@GetMapping("/epSale/epSale/{addUserId}")
	@Powers({PowerConsts.EPSALEMOUDULE_COMMON_QUEYR})
	//@Powers({PowerConsts.NOLOGINPOWER})
	@ResponseBody
	public Result listEPSale(EPSale epSale, @PathVariable("addUserId") String addUserId){
		return epSaleService.findEPSaleListByUserId(Long.valueOf(addUserId));
	}

	@RequestMapping("/epSale/epSale-info")
	@ResponseBody
	@Powers({PowerConsts.EPSALEMOUDULE_COMMON_QUEYR})
	//@Powers({PowerConsts.NOLOGINPOWER})
	public Map epSaleInfo(EPSale epSale, HttpServletRequest request){
		Map<String, Object> map = new HashMap<String, Object>();
		//Object list=cityService.queryByPidList(0);
/*		deliveryAddress.setId(new Long(5000));*/
		epSale=epSaleService.finEPSaleById(epSale.getId());
		map.put("code", Result.OK);
		map.put("data", epSale);
		map.put("epSalePics", fileService.findFilesByRefid(epSale.getId().toString()));
		request.setAttribute("bean", epSale);
		//request.setAttribute("province", list);
		return map;
	}

	@PostMapping("/epSale/epSale-edit")
	@ResponseBody
	@Powers({PowerConsts.EPSALEMOUDULE_COMMON_EDIT})
	//@Powers({PowerConsts.NOLOGINPOWER})
	public void epSaleEdit(EPSale epSale, @RequestParam(name = "file",required = false) MultipartFile[] files, HttpServletRequest request){
            returnResult(epSaleService.epSaleEdit(epSale,request, files));
	}

	@RequestMapping("/epSale/epSale-delete")
	@Powers({PowerConsts.EPSALEMOUDULE_COMMON_DELETE})
	//@Powers({PowerConsts.NOLOGINPOWER})
	public void epSaleDelete(EPSale epSale){
		returnResult(epSaleService.epSaleDelete(epSale));
	}
}
