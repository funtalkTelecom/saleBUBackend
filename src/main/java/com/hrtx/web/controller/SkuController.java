package com.hrtx.web.controller;

import com.hrtx.dto.Result;
import com.hrtx.web.pojo.Sku;
import com.hrtx.web.service.SkuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sku")
public class SkuController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SkuService skuService;

	@RequestMapping("/sku-list-gid")
	@ResponseBody
	public Map skuListByGid(Sku sku){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", Result.OK);
		map.put("data", skuService.skuListByGid(sku.getgId()));
		return map;
	}
}
