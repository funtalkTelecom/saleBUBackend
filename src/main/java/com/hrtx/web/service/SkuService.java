package com.hrtx.web.service;

import com.hrtx.web.mapper.NumberMapper;
import com.hrtx.web.mapper.SkuMapper;
import com.hrtx.web.mapper.SkuPropertyMapper;
import com.hrtx.web.pojo.Number;
import com.hrtx.web.pojo.Sku;
import com.hrtx.web.pojo.SkuProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


@Service
public class SkuService {
	
	@Autowired
	private SkuMapper skuMapper;
	@Autowired
	private NumberMapper numberMapper;
	@Autowired
	private SkuPropertyMapper skuPropertyMapper;

	public LinkedHashMap<String, LinkedHashMap<String, String>> skuListByGid(Integer gId) {
		LinkedHashMap<String, LinkedHashMap<String, String>> skuidMap = new LinkedHashMap<String, LinkedHashMap<String, String>>();

		List<Sku> skuList = skuMapper.findSkuInfo(gId);
		List<SkuProperty> skuPropertyList = skuPropertyMapper.findSkuPropertyByGid(gId);
		DecimalFormat df = new DecimalFormat("######0.00");
		//从tb_sku中读取固有的几个字段
		if (skuList != null && skuList.size() > 0) {
			for (Sku sku : skuList) {
				LinkedHashMap<String, String> skuMap = new LinkedHashMap<String, String>();
				skuMap.put("skuId", String.valueOf(sku.getSkuId()));
				skuMap.put("skuTobPrice", df.format(sku.getSkuTobPrice()));
//				skuMap.put("skuTocPrice", df.format(sku.getSkuTocPrice()));
//				skuMap.put("skuIsNum", sku.getSkuIsNum());
				skuMap.put("skuSaleNum", concatSaleNumBySkuid(String.valueOf(sku.getSkuId())));
//				skuMap.put("skuSaleNum", sku.getSkuSaleNum().replaceAll(",", "\n"));
				skuMap.put("skuNum", String.valueOf(sku.getSkuNum()));
				skuMap.put("skuGoodsType", sku.getSkuGoodsType());
				skuMap.put("skuRepoGoods", sku.getSkuRepoGoods());
				skuMap.put("skuRepoGoodsName", sku.getSkuRepoGoodsName());
				skuMap.put("skustatusText", sku.getStatusText());

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

    private String concatSaleNumBySkuid(String skuid) {
        String result = "";
        List<Number> list = numberMapper.getListBySkuid(skuid);
        if(list!=null && list.size()>0){
            for(int i=0; i<list.size(); i++){
                Number number = list.get(i);
                result += number.getNumResource() + "\n";
            }
        }

        return "".equals(result)?"":result.substring(0, result.length()-1);
    }
}
