package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.web.mapper.NumRuleMapper;
import com.hrtx.web.mapper.NumberMapper;
import com.hrtx.web.pojo.NumRule;
import com.hrtx.web.pojo.Number;
import org.apache.commons.lang.StringUtils;
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

	public Result pageNumber(Number number) {
		PageHelper.startPage(number.getPageNum(),number.getLimit());
		Page<Object> ob=this.numberMapper.queryPageList(number);
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
                                nnr.setId(nnr.getGeneralId());
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
					return new Result(Result.PARAM, "请选择标签后在操作");
				}
			}else{
				return new Result(Result.PARAM, "号码不能为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Result.ERROR, "操作异常");
		}

		return new Result(Result.OK, "设置成功");
	}
}
