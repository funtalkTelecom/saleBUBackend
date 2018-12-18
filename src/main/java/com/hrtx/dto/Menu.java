package com.hrtx.dto;

import java.io.Serializable;

public class Menu implements Serializable {

	private String name;
	private String url;
	private int id;
	private int pid;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public Menu(String name, String url, int id, int pid, int grade) {
		this.name = name;
		this.url = url;
		this.id = id;
		this.pid = pid;
		this.grade = grade;
	}

}
