package com.hrtx.web.pojo;

public class Sequence {
    private String seqName;

    private Integer currentVal;

    private Integer incrementVal;

    public Sequence(String seqName, Integer currentVal, Integer incrementVal) {
        this.seqName = seqName;
        this.currentVal = currentVal;
        this.incrementVal = incrementVal;
    }

    public Sequence() {
        super();
    }

    public String getSeqName() {
        return seqName;
    }

    public void setSeqName(String seqName) {
        this.seqName = seqName == null ? null : seqName.trim();
    }

    public Integer getCurrentVal() {
        return currentVal;
    }

    public void setCurrentVal(Integer currentVal) {
        this.currentVal = currentVal;
    }

    public Integer getIncrementVal() {
        return incrementVal;
    }

    public void setIncrementVal(Integer incrementVal) {
        this.incrementVal = incrementVal;
    }
}