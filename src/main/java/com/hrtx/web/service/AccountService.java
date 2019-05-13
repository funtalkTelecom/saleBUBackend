package com.hrtx.web.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.Constants;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.AccountMapper;
import com.hrtx.web.mapper.ConsumerMapper;
import com.hrtx.web.pojo.Account;
import com.hrtx.web.pojo.Consumer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AccountService {

	@Autowired private AccountMapper accountMapper;
	@Autowired private ConsumerMapper consumerMapper;
	@Autowired private SmsService smsService;

	public Result pageAccount(Account account) {
		PageHelper.startPage(account.startToPageNum(),account.getLimit());
		List<Account> accountList=this.findAccountList(account);
		PageInfo<Account> pm = new PageInfo<>(accountList);
		return new Result(Result.OK, pm);
	}

	public Result accountEdit(Account account,String consumer_id,String sms_code) {//不修改，删除旧数据，创建新纪录
		if (account.getId() != null && account.getId() > 0) {
			Result result=this.accountDelete(account.getId());
			if(result.getCode()!=Result.OK)return result;
		}
		Result result=this.smsService.checkKey(SmsService.req_type_pc,sms_code);
		if(result.getCode()!=Result.OK)return result;
		if(account.getAccountType()==null||account.getAccountType()==-1)return new Result(Result.ERROR, "请选择账户类型");
		if(account.getAccountType()==Constants.ACCOUNT_TYPE_1.getIntKey()){//银行
			if(StringUtils.isEmpty(account.getCardAccount()))return new Result(Result.ERROR, "请输入银行账号");
			if(StringUtils.isEmpty(account.getBankAccount()))return new Result(Result.ERROR, "请输入户名");
			if(StringUtils.isEmpty(account.getCardBankName()))return new Result(Result.ERROR, "请选择银行");
		}else if(account.getAccountType()==Constants.ACCOUNT_TYPE_2.getIntKey()){//微信
			Consumer consumer=consumerMapper.selectByPrimaryKey(NumberUtils.toInt(consumer_id));
			if(consumer==null)return new Result(Result.ERROR, "请选择微信账户");
			account.setCardAccount(consumer.getId()+"");
			account.setBankAccount(consumer.getNickName());
			account.setCardBankName("微信");
			account.setCardBank(2);
			account.setSubbranchBank(null);
		}else{
			return new Result(Result.ERROR, "账户类型错误");
		}
		account.setAddDate(new Date());
		account.setAddUserId(SessionUtil.getUserId());
		account.setCorpId(SessionUtil.getUser().getCorpId());
		account.setIsDel(0);
		this.accountMapper.insert(account);
		return new Result(Result.OK, "提交成功");
	}

	public Result findAcountById(Integer id) {
		Account account = accountMapper.selectByPrimaryKey(id);
		if(!SessionUtil.getUser().getCorpId().equals(account.getCorpId()))return new Result(Result.ERROR, "抱歉，权限不足");
		return new Result(Result.OK, account);
	}

	public Result accountDelete(Integer account_id) {
		Account account=this.accountMapper.selectByPrimaryKey(account_id);
		if(!SessionUtil.getUser().getCorpId().equals(account.getCorpId()))return new Result(Result.ERROR, "抱歉，权限不足");
		account.setIsDel(1);
		this.accountMapper.updateByPrimaryKey(account);
		return new Result(Result.OK, "删除成功");
	}


	public List<Account> findAccountList(Account account) {
//		Example example=new Example(Account.class);
//		example.createCriteria().andEqualTo("corpId",SessionUtil.getUser().getCorpId());
		Account searchBean=new Account();
		searchBean.setCorpId(SessionUtil.getUser().getCorpId());
		searchBean.setIsDel(0);
		if(StringUtils.isNotEmpty(account.getCardAccount()))searchBean.setCardAccount(account.getCardAccount());
		if(StringUtils.isNotEmpty(account.getBankAccount()))searchBean.setBankAccount(account.getBankAccount());
		if(account.getAccountType()!=null)searchBean.setAccountType(account.getAccountType());
		List<Account> accountList=accountMapper.select(searchBean);//加密并隐藏
		for (Account bean:accountList) {
			encryptionAndHidden(bean);
		}
		return accountList;
	}

	public Account encryptionAndHidden(Account account) {//先隐藏再对原字段加密
//		account.setBankAccountHidden(Utils.sensitive(account.getCardAccount(),1,1));//姓名或企业名
		account.setCardAccountHidden(Utils.sensitive(account.getCardAccount(),4,4));//银行账号
		account.setCardAccount(Utils.encodeBySha1(account.getCardAccount()));
		return account;
	}
}
