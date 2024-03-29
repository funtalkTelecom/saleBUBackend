package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.Constants;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.NumRuleMapper;
import com.hrtx.web.mapper.NumberMapper;
import com.hrtx.web.mapper.NumberPriceMapper;
import com.hrtx.web.mapper.SkuMapper;
import com.hrtx.web.pojo.*;
import com.hrtx.web.pojo.Number;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class NumberService {
	
	@Autowired
	private NumberMapper numberMapper;
	@Autowired
	private NumRuleMapper numRuleMapper;
	@Autowired
	private SkuMapper skuMapper;
	@Autowired
	private NumberPriceMapper numberPriceMapper;

    public final Logger log = LoggerFactory.getLogger(this.getClass());

	public Result numberUpdate(List<Map<String,Object>> isGoodSkuMap, Goods goods){
        for(int i=0; i<isGoodSkuMap.size(); i++){
            log.info("更新开始");
            Map map1 =(Map) isGoodSkuMap.get(i);
            Integer skuid =NumberUtils.toInt(String.valueOf(map1.get("SkuId")));
            Sku s =  skuMapper.selectByPrimaryKey(skuid);
            String skuSaleNumb = String.valueOf(map1.get("skuSaleNumbs"));
            double basePrice =Double.valueOf(String.valueOf(map1.get("basePrice")));
            String[] skuSaleNumbs = skuSaleNumb.split("\\r?\\n");
            List<NumberPrice> numberPriceList = new ArrayList<NumberPrice>();
            if(s.getStatus()==1  ){
                if(!"1".equals(s.getSkuGoodsType())) {
                    //更tb_num
                    int size = skuSaleNumbs.length;
                    int starts =0;
                    Object[] numResource = null;
                    int limitSize = 1000;
                    while (starts < size){
                        numResource = ArrayUtils.subarray(skuSaleNumbs,starts, starts+limitSize);
                        starts = starts + numResource.length;
                        String b = ArrayUtils.toString(numResource,"");
                        String StrNums = b.substring(b.indexOf("{") + 1, b.indexOf("}"));
                        if("1".equals(goods.getgIsAuc())){
                            numberMapper.updateStatusByNumber(StrNums,skuid,2,goods.getgStartTime(),goods.getgEndTime());
                        }else{
                            numberMapper.updateStatusByNumber(StrNums,skuid,2,null,null);
                        }
                    }
                    log.info("更新tb_num 结束");
                    if("0".equals(goods.getgIsAuc()) && "4".equals(s.getSkuGoodsType())){//更新tb_num_price
                        numberPriceMapper.insertListNumPrice(skuid,basePrice,SessionUtil.getUser().getCorpId());
                        log.info("更新tb_num_price 结束");
                    }

                }
            }
        }
	    return  new Result(Result.OK, "");
    }

	public Result pageNumber(Number number, Map param) {
		PageHelper.startPage(number.startToPageNum(),number.getLimit());
		Page<Object> ob=this.numberMapper.queryPageList(number, param);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Result addTags(Number number, HttpServletRequest request) {
		try {
			String[] numbers = "".equals(number.getNumResource())?new String[] {}:number.getNumResource().split("\\r?\\n");
			if(numbers!=null && numbers.length>0){
				String[] newTags = request.getParameterValues("num_tags");
				boolean isExist = false;
				if(newTags!=null && newTags.length>0) {
                    List insertList = new ArrayList();
                    //验证号码是否在库中
					StringBuffer errorNums = new StringBuffer();
					for(String num : numbers){
						Map cn = numberMapper.getNumInfoByNum(num);
						if(cn==null || cn.get("id")==null) errorNums.append(num + "\n");
					}
					if(!StringUtils.isBlank(errorNums.toString())){
						return new Result(Result.ERROR, "以下号码不在库中,无法设置\n" + errorNums.toString());
					}
					for(String num : numbers){
						//获取号码现有的所有标签,然后和新标签比对,无则新增
                        String tnt = "";
						List<NumRule> list = numRuleMapper.getNumRuleByNum(num);
                        NumRule nr = null;
                        for(int i=0; i<newTags.length; i++){
                            tnt = newTags[i];
                            //比对标签值
                            for(int j=0; j<list.size(); j++){
                                nr = list.get(j);
                                if (tnt.equals(nr.getValue())) {
                                    isExist = true;
                                    break;
                                }
                            }
                            if(isExist){
                                isExist = false;
                                continue;
                            }else{//标签不存在,新增相应号码的相应标签
                                NumRule nnr = new NumRule();
//                                nnr.setId(nnr.getGeneralId());
                                nnr.setRuleType("tag");
                                nnr.setNumId(nr.getNumId());
                                nnr.setNum(num);
                                nnr.setValue(tnt);
                                insertList.add(nnr);
                            }
                        }
					}
					if(insertList.size()>0) numRuleMapper.insertBatch(insertList);
				}else{
					return new Result(Result.PARAM, "请选择标签");
				}
			}else{
				return new Result(Result.PARAM, "号码不能为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Result.ERROR, "设置标签出错,请稍后再试");
		}

		return new Result(Result.OK, "设置成功");
	}

    public Result clearTags(Number number, HttpServletRequest request) {
        try {
            String[] numbers = "".equals(number.getNumResource())?new String[] {}:number.getNumResource().split("\\r?\\n");
            if(numbers!=null && numbers.length>0){
                StringBuffer nums = new StringBuffer();
                for (String n : numbers) {
                    if(!StringUtils.isBlank(n)) {
                        nums.append("'" + n.trim() + "'" + ",");
                    }
                }
                numRuleMapper.deleteByNums(nums.length()>0?nums.substring(0, nums.length()-1):"");
            }else{
                return new Result(Result.PARAM, "号码不能为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(Result.ERROR, "清空标签出错,请稍后再试");
        }

        return new Result(Result.OK, "清空标签成功");
    }

    public Result againSl(Number number) {
	    number = numberMapper.selectByPrimaryKey(number.getId());
	    if(number == null || number.getStatus() != Constants.NUM_STATUS_7.getIntKey()) return new Result(Result.ERROR, "号码不存在");
        number.setStatus(Constants.NUM_STATUS_5.getIntKey());
        numberMapper.updateByPrimaryKeySelective(number);
	    return new Result(Result.OK, "提交成功");
    }

    public Result over(Number number) {
        number = numberMapper.selectByPrimaryKey(number.getId());
        if(number == null || number.getStatus() != Constants.NUM_STATUS_7.getIntKey()) return new Result(Result.ERROR, "号码不存在");
        number.setStatus(Constants.NUM_STATUS_9.getIntKey());
        numberMapper.updateByPrimaryKeySelective(number);
        return new Result(Result.OK, "提交成功");
    }

    public Result beginSale(Number number) {
        List<String> nums = Utils.trimToArray(ObjectUtils.toString(number.getNumResource()), "\\n");
	    if(nums.size() == 0) return new Result(Result.ERROR,"请输入号码");
	    List<String> errors = new ArrayList<>();
        for (int i = 0; i < nums.size(); i++) {
            String num = nums.get(i);
            Example example = new Example(Number.class);
            example.createCriteria().andEqualTo("numResource", num).andEqualTo("status", Constants.NUM_STATUS_10.getIntKey());
            Number number1 = new Number();
            number1.setStatus(Constants.NUM_STATUS_2.getIntKey());
            int count = numberMapper.updateByExampleSelective(number1, example);
            if(count == 0) errors.add(num);
        }
        return new Result(Result.OK, errors);
    }

    public Result stopSale(Number number) {
        List<String> nums = Utils.trimToArray(ObjectUtils.toString(number.getNumResource()), "\\n");
        if(nums.size() == 0) return new Result(Result.ERROR,"请输入号码");
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < nums.size(); i++) {
            String num = nums.get(i);
            Example example = new Example(Number.class);
            example.createCriteria().andEqualTo("numResource", num).andEqualTo("status", Constants.NUM_STATUS_2.getIntKey());
            Number number1 = new Number();
            number1.setStatus(Constants.NUM_STATUS_10.getIntKey());
            int count = numberMapper.updateByExampleSelective(number1, example);
            if(count == 0) errors.add(num);
        }
        return new Result(Result.OK, errors);
    }
}
