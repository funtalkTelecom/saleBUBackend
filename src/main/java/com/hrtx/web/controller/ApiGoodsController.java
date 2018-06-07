package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.mapper.GoodsMapper;
import com.hrtx.web.pojo.Goods;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class ApiGoodsController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private GoodsMapper goodsMapper;

	/**
	 * 在售商品列表
	 * @param goods
	 * @param request
	 * @return
	 */
	@GetMapping("/goods")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public String goodsList(Goods goods, HttpServletRequest request){
		PageInfo<Object> pm = null;
		try {
			goods.setPageNum(request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum")));
			goods.setLimit(request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit")));

			PageHelper.startPage(goods.getPageNum(),goods.getLimit());
			Page<Object> ob=this.goodsMapper.queryPageListApi(goods);
			pm = new PageInfo<Object>(ob);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
		}

		return JSONObject.fromObject(pm).toString();
	}
}
