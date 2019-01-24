package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.AgentMapper;
import com.hrtx.web.mapper.NumPriceMapper;
import com.hrtx.web.mapper.NumberMapper;
import com.hrtx.web.pojo.Agent;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.NumPrice;
import com.hrtx.web.pojo.Number;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ApiNumberService {

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private NumberMapper numberMapper;
	@Autowired
	private ApiSessionUtil apiSessionUtil;
	@Autowired
	private AgentMapper agentMapper;
	@Autowired
	private NumPriceMapper numPriceMapper;
	@Autowired
	private NumService numService;
	@Autowired
	private AgentService agentService;
	@Autowired
	private ActivityService activityService;
	/**
	 * 根据tags获取号码
	 * @param numPrice
	 * @param request
	 * @return
	 */
	public Result numberList(NumPrice numPrice, HttpServletRequest request){
		PageInfo<Object> pm = null;
		int pageNum = request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum"));
		int limit = request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit"));
		numPrice.setPageNum(pageNum);
		numPrice.setLimit(limit);
		String tags = request.getParameter("tags")==null?"": request.getParameter("tags");
		tags = "'"+ tags.replaceAll(",", "','") +"'";
		Consumer consumer= this.apiSessionUtil.getConsumer();
		//是否是代理商
		Result reagent = agentService.queryCurrAgent(consumer);
		if(reagent.getCode()!=Result.OK){
			return new Result(reagent.ERROR, reagent.getData());
		}
		Agent agent = (Agent) reagent.getData();
		//-----zdh
//		Date[] queryActiveDate = activityService.queryActiveDate(1);
//		numPrice.setFalg(1);
//		numPrice.setBeginDate(Utils.getDate(queryActiveDate[0],"yyyy-MM-dd HH:mm:ss"));
//		numPrice.setEndDate(Utils.getDate(queryActiveDate[2],"yyyy-MM-dd HH:mm:ss"));
		//------zdh
		numPrice.setAgentId(agent.getId());
		pm = numService.queryNumPrice(numPrice);
		//处理号码,生成号码块字段(numBlock)
		List ob =pm.getList();
		for (int i = 0; i < ob.size(); i++) {
			Map obj= (Map) ob.get(i);
			obj.put("numBlock", getNumBlock((String) obj.get("resource")));
		}
		return new Result(Result.OK, pm);
	}


	public Result seckillListTime( HttpServletRequest request){
		int falg = request.getParameter("falg")==null? 1: Integer.parseInt(request.getParameter("falg"));
		Date[] queryActiveDate = activityService.queryActiveDate(falg);
		NumPrice numPrice = new NumPrice();
		numPrice.setBeginDate(Utils.getDate(queryActiveDate[0],"yyyy-MM-dd HH:mm:ss"));
		numPrice.setNewDate(Utils.getDate(queryActiveDate[1],"yyyy-MM-dd HH:mm:ss"));
		numPrice.setEndDate(Utils.getDate(queryActiveDate[2],"yyyy-MM-dd HH:mm:ss"));
		return new Result(Result.OK, numPrice);
	}
	/***
	 * 秒杀
	 * @param numPrice
	 * @param request
	 * @return
	 */
	public Result numberSeckillList(NumPrice numPrice, HttpServletRequest request){
		PageInfo<Object> pm = null;
		int pageNum = request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum"));
		int limit = request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit"));
		numPrice.setPageNum(pageNum);
		numPrice.setLimit(limit);
		//1 当前正在进行；2即将开始
		int falg = request.getParameter("falg")==null? 1: Integer.parseInt(request.getParameter("falg"));
		Consumer consumer= this.apiSessionUtil.getConsumer();
		//是否是代理商
		Result reagent = agentService.queryCurrAgent(consumer);
		if(reagent.getCode()!=Result.OK){
			return new Result(reagent.ERROR, reagent.getData());
		}
		Date[] queryActiveDate = activityService.queryActiveDate(falg);
		Agent agent = (Agent) reagent.getData();
		numPrice.setAgentId(agent.getId());
		numPrice.setFalg(falg);
		numPrice.setBeginDate(Utils.getDate(queryActiveDate[0],"yyyy-MM-dd HH:mm:ss"));
		numPrice.setEndDate(Utils.getDate(queryActiveDate[2],"yyyy-MM-dd HH:mm:ss"));
		pm = numService.queryNumPrice(numPrice);
		//处理号码,生成号码块字段(numBlock)
		List ob =pm.getList();
		for (int i = 0; i < ob.size(); i++) {
			Map obj= (Map) ob.get(i);
			obj.put("numBlock", getNumBlock((String) obj.get("resource")));
		}
		return new Result(Result.OK, pm);
	}
//	public Result numberList(Number number, HttpServletRequest request){
//		PageInfo<Object> pm = null;
//		try {
//			int pageNum = request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum"));
//			int limit = request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit"));
////			number.setStart(limit*(pageNum-1));
//			number.setPageNum(pageNum);
//			number.setLimit(limit);
//			String tags = request.getParameter("tags")==null?"": request.getParameter("tags");
//			tags = "'"+ tags.replaceAll(",", "','") +"'";
//			PageHelper.startPage(number.getPageNum(),number.getLimit());
//			Page<Object> ob=this.numberMapper.queryPageListApi(tags);
//			if(ob!=null && ob.size()>0){
//				//处理号码,生成号码块字段(numBlock)
//				for (int i = 0; i < ob.size(); i++) {
//					Map obj= (Map) ob.get(i);
//					obj.put("numBlock", getNumBlock((String) obj.get("numResource")));
//				}
//			}
//			pm = new PageInfo<Object>(ob);
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//			pm = new PageInfo<Object>(null);
//			return new Result(Result.ERROR, pm);
//		}
//
//		return new Result(Result.OK, pm);
//	}

	public Result numberTypeList(Number number, HttpServletRequest request){
		Map map = new HashMap();
		PageInfo<Object> pm = null;
		Consumer consumer = apiSessionUtil.getConsumer();
//		consumer = new Consumer();
//		consumer.setId(1009758229598568448L);
		try {
			if(consumer==null) return new Result(Result.ERROR, "未获取到用户");

			int pageNum = request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum"));
			int limit = request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit"));
//			number.setStart(limit*(pageNum-1));
			number.setLimit(limit);
			number.setPageNum(pageNum);
			String skuGoodsType = request.getParameter("skuGoodsType");
			if(skuGoodsType==null || "".equals(skuGoodsType)) return new Result(Result.ERROR, "商品类型不能为空");
			else skuGoodsType = "'" + skuGoodsType.replaceAll(",", "','") + "'";

			PageHelper.startPage(number.getPageNum(),number.getLimit());
			Page<Object> ob=this.numberMapper.queryPageListApiForNumber3(skuGoodsType, consumer.getAgentCity(),consumer.getIsAgent());
			if(ob!=null && ob.size()>0){
				//处理号码,生成号码块字段(numBlock)
				for (int i = 0; i < ob.size(); i++) {
					Map obj= (Map) ob.get(i);
					obj.put("numBlock", getNumBlock((String) obj.get("numResource")));
					//普靓当对应地市无一级代理商时联系人和联系电话取默认
					if(obj.get("phone")==null || StringUtils.isBlank(String.valueOf(obj.get("phone")))) obj.put("phone", SystemParam.get("default_linktel"));
					if(obj.get("name")==null || StringUtils.isBlank(String.valueOf(obj.get("name")))) obj.put("name", SystemParam.get("default_linkman"));

					//不是一级代理商,把价格和保底消费置空
					if(consumer.getIsAgent()==null || 2!=consumer.getIsAgent()){
						obj.put("lowConsume", "");
						obj.put("skuTocPrice", "");
						obj.put("skuTobPrice", "");
						obj.put("isAgent", "0");
					}else{
						obj.put("isAgent", "1");
					}
				}
			}
			pm = new PageInfo<Object>(ob);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
			map = setIsAgent(consumer, pm);
			return new Result(Result.ERROR, map);
		}

		map = setIsAgent(consumer, pm);

		return new Result(Result.OK, map);
	}

	private Map setIsAgent(Consumer consumer, PageInfo<Object> pm) {
		String isAgent = "";
		if(consumer.getIsAgent()==null || 2!=consumer.getIsAgent()){
			isAgent = "0";
		}else{
			isAgent = "1";
		}
		Map map = new HashMap();
		map.put("isAgent", isAgent);
		map.put("pm", pm);

		return map;
	}

	public Result numberInfo(String id, HttpServletRequest request){
		PageInfo<Object> pm = null;

		Map map = numberMapper.getNumSkuGoodsTypeById(id);
		String skuGoodsType =String.valueOf(map.get("sku_goods_type"));
		Map obj = new HashMap();
		if(map == null) return new Result(Result.ERROR, "未找到号码");
		if( skuGoodsType.equals("4")){ //超靓
			NumPrice numPrice = new NumPrice();
			int unmId = NumberUtils.toInt(id);
			numPrice.setNumId(unmId);
			Consumer consumer= this.apiSessionUtil.getConsumer();
			//是否是代理商
			Result reagent = agentService.queryCurrAgent(consumer);
			if(reagent.getCode()!=Result.OK){
				return new Result(reagent.ERROR, reagent.getData());
			}

			Agent agent = (Agent) reagent.getData();
			numPrice.setAgentId(agent.getId());
			pm = numService.queryNumPrice(numPrice);
			List ob = pm.getList();
			if(ob.size()==0) return new Result(Result.ERROR, "未找到号码");

			for (int i = 0; i < ob.size(); i++) {
				obj= (Map) ob.get(i);
				obj.put("numBlock", getNumBlock((String) obj.get("resource")));
			}
		}else if(skuGoodsType.equals("3")){  //普靓
			obj = numberMapper.getNumInfoById(id);
			if(obj==null) return new Result(Result.ERROR, "未找到号码");
			obj.put("numBlock", getNumBlock((String) obj.get("numResource")));
		}
		obj.put("newDate",new Date());
		return new Result(Result.OK, obj);
	}

	private String[] getNumBlock(String num) {
		StringBuffer numResource = new StringBuffer(num);
		numResource.insert(3, ",");
		numResource.insert(8, ",");
		return numResource.toString().split(",");
	}


	/**
	 * 根据号码查询获取号码
	 * @param numPrice
	 * @param request
	 * @return
	 */
	public Result numberListByNum(NumPrice numPrice, HttpServletRequest request){
		PageInfo<Object> pm = null;
		numPrice.setPageNum(numPrice.startToPageNum());
		String num = request.getParameter("num")==null?"": request.getParameter("num");
		Consumer consumer= this.apiSessionUtil.getConsumer();
	    Result reagent = agentService.queryCurrAgent(consumer);
	    if(reagent.getCode()!=Result.OK){
			return new Result(reagent.ERROR, reagent.getData());
		}
		Agent agent = (Agent) reagent.getData();
		numPrice.setAgentId(agent.getId());
		numPrice.setResource(num);
		pm = numService.queryNumPrice(numPrice);
		List ob =pm.getList();
		if(ob!=null && ob.size()>0){
			//处理号码,生成号码块字段(numBlock)
			for (int i = 0; i < ob.size(); i++) {
				Map obj= (Map) ob.get(i);
				obj.put("numBlock", getNumBlock((String) obj.get("resource")));
			}
		}
		pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}


	public Result searchNumberList( NumPrice numPrice,HttpServletRequest request){
		PageInfo<Object> pm = null;
		int pageNum = request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum"));
		int limit = request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit"));
		numPrice.setPageNum(pageNum);
		numPrice.setLimit(limit);

		String feature = request.getParameter("feature")==null?"": request.getParameter("feature");  //靓号类型
		String netType = request.getParameter("netType")==null?"": request.getParameter("netType");  //运营商
		String numTags = request.getParameter("numTags")==null?"": request.getParameter("numTags");	 //吉利号
		Integer provinceCode = request.getParameter("provinceCode")==null ? 0: NumberUtils.toInt(request.getParameter("provinceCode")) ;	 //省份
		Integer cityCode = request.getParameter("cityCode")==null ? 0: NumberUtils.toInt(request.getParameter("cityCode")) ;	 //城市
		String num = request.getParameter("num")==null?"": request.getParameter("num");  //号码
		double priceS = request.getParameter("priceS")==""? 0: Double.valueOf(request.getParameter("priceS")) ;  //起始价格
		double priceE = request.getParameter("priceE")==""? 0 : Double.valueOf( request.getParameter("priceE"));  //结束价格


		Consumer consumer= this.apiSessionUtil.getConsumer();
		Result reagent = agentService.queryCurrAgent(consumer);
		if(reagent.getCode()!=Result.OK){
			return new Result(reagent.ERROR, reagent.getData());
		}
		Agent agent = (Agent) reagent.getData();
		if(cityCode==-1){
			numPrice.setProvinceCode(provinceCode);
		}else{
			numPrice.setCityCode(cityCode);
		}
		numPrice.setAgentId(agent.getId());
		numPrice.setFeature(feature);
		numPrice.setNetType(netType);
		numPrice.setNumTags(numTags);
		numPrice.setResource(num);
		numPrice.setPriceS(new BigDecimal(priceS));
		numPrice.setPriceE(new BigDecimal(priceE));

		pm = numService.queryNumPrice(numPrice);
		List ob =pm.getList();
		if(ob!=null && ob.size()>0){
			//处理号码,生成号码块字段(numBlock)
			for (int i = 0; i < ob.size(); i++) {
				Map obj= (Map) ob.get(i);
				obj.put("numBlock", getNumBlock((String) obj.get("resource")));
			}
		}
		pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

}
