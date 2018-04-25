package com.hrtx.dto;

import java.io.Serializable;

public class Menu implements Serializable {

	private String name;
	private String url; 
	private long id;
	private long pid;
	private int grade;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public Menu(String name, String url, long id, long pid, int grade) {
		this.name = name;
		this.url = url;
		this.id = id;
		this.pid = pid;
		this.grade = grade;
	}

}
