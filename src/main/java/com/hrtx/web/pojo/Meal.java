package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Table(name = "tb_meal")
public class Meal extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long mid;
	private String mealId;
	private String mealName;
	private String mealDesc;
    private Long saleCity;
    private String saleCityName;
	private String saleType;
	private Long createBy;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date createDate;
	private Long updateBy;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date updateDate;
	private String teleType;
	private Integer isDel;

	public Meal() {
	}

    public Meal(Long mid, String mealId, String mealName, String mealDesc, Long saleCity, String saleCityName, String saleType, Long createBy, Date createDate, Long updateBy, Date updateDate, String teleType, Integer isDel) {
        this.mid = mid;
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealDesc = mealDesc;
        this.saleCity = saleCity;
        this.saleCityName = saleCityName;
        this.saleType = saleType;
        this.createBy = createBy;
        this.createDate = createDate;
        this.updateBy = updateBy;
        this.updateDate = updateDate;
        this.teleType = teleType;
        this.isDel = isDel;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealDesc() {
        return mealDesc;
    }

    public void setMealDesc(String mealDesc) {
        this.mealDesc = mealDesc;
    }

    public Long getSaleCity() {
        return saleCity;
    }

    public void setSaleCity(Long saleCity) {
        this.saleCity = saleCity;
    }

    public String getSaleCityName() {
        return saleCityName;
    }

    public void setSaleCityName(String saleCityName) {
        this.saleCityName = saleCityName;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getTeleType() {
        return teleType;
    }

    public void setTeleType(String teleType) {
        this.teleType = teleType;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }
}