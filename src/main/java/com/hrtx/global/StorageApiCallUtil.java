package com.hrtx.global;

import com.hrtx.dto.Result;
import com.hrtx.dto.StorageInterfaceRequest;
import com.hrtx.web.dto.StorageInterfaceResponse;
import com.hrtx.web.pojo.Order;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class StorageApiCallUtil {
	private static Logger log = LoggerFactory.getLogger(StorageApiCallUtil.class);

	public static Result storageApiCall(Map param, String act_type){
		Result res = new Result(Result.ERROR, "调用接口异常");
		String merid = SystemParam.get("merid");
		String key = SystemParam.get("key");
		String serial = Utils.randomNoByDateTime();
		try {
			res = HttpUtil.doHttpPost(SystemParam.get("Storage_domain")+"/dispatchRequests.htm",
					JSONObject.fromObject(new StorageInterfaceRequest(
							merid,
							act_type,
							serial,
							key,
							param
					)).toString(),
					"application/json",
					"UTF-8");
		} catch (Exception e) {
			return new Result(Result.ERROR, "调用接口异常");
		}

		return res;
	}
}