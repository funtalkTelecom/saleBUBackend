package com.hrtx.web.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_num_browse")
public class NumBrowse extends BasePojo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer numId;
    private String num;
    private Integer consumerId;
    private Integer channel;
    private String openUrl;
    private Date addDate;
    private String openIp;
    private Integer openCount;
    private Integer actType;
    private Integer shareId;
    private Integer shareConsumerId;
    private Integer shareFirstBrowse;

    public NumBrowse(Integer numId, String num, Integer consumerId, Integer channel, String openUrl, String openIp,Integer actType, Integer shareId) {
        this.numId=numId;
        this.num=num;
        this.consumerId = consumerId;
        this.shareId = shareId;
        this.channel = channel;
        this.openUrl = openUrl;
        this.addDate = new Date();
        this.openIp = openIp;
        this.openCount =0;
        this.shareFirstBrowse=0;
        this.actType=actType;
    }
    public NumBrowse(Integer id, Integer consumerId, Integer shareId, Integer channel, String openUrl, Date addDate, String openIp, Integer openCount) {
        this.id = id;
        this.consumerId = consumerId;
        this.shareId = shareId;
        this.channel = channel;
        this.openUrl = openUrl;
        this.addDate = addDate;
        this.openIp = openIp;
        this.openCount = openCount;
    }

    public NumBrowse() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(Integer consumerId) {
        this.consumerId = consumerId;
    }

    public Integer getShareId() {
        return shareId;
    }

    public void setShareId(Integer shareId) {
        this.shareId = shareId;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
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

    public String getOpenUrl() {
        return openUrl;
    }

    public void setOpenUrl(String openUrl) {
        this.openUrl = openUrl == null ? null : openUrl.trim();
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public String getOpenIp() {
        return openIp;
    }

    public void setOpenIp(String openIp) {
        this.openIp = openIp == null ? null : openIp.trim();
    }

    public Integer getOpenCount() {
        return openCount;
    }

    public void setOpenCount(Integer openCount) {
        this.openCount = openCount;
    }

    public Integer getShareConsumerId() {
        return shareConsumerId;
    }

    public void setShareConsumerId(Integer shareConsumerId) {
        this.shareConsumerId = shareConsumerId;
    }

    public Integer getShareFirstBrowse() {
        return shareFirstBrowse;
    }

    public void setShareFirstBrowse(Integer shareFirstBrowse) {
        this.shareFirstBrowse = shareFirstBrowse;
    }

    public Integer getActType() {
        return actType;
    }

    public void setActType(Integer actType) {
        this.actType = actType;
    }
}