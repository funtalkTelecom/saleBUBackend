package com.hrtx.global;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class MapJsonUtils {
	
	/**
	 * 数组json解析成list
	 * @param jsonStr
	 * @return
	 */
	public static List<Map<String, Object>> parseJSON2List(String jsonStr){
	    JSONArray jsonArr = JSONArray.fromObject(jsonStr);
	    List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	    Iterator<JSONObject> it = jsonArr.iterator();
	    while(it.hasNext()){
	       JSONObject json2 = it.next();
	       list.add(parseJSON2Map(json2.toString()));
	    }
	    return list;
	}
  
   
	/**
	 * 对象json解析成map
	 * @param jsonStr
	 * @return
	 */
    public static Map<String, Object> parseJSON2Map(String jsonStr){
    	TreeMap<String, Object> map = new TreeMap<String, Object>();
    	//最外层解析
    	JSONObject json = JSONObject.fromObject(jsonStr);
    	for(Object k : json.keySet()){
    		Object v = json.get(k); 
    		//如果内层还是数组的话，继续解析
    		if(v instanceof JSONArray){
    			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
    			Iterator<JSONObject> it = ((JSONArray)v).iterator();
    			while(it.hasNext()){
    				JSONObject json2 = it.next();
    				list.add(parseJSON2Map(json2.toString()));
    			}
    			map.put(k.toString(), list);
    		}else {
    			map.put(k.toString(), v);
    		}
    	}
    	return map;
    }
  
   
    public static List<Map<String, Object>> getListByUrl(String url){
    	try {
    		//通过HTTP获取JSON数据
    		InputStream in = new URL(url).openStream();
    		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    		StringBuilder sb = new StringBuilder();
    		String line;
    		while((line=reader.readLine())!=null){
    			sb.append(line);
	      	}
    		return parseJSON2List(sb.toString());
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return null;
    }
  
   
    public static Map<String, Object> getMapByUrl(String url){
    	try {
    	  //通过HTTP获取JSON数据
	      InputStream in = new URL(url).openStream();
	      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	      StringBuilder sb = new StringBuilder();
	      String line;
	      while((line=reader.readLine())!=null){
	    	  sb.append(line);
	      }
	      return parseJSON2Map(sb.toString());
    	} catch (Exception e) {
    		e.printStackTrace();
	    }
	    return null;
    }
  
}