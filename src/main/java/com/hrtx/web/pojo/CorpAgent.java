package com.hrtx.web.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_corp_agent")
public class CorpAgent extends BasePojo implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer corpId;

    private Integer agentId;

    private String loginName;

    private String pwd;

    private Integer channelId;

    private String workName;

    private String workCardType;

    private String workCardCode;

    private String workAddress;

    private String workContact;

    private String workContactPhone;

    private Integer status;

    private String checkRemark;

    public CorpAgent(Integer id, Integer corpId, Integer agentId, String loginName, String pwd, Integer channelId, String workName, String workCardType, String workCardCode, String workAddress, String workContact, String workContactPhone, Integer status, String checkRemark) {
        this.id = id;
        this.corpId = corpId;
        this.agentId = agentId;
        this.loginName = loginName;
        this.pwd = pwd;
        this.channelId = channelId;
        this.workName = workName;
        this.workCardType = workCardType;
        this.workCardCode = workCardCode;
        this.workAddress = workAddress;
        this.workContact = workContact;
        this.workContactPhone = workContactPhone;
        this.status = status;
        this.checkRemark = checkRemark;
    }

    public CorpAgent() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName == null ? null : loginName.trim();
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd == null ? null : pwd.trim();
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName == null ? null : workName.trim();
    }

    public String getWorkCardType() {
        return workCardType;
    }

    public void setWorkCardType(String workCardType) {
        this.workCardType = workCardType == null ? null : workCardType.trim();
    }

    public String getWorkCardCode() {
        return workCardCode;
    }

    public void setWorkCardCode(String workCardCode) {
        this.workCardCode = workCardCode == null ? null : workCardCode.trim();
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress == null ? null : workAddress.trim();
    }

    public String getWorkContact() {
        return workContact;
    }

    public void setWorkContact(String workContact) {
        this.workContact = workContact == null ? null : workContact.trim();
    }

    public String getWorkContactPhone() {
        return workContactPhone;
    }

    public void setWorkContactPhone(String workContactPhone) {
        this.workContactPhone = workContactPhone == null ? null : workContactPhone.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCheckRemark() {
        return checkRemark;
    }

    public void setCheckRemark(String checkRemark) {
        this.checkRemark = checkRemark == null ? null : checkRemark.trim();
    }
}