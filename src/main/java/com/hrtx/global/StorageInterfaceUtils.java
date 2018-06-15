package com.hrtx.global;

import com.hrtx.global.pinganUtils.MD5;
import com.hrtx.global.pinganUtils.TLinxUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageInterfaceUtils {
    private static Logger log = LoggerFactory.getLogger(StorageInterfaceUtils.class);
    public static String getSign(Object object, String key){
        JSONObject json=JSONObject.fromObject(object);
        json.remove("sign");
        String stra="";
        try {
            stra = TLinxUtil.sort(json);
        } catch (Exception e) {
            log.error("加密排序异常", e);
        }
        stra+="&key="+key;
        return MD5.MD5Encode(stra).toUpperCase();
    }

}

