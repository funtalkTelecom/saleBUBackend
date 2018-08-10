package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_ep_sale_notice")
public class EPSaleNotice extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
    @Id
	@JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long epSaleId;//竞拍活动ID  来自tb_ep_sale.id
    private String phone;//	短信通知号码
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addDate;//	关注时间 添加
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;//	关注时间  修改
    private Long  consumerId;//	用户id  来自tb_consumer.id
    private String addIp;//	用户ip
    private String  remark;//备注
    private boolean isDel;
    private Integer isNotice; //是否提醒



    public EPSaleNotice() {
    }

    public EPSaleNotice(Long id, Long epSaleId, String phone, Date addDate, Date updateDate, Long consumerId,
                       String addIp, String remark, Boolean isDel, Integer isNotice) {
        this.id = id;
        this.epSaleId = epSaleId;
        this.phone = phone;
        this.addDate = addDate;
        this.updateDate=updateDate;
        this.consumerId = consumerId;
        this.addIp = addIp;
        this.consumerId = consumerId;
        this.addIp = addIp;
        this.remark = remark;
        this.isDel = isDel;
        this.isNotice = isNotice;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Date getUpdateDate() { return updateDate; }

    public void setUpdateDate(Date updateDate) { this.updateDate = updateDate; }

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

    public boolean isDel() {
        return isDel;
    }

    public void setDel(boolean del) {
        isDel = del;
    }

    public Integer getIsNotice() { return isNotice; }

    public void setIsNotice(Integer isNotice) { this.isNotice = isNotice; }

    public Long getEpSaleId() { return epSaleId; }

    public void setEpSaleId(Long epSaleId) { this.epSaleId = epSaleId; }
}