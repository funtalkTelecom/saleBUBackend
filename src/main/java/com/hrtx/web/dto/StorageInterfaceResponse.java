package com.hrtx.web.dto;

import com.hrtx.global.MapJsonUtils;
import com.hrtx.global.StorageInterfaceUtils;
import com.hrtx.global.Utils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class StorageInterfaceResponse {
    private String merid;
    private String act_type;
    private String serial;
    private String sign;
    private String timestamp;
    private String code;
    private String desc;
    private Object platresponse;

    public StorageInterfaceResponse() {
    }

    public StorageInterfaceResponse(String merid, String actType, String serial, String code, String desc, String key, Object platresponse){
        this.merid = merid;
        this.act_type = actType;
        this.serial = serial;
        this.code = code;
        this.desc = desc;
        this.platresponse = platresponse == null ? new HashMap<String, String>() : platresponse;
        this.timestamp = Utils.getDate(0, "yyyyMMddHHmmss");
        if(StringUtils.isNotBlank(key)) {
            this.sign = StorageInterfaceUtils.getSign(this, key);
        }else {
            this.sign = "";
        }
    }

    public StorageInterfaceResponse(String jsonStr, String key) {
        Map jsonObject = MapJsonUtils.parseJSON2Map(jsonStr);
        this.merid = ObjectUtils.toString(jsonObject.get("merid"));
        this.act_type = ObjectUtils.toString(jsonObject.get("act_type"));
        this.serial = ObjectUtils.toString(jsonObject.get("serial"));
        this.timestamp = ObjectUtils.toString(jsonObject.get("timestamp"));
        this.sign = ObjectUtils.toString(jsonObject.get("sign"));
        this.code = ObjectUtils.toString(jsonObject.get("code"));
        this.desc = ObjectUtils.toString(jsonObject.get("desc"));
        this.platresponse = JSONObject.fromObject(jsonStr).get("platresponse");
        if("00000".equals(this.code) || "10000".equals(this.code)) {
            String localSign = StorageInterfaceUtils.getSign(this, key);
            if(!localSign.equals(this.sign)) {
                this.code = "C0005";
                this.desc = "签名出错";
            }
        }
        this.platresponse = jsonObject.get("platresponse");
    }

    public static StorageInterfaceResponse create(String jsonStr, String key) {
        return new StorageInterfaceResponse(jsonStr, key);
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


    public String getCode() {
        return code;
    }


    public String getDesc() {
        return desc;
    }


    public Object getPlatresponse() {
        return platresponse;
    }


    @Override
    public String toString() {
        return JSONObject.fromObject(this).toString();
    }

}
