package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "tb_num")
public class Number extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Long cityId;
    private String cityName;
    private String netType;
    private String numResource;
    private String numType;
    private String numLevel;
    private BigDecimal lowConsume;
    private int with4;
    private String feature;
    private String sectionNo;
    private String moreDigit;
    private Long sellerId;
    private String seller;
    private Long buyerId;
    private String buyer;
    private String iccid;
    private int status;
    private Long skuId;
    private String teleType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    public Number() {
    }

    public Number(Long id, Long cityId, String cityName, String netType, String numResource, String numType, String numLevel, BigDecimal lowConsume, int with4, String feature, String sectionNo, String moreDigit, Long sellerId, String seller, Long buyerId, String buyer, String iccid, int status, Long skuId, String teleType, Date startTime, Date endTime) {
        this.id = id;
        this.cityId = cityId;
        this.cityName = cityName;
        this.netType = netType;
        this.numResource = numResource;
        this.numType = numType;
        this.numLevel = numLevel;
        this.lowConsume = lowConsume;
        this.with4 = with4;
        this.feature = feature;
        this.sectionNo = sectionNo;
        this.moreDigit = moreDigit;
        this.sellerId = sellerId;
        this.seller = seller;
        this.buyerId = buyerId;
        this.buyer = buyer;
        this.iccid = iccid;
        this.status = status;
        this.skuId = skuId;
        this.teleType = teleType;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getNumResource() {
        return numResource;
    }

    public void setNumResource(String numResource) {
        this.numResource = numResource;
    }

    public String getNumType() {
        return numType;
    }

    public void setNumType(String numType) {
        this.numType = numType;
    }

    public String getNumLevel() {
        return numLevel;
    }

    public void setNumLevel(String numLevel) {
        this.numLevel = numLevel;
    }

    public BigDecimal getLowConsume() {
        return lowConsume;
    }

    public void setLowConsume(BigDecimal lowConsume) {
        this.lowConsume = lowConsume;
    }

    public int getWith4() {
        return with4;
    }

    public void setWith4(int with4) {
        this.with4 = with4;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getSectionNo() {
        return sectionNo;
    }

    public void setSectionNo(String sectionNo) {
        this.sectionNo = sectionNo;
    }

    public String getMoreDigit() {
        return moreDigit;
    }

    public void setMoreDigit(String moreDigit) {
        this.moreDigit = moreDigit;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getTeleType() {
        return teleType;
    }

    public void setTeleType(String teleType) {
        this.teleType = teleType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}