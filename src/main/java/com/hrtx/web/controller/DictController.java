package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Dict;
import com.hrtx.web.service.DictService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dict")
public class DictController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DictService dictService;

	@RequestMapping("/dict-query")
	@Powers({PowerConsts.DICTMOUDULE_COMMON_QUEYR})
	public ModelAndView dictQuery(Dict dict){
		return new ModelAndView("admin/dict/dict-query");
	}

	@RequestMapping("/dict-list")
	@Powers({PowerConsts.DICTMOUDULE_COMMON_QUEYR})
	public Result listDict(Dict dict){
		return dictService.pageDict(dict);
	}

	@RequestMapping("/dict-info")
	@ResponseBody
	@Powers({PowerConsts.DICTMOUDULE_COMMON_QUEYR})
	public Map dictInfo(Dict dict){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", Result.OK);
		map.put("data", dictService.findDictById(dict.getId()));
		return map;
	}

	@RequestMapping("/group-max-info")
	@ResponseBody
	public Map groupMaxInfo(Dict dict){
		Map<String, Object> map = new HashMap<String, Object>();
		dict = dictService.findGroupMaxInfo(dict.getKeyGroup());
		dict.setId(null);
		dict.setKeyId("");
		dict.setKeyValue("");
		dict.setSeq(dict.getSeq()+1);
		map.put("code", Result.OK);
		map.put("data", dict);
		return map;
	}

	@RequestMapping("/dict-edit")
	@Powers({PowerConsts.DICTMOUDULE_COMMON_EDIT})
	public void dictEdit(Dict dict){
		returnResult(dictService.dictEdit(dict));
	}

	@RequestMapping("/dict-delete")
	@Powers({PowerConsts.DICTMOUDULE_COMMON_DELETE})
	public void dictDelete(Dict dict){
		returnResult(dictService.dictDelete(dict));
	}
}
