package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_ep_sale")
public class EPSale extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    private String  title ;//标题
    private String  epRule ;//竞拍规则
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date  startTime;//开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date  endTime;//结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date  lastPayTime;//最迟付款时间
    private int  isShow;   //显示条件  预备暂时不用
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date  createDate;//'创建日期
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date  updateDate;//修改日期
    private Long  addUserId;//添加人ID
    private int  isDel;//是否删除
    private String  note;//备注



    public EPSale() {
	}

    public EPSale(Long id, String title, String epRule, Date startTime, Date endTime, Date lastPayTime, Date createDate, Date updateDate
            , Long addUserId , int isDel , int isDefaultl, String note) {
        this.id = id;
        this.title = title;
        this.epRule = epRule;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lastPayTime=lastPayTime;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.addUserId = addUserId;
        this.isDel = isDel;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEpRule() {
        return epRule;
    }

    public void setEpRule(String epRule) {
        this.epRule = epRule;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getIsShow() {
        return isShow;
    }

    public void setIsShow(int isShow) {
        this.isShow = isShow;
    }
    public Date getLastPayTime() {
        return lastPayTime;
    }

    public void setLastPayTime(Date lastPayTime) {
        this.lastPayTime = lastPayTime;
    }
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getAddUserId() {
        return addUserId;
    }

    public void setAddUserId(Long addUserId) {
        this.addUserId = addUserId;
    }

    public int getIsDel() {
        return isDel;
    }

    public void setIsDel(int isDel) {
        this.isDel = isDel;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}