package com.hrtx.global;


public enum PowerConsts {
	//编码采用5位数，第1位表示1级菜单，第2，3位表示2级菜单，第4，5位表示3级菜单
	//id 权限名称 父亲id 叶子  url 菜单等级  seq
	NOPOWER(-1,"",0,0,"",0,111111),//不需要权限
	NOLOGINPOWER(-2,"免登陆且无需权限",0,0,"",0, 1),
	SYSTEM(1,"靓号优选",0,0,"",0,111112),

	/********************* 系统管理模块start **********************/
	SYSTEMMODULE(90000,"系统管理",1,0,"",1,4),
	/**管理员   区分是否是管理员和经销商  无效单独每个节点配置，特殊权限可以各自节点配置*/
	
	SYSTEMMOUULE_USERLIST_ALL(90001,"管理员",90000,1,"",3,5),//区分是否是管理员和经销商

	SYSMOUDULE(90300,"配置管理",90000,0,"system/system-query",2,1),//配置管理
	SYSMOUDULE_COMMON_QUEYR(90301,"查询",90300,1,"",3,1),//配置查询
	SYSMOUDULE_COMMON_EDIT(90302,"修改",90300,1,"",3,2),//配置修改
	SYSMOUDULE_COMMON_AUDIT(90303,"审核",90300,1,"",3,3),//配置审核
	SYSMOUDULE_COMMON_ADD(90304,"添加",90300,1,"",3,4),//配置添加
	SYSMOUDULE_COMMON_DELETE(90305,"删除",90300,1,"",3,4),//配置删除

	DICTMOUDULE(90400,"字典管理",90000,0,"dict/dict-query",2,2),//字典管理
	DICTMOUDULE_COMMON_ADD(90401,"添加",90400,1,"",3,1),//字典添加
	DICTMOUDULE_COMMON_QUEYR(90402,"查询",90400,1,"",3,2),//字典查询
	DICTMOUDULE_COMMON_EDIT(90403,"修改",90400,1,"",3,3),//字典修改
	DICTMOUDULE_COMMON_DELETE(90404,"删除",90400,1,"",3,4),//字典删除

	SYSTEMMOUULE_USERLIST(90200,"用户管理",90000,0,"user/query-user",2,3),
	SYSTEMMOUULE_USERLIST_LIST(90201,"查询",90200,1,"",3,2), 
//	SYSTEMMOUULE_USERLIST_DISTRIBUTE(90202,"分配角色",90200,1,"",3,2),
	SYSTEMMOUULE_USERLIST_ADD(90203,"添加",90200,1,"",3,3),
//	SYSTEMMOUULE_USERLIST_EDIT(90204,"修改",90200,1,"",3,3),
//	SYSTEMMOUULE_USERLIST_UPPWD(90205,"修改密码",90200,1,"",3,6),

	SYSTEMMOUULE_PERMISSION(90100,"角色授权",90000,0,"user/query-role",2,4),
	SYSTEMMOUULE_ADDROLE_PERMISSION(90101,"添加角色",90100,1,"",3,2),             //角色管理

	POSTERMOUDULE(90500,"海报管理",90000,0,"poster/poster-query",2,5),//海报管理
	POSTERMOUDULE_COMMON_ADD(90501,"添加",90500,1,"",3,1),//海报添加
	POSTERMOUDULE_COMMON_QUEYR(90502,"查询",90500,1,"",3,2),//海报查询
	POSTERMOUDULE_COMMON_EDIT(90503,"修改",90500,1,"",3,3),//海报修改
	POSTERMOUDULE_COMMON_DELETE(90504,"删除",90500,1,"",3,4),//海报删除

	PARTNER_MOUULE(91100,"合伙人管理",90000,0,"partner/partner-index",2,6),
	PARTNER_MOUULE_QUERY(91101,"查询",91100,1,"",3,1),
	PARTNER_MOUULE_EDIT(91102,"审核",91100,1,"",3,1),

