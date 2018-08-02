package com.hrtx.web.dto;

import net.sf.json.JSONObject;

public class ThirdRequestResponse {
    private String requestStr;
    private String responseStr;
    private String contentType;
    private String interfaceName;
    private String thirdType;

    public ThirdRequestResponse(String requestStr, String responseStr, String contentType, String interfaceName, String thirdType) {
        this.requestStr = requestStr;
        this.responseStr = responseStr;
        this.contentType = contentType;
        this.interfaceName = interfaceName;
        this.thirdType = thirdType;
    }

    public String getRequestStr() {
        return requestStr;
    }

    public void setRequestStr(String requestStr) {
        this.requestStr = requestStr;
    }

    public String getResponseStr() {
        return responseStr;
    }

    public void setResponseStr(String responseStr) {
        this.responseStr = responseStr;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getThirdType() {
        return thirdType;
    }

    public void setThirdType(String thirdType) {
        this.thirdType = thirdType;
    }

    @Override
    public String toString() {
        return JSONObject.fromObject(this).toString();
    }

}
