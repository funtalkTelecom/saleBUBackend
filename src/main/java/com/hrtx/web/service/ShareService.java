package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.Constants;
import com.hrtx.global.SessionUtil;
import com.hrtx.web.mapper.PromotionPlanMapper;
import com.hrtx.web.mapper.PromotionPlanNumMapper;
import com.hrtx.web.mapper.UserMapper;
import com.hrtx.web.pojo.Dict;
import com.hrtx.web.pojo.PromotionPlan;
import com.hrtx.web.pojo.PromotionPlanNum;
import com.hrtx.web.pojo.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ShareService {
	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private PromotionPlanMapper promotionPlanMapper;
	@Autowired private PromotionPlanNumMapper promotionPlanNumMapper;
	@Autowired private UserMapper userMapper;
	/**
	 * 添加合伙人信息
	 */
	public Result addInfo(){
		return new Result(Result.OK,"");
	}

	/**
	 * 查询合伙人信息
	 */
	public Result findInfo(){
		return new Result(Result.OK,"");
	}

	/**
	 * 修改合伙人信息
	 */
	public Result editInfo(){
		return new Result(Result.OK,"");
	}

	///////////////////////////////////////////////////////
	/**
	 * 查看合伙人收益
	 */
	public Result findIncome(){
		return new Result(Result.OK,"");
	}
	/**
	 * 查看推广记录
	 */
	public Result queryShareList(){
		return new Result(Result.OK,"");
	}
	/**
	 * 生成推广卡片
	 */
	public Result shareCard(){
		return new Result(Result.OK,"");
	}
	/**
	 * 生成分享地址
	 */
	public Result shareUrl(){
		return new Result(Result.OK,"");
	}
	/**
	 * 分享浏览记录统计
	 */
	public Result shareBrowseCount(){
		return new Result(Result.OK,"");
	}
	/**
	 * 分享浏览记录
	 */
	public Result shareBrowse(){
		return new Result(Result.OK,"");
	}

	//////////////////////////////////////////////
	/**
	 * 余额查看
	 */
	public Result financeBalance(){
		return new Result(Result.OK,"");
	}
	/**
	 * 结算汇总
	 */
	public Result financeCount(){
		return new Result(Result.OK,"");
	}
	/**
	 * 收支明细
	 */
	public Result financeList(){
		return new Result(Result.OK,"");
	}
	/**
	 * 收支明细
	 */
	public Result financeWithdrawProgresss(){
		return new Result(Result.OK,"");
	}


	//////////////////////////////////////////////

	/**
	 * 提交默认佣金--初始化	(商家开户时调用)
	 * @return
	 */
	public Result defaultCommission(){
		/*
		 *利益关系体  2技术服务费；3交易服务费；4发展人；5天下梧桐
		 */
		return new Result(Result.OK,"");
	}

	/**
	 *商家设置结算佣金
	 * @return
	 */
	public Result saveCommission(){
		return new Result(Result.OK,"");
	}
	/**
	 *计算佣金(商家订单支付成功后调用)
	 * @return
	 */
	public Result calculateCommission(){
		return new Result(Result.OK,"");
	}
	/**
	 * 正式结算佣金(商家订单签收后7天调用)
	 * @return
	 */
	public Result settelCommission(){
		return new Result(Result.OK,"");
	}

	/**
	 *
	 * @param status
	 * @param num
	 * @param start
	 * @param limit
	 * @return
	 */
	public Result promotionPlanPage(int status,String num,int start,int limit) {
		PromotionPlan pp=new PromotionPlan();
		pp.setStart(start);
		pp.setLimit(limit);
		PageHelper.startPage(pp.startToPageNum(),pp.getLimit());
		pp.setCorpId(SessionUtil.getUser().getCorpId());
		pp.setStatus(status);
		pp.setNum(StringUtils.trim(num));
		Page<Object> ob=promotionPlanMapper.queryPageList(pp);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Result promotionPlanAdd(int operation,PromotionPlan bean,String[] nums) {
		bean.setFeeType(Constants.PROMOTION_PLAN_FEETYPE_1.getIntKey());
		bean.setCorpId(SessionUtil.getUser().getCorpId());
		boolean is_update=bean.getId()!=0;
		if(!is_update){
			bean.setUpdateUser(SessionUtil.getUserId());
			bean.setAddUser(SessionUtil.getUserId());
		}
		if(operation==1){//草稿操作
			bean.setStatus(Constants.PROMOTION_PLAN_STATUS_1.getIntKey());
			return promotionPlanSave(bean,nums);
		}
		bean.setStatus(Constants.PROMOTION_PLAN_STATUS_2.getIntKey());
		/////////////////////////////////
		//promotion 0全部号码1根据号码销售价格段2指定号码;award_way 1固定金额2订单比例;is_limit0无1有
		if(bean.getPromotion()== Constants.PROMOTION_PLAN_PROMOTION_0.getIntKey()){
			nums=new String[0];
			bean.setBeginPrice(new BigDecimal(0d));
			bean.setEndPrice(new BigDecimal(0d));
		}else if(bean.getPromotion()==Constants.PROMOTION_PLAN_PROMOTION_1.getIntKey()){
			nums=new String[0];
			if(bean.getBeginPrice().doubleValue()<0)return new Result(Result.ERROR,"价格范围必须大于等于0元");
			if(bean.getBeginPrice().doubleValue()>bean.getEndPrice().doubleValue())return new Result(Result.ERROR,"截至价格需大于等于开始价格");
		}else if(bean.getPromotion()==Constants.PROMOTION_PLAN_PROMOTION_2.getIntKey()){
			if(nums.length<=0)return new Result(Result.ERROR,"请输入参与推广的号码");
			bean.setBeginPrice(new BigDecimal(0d));
			bean.setEndPrice(new BigDecimal(0d));
		}else{
			return new Result(Result.ERROR,"未知参与方式");
		}
		if(bean.getAward().doubleValue()<=0d)return new Result(Result.ERROR,"奖励金额不能小于等于0");
		if(bean.getAwardWay()==Constants.PROMOTION_PLAN_AWARDWAY_1.getIntKey()){
		}else if(bean.getAwardWay()==Constants.PROMOTION_PLAN_AWARDWAY_2.getIntKey()){
			if(bean.getAward().doubleValue()>100d)return new Result(Result.ERROR,"奖励方案按比例时，比例不能超过100%");
		}else{
			return new Result(Result.ERROR,"未知奖励方案");
		}
		if(bean.getEndDate().getTime()<bean.getBeginDate().getTime())return new Result(Result.ERROR,"截至时间需大于等于开始时间");
		/*
		* 1、全部号码参与时间段不能冲突
		* 2、指定号码的同号码参与时间段不能冲突
		* 3、指定价格段的同价格区间参与时间段不能冲突
		*  区间对比
		*  区间线性效果:a_start	b_start------b_end	a_end
		*  反响思考  a_start>b_end or a_end<b_start 时不冲突，其他情况都冲突
		* */
		Set<String> num_set=new HashSet<>();
		for (String num:nums){
			num_set.add(StringUtils.trim(num));
		}
		if(num_set.size()!=nums.length)return new Result(Result.ERROR,"所提交的号码中存在重复");
		Page<PromotionPlan> page=this.promotionPlanMapper.queryConflictList(bean);//查出时间冲突的推广
		for(PromotionPlan cbean:page){
			if(cbean.getPromotion()!=bean.getPromotion())continue;
			if(cbean.getPromotion()==Constants.PROMOTION_PLAN_PROMOTION_0.getIntKey())return new Result(Result.ERROR,"您当前提交的和之前提交的推广计划(推广编号:"+cbean.getId()+")存在冲突");
			if(cbean.getPromotion()==Constants.PROMOTION_PLAN_PROMOTION_1.getIntKey()){//价格
				double a_start=bean.getBeginPrice().doubleValue();
				double a_end=bean.getEndPrice().doubleValue();
				double b_start=cbean.getBeginPrice().doubleValue();
				double b_end=cbean.getEndPrice().doubleValue();
				if(!(a_start>b_end || a_end<b_start))return new Result(Result.ERROR,"您当前提交的和之前提交的推广计划(推广编号:"+cbean.getId()+")存在价格冲突");
			}
			if(cbean.getPromotion()==Constants.PROMOTION_PLAN_PROMOTION_2.getIntKey()){
				Example example = new Example(PromotionPlanNum.class);
				example.createCriteria().andEqualTo("promotionPlanId",cbean.getId()).andEqualTo("isDel",0);
				List<?> _list=this.promotionPlanNumMapper.selectByExample(example);
				Set<String> cnum_set=new HashSet<>();
				cnum_set.addAll(num_set);
				for (int i=0;i<_list.size();i++){
					PromotionPlanNum cbeanNum=(PromotionPlanNum)_list.get(i);
					cnum_set.add(cbeanNum.getNum());
				}
				if(cnum_set.size()!=(num_set.size()+_list.size()))return new Result(Result.ERROR,"您当前提交的和之前提交的推广计划(推广编号:"+cbean.getId()+")存在号码冲突");
			}
		}
		return promotionPlanSave(bean,nums);
	}
	private Result promotionPlanSave(PromotionPlan bean,String[] nums) {
		if(bean.getId()!=0){
			PromotionPlan newBean=this.promotionPlanMapper.selectByPrimaryKey(bean.getId());
			if(newBean==null)return new Result(Result.ERROR,"数据错误");
			if(!SessionUtil.getUser().getCorpId().equals(newBean.getCorpId()))return new Result(Result.ERROR,"您无权修改他人推广计划");
			if(newBean.getStatus()!=Constants.PROMOTION_PLAN_STATUS_1.getIntKey())return new Result(Result.ERROR,"只能对草稿的推广计划进行修改");
			newBean.setPromotion(bean.getPromotion());
			newBean.setAwardWay(bean.getAwardWay());
			newBean.setAward(bean.getAward());
			newBean.setIsLimit(bean.getIsLimit());
			newBean.setLimitAward(bean.getLimitAward());
			newBean.setBeginPrice(bean.getBeginPrice());
			newBean.setEndPrice(bean.getEndPrice());
			newBean.setUpdateUser(SessionUtil.getUserId());
			newBean.setUpdateDate(new Date());
			newBean.setBeginDate(bean.getBeginDate());
			newBean.setEndDate(bean.getEndDate());
			newBean.setStatus(bean.getStatus());
			this.promotionPlanMapper.updateByPrimaryKey(newBean);
		}else{
			this.promotionPlanMapper.insert(bean);
		}
		this.promotionPlanNumMapper.deletePromotionPlanNum(bean.getId());
		for(String num:nums){
			PromotionPlanNum nbean=new PromotionPlanNum(bean.getId(),StringUtils.trim(num));
			promotionPlanNumMapper.insert(nbean);
		}
		return new Result(Result.OK,"保存成功");
	}
	public Result findPromotionPlan(Integer promotion_plan_id){
		PromotionPlan bean=this.promotionPlanMapper.selectByPrimaryKey(promotion_plan_id);
		if(!SessionUtil.getUser().getCorpId().equals(bean.getCorpId()))return new Result(Result.ERROR,"您无权查看他人推广计划");
		Example example = new Example(PromotionPlanNum.class);
		example.createCriteria().andEqualTo("promotionPlanId",promotion_plan_id).andEqualTo("isDel",0);
		List<?> _list=this.promotionPlanNumMapper.selectByExample(example);
		bean.setList((List<Object>)_list);
		User adduser=userMapper.selectByPrimaryKey(bean.getAddUser());
		bean.setAddUserStr(adduser.getName());
		User updateuser=userMapper.selectByPrimaryKey(bean.getUpdateUser());
		bean.setUpdateUserStr(updateuser.getName());
		return new Result(Result.OK,bean);
	}
	public Result promotionPlanExpire(Integer promotion_plan_id){
		PromotionPlan bean=this.promotionPlanMapper.selectByPrimaryKey(promotion_plan_id);
		if(!SessionUtil.getUser().getCorpId().equals(bean.getCorpId()))return new Result(Result.ERROR,"您无权修改他人推广计划");
		if(bean.getStatus()!=Constants.PROMOTION_PLAN_STATUS_2.getIntKey())return new Result(Result.ERROR,"只能对有效的推广计划进行设置");
		bean.setStatus(Constants.PROMOTION_PLAN_STATUS_3.getIntKey());
		bean.setUpdateDate(new Date());
		bean.setUpdateUser(SessionUtil.getUserId());
		this.promotionPlanMapper.updateByPrimaryKey(bean);
		return new Result(Result.OK,"设置成功");
	}
}
