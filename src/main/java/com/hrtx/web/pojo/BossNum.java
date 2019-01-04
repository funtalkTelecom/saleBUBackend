package com.hrtx.web.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_boss_num")
public class BossNum extends BasePojo implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer cityId;

    private Integer corpAgentId;

    private String bossNum;

    public BossNum(Integer id, Integer cityId, Integer corpAgentId, String bossNum) {
        this.id = id;
        this.cityId = cityId;
        this.corpAgentId = corpAgentId;
        this.bossNum = bossNum;
    }

    public BossNum() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getCorpAgentId() {
        return corpAgentId;
    }

    public void setCorpAgentId(Integer corpAgentId) {
        this.corpAgentId = corpAgentId;
    }

    public String getBossNum() {
        return bossNum;
    }

    public void setBossNum(String bossNum) {
        this.bossNum = bossNum == null ? null : bossNum.trim();
    }
}