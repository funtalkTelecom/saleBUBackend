package com.hrtx.global;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * CryptTool 封装了一些加密工具方法.
 *
 */
public class CryptTool {

    public CryptTool() {
    }

    public final static String MD5(String s) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes("UTF-8"));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void main(String[] args) {
        //000110011059110010001201606170917570000011895917759159111000873247754746E4145
        System.out.print(MD5("110"));
    }

    public static String getSign(Map<String, Object> mapParam, String key) {
        mapParam.remove("sign");
        mapParam.remove("data");
        String[] platRequestKeys = CryptTool.properties(mapParam);
        StringBuffer sign= new StringBuffer();
        for (String string : platRequestKeys) {
            sign.append(mapParam.get(string));
        }
        sign.append(key);
        System.out.println("获取到sign_key:["+StringUtils.join(platRequestKeys,"|")+"]");
        System.out.println("获取到明文sign:["+sign.toString()+"]");
        return CryptTool.MD5(sign.toString());
    }

    /**
     * 获取bean的所有属性并按各个参数名的每一个值从a到z的顺序排序，若遇到相同首字母，则看第二个字母排序,以此类推
     * @return
     */
    public static String[] properties(Map<String, Object> map){
        List<String> list= new ArrayList<String>();
        Iterator<String> it=map.keySet().iterator();
        while(it.hasNext()){
            list.add(it.next());
        }
        String[] array=list.toArray(new String[list.size()]);
        //严格按字母表顺序排序，也就是忽略大小写排序 Case-insensitive sort
        Arrays.sort(array,String.CASE_INSENSITIVE_ORDER); //排序:每一个值从a到z的顺序排序，若遇到相同首字母，则看第二个字母排序,以此类推
        return array;
    }
}
