package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_channel")
public class Channel extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private int channelId;
    private Double ratioPrice;
    private String channel;
    private Integer isDel;

    public Channel() {
    }

    public Channel(Long id, int channelId, Double ratioPrice,String channel, Integer isDel) {
        this.id = id;
        this.channelId = channelId;
        this.ratioPrice = ratioPrice;
        this.channel = channel;
        this.isDel = isDel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public Double getRatioPrice() {
        return ratioPrice;
    }

    public void setRatioPrice(Double ratioPrice) {
        this.ratioPrice = ratioPrice;
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
}