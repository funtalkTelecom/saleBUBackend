package com.hrtx.web.controller;

import com.hrtx.dto.Result;
import com.hrtx.global.ImageUtils;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.global.Utils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	protected Result downLoadImg(String path, HttpServletResponse response) {
		return downLoadFile(path, "image/jpeg", response);
	}

	protected Result downLoadFile(String path, HttpServletResponse response) {
		return downLoadFile(path, "application/x-msdownload", response);
	}

	/**
	 * 现在文件
	 * @param path 子路径
	 * @param contentType 下载传类型
	 * @param response
	 * @return
	 */
	protected Result downLoadFile(String path, String contentType, HttpServletResponse response) {
		InputStream fis = null;
		OutputStream os = null;
		try {
			String file_path=SystemParam.get("upload_root_path")+File.separator+path;
			File file = new File(file_path);// path是根据日志路径和文件名拼接出来的
			log.info("获取文件:"+file_path);
			if(!file.exists())return new Result(Result.ERROR,"文件不存在");
			fis = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
//			response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.replaceAll(" ", "").getBytes("utf-8"),"iso8859-1"));
//			response.addHeader("Content-Length", "" + file.length());
			os = new BufferedOutputStream(response.getOutputStream());
			response.setContentType(contentType);
			os.write(buffer);// 输出文件
			return new Result(Result.OK,"success");
		} catch (Exception e) {
			log.error("文件下载失败", e);
			return new Result(Result.ERROR,"文件下载失败");
		}finally {
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
			if(os != null) {
				try {
					os.flush();
					os.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
		}
	}

	/**
	 *
	 * @param subPath  子路径（最后带“/”）
	 * @param file_suffix_s "可上传文件类型"
	 * @param file  文件
	 * @param isScale 是否进行截图操作（截出300和100的子图）
	 * @param isDel 是否删除原图
	 * @return
	 * @throws Exception
	 */
	public static Result uploadFile(String subPath, String file_suffix_s, MultipartFile file, boolean isScale, boolean isDel) throws Exception {
		String path = SystemParam.get("upload_root_path");
		if(StringUtils.isBlank(path)) return new Result(Result.ERROR,"上传根目录未配置，请联系管理员");
		String projectRealPath = path+File.separator+subPath;
		// ==========验证文件后缀start==========//
		if(file == null || file.isEmpty()) return new Result(Result.ERROR, "请选择上传的文件");
		String originalFilename = file.getOriginalFilename();
		String suffix_v = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
		if(!(","+file_suffix_s+",").contains(","+suffix_v+",")){
			return new Result(Result.ERROR, "请上传格式为["+file_suffix_s+"]的文件");
		}
		// ==========验证文件后缀end==========//
		Map<String, Object> map = new HashMap<>();
		String bname = Utils.randomNoByDateTime();
		String sourceServerFileName = bname + "."+suffix_v;
		map.put("sourceServerFileName", sourceServerFileName);  //服务器原文件名称
		map.put("sourceFileName", originalFilename);			//上传文件名
		String fullUrl = projectRealPath + sourceServerFileName;
		File outDir = new File(projectRealPath);
		if (!outDir.exists())  outDir.mkdirs();
//        IOUtils.copy(file.getInputStream(), new FileOutputStream(fullUrl));
		File originalFile = new File(fullUrl);
		FileUtils.copyInputStreamToFile(file.getInputStream(), originalFile);
		if(isScale){
			map.put("thumbnailServerFileName", sourceServerFileName);   //服务器原文件缩略图名称
			if (!new File(projectRealPath+"/300/").exists())  new File(projectRealPath+"/300/").mkdirs();
			ImageUtils.scale(fullUrl, projectRealPath+"/300/"+sourceServerFileName, 300, 300, true);
			if (!new File(projectRealPath+"/1000/").exists())  new File(projectRealPath+"/1000/").mkdirs();
			ImageUtils.scale(fullUrl, projectRealPath+"/1000/"+sourceServerFileName, 1000, 1000, true);
//            map.put("width", widthHeight[0]);
//            map.put("height", widthHeight[1]);
		}
		if(isDel) FileUtils.forceDelete(originalFile);
		return new Result(Result.OK, map);
	}

	void deleteFile(String subPath, String sourceServerFileName) {
		try {
			String path = SystemParam.get("upload_path");
			FileUtils.forceDelete(new File(path+File.separator+subPath+sourceServerFileName));
		} catch (IOException e) {
			log.info("删除文件失败",e);
		}
	};

    protected Map<String,String> getParamMap(HttpServletRequest request) {
		Map<String,String> params = new HashMap<>();
		Enumeration<?> e= request.getParameterNames();
		while (e.hasMoreElements()) {
			String object = (String) e.nextElement();
			String[] values = (String[])request.getParameterValues(object);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(object, valueStr);
		}
		return params;
    }

    protected Map getErrors(List<FieldError> fieldErrors) {
    	Map errors = new HashMap();
		for (FieldError fieldError:fieldErrors) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return errors;
    }

	protected String getParamBody(HttpServletRequest request) {
		InputStream is = null;
		BufferedReader reader = null;
		try {
			is = request.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			StringBuffer buffer = new StringBuffer();
			String line=null;
			while((line = reader.readLine())!=null){
				buffer.append(line);
			}
			return buffer.toString();
		} catch (Exception e) {
			log.error("解析参数流异常",e);
			return null;
		}finally{
			try {
				if(is != null) {
					is.close();
					is = null;
				}
				if(reader != null){
					reader.close();
					reader = null;
				}
			} catch (IOException e) {
				log.error("",e);
			}
		}
	}
}