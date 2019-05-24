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
    PAY_MENTHOD_TYPE_5("5", "免支付", "PAY_MENTHOD_TYPE"),

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
    NUM_STATUS_11(11, "受理中", "NUM_STATUS"),

    //订单类型
    ORDER_TYPE_1(1, "普通单", "NUM_STATUS"),
    ORDER_TYPE_2(2, "靓号单", "NUM_STATUS"),
    ORDER_TYPE_3(3, "竞拍单", "NUM_STATUS"),
    ORDER_TYPE_4(4, "客服单", "NUM_STATUS"),
    ORDER_TYPE_5(5, "补发单", "NUM_STATUS"),

    //是否需要发货
    ORDERITEM_SHIPMENT_1(1, "是", "ORDERITEM_SHIPMENT"),
    ORDERITEM_SHIPMENT_0(0, "否", "ORDERITEM_SHIPMENT"),

    CHANNEL_ID_1("1", "一级代理", "CHANNEL_ID"),
    CHANNEL_ID_2("2", "二级代理", "CHANNEL_ID"),
    CHANNEL_ID_3("3", "线上商超", "CHANNEL_ID"),

    //1 待审核，2 审核通过,3 审核未通过
    CORP_AGENT_STATUS_1(1, "待审核", "CORP_AGENT_STATUS"),
    CORP_AGENT_STATUS_2(2, "审核通过", "CORP_AGENT_STATUS"),
    CORP_AGENT_STATUS_3(3, "审核未通过", "CORP_AGENT_STATUS"),

    PROMOTION_PLAN_FEETYPE_1(1, "商家推广费", "PROMOTION_PLAN_FEETYPE"),
    PROMOTION_PLAN_FEETYPE_2(2, "技术服务费", "PROMOTION_PLAN_FEETYPE"),
    PROMOTION_PLAN_FEETYPE_3(3, "交易服务费", "PROMOTION_PLAN_FEETYPE"),
    PROMOTION_PLAN_FEETYPE_4(4, "乐语发展费", "PROMOTION_PLAN_FEETYPE"),
    PROMOTION_PLAN_FEETYPE_5(5, "梧桐市场费", "PROMOTION_PLAN_FEETYPE"),
    PROMOTION_PLAN_FEETYPE_6(6, "基础推广费", "PROMOTION_PLAN_FEETYPE"),
    PROMOTION_PLAN_FEETYPE_7(7, "订单货款", "PROMOTION_PLAN_FEETYPE"),

    PROMOTION_PLAN_PROMOTION_0(0, "全部号码参与", "PROMOTION_PLAN_PROMOTION"),
    PROMOTION_PLAN_PROMOTION_1(1, "销售价格段号码参与", "PROMOTION_PLAN_PROMOTION"),
    PROMOTION_PLAN_PROMOTION_2(2, "指定号码参与", "PROMOTION_PLAN_PROMOTION"),

    PROMOTION_PLAN_AWARDWAY_1(1, "固定金额", "PROMOTION_PLAN_AWARDWAY"),
    PROMOTION_PLAN_AWARDWAY_2(2, "销售价比例", "PROMOTION_PLAN_AWARDWAY"),

    PROMOTION_PLAN_STATUS_1(1, "草稿", "PROMOTION_PLAN_STATUS"),
    PROMOTION_PLAN_STATUS_2(2, "有效", "PROMOTION_PLAN_STATUS"),
    PROMOTION_PLAN_STATUS_3(3, "失效", "PROMOTION_PLAN_STATUS"),
    PROMOTION_PLAN_STATUS_4(4, "过期", "PROMOTION_PLAN_STATUS"),
    PROMOTION_PLAN_STATUS_99(99, "删除", "PROMOTION_PLAN_STATUS"),

    /**是否合伙人*/
    CONSUMER_ISPARTNER_1(1, "是", "CONSUMER_ISPARTNER"),
    CONSUMER_ISPARTNER_0(0, "否", "CONSUMER_ISPARTNER"),
    /**已确认的合伙人*/
    CONSUMER_PARTNERCHECK_1(1, "是", "CONSUMER_PARTNERCHECK"),
    CONSUMER_PARTNERCHECK_0(0, "否", "CONSUMER_PARTNERCHECK"),

    /**文件上传存储的相对路径*/
    UPLOAD_PATH_IDCARD("idcard", "身份证", "UPLOAD_PATH"),
    UPLOAD_PATH_SHARE("share", "分享", "UPLOAD_PATH"),
    /**分享的资源*/
    SHARE_SOURCE_4(4, "号码详情", "SHARE_SOURCE"),
    SHARE_SOURCE_1(1, "首页", "SHARE_SOURCE"),
    /*号码浏览状态*/
    NUMBROWSE_ACTTYPE_1(1, "浏览", "NUMBROWSE_ACTTYPE"),
    NUMBROWSE_ACTTYPE_2(2, "购买", "NUMBROWSE_ACTTYPE"),

    /**订单结算状态*/
    ORDERSETTLE_STATUS_1(1, "待结算", "ORDERSETTLE_STATUS"),
    ORDERSETTLE_STATUS_2(2, "已结算", "ORDERSETTLE_STATUS"),
    ORDERSETTLE_STATUS_3(3, "过期失效", "ORDERSETTLE_STATUS"),


    ACCOUNT_TYPE_1(1, "银行卡", "ACCOUNT_TYPE"),
    ACCOUNT_TYPE_2(2, "微信", "ACCOUNT_TYPE"),

    /**支付流水状态**/
    PAY_SERIAL_STATUS_1(1, "初始", " PAY_SERIAL_STATUS"),
    PAY_SERIAL_STATUS_2(2, "支付中", " PAY_SERIAL_STATUS"),
    PAY_SERIAL_STATUS_3(3, "支付成功", " PAY_SERIAL_STATUS"),

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
