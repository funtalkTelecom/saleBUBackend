package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.Dict;
import com.hrtx.web.pojo.File;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.pojo.Sku;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ApiGoodsService {

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private SkuMapper skuMapper;
	@Autowired
	private SkuPropertyMapper skuPropertyMapper;
	@Autowired
	private FileMapper fileMapper;
	@Autowired
	private ApiSessionUtil apiSessionUtil;

	/**
	 * 在售商品列表
	 * 根据传入的地市进行查询
	 * @param goods
	 * @param request
	 * @return
	 */
	public Result goodsList(Goods goods, HttpServletRequest request){
		PageInfo<Object> pm = null;
		Result result = null;
		try {
			goods.setPageNum(request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum")));
			goods.setLimit(request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit")));

			//模拟登陆
//			Consumer u = new Consumer();
//			u.setId(1L);
//			u.setName("周元强");
//			u.setCity("396");
//			u.setIsAgent(2);//设置为一级代理商
//			u.setAgentCity(396L);
			//apiSessionUtil.getConsumer()==null?u.getAgentCity():

			goods.setgSaleCity(String.valueOf(apiSessionUtil.getConsumer().getAgentCity()));
			PageHelper.startPage(goods.getPageNum(),goods.getLimit());
			Page<Object> ob=this.goodsMapper.queryPageSkuListApi(goods, goods.getgSaleCity());
			if(ob!=null && ob.size()>0){
				for(int i=0; i<ob.size(); i++){
					Map g = (Map) ob.get(i);
					g.put("fileName", SystemParam.get("domain-full") + "/get-img"+SystemParam.get("goodsPics") +g.get("gId")+"/"+ g.get("fileName"));
					//获取sku的属性,追加到名称中
					List prolist = skuPropertyMapper.findSkuPropertyBySkuidForOrder(Long.parseLong(String.valueOf(g.get("skuId"))));
					if(prolist!=null && prolist.size()>0){
						StringBuffer pro = new StringBuffer();
						for(int j=0; j<prolist.size(); j++){
							Map p = (Map) prolist.get(j);
							pro.append(p.get("keyValue")+" ");
						}
						String n = g.get("gName") + " (" + pro.substring(0, pro.length()-1) + ")";
						g.put("gName", pro.length()>0?n:n.substring(0, n.length()-3));
					}
				}
			}
			pm = new PageInfo<Object>(ob);
			result = new Result(Result.OK, pm);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
			result = new Result(Result.ERROR, pm);
		}

		return result;
	}

	/**
	 * 商品详情
	 * sku列表
	 * file列表
	 * @param id(skuid)
	 * @param request
	 * @return
	 */
	public Result goodsDetail(String id, HttpServletRequest request){
		Map returnMap = new HashMap();
		Goods goods = new Goods();
		Sku sku = new Sku();
		List<File> fileList = new ArrayList<File>();

		try {
			goods = goodsMapper.findGoodsInfoBySkuid(id);

			sku = skuMapper.getSkuBySkuid(Long.parseLong(id));
			//获取sku的属性,追加到名称中
			List prolist = skuPropertyMapper.findSkuPropertyBySkuidForOrder(Long.parseLong(id));
			if(prolist!=null && prolist.size()>0){
				StringBuffer pro = new StringBuffer();
				for(int j=0; j<prolist.size(); j++){
					Map p = (Map) prolist.get(j);
					pro.append(p.get("keyValue")+" ");
				}
				String n = goods.getgName() + " (" + pro.substring(0, pro.length()-1) + ")";
				goods.setgName(pro.length()>0?n:n.substring(0, n.length()-3));
			}

			fileList = fileMapper.findFilesByRefid(String.valueOf(goods.getgId()));
			if (fileList != null && fileList.size() > 0) {
				for (File file : fileList) {
					file.setFileName(SystemParam.get("domain-full") + "/get-img"+SystemParam.get("goodsPics") +goods.getgId()+"/"+ file.getFileName());
				}
			}
			returnMap.put("code", Result.OK);
		} catch (Exception e) {
			e.printStackTrace();
			goods = new Goods();
			sku = new Sku();
			fileList = new ArrayList<>();
			returnMap.put("code", Result.ERROR);
			return new Result(Result.ERROR, "异常");
		}


		returnMap.put("goods", goods);
		returnMap.put("sku", sku);
		returnMap.put("fileList", fileList);
		return new Result(Result.OK, returnMap);
	}
}
