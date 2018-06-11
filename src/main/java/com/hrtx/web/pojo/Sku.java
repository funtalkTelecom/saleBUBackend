package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_sku")
public class Sku extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long skuId;
    private Long gId;
    private String skuTobPrice;
    private String skuTocPrice;
    private String skuIsNum;
    private String skuSaleNum;
    private int skuNum;
    private String skuGoodsType;
    private String skuRepoGoods;

    public Sku() {
    }

    public Sku(Long skuId, Long gId, String skuTobPrice, String skuTocPrice, String skuIsNum, String skuSaleNum, int skuNum, String skuGoodsType, String skuRepoGoods) {
        this.skuId = skuId;
        this.gId = gId;
        this.skuTobPrice = skuTobPrice;
        this.skuTocPrice = skuTocPrice;
        this.skuIsNum = skuIsNum;
        this.skuSaleNum = skuSaleNum;
        this.skuNum = skuNum;
        this.skuGoodsType = skuGoodsType;
        this.skuRepoGoods = skuRepoGoods;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getgId() {
        return gId;
    }

    public void setgId(Long gId) {
        this.gId = gId;
    }

    public String getSkuTobPrice() {
        return skuTobPrice;
    }

    public void setSkuTobPrice(String skuTobPrice) {
        this.skuTobPrice = skuTobPrice;
    }

    public String getSkuTocPrice() {
        return skuTocPrice;
    }

    public void setSkuTocPrice(String skuTocPrice) {
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
}