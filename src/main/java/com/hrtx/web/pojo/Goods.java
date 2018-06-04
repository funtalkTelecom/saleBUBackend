package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_goods")
public class Goods extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
    private Long gId;
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
    private String gActive;
    private String gIsPack;

    public Goods() {
    }

    public Goods(Long gId, String gType1, String gType2, String gName, String gAd, Date gStartTime, Date gEndTime, String gSaleCity, String gIsAuc, String gActive, String gIsPack) {
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
    }

    public Long getgId() {
        return gId;
    }

    public void setgId(Long gId) {
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

    public String getgActive() {
        return gActive;
    }

    public void setgActive(String gActive) {
        this.gActive = gActive;
    }

    public String getgIsPack() {
        return gIsPack;
    }

    public void setgIsPack(String gIsPack) {
        this.gIsPack = gIsPack;
    }
}