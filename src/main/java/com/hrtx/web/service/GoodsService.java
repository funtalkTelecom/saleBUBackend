package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.System;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
            String ignoreKey = "undefined,skuId,skuTobPrice,skuTocPrice,skuIsNum,skuSaleNum,skuNum,skuGoodsType,skuRepoGoods,skuRepoGoodsName";
            //商品主表操作
            List<Goods> list = new ArrayList<Goods>();
            String isSale = goods.getgIsSale();
            if (goods.getgId() != null && goods.getgId() > 0) {
                //上架中的竞拍商品只允许修改个别字段
                if("1".equals(goods.getgIsAuc()) && "1".equals(isSale)){

                }
                goodsMapper.goodsEdit(goods);
                goods = goodsMapper.findGoodsInfo(goods.getgId());
            } else {
//                User user = SessionUtil.getUser();
                Corporation corporation = (Corporation) SessionUtil.getSession().getAttribute("corporation");
                goods.setgId(goods.getGeneralId());
                goods.setgSellerId(corporation.getId());
                goods.setgSellerName(corporation.getName());
                list.add(goods);
            }
            //商品子表操作
            Sku sku = new Sku();
            Sku dsku = new Sku();
            dsku.setgId(goods.getgId());

            SkuProperty skuProperty = new SkuProperty();
            SkuProperty dskuProperty = new SkuProperty();
            dskuProperty.setgId(dsku.getgId());


            String skuPropertyJsonStr = request.getParameter("skuJson") == null ? "" : request.getParameter("skuJson");
            JSONArray skuPropertyJsonArr = JSONArray.fromObject(skuPropertyJsonStr);
            //删除之前先验证
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
                    String price = ((JSONObject) obj.get("skuTobPrice")).get("value")==null||((JSONObject) obj.get("skuTobPrice")).get("value").equals("null")||((JSONObject) obj.get("skuTobPrice")).get("value").equals("")?"": (String) ((JSONObject) obj.get("skuTobPrice")).get("value");
                    if("".equals(price)) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return new Result(Result.ERROR, "第"+(i+1)+"行2B价格为空");
                    }else{
                        String pattern = "[1-9]\\d*.?\\d*|0.\\d*[1-9]\\d*";
                        Pattern r = Pattern.compile(pattern);
                        Matcher m = r.matcher(price);
                        if(!m.matches()) return new Result(Result.ERROR, "第" + (i + 1) + "行2B价格格式错误,请输入两位正小数");
                    }
                    sku.setSkuTobPrice(Double.parseDouble(price));

//                    price = ((JSONObject) obj.get("skuTocPrice")).get("value")==null||((JSONObject) obj.get("skuTocPrice")).get("value").equals("null")||((JSONObject) obj.get("skuTocPrice")).get("value").equals("")?"": (String) ((JSONObject) obj.get("skuTocPrice")).get("value");
//                    if("".equals(price)) {
//                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                        return new Result(Result.ERROR, "第"+(i+1)+"行2C价格为空");
//                    }
//                    sku.setSkuTocPrice(Double.parseDouble(price));

//                    sku.setSkuIsNum(((JSONObject) obj.get("skuIsNum")).get("value")==null||((JSONObject) obj.get("skuIsNum")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuIsNum")).get("value"));
                    price = ((JSONObject) obj.get("skuNum")).get("value")==null||((JSONObject) obj.get("skuNum")).get("value").equals("null")?"": ((JSONObject) obj.get("skuNum")).get("value").toString();
                    if("".equals(price)) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return new Result(Result.ERROR, "第" + (i + 1) + "行数量为空");
                    }else{
                        String pattern = "[1-9]\\d*";
                        Pattern r = Pattern.compile(pattern);
                        Matcher m = r.matcher(price);
                        if(!m.matches()) return new Result(Result.ERROR, "第" + (i + 1) + "行数量格式错误,请输入正整数");
                    }

                    skuSaleNum = ((JSONObject) obj.get("skuSaleNum")).get("value")==null||((JSONObject) obj.get("skuSaleNum")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuSaleNum")).get("value");
                    sku.setSkuGoodsType(((JSONObject) obj.get("skuGoodsType")).get("value")==null||((JSONObject) obj.get("skuGoodsType")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuGoodsType")).get("value"));
                    String tskuId = String.valueOf(((JSONObject) obj.get("skuId")).get("value"));
                    sku.setSkuId(Long.parseLong(tskuId==null||tskuId.equals("null")||tskuId.equals("")?String.valueOf(sku.getGeneralId()): tskuId));
                    sku.setSkuNum(((JSONObject) obj.get("skuNum")).get("value")==null||((JSONObject) obj.get("skuNum")).get("value").equals("null")?0: Integer.parseInt(((JSONObject) obj.get("skuNum")).get("value").toString()));

                    //白卡不验证号码
                    if(!"1".equals(sku.getSkuGoodsType())) {
                        String repeatNum = getRepeatNum(skuSaleNum);
                        if(repeatNum!=null && repeatNum.length()>0) return new Result(Result.ERROR, "以下号码重复\n"+repeatNum);
                        skuSaleNum = checkSkuSaleNum(skuSaleNum, sku, false, "".equals(tskuId)?sku.getSkuId():Long.parseLong(tskuId));
    //                    sku.setSkuRepoGoods(((JSONObject) obj.get("skuRepoGoods")).get("value")==null||((JSONObject) obj.get("skuRepoGoods")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuRepoGoods")).get("value"));
    //                    sku.setSkuRepoGoodsName(((JSONObject) obj.get("skuRepoGoodsName")).get("value")==null||((JSONObject) obj.get("skuRepoGoodsName")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuRepoGoodsName")).get("value"));
                        String[] okSkuNum = new String[] {};
                        if(skuSaleNum.indexOf("★")!=0){
                            okSkuNum = skuSaleNum.split("★")[0].split("\\r?\\n");
                        }
                        int okCount = okSkuNum.length;
                        if (okSkuNum.length !=0 && StringUtils.isBlank(okSkuNum[0]) && okSkuNum.length == 1) okCount = 0;
                        //有错误号码
                        if (skuSaleNum.split("★").length > 1) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return new Result(Result.ERROR, "第" + (i + 1) + "行,以下号码不符合,请重新确认\n" + skuSaleNum.split("★")[1]);
                        }
                        if (sku.getSkuNum() != okCount) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return new Result(Result.ERROR, "第" + (i + 1) + "行,填写数量和允许上架号码量(" + okCount + ")不一致");
                        }
                    }
                }
            }
            if(list!=null && list.size()>0) goodsMapper.insertBatch(list);
            //验证通过之后删除数据,重新写入
//            skuMapper.deleteSkuByGid(dsku);
            skuPropertyMapper.deleteSkuPropertyByGid(dskuProperty);
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
                    String price = ((JSONObject) obj.get("skuTobPrice")).get("value")==null||((JSONObject) obj.get("skuTobPrice")).get("value").equals("null")||((JSONObject) obj.get("skuTobPrice")).get("value").equals("")?"": (String) ((JSONObject) obj.get("skuTobPrice")).get("value");
                    sku.setSkuTobPrice(Double.parseDouble(price));

//                    sku.setSkuIsNum(((JSONObject) obj.get("skuIsNum")).get("value")==null||((JSONObject) obj.get("skuIsNum")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuIsNum")).get("value"));
                    price = ((JSONObject) obj.get("skuNum")).get("value")==null||((JSONObject) obj.get("skuNum")).get("value").equals("null")?"": ((JSONObject) obj.get("skuNum")).get("value").toString();
                    sku.setSkuNum(Integer.parseInt(price));
                    skuSaleNum = ((JSONObject) obj.get("skuSaleNum")).get("value")==null||((JSONObject) obj.get("skuSaleNum")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuSaleNum")).get("value");
//                    验证号码可用性之前赋值旧的skuId,便于tb_num表复原状态
                    sku.setSkuGoodsType(((JSONObject) obj.get("skuGoodsType")).get("value")==null||((JSONObject) obj.get("skuGoodsType")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuGoodsType")).get("value"));
                    String tskuId = String.valueOf(((JSONObject) obj.get("skuId")).get("value"));
                    sku.setSkuId(Long.parseLong(tskuId==null||tskuId.equals("null")||tskuId.equals("")?String.valueOf(sku.getGeneralId()): tskuId));

//                    skuSaleNum = checkSkuSaleNum(skuSaleNum, sku, true, Long.parseLong(tskuId));
                    //白卡不验证号码
                    if(!"1".equals(sku.getSkuGoodsType())) {
                        skuSaleNum = checkSkuSaleNum(skuSaleNum, sku, true, "".equals(tskuId) ? sku.getSkuId() : Long.parseLong(tskuId));
                        String[] okSkuNum = skuSaleNum.split("★")[0].split("\\r?\\n");
                        int okCount = okSkuNum.length;
                        if (okSkuNum[0] == "" && okSkuNum.length == 1) okCount = 0;
                        sku.setSkuNum(okCount);
                        //统一更新号码状态
                        for (String okNum : okSkuNum) {
                            Number okNumber = new Number();
                            okNumber.setSkuId(sku.getSkuId());
                            okNumber.setStatus(2);
                            okNumber.setNumResource(okNum);
                            //竞拍商品把时间往tb_num写入
                            if("1".equals(goods.getgIsAuc())){
                                okNumber.setStartTime(goods.getgStartTime());
                                okNumber.setEndTime(goods.getgEndTime());
                            }
                            numberMapper.updateStatus(okNumber, false);
                        }
                    }else {//是白卡就把该skuid下的所有号码状态改成1,清空tb_num的sku_id
                        checkSkuSaleNum("", sku, true, "".equals(tskuId) ? sku.getSkuId() : Long.parseLong(tskuId));
                    }

//                    sku.setSkuId(sku.getGeneralId());
                    sku.setSkuSaleNum(skuSaleNum.split("★")[0].split("\n")[0]);
                    sku.setSkuRepoGoods(((JSONObject) obj.get("skuRepoGoods")).get("value")==null||((JSONObject) obj.get("skuRepoGoods")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuRepoGoods")).get("value"));
                    sku.setSkuRepoGoodsName(((JSONObject) obj.get("skuRepoGoodsName")).get("value")==null||((JSONObject) obj.get("skuRepoGoodsName")).get("value").equals("null")?"": (String) ((JSONObject) obj.get("skuRepoGoodsName")).get("value"));

                    //调用仓储接口
                    Map param = new HashMap();
                    param.put("supply_id", sku.getSkuId());//供货单编码(sku_id)
                    //获取目前sku信息
                    Sku nowSku = skuMapper.getSkuBySkuid(sku.getSkuId());
                    Result res;
                    if(goods.getGeneralId()!=goods.getgId() && "1".equals(isSale)) {
                        //先解冻现有库存
                        param.put("type", "2");//处理类型1上架；2下架
                        param.put("quantity", nowSku == null ? 0 : nowSku.getSkuNum());//数量
                        param.put("companystock_id", nowSku.getSkuRepoGoods());//库存编码(skuRepoGoods)
                        if(!"0".equals(param.get("quantity").toString())) {
                            res = StorageApiCallUtil.storageApiCall(param, "HK0002");
                            if (200 != (res.getCode())) {
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                return new Result(Result.ERROR, "第"+(i+1)+"行,库存验证失败");
                            } else {
                                StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
                                if (!"00000".equals(sir.getCode())) {
                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                    return new Result(Result.ERROR, "第"+(i+1)+"行,解冻库存失败\n"+sir.getDesc());
                                }
                            }
                        }
                    }
                    //再冻结新库存
                    param.put("type", "1");//处理类型1上架；2下架
                    param.put("quantity", sku.getSkuNum());//数量
                    param.put("companystock_id", sku.getSkuRepoGoods());//库存编码(skuRepoGoods)
                    if(!"0".equals(param.get("quantity").toString())) {
                        res = StorageApiCallUtil.storageApiCall(param, "HK0002");
                        if (200 != (res.getCode())) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return new Result(Result.ERROR, "第"+(i+1)+"行,库存验证失败");
                        } else {
                            StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
                            if (!"00000".equals(sir.getCode())) {
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                return new Result(Result.ERROR, "第"+(i+1)+"行,冻结库存失败\n"+sir.getDesc());
                            }
                        }
                    }

                    //sku属性表操作end
                    //判断是否存在,存在就update,否则加到list中insert
                    if(!"".equals(tskuId)&&skuMapper.getSkuBySkuid(Long.parseLong(tskuId))!=null) {
                        sku.setSkuId(Long.parseLong(tskuId));
                        skuMapper.updateSku(sku);
                    }
                    else skuList.add(sku);

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
                    if(skuPropertyList!=null && skuPropertyList.size()>0) skuPropertyMapper.insertBatch(skuPropertyList);
                }
                if(skuList!=null && skuList.size()>0) skuMapper.insertBatch(skuList);
                //删除前台传过来的sku
                String delSkus = request.getParameter("delSkus");
                //调用仓储解冻库存
                if(!StringUtils.isBlank(delSkus)){
                    //上架中的才需要解冻库存
                    if("1".equals(isSale)) {
                        String[] delskus = delSkus.split(",");
                        for (String delSku : delskus) {
                            if (!StringUtils.isBlank(delSku)) {
                                //获取目前sku信息
                                Sku s = skuMapper.getSkuBySkuid(Long.parseLong(delSku));
                                if (s != null) {
                                    //调用仓储接口
                                    Map param = new HashMap();
                                    param.put("supply_id", s.getSkuId());//供货单编码(sku_id)
                                    Result res;
                                    //解冻现有库存
                                    param.put("type", "2");//处理类型1上架；2下架
                                    param.put("quantity", s == null ? 0 : s.getSkuNum());//数量
                                    param.put("companystock_id", s.getSkuRepoGoods());//库存编码(skuRepoGoods)
                                    if (!"0".equals(param.get("quantity").toString())) {
                                        res = StorageApiCallUtil.storageApiCall(param, "HK0002");
                                        if (200 != (res.getCode())) {
                                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                            return new Result(Result.ERROR, "解冻库存失败");
                                        } else {
                                            StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
                                            if (!"00000".equals(sir.getCode())) {
                                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                                return new Result(Result.ERROR, "解冻库存失败\n" + sir.getDesc());
                                            }
                                        }
                                    }
                                }
                                // by zdh 20180712
                                //更新删除的tb_num 状态
                                numberMapper.updateDelStatus(s.getSkuId());
                            }
                        }
                    }
                    //删除sku
                    delSkus = StringUtils.isBlank(delSkus) ? "" : "'"+delSkus.substring(0, delSkus.length() - 1).replaceAll(",", "','")+"'";
                    skuMapper.deleteSkuBySkuids(delSkus);
                }

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
                        result = BaseReturn.uploadFile(SystemParam.get("goodsPics")+goods.getgId()+java.io.File.separator, "jpg,png,gif", file, false, false);
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
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result(Result.ERROR, "操作异常");
        }

        return new Result(Result.OK, "提交成功");
	}

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

    private String checkSkuSaleNum(String skuSaleNum, Sku sku, boolean isUpdate, Long skuid) {
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
                //验证号码可用性
                number = new Number();
                number.setNumResource(skuSaleNumbs[i].trim());
                number.setSkuId(skuid);
                if(numberMapper.checkNumberIsOk(number) > 0) {
                    skuSaleNum += skuSaleNumbs[i].trim()+"\n";
                    //更新状态
                    number.setSkuId(skuid);
                    number.setStatus(2);
//                    if(isUpdate) numberMapper.updateStatus(number);
                }else{
                    errorNum += skuSaleNumbs[i].trim() + "\n";
                }
            }
        }else return "";

	    if(skuSaleNum.length()!=0) skuSaleNum = skuSaleNum.substring(0, skuSaleNum.length()-1);
	    return skuSaleNum+"★"+errorNum;
    }

    public Result goodsDelete(Goods goods) {
        Result res = new Result(Result.ERROR, "请求异常");
        List<Sku> skuList = skuMapper.findSkuInfo(goods.getgId());
        try {
            for(Sku s : skuList){
                Map param = new HashMap();
                param.put("supply_id", s.getSkuId());//供货单编码(sku_id)
                param.put("companystock_id", s.getSkuRepoGoods());//库存编码(skuRepoGoods)
                param.put("type", "2");//处理类型1上架；2下架
                param.put("quantity", s.getSkuNum());//数量
                if(s.getSkuNum()!=0 && "1".equals(goods.getgIsSale())) {
                    res = StorageApiCallUtil.storageApiCall(param, "HK0002");
                    if (200 != (res.getCode())) {
                        return new Result(Result.ERROR, "库存验证失败");
                    } else {
                        StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
                        if (!"00000".equals(sir.getCode())) {
                            return new Result(Result.ERROR, "库存验证失败\n" + sir.getDesc());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(Result.ERROR, "请求异常");
        }
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

    public Result goodsUnsale(Goods goods, HttpServletRequest request) {

        Result res = new Result(Result.ERROR, "请求异常");
        try {
            List<Sku> skuList = skuMapper.findSkuInfo(goods.getgId());
            for(Sku s : skuList){
                Map param = new HashMap();
                param.put("supply_id", s.getSkuId());//供货单编码(sku_id)
                param.put("companystock_id", s.getSkuRepoGoods());//库存编码(skuRepoGoods)
                param.put("type", "2");//处理类型1上架；2下架
                param.put("quantity", s.getSkuNum());//数量
                if(s.getSkuNum()!=0) {
                    res = StorageApiCallUtil.storageApiCall(param, "HK0002");
                    if (200 != (res.getCode())) {
                        return new Result(Result.ERROR, "库存验证失败");
                    } else {
                        StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
                        if (!"00000".equals(sir.getCode())) {
                            return new Result(Result.ERROR, "库存验证失败");
                        }else{
                            //成功之后吧上架的号码状态还原成1
                            Number number = new Number();
                            number.setSkuId(s.getSkuId());
                            number.setStatus(1);
                            numberMapper.updateStatus(number, false);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(Result.ERROR, "下架库存异常");
        }
        goodsMapper.goodsUnsale(goods);
        res = new Result(Result.OK, "下架成功");
        return res;
    }

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
}
