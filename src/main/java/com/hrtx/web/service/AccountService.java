package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.SessionUtil;
import com.hrtx.web.mapper.AccountMapper;
import com.hrtx.web.pojo.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AccountService {

	@Autowired SessionUtil sessionUtil;
	@Autowired private AccountMapper accountMapper;

	public Result pageAccount(Account account) {
		PageHelper.startPage(account.startToPageNum(),account.getLimit());
		Page<Object> ob=this.accountMapper.queryPageList(account);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Result accountEdit(Account account) {
		if (account.getId() != null && account.getId() > 0) {
			account.setAddDate(new Date());
			account.setAddUserId(SessionUtil.getUserId());
			this.accountMapper.accountEdit(account);
		} else {
			List<Account> list = new ArrayList<Account>();
//			account.setId(account.getGeneralId());
			account.setAddDate(new Date());
			account.setAddUserId(SessionUtil.getUserId());
			list.add(account);
			this.accountMapper.insertAccount(list);
		}
		return new Result(Result.OK, "提交成功");
	}

	public Account findAcountById(Integer id) {
		Account account = accountMapper.findAccountInfo(id);
		return account;
	}

	public Result accountDelete(Account account) {
		accountMapper.accountDelete(account);
		return new Result(Result.OK, "删除成功");
	}


	public Result findAccountListByUserId(Integer userId) {
		return new Result(Result.OK,  accountMapper.findAccountListByUserId( userId));
	}
}
