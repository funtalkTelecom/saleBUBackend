package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.Constants;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.AccountMapper;
import com.hrtx.web.mapper.CorporationMapper;
import com.hrtx.web.mapper.OrderSettleMapper;
import com.hrtx.web.pojo.Account;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.Corporation;
import com.hrtx.web.pojo.HrpayAccount;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class CorporationService {
	
	@Autowired private CorporationMapper corporationMapper;
	@Autowired private AccountMapper accountMapper;
	@Autowired private HrpayAccountService hrpayAccountService;
	@Autowired private FundOrderService fundOrderService;
	@Autowired private OrderSettleMapper orderSettleMapper;

	public Result pageCorporation(Corporation corporation) {
		PageHelper.startPage(corporation.startToPageNum(),corporation.getLimit());
		Page<Object> ob=this.corporationMapper.queryPageList(corporation);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Corporation findCorporationById(Integer id) {
		Corporation c = new Corporation();
		c.setId(id);
		Corporation corporation = corporationMapper.selectOne(c);
		return corporation;
	}

	//////////////////////////////////////////
	public Object queryCorpBusiData() {
		int corpId = SessionUtil.getUser().getCorpId();
		Result result1 = this.hrpayAccountService.hrPayAccount(HrpayAccount.acctoun_type_corp, corpId);
		Map<String,String> resMap=new HashMap<>();
		List<Map> list=orderSettleMapper.sumCorpOrderAmt(corpId);
		if(!list.isEmpty()&&list.get(0)!=null){
			Map map=list.get(0);
			resMap.put("waitBalance",Utils.convertFormat(NumberUtils.toDouble(ObjectUtils.toString(map.get("waitBalance"))),1));//待签收款
			resMap.put("signBalance",Utils.convertFormat(NumberUtils.toDouble(ObjectUtils.toString(map.get("signBalance"))),1));//已签收款
			resMap.put("waitActiveBalance",Utils.convertFormat(NumberUtils.toDouble(ObjectUtils.toString(map.get("waitActiveBalance"))),1));//待结算活动款
			resMap.put("hasActivebalance",Utils.convertFormat(NumberUtils.toDouble(ObjectUtils.toString(map.get("hasActivebalance"))),1));//已结算活动款
			resMap.put("waitBusiBalance",Utils.convertFormat(NumberUtils.toDouble(ObjectUtils.toString(map.get("waitBusiBalance"))),1));//待结算运营款
			resMap.put("hasBusibalance",Utils.convertFormat(NumberUtils.toDouble(ObjectUtils.toString(map.get("hasBusibalance"))),1));//已结算运营款
		}
		if(result1.getCode()==Result.OK){
			Result result=this.fundOrderService.payHrPayAccount(String.valueOf(result1.getData()));
			if(result.getCode()==Result.OK){
				Map map=(Map)result.getData();
				resMap.put("balanceStr",Utils.convertFormat(NumberUtils.toDouble(ObjectUtils.toString(map.get("balance"))),1));
				resMap.put("balance",NumberUtils.toDouble(ObjectUtils.toString(map.get("balance")))+"");
			}
		}
		return resMap;
	}
	/**
	 * 收支明细
	 */
	public Object financeList(String startDate,String endDate,Corporation corporation){
		int corpId=SessionUtil.getUser().getCorpId();
		Result result1=this.hrpayAccountService.hrPayAccount(HrpayAccount.acctoun_type_corp,corpId);
		if(result1.getCode()!=Result.OK)return null;
		Result result= this.fundOrderService.payHrPayAccountDetail(corporation.startToPageNum(),corporation.getLimit(),null,String.valueOf(result1.getData()),startDate,endDate);
		if(result.getCode()!=Result.OK)return result;
		Map pageInfo=(Map)result.getData();
		List _res_list=null;
		if(pageInfo.containsKey("list"))_res_list=(List)pageInfo.get("list");
		for(int i=0;_res_list!=null&&i<_res_list.size();i++){
			Map map=(Map)_res_list.get(i);
			try {
				map.put("pay_date",Utils.stringToDate(ObjectUtils.toString(map.get("add_date")),"yyyy-MM-dd HH:mm:ss"));
			}catch (Exception e){

			}
		}
		return result;
	}
	/**
	 * 提现进度
	 */
	public Result financeWithdrawProgresss(){
		return new Result(Result.OK,"");//TODO 待实现
	}
	/**
	 * 提现
	 * 暂时仅支持提现到微信，未来再开放提现到银行卡
	 */
	public Result financeWithdraw(int accountId,Double amt){
		if(amt<0.3d)return new Result(Result.OK,"提现金额必须大于等于0.3元");
		int corpId=SessionUtil.getUser().getCorpId();
		if(accountId<=0)return new Result(Result.ERROR, "请选择收款账户");
		Account account=accountMapper.selectByPrimaryKey(accountId);
		if(account==null)return new Result(Result.ERROR, "请选择收款账户");
		if(account.getCorpId()!=corpId)return new Result(Result.ERROR, "收款账户错误");
		if(account.getAccountType()==null)return new Result(Result.ERROR, "账号类型错误");
		Result result=null;
		if(account.getAccountType()==Constants.ACCOUNT_TYPE_2.getIntKey()) {//微信
			Result result1=this.hrpayAccountService.hrPayAccount(HrpayAccount.acctoun_type_corp,corpId);
			if(result1.getCode()!=Result.OK)return result1;
			result=fundOrderService.payHrPayWithdrawToWx(String.valueOf(result1.getData()),NumberUtils.toInt(account.getCardAccount()),amt);
		}else{
			return new Result(Result.ERROR, "暂时仅支持提现到微信");
		}
		if(result.getCode()==Result.OK)return new Result(Result.OK,"提现成功");
		else return new Result(Result.ERROR,result.getData());
	}
}
