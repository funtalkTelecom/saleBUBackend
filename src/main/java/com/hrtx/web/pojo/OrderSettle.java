package com.hrtx.web.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
@Table(name = "tb_order_settle")
public class OrderSettle  extends BasePojo{

    public static Double busi_pp_cost_fee=0.1d;//商家推广梧桐期望收取的费率
    public static Double base_pp_price=1000d;//基础推广 销售价格点
    public static Double base_pp_price_low_fee=0.2d;//基础推广  低于等于销售价格点的费率
    public static Double base_pp_price_more_fee=0.1d;//基础推广  大于销售价格点的费率
    public static Double base_pp_price_all_fee=0.05d;//基础推广 全平台的推广
    public static int base_pp_price_month_count=5;//基础推广  每月最低单数

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer orderId;
    private Integer settler;//支付方
    private Integer feeType;
    private Integer settleUser;//收款分
    private BigDecimal settleAmt;
    private Integer status;
    private Date addDate;
    private Date settleDate;

    public OrderSettle(Integer orderId, Integer feeType, Integer settler, Integer settleUser,Double settleAmt, Integer status) {
        this.orderId = orderId;
        this.feeType = feeType;
        this.settler=settler;
        this.settleUser = settleUser;
        this.settleAmt = BigDecimal.valueOf(settleAmt);
        this.status = status;
        this.addDate =new Date();
    }

    public OrderSettle(Integer id, Integer orderId, Integer feeType, Integer settleUser, BigDecimal settleAmt, Integer status, Date addDate, Date settleDate) {
        this.id = id;
        this.orderId = orderId;
        this.feeType = feeType;
        this.settleUser = settleUser;
        this.settleAmt = settleAmt;
        this.status = status;
        this.addDate = addDate;
        this.settleDate = settleDate;
    }

    public OrderSettle() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getFeeType() {
        return feeType;
    }

    public void setFeeType(Integer feeType) {
        this.feeType = feeType;
    }

    public Integer getSettleUser() {
        return settleUser;
    }

    public void setSettleUser(Integer settleUser) {
        this.settleUser = settleUser;
    }

    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public Integer getSettler() {
        return settler;
    }

    public void setSettler(Integer settler) {
        this.settler = settler;
    }
}