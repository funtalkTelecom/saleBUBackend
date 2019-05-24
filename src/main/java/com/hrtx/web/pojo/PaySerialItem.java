package com.hrtx.web.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_pay_serial_item")
public class PaySerialItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer paySerialId;

    private String itemId;

    private String payee;

    private Integer amt;

    private Date addDate;

    public PaySerialItem(Integer paySerialId, String itemId, String payee, Integer amt) {
        this.paySerialId = paySerialId;
        this.itemId = itemId;
        this.payee = payee;
        this.amt = amt;
        this.addDate = new Date();
    }

    public PaySerialItem() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPaySerialId() {
        return paySerialId;
    }

    public void setPaySerialId(Integer paySerialId) {
        this.paySerialId = paySerialId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId == null ? null : itemId.trim();
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee == null ? null : payee.trim();
    }

    public Integer getAmt() {
        return amt;
    }

    public void setAmt(Integer amt) {
        this.amt = amt;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }
}