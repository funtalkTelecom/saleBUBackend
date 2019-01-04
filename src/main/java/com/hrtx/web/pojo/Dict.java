package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_dict")
public class Dict extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
    private Integer id;
    private String keyId;
    private String keyGroup;
    private String keyValue;
    private int isDel;
    private String note;
    private Integer pid;
    private int seq;
    private String ext1;
    private String ext2;
    private Integer corpId;

    public Dict() {
    }

    public Dict(String keyId, String keyGroup) {
        this.keyId = keyId;
        this.keyGroup = keyGroup;
    }

    public Dict(Integer id, String keyId, String keyGroup, String keyValue, int isDel, String note, Integer pid, int seq) {
        this.id = id;
        this.keyId = keyId;
        this.keyGroup = keyGroup;
        this.keyValue = keyValue;
        this.isDel = isDel;
        this.note = note;
        this.pid = pid;
        this.seq = seq;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyGroup() {
        return keyGroup;
    }

    public void setKeyGroup(String keyGroup) {
        this.keyGroup = keyGroup;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
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

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }
}
