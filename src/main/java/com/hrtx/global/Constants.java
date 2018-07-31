package com.hrtx.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 此类为常量类，存放支付的常量,便于统一管理。
 * 命名规则如下
 * 若为系统要用到的常量，则为:参数+有意义的值命名
 * @author 张进春
 *
 */
public enum Constants {

    //支付方式
    PAY_MENTHOD_TYPE_1("1", "微信支付", "PAY_MENTHOD_TYPE"),
//    PAY_MENTHOD_TYPE_2("2", "支付宝支付", "PAY_MENTHOD_TYPE"),
    PAY_MENTHOD_TYPE_3("3", "线下支付", "PAY_MENTHOD_TYPE"),
    PAY_MENTHOD_TYPE_4("4", "分期付款", "PAY_MENTHOD_TYPE"),

    ;

    private Object key;
    private String value;
    private String type;
    private Constants(Object key,String value, String type){
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public Object getKey(){
        return key;
    }
    public int getIntKey() {
        return (Integer) key;
    }
    public String getStringKey() {
        return String.valueOf(key);
    }
    public String getValue() {
        return value;
    }
    public String getType() {
        return type;
    }

    public static Map<Object, Map<Object, String>> map = null;


    public static Map<Object, Map<Object, String>> setValue() {
        Map<Object, Map<Object, String>> _map = new HashMap<Object, Map<Object, String>>();
        Constants[] constants = Constants.values();
        for (Constants constant : constants) {
            Map<Object, String> map = _map.get(constant.getType());
            if(map==null){
                map =new HashMap<Object, String>();
            }
            map.put(constant.getKey(), constant.getValue());
            _map.put(constant.getType(), map);
        }
        return _map;
    }

    public static Map<Object, String> contantsToMap(String type) {
        if(map==null){
            map=setValue();
        }
        return map.get(type);
    }

    public static List<Map<String, Object>> contantsToList(String type){
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        Constants[] constants = Constants.values();
        for (Constants constant : constants) {
            if(constant.getType().equals(type)){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("key", constant.getKey());
                map.put("value", constant.getValue());
                list.add(map);
            }
        }
        return list;
    }

    public static Object[] getValueObject(String type) {
        Object[] o = null;
        List<Map<String, Object>> list = contantsToList(type);
        if(list !=null && list.size() > 0 ){
            o = new Object[list.size()];
            for (int i = 0; i < list.size(); i++) {
                o[i] = list.get(i).get("value");
            }
        }
        return o;
    }

    public static Object[] getKeyObject(String type) {
        Object[] o = null;
        List<Map<String, Object>> list = contantsToList(type);
        if(list !=null && list.size() > 0 ){
            o = new Object[list.size()];
            for (int i = 0; i < list.size(); i++) {
                o[i] = list.get(i).get("key");
            }
        }
        return o;
    }

}
