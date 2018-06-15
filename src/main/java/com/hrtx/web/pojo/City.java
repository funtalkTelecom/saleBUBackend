package com.hrtx.web.pojo;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * City entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "tb_city")
public class City implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String fullName;
	private String acronym;
	private Integer pid;
	private Integer grade;
	private String zip_code;
	private String area_code;
	private Integer is_del;
//	private Integer seq;

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

	// Property accessors
//	@SequenceGenerator(name = "generator", allocationSize=1, sequenceName="SYS_SEQ")
	//	@GeneratedValue(strategy = SEQUENCE, generator = "generator")
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10, scale = 0)
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Column(name = "name", length = 50)
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "full_name", length = 200)
	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Column(name = "acronym", length = 50)
	public String getAcronym() {
		return this.acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	@Column(name = "pid", precision = 10, scale = 0)
	public int getPid() {
		return this.pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	@Column(name = "grade", precision = 10, scale = 0)
	public int getGrade() {
		return this.grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}
	@Column(name = "zip_code")
	public String getZip_code() {
		return zip_code;
	}

	public void setZip_code(String zipCode) {
		zip_code = zipCode;
	}
	@Column(name = "area_code")
	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String areaCode) {
		area_code = areaCode;
	}
//	@Column(name = "seq")
//	public Integer getSeq() {
//		return seq;
//	}
//
//	public void setSeq(Integer seq) {
//		this.seq = seq;
//	}

	@Column(name = "is_del")
	public int getIs_del() {
		return is_del;
	}

	public void setIs_del(int is_del) {
		this.is_del = is_del;
	}
}