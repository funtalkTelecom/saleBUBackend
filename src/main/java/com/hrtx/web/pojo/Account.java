package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_account")
public class Account extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private String bankAccount;
	private Long cardBank;
	private String cardBankName;
	private String subbranchBank;
	private String cardAccount;
    private Long addUserId;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date addDate;
	private Integer isDel;

	public Account() {
	}

    public Account(Long id, String bankAccount, Long cardBank,String cardBankName, String subbranchBank, String cardAccount, Long addUserId, Date addDate, Integer isDel) {
        this.id = id;
        this.bankAccount = bankAccount;
        this.cardBank = cardBank;
        this.cardBankName =cardBankName;
        this.subbranchBank = subbranchBank;
        this.cardAccount = cardAccount;
        this.addUserId = addUserId;
        this.addDate = addDate;
        this.isDel = isDel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Long getCardBank() {
        return cardBank;
    }

    public void setCardBank(Long cardBank) {
        this.cardBank = cardBank;
    }

    public String getSubbranchBank() {
        return subbranchBank;
    }

    public void setSubbranchBank(String subbranchBank) {
        this.subbranchBank = subbranchBank;
    }

    public String getCardAccount() {
        return cardAccount;
    }

    public void setCardAccount(String cardAccount) {
        this.cardAccount = cardAccount;
    }

    public Long getAddUserId() {
        return addUserId;
    }

    public void setAddUserId(Long addUserId) {
        this.addUserId = addUserId;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public String getCardBankName() {
        return cardBankName;
    }

    public void setCardBankName(String cardBankName) {
        this.cardBankName = cardBankName;
    }
}