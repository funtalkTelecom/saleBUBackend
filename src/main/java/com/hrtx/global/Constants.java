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

    //订单状态
    ORDER_STATUS_0(0, "创建订单", "ORDER_STATUS"),
    ORDER_STATUS_20(20, "已创建待冻结", "ORDER_STATUS"),
    ORDER_STATUS_21(21, "待审核", "ORDER_STATUS"),
    ORDER_STATUS_1(1, "待付款", "ORDER_STATUS"),
    ORDER_STATUS_2(2, "已付款待推送", "ORDER_STATUS"),
    ORDER_STATUS_3(3, "待配货", "ORDER_STATUS"),
    ORDER_STATUS_4(4, "待配卡", "ORDER_STATUS"),
    ORDER_STATUS_5(5, "待签收", "ORDER_STATUS"),
    ORDER_STATUS_6(6, "完成", "ORDER_STATUS"),
    ORDER_STATUS_7(7, "已取消", "ORDER_STATUS"),
    ORDER_STATUS_11(11, "待仓库撤销", "ORDER_STATUS"),
    ORDER_STATUS_12(12, "退款中", "ORDER_STATUS"),
    ORDER_STATUS_13(13, "退款失败", "ORDER_STATUS"),
    ORDER_STATUS_14(14, "待财务退款", "ORDER_STATUS"),
    //号码状态
    NUM_STATUS_1(1, "在库", "NUM_STATUS"), //未售出（在华睿库）
    NUM_STATUS_2(2, "销售中", "NUM_STATUS"),//未售出（在华睿库）
    NUM_STATUS_3(3, "冻结", "NUM_STATUS"),
    NUM_STATUS_4(4, "待配卡", "NUM_STATUS"),
    NUM_STATUS_5(5, "待受理", "NUM_STATUS"),
    NUM_STATUS_6(6, "已受理", "NUM_STATUS"),//彻底结束
    NUM_STATUS_7(7, "受理失败", "NUM_STATUS"),
    NUM_STATUS_8(8, "已失效", "NUM_STATUS"),//彻底结束
    NUM_STATUS_9(9, "结束", "NUM_STATUS"),//彻底结束
    NUM_STATUS_10(10, "停售", "NUM_STATUS"),//未售出（在华睿库）

    //订单类型
    ORDER_TYPE_1(1, "普通单", "NUM_STATUS"),
    ORDER_TYPE_2(2, "靓号单", "NUM_STATUS"),
    ORDER_TYPE_3(3, "竞拍单", "NUM_STATUS"),
    ORDER_TYPE_4(4, "客服单", "NUM_STATUS"),

    CHANNEL_ID_1("1", "一级代理", "CHANNEL_ID"),
    CHANNEL_ID_2("2", "二级代理", "CHANNEL_ID"),
    CHANNEL_ID_3("3", "线上商超", "CHANNEL_ID"),

    //1 待审核，2 审核通过,3 审核未通过
    CORP_AGENT_STATUS_1(1, "待审核", "CORP_AGENT_STATUS"),
    CORP_AGENT_STATUS_2(2, "审核通过", "CORP_AGENT_STATUS"),
    CORP_AGENT_STATUS_3(3, "审核未通过", "CORP_AGENT_STATUS"),

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
