package com.hrtx.global;

import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class HttpUtil {
	private static Logger log = LoggerFactory.getLogger(HttpUtil.class);

	/**
	 *
	 * HTTP协议POST请求方法
	 */
	public static String sendPost(String url, String params, String charet) {
		if (null == charet || "".equals(charet)) {
			charet = "UTF-8";
		}
		StringBuffer sb = new StringBuffer();
		URL urls;
		HttpURLConnection uc = null;
		BufferedReader in = null;
		try {
			urls = new URL(url);
			uc = (HttpURLConnection) urls.openConnection();
			uc.setRequestMethod("POST");
			uc.setDoOutput(true);
			uc.setDoInput(true);
			uc.setUseCaches(false);
			uc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			uc.connect();
			DataOutputStream out = new DataOutputStream(uc.getOutputStream());
			out.write(params.getBytes(charet));
			out.flush();
			out.close();
			in = new BufferedReader(new InputStreamReader(uc.getInputStream(),
					charet));
			String readLine = "";
			while ((readLine = in.readLine()) != null) {
				sb.append(readLine);
			}
			if (in != null) {
				in.close();
			}
			if (uc != null) {
				uc.disconnect();
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (uc != null) {
				uc.disconnect();
			}
		}
		return sb.toString();
	}

	public static String sendPost(String url, Map<String, ?> paramMap) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";

		String param = "";
		Iterator<String> it = paramMap.keySet().iterator();

		while (it.hasNext()) {
			String key = it.next();
			param += key + "=" + paramMap.get(key) + "&";
		}

		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		//使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}


	//发起get请求的方法。
	public static String get(String url,String param){
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			//System.out.println(urlNameString);
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
            /*for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }*/
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader( connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	public static JSONObject doGetStr(String url,String param){
		DefaultHttpClient httpClient = new DefaultHttpClient();
		String urlNameString = url + "?" + param;
		HttpGet httpGet = new HttpGet(urlNameString);
		JSONObject jsonObject = null;
		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();   //接受结果
			if(entity != null){
				String result = EntityUtils.toString(entity,"UTF-8");
				jsonObject = JSONObject.fromObject(result);
			}

		}catch (IOException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
}