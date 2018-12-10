package com.hrtx.web.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_channel")
public class Channel extends BasePojo{
    @Id
    private Long id;

    private Integer channelId;

    private String channel;

    private Integer isDel;

    private Double ratioPrice;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public Double getRatioPrice() {
        return ratioPrice;
    }

    public void setRatioPrice(Double ratioPrice) {
        this.ratioPrice = ratioPrice;
    }
}