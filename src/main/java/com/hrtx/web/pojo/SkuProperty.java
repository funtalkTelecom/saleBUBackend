package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_sku_property")
public class SkuProperty extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer skupId;
    private Integer skuId;
    private Integer gId;
    private String skupKey;
    private String skupValue;
    private int seq;

    public SkuProperty() {
    }

    public SkuProperty(Integer skupId, Integer skuId, Integer gId, String skupKey, String skupValue, int seq) {
        this.skupId = skupId;
        this.skuId = skuId;
        this.gId = gId;
        this.skupKey = skupKey;
        this.skupValue = skupValue;
        this.seq = seq;
    }

    public Integer getSkupId() {
        return skupId;
    }

    public void setSkupId(Integer skupId) {
        this.skupId = skupId;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Integer getgId() {
        return gId;
    }

    public void setgId(Integer gId) {
        this.gId = gId;
    }

    public String getSkupKey() {
        return skupKey;
    }

    public void setSkupKey(String skupKey) {
        this.skupKey = skupKey;
    }

    public String getSkupValue() {
        return skupValue;
    }

    public void setSkupValue(String skupValue) {
        this.skupValue = skupValue;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}