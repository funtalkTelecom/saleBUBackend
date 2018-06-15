package com.hrtx.dto;
import java.util.HashMap;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import com.hrtx.global.StorageInterfaceUtils;
import com.hrtx.global.Utils;

public class StorageInterfaceRequest {
    private String merid;
    private String act_type;
    private String serial;
    private String sign;
    private String timestamp;
    private Object platrequest;

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
