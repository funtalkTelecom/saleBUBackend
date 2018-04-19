package com.hrtx.dto;

import java.io.File;
import org.springframework.stereotype.Component;

@Component
public class PermissionAllocateForm {

	private String listJson;
	
	private int start;
	
	private int limit;
	
	private long nodeId;
	
	private String nodeText;
	private String remark;
	private String nodeValue;
	
	private long roleId;
	
	private String permissionIds;// 权限id串已（，）隔开
	
	private String loginName;
	private String userName;
	private long corpId;
	private String pwd;
	private String passpwd;
	private String phone;
	private long id;
	private int property;
	
    private File file;
    private String fileFileName;
    private String time;
    private long city;
	private long district;
	
	
	private String firstName;
	private String listName;
	private String companyName;
	private String resellerNo;   //代理编码
	public String getListJson() {
		return listJson;
	}
	public void setListJson(String listJson) {
		this.listJson = listJson;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeText() {
		return nodeText;
	}
	public void setNodeText(String nodeText) {
		this.nodeText = nodeText;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getNodeValue() {
		return nodeValue;
	}
	public void setNodeValue(String nodeValue) {
		this.nodeValue = nodeValue;
	}
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public String getPermissionIds() {
		return permissionIds;
	}
	public void setPermissionIds(String permissionIds) {
		this.permissionIds = permissionIds;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public long getCorpId() {
		return corpId;
	}
	public void setCorpId(long corpId) {
		this.corpId = corpId;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getPasspwd() {
		return passpwd;
	}
	public void setPasspwd(String passpwd) {
		this.passpwd = passpwd;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getProperty() {
		return property;
	}
	public void setProperty(int property) {
		this.property = property;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getFileFileName() {
		return fileFileName;
	}
	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public long getCity() {
		return city;
	}
	public void setCity(long city) {
		this.city = city;
	}
	public long getDistrict() {
		return district;
	}
	public void setDistrict(long district) {
		this.district = district;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getListName() {
		return listName;
	}
	public void setListName(String listName) {
		this.listName = listName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getResellerNo() {
		return resellerNo;
	}
	public void setResellerNo(String resellerNo) {
		this.resellerNo = resellerNo;
	}
	
	
	
	
}
