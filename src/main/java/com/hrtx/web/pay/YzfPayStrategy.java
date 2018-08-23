package com.hrtx.web.pay;

import com.hrtx.dto.Result;
import com.hrtx.global.SystemParam;
import com.hrtx.global.pinganUtils.TLinxAESCoder;
import com.hrtx.global.pinganUtils.TLinxSHA1;
import com.hrtx.global.pinganUtils.YzffqUtil;
import com.hrtx.web.dto.PayRequest;
import com.hrtx.web.dto.PayResponse;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.TreeMap;

@Component
public class YzfPayStrategy implements ThirdPayStrategy {
	private static Logger log = LoggerFactory.getLogger(YzfPayStrategy.class);

	private String PAYLIST = "paylist";
    private String ORDERLIST = "order";
    private String ORDERVIEW = "order/view";
    private String PAYORDER = "payorder";
    private String QUERYPAYSTATUS = "paystatus";
    private String PAYCANCEL = "paycancel";
    private String PAYREFUND = "payrefund";

//	@Value("${yzffq.private.key}")
//	public String privateKey;

    private static YzfPayStrategy payStrategy = null;
    //静态工厂方法
    public static YzfPayStrategy getInstance() {
        if (payStrategy == null) {
            payStrategy = new YzfPayStrategy();
        }
        return payStrategy;
    }

//    private String getPrivateKey(){
//		return this.privateKey;
//	}
    public static String getOpenId(){
		return SystemParam.get("yzffq_open_id");
	}
    public static String getOpenKey(){
		return SystemParam.get("yzffq_open_key");
	}

    public static String getOpenUrl(){
		return SystemParam.get("yzffq_open_url");
	}

	/**
	 * Result code 定义  200=成功；500超时及不确定的异常 ERROR；999明确失败 PARAM
	 * 创建基础请求对象
	 * 1、订单
	 * 2、订单取消
	 * 3、订单退款
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
        if (!YzffqUtil.verifySign(respObject)) {
            log.info("=====验签失败=====");
            return new Result(Result.WARN,"验签失败");
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
            return new Result(Result.WARN,"解密失败");
//			return new Result("B0007","解密失败",null);
        }
	}
	public PayResponse payCancel(PayRequest payRequest) {
		TreeMap<String, String> postmap =getBaseMap();

		TreeMap<String, String> datamap = new TreeMap<String, String>();    // data参数的map

		datamap.put("out_no", payRequest.getOrderNo());
		datamap.put("ord_no", payRequest.getThirdNo());

		/**
		 * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
		 */
		try {
			YzffqUtil.handleEncrypt(datamap, postmap);
		} catch (Exception e) {
			log.error("AES加密失败",e);
			return new PayResponse(Result.ERROR,"加密失败", null, JSONObject.fromObject(datamap).toString(), "", "json", PAYCANCEL, "YZF");
		}

		//		postmap.put("sign_type", "RSA");
//		YzffqUtil.handleSignRSA(postmap, this.getPrivateKey());
		/**
		 * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名
		 */
		YzffqUtil.handleSign(postmap);

