package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.*;
import java.util.Date;

@Table(name = "tb_account")
public class Account extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	public static final int card_bank_wx =-1;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer id;
	private Integer corpId;
	private Integer accountType;//账号类型：1银行账号；2微信号
	private String bankAccount;//户主
	private Integer cardBank;//银行编码(字典表)
	private String cardBankName;//银行
	private String subbranchBank;//支行
	private String cardAccount;//accountType=1银行账号;accountType=2 consumer_id;
	private Integer addUserId;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date addDate;
	private Integer isDel;
	@Transient
	private String bankAccountHidden;//户主信息隐藏 ****代替
	@Transient
	private String cardAccountHidden;//账号信息隐藏 ****代替

	public Account() {
	}

	public Account(Integer id, String bankAccount, Integer cardBank,String cardBankName, String subbranchBank, String cardAccount, Integer addUserId, Date addDate, Integer isDel) {
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public Integer getCardBank() {
		return cardBank;
	}

	public void setCardBank(Integer cardBank) {
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

	public Integer getAddUserId() {
		return addUserId;
	}

	public void setAddUserId(Integer addUserId) {
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

	public Integer getCorpId() {
		return corpId;
	}

	public void setCorpId(Integer corpId) {
		this.corpId = corpId;
	}

	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

	public String getBankAccountHidden() {
		return bankAccountHidden;
	}

	public void setBankAccountHidden(String bankAccountHidden) {
		this.bankAccountHidden = bankAccountHidden;
	}

	public String getCardAccountHidden() {
		return cardAccountHidden;
	}

	public void setCardAccountHidden(String cardAccountHidden) {
		this.cardAccountHidden = cardAccountHidden;
	}
}