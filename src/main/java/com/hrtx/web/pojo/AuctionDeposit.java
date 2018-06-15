package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_auction_deposit")
public class AuctionDeposit extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
    @Id
	@JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Long skuId;//	tb_sku.id  二级属性关联到一级商品保证金;号码三级
    private Long numId;//	号码编码
    private String num;//	号码  来自 tb_num.num_resource
    private double amt;//	金额
    private int status;//	状态  1初始；2已支付；3已退款
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addDate;//	创建时间
    private Long  consumerId;//	用户id  来自tb_consumer.id
    private String addIp;//	用户ip
    private String paySnn;//	支付流水
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payDate;//	支付时间
    private double refundAmt;//	退款金额
    private String refundSnn;//	退款流水
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date refundDate;//	退款时间

    public AuctionDeposit() {
    }

    public AuctionDeposit(Long id, Long skuId, Long numId, String num, double amt, int status, Date addDate, Long consumerId, String addIp, String paySnn, Date payDate,
                          double refundAmt, String refundSnn, Date refundDate) {
        this.id = id;
        this.skuId = skuId;
        this.numId = numId;
        this.num = num;
        this.amt = amt;
        this.status = status;
        this.addDate = addDate;
        this.consumerId = consumerId;
        this.addIp = addIp;
        this.paySnn = paySnn;
        this.payDate = payDate;
        this.refundAmt = refundAmt;
        this.refundSnn = refundSnn;
        this.refundDate = refundDate;
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

    public double getAmt() {
        return amt;
    }

    public void setAmt(double amt) {
        this.amt = amt;
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

    public String getPaySnn() {
        return paySnn;
    }

    public void setPaySnn(String paySnn) {
        this.paySnn = paySnn;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public double getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(double refundAmt) {
        this.refundAmt = refundAmt;
    }

    public String getRefundSnn() {
        return refundSnn;
    }

    public void setRefundSnn(String refundSnn) {
        this.refundSnn = refundSnn;
    }

    public Date getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

}