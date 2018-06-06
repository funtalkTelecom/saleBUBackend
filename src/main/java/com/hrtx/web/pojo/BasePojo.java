package com.hrtx.web.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Value;

import com.hrtx.global.IdWorker;

public class BasePojo {
	@Transient
	protected int limit = 0;
	@Transient
	protected int start = 0;
	@Transient
	protected int pageNum = 0;
	@Transient
	protected List<Object> list = new ArrayList<Object>();
	@Transient
	protected Map<String, Object> map = new HashMap<String, Object>();
	
	@Transient
	@Value("${app.idworker.workerid}")
	private static int workerId;
	@Transient
	protected static IdWorker idWorker = new IdWorker(workerId, 0);
	@Transient
	private long generalId = idWorker.nextId();

	public int getPageNum() {
		if(this.limit==0) return 0;
		return (this.start/this.limit)+1;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public long getGeneralId() {
		return generalId;
	}
	public void setGeneralId(long generalId) {
		this.generalId = generalId;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public List<Object> getList() {
		return list;
	}
	public void setList(List<Object> list) {
		this.list = list;
	}
	public Map<String, Object> getMap() {
		return map;
	}
	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
}
