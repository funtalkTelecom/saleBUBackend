package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "tb_num")
public class Num extends BasePojo  implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer cityId;
    private String cityName;
    private String netType;
    private String numResource;
    private String numType;
    private String numLevel;
    private Boolean with4;
    private String feature;
    private String sectionNo;
    private String moreDigit;
    private Integer sellerId;
    private String seller;
    private Integer buyerId;
    private BigDecimal lowConsume;
    private String buyer;
    private Integer iccidId;
    private String iccid;
    private Integer mealMid;
    private String slReason;
    private String uploadFileName;
    private Integer isFreeze;
    private Integer skuId;
    private String teleType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private Integer status; //1在库、2销售中、3冻结(下单未付款)、4待配卡(已付款 针对2C或电销无需购买卡时、代理商买号而未指定白卡时)、5待受理(代理商已提交或仓库已发货，待提交乐语BOSS)、6已受理(乐语BOSS处理成功)、7受理失败(BOSS受理失败，需要人介入解决)、8已失效(乐语BOSS提示号码已非可用)

    public Num(Integer id, Integer cityId, String cityName, String netType, String numResource, String numType,
               String numLevel, BigDecimal lowConsume, Boolean with4, String feature, String sectionNo, String moreDigit,
               Integer sellerId, String seller, Integer buyerId, String buyer, String iccid, Integer status,Integer mealMid) {
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
        this.mealMid=mealMid;
    }

    public Num() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType == null ? null : netType.trim();
    }

    public String getNumResource() {
        return numResource;
    }

    public void setNumResource(String numResource) {
        this.numResource = numResource == null ? null : numResource.trim();
    }

    public String getNumType() {
        return numType;
    }

    public void setNumType(String numType) {
        this.numType = numType == null ? null : numType.trim();
    }

    public String getNumLevel() {
        return numLevel;
    }

    public void setNumLevel(String numLevel) {
        this.numLevel = numLevel == null ? null : numLevel.trim();
    }

    public Double getLowConsume() {
        return lowConsume==null?null:lowConsume.doubleValue();
    }

    public Boolean getWith4() {
        return with4;
    }

    public void setWith4(Boolean with4) {
        this.with4 = with4;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public String getSectionNo() {
        return sectionNo;
    }

    public void setSectionNo(String sectionNo) {
        this.sectionNo = sectionNo == null ? null : sectionNo.trim();
    }

    public String getMoreDigit() {
        return moreDigit;
    }

    public void setMoreDigit(String moreDigit) {
        this.moreDigit = moreDigit == null ? null : moreDigit.trim();
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller == null ? null : seller.trim();
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer == null ? null : buyer.trim();
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid == null ? null : iccid.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIccidId() {
        return iccidId;
    }

    public void setIccidId(Integer iccidId) {
        this.iccidId = iccidId;
    }

    public Integer getMealMid() { return mealMid; }

    public void setMealMid(Integer mealMid) { this.mealMid = mealMid; }

    public String getSlReason() {
        return slReason;
    }

    public void setSlReason(String slReason) {
        this.slReason = slReason;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public Integer getIsFreeze() {
        return isFreeze;
    }

    public void setIsFreeze(Integer isFreeze) {
        this.isFreeze = isFreeze;
    }

    public void setLowConsume(BigDecimal lowConsume) {
        this.lowConsume = lowConsume;
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

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public String getTeleType() {
        return teleType;
    }

    public void setTeleType(String teleType) {
        this.teleType = teleType;
    }
}