package com.hrtx.web.service;

import com.hrtx.web.mapper.SkuMapper;
import com.hrtx.web.mapper.SkuPropertyMapper;
import com.hrtx.web.pojo.Sku;
import com.hrtx.web.pojo.SkuProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


@Service
public class SkuService {
	
	@Autowired
	private SkuMapper skuMapper;
	@Autowired
	private SkuPropertyMapper skuPropertyMapper;

	public LinkedHashMap<String, LinkedHashMap<String, String>> skuListByGid(Long gId) {
		LinkedHashMap<String, LinkedHashMap<String, String>> skuidMap = new LinkedHashMap<String, LinkedHashMap<String, String>>();

		List<Sku> skuList = skuMapper.findSkuInfo(gId);
		List<SkuProperty> skuPropertyList = skuPropertyMapper.findSkuPropertyByGid(gId);

		//从tb_sku中读取固有的几个字段
		if (skuList != null && skuList.size() > 0) {
			for (Sku sku : skuList) {
				LinkedHashMap<String, String> skuMap = new LinkedHashMap<String, String>();
				skuMap.put("skuId", String.valueOf(sku.getSkuId()));
				skuMap.put("skuTobPrice", sku.getSkuTobPrice());
				skuMap.put("skuTocPrice", sku.getSkuTocPrice());
//				skuMap.put("skuIsNum", sku.getSkuIsNum());
				skuMap.put("skuSaleNum", sku.getSkuSaleNum());
				skuMap.put("skuNum", String.valueOf(sku.getSkuNum()));
				skuMap.put("skuGoodsType", sku.getSkuGoodsType());
				skuMap.put("skuRepoGoods", sku.getSkuRepoGoods());

				skuidMap.put(sku.getSkuId().toString(), skuMap);
			}
		}
		//从tb_sku_property读取动态的K-V
		if (skuPropertyList != null && skuPropertyList.size() > 0) {
			for (SkuProperty skuProperty : skuPropertyList) {
				//读取skuid相应的map,新增动态K-V,再put回去
				LinkedHashMap<String, String> skumap = skuidMap.get(skuProperty.getSkuId().toString());
				if(skumap==null) skumap = new LinkedHashMap<String, String>();

				skumap.put(skuProperty.getSkupKey(), skuProperty.getSkupValue());
				skuidMap.put(skuProperty.getSkuId().toString(), skumap);
			}
		}

		return skuidMap;
	}
}
