package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SessionUtil;
import com.hrtx.web.mapper.DictMapper;
import com.hrtx.web.mapper.NumPriceMapper;
import com.hrtx.web.pojo.Dict;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class DictService {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DictMapper dictMapper;
	@Autowired private NumPriceMapper numPriceMapper;
	@Autowired private LyCrmService lyCrmService;

	public Result pageDict(Dict dict) {
		PageHelper.startPage(dict.startToPageNum(),dict.getLimit());
		Page<Object> ob=this.dictMapper.queryPageList(dict);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Dict findDictById(Integer id) {
		Dict dict = dictMapper.findDictInfo(id);
		return dict;
	}

	public Result dictEdit(Dict dict) {
		if (dict.getId() != null && dict.getId() > 0) {
			if (!DictKeyIdIsExist(dict)) {
				dictMapper.dictEdit(dict);
			} else {
				return new Result(Result.ERROR, "key_id已存在");
			}
		} else {
			List<Dict> list = new ArrayList<Dict>();
			list.add(dict);
			dictMapper.insertBatch(list);
		}

		return new Result(Result.OK, "提交成功");
	}

	private boolean DictKeyIdIsExist(Dict dict) {
		boolean b = true;
		int num = this.dictMapper.checkDictKeyIdIsExist(dict);
		if(num <= 0) b=false;

		return b;
	}

	public Result dictDelete(Dict dict) {
		dictMapper.dictDelete(dict);
		return new Result(Result.OK, "删除成功");
	}

	public Result featherDelete(Dict dict) {
		Dict dict1 = dictMapper.selectByPrimaryKey(dict.getId());
		dictMapper.dictDelete(dict);
		lyCrmService.delRule(dict1);
		return new Result(Result.OK, "删除成功");
	}

	public Dict findGroupMaxInfo(String keyGroup) {
		return dictMapper.findGroupMaxInfo(keyGroup);
	}

	public List findDictByGroup(String group) {
		return dictMapper.findDictByGroup(group);
	}

    public List findDictByTypeGroup(String group) {
		return dictMapper.findDictByTypeGroup(group);
    }

	public Result editFeatherPrice(Dict dict) {
		int id = NumberUtils.toInt(String.valueOf(dict.getId()));
		if(id==0){
			if(SessionUtil.hasPower(PowerConsts.CHANNELMOUDULE_COMMON_ADD)){
				String name =ObjectUtils.toString(dict.getKeyValue(), " ");
				Dict d = new Dict();
				d.setKeyValue(dict.getKeyValue());
				d.setKeyGroup("feather_price");
				d.setCorpId(SessionUtil.getUser().getCorpId());
				d.setIsDel(0);
				Dict dict2 = dictMapper.selectOne(d);
				if(dict2!=null) return new Result(Result.ERROR,"["+name+"]在该类别已存在");
				dict.setKeyGroup("feather_price");
				dict.setIsDel(0);
				Map map=dictMapper.maxSeqAndKeyId("feather_price");
				dict.setKeyId(String.valueOf(NumberUtils.toInt(String.valueOf(map.get("keyId")))+1));
				dict.setSeq(NumberUtils.toInt(String.valueOf(map.get("seq")))+1);
				dict.setCorpId(SessionUtil.getUser().getCorpId());
				dictMapper.insert(dict);
				lyCrmService.addRule(dict);
			}else {
				return new Result(Result.ERROR, "没有权限");
			}
		}else {
			if(SessionUtil.hasPower(PowerConsts.CHANNELMOUDULE_COMMON_EDIT)){
				Dict dict1 = dictMapper.selectByPrimaryKey(dict.getId());
				if(dict1==null) return new Result(Result.OK, "数据不存在");
				dict1.setExt1(dict.getExt1());
				dict1.setExt2(dict.getExt2());
				dictMapper.updateByPrimaryKey(dict1);
				log.info("号码规则["+dict1.getKeyValue()+"]更改后的不带4价为["+dict.getExt1()+"],带4价为["+dict.getExt2()+"]");
				List<Map> maps = numPriceMapper.queryNewestNumPrice(dict1.getCorpId());
				if(maps.size()>0) numPriceMapper.matchNumPriceByBatch(maps);
			}else {
				return new Result(Result.ERROR, "没有权限");
			}
		}
		return new Result(Result.OK, "成功");
	}

	public Result addFeatherType(Dict dict,String keyGroup) {
		String name =ObjectUtils.toString(dict.getKeyValue(), " ");
		Dict d = new Dict();
		d.setKeyValue(dict.getKeyValue());
		d.setKeyGroup(keyGroup);
		d.setIsDel(0);
		d.setCorpId(-1);
		Dict dict2 = dictMapper.selectOne(d);
		if(dict2!=null) return new Result(Result.ERROR,"["+name+"]在该类别已存在");
		dict.setKeyGroup(keyGroup);
		dict.setIsDel(0);
		Map map=dictMapper.maxSeqAndKeyId(keyGroup);
		dict.setKeyId(String.valueOf(NumberUtils.toInt(String.valueOf(map.get("keyId")))+1));
		dict.setSeq(NumberUtils.toInt(String.valueOf(map.get("seq")))+1);
		dictMapper.insert(dict);
		lyCrmService.addRule(dict);
		return new Result(Result.OK, "成功");
	}

	public List findDictByValue(String keyGroup,String keyValue) {
		return dictMapper.findDictByValue(keyGroup,keyValue);
	}


}
