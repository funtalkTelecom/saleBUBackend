package com.hrtx.web.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_num_base")
public class NumBase extends BasePojo {
    @Id
    private Integer id;

    private String cityCode;

    private String cityName;

    private String netType;

    private String numResource;

    private String numType;

    private String numLevel;

    private Double lowConsume;

    private Date importDate;

    private String importFile;

    private Integer sellerId;

    public NumBase(String cityCode, String cityName, String netType, String numResource, String numType, String numLevel, Double lowConsume, Date importDate, String importFile, Integer sellerId) {
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.netType = netType;
        this.numResource = numResource;
        this.numType = numType;
        this.numLevel = numLevel;
        this.lowConsume = lowConsume;
        this.importDate = importDate;
        this.importFile = importFile;
        this.sellerId = sellerId;
    }

    public NumBase() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode == null ? null : cityCode.trim();
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

    public Date getImportDate() {
        return importDate;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    public String getImportFile() {
        return importFile;
    }

    public void setImportFile(String importFile) {
        this.importFile = importFile == null ? null : importFile.trim();
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }
}