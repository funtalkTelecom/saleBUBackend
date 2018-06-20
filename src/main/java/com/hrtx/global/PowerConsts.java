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

	SALEMOUDULE(70000,"销售管理",1,0,"",1,3),//销售管理
	MEALMOUDULE(70100,"套餐管理",70000,0,"meal/query-meal",2,1),//套餐管理
	MEALMOUDULE_COMMON_QUEYR(70201,"查询",70100,1,"",3,1),//套餐查询
	MEALMOUDULE_COMMON_IMPORT(70202,"导入",70100,1,"",3,2),//套餐导入
	MEALMOUDULE_COMMON_EDIT(70203,"修改",70100,1,"",3,3),//套餐修改
	MEALMOUDULE_COMMON_DELETE(70204,"删除",70100,1,"",3,4),//套餐删除

	GOODSMOUDULE(70200,"商品管理",70000,0,"goods/goods-query",2,1),//商品管理
	GOODSMOUDULE_COMMON_QUEYR(70201,"查询",70200,1,"",3,1),//商品查询
	GOODSMOUDULE_COMMON_EDIT(70202,"修改",70200,1,"",3,2),//商品修改
	GOODSMOUDULE_COMMON_DELETE(70203,"删除",70200,1,"",3,3),//商品删除
	GOODSMOUDULE_COMMON_ADD(70204,"添加",70200,1,"",3,4),//商品添加

	NUMBERMOUDULE(70300,"号码管理",70000,0,"number/number-query",2,1),//号码管理
	NUMBERMOUDULE_COMMON_QUEYR(70301,"查询",70300,1,"",3,1),//号码查询
	NUMBERMOUDULE_COMMON_ADDTAG(70302,"号码添加标签",70300,1,"",3,2),//号码添加标签

	ACCOUNTMOUDULE(70400,"收款账号管理",70000,0,"account/query-account",2,1),
	ACCOUNTMOUDULE_COMMON_QUEYR(70401,"查询",70400,1,"",3,1),//收款账号管理查询
	ACCOUNTMOUDULE_COMMON_EDIT(70402,"修改",70400,1,"",3,2),//收款账号管理修改
	ACCOUNTMOUDULE_COMMON_DELETE(70403,"删除",70400,1,"",3,3),//收款账号管理删除

	AGENTMOUDULE(70500,"代理商理管理",70000,0,"agent/query-agent",2,1),
	AGENTMOUDULE_COMMON_QUEYR(70501,"查询",70500,1,"",3,1),//管代理商理查询
	AGENTMOUDULE_COMMON_CHECK(70502,"审核",70500,1,"",3,2),//管代理商理审核

	ORDERMOUDULE(70600,"订单管理",70000,0,"order/order-query",2,1),
	ORDERMOUDULE_COMMON_QUEYR(70601,"查询",70600,1,"",3,1),//订单查询

	SYSMOUDULE(90300,"配置管理",90000,0,"system/system-query",2,1),//配置管理
	SYSMOUDULE_COMMON_QUEYR(90301,"查询",90300,1,"",3,1),//配置查询
	SYSMOUDULE_COMMON_EDIT(90302,"修改",90300,1,"",3,2),//配置修改
	SYSMOUDULE_COMMON_AUDIT(90303,"审核",90300,1,"",3,3),//配置审核
	SYSMOUDULE_COMMON_ADD(90304,"添加",90300,1,"",3,4),//配置添加
	SYSMOUDULE_COMMON_DELETE(90305,"删除",90300,1,"",3,4),//配置删除

	DICTMOUDULE(90400,"字典管理",90000,0,"dict/dict-query",2,1),//字典管理
	DICTMOUDULE_COMMON_ADD(90401,"添加",90400,1,"",3,1),//字典添加
	DICTMOUDULE_COMMON_QUEYR(90402,"查询",90400,1,"",3,2),//字典查询
	DICTMOUDULE_COMMON_EDIT(90403,"修改",90400,1,"",3,3),//字典修改
	DICTMOUDULE_COMMON_DELETE(90404,"删除",90400,1,"",3,4),//字典删除

	POSTERMOUDULE(90500,"海报管理",90000,0,"poster/poster-query",2,1),//海报管理
	POSTERMOUDULE_COMMON_ADD(90501,"添加",90500,1,"",3,1),//海报添加
	POSTERMOUDULE_COMMON_QUEYR(90502,"查询",90500,1,"",3,2),//海报查询
	POSTERMOUDULE_COMMON_EDIT(90503,"修改",90500,1,"",3,3),//海报修改
	POSTERMOUDULE_COMMON_DELETE(90504,"删除",90500,1,"",3,4),//海报删除

	DELIVERYADDRESSMOUDULE(90600,"收货地址管理",90000,0,"deliveryAddress/deliveryAddress-query",2,1),//收货地址管理
	DELIVERYADDRESSMOUDULE_COMMON_ADD(90601,"添加",90600,1,"",3,1),//收货地址添加
	DELIVERYADDRESSMOUDULE_COMMON_QUEYR(90602,"查询",90600,1,"",3,2),//收货地址查询
	DELIVERYADDRESSMOUDULE_COMMON_EDIT(90603,"修改",90600,1,"",3,3),//收货地址修改
	DELIVERYADDRESSMOUDULE_COMMON_DELETE(90604,"删除",90600,1,"",3,4),//收货地址删除

	EPSALEMOUDULE(90700,"竟拍活动管理",90000,0,"epSale/epSale-query",2,1),//竟拍活动管理
	EPSALEMOUDULE_COMMON_ADD(90701,"添加",90700,1,"",3,1),//竟拍活动添加
	EPSALEMOUDULE_COMMON_QUEYR(90702,"查询",90700,1,"",3,2),//竟拍活动查询
	EPSALEMOUDULE_COMMON_EDIT(90703,"修改",90700,1,"",3,3),//竟拍活动修改
	EPSALEMOUDULE_COMMON_DELETE(90704,"删除",90700,1,"",3,4),//竟拍活动删除
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