	CHANNELMOUDULE(91200,"渠道和价格规则管理",90000,0,"channel/channel-query",2,7),
	CHANNELMOUDULE_COMMON_QUEYR(91201,"查询",91200,1,"",3,1),//渠道和价格规则查询
	CHANNELMOUDULE_COMMON_EDIT(91202,"修改",91200,1,"",3,2),//修改
	CHANNELMOUDULE_COMMON_DELETE(91203,"删除",91200,1,"",3,3),//删除
	CHANNELMOUDULE_COMMON_ADD(91204,"添加",91200,1,"",3,4),//添加

	/**************************商家管理*******************************************/
	SALEMOUDULE(70000,"商家管理",1,0,"",1,3),//销售管理

	GOODSMOUDULE(70200,"商品管理",70000,0,"goods/goods-query",2,1),//商品管理
	GOODSMOUDULE_COMMON_QUEYR(70201,"查询",70200,1,"",3,1),//商品查询
	GOODSMOUDULE_COMMON_EDIT(70202,"修改",70200,1,"",3,2),//商品修改
	GOODSMOUDULE_COMMON_DELETE(70203,"删除",70200,1,"",3,3),//商品删除
	GOODSMOUDULE_COMMON_ADD(70204,"添加",70200,1,"",3,4),//商品添加

	NUMBERMOUDULE(70300,"号码管理",70000,0,"number/number-query",2,2),//号码管理
	NUMBERMOUDULE_COMMON_QUEYR(70301,"查询",70300,1,"",3,1),//号码查询
	NUMBERMOUDULE_COMMON_ADDTAG(70302,"号码添加标签",70300,1,"",3,2),//号码添加标签
	NUMBERMOUDULE_COMMON_SL(70303,"受理",70300,1,"",3,2),//受理操作
	NUMBERMOUDULE_COMMON_STOP_SALE(70304,"停售",70300,1,"",3,2),//停售操作

	ORDERMOUDULE(70600,"订单管理",70000,0,"order/order-query",2,3),
	ORDERMOUDULE_COMMON_QUEYR(70601,"查询",70600,1,"",3,1),//订单查询
	ORDERMOUDULE_COMMON_RECEIPT(70602,"收款",70600,1,"",3,2),//订单收款
	ORDERMOUDULE_COMMON_BINDCARD(70603,"绑卡",70600,1,"",3,3),//订单绑卡
	ORDERMOUDULE_COMMON_REFUND(70604,"线下单退款",70600,1,"",3,4),//线下单退款
	ORDERMOUDULE_COMMON_REFUND_LIVE(70605,"线上单退款",70600,1,"",3,5),//线上单退款
	ORDERMOUDULE_COMMON_CHECK(70606,"审核",70600,1,"",3,6),//
	ORDERMOUDULE_COMMON_CANCEL(70607,"取消订单",70600,1,"",3,7),//

	MEALMOUDULE(70100,"套餐管理",70000,0,"meal/query-meal",2,4),//套餐管理
	MEALMOUDULE_COMMON_QUEYR(70101,"查询",70100,1,"",3,1),//套餐查询
	MEALMOUDULE_COMMON_IMPORT(70102,"导入",70100,1,"",3,2),//套餐导入
	MEALMOUDULE_COMMON_EDIT(70103,"修改",70100,1,"",3,3),//套餐修改
	MEALMOUDULE_COMMON_DELETE(70104,"删除",70100,1,"",3,4),//套餐删除

	AGENTMOUDULE(70500,"代理商管理",70000,0,"agent/query-agent",2,5),
	AGENTMOUDULE_COMMON_QUEYR(70501,"查询",70500,1,"",3,1),//管代理商理查询
	AGENTMOUDULE_COMMON_CHECK(70502,"审核",70500,1,"",3,2),//管代理商理审核
	AGENTMOUDULE_COMMON_UPDATE(70503,"修改",70500,1,"",3,3),//管代理商理修改渠道

	LIANGHAOMOUDULE(70700,"客服靓号",70000,0,"lianghao/lianghao-query",2,6),
	LIANGHAOMOUDULE_COMMON_QUEYR(70701,"查询",70700,1,"",3,1),//靓号查询
	LIANGHAOMOUDULE_COMMON_FREEZE(70702,"冻结",70700,1,"",3,2),//冻结
	LIANGHAOMOUDULE_COMMON_ADD(70703,"下单",70700,1,"",3,2),//下单

	NUMPRICEMOUDULE(70800,"号码价格管理",70000,0,"numprice/numprice-query",2,7),
	NUMPRICEMOUDULE_COMMON_QUEYR(70801,"查询",70800,1,"",3,1),//号码价格查询
	NUMPRICEMOUDULE_COMMON_EDIT(70802,"修改",70800,1,"",3,2),//修改

	PROMOTIONPLAN(71100,"推广计划",70000,0,"partner/promotion-plan-index",2,8),//佣金管理
	PROMOTIONPLAN_QUERY(71101,"查询",71100,1,"",3,1),
	PROMOTIONPLAN_EDIT(71102,"编辑",71100,1,"",3,1),

	ACTIVITYMOUDULE(70900,"活动管理",70000,0,"activity/activity-query",2,9),
	ACTIVITYMOUDULE_COMMON_ADD(70901,"添加",70900,1,"",3,1),//活动添加
	ACTIVITYMOUDULE_COMMON_QUEYR(70902,"查询",70900,1,"",3,2),//活动查询
	ACTIVITYMOUDULE_COMMON_CANCEL(70903,"取消",70900,1,"",3,3),//活动取消

	EPSALEMOUDULE(71200,"竞拍活动管理",70000,0,"epSale/epSale-query",2,10),//竞拍活动管理
	EPSALEMOUDULE_COMMON_ADD(71201,"添加",71200,1,"",3,1),//竞拍活动添加
	EPSALEMOUDULE_COMMON_QUEYR(71202,"查询",71200,1,"",3,2),//竞拍活动查询
	EPSALEMOUDULE_COMMON_EDIT(71203,"修改",71200,1,"",3,3),//竞拍活动修改
	EPSALEMOUDULE_COMMON_DELETE(71204,"删除",71200,1,"",3,4),//竞拍活动删除

	/********************************报表管理*************************************/
	REPORTMODULE(60000,"报表管理",1,0,"",1,9),
	REPORTMODULE_COMMON(60900,"通用报表",60000,0,"searchCommon.htm",2,99),   //通用报表
	REPORTMODULE_COMMON_QUERY(60901,"查询",60900,1,"",3,1),   //通用查询报表
	REPORTMODULE_COMMON_EXPORT(60902,"导出",60900,1,"",3,2),   //通用导出报表

	/*******************************个人中心**************************************/
	USERCENTRE(80000,"个人中心",1,0,"",1,10),

	ACCOUNTMOUDULE(80400,"收款账号管理",80000,0,"account/query-account",2,1),
	ACCOUNTMOUDULE_COMMON_QUEYR(80401,"查询",80400,1,"",3,1),//收款账号管理查询
	ACCOUNTMOUDULE_COMMON_EDIT(80402,"修改",80400,1,"",3,2),//收款账号管理修改
	ACCOUNTMOUDULE_COMMON_DELETE(80403,"删除",80400,1,"",3,3),//收款账号管理删除

	SYSTEMMOUULE_UPDATE_PWD(80800,"修改密码",80000,1,"/update-pwd-index",2,100),
	;

	
	public int getId() {
		return id;
	}
	public String getPowerName() {
		return powerName;
	}
	public int getPid() {
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

	private int id;                 //编号
	private String powerName;       //权限名, 菜单名
	private int pid;                //父编号
	private int leaf;               //叶子，是否无子类(1表示是叶子，0表示不是)
	private String url;             //菜单地址
	private int grade;              //菜单等级
	private int seq;
	private PowerConsts(int id,String powerName,int pid,int leaf, String url, int grade, int seq){
		this.id = id;
		this.powerName = powerName;
		this.pid = pid;
		this.leaf = leaf;
		this.url = url;
		this.grade = grade;
		this.seq = seq;
	}
}
