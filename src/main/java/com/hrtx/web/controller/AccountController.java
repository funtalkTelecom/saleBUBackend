package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.Constants;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Account;
import com.hrtx.web.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/account")
public class AccountController extends BaseReturn{

	@Autowired AccountService accountService;
	@GetMapping("/query-account")
	@Powers({PowerConsts.ACCOUNTMOUDULE_COMMON_QUEYR})
	public ModelAndView queryAccount(){
		return new ModelAndView("admin/account/query-account");
	}

	@PostMapping("/list-account")
	@Powers({PowerConsts.ACCOUNTMOUDULE_COMMON_QUEYR})
	public Result listAccount(Account account){
		return accountService.pageAccount(account);
	}

	@GetMapping("/list-account")
	@Powers({PowerConsts.ACCOUNTMOUDULE_COMMON_QUEYR,PowerConsts.FINANCE_CORP})
	public Object listAccount(){
		Account account=new Account();
		account.setAccountType(Constants.ACCOUNT_TYPE_2.getIntKey());
		return accountService.findAccountList(account);
	}

	@PostMapping("/account-edit")
	@Powers({PowerConsts.ACCOUNTMOUDULE_COMMON_EDIT})
	public Result posterEdit(Account account,HttpServletRequest request){
		String sms_code=request.getParameter("sms_code");
		String consumer_id=request.getParameter("consumer_id");
		return accountService.accountEdit(account,consumer_id,sms_code);
	}

	@GetMapping("/account-info/{account_id}")
	@Powers({PowerConsts.POSTERMOUDULE_COMMON_QUEYR})
	public Result posterInfo(@PathVariable("account_id") Integer account_id){
		Result result= accountService.findAcountById(account_id);
		return result;
	}

	@GetMapping("/account-delete/{account_id}")
	@Powers({PowerConsts.ACCOUNTMOUDULE_COMMON_DELETE})
	public void accountDelete(@PathVariable("account_id") Integer account_id){
		returnResult(accountService.accountDelete(account_id));
	}

}
