package com.hrtx.web.pojo;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
@Table(name = "tb_promotion_plan")
public class PromotionPlan extends BasePojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer corpId;
    private Integer feeType;
    private Integer promotion;
    private Integer awardWay;
    private BigDecimal award;
    private Integer isLimit;
    private BigDecimal limitAward;
    private BigDecimal beginPrice;
    private BigDecimal endPrice;
    private Date beginDate;
    private Date endDate;
    private Integer status;
    private Integer addUser;
    private Date addDate;
    private Integer updateUser;
    private Date updateDate;
    @Transient
    protected String num;
    @Transient
    protected String addUserStr;
    @Transient
    protected String updateUserStr;

    public PromotionPlan(Integer promotion, Integer awardWay, double award, Integer isLimit, double limitAward, double beginPrice, double endPrice, Date beginDate, Date endDate) {
        this.promotion = promotion;
        this.awardWay = awardWay;
        this.award = new BigDecimal(award);
        this.isLimit = isLimit;
        this.limitAward = new BigDecimal(limitAward);
        this.beginPrice = new BigDecimal(beginPrice);
        this.endPrice = new BigDecimal(endPrice);
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.addDate=new Date();
        this.updateDate=new Date();
    }

    public PromotionPlan(Integer id, Integer corpId, Integer feeType, Integer promotion, Integer awardWay, BigDecimal award, Integer isLimit, BigDecimal limitAward, BigDecimal beginPrice, BigDecimal endPrice, Date beginDate, Date endDate, Integer status, Integer addUser, Date addDate, Integer updateUser, Date updateDate) {
        this.id = id;
        this.corpId = corpId;
        this.feeType = feeType;
        this.promotion = promotion;
        this.awardWay = awardWay;
        this.award = award;
        this.isLimit = isLimit;
        this.limitAward = limitAward;
        this.beginPrice = beginPrice;
        this.endPrice = endPrice;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.status = status;
        this.addUser = addUser;
        this.addDate = addDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
    }

    public PromotionPlan() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getFeeType() {
        return feeType;
    }

    public void setFeeType(Integer feeType) {
        this.feeType = feeType;
    }

    public Integer getPromotion() {
        return promotion;
    }

    public void setPromotion(Integer promotion) {
        this.promotion = promotion;
    }

    public Integer getAwardWay() {
        return awardWay;
    }

    public void setAwardWay(Integer awardWay) {
        this.awardWay = awardWay;
    }

    public BigDecimal getAward() {
        return award;
    }

    public void setAward(BigDecimal award) {
        this.award = award;
    }

    public Integer getIsLimit() {
        return isLimit;
    }

    public void setIsLimit(Integer isLimit) {
        this.isLimit = isLimit;
    }

    public BigDecimal getLimitAward() {
        return limitAward;
    }

    public void setLimitAward(BigDecimal limitAward) {
        this.limitAward = limitAward;
    }

    public BigDecimal getBeginPrice() {
        return beginPrice;
    }

    public void setBeginPrice(BigDecimal beginPrice) {
        this.beginPrice = beginPrice;
    }

    public BigDecimal getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(BigDecimal endPrice) {
        this.endPrice = endPrice;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAddUser() {
        return addUser;
    }

    public void setAddUser(Integer addUser) {
        this.addUser = addUser;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Integer getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Integer updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getAddUserStr() {
        return addUserStr;
    }

    public void setAddUserStr(String addUserStr) {
        this.addUserStr = addUserStr;
    }

    public String getUpdateUserStr() {
        return updateUserStr;
    }

    public void setUpdateUserStr(String updateUserStr) {
        this.updateUserStr = updateUserStr;
    }
}