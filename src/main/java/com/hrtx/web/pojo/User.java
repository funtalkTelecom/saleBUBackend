package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Date;

import javax.persistence.*;

@Table(name = "tb_user")
public class User extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer id;
	private String loginName;
	private String pwd;
	private String name;
	private Integer corpId;
	private Integer agentId;
	private String phone;
	private Integer addUser;
	private Date addDate;
	private Integer status;
	protected Integer isDel;
	@Transient
	private String roles;

	public User() {
	}

	public User(Integer id) {
		this.id = id;
	}

	private User(Integer id, String loginName, String pwd, String name,
				 Integer corpId, String phone, Integer addUserId, Date addDate, Integer status) {
		super();
		this.id = id;
		this.loginName = loginName;
		this.pwd = pwd;
		this.name = name;
		this.corpId = corpId;
		this.phone = phone;
		this.addUser = addUserId;
		this.addDate = addDate;
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCorpId() {
		return corpId;
	}

	public void setCorpId(Integer corpId) {
		this.corpId = corpId;
	}

	public Integer getAgentId() {
		return agentId;
	}

	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getAddUser() {
		return addUser;
	}

	public void setAddUser(Integer addUser) {
		this.addUser = addUser;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}
//36.110.179.*,106.39.177.*,120.52.149.*,111.206.226.*
	public static void main(String[] args) {
//		36.110.179.96-119，106.39.177.176-191（共48个IP）
//		120.52.149.80-111，111.206.226.32-47（共48个IP)
//		120.52.149.65
//		36.110.177.16-23
//		String aa = "36.110.177.(16|17|18|19|20|21|22|23),36.110.179.(96|97|98|99|100|101|102|103|104|105|106|107|108|109|110|111|112|113|114|115|116|117|118|119),106.39.177.(176|177|178|179|180|181|182|183|184|185|186|187|188|189|190|191),120.52.149.(65|80|81|82|83|84|85|86|87|88|89|90|91|92|93|94|95|96|97|98|99|100|101|102|103|104|105|106|107|108|109|110|111),111.206.226.(32|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47)";
//		String bb = "<platrequest>    <req__no>1001010220181126155930000012</req__no> </platrequest>";
//		java.lang.System.out.println(bb.replaceAll("\\s*","").replaceAll("</platrequest>","").replaceAll("<platrequest>", ""));
		//        A
//                AA
//        AAA
//                AAAA
//        AAAAA
//                AAAAAA
//        ABC
//                ABCD
//        AABB
//                AAABBB
//        AAAABBBB
//                ABAB
//        ABB
//                ABABAB
//        AABBAABB
		String aa = "12345644444";
		java.lang.System.out.println(aa.matches("\\d{6}(\\d)\\1{4}"));
		String bb = "00000121212";
		java.lang.System.out.println(bb.matches("\\d{5}([0-9])((?!\\1)([0-9]))\\1\\2\\1\\2"));
		String cc = "00011441144";
		java.lang.System.out.println(cc.matches("\\d{3}([0-9])\\1((?!\\1)([0-9]))\\2\\1{2}\\2{2}"));
	}

}