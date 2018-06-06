package com.hrtx.web.service;

import com.hrtx.web.mapper.CityMapper;
import com.hrtx.web.pojo.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class CityService {
	@Autowired
	private CityMapper cityMapper;

	public List<City> queryByPidList(int pid){
		return cityMapper.queryByPidList(pid);
	}

	public City findCityByName(String name){
		return cityMapper.findCityByName(name);
	}

	public Map findCityByNameFromThird(String name) {
		List<HashMap> list = cityMapper.findCityByNameFromThird(name);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("num", 0);
		if(list!=null && list.size()>0) map.put("num", list.size());
		if(list.size()==1) map.put("city_id", list.get(0).get("cityId"));
		return map;
	}

	public List queryByThird(String third) {
		return cityMapper.queryByThird(third);
	}

    public List queryByPidListForZtree(int pid, String isopen) {
		return cityMapper.queryByPidListForZtree(pid, isopen);
    }
}
