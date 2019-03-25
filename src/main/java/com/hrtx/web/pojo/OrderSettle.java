package com.hrtx.web.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
@Table(name = "tb_order_settle")
public class OrderSettle  extends BasePojo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer orderId;
    private Integer feeType;
    private Integer settleUser;
    private BigDecimal settleAmt;
    private Integer status;
    private Date addDate;
    private Date settleDate;

    public OrderSettle(Integer orderId, Integer feeType, Integer settleUser,Double settleAmt, Integer status) {
        this.orderId = orderId;
        this.feeType = feeType;
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
}