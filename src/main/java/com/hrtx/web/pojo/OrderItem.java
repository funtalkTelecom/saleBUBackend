package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_order_item")
public class OrderItem extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
    private Integer itemId;
    private Integer pItemId;
    private Integer orderId;
    private Integer goodsId;
    private Integer skuId;
    private String skuProperty;
    private Integer numId;
    private String num;
    private int isShipment;
    private Integer sellerId;
    private String sellerName;
    private String shipmentApi;
    private Integer companystockId;
    private int quantity;
    private double price;
    private double total;
    private Integer mealId;
    private String iccid;

    public OrderItem() {
    }
    public OrderItem(Integer orderId, Integer goodsId, Integer skuId, String skuProperty, Integer numId, String num, int isShipment, Integer sellerId, String sellerName, String shipmentApi, Integer companystockId, int quantity, double price, double total,Integer pItemId) {
        this.orderId = orderId;
        this.goodsId = goodsId;
        this.skuId = skuId;
        this.skuProperty = skuProperty;
        this.numId = numId;
        this.num = num;
        this.isShipment = isShipment;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.shipmentApi = shipmentApi;
        this.companystockId = companystockId;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.pItemId=pItemId;
    }
    public OrderItem(Integer itemId, Integer pItemId, Integer orderId, Integer goodsId, Integer skuId, String skuProperty, Integer numId, String num, int isShipment, Integer sellerId, String sellerName, String shipmentApi, Integer companystockId, int quantity, double price, double total, Integer mealId, String iccid) {
        this.itemId = itemId;
        this.pItemId = pItemId;
        this.orderId = orderId;
        this.goodsId = goodsId;
        this.skuId = skuId;
        this.skuProperty = skuProperty;
        this.numId = numId;
        this.num = num;
        this.isShipment = isShipment;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.shipmentApi = shipmentApi;
        this.companystockId = companystockId;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.mealId = mealId;
        this.iccid = iccid;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getpItemId() {
        return pItemId;
    }

    public void setpItemId(Integer pItemId) {
        this.pItemId = pItemId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public String getSkuProperty() {
        return skuProperty;
    }

    public void setSkuProperty(String skuProperty) {
        this.skuProperty = skuProperty;
    }

    public Integer getNumId() {
        return numId;
    }

    public void setNumId(Integer numId) {
        this.numId = numId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public int getIsShipment() {
        return isShipment;
    }

    public void setIsShipment(int isShipment) {
        this.isShipment = isShipment;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getShipmentApi() {
        return shipmentApi;
    }

    public void setShipmentApi(String shipmentApi) {
        this.shipmentApi = shipmentApi;
    }

    public Integer getCompanystockId() {
        return companystockId;
    }

    public void setCompanystockId(Integer companystockId) {
        this.companystockId = companystockId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Integer getMealId() {
        return mealId;
    }

    public void setMealId(Integer mealId) {
        this.mealId = mealId;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }
}