package com.hrtx.web.service;

import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.global.pinganUtils.*;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class PinganService {
	private static Logger log = LoggerFactory.getLogger(PinganService.class);

	public static String PAYLIST = "paylist";
	public static String ORDERLIST = "order";
	public static String ORDERVIEW = "order/view";
	public static String PAYORDER = "payorder";
	public static String QUERYPAYSTATUS = "paystatus";
	public static String PAYCANCEL = "paycancel";
	public static String PAYREFUND = "payrefund";

	public static String getPrivateKey(){
		return SystemParam.get("pingan_private_key");
	}
	public static String getOpenId(){
		return SystemParam.get("pingan_open_id");
	}
	public static String getOpenKey(){
		return SystemParam.get("pingan_open_key");
	}
	public static String getOpenUrl(){
		return SystemParam.get("pingan_open_url");
	}



	@Autowired private PinganOrderResMapper pinganOrderResMapper;
	@Autowired private PinganOrderMapper pinganOrderMapper;

	@Autowired private PinganCancelMapper pinganCancelMapper;
	@Autowired private PinganCancelResMapper pinganCancelResMapper;

	@Autowired private PinganRefundMapper pinganRefundMapper;
	@Autowired private PinganRefundResMapper pinganRefundResMapper;
	@Autowired private ApiSessionUtil apiSessionUtil;

	/**
	 * Result code 定义  200=成功；500超时及不确定的异常 ERROR；999明确失败 PARAM
	 * 创建基础请求对象
	 * 1、订单
	 * 2、订单取消
	 * 3、订单退款
	 *
	 *
	 */

	private TreeMap<String, String> getBaseMap(){
		// 初始化参数
		String timestamp = new Date().getTime() / 1000 + "";    // 时间
		// 固定参数
		TreeMap<String, String> postmap = new TreeMap<String, String>();    // 请求参数的map
		postmap.put("open_id", this.getOpenId());
		postmap.put("timestamp", timestamp);
		return postmap;
	}

	private Result baseCheck(String rspStr){
		log.info(String.format("接口返回结果[%s]",rspStr));
		/**
		 * 4 验签  有data节点时才验签
		 */
		JSONObject respObject = JSONObject.fromObject(rspStr);
        /*if(!respObject.containsKey("data")){
        	log.info("=====没有返回data数据=====");
			return new Result("B0007","接口返回数据错误",null);
        }*/
        if("99999".equals(respObject.getString("errcode"))) return new Result(Result.WARN,respObject.getString("msg"));
        if(!"0".equals(respObject.getString("errcode"))) return new Result(Result.ERROR,respObject.getString("msg"));
		Object dataStr= respObject.get("data");
		if (!(!rspStr.isEmpty() || ( dataStr != null ))) {
			log.info("=====没有返回data数据=====");
			return new Result(Result.ERROR,"接口返回数据错误");
//			return new Result("B0007","接口返回数据错误",null);
		}
//		if(!respObject.containsKey("data"))return new Result(Result.ERROR,respObject.getString("msg"));//return new Result("B0008",respObject.getString("msg"),null);
		//return new Result("B0008",respObject.getString("msg"),null);
		if (!TLinx2Util.verifySign(respObject)) {
			log.info("=====验签失败=====");
			return new Result(Result.PARAM,"验签失败");
//			return new Result("B0007","验签失败",null);
		}
		log.info("验签成功");
		/**
		 * 5 AES解密，并hex2bin
		 */
		try {
			String respData = TLinxAESCoder.decrypt(dataStr.toString(),this.getOpenKey());
			log.info("==================响应data内容:" + respData);
			respObject = JSONObject.fromObject(respData);
			return new Result(Result.OK,respObject);
//			return new Result("00000","验签成功",respObject);
		} catch (Exception e) {
			log.error("AES解密失败",e);
			return new Result(Result.ERROR,"解密失败");
//			return new Result("B0007","解密失败",null);
		}
	}
	public Result payCancel(String ordNo, String outNo) {
		TreeMap<String, String> postmap =getBaseMap();

		TreeMap<String, String> datamap = new TreeMap<String, String>();    // data参数的map

		datamap.put("out_no", outNo);
		datamap.put("ord_no", ordNo);
		this.saveDateReq(datamap, PinganCancel.class);
		/**
		 * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
		 */
		try {
			TLinx2Util.handleEncrypt(datamap, postmap);
		} catch (Exception e) {
			log.error("AES加密失败",e);
			return new Result(Result.ERROR,"加密失败");
//			return new Result("B0008","加密失败",null);
		}

		//签名方式
		postmap.put("sign_type", "RSA");

		/**
		 * 2 请求参数签名 按A~z排序，串联成字符串，先进行RSA签名
		 */
		TLinx2Util.handleSignRSA(postmap);

		/**
		 * 3 请求、响应
		 */
		String rspStr=null;
		try {
			rspStr = TLinx2Util.handlePost(postmap, this.PAYCANCEL);
		} catch (Exception e) {
			log.error("请求接口失败",e);
			return new Result(Result.ERROR,"请求失败");
//			return new Result("B0007","请求失败",null);
		}
		/**
		 * 4 验签  有data节点时才验签
		 *
		 * 5 AES解密，并hex2bin
		 */
		Result result=baseCheck(rspStr);
		this.saveDateResp(result,rspStr,PinganCancelRes.class);
		return result;
	}

	public Result payRefund(String outNo, String refundOutNo,
							String refundOrdName, Integer refundAmount, String tradeAccount,
							String tradeNo, String tradeResult, String tmlToken, String remark,
							String shopPass) {
		TreeMap<String, String> postmap =getBaseMap();

		TreeMap<String, String> datamap = new TreeMap<String, String>();    // data参数的map

		datamap.put("out_no", outNo);
		datamap.put("refund_out_no", refundOutNo);
		datamap.put("refund_ord_name", refundOrdName);
		datamap.put("refund_amount", refundAmount + "");
		datamap.put("trade_account", tradeAccount);
		datamap.put("trade_no", tradeNo);
		datamap.put("trade_result", tradeResult);
		datamap.put("tml_token", tmlToken);
		datamap.put("remark", remark);
		datamap.put("shop_pass", shopPass);

		this.saveDateReq(datamap, PinganRefund.class);
		/**
		 * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
		 */
		try {
			TLinx2Util.handleEncrypt(datamap, postmap);
		} catch (Exception e) {
			log.error("AES加密失败",e);
			return new Result(Result.PARAM,"加密失败");
//			return new Result("B0008","加密失败",null);
		}
		postmap.put("sign_type", "RSA");
		/**
		 * 2 请求参数签名 按A~z排序，串联成字符串，进行RSA签名
		 */
		TLinx2Util.handleSignRSA(postmap);

		/**
		 * 3 请求、响应
		 */
		String rspStr=null;
		try {
			rspStr = TLinx2Util.handlePost(postmap, this.PAYREFUND);
		} catch (Exception e) {
			log.error("请求接口失败",e);
			return new Result(Result.ERROR,"请求失败");
//			return new Result("B0007","请求失败",null);
		}
		/**
		 * 4 验签  有data节点时才验签
		 *
		 * 5 AES解密，并hex2bin
		 */
		Result result=baseCheck(rspStr);
		this.saveDateResp(result,rspStr,PinganRefundRes.class);
		return result;
	}
	public Result payRefund(String outNo, String refundOutNo,
							String refundOrdName, Integer refundAmount, String remark) {
		return this.payRefund(outNo, refundOutNo, refundOrdName, refundAmount, null, null, null, null, remark,TLinxSHA1.SHA1("123456"));
	}

	private Result saveDateReq(Map<String, String> datamap,Class<?> bean_name) {
		Map<String,Object> _resp=new HashMap<>();
		Iterator<?> iterator=datamap.keySet().iterator();
		while (iterator.hasNext()) {
			String key=(String) iterator.next();
			_resp.put(key,datamap.get(key));
		}
		return this.saveDate(_resp,bean_name);
	}
    private Result saveDateResp(Result result,String rspStr,Class<?> bean_name) {
		Map<String,Object> _resp=new HashMap<>();
		JSONObject respObject=JSONObject.fromObject(rspStr);
		Iterator<?> iterator=respObject.keySet().iterator();
		while (iterator.hasNext()) {
			String key=(String) iterator.next();
			String value=respObject.get(key).toString();
			_resp.put(key, value);
		}
		if(result.getCode()==Result.OK)_resp.put("check_sign",1);
		else _resp.put("check_sign",2);
		return this.saveDate(_resp,bean_name);
	}
    private Result saveDate(Map<String, Object> datamap,Class<?> bean_name) {
		Object bean=null;
		log.info(String.format("实例化[%s]对象",bean_name.getName()));
		try {
			bean=bean_name.newInstance();
			BeanUtils.setProperty(bean,"id",new PinganOrder().getGeneralId());
		} catch (Exception e) {
			log.error("对象无法实例化",e);
			return new Result(Result.PARAM,"无法实例化对象，保存失败");
		}
		Iterator<String> iterator=datamap.keySet().iterator();
		while (iterator.hasNext()) {
			String _key=iterator.next();
			try {
				BeanUtils.setProperty(bean,_key,datamap.get(_key));
			} catch (Exception e) {
				log.error(String.format("属性[%s]不存在,值[%s]",_key,datamap.get(_key)));
			}
		}
		if(bean instanceof PinganOrder) this.pinganOrderMapper.insert((PinganOrder)bean);
		if(bean instanceof PinganOrderRes) this.pinganOrderResMapper.insert((PinganOrderRes)bean);
		if(bean instanceof PinganCancel) this.pinganCancelMapper.insert((PinganCancel)bean);
		if(bean instanceof PinganCancelRes) this.pinganCancelResMapper.insert((PinganCancelRes)bean);
		if(bean instanceof PinganRefund) this.pinganRefundMapper.insert((PinganRefund)bean);
		if(bean instanceof PinganRefundRes) this.pinganRefundResMapper.insert((PinganRefundRes)bean);
		log.info("记录保存成功");
		return new Result(Result.OK,"保存成功");
	}

	public static void main(String[] args) {
		/*String jumpUrl="http://local.ttg.cn:8400/tlinx2apidemo1/callback/scanpay_cashier/payResult";
		String notifyUrl="http://local.ttg.cn:8400/tlinx2apidemo1/callback/scanpay_cashier/payResult";
		new PinganServiceImpl().payOrderAliWin("H81685228", "AlipayCS","支付宝支付测试",1,1,null,jumpUrl, notifyUrl,null,null);*/
//		new PinganServiceImpl().payCancel("h81685223","");
	}

	/**
	 * 创建微信App支付
	 * @param outNo
	 * @param pmtTag
	 * @param ordName
	 * @param originalAmount
	 * @param tradeAmount
	 * @param remark
	 * @param notifyUrl
	 * @return
	 */
	public Result payOrder(String outNo, String pmtTag,String ordName, Integer originalAmount,Integer tradeAmount,
								 String remark,String notifyUrl,String sub_appid, String sub_openid, String trade_type){
		return this.payOrder(outNo, pmtTag,null, ordName, originalAmount,0, 0,
				tradeAmount,null, null, null, remark, null, null, null,
				notifyUrl,null, null, null,null, null,sub_appid,sub_openid,trade_type);
	}

	public Result payOrder(String outNo, String pmtTag, String pmtName,
						   String ordName, Integer originalAmount, Integer discountAmount,
						   Integer ignoreAmount, Integer tradeAmount, String tradeAccount,
						   String tradeNo, String tradeResult, String remark, String authCode,
						   String tag, String jumpUrl, String notifyUrl, String wx_appid, String wx_limit_pay,
						   String wx_goods_tag, String ali_goods_detail, String ali_extend_params,
						   String sub_appid, String sub_openid, String trade_type
	) {
		TreeMap<String, String> postmap =getBaseMap();

		TreeMap<String, String> datamap = new TreeMap<String, String>();    // data参数的map

		datamap.put("out_no", outNo);
		datamap.put("pmt_tag", pmtTag);
		datamap.put("pmt_name", pmtName);
		datamap.put("ord_name", ordName);
		datamap.put("original_amount", originalAmount+"");
		datamap.put("discount_amount", discountAmount+"");
		datamap.put("ignore_amount", ignoreAmount+"");
		datamap.put("trade_amount", tradeAmount+"");
		datamap.put("trade_account", tradeAccount);
		datamap.put("trade_no", tradeNo);
		datamap.put("trade_result", tradeResult);
		datamap.put("remark", remark);
		datamap.put("auth_code", authCode);
		datamap.put("tag", tag);
		datamap.put("jump_url", jumpUrl);
		datamap.put("notify_url", notifyUrl);
		datamap.put("wx_appid", wx_appid);
		datamap.put("limit_pay", wx_limit_pay);
		datamap.put("goods_tag", wx_goods_tag);
		datamap.put("goods_detail", ali_goods_detail);
		datamap.put("extend_params", ali_extend_params);

		datamap.put("sub_appid", sub_appid);
		datamap.put("sub_openid", sub_openid);
		datamap.put("trade_type",trade_type);


		this.saveDateReq(datamap,PinganOrder.class);

		/**
		 * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
		 */
		try {
			TLinx2Util.handleEncrypt(datamap, postmap);
		} catch (Exception e) {
			log.error("AES加密失败",e);
			return new Result(Result.PARAM,"加密失败");
		}

		/**
		 * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名
		 */
		TLinx2Util.handleSign(postmap);
		 /**
		 * 3 请求、响应
		 */
		String rspStr=null;
		try {
			rspStr = TLinx2Util.handlePost(postmap, this.PAYORDER);
		} catch (Exception e) {
			log.error("请求接口未知异常",e);
			return new Result(Result.WARN,"未知异常");
		}
		/**
		 * 4 验签  有data节点时才验签
		 *
		 * 5 AES解密，并hex2bin
		 */
		Result result=baseCheck(rspStr);

		this.saveDateResp(result,rspStr,PinganOrderRes.class);

		return result;
	}

	public Result queryOrderList(String outNo, String tradeNo, String ordNo,
								 String pmtTag, String ordType, String status, String sdate,
								 String edate) {
		TreeMap<String, String> postmap =getBaseMap();

		TreeMap<String, String> datamap = new TreeMap<String, String>();//data参数的map
		datamap.put("out_no", outNo);
		datamap.put("trade_no", tradeNo);
		datamap.put("ord_no", ordNo);
		datamap.put("pmt_tag", pmtTag);
		datamap.put("ord_type", ordType);
		datamap.put("status", status);
		datamap.put("sdate", sdate);
		datamap.put("edate", edate);

		/**
		 * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
		 */
		try {
			TLinx2Util.handleEncrypt(datamap, postmap);
		} catch (Exception e) {
			log.error("AES加密失败",e);
			return new Result(Result.PARAM,"加密失败");
		}

		/**
		 * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名
		 */
		TLinx2Util.handleSign(postmap);

		/**
		 * 3 请求、响应
		 */
		String rspStr=null;
		try {
			rspStr = TLinx2Util.handlePost(postmap, this.ORDERLIST);
		} catch (Exception e) {
			log.error("请求接口失败",e);
			return new Result(Result.ERROR,"请求失败");
		}
		/**
		 * 4 验签  有data节点时才验签
		 *
		 * 5 AES解密，并hex2bin
		 */
		Result result=baseCheck(rspStr);
		return result;
	}

	public Result queryOrderView(String outNo) {
		TreeMap<String, String> postmap =getBaseMap();
		TreeMap<String, String> datamap = new TreeMap<String, String>();    // data参数的map

		datamap.put("out_no", outNo);

		/**
		 * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
		 */
		try {
			TLinx2Util.handleEncrypt(datamap, postmap);
		} catch (Exception e) {
			log.error("AES加密失败",e);
			return new Result(Result.PARAM,"加密失败");
		}

		/**
		 * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名
		 */
		TLinx2Util.handleSign(postmap);
		/**
		 * 3 请求、响应
		 */
		String rspStr=null;
		try {
			rspStr = TLinx2Util.handlePost(postmap, this.ORDERVIEW);
		} catch (Exception e) {
			log.error("请求接口失败",e);
			return new Result(Result.ERROR,"请求失败");
		}
		/**
		 * 4 验签  有data节点时才验签
		 *
		 * 5 AES解密，并hex2bin
		 */
		Result result=baseCheck(rspStr);
		return result;
	}

	public Result queryPayList() {
		// 初始化参数
		String pmtType   = "0,1,2,3";
		TreeMap<String, String> postmap =getBaseMap();

		TreeMap<String, String> datamap = new TreeMap<String, String>();//data参数的map
		datamap.put("pmt_type", pmtType);

		/**
		 * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
		 */
		try {
			TLinx2Util.handleEncrypt(datamap, postmap);
		} catch (Exception e) {
			log.error("AES加密失败",e);
			return new Result(Result.PARAM,"加密失败");
		}

		/**
		 * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名
		 */
		TLinx2Util.handleSign(postmap);
		/**
		 * 3 请求、响应
		 */
		String rspStr=null;
		try {
			rspStr = TLinx2Util.handlePost(postmap, this.PAYLIST);
		} catch (Exception e) {
			log.error("请求接口失败",e);
			return new Result(Result.ERROR,"请求失败");
		}
		/**
		 * 4 验签  有data节点时才验签
		 *
		 * 5 AES解密，并hex2bin
		 */
		Result Result=baseCheck(rspStr);
		return Result;
	}

	public Result queryPayStatus(String ordNo, String outNo) {
		TreeMap<String, String> postmap =getBaseMap();

		TreeMap<String, String> datamap = new TreeMap<String, String>();    // data参数的map

		datamap.put("out_no", outNo);
		datamap.put("ord_no", ordNo);

		/**
		 * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
		 */
		try {
			TLinx2Util.handleEncrypt(datamap, postmap);
		} catch (Exception e) {
			log.error("AES加密失败",e);
			return new Result(Result.PARAM,"加密失败");
		}

		/**
		 * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名
		 */
		TLinx2Util.handleSign(postmap);
		/**
		 * 3 请求、响应
		 */
		String rspStr=null;
		try {
			rspStr = TLinx2Util.handlePost(postmap, this.QUERYPAYSTATUS);
		} catch (Exception e) {
			log.error("请求接口失败",e);
			return new Result(Result.ERROR,"请求失败");
		}
		/**
		 * 4 验签  有data节点时才验签
		 *
		 * 5 AES解密，并hex2bin
		 */
		Result Result=baseCheck(rspStr);

		return Result;
	}
	/**
	 * 4.9	订单退款查询接口
	 * @param refund_out_no	退款订单的开发者流水号
	 * @param refund_ord_no	退款订单号（我行返回的订单号）
	 * @return
	 */
	public Result queryPayRefund(String refund_out_no, String refund_ord_no) {
		TreeMap<String, String> postmap =getBaseMap();

		TreeMap<String, String> datamap = new TreeMap<String, String>();    // data参数的map

		datamap.put("refund_out_no", refund_out_no);
		datamap.put("refund_ord_no", refund_ord_no);

		/**
		 * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
		 */
		try {
			TLinx2Util.handleEncrypt(datamap, postmap);
		} catch (Exception e) {
			log.error("AES加密失败",e);
			return new Result(Result.PARAM,"加密失败");
		}

		/**
		 * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名
		 */
		TLinx2Util.handleSign(postmap);
		/**
		 * 3 请求、响应
		 */
		String rspStr=null;
		try {
			rspStr = TLinx2Util.handlePost(postmap, this.QUERYPAYSTATUS);
		} catch (Exception e) {
			log.error("请求接口失败",e);
			return new Result(Result.ERROR,"请求失败");
		}
		/**
		 * 4 验签  有data节点时才验签
		 *
		 * 5 AES解密，并hex2bin
		 */
		Result Result=baseCheck(rspStr);

		return Result;
	}



}
