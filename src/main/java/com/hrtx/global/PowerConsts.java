package com.hrtx.global;


public enum PowerConsts {
	//编码采用5位数，第1位表示1级菜单，第2，3位表示2级菜单，第4，5位表示3级菜单
	//id 权限名称 父亲id 叶子  url 菜单等级  seq
	NOPOWER(-1,"",0,0,"",0,111111),//不需要权限
	NOLOGINPOWER(-2,"免登陆且无需权限",0,0,"",0, 1),
	SYSTEM(1,"固网终端售后系统",0,0,"",0,111112),
	
	/********************* 系统管理模块start **********************/
	SYSTEMMODULE(90000,"系统管理",1,0,"",1,4),
	/**管理员   区分是否是管理员和经销商  无效单独每个节点配置，特殊权限可以各自节点配置*/
	
	SYSTEMMOUULE_USERLIST_ALL(90001,"管理员",90000,1,"",3,5),//区分是否是管理员和经销商
	
	SYSTEMMOUULE_PERMISSION(90100,"角色授权",90000,0,"user/query-role",2,1),
	SYSTEMMOUULE_ADDROLE_PERMISSION(90101,"添加角色",90100,1,"",3,2),             //角色管理
	
	SYSTEMMOUULE_USERLIST(90200,"用户管理",90000,0,"user/query-user",2,2),           
	SYSTEMMOUULE_USERLIST_LIST(90201,"查询",90200,1,"",3,2), 
	SYSTEMMOUULE_USERLIST_DISTRIBUTE(90202,"分配角色",90200,1,"",3,2), 
	SYSTEMMOUULE_USERLIST_ADD(90203,"添加",90200,1,"",3,3),
	SYSTEMMOUULE_USERLIST_EDIT(90204,"修改",90200,1,"",3,3),
	SYSTEMMOUULE_USERLIST_UPPWD(90205,"修改密码",90200,1,"",3,6),
	
	
	REPORTMODULE(60000,"报表管理",1,0,"",1,2),
	REPORTMODULE_DELIVERYITEMINFO(60100,"固网终端查询管理",60000,0,"deliveryItemInfo.htm",2,1),
	REPORTMODULE_DELIVERYITEMINFO_LIST(60101,"查询",60100,1,"",3,1),
	REPORTMODULE_DELIVERYITEMINFO_EXPORT(60102,"导出",60100,1,"",3,2),
	
	REPORTMODULE_COMMON(60900,"通用报表",60000,0,"searchCommon.htm",2,99),   //通用报表
	REPORTMODULE_COMMON_QUERY(60901,"查询",60900,1,"",3,1),   //通用查询报表
	REPORTMODULE_COMMON_EXPORT(60902,"导出",60900,1,"",3,2),   //通用导出报表
	
	
	;
	
	public long getId() {
		return id;
	}
	public String getPowerName() {
		return powerName;
	}
	public long getPid() {
		return pid;
	}
	public int getLeaf() {
		return leaf;
	}
	public String getUrl() {
		return url;
	}
	public int getGrade() {
		return grade;
	}
	public int getSeq() {
		return seq;
	}

	private long id;                 //编号
	private String powerName;       //权限名, 菜单名
	private Long pid;                //父编号
	private int leaf;               //叶子，是否无子类(1表示是叶子，0表示不是)
	private String url;             //菜单地址
	private int grade;              //菜单等级
	private int seq;
	private PowerConsts(long id,String powerName,long pid,int leaf, String url, int grade, int seq){
		this.id = id;
		this.powerName = powerName;
		this.pid = pid;
		this.leaf = leaf;
		this.url = url;
		this.grade = grade;
		this.seq = seq;
	}
}
