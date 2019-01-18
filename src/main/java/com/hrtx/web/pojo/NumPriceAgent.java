package com.hrtx.web.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
@Table(name = "tb_num_price_agent")
public class NumPriceAgent implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer skuId;

    private Integer numId;

    private Integer provinceCode;

    private String provinceName;

    private Integer cityCode;

    private String cityName;

    private String resource;

    private BigDecimal basePrice;

    private String netType;

    private String feature;

    private BigDecimal lowConsume;

    private Integer corpId;

    private Integer channel;

    private BigDecimal ratioPrice;

    private BigDecimal price;

    private String agent;

    private Integer agentId;

    private Date addDate;

    private Integer with4;

    private Integer isFreeze;

    private String numLevel;

    private BigDecimal skuTocPrice;

    private BigDecimal skuTobPrice;

    private String numTags;

    private Integer status;

    private Integer excPrice;

    private Integer activityId;

    private Integer activityType;

    private Date activitySdate;

    private Date activityEdate;

    private Double activityPrice;


    public NumPriceAgent(Integer id, Integer skuId, Integer numId, Integer provinceCode, String provinceName, Integer cityCode, String cityName, String resource, BigDecimal basePrice, String netType, String feature, BigDecimal lowConsume, Integer corpId, Integer channel, BigDecimal ratioPrice, BigDecimal price, String agent, Integer agentId, Date addDate, Integer with4, Integer isFreeze, String numLevel, BigDecimal skuTocPrice, BigDecimal skuTobPrice, String numTags, Integer status, Integer excPrice) {
        this.id = id;
        this.skuId = skuId;
        this.numId = numId;
        this.provinceCode = provinceCode;
        this.provinceName = provinceName;
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.resource = resource;
        this.basePrice = basePrice;
        this.netType = netType;
        this.feature = feature;
        this.lowConsume = lowConsume;
        this.corpId = corpId;
        this.channel = channel;
        this.ratioPrice = ratioPrice;
        this.price = price;
        this.agent = agent;
        this.agentId = agentId;
        this.addDate = addDate;
        this.with4 = with4;
        this.isFreeze = isFreeze;
        this.numLevel = numLevel;
        this.skuTocPrice = skuTocPrice;
        this.skuTobPrice = skuTobPrice;
        this.numTags = numTags;
        this.status = status;
        this.excPrice = excPrice;
    }

    public NumPriceAgent() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Integer getNumId() {
        return numId;
    }

    public void setNumId(Integer numId) {
        this.numId = numId;
    }

    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName == null ? null : provinceName.trim();
    }

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource == null ? null : resource.trim();
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType == null ? null : netType.trim();
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public BigDecimal getLowConsume() {
        return lowConsume;
    }

    public void setLowConsume(BigDecimal lowConsume) {
        this.lowConsume = lowConsume;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public BigDecimal getRatioPrice() {
        return ratioPrice;
    }

    public void setRatioPrice(BigDecimal ratioPrice) {
        this.ratioPrice = ratioPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent == null ? null : agent.trim();
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Integer getWith4() {
        return with4;
    }

    public void setWith4(Integer with4) {
        this.with4 = with4;
    }

    public Integer getIsFreeze() {
        return isFreeze;
    }

    public void setIsFreeze(Integer isFreeze) {
        this.isFreeze = isFreeze;
    }

    public String getNumLevel() {
        return numLevel;
    }

    public void setNumLevel(String numLevel) {
        this.numLevel = numLevel == null ? null : numLevel.trim();
    }

    public BigDecimal getSkuTocPrice() {
        return skuTocPrice;
    }

    public void setSkuTocPrice(BigDecimal skuTocPrice) {
        this.skuTocPrice = skuTocPrice;
    }

    public BigDecimal getSkuTobPrice() {
        return skuTobPrice;
    }

    public void setSkuTobPrice(BigDecimal skuTobPrice) {
        this.skuTobPrice = skuTobPrice;
    }

    public String getNumTags() {
        return numTags;
    }

    public void setNumTags(String numTags) {
        this.numTags = numTags == null ? null : numTags.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getExcPrice() {
        return excPrice;
    }

    public void setExcPrice(Integer excPrice) {
        this.excPrice = excPrice;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getActivityType() {
        return activityType;
    }

    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
    }

    public Date getActivitySdate() {
        return activitySdate;
    }

    public void setActivitySdate(Date activitySdate) {
        this.activitySdate = activitySdate;
    }

    public Date getActivityEdate() {
        return activityEdate;
    }

    public void setActivityEdate(Date activityEdate) {
        this.activityEdate = activityEdate;
    }

    public Double getActivityPrice() {
        return activityPrice;
    }

    public void setActivityPrice(Double activityPrice) {
        this.activityPrice = activityPrice;
    }
}