package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.hrtx.web.mapper.CityMapper;
import com.hrtx.web.pojo.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


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

	public List<Object> queryCitys() {
		City city1=new City();
		city1.setIs_del(0);
		Example example=new Example(City.class);
		example.createCriteria().andEqualTo("is_del",0);
		List<City> list_city=this.cityMapper.selectByExample(example);
		Map<Integer,List<City>> map=new HashMap<>();
		for (City city:list_city) {
			List<City> _list=map.get(city.getPid());
			if(_list==null)_list=new ArrayList<>();
			_list.add(city);
			map.put(city.getPid(),_list);
		}
		List<Object> list=queryCityByPid(0,map);
		return list;
	}
	public List<Object> queryCityByPid(int pid,Map<Integer,List<City>> map_city) {
		List<Object> list=new ArrayList<>();
		//List<City> _list=this.cityMapper.queryByPidList(pid);
		List<City> _list=map_city.get(pid);
		if(_list==null)return null;
		for (City city: _list) {
			TreeMap<String,Object> _map=new TreeMap<>();
			_map.put("id",city.getId());
			_map.put("name",city.getName());
			_map.put("pid",city.getPid());
			if(city.getGrade()==1) {
				List<Object> _list1=queryCityByPid(city.getId(),map_city);
				_map.put("cityList",_list1)	;
			}
			if(city.getGrade()==2) {
				List<Object> _list1=queryCityByPid(city.getId(),map_city);
				_map.put("districtList",_list1)	;
			}
			list.add(_map);
		}
		return list;
	}
}
