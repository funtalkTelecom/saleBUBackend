package com.hrtx.dto;
import java.util.HashMap;
import java.util.Map;

import com.hrtx.config.advice.ServiceException;
import com.hrtx.global.MapJsonUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.hrtx.global.StorageInterfaceUtils;
import com.hrtx.global.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageInterfaceRequest {
    public final Logger log = LoggerFactory.getLogger(this.getClass());

    private String merid;
    private String act_type;
    private String serial;
    private String sign;
    private String timestamp;
    private Object platrequest;

    public StorageInterfaceRequest(){}

    public StorageInterfaceRequest(String merid, String actType, String serial, String key, Object platrequest){
        this.merid = merid;
        this.act_type = actType;
        this.serial = serial;
        this.platrequest = platrequest == null ? new HashMap<String, String>() : platrequest;
        this.timestamp = Utils.getDate(0, "yyyyMMddHHmmss");
        if(StringUtils.isNotBlank(key)) {
            this.sign = StorageInterfaceUtils.getSign(this, key);
        }else {
            this.sign = "";
        }
    }

    public StorageInterfaceRequest(String jsonStr, String key) {
        try{
            Map jsonObject = MapJsonUtils.parseJSON2Map(jsonStr);
            this.merid = ObjectUtils.toString(jsonObject.get("merid"));
            this.act_type = ObjectUtils.toString(jsonObject.get("act_type"));
            this.serial = ObjectUtils.toString(jsonObject.get("serial"));
            this.timestamp = ObjectUtils.toString(jsonObject.get("timestamp"));
            this.sign = ObjectUtils.toString(jsonObject.get("sign"));
            this.platrequest = JSONObject.fromObject(jsonStr).get("platrequest");
            String localSign = StorageInterfaceUtils.getSign(this, key);
            if(!localSign.equals(this.sign)) throw new ServiceException("签名出错");
            this.platrequest = jsonObject.get("platrequest");
        }catch (ServiceException e) {
            throw e;
        }catch (Exception e) {
            log.error("参数解析异常，请检查参数格式是否正确", e);
            throw new ServiceException("参数解析异常，请检查参数格式是否正确");
        }

    }

    public static StorageInterfaceRequest create(String jsonStr, String key) {
        return new StorageInterfaceRequest(jsonStr, key);
    }


    public String getMerid() {
        return merid;
    }


    public String getAct_type() {
        return act_type;
    }


    public String getSerial() {
        return serial;
    }


    public String getSign() {
        return sign;
    }


    public String getTimestamp() {
        return timestamp;
    }


    public Object getPlatrequest() {
        return platrequest;
    }

    @Override
    public String toString() {
        return JSONObject.fromObject(this).toString();
    }

}
