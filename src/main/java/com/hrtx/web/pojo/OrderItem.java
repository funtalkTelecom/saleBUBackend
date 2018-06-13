package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_order_item")
public class OrderItem extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
    private Long itemId;
    private Long orderId;
    private Long goodsId;
    private Long skuId;
    private String skuProperty;
    private Long numId;
    private String num;
    private int isShipment;
    private Long sellerId;
    private String sellerName;
    private String shipmentApi;
    private Long companystockId;
    private int quantity;
    private double price;
    private double total;
    private Long mealId;
    private String iccid;

    public OrderItem() {
    }

    public OrderItem(Long itemId, Long orderId, Long goodsId, Long skuId, String skuProperty, Long numId, String num, int isShipment, Long sellerId, String sellerName, String shipmentApi, Long companystockId, int quantity, double price, double total, Long mealId, String iccid) {
        this.itemId = itemId;
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

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getSkuProperty() {
        return skuProperty;
    }

    public void setSkuProperty(String skuProperty) {
        this.skuProperty = skuProperty;
    }

    public Long getNumId() {
        return numId;
    }

    public void setNumId(Long numId) {
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

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
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

    public Long getCompanystockId() {
        return companystockId;
    }

    public void setCompanystockId(Long companystockId) {
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

    public Long getMealId() {
        return mealId;
    }

    public void setMealId(Long mealId) {
        this.mealId = mealId;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }
}