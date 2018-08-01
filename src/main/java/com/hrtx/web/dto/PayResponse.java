package com.hrtx.web.dto;

import net.sf.json.JSONObject;

public class PayResponse extends ThirdRequestResponse {
    private int resCode;
    private String resDesc;
    private Object data;

    public PayResponse(int resCode, String resDesc, Object data, String requestStr, String responseStr, String contentType, String interfaceName, String thirdType) {
        super(requestStr, responseStr, contentType, interfaceName, thirdType);
        this.resCode = resCode;
        this.resDesc = resDesc;
        this.data = data;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getResDesc() {
        return resDesc;
    }

    public void setResDesc(String resDesc) {
        this.resDesc = resDesc;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONObject.fromObject(this).toString();
    }

}
