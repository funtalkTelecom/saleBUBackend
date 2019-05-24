package com.hrtx.web.pojo;

import com.hrtx.global.Constants;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_pay_serial")
public class PaySerial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer amt;

    private String payer;

    private Integer status;

    private String orderName;

    private String beforeUrl;

    private String afterUrl;

    private String payTradeType;

    private String subAppId;

    private String subOpenId;

    private Date addDate;

    private Integer orderTradeType;

    private Integer payType;

    private String merParam;

    public PaySerial(Integer amt, String payer, String orderName, String beforeUrl, String afterUrl, String payTradeType, String subAppId, String subOpenId, Integer orderTradeType, Integer payType, String merParam) {
        this.amt = amt;
        this.payer = payer;
        this.status = Constants.PAY_SERIAL_STATUS_2.getIntKey();
        this.orderName = orderName;
        this.beforeUrl = beforeUrl;
        this.afterUrl = afterUrl;
        this.payTradeType = payTradeType;
        this.subAppId = subAppId;
        this.subOpenId = subOpenId;
        this.addDate = new Date();
        this.orderTradeType = orderTradeType;
        this.payType = payType;
        this.merParam = merParam;
    }

    public PaySerial() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAmt() {
        return amt;
    }

    public void setAmt(Integer amt) {
        this.amt = amt;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer == null ? null : payer.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName == null ? null : orderName.trim();
    }

    public String getBeforeUrl() {
        return beforeUrl;
    }

    public void setBeforeUrl(String beforeUrl) {
        this.beforeUrl = beforeUrl == null ? null : beforeUrl.trim();
    }

    public String getAfterUrl() {
        return afterUrl;
    }

    public void setAfterUrl(String afterUrl) {
        this.afterUrl = afterUrl == null ? null : afterUrl.trim();
    }

    public String getPayTradeType() {
        return payTradeType;
    }

    public void setPayTradeType(String payTradeType) {
        this.payTradeType = payTradeType == null ? null : payTradeType.trim();
    }

    public String getSubAppId() {
        return subAppId;
    }

    public void setSubAppId(String subAppId) {
        this.subAppId = subAppId == null ? null : subAppId.trim();
    }

    public String getSubOpenId() {
        return subOpenId;
    }

    public void setSubOpenId(String subOpenId) {
        this.subOpenId = subOpenId == null ? null : subOpenId.trim();
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Integer getOrderTradeType() {
        return orderTradeType;
    }

    public void setOrderTradeType(Integer orderTradeType) {
        this.orderTradeType = orderTradeType;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getMerParam() {
        return merParam;
    }

    public void setMerParam(String merParam) {
        this.merParam = merParam == null ? null : merParam.trim();
    }
}