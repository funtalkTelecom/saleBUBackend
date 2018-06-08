package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.ReadExcelNew;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.Utils;
import com.hrtx.web.pojo.Account;
import com.hrtx.web.pojo.Meal;
import com.hrtx.web.service.AccountService;
import com.hrtx.web.service.CityService;
import com.hrtx.web.service.MealService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/account")
public class AccountController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired AccountService accountService;
	@Autowired
	CityService cityService;


	@RequestMapping("/query-account")
	@Powers({PowerConsts.ACCOUNTMOUDULE_COMMON_QUEYR})
	public ModelAndView queryMeal(Account account){
		return new ModelAndView("admin/account/query-account");
	}

	@RequestMapping("/list-account")
	@Powers({PowerConsts.ACCOUNTMOUDULE_COMMON_QUEYR})
	public Result listAccount(Account account){
		return accountService.pageAccount(account);
	}

	@RequestMapping("/account-edit")
	@Powers({PowerConsts.ACCOUNTMOUDULE_COMMON_EDIT})
	public void posterEdit(Account account,  HttpServletRequest request){
		returnResult(accountService.accountEdit(account));
	}

	@RequestMapping("/account-info")
	@ResponseBody
	@Powers({PowerConsts.POSTERMOUDULE_COMMON_QUEYR})
	public Map posterInfo(Account account,HttpServletRequest request){
		Map<String, Object> map = new HashMap<String, Object>();
		account = accountService.findAcountById(account.getId());
		map.put("code", Result.OK);
		map.put("data", account);
		request.setAttribute("bean", account);
		return map;
	}

	@RequestMapping("/account-delete")
	@Powers({PowerConsts.ACCOUNTMOUDULE_COMMON_DELETE})
	public void accountDelete(Account account){
		returnResult(accountService.accountDelete(account));
	}
}
