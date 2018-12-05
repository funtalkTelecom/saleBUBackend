package com.hrtx.web.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
@Table(name = "tb_num")
public class Num extends BasePojo{
    @Id
    private Long id;

    private Long cityId;

    private String cityName;

    private String netType;

    private String numResource;

    private String numType;

    private String numLevel;

    private Double lowConsume;

    private Boolean with4;

    private String feature;

    private String sectionNo;

    private String moreDigit;

    private Long sellerId;

    private String seller;

    private Long buyerId;

    private String buyer;

    private Long iccidId;
    private String iccid;
    private Long mealMid;
    private String slReason;
    private String uploadFileName;
    private Integer isFreeze;


    private Integer status; //1在库、2销售中、3冻结(下单未付款)、4待配卡(已付款 针对2C或电销无需购买卡时、代理商买号而未指定白卡时)、5待受理(代理商已提交或仓库已发货，待提交乐语BOSS)、6已受理(乐语BOSS处理成功)、7受理失败(BOSS受理失败，需要人介入解决)、8已失效(乐语BOSS提示号码已非可用)

    public Num(Long id, Long cityId, String cityName, String netType, String numResource, String numType, String numLevel, Double lowConsume, Boolean with4, String feature, String sectionNo, String moreDigit, Long sellerId, String seller, Long buyerId, String buyer, String iccid, Integer status,Long mealMid) {
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
        return lowConsume;
    }

    public void setLowConsume(Double lowConsume) {
        this.lowConsume = lowConsume;
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
        this.seller = seller == null ? null : seller.trim();
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

    public Long getIccidId() {
        return iccidId;
    }

    public void setIccidId(Long iccidId) {
        this.iccidId = iccidId;
    }

    public Long getMealMid() { return mealMid; }

    public void setMealMid(Long mealMid) { this.mealMid = mealMid; }

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
}