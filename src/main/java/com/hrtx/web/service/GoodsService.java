package com.hrtx.web.service;

import com.fasterxml.jackson.databind.node.DecimalNode;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.advice.ServiceException;
import com.hrtx.config.advice.WarmException;
import com.hrtx.dto.Result;
import com.hrtx.dto.StorageInterfaceRequest;
import com.hrtx.global.*;
import com.hrtx.web.controller.BaseReturn;
import com.hrtx.web.dto.StorageInterfaceResponse;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import com.hrtx.web.pojo.Number;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.System;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class GoodsService {

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
    private NumberMapper numberMapper;
    @Autowired
    private NumMapper numMapper;
    @Autowired
    private ChannelMapper channelMapper;
    @Autowired
    private NumberPriceMapper numberPriceMapper;
    @Autowired
    private NumPriceMapper numPriceMapper;
    @Autowired
    private NumberService numberService;
	public Result pageGoods(Goods goods) {
		PageHelper.startPage(goods.startToPageNum(),goods.getLimit());
		goods.setgSellerId(SessionUtil.getUser().getCorpId());
		Page<Object> ob=this.goodsMapper.queryPageList(goods);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Goods findGoodsById(Integer id) {
		Goods goods = goodsMapper.findGoodsInfo(id);
		return goods;
	}

    public Result goodsEdit(Goods goods, HttpServletRequest request, MultipartFile[] files) {
        //商品主表操作
        List<Goods> list = new ArrayList<Goods>();
        String isSale = goods.getgIsSale();  //是否上架
        Sku sku = new Sku();
        SkuProperty dskuProperty = new SkuProperty();
        //sku json 串
        String skuPropertyJsonStr = request.getParameter("skuJson") == null ? "" : request.getParameter("skuJson");
        //验证参数
        Result resd = this.verify(sku,skuPropertyJsonStr);
        if(resd.getCode()==888){
            return new Result(Result.OTHER, resd.getData());
        }
        if (goods.getgId() != null && goods.getgId() > 0) { //修改
//            goodsMapper.goodsEdit(goods);
//            goods = goodsMapper.findGoodsInfo(goods.getgId());
        } else { //添加
            Corporation corporation = (Corporation) SessionUtil.getSession().getAttribute("corporation");
            goods.setgId(goodsMapper.getId());
            goods.setStatus(0);  //商品上架初始值0
            goods.setgSellerId(corporation.getId());
            goods.setgSellerName(corporation.getName());
            list.add(goods);

        }
        //富文本信息获取
        String kindeditorContent = request.getParameter("kindeditorContent");
        Utils.kindeditorWriter(kindeditorContent, goods.getgId()+".txt", SystemParam.get("kindedtiorDir"));
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
                    f.setFileId(fileMapper.getId());
                    f.setFileGroup("goodsPic");
                    result = BaseReturn.uploadFile(SystemParam.get("goodsPics")+goods.getgId()+java.io.File.separator, "jpg,png,gif", file, true, false);
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
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return result;
            }
        }
        if(list!=null && list.size()>0) goodsMapper.insertBatch(list);
        //sku ,numPrice 操作
        Result isRes = this.putaway(goods,skuPropertyJsonStr);



        return new Result(Result.OK, isRes.getData());
    }

    public Result putaway(Goods goods,String skuPropertyJsonStr){
        JSONArray skuPropertyJsonArr = JSONArray.fromObject(skuPropertyJsonStr);
        String ignoreKey = "undefined,skuId,skuTobPrice,skuTocPrice,skuIsNum,skuSaleNum,skuNum,skuGoodsType,skuRepoGoods,skuRepoGoodsName,skustatusText";
        String noticeStr="";
        if(!skuPropertyJsonArr.isEmpty()){
            List<SkuProperty> skuPropertyList = new ArrayList<SkuProperty>();
            List<Sku> skuList = new ArrayList<Sku>();
            String skuSaleNum = "";

            List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();

            for(int i=0; i<skuPropertyJsonArr.size(); i++){
                Map map= new HashMap();
                //sku表操作
                Sku sku = new Sku();
                sku.setgId(goods.getgId());
                //sku属性表操作
                JSONObject obj = (JSONObject) skuPropertyJsonArr.get(i);
                Iterator it = obj.keySet().iterator();
                String price = ((JSONObject) obj.get("skuTobPrice")).get("value")==null||((JSONObject) obj.get("skuTobPrice")).get("value").equals("null")||((JSONObject) obj.get("skuTobPrice")).get("value").equals("")?"": (String) ((JSONObject) obj.get("skuTobPrice")).get("value");
                sku.setSkuTobPrice(Double.parseDouble(price));
                String skuNum = ((JSONObject) obj.get("skuNum")).get("value")==null||((JSONObject) obj.get("skuNum")).get("value").equals("null")?"": ((JSONObject) obj.get("skuNum")).get("value").toString();
                sku.setSkuNum(Integer.parseInt(skuNum));
                skuSaleNum = ((JSONObject) obj.get("skuSaleNum")).get("value")==null||((JSONObject) obj.get("skuSaleNum")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuSaleNum")).get("value");
                sku.setSkuGoodsType(((JSONObject) obj.get("skuGoodsType")).get("value")==null||((JSONObject) obj.get("skuGoodsType")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuGoodsType")).get("value"));
                sku.setSkuId(skuMapper.getId());

                sku.setSkuSaleNum(skuSaleNum.split("★")[0].split("\n")[0]);
                sku.setSkuRepoGoods(((JSONObject) obj.get("skuRepoGoods")).get("value")==null||((JSONObject) obj.get("skuRepoGoods")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuRepoGoods")).get("value"));
                sku.setSkuRepoGoodsName(((JSONObject) obj.get("skuRepoGoodsName")).get("value")==null||((JSONObject) obj.get("skuRepoGoodsName")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuRepoGoodsName")).get("value"));
                sku.setStatus(0);
                sku.setIsDel(0);
                map.put("SkuId",sku.getSkuId());
                map.put("skuSaleNumbs",skuSaleNum);
                map.put("skuNum",Integer.parseInt(skuNum));
                map.put("companystock_id",sku.getSkuRepoGoods());
                map.put("skuGoodsType",sku.getSkuGoodsType());
                map.put("basePrice",sku.getSkuTobPrice());
                listMap.add(map);
                skuList.add(sku);

                skuPropertyList.clear();
                while (it.hasNext()){
                    SkuProperty skuProperty = new SkuProperty();
                    String key = (String) it.next();
                    JSONObject col = (JSONObject) obj.get(key);
                    if(ignoreKey.indexOf(key)!=-1) {
                        continue;
                    }
                    skuProperty.setgId(sku.getgId());
                    skuProperty.setSkuId(sku.getSkuId());
                    skuProperty.setSkupId(skuPropertyMapper.getId());
                    skuProperty.setSkupKey(key);
                    skuProperty.setSkupValue((String) (col.get("value")==null||col.get("value").equals("null")?"":col.get("value")));
                    skuProperty.setSeq(Integer.parseInt((String) ((col.get("seq").equals(""))?"0":col.get("seq"))));

                    skuPropertyList.add(skuProperty);
                }
                if(skuPropertyList!=null && skuPropertyList.size()>0) skuPropertyMapper.insertBatch(skuPropertyList);
            }
            if(skuList!=null && skuList.size()>0) skuMapper.insertBatch(skuList);

            List<Map<String,Object>> isGoodSkuMap = new ArrayList<Map<String,Object>>();
            for(int i=0; i<listMap.size(); i++){
                Map map= new HashMap();
                Result res;
                Map map1 =(Map) listMap.get(i);
                Long skuid =Long.parseLong( String.valueOf(map1.get("SkuId")));
                Sku sku = skuList.get(i);
                String skuSaleNumb = String.valueOf(map1.get("skuSaleNumbs"));
                int skuNum =Integer.valueOf(String.valueOf(map1.get("skuNum")));
                String skuRepoGoods =String.valueOf(map1.get("companystock_id"));
                String skuGoodsType =String.valueOf(map1.get("skuGoodsType"));
                double basePrice =Double.valueOf(String.valueOf(map1.get("basePrice")));

                sku.setStatus(1);
                sku.setStatusText("上架成功");



                map.put("SkuId",skuid);
                map.put("skuSaleNumbs",skuSaleNumb);
                map.put("skuNum",skuNum);
                map.put("skuRepoGoods",skuRepoGoods);
                map.put("skuGoodsType",skuGoodsType);
                map.put("basePrice",basePrice);
                isGoodSkuMap.add(map);
                skuMapper.updateSkuStatus(sku);

//                log.info("仓库接口调用结束");
//                if(!skuGoodsType.equals("3")){
//                    //调用仓储接口
//                    Map param = new HashMap();
//                    param.put("supply_id",skuid);//供货单编码(sku_id)
//                    param.put("type", "1");//处理类型1上架；2下架
//                    param.put("quantity", skuNum);//数量
//                    param.put("companystock_id", skuRepoGoods);//库存编码(skuRepoGoods)
//                    if(!"0".equals(param.get("quantity").toString())) {
//                        res = StorageApiCallUtil.storageApiCall(param, "HK0002");
//                        if (200 != (res.getCode())) {
//                            sku.setStatus(90);  //未知异常
//                            sku.setStatusText("上架调用仓库接口异常");
////                            return new Result(Result.ERROR, "第"+(i+1)+"行,库存验证失败");
////                            continue;
//                        } else {
//                            StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
//                            if (!"00000".equals(sir.getCode())) {
//                                sku.setStatus(91);
//                                sku.setStatusText(sir.getDesc());
////                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
////                                return new Result(Result.ERROR, "第"+(i+1)+"行,冻结库存失败\n"+sir.getDesc());
////                                continue;
//                            }else {
//                                sku.setStatus(1);
//                                sku.setStatusText("上架成功");
//                                map.put("SkuId",skuid);
//                                map.put("skuSaleNumbs",skuSaleNumb);
//                                map.put("skuNum",skuNum);
//                                map.put("skuRepoGoods",skuRepoGoods);
//                                map.put("skuGoodsType",skuGoodsType);
//                                map.put("basePrice",basePrice);
//                                isGoodSkuMap.add(map);
//                            }
//                        }
//                    }
//                }else{
//                    sku.setStatus(1);
//                    sku.setStatusText("上架成功");
//                    map.put("SkuId",skuid);
//                    map.put("skuSaleNumbs",skuSaleNumb);
//                    map.put("skuNum",skuNum);
//                    map.put("skuRepoGoods",skuRepoGoods);
//                    map.put("skuGoodsType",skuGoodsType);
//                    map.put("basePrice",basePrice);
//                    isGoodSkuMap.add(map);
//                }

            }
//            log.info("仓库接口调用结束");
            numberService.numberUpdate(isGoodSkuMap,goods);

            goods.setStatus(5);   //价格更新失败
            goodsMapper.updateGoodStatus(goods);
            numPriceMapper.matchNumPrice(goods.getgSellerId());
            List isWz = skuMapper.queryStatusList(goods.getgId(),"90,91");
            if(isWz.size()>0){
                goods.setStatus(2); //部分上架
                noticeStr="上架失败";
            }else {
                goods.setStatus(1); //全部上架
                noticeStr="上架成功";
            }
            goodsMapper.updateGoodStatus(goods);
        }
        return new Result(Result.OK,noticeStr);
    }

    /***
     * 验证上架的参数
     * @param sku
     * @param skuPropertyJsonStr
     * @return
     */
    public Result verify(Sku sku,String skuPropertyJsonStr){
        JSONArray skuPropertyJsonArr = JSONArray.fromObject(skuPropertyJsonStr);
        if(!skuPropertyJsonArr.isEmpty()){
            //验证基sku 基础信息
            Set<String> set = new HashSet<String>();
            for(int i=0; i<skuPropertyJsonArr.size(); i++){
                //sku属性表操作
                sku = new Sku();
                JSONObject obj = (JSONObject) skuPropertyJsonArr.get(i);
                Iterator it = obj.keySet().iterator();
                String price = ((JSONObject) obj.get("skuTobPrice")).get("value")==null
                        ||((JSONObject) obj.get("skuTobPrice")).get("value").equals("null")
                        ||((JSONObject) obj.get("skuTobPrice")).get("value").equals("")?"": (String) ((JSONObject) obj.get("skuTobPrice")).get("value");
                if("".equals(price)) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return new Result(Result.OTHER, "第"+(i+1)+"行2B价格为空");
                }else{
                    String pattern = "[1-9]\\d*.?\\d*|0.\\d*[1-9]\\d*";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(price);
                    if(!m.matches()) return new Result(Result.ERROR, "第" + (i + 1) + "行2B价格格式错误,请输入两位正小数");
                }
                String Num = ((JSONObject) obj.get("skuNum")).get("value")==null||((JSONObject) obj.get("skuNum")).get("value").equals("null")?"": ((JSONObject) obj.get("skuNum")).get("value").toString();
                if("".equals(Num)) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return new Result(Result.OTHER, "第" + (i + 1) + "行数量为空");
                }else{
                    String pattern = "[1-9]\\d*";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(Num);
                    if(!m.matches()) return new Result(Result.OTHER, "第" + (i + 1) + "行数量格式错误,请输入正整数");
                }
                sku.setSkuId(skuMapper.getId());
                String skuSaleNum = ((JSONObject) obj.get("skuSaleNum")).get("value")==null||((JSONObject) obj.get("skuSaleNum")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuSaleNum")).get("value");
                String skuGoodsType =((JSONObject) obj.get("skuGoodsType")).get("value")==null||((JSONObject) obj.get("skuGoodsType")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuGoodsType")).get("value");
                //白卡不验证号码
                if(!"1".equals(skuGoodsType)) {
                    String repeatNum = getRepeatNum(skuSaleNum);
                    if(repeatNum!=null && repeatNum.length()>0) return new Result(Result.OTHER, "以下号码重复\n"+repeatNum);
                    String[] nums = skuSaleNum.split("\n");
                    for (String a :nums){
                        if(set.contains(a)){
                            return new Result(Result.OTHER, "该上架商品存在重复号码\n"+a);
                        }
                        set.add(a);
                    }
                    skuSaleNum = checkSkuSaleNum(skuSaleNum);
                    sku.setSkuNum(((JSONObject) obj.get("skuNum")).get("value")==null||((JSONObject) obj.get("skuNum")).get("value").equals("null")?0: Integer.parseInt(((JSONObject) obj.get("skuNum")).get("value").toString()));
                    String[] okSkuNum = new String[] {};
                    if(skuSaleNum.indexOf("★")!=0){
                        okSkuNum = skuSaleNum.split("★")[0].split("\\r?\\n");
                    }
                    int okCount = okSkuNum.length;
                    if (okSkuNum.length !=0 && StringUtils.isBlank(okSkuNum[0]) && okSkuNum.length == 1) okCount = 0;
                    //有错误号码
                    if (skuSaleNum.split("★").length > 1) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return new Result(Result.OTHER, "第" + (i + 1) + "行,以下号码不可以上架,请重新确认\n" + skuSaleNum.split("★")[1]);
                    }
                    if (sku.getSkuNum() != okCount) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return new Result(Result.OTHER, "第" + (i + 1) + "行,填写数量和允许上架号码量(" + okCount + ")不一致");
                    }
                }
            }
        }
        return new Result(Result.OK, "参数验证成功");
    }
//	public Result goodsEdit(Goods goods, HttpServletRequest request, MultipartFile[] files) {
//        try {
//            String ignoreKey = "undefined,skuId,skuTobPrice,skuTocPrice,skuIsNum,skuSaleNum,skuNum,skuGoodsType,skuRepoGoods,skuRepoGoodsName";
//            //商品主表操作
//            List<Goods> list = new ArrayList<Goods>();
//            String isSale = goods.getgIsSale();
//            if (goods.getgId() != null && goods.getgId() > 0) {
//                //上架中的竞拍商品只允许修改个别字段
//                if("1".equals(goods.getgIsAuc()) && "1".equals(isSale)){
//
//                }
//                goodsMapper.goodsEdit(goods);
//                goods = goodsMapper.findGoodsInfo(goods.getgId());
//            } else {
////                User user = SessionUtil.getUser();
//                Corporation corporation = (Corporation) SessionUtil.getSession().getAttribute("corporation");
////                goods.setgId(goods.getGeneralId());
//                goods.setgSellerId(corporation.getId());
//                goods.setgSellerName(corporation.getName());
//                list.add(goods);
//            }
//            //商品子表操作
//            Sku sku = new Sku();
//            Sku dsku = new Sku();
//            dsku.setgId(goods.getgId());
//
//            SkuProperty skuProperty = new SkuProperty();
//            SkuProperty dskuProperty = new SkuProperty();
//            dskuProperty.setgId(dsku.getgId());
//
//
//            String skuPropertyJsonStr = request.getParameter("skuJson") == null ? "" : request.getParameter("skuJson");
//            JSONArray skuPropertyJsonArr = JSONArray.fromObject(skuPropertyJsonStr);
//            //删除之前先验证
//            if(!skuPropertyJsonArr.isEmpty()){
//                List<SkuProperty> skuPropertyList = new ArrayList<SkuProperty>();
//                List<Sku> skuList = new ArrayList<Sku>();
//                String skuSaleNum = "";
//                Set<String> set = new HashSet<String>();
//                for(int i=0; i<skuPropertyJsonArr.size(); i++){
//                    //sku表操作
//                    sku = new Sku();
//                    sku.setgId(goods.getgId());
//
//                    //sku属性表操作
//                    JSONObject obj = (JSONObject) skuPropertyJsonArr.get(i);
//                    Iterator it = obj.keySet().iterator();
//                    String price = ((JSONObject) obj.get("skuTobPrice")).get("value")==null||((JSONObject) obj.get("skuTobPrice")).get("value").equals("null")||((JSONObject) obj.get("skuTobPrice")).get("value").equals("")?"": (String) ((JSONObject) obj.get("skuTobPrice")).get("value");
//                    if("".equals(price)) {
//                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                        return new Result(Result.ERROR, "第"+(i+1)+"行2B价格为空");
//                    }else{
//                        String pattern = "[1-9]\\d*.?\\d*|0.\\d*[1-9]\\d*";
//                        Pattern r = Pattern.compile(pattern);
//                        Matcher m = r.matcher(price);
//                        if(!m.matches()) return new Result(Result.ERROR, "第" + (i + 1) + "行2B价格格式错误,请输入两位正小数");
//                    }
//                    sku.setSkuTobPrice(Double.parseDouble(price));
//                    price = ((JSONObject) obj.get("skuNum")).get("value")==null||((JSONObject) obj.get("skuNum")).get("value").equals("null")?"": ((JSONObject) obj.get("skuNum")).get("value").toString();
//                    if("".equals(price)) {
//                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                        return new Result(Result.ERROR, "第" + (i + 1) + "行数量为空");
//                    }else{
//                        String pattern = "[1-9]\\d*";
//                        Pattern r = Pattern.compile(pattern);
//                        Matcher m = r.matcher(price);
//                        if(!m.matches()) return new Result(Result.ERROR, "第" + (i + 1) + "行数量格式错误,请输入正整数");
//                    }
//
//                    skuSaleNum = ((JSONObject) obj.get("skuSaleNum")).get("value")==null||((JSONObject) obj.get("skuSaleNum")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuSaleNum")).get("value");
//                    sku.setSkuGoodsType(((JSONObject) obj.get("skuGoodsType")).get("value")==null||((JSONObject) obj.get("skuGoodsType")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuGoodsType")).get("value"));
//                    String tskuId =  String.valueOf(((JSONObject) obj.get("skuId")).get("value"));
//                    sku.setSkuId(Long.parseLong(tskuId==null||tskuId.equals("null")||tskuId.equals("")?String.valueOf(sku.getGeneralId()): tskuId));
//                    sku.setSkuNum(((JSONObject) obj.get("skuNum")).get("value")==null||((JSONObject) obj.get("skuNum")).get("value").equals("null")?0: Integer.parseInt(((JSONObject) obj.get("skuNum")).get("value").toString()));
//
//                    //白卡不验证号码
//                    if(!"1".equals(sku.getSkuGoodsType())) {
//                        String repeatNum = getRepeatNum(skuSaleNum);
//                        if(repeatNum!=null && repeatNum.length()>0) return new Result(Result.ERROR, "以下号码重复\n"+repeatNum);
//
//                        String[] nums = skuSaleNum.split("\n");
//                        for (String a :nums){
//                            if(set.contains(a)){
//                                return new Result(Result.ERROR, "该上架商品存在重复号码\n"+a);
//                            }
//                            set.add(a);
//                        }
//                        skuSaleNum = checkSkuSaleNumUpdateStatus(skuSaleNum, "".equals(tskuId)?sku.getSkuId():Long.parseLong(tskuId));
//    //                    sku.setSkuRepoGoods(((JSONObject) obj.get("skuRepoGoods")).get("value")==null||((JSONObject) obj.get("skuRepoGoods")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuRepoGoods")).get("value"));
//    //                    sku.setSkuRepoGoodsName(((JSONObject) obj.get("skuRepoGoodsName")).get("value")==null||((JSONObject) obj.get("skuRepoGoodsName")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuRepoGoodsName")).get("value"));
//                        String[] okSkuNum = new String[] {};
//                        if(skuSaleNum.indexOf("★")!=0){
//                            okSkuNum = skuSaleNum.split("★")[0].split("\\r?\\n");
//                        }
//                        int okCount = okSkuNum.length;
//                        if (okSkuNum.length !=0 && StringUtils.isBlank(okSkuNum[0]) && okSkuNum.length == 1) okCount = 0;
//                        //有错误号码
//                        if (skuSaleNum.split("★").length > 1) {
//                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                            return new Result(Result.ERROR, "第" + (i + 1) + "行,以下号码不符合,请重新确认\n" + skuSaleNum.split("★")[1]);
//                        }
//                        if (sku.getSkuNum() != okCount) {
//                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                            return new Result(Result.ERROR, "第" + (i + 1) + "行,填写数量和允许上架号码量(" + okCount + ")不一致");
//                        }
//                    }
//                }
//            }
//            if(list!=null && list.size()>0) goodsMapper.insertBatch(list);
//            //验证通过之后删除数据,重新写入
////            skuMapper.deleteSkuByGid(dsku);
//            skuPropertyMapper.deleteSkuPropertyByGid(dskuProperty);
//            if(!skuPropertyJsonArr.isEmpty()){
//                List<SkuProperty> skuPropertyList = new ArrayList<SkuProperty>();
//                List<Sku> skuList = new ArrayList<Sku>();
//                String skuSaleNum = "";
//                for(int i=0; i<skuPropertyJsonArr.size(); i++){
//                    //sku表操作
//                    sku = new Sku();
//                    sku.setgId(goods.getgId());
//
//                    //sku属性表操作
//                    JSONObject obj = (JSONObject) skuPropertyJsonArr.get(i);
//                    Iterator it = obj.keySet().iterator();
//
//                    //,skuTobPrice,skuTocPrice,skuIsNum,skuSaleNum,skuGoodsType,skuRepoGoods,
//                    String price = ((JSONObject) obj.get("skuTobPrice")).get("value")==null||((JSONObject) obj.get("skuTobPrice")).get("value").equals("null")||((JSONObject) obj.get("skuTobPrice")).get("value").equals("")?"": (String) ((JSONObject) obj.get("skuTobPrice")).get("value");
//                    sku.setSkuTobPrice(Double.parseDouble(price));
//
////                    sku.setSkuIsNum(((JSONObject) obj.get("skuIsNum")).get("value")==null||((JSONObject) obj.get("skuIsNum")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuIsNum")).get("value"));
//                    price = ((JSONObject) obj.get("skuNum")).get("value")==null||((JSONObject) obj.get("skuNum")).get("value").equals("null")?"": ((JSONObject) obj.get("skuNum")).get("value").toString();
//                    sku.setSkuNum(Integer.parseInt(price));
//                    skuSaleNum = ((JSONObject) obj.get("skuSaleNum")).get("value")==null||((JSONObject) obj.get("skuSaleNum")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuSaleNum")).get("value");
////                    验证号码可用性之前赋值旧的skuId,便于tb_num表复原状态
//                    sku.setSkuGoodsType(((JSONObject) obj.get("skuGoodsType")).get("value")==null||((JSONObject) obj.get("skuGoodsType")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuGoodsType")).get("value"));
//                    Integer tskuId = Integer.parseInt(String.valueOf(((JSONObject) obj.get("skuId")).get("value")));
//                    sku.setSkuId(tskuId==0||tskuId==null ?skuMapper.getId(): tskuId);
//
////                    skuSaleNum = checkSkuSaleNum(skuSaleNum, sku, true, Long.parseLong(tskuId));
//                    //白卡不验证号码
//                    if(!"1".equals(sku.getSkuGoodsType())) {
//                        skuSaleNum = checkSkuSaleNumUpdateStatus(skuSaleNum,  "".equals(tskuId) ? sku.getSkuId() : tskuId);
//                        String[] okSkuNum = skuSaleNum.split("★")[0].split("\\r?\\n");
//                        int okCount = okSkuNum.length;
//                        if (okSkuNum[0] == "" && okSkuNum.length == 1) okCount = 0;
//                        sku.setSkuNum(okCount);
//                        //统一更新号码状态
//                        for (String okNum : okSkuNum) {
//                            Number okNumber = new Number();
//                            okNumber.setSkuId(sku.getSkuId());
//                            okNumber.setStatus(2);
//                            okNumber.setNumResource(okNum);
//                            //竞拍商品把时间往tb_num写入
//                            if("1".equals(goods.getgIsAuc())){
//                                okNumber.setStartTime(goods.getgStartTime());
//                                okNumber.setEndTime(goods.getgEndTime());
//                            }
//                            numberMapper.updateStatus(okNumber, false);
//                        }
//                    }else {//是白卡就把该skuid下的所有号码状态改成1,清空tb_num的sku_id
//                        checkSkuSaleNumUpdateStatus("","".equals(tskuId) ? sku.getSkuId() : Long.parseLong(tskuId));
//                    }
//
////                    sku.setSkuId(sku.getGeneralId());
//                    sku.setSkuSaleNum(skuSaleNum.split("★")[0].split("\n")[0]);
//                    sku.setSkuRepoGoods(((JSONObject) obj.get("skuRepoGoods")).get("value")==null||((JSONObject) obj.get("skuRepoGoods")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuRepoGoods")).get("value"));
//                    sku.setSkuRepoGoodsName(((JSONObject) obj.get("skuRepoGoodsName")).get("value")==null||((JSONObject) obj.get("skuRepoGoodsName")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuRepoGoodsName")).get("value"));
//
//                    //调用仓储接口
//                    Map param = new HashMap();
//                    param.put("supply_id", sku.getSkuId());//供货单编码(sku_id)
//                    //获取目前sku信息
//                    Sku nowSku = skuMapper.getSkuBySkuid(sku.getSkuId());
//                    Result res;
//                    if(!sku.getSkuGoodsType().equals("3")){
//                        if(goods.getGeneralId()!=goods.getgId() && "1".equals(isSale)) {
//                            //先解冻现有库存
//                            param.put("type", "2");//处理类型1上架；2下架
//                            param.put("quantity", nowSku == null ? 0 : nowSku.getSkuNum());//数量
//                            param.put("companystock_id", nowSku.getSkuRepoGoods());//库存编码(skuRepoGoods)
//                            if(!"0".equals(param.get("quantity").toString())) {
//                                res = StorageApiCallUtil.storageApiCall(param, "HK0002");
//                                if (200 != (res.getCode())) {
//                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                                    return new Result(Result.ERROR, "第"+(i+1)+"行,库存验证失败");
//                                } else {
//                                    StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
//                                    if (!"00000".equals(sir.getCode())) {
//                                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                                        return new Result(Result.ERROR, "第"+(i+1)+"行,解冻库存失败\n"+sir.getDesc());
//                                    }
//                                }
//                            }
//                        }
//                        //再冻结新库存
//                        param.put("type", "1");//处理类型1上架；2下架
//                        param.put("quantity", sku.getSkuNum());//数量
//                        param.put("companystock_id", sku.getSkuRepoGoods());//库存编码(skuRepoGoods)
//                        if(!"0".equals(param.get("quantity").toString())) {
//                            res = StorageApiCallUtil.storageApiCall(param, "HK0002");
//                            if (200 != (res.getCode())) {
//                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                                return new Result(Result.ERROR, "第"+(i+1)+"行,库存验证失败");
//                            } else {
//                                StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
//                                if (!"00000".equals(sir.getCode())) {
//                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                                    return new Result(Result.ERROR, "第"+(i+1)+"行,冻结库存失败\n"+sir.getDesc());
//                                }
//                            }
//                        }
//                    }
//                    //sku属性表操作end
//                    //判断是否存在,存在就update,否则加到list中insert
//                    if(!"".equals(tskuId)&&skuMapper.getSkuBySkuid(Long.parseLong(tskuId))!=null) {
//                        sku.setSkuId(Long.parseLong(tskuId));
//                        skuMapper.updateSku(sku);
//                    }
//                    else skuList.add(sku);
//
//                    skuPropertyList.clear();
//                    while (it.hasNext()){
//                        String key = (String) it.next();
//                        JSONObject col = (JSONObject) obj.get(key);
//                        if(ignoreKey.indexOf(key)!=-1) {
//                            continue;
//                        }
//                        skuProperty = new SkuProperty();
//                        skuProperty.setgId(sku.getgId());
//                        skuProperty.setSkuId(sku.getSkuId());
//                        skuProperty.setSkupId(skuProperty.getGeneralId());
//                        skuProperty.setSkupKey(key);
//                        skuProperty.setSkupValue((String) (col.get("value")==null||col.get("value").equals("null")?"":col.get("value")));
//                        skuProperty.setSeq(Integer.parseInt((String) ((col.get("seq").equals(""))?"0":col.get("seq"))));
//
//                        skuPropertyList.add(skuProperty);
//                    }
//                    if(skuPropertyList!=null && skuPropertyList.size()>0) skuPropertyMapper.insertBatch(skuPropertyList);
//                }
//                if(skuList!=null && skuList.size()>0) skuMapper.insertBatch(skuList);
//                //删除前台传过来的sku
//                String delSkus = request.getParameter("delSkus");
//                //调用仓储解冻库存
//                if(!StringUtils.isBlank(delSkus)){
//                    //上架中的才需要解冻库存
//                    if("1".equals(isSale)) {
//                        String[] delskus = delSkus.split(",");
//                        for (String delSku : delskus) {
//                            if (!StringUtils.isBlank(delSku)) {
//                                //获取目前sku信息
//                                Sku s = skuMapper.getSkuBySkuid(Long.parseLong(delSku));
//                                if (s != null) {
//                                    if(!s.getSkuGoodsType().equals("3")){
//                                        //调用仓储接口
//                                        Map param = new HashMap();
//                                        param.put("supply_id", s.getSkuId());//供货单编码(sku_id)
//                                        Result res;
//                                        //解冻现有库存
//                                        param.put("type", "2");//处理类型1上架；2下架
//                                        param.put("quantity", s == null ? 0 : s.getSkuNum());//数量
//                                        param.put("companystock_id", s.getSkuRepoGoods());//库存编码(skuRepoGoods)
//                                        if (!"0".equals(param.get("quantity").toString())) {
//                                            res = StorageApiCallUtil.storageApiCall(param, "HK0002");
//                                            if (200 != (res.getCode())) {
//                                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                                                return new Result(Result.ERROR, "解冻库存失败");
//                                            } else {
//                                                StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
//                                                if (!"00000".equals(sir.getCode())) {
//                                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                                                    return new Result(Result.ERROR, "解冻库存失败\n" + sir.getDesc());
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                                // by zdh 20180712
//                                //更新删除的tb_num 状态
//                                numberMapper.updateDelStatus(s.getSkuId());
//                            }
//                        }
//                    }
//                    //删除sku
//                    delSkus = StringUtils.isBlank(delSkus) ? "" : "'"+delSkus.substring(0, delSkus.length() - 1).replaceAll(",", "','")+"'";
//                    skuMapper.deleteSkuBySkuids(delSkus);
//                }
//
//                //富文本信息获取
//                String kindeditorContent = request.getParameter("kindeditorContent");
//                Utils.kindeditorWriter(kindeditorContent, goods.getgId()+".txt", SystemParam.get("kindedtiorDir"));
//
//            }
//            //图片保存
//            Result result = null;
//            String picSeqs = request.getParameter("picSeqs")==null?"":request.getParameter("picSeqs");
//            String delPicSeqs = request.getParameter("delPicSeqs")==null?"":request.getParameter("delPicSeqs");
//            if(!picSeqs.equals("")){
//                fileMapper.deleteFilesByRefid(goods.getgId().toString(), picSeqs.equals("")?"":picSeqs.substring(0, picSeqs.length()-1));
//            }
//            if(!delPicSeqs.equals("")){
//                fileMapper.deleteFilesByRefid(goods.getgId().toString(), delPicSeqs.equals("")?"":delPicSeqs.substring(0, delPicSeqs.length()-1));
//            }
//            if(files!=null && files.length>0){
//                try {
//                    List<File> fileList = new ArrayList<File>();
//                    for (int i=0; i<files.length; i++) {
//                        MultipartFile file = files[i];
//                        File f = new File();
//                        f.setFileId(f.getGeneralId());
//                        f.setFileGroup("goodsPic");
//                        result = BaseReturn.uploadFile(SystemParam.get("goodsPics")+goods.getgId()+java.io.File.separator, "jpg,png,gif", file, true, false);
//                        f.setFileName(((Map)result.getData()).get("sourceServerFileName").toString());
//                        f.setRefId(goods.getgId());
//                        f.setSeq(Integer.parseInt(picSeqs.replaceAll("\"","").split(",")[i]));
//                        fileList.add(f);
//                    }
//                    if(fileList!=null && fileList.size()>0) {
//                        fileMapper.insertBatch(fileList);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    if(result==null) result = new Result(Result.ERROR, "保存图片异常");
//                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                    return result;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Result(Result.ERROR, "操作异常");
//        }
//
//        return new Result(Result.OK, "提交成功");
//	}

    private String getRepeatNum(String skuSaleNum) {
	    if(skuSaleNum==null || skuSaleNum.length() <=0) return "";
        String repeatNum = "";
	    String[] nums = skuSaleNum.split("\\r?\\n");
        Set set = new HashSet();
        int psize = set.size();
        for (String n : nums) {
            if(StringUtils.isBlank(n)) continue;
            set.add(n);
            if(set.size()<= psize){
                repeatNum += n + "\n";
            }
            psize = set.size();
        }
        return repeatNum;
    }

    /**
     * 验证可以上架的号码 status in 1,2
     * @param skuSaleNum
     * by zdh
     * @return
     */
    private String checkSkuSaleNum(String skuSaleNum) {
        String errorNum = "";
        Number number = new Number();
        String[] skuSaleNumbs = skuSaleNum.split("\\r?\\n");
        if(skuSaleNumbs!=null && skuSaleNumbs.length>0){
            skuSaleNum = "";
            for (int i = 0; i < skuSaleNumbs.length; i++) {
                if(StringUtils.isBlank(skuSaleNumbs[i])) continue;
                //验证号码可用性
                number = new Number();
                number.setNumResource(skuSaleNumbs[i].trim());
                number.setSellerId(SessionUtil.getUser().getCorpId());
                if(numberMapper.checkNumberIsOkStatus(number) > 0) {
                    skuSaleNum += skuSaleNumbs[i].trim()+"\n";
                }else{
                    errorNum += skuSaleNumbs[i].trim() + "\n";
                }
            }
        }else return "";

        if(skuSaleNum.length()!=0) skuSaleNum = skuSaleNum.substring(0, skuSaleNum.length()-1);
        return skuSaleNum+"★"+errorNum;
    }

    private String checkSkuSaleNumUpdateStatus(String skuSaleNum, Integer skuid) {
	    //把tb_num里面skuid下的所有状态改成1
        String errorNum = "";
        Number number = new Number();
        number.setSkuId(skuid);
        number.setStatus(1);
        numberMapper.updateStatus(number, true);

	    String[] skuSaleNumbs = skuSaleNum.split("\\r?\\n");
	    if(skuSaleNumbs!=null && skuSaleNumbs.length>0){
            skuSaleNum = "";
            for (int i = 0; i < skuSaleNumbs.length; i++) {
                if(StringUtils.isBlank(skuSaleNumbs[i])) continue;
                if(numberMapper.checkNumberIsOk(number) > 0) {
                    skuSaleNum += skuSaleNumbs[i].trim()+"\n";
                }else{
                    errorNum += skuSaleNumbs[i].trim() + "\n";
                }
            }
        }else return "";

	    if(skuSaleNum.length()!=0) skuSaleNum = skuSaleNum.substring(0, skuSaleNum.length()-1);
	    return skuSaleNum+"★"+errorNum;
    }

    public Result goodsAllUnsale(HttpServletRequest request){
        List<Sku> skuList = skuMapper.querySkuList("1,90,92,93");
        List<String> ress = new ArrayList<>();
        for(Sku s : skuList){
            if(!"3".equals(s.getSkuGoodsType())){
                Map param = new HashMap();
                param.put("supply_id", s.getSkuId());//供货单编码(sku_id)
                param.put("companystock_id", s.getSkuRepoGoods());//库存编码(skuRepoGoods)
                param.put("type", "2");//处理类型1上架；2下架
                param.put("quantity", s.getSkuNum());//数量
                if(s.getSkuNum()!=0) {
                    Result result = StorageApiCallUtil.storageApiCall(param, "HK0002");
                    ress.add("skuid["+s.getSkuId()+"]下架结果"+JSONObject.fromObject(result).toString()+"<br/>");
                }
            }
        }

        return new Result(Result.OK, ress);
    }
    public Result goodsUnsale(Goods goods, HttpServletRequest request){
        String noticeStr="";
        List<Sku> skuList = skuMapper.queryStatusList(goods.getgId(),"1,90,92,93");
        //批量验证号码
        for(Sku s : skuList){
            Integer skuIds =s.getSkuId();
            Map map = numMapper.queryNumCountByskuid(skuIds,Constants.NUM_STATUS_2.getIntKey()+","+Constants.NUM_STATUS_8.getIntKey()+","+Constants.NUM_STATUS_10.getIntKey());
            int counts = NumberUtils.toInt(String.valueOf(map.get("count")));
            if(!s.getSkuGoodsType().equals("1")){//上架未知异常不验证号码
                if(s.getStatus()!=90){
                    if(counts!=s.getSkuNum()) throw new WarmException("上架中的号码数量和销售中的号码数量不一致");
                }
            }
        }
        //批量验证仓库库存
        Result res;
        List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
        for(Sku s : skuList){
//            Map param = new HashMap();
            Map map= new HashMap();
//            if(!"3".equals(s.getSkuGoodsType())){
//                param.put("supply_id", s.getSkuId());//供货单编码(sku_id)
//                param.put("companystock_id", s.getSkuRepoGoods());//库存编码(skuRepoGoods)
//                param.put("type", "2");//处理类型1上架；2下架
//                param.put("quantity", s.getSkuNum());//数量
//                if(s.getSkuNum()!=0) {
//                    res = StorageApiCallUtil.storageApiCall(param, "HK0002");
//                    if (200 != (res.getCode())) {
//                        if(s.getStatus()!=90){
//                            s.setStatus(92);  //未知异常
//                            s.setStatusText("下架调用仓库接口异常");
//                        }
//                    } else {
//                        StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
//                        if (!"00000".equals(sir.getCode())) {
//                            if(s.getStatus()==90){
//                                if("F0002".equals(sir.getCode())){  //单据不存在
//                                    s.setStatus(2);
//                                    s.setStatusText("下架成功");
//                                }
//                            }else{
//                                s.setStatus(93);
//                                s.setStatusText(sir.getDesc());
//                            }
//                        }else {
//                            s.setStatus(2);  //下架成功
//                            s.setStatusText("下架成功");
//                            map.put("SkuId",s.getSkuId());
//                            mapList.add(map);
//                        }
//                    }
//                }
//            }else {
//                s.setStatus(2);
//                s.setStatusText("");
//                map.put("SkuId",s.getSkuId());
//                mapList.add(map);
//            }
            s.setStatus(2);  //下架成功
            s.setStatusText("下架成功");
            map.put("SkuId",s.getSkuId());
            mapList.add(map);
            skuMapper.updateSkuStatus(s);
        }
        for(int i=0; i<mapList.size(); i++){
            Map map1 =(Map) mapList.get(i);
            Integer skuid =Integer.parseInt( String.valueOf(map1.get("SkuId")));

            Map map = numMapper.queryNumCountByskuid(skuid,"2");
            int counts = NumberUtils.toInt(String.valueOf(map.get("count")));
            Sku sku = new Sku();
            sku.setSkuId(skuid);
            Sku ss = skuMapper.selectByPrimaryKey(sku);

            if(!"1".equals(ss.getSkuGoodsType())){
                if(ss.getStatus()==2){
                    Number number = new Number();
                    number.setSkuId(ss.getSkuId());
                    number.setStatus(1);
                    numberMapper.updateStatus(number, true);
                }
            }
//            ss.setSkuNum(ss.getSkuNum()-counts);
            skuMapper.updateSkuNumDown(ss.getSkuId(),counts);
            numberPriceMapper.updateNumberPrice(ss.getSkuId());
        }

        List isWz = skuMapper.queryStatusList(goods.getgId(),"90,92,93");
        Goods d = goodsMapper.selectByPrimaryKey(goods);
        if(isWz.size()>0){
            d.setStatus(4); //部分下架
            noticeStr="下架失败";
        }else {
            d.setStatus(3); //全部下架
            noticeStr="下架成功";
        }
        goodsMapper.updateGoodStatus(d);
        goodsMapper.goodsUnsale(goods);
        return new Result(Result.OK, noticeStr);
    }

//    public Result goodsUnsale(Goods goods, HttpServletRequest request) {
//        Result res = new Result(Result.ERROR, "请求异常");
////        List lista = skuMapper.findNumStatus(goods.getgId());
////        if(lista.size()>0)  return new Result(Result.ERROR, "该上架商品中有部分号码状态不是销售中，不能下架");
//        try {
//            List<Sku> skuList = skuMapper.findSkuInfo(goods.getgId());
//            for(Sku s : skuList){
//                Map param = new HashMap();
//                Integer skuIds =s.getSkuId();
//                Map map = numMapper.queryNumCountByskuid(skuIds,"2");
//                int counts = NumberUtils.toInt(String.valueOf(map.get("count")));
//
//                if(s.getSkuGoodsType().equals("1")){
//                    param.put("supply_id", s.getSkuId());//供货单编码(sku_id)
//                    param.put("companystock_id", s.getSkuRepoGoods());//库存编码(skuRepoGoods)
//                    param.put("type", "2");//处理类型1上架；2下架
//                    param.put("quantity", s.getSkuNum());//数量
//                    if(s.getSkuNum()!=0) {
//                        res = StorageApiCallUtil.storageApiCall(param, "HK0002");
//                        if (200 != (res.getCode())) {
//                            throw new ServiceException("库存验证失败");
//                        } else {
//                            StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
//                            if (!"00000".equals(sir.getCode())) {
//                                throw new ServiceException("库存验证失败");
//                            }
//                        }
//                    }
//                }else if(s.getSkuGoodsType().equals("3")){
//                    if(counts!=s.getSkuNum()) throw new WarmException("上架的号码数量和销售中的号码数量不一致");
//                    Number number = new Number();
//                    number.setSkuId(s.getSkuId());
//                    number.setStatus(1);
//                    numberMapper.updateStatus(number, true);
//                }else {
//                    if(counts!=s.getSkuNum()) throw new WarmException("上架的号码数量和销售中的号码数量不一致");
//                    param.put("supply_id", s.getSkuId());//供货单编码(sku_id)
//                    param.put("companystock_id", s.getSkuRepoGoods());//库存编码(skuRepoGoods)
//                    param.put("type", "2");//处理类型1上架；2下架
//                    param.put("quantity", s.getSkuNum());//数量
//                    if(s.getSkuNum()!=0) {
//                        res = StorageApiCallUtil.storageApiCall(param, "HK0002");
//                        if (200 != (res.getCode())) {
//                            throw new ServiceException("库存验证失败");
//                        } else {
//                            StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
//                            if (!"00000".equals(sir.getCode())) {
//                                throw new ServiceException("库存验证失败");
//                            }else{
//                                //成功之后吧上架的号码状态还原成1
//                                Number number = new Number();
//                                number.setSkuId(s.getSkuId());
//                                number.setStatus(1);
//                                numberMapper.updateStatus(number, true);
//                            }
//                        }
//                    }
//                }
//                Sku nowSku = skuMapper.getSkuBySkuid(skuIds);
////                nowSku.setSkuNum(Integer.parseInt((String.valueOf(nowSku.getSkuNum())))-s.getSkuNum());//修改sku数量
//                skuMapper.updateSkuNumDown(nowSku.getSkuId(),s.getSkuNum());//修改sku数量
//            }
//
//        } catch (WarmException e) {
//            throw e;
//        }catch (ServiceException e) {
//            throw e;
//        }catch (Exception e) {
//            throw new ServiceException("下架库存异常");
//        }
//        goodsMapper.goodsUnsale(goods);
//        res = new Result(Result.OK, "下架成功");
//        return res;
//    }

    public Result repoGoods(HttpServletRequest request) {
        Result res = new Result(Result.ERROR, "请求异常");
        try {

            Map param = new HashMap();
            Corporation corporation = (Corporation) SessionUtil.getSession().getAttribute("corporation");
            param.put("storage_id", corporation.getStorageId());
            param.put("company_id", corporation.getCompanyId());
            res = StorageApiCallUtil.storageApiCall(param, "HK0001");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean checkGnameIsExist(Goods goods) {
	    int n = goodsMapper.checkGnameIsExist(goods);
	    return n>0?true:false;
    }

    public String getKindeditorContent(Goods goods) throws IOException {
	    return Utils.kindeditorReader(goods.getgId() + ".txt", SystemParam.get("kindedtiorDir"));
    }

    /***
     * 判断是否过期的上架商品
     */
//    @Scheduled(fixedRate=3000) update by zjc 2018.12.20  统一关闭应用定时器，由外部调用
    public void goodsTimer(){
        if(!"true".equals(SystemParam.get("goods_timer"))) return;
        log.info("开始执行判断商品是否过期定时器");
        List<Goods> list = goodsMapper.findGoodsIsSale();
        if(list.size()==0){
//            log.info(String.format("暂无过期的上架商品"));return;
        }else {
            for(Goods g : list){
                try {
                    log.info("下架商品GoodsID:"+g.getgId());
                    this.goodsUnsale(g,null);
                }catch (Exception e) {
                    log.error("未知异常", e);
                }
            }
        }

    }

    public Result ByNumToUnShelve(String num,String skuid){
        if(num==null || skuid==null) return new Result(Result.ERROR, "参数有误");
        List list = goodsMapper.findNumStatus(num,skuid);
        if(list.size() ==0 ) return new Result(Result.ERROR, "该号码不允许操作");
        if(list.size() >1 ) return new Result(Result.ERROR, "该号码存在多条以上的记录，请核实");
        Map map = (Map) list.get(0);
        int isauc = NumberUtils.toInt(String.valueOf(map.get("g_is_auc")));
        int skuGoodsType =  NumberUtils.toInt(String.valueOf(map.get("sku_goods_type")));
        String num_id =String.valueOf( map.get("num_id"));  //tb_num.id

        Map param = new HashMap();
        if(isauc ==1){//竞拍
            //更新tb_num 表 skuid,start_time,end_time,status
            goodsMapper.updateNumStatus(num_id,skuid,num);
            //更新 tb_sku 表 sku_num 数量
            int skuids = NumberUtils.toInt(skuid);
            Sku nowSku = skuMapper.getSkuBySkuid(skuids);
            int connt = 1;
//            nowSku.setSkuNum(Integer.parseInt((String.valueOf(nowSku.getSkuNum())))-1);//修改sku数量
            skuMapper.updateSkuNumDown(nowSku.getSkuId(),connt);
            //调用仓储接口
            param.put("supply_id", skuids);//供货单编码(sku_id)
            Result res;
            //再冻结新库存
            param.put("type", "2");//处理类型1上架；2下架
            param.put("quantity", 1);//数量
            param.put("companystock_id", nowSku.getSkuRepoGoods());//库存编码(skuRepoGoods)
            if(!"0".equals(param.get("quantity").toString())) {
                res = StorageApiCallUtil.storageApiCall(param, "HK0002");
                if(res.getCode()!=200){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return new Result(Result.ERROR, "库存验证失败");
                }else {
                    StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
                    if (!"00000".equals(sir.getCode())) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return new Result(Result.ERROR, "冻结库存失败\n"+sir.getDesc());
                    }
                }
            }
        }else {
            //更新tb_num 表 skuid,start_time,end_time,status
            goodsMapper.updateNumStatus(num_id,skuid,num);
            //更新 tb_sku 表 sku_num 数量
            int skuids = NumberUtils.toInt(skuid);
            Sku nowSku = skuMapper.getSkuBySkuid(skuids);
//            nowSku.setSkuNum(Integer.parseInt((String.valueOf(nowSku.getSkuNum())))-1);//修改sku数量
            int count =1;
            skuMapper.updateSkuNumDown(nowSku.getSkuId(),count);
            if(skuGoodsType!=3){  //普靓,不涉及仓库库存
                //调用仓储接口
                param.put("supply_id", skuids);//供货单编码(sku_id)
                Result res;
                //再冻结新库存
                param.put("type", "2");//处理类型1上架；2下架
                param.put("quantity", 1);//数量
                param.put("companystock_id", nowSku.getSkuRepoGoods());//库存编码(skuRepoGoods)
                if(!"0".equals(param.get("quantity").toString())) {
                    res = StorageApiCallUtil.storageApiCall(param, "HK0002");
                    if(res.getCode()!=200){
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return new Result(Result.ERROR, "库存验证失败");
                    }else {
                        StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
                        if (!"00000".equals(sir.getCode())) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return new Result(Result.ERROR, "冻结库存失败\n"+sir.getDesc());
                        }
                    }
                }
            }
        }
        return new Result(Result.OK, "该号码已释放");
    }
}
