package com.hrtx.global.pinganUtils;

//~--- non-JDK imports --------------------------------------------------------

import com.hrtx.web.pay.YzfPayStrategy;
import net.sf.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Class TLinx2Util
 * Description
 * Create 2017-03-07 14:01:23
 * @author Benny.YEE
 */
public class YzffqUtil {



    /**
     * 签名
     * @param postMap
     * @return
     */
    public static String sign(Map<String, String> postMap) {
        String sortStr = null;
        String sign    = null;

        try {

            /**
             * 1 A~z排序(加上open_key)
             */
            sortStr = TLinxUtil.sort(postMap);

            /**
             * 2 sha1加密(小写)
             */
            String sha1 = TLinxSHA1.SHA1(sortStr);

            /**
             * 3 md5加密(小写)
             */
            sign = MD5.MD5Encode(sha1).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sign;
    }
    
    public static String signRSA(Map<String, String> postMap, String privateKey) {
        String sortStr = null;
        String sign    = null;

        try {

            /**
             * 1 A~z排序(加上open_key)
             */
            sortStr = TLinxUtil.sort(postMap);
            sign = TLinxRSACoder.sign(sortStr.getBytes("utf-8"), privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sign;
    }
    /**
     * 验签
     * @param respObject
     * @return
     */
    public static Boolean verifySign(JSONObject respObject) {
        String respSign = respObject.get("sign").toString();

        respObject.remove("sign");    // 删除sign节点
        respObject.put("open_key",YzfPayStrategy.getOpenKey());

        String veriSign = sign(respObject);    // 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名

        if (respSign.equals(veriSign)) {
            System.out.println("=====验签成功=====");

            return true;
        } else {
            System.out.println("=====验签失败=====");
        }

        return false;
    }

    /**
     * 验签
     * @param respObject
     * @return
     */
    public static Boolean verifySign(Map respObject) {
        String respSign = respObject.get("sign").toString();

        respObject.remove("sign");    // 删除sign节点
        respObject.put("open_key",YzfPayStrategy.getOpenKey());

        String veriSign = sign(respObject);    // 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名

        if (respSign.equals(veriSign)) {
            System.out.println("=====验签成功=====");
            return true;
        } else {
            System.out.println("=====验签失败=====");
        }
        return false;
    }

    /**
     * AES加密，再二进制转十六进制(bin2hex)
     * @param postmap 说明：
     * @throws Exception
     */
    public static void handleEncrypt(TreeMap<String, String> datamap, TreeMap<String, String> postmap) throws Exception {

        JSONObject dataobj = JSONObject.fromObject(datamap);
        String data    = TLinxAESCoder.encrypt(dataobj.toString(),YzfPayStrategy.getOpenKey());    // AES加密，并bin2hex

        postmap.put("data", data);
    }

    /**
     * 签名
     * @param postmap
     */
    public static void handleSign(TreeMap<String, String> postmap) {
        Map<String, String> veriDataMap = new HashMap<String, String>();

        veriDataMap.putAll(postmap);
        veriDataMap.put("open_key",YzfPayStrategy.getOpenKey());

        // 签名
        String sign = sign(veriDataMap);

        postmap.put("sign", sign);
    }
    
    /**
     * 签名
     * @param postmap
     */
    public static void handleSignRSA(TreeMap<String, String> postmap, String privateKey) {
        Map<String, String> veriDataMap = new HashMap<String, String>();

        veriDataMap.putAll(postmap);
        veriDataMap.put("open_key",YzfPayStrategy.getOpenKey());

        // 签名
        String sign = signRSA(veriDataMap, privateKey);

        postmap.put("sign", sign);
    }
    

    /**
     * 请求接口
     * @param postmap
     * @return      响应字符串
     */
    public static String handlePost(TreeMap<String, String> postmap, String interfaceName) {
        String url = YzfPayStrategy.getOpenUrl() + interfaceName;

        if (url.contains("https")) {
            return HttpsUtil.httpMethodPost(url, postmap, "UTF-8");
        } else {
            return HttpUtil.httpMethodPost(url, postmap, "UTF-8");
        }
    }

}