		/**
		 * 3 请求、响应
		 */
		String rspStr=null;
		try {
			rspStr = YzffqUtil.handlePost(postmap, this.PAYCANCEL);
		} catch (Exception e) {
			log.error("请求接口失败",e);
			return new PayResponse(Result.WARN,"请求失败", null, JSONObject.fromObject(datamap).toString(), "", "json", PAYCANCEL, "YZF");
		}
		/**
		 * 4 验签  有data节点时才验签
		 *
		 * 5 AES解密，并hex2bin
		 */
		Result result=baseCheck(rspStr);
		if(result.getCode() == Result.OK) return new PayResponse(result.getCode(),"success", result.getData(), JSONObject.fromObject(datamap).toString(), rspStr+"|#|"+String.valueOf(result.getData()), "json", PAYCANCEL, "YZF");
		return new PayResponse(result.getCode(),String.valueOf(result.getData()), null, JSONObject.fromObject(datamap).toString(), rspStr, "json", PAYCANCEL, "YZF");
	}

	public PayResponse payRefund(PayRequest payRequest) {
		TreeMap<String, String> postmap =getBaseMap();

		TreeMap<String, String> datamap = new TreeMap<String, String>();    // data参数的map

		datamap.put("out_no", payRequest.getThirdNo());
		datamap.put("ord_no", "");
		datamap.put("refund_out_no", payRequest.getOrderNo());
		datamap.put("refund_ord_name", "");
		datamap.put("refund_amount", payRequest.getAmt()+"");
		datamap.put("remark", payRequest.getRemark());
		datamap.put("shop_pass", TLinxSHA1.SHA1("123456"));

//		this.saveDateReq(datamap, PinganRefund.class);
		/**
		 * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
		 */
		try {
			YzffqUtil.handleEncrypt(datamap, postmap);
		} catch (Exception e) {
			log.error("AES加密失败",e);
			return new PayResponse(Result.ERROR,"加密失败", null, JSONObject.fromObject(datamap).toString(), "", "json", PAYREFUND, "YZF");
		}

//		postmap.put("sign_type", "RSA");
//		YzffqUtil.handleSignRSA(postmap, this.getPrivateKey());
		/**
		 * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名
		 */
		YzffqUtil.handleSign(postmap);

		/**
		 * 3 请求、响应
		 */
		String rspStr=null;
		try {
			rspStr = YzffqUtil.handlePost(postmap, this.PAYREFUND);
		} catch (Exception e) {
			log.error("请求接口失败",e);
			return new PayResponse(Result.WARN,"请求失败", null, JSONObject.fromObject(datamap).toString(), "", "json", PAYREFUND, "YZF");
		}
		/**
		 * 4 验签  有data节点时才验签
		 *
		 * 5 AES解密，并hex2bin
		 */
		Result result=baseCheck(rspStr);
        if(result.getCode() == Result.OK) return new PayResponse(result.getCode(),"success", result.getData(), JSONObject.fromObject(datamap).toString(), rspStr+"|#|"+String.valueOf(result.getData()), "json", PAYREFUND, "YZF");
        return new PayResponse(result.getCode(),String.valueOf(result.getData()), null, JSONObject.fromObject(datamap).toString(), rspStr, "json", PAYREFUND, "YZF");
	}

	public PayResponse payOrder(PayRequest payRequest) {
		TreeMap<String, String> postmap =getBaseMap();

		TreeMap<String, String> datamap = new TreeMap<String, String>();    // data参数的map

		datamap.put("out_no", payRequest.getOrderNo());
//		datamap.put("pmt_tag", "UYStagPA");
		datamap.put("pmt_tag", "UYStagTY");
//		datamap.put("pmt_tag", "Cash");
		datamap.put("pmt_name", "");
		datamap.put("ord_name", payRequest.getOrderName());
		datamap.put("original_amount", payRequest.getAmt()+"");
		datamap.put("discount_amount", "0");
		datamap.put("ignore_amount", "0");
		datamap.put("trade_amount", payRequest.getAmt()+"");
		datamap.put("remark", payRequest.getRemark());
		datamap.put("tag", null);

		datamap.put("notify_url", payRequest.getAfterUrl());
		datamap.put("uystag_jump", payRequest.getBeforeUrl());

		/**
		 * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
		 */
		try {
			YzffqUtil.handleEncrypt(datamap, postmap);
		} catch (Exception e) {
			log.error("AES加密失败",e);
            return new PayResponse(Result.ERROR,"加密失败", null, JSONObject.fromObject(datamap).toString(), "", "json", PAYORDER, "YZF");
		}

		/**
		 * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名
		 */
		YzffqUtil.handleSign(postmap);
		 /**
		 * 3 请求、响应
		 */
		String rspStr=null;
		try {
			rspStr = YzffqUtil.handlePost(postmap, this.PAYORDER);
		} catch (Exception e) {
			log.error("请求接口未知异常",e);
            return new PayResponse(Result.WARN,"请求失败，未知异常", null, JSONObject.fromObject(datamap).toString(), "", "json", PAYORDER, "YZF");
		}
		/**
		 * 4 验签  有data节点时才验签
		 *
		 * 5 AES解密，并hex2bin
		 */
		Result result=baseCheck(rspStr);
		if(result.getCode() == Result.OK) return new PayResponse(result.getCode(),"success", result.getData(), JSONObject.fromObject(datamap).toString(), rspStr+"|#|"+String.valueOf(result.getData()), "json", PAYORDER, "YZF");
        return new PayResponse(result.getCode(),String.valueOf(result.getData()), null, JSONObject.fromObject(datamap).toString(), rspStr, "json", PAYORDER, "YZF");
	}

	public PayResponse queryOrderList(PayRequest payRequest) {
		return null;
	}

	public PayResponse queryOrderView(String orderNo) {
		TreeMap<String, String> postmap =getBaseMap();
		TreeMap<String, String> datamap = new TreeMap<String, String>();    // data参数的map

		datamap.put("out_no", orderNo);
		datamap.put("ord_no", null);

		/**
		 * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
		 */
		try {
			YzffqUtil.handleEncrypt(datamap, postmap);
		} catch (Exception e) {
			log.error("AES加密失败",e);
            return new PayResponse(Result.ERROR,"加密失败", null, JSONObject.fromObject(datamap).toString(), "", "json", ORDERVIEW, "YZF");
		}

		/**
		 * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名
		 */
		YzffqUtil.handleSign(postmap);
		/**
		 * 3 请求、响应
		 */
		String rspStr=null;
		try {
			rspStr = YzffqUtil.handlePost(postmap, this.ORDERVIEW);
		} catch (Exception e) {
			log.error("请求接口失败",e);
            return new PayResponse(Result.ERROR,"请求失败，未知异常", null, JSONObject.fromObject(datamap).toString(), "", "json", ORDERVIEW, "YZF");
		}
		/**
		 * 4 验签  有data节点时才验签
		 *
		 * 5 AES解密，并hex2bin
		 */
		Result result=baseCheck(rspStr);
		if(result.getCode() == Result.OK) return new PayResponse(result.getCode(),"success", null, JSONObject.fromObject(datamap).toString(), rspStr, "json", ORDERVIEW, "YZF");
        return new PayResponse(result.getCode(),String.valueOf(result.getData()), null, JSONObject.fromObject(datamap).toString(), rspStr, "json", ORDERVIEW, "YZF");
	}

	public PayResponse queryPayList() {
		return null;
	}

}
