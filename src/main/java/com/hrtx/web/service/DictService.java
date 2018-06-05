package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.web.mapper.DictMapper;
import com.hrtx.web.pojo.Dict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class DictService {
	
	@Autowired
	private DictMapper dictMapper;

	public Result pageDict(Dict dict) {
		PageHelper.startPage(dict.getPageNum(),dict.getLimit());
		Page<Object> ob=this.dictMapper.queryPageList(dict);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Dict findDictById(Long id) {
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
			dict.setId(dict.getGeneralId());
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

	public Dict findGroupMaxInfo(String keyGroup) {
		return dictMapper.findGroupMaxInfo(keyGroup);
	}

	public List findDictByGroup(String group) {
		return dictMapper.findDictByGroup(group);
	}
}
