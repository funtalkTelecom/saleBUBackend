package com.hrtx.web.pojo;

import org.apache.commons.lang.ArrayUtils;

import javax.persistence.*;
import javax.tools.Tool;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * City entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "tb_city")
public class City implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private Integer id;
	private String name;
	private String fullName;
	private String acronym;
	private Integer pid;
	private Integer grade;
	private String zip_code;
	private String area_code;
	private Integer is_del;

	// Constructors

	/** default constructor */
	public City() {
	}

	public City(int id, String name, String fullName, String acronym, int pid,
                int grade) {
		super();
		this.id = id;
		this.name = name;
		this.fullName = fullName;
		this.acronym = acronym;
		this.pid = pid;
		this.grade = grade;
	}

	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFullName() {
		return this.fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getAcronym() {
		return this.acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public int getPid() {
		return this.pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getGrade() {
		return this.grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}
	public String getZip_code() {
		return zip_code;
	}

	public void setZip_code(String zipCode) {
		zip_code = zipCode;
	}
	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String areaCode) {
		area_code = areaCode;
	}
	public int getIs_del() {
		return is_del;
	}

	public void setIs_del(int is_del) {
		this.is_del = is_del;
	}

	public static void main(String[] args) {
		String[] skuSaleNumbs = {"a","b","c","d","e","f","g"};
		int size = skuSaleNumbs.length;
		int start = 0;
		Object[] t = null;
		int limit = 5;
		while(start < size) {
			t = ArrayUtils.subarray(skuSaleNumbs,start, start+limit);
			start = start + t.length;
			String a =ArrayUtils.toString(t,"");
			String getSignInfo = a.substring(a.indexOf("{") + 1, a.indexOf("}"));
			java.lang.System.out.println( a);
			java.lang.System.out.println( getSignInfo);
		}
	}
}