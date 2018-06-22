package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.mapper.NumberMapper;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.Number;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiNumberController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private NumberMapper numberMapper;
	@Autowired
	private ApiSessionUtil apiSessionUtil;

	/**
	 * 根据tags获取号码
	 * @param number
	 * @param request
	 * @return
	 */
	@GetMapping("/number")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result numberList(Number number, HttpServletRequest request){
		PageInfo<Object> pm = null;
		try {
			int pageNum = request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum"));
			int limit = request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit"));
			number.setStart(limit*(pageNum-1));
			number.setLimit(limit);
			String tags = request.getParameter("tags")==null?"": request.getParameter("tags");
			tags = "'"+ tags.replaceAll(",", "','") +"'";

			PageHelper.startPage(number.getPageNum(),number.getLimit());
			Page<Object> ob=this.numberMapper.queryPageListApi(tags);
			if(ob!=null && ob.size()>0){
				//处理号码,生成号码块字段(numBlock)
				for (int i = 0; i < ob.size(); i++) {
					Map obj= (Map) ob.get(i);
					obj.put("numBlock", getNumBlock((String) obj.get("numResource")));
				}
			}
			pm = new PageInfo<Object>(ob);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
			return new Result(Result.ERROR, pm);
		}

		return new Result(Result.OK, pm);
	}

	@GetMapping("/numberTypeList")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result numberTypeList(Number number, HttpServletRequest request){
		PageInfo<Object> pm = null;
		try {
			Consumer consumer = apiSessionUtil.getConsumer();
			if(consumer==null) return new Result(Result.ERROR, "未获取到用户");

			int pageNum = request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum"));
			int limit = request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit"));
			number.setStart(limit*(pageNum-1));
			number.setLimit(limit);

			String skuGoodsType = request.getParameter("skuGoodsType");
			if(skuGoodsType==null || "".equals(skuGoodsType)) return new Result(Result.ERROR, "商品类型不能为空");
			else skuGoodsType = "'" + skuGoodsType.replaceAll(",", "','") + "'";

			PageHelper.startPage(number.getPageNum(),number.getLimit());
			Page<Object> ob=this.numberMapper.queryPageListApiForNumber3(skuGoodsType, consumer.getAgentCity());
			if(ob!=null && ob.size()>0){
				//处理号码,生成号码块字段(numBlock)
				for (int i = 0; i < ob.size(); i++) {
					Map obj= (Map) ob.get(i);
					obj.put("numBlock", getNumBlock((String) obj.get("numResource")));

					//不是一级代理商,把价格和保底消费置空
					if(consumer.getIsAgent()==null || 2!=consumer.getIsAgent()){
						obj.put("lowConsume", "");
						obj.put("skuTocPrice", "");
						obj.put("skuTobPrice", "");
					}
				}
			}
			pm = new PageInfo<Object>(ob);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
			return new Result(Result.ERROR, pm);
		}

		return new Result(Result.OK, pm);
	}

	@GetMapping("/number/{id}")
	@Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result numberInfo(@PathVariable("id") String id, HttpServletRequest request){
		Map number = new HashMap();
		try {
			number = numberMapper.getNumInfoById(id);
			number.put("numBlock", getNumBlock((String) number.get("numResource")));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return new Result(Result.ERROR, new HashMap());
		}

		return new Result(Result.OK, number);
	}

	private String[] getNumBlock(String num) {
		StringBuffer numResource = new StringBuffer(num);
		numResource.insert(3, ",");
		numResource.insert(8, ",");
		return numResource.toString().split(",");
	}
}
