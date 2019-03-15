package com.hrtx.web.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
@Table(name = "tb_share")
public class Share  extends BasePojo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer consumerId;

    private Integer shareSource;

    private String shareNum;

    private Integer shareNumId;

    private String shareImage;

    private String shareUrl;

    private String shareFilePath;

    private Date shareDate;

    private Integer isDel;

    public Share(Integer consumerId, Integer shareSource, String shareNum, Integer shareNumId, String shareImage, String shareUrl) {
        this.id = id;
        this.consumerId = consumerId;
        this.shareSource = shareSource;
        this.shareNum = shareNum;
        this.shareNumId = shareNumId;
        this.shareImage = shareImage;
        this.shareUrl = shareUrl;
        this.shareDate = new Date();
        this.isDel = 0;
    }
    public Share(Integer id, Integer consumerId, Integer shareSource, String shareNum, Integer shareNumId, String shareImage, String shareUrl, String shareFilePath, Date shareDate, Integer isDel) {
        this.id = id;
        this.consumerId = consumerId;
        this.shareSource = shareSource;
        this.shareNum = shareNum;
        this.shareNumId = shareNumId;
        this.shareImage = shareImage;
        this.shareUrl = shareUrl;
        this.shareFilePath = shareFilePath;
        this.shareDate = shareDate;
        this.isDel = isDel;
    }

    public Share() {
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

    public String getShareNum() {
        return shareNum;
    }

    public void setShareNum(String shareNum) {
        this.shareNum = shareNum == null ? null : shareNum.trim();
    }

    public Integer getShareNumId() {
        return shareNumId;
    }

    public void setShareNumId(Integer shareNumId) {
        this.shareNumId = shareNumId;
    }

    public String getShareImage() {
        return shareImage;
    }

    public void setShareImage(String shareImage) {
        this.shareImage = shareImage == null ? null : shareImage.trim();
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl == null ? null : shareUrl.trim();
    }

    public String getShareFilePath() {
        return shareFilePath;
    }

    public void setShareFilePath(String shareFilePath) {
        this.shareFilePath = shareFilePath == null ? null : shareFilePath.trim();
    }

    public Date getShareDate() {
        return shareDate;
    }

    public void setShareDate(Date shareDate) {
        this.shareDate = shareDate;
    }

    public Integer getShareSource() {
        return shareSource;
    }

    public void setShareSource(Integer shareSource) {
        this.shareSource = shareSource;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }
}