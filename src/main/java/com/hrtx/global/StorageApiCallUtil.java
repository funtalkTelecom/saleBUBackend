package com.hrtx.global;

import com.hrtx.dto.Result;
import com.hrtx.dto.StorageInterfaceRequest;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StorageApiCallUtil {
	private static Logger log = LoggerFactory.getLogger(StorageApiCallUtil.class);

	public static Result storageApiCall(Map param, String act_type){
		Result res =null;// new Result(Result.ERROR, "调用接口异常");
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
			log.error("调用接口异常", e);
			return new Result(Result.ERROR, "调用接口异常");
		}
		return res;
	}
}