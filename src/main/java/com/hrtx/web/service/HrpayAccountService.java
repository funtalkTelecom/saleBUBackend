package com.hrtx.web.service;

import com.hrtx.dto.Result;
import com.hrtx.web.mapper.HrpayAccountMapper;
import com.hrtx.web.pojo.HrpayAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class HrpayAccountService {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private HrpayAccountMapper hrpayAccountMapper;
	@Autowired private FundOrderService fundOrderService;

	public Result hrPayAccount(String type,int source_id){
		HrpayAccount hrpayAccount=new HrpayAccount();
		hrpayAccount.setSourceId(source_id);
		hrpayAccount.setType(type);
		HrpayAccount bean=this.hrpayAccountMapper.selectOne(hrpayAccount);
		if(bean==null){
			Result result=this.addPayAccount(type,source_id);
			if(result.getCode()==Result.OK)return hrPayAccount(type,source_id);
			else return new Result(Result.ERROR,"账号不存在");
		}
		if(!bean.getStatus()){
			Result result=this.addPayAccount(bean);
			if(result.getCode()==Result.OK)return new Result(Result.OK,bean.getId());
			else return new Result(Result.ERROR,"账号不存在");
		}
		return new Result(Result.OK,bean.getId());
	}

	public Result addPayAccount(String type,int source_id){
		HrpayAccount hrpayAccount=new HrpayAccount();
		hrpayAccount.setSourceId(source_id);
		hrpayAccount.setType(type);
		hrpayAccount.setStatus(false);
		this.hrpayAccountMapper.insert(hrpayAccount);
		return this.addPayAccount(hrpayAccount);
	}

	private Result addPayAccount(HrpayAccount hrpayAccount){
		Result result=fundOrderService.createHrPayAccount(String.valueOf(hrpayAccount.getId()),null,null);
		if(result.getCode()==Result.OK){
			hrpayAccount.setStatus(true);
			this.hrpayAccountMapper.updateByPrimaryKey(hrpayAccount);
		}
		return result;
	}

}
