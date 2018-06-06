package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.SystemParam;
import com.hrtx.global.Utils;
import com.hrtx.web.controller.BaseReturn;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.File;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.pojo.Number;
import com.hrtx.web.pojo.Sku;
import com.hrtx.web.pojo.SkuProperty;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Service
public class GoodsService {
	
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private SkuMapper skuMapper;
	@Autowired
	private SkuPropertyMapper skuPropertyMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private NumberMapper numberMapper;

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

	public Result goodsEdit(Goods goods, HttpServletRequest request, MultipartFile[] files) {
        try {
            String ignoreKey = "skuId,skuTobPrice,skuTocPrice,skuIsNum,skuSaleNum,skuGoodsType,skuRepoGoods,";
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
                    sku.setgId(goods.getgId());

                    //sku属性表操作
                    JSONObject obj = (JSONObject) skuPropertyJsonArr.get(i);
                    Iterator it = obj.keySet().iterator();

                    //,skuTobPrice,skuTocPrice,skuIsNum,skuSaleNum,skuGoodsType,skuRepoGoods,
                    sku.setSkuTobPrice(((JSONObject) obj.get("skuTobPrice")).get("value")==null||((JSONObject) obj.get("skuTobPrice")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuTobPrice")).get("value"));
                    sku.setSkuTocPrice(((JSONObject) obj.get("skuTocPrice")).get("value")==null||((JSONObject) obj.get("skuTocPrice")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuTocPrice")).get("value"));
                    sku.setSkuIsNum(((JSONObject) obj.get("skuIsNum")).get("value")==null||((JSONObject) obj.get("skuIsNum")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuIsNum")).get("value"));
                    skuSaleNum = ((JSONObject) obj.get("skuSaleNum")).get("value")==null||((JSONObject) obj.get("skuSaleNum")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuSaleNum")).get("value");
//                    验证号码可用性之前赋值旧的skuId,便于tb_num表复原状态
                    sku.setSkuId(Long.parseLong(((JSONObject) obj.get("skuId")).get("value")==null||((JSONObject) obj.get("skuId")).get("value").equals("null")?"9999": (String) ((JSONObject) obj.get("skuId")).get("value")));

                    skuSaleNum = checkSkuSaleNum(skuSaleNum, sku);
//                    验证完之后赋予新的skuId
                    sku.setSkuId(sku.getGeneralId());
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

                //富文本信息获取
                String kindeditorContent = request.getParameter("kindeditorContent");
                Utils.kindeditorWriter(kindeditorContent, goods.getgId()+".txt", SystemParam.get("kindedtiorDir"));

            }
            //图片保存
            Result result = null;
            String picSeqs = request.getParameter("picSeqs")==null?"":request.getParameter("picSeqs");
            String delPicSeqs = request.getParameter("delPicSeqs")==null?"":request.getParameter("delPicSeqs");
            if(!picSeqs.equals("")){
                fileMapper.deleteFilesByRefid(goods.getgId().toString(), picSeqs.equals("")?"":picSeqs.substring(0, picSeqs.length()-1));
            }
            if(!delPicSeqs.equals("")){
                fileMapper.deleteFilesByRefid(goods.getgId().toString(), delPicSeqs.equals("")?"":delPicSeqs.substring(0, delPicSeqs.length()-1));
            }
            if(files!=null && files.length>0){
                try {
                    List<File> fileList = new ArrayList<File>();
                    for (int i=0; i<files.length; i++) {
                        MultipartFile file = files[i];
                        File f = new File();
                        f.setFileId(f.getGeneralId());
                        f.setFileGroup("goodsPic");
                        result = BaseReturn.uploadFile(SystemParam.get("goodsPics")+goods.getgId()+"\\", "jpg,png,gif", file, false, false);
                        f.setFileName(((Map)result.getData()).get("sourceServerFileName").toString());
                        f.setRefId(goods.getgId());
                        f.setSeq(Integer.parseInt(picSeqs.replaceAll("\"","").split(",")[i]));
                        fileList.add(f);
                    }
                    if(fileList!=null && fileList.size()>0) {
                        fileMapper.insertBatch(fileList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(result==null) result = new Result(Result.ERROR, "保存图片异常");
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(Result.ERROR, "操作异常");
        }

        return new Result(Result.OK, "提交成功");
	}

    private String checkSkuSaleNum(String skuSaleNum, Sku sku) {
	    //把tb_num里面skuid下的所有状态改成1
        Number number = new Number();
        number.setSkuId(sku.getSkuId());
        number.setStatus(1);
        numberMapper.updateStatus(number);

	    String[] skuSaleNumbs = skuSaleNum.split("\\r?\\n");
	    if(skuSaleNumbs!=null && skuSaleNumbs.length>0){
            skuSaleNum = "";
            for (int i = 0; i < skuSaleNumbs.length; i++) {
                //验证号码可用性
                number = new Number();
                number.setNumResource(skuSaleNumbs[i]);
                if(numberMapper.checkNumberIsOk(number) > 0) {
                    skuSaleNum += skuSaleNumbs[i]+"\n";
                    //更新状态
                    number.setSkuId(sku.getGeneralId());
                    number.setStatus(2);
                    numberMapper.updateStatus(number);
                }else{

                }
            }
        }else return "";

	    if(skuSaleNum.length()!=0) skuSaleNum = skuSaleNum.substring(0, skuSaleNum.length()-1);
	    return skuSaleNum;
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
