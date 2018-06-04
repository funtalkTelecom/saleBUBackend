package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.web.mapper.GoodsMapper;
import com.hrtx.web.mapper.SkuMapper;
import com.hrtx.web.mapper.SkuPropertyMapper;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.pojo.Sku;
import com.hrtx.web.pojo.SkuProperty;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Service
public class GoodsService {
	
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private SkuMapper skuMapper;
	@Autowired
	private SkuPropertyMapper skuPropertyMapper;

	public Result pageGoods(Goods goods) {
		PageHelper.startPage(goods.getPageNum(),goods.getLimit());
		Page<Object> ob=this.goodsMapper.queryPageList(goods);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Goods findGoodsById(Long id) {
		Goods goods = goodsMapper.findGoodsInfo(id);
		return goods;
	}

	public Result goodsEdit(Goods goods, HttpServletRequest request) {
        try {
            String ignoreKey = ",skuTobPrice,skuTocPrice,skuIsNum,skuSaleNum,skuGoodsType,skuRepoGoods,";
            //商品主表操作
            if (goods.getgId() != null && goods.getgId() > 0) {
                goodsMapper.goodsEdit(goods);
            } else {
                List<Goods> list = new ArrayList<Goods>();
                goods.setgId(goods.getGeneralId());
                list.add(goods);
                goodsMapper.insertBatch(list);
            }
            //商品子表操作
            Sku sku = new Sku();
            sku.setgId(goods.getgId());
            skuMapper.deleteSkuByGid(sku);

            SkuProperty skuProperty = new SkuProperty();
            skuProperty.setgId(sku.getgId());
            skuPropertyMapper.deleteSkuPropertyByGid(skuProperty);


            String skuPropertyJsonStr = request.getParameter("skuJson") == null ? "" : request.getParameter("skuJson");
            JSONArray skuPropertyJsonArr = JSONArray.fromObject(skuPropertyJsonStr);
            if(!skuPropertyJsonArr.isEmpty()){
                List<SkuProperty> skuPropertyList = new ArrayList<SkuProperty>();
                List<Sku> skuList = new ArrayList<Sku>();
                String skuSaleNum = "";
                for(int i=0; i<skuPropertyJsonArr.size(); i++){
                    //sku表操作
                    sku = new Sku();
                    sku.setSkuId(sku.getGeneralId());
                    sku.setgId(goods.getgId());

                    //sku属性表操作
                    JSONObject obj = (JSONObject) skuPropertyJsonArr.get(i);
                    Iterator it = obj.keySet().iterator();

                    //,skuTobPrice,skuTocPrice,skuIsNum,skuSaleNum,skuGoodsType,skuRepoGoods,
                    sku.setSkuTobPrice(((JSONObject) obj.get("skuTobPrice")).get("value")==null||((JSONObject) obj.get("skuTobPrice")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuTobPrice")).get("value"));
                    sku.setSkuTocPrice(((JSONObject) obj.get("skuTocPrice")).get("value")==null||((JSONObject) obj.get("skuTocPrice")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuTocPrice")).get("value"));
                    sku.setSkuIsNum(((JSONObject) obj.get("skuIsNum")).get("value")==null||((JSONObject) obj.get("skuIsNum")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuIsNum")).get("value"));
                    skuSaleNum = ((JSONObject) obj.get("skuSaleNum")).get("value")==null||((JSONObject) obj.get("skuSaleNum")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuSaleNum")).get("value");
                    skuSaleNum = checkSkuSaleNum(skuSaleNum);
                    sku.setSkuSaleNum(skuSaleNum);
                    sku.setSkuGoodsType(((JSONObject) obj.get("skuGoodsType")).get("value")==null||((JSONObject) obj.get("skuGoodsType")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuGoodsType")).get("value"));
                    sku.setSkuRepoGoods(((JSONObject) obj.get("skuRepoGoods")).get("value")==null||((JSONObject) obj.get("skuRepoGoods")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuRepoGoods")).get("value"));

                    skuPropertyList.clear();
                    while (it.hasNext()){
                        String key = (String) it.next();
                        JSONObject col = (JSONObject) obj.get(key);
                        if(ignoreKey.indexOf(key)!=-1) {
                            continue;
                        }
                        skuProperty = new SkuProperty();
                        skuProperty.setgId(sku.getgId());
                        skuProperty.setSkuId(sku.getSkuId());
                        skuProperty.setSkupId(skuProperty.getGeneralId());
                        skuProperty.setSkupKey(key);
                        skuProperty.setSkupValue((String) (col.get("value")==null||col.get("value").equals("null")?"":col.get("value")));
                        skuProperty.setSeq(Integer.parseInt((String) ((col.get("seq").equals(""))?"0":col.get("seq"))));

                        skuPropertyList.add(skuProperty);
                    }
                    skuPropertyMapper.insertBatch(skuPropertyList);
                    //sku属性表操作end
                    skuList.add(sku);
                }
                skuMapper.insertBatch(skuList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(Result.ERROR, "操作异常");
        }

        return new Result(Result.OK, "提交成功");
	}

    private String checkSkuSaleNum(String skuSaleNum) {
	    String[] skuSaleNumbs = skuSaleNum.split("\\r?\\n");
	    if(skuSaleNumbs!=null && skuSaleNumbs.length>0){
            skuSaleNum = "";
            for (int i = 0; i < skuSaleNumbs.length; i++) {
                //验证号码可用性
                skuSaleNum += skuSaleNumbs[i]+"\n";
            }
        }else return "";

	    return skuSaleNum.substring(0, skuSaleNum.length()-1);
    }

    public Result goodsDelete(Goods goods) {
		goodsMapper.goodsDelete(goods);
        //商品子表操作
        Sku sku = new Sku();
        sku.setgId(goods.getgId());
        skuMapper.deleteSkuByGid(sku);

        SkuProperty skuProperty = new SkuProperty();
        skuProperty.setgId(sku.getgId());
        skuPropertyMapper.deleteSkuPropertyByGid(skuProperty);

        return new Result(Result.OK, "删除成功");
	}
}
