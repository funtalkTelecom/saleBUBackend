package com.hrtx.web.controller;

import com.hrtx.dto.Result;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.Utils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class BaseReturn {
	private static final long serialVersionUID = 1L;
	protected static Logger log = LoggerFactory.getLogger(BaseReturn.class);

	/**
     * @return 登陆用户真实Ip
     */
	public static String getUserIp(){
		return SessionUtil.getUserIp();
    }

	public static String returnError(String errorMsg){
		return returnJson(Result.ERROR, errorMsg);
	}
	
	public static String returnNoPower(){
		return returnJson(Result.NOPOWER, "抱歉，权限不足！");
	}
	
	public static String returnNoLogin(){
		return returnJson(Result.TIME_OUT, "登录超时,请重新登录！");
	}
	
	public static String returnErrorParam(Object data) {
		return returnJson(Result.PARAM, data);
	}
	
	public static String returnJson(Object data) {
		return returnJson(Result.OK, data);
	}
	
	public static String returnResult(Result res){
		return returnJson(res.getCode(), res.getData());
	}
	
	private static String returnJson(int code, Object data){
		Result res = new Result(code, data);
		JSONObject jobj = JSONObject.fromObject(res);
		return renderJson( jobj.toString());
	}
	
	public static String renderHtml(String text) {
		return render(text, "text/html;charset=UTF-8");
	}
	public static String renderXml(String text) {
		return render(text, "text/xml;charset=UTF-8");
	}
	public static String renderJson(String text) {
		return render(text, "text/json;charset=UTF-8");
	}
	public static String returnOk() {
		return returnJson(Result.OK, "ok");
	}

	public String getJsonObject(Object data){
		return JSONObject.fromObject(data).toString();
	}
	public String getJsonArray(Object data){
		return JSONArray.fromObject(data).toString();
	}
	public static String render(String text, String contentType) {
		PrintWriter w = null;
		try {
			HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
			response.setHeader("Cache-Control", "no-cache");
			response.setContentType(contentType);
			w = response.getWriter();
			w.write(text);
			w.flush();
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if(w != null){
				w.close();
				w = null;
			}
		}
		return null;
	}

    protected String error(String msg) {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		request.setAttribute("message",msg);
		return "error";
    }

    void deleteFile(String path, String sourceServerFileName) {
        try {
            FileUtils.forceDelete(new File(path+sourceServerFileName));
        } catch (IOException e) {
            log.info("删除文件失败",e);
        }
    };

}