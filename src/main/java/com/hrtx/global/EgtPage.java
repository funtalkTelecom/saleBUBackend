package com.hrtx.global;


public class EgtPage extends com.github.pagehelper.Page {
    private long total;

    public EgtPage(int pageNum, int pageSzie, long totalHits) {
        super(pageNum, pageSzie);
        this.total = totalHits;
    }

    @Override
    public long getTotal() {
        return total;
    }

    @Override
    public void setTotal(long total) {
        this.total = total;
    }
}

