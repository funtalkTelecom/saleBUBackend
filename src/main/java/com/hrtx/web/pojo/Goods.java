package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Table(name = "tb_goods")
public class Goods extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
    private Integer gId;
    private String gType1;
    private String gType2;
    private String gName;
    private String gAd;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gEndTime;
    private String gSaleCity;
    private String gIsAuc;
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer gActive;
    private String gIsPack;
    private String gLoopTime;
    private String gDeposit;
    private String gStartNum;
    private String gPriceUp;
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer gSellerId;
    private String gSellerName;
    private String gIsSale;
    private Integer status;

    @Transient
    private String statusText;
    @Transient
    private String numResource;

    public Goods() {
    }

    public Goods(Integer gId, String gType1, String gType2, String gName, String gAd, Date gStartTime,
                 Date gEndTime, String gSaleCity, String gIsAuc, Integer gActive, String gIsPack, String gLoopTime,
                 String gDeposit, String gStartNum, String gPriceUp, Integer gSellerId, String gSellerName, String gIsSale,int status) {
        this.gId = gId;
        this.gType1 = gType1;
        this.gType2 = gType2;
        this.gName = gName;
        this.gAd = gAd;
        this.gStartTime = gStartTime;
        this.gEndTime = gEndTime;
        this.gSaleCity = gSaleCity;
        this.gIsAuc = gIsAuc;
        this.gActive = gActive;
        this.gIsPack = gIsPack;
        this.gLoopTime = gLoopTime;
        this.gDeposit = gDeposit;
        this.gStartNum = gStartNum;
        this.gPriceUp = gPriceUp;
        this.gSellerId = gSellerId;
        this.gSellerName = gSellerName;
        this.gIsSale = gIsSale;
        this.status = status;
    }

    public Integer getgId() {
        return gId;
    }

    public void setgId(Integer gId) {
        this.gId = gId;
    }

    public String getgType1() {
        return gType1;
    }

    public void setgType1(String gType1) {
        this.gType1 = gType1;
    }

    public String getgType2() {
        return gType2;
    }

    public void setgType2(String gType2) {
        this.gType2 = gType2;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public String getgAd() {
        return gAd;
    }

    public void setgAd(String gAd) {
        this.gAd = gAd;
    }

    public Date getgStartTime() {
        return gStartTime;
    }

    public void setgStartTime(Date gStartTime) {
        this.gStartTime = gStartTime;
    }

    public Date getgEndTime() {
        return gEndTime;
    }

    public void setgEndTime(Date gEndTime) {
        this.gEndTime = gEndTime;
    }

    public String getgSaleCity() {
        return gSaleCity;
    }

    public void setgSaleCity(String gSaleCity) {
        this.gSaleCity = gSaleCity;
    }

    public String getgIsAuc() {
        return gIsAuc;
    }

    public void setgIsAuc(String gIsAuc) {
        this.gIsAuc = gIsAuc;
    }

    public Integer getgActive() {
        return gActive;
    }

    public void setgActive(Integer gActive) {
        this.gActive = gActive;
    }

    public String getgIsPack() {
        return gIsPack;
    }

    public void setgIsPack(String gIsPack) {
        this.gIsPack = gIsPack;
    }

    public String getgLoopTime() {
        return gLoopTime;
    }

    public void setgLoopTime(String gLoopTime) {
        this.gLoopTime = gLoopTime;
    }

    public String getgDeposit() {
        return gDeposit;
    }

    public void setgDeposit(String gDeposit) {
        this.gDeposit = gDeposit;
    }

    public String getgStartNum() {
        return gStartNum;
    }

    public void setgStartNum(String gStartNum) {
        this.gStartNum = gStartNum;
    }

    public String getgPriceUp() {
        return gPriceUp;
    }

    public void setgPriceUp(String gPriceUp) {
        this.gPriceUp = gPriceUp;
    }

    public Integer getgSellerId() {
        return gSellerId;
    }

    public void setgSellerId(Integer gSellerId) {
        this.gSellerId = gSellerId;
    }

    public String getgSellerName() {
        return gSellerName;
    }

    public void setgSellerName(String gSellerName) {
        this.gSellerName = gSellerName;
    }

    public String getgIsSale() {
        return gIsSale;
    }

    public void setgIsSale(String gIsSale) {
        this.gIsSale = gIsSale;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getNumResource() {
        return numResource;
    }

    public void setNumResource(String numResource) {
        this.numResource = numResource;
    }
}