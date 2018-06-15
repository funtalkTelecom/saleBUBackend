package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_auction")
public class Auction extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
    @Id
	@JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Long skuId;//	tb_sku.id  二级属性关联到一级商品保证金;号码三级
    private Long numId;//	号码编码
    private String num;//	号码  来自 tb_num.num_resource
    private String gName;//商品名称
    private Long gId;//商品编码tb_goods.id
    private double price;//	AuctionDeposit
    private int status;//	状态 1初始；2成功；3失败；4落败  保证金：出价时若未付保证金或支付失败=>1初始;支付成功=》2成功;新出价:旧出价=>4落败
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addDate;//	创建时间
    private Long  consumerId;//	用户id  来自tb_consumer.id
    private String addIp;//	用户ip
    private Long orderId;//订单id
    private String  remark;//备注



    public Auction() {
    }

    public Auction(Long id, Long skuId, Long numId, String num, String gName,Long gId, double price, int status, Date addDate, Long consumerId,
                   String addIp, Long orderId,String remark) {
        this.id = id;
        this.skuId = skuId;
        this.numId = numId;
        this.num = num;
        this.price = price;
        this.status = status;
        this.addDate = addDate;
        this.consumerId = consumerId;
        this.addIp = addIp;
        this.consumerId = consumerId;
        this.addIp = addIp;
        this.orderId = orderId;
        this.remark = remark;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
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

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public Long getgId() {
        return gId;
    }

    public void setgId(Long gId) {
        this.gId = gId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(Long consumerId) {
        this.consumerId = consumerId;
    }

    public String getAddIp() {
        return addIp;
    }

    public void setAddIp(String addIp) {
        this.addIp = addIp;
    }


}