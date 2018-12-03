package com.hrtx.global;

public class RegexConsts {
    public static final String REGEX_MOBILE_COMMON = "^((13)|(14)|(15)|(16)|(17)|(18)|(19))\\d{9}$";
    public static final String REGEX_MOBILE_DX = "^((133)|(153)|(17[0,3,7])|(18[0,1,9])|(19[0-9]{1}))\\d{8}$";
    public static final String REGEX_MOBILE_YD = "^((13[4-9])|(147)|(15[0-2,7-9])|(18[2-3,7-8]))\\d{8}$";
    public static final String REGEX_MOBILE_LT = "^((13[0-2])|(145)|(15[5-6])|(18[5-6]))\\d{8}$";
//    序列号格式：商户号(4位)+业务类型见附录5.4 (4位) +YYYYMMDDHHMISS(14位)+递增序列(6位)
    public static final String REGEX_THIRD_ORDER = "^[a-zA-Z0-9]{4}(0101|0102|0103)\\d{20}$";
}
