package com.hrtx.web.dto;

import net.sf.json.JSONObject;

public class PayRequest {
    private Integer amt;
    private String payee;
    private String payer;
    private String serial;
    private String orderNo;
    private String thirdNo;
    private String orderName;
    private String third;
    private Integer actualAmt;
    private String remark;
    private String beforeUrl;
    private String afterUrl;

    public PayRequest(Integer amt, String payee, String payer, String serial, String orderNo, String thirdNo, String orderName, String third, Integer actualAmt, String remark, String beforeUrl, String afterUrl) {
        this.amt = amt;
        this.payee = payee;
        this.payer = payer;
        this.serial = serial;
        this.orderNo = orderNo;
        this.thirdNo = thirdNo;
        this.orderName = orderName;
        this.third = third;
        this.actualAmt = actualAmt;
        this.remark = remark;
        this.beforeUrl = beforeUrl;
        this.afterUrl = afterUrl;
    }

    public String getBeforeUrl() {
        return beforeUrl;
    }

    public void setBeforeUrl(String beforeUrl) {
        this.beforeUrl = beforeUrl;
    }

    public String getAfterUrl() {
        return afterUrl;
    }

    public void setAfterUrl(String afterUrl) {
        this.afterUrl = afterUrl;
    }

    public String getThirdNo() {
        return thirdNo;
    }

    public void setThirdNo(String thirdNo) {
        this.thirdNo = thirdNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getAmt() {
        return amt;
    }

    public void setAmt(Integer amt) {
        this.amt = amt;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getThird() {
        return third;
    }

    public void setThird(String third) {
        this.third = third;
    }

    public Integer getActualAmt() {
        return actualAmt;
    }

    public void setActualAmt(Integer actualAmt) {
        this.actualAmt = actualAmt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return JSONObject.fromObject(this).toString();
    }

}
