package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Table(name = "tb_sku")
public class Sku extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer skuId;
    private Integer gId;
    private Double skuTobPrice;
    private Double skuTocPrice;
    private String skuIsNum;
    private String skuSaleNum;
    private int skuNum;
    private String skuGoodsType;
    private String skuRepoGoods;
    private String skuRepoGoodsName;

    private int status;
    private Integer isDel;

    private String statusText;

    public Sku() {
    }

    public Sku(Integer skuId, Integer gId, Double skuTobPrice, Double skuTocPrice, String skuIsNum,
               String skuSaleNum, int skuNum, String skuGoodsType, String skuRepoGoods,
               String skuRepoGoodsName,int status,Integer isDel,String statusText) {
        this.skuId = skuId;
        this.gId = gId;
        this.skuTobPrice = skuTobPrice;
        this.skuTocPrice = skuTocPrice;
        this.skuIsNum = skuIsNum;
        this.skuSaleNum = skuSaleNum;
        this.skuNum = skuNum;
        this.skuGoodsType = skuGoodsType;
        this.skuRepoGoods = skuRepoGoods;
        this.skuRepoGoodsName = skuRepoGoodsName;
        this.status = status;
        this.isDel = isDel;
        this.statusText = statusText;
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

    public Double getSkuTobPrice() {
        return skuTobPrice;
    }

    public void setSkuTobPrice(Double skuTobPrice) {
        this.skuTobPrice = skuTobPrice;
    }

    public Double getSkuTocPrice() {
        return skuTocPrice;
    }

    public void setSkuTocPrice(Double skuTocPrice) {
        this.skuTocPrice = skuTocPrice;
    }

    public String getSkuIsNum() {
        return skuIsNum;
    }

    public void setSkuIsNum(String skuIsNum) {
        this.skuIsNum = skuIsNum;
    }

    public String getSkuSaleNum() {
        return skuSaleNum;
    }

    public void setSkuSaleNum(String skuSaleNum) {
        this.skuSaleNum = skuSaleNum;
    }

    public int getSkuNum() {
        return skuNum;
    }

    public void setSkuNum(int skuNum) {
        this.skuNum = skuNum;
    }

    public String getSkuGoodsType() {
        return skuGoodsType;
    }

    public void setSkuGoodsType(String skuGoodsType) {
        this.skuGoodsType = skuGoodsType;
    }

    public String getSkuRepoGoods() {
        return skuRepoGoods;
    }

    public void setSkuRepoGoods(String skuRepoGoods) {
        this.skuRepoGoods = skuRepoGoods;
    }

    public String getSkuRepoGoodsName() {
        return skuRepoGoodsName;
    }

    public void setSkuRepoGoodsName(String skuRepoGoodsName) {
        this.skuRepoGoodsName = skuRepoGoodsName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}