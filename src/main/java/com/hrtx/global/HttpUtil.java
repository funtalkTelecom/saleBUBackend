package com.hrtx.global;

import com.hrtx.dto.Result;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.util.*;

public class HttpUtil {
	private static Logger log = LoggerFactory.getLogger(HttpUtil.class);
    public static Result doHttpPost(String url, String sendR, String type, String charset) throws Exception{
        log.info("请求url："+url);
        log.info("请求参数："+sendR);
        log.info("请求contentType："+type);
        log.info("请求字符编码："+charset);
        HttpClient httpClient = new HttpClient();
        HttpMethodBase getMethod = createMethod(url, sendR, type, charset);
        int statusCode = httpClient.executeMethod(getMethod);
        //只要在获取源码中，服务器返回的不是200代码，则统一认为抓取源码失败，返回失败。
        if (statusCode != HttpStatus.SC_OK) {
            log.error("Method failed: " + getMethod.getStatusLine() + "\tstatusCode: " + statusCode);
            return new Result(500, "返回码异常["+statusCode+"]");
        }else{
            return new Result(200, getMethod.getResponseBodyAsString());
        }

    }

    private static HttpMethodBase createMethod(String url, String param, String type, String  charset) throws UnsupportedEncodingException {
        PostMethod method = null;
        method = new PostMethod(url);
        RequestEntity se = new StringRequestEntity(param, type, charset);
        method.setRequestEntity(se);
        //使用系统提供的默认的恢复策略
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        //设置超时的时间
        method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
        return method;
    }
	/**
	 *
	 * HTTP协议POST请求方法
	 */
	public static String sendPost(String url, String params, String charet) throws IOException {
		if (null == charet || "".equals(charet)) {
			charet = "UTF-8";
		}
		StringBuffer sb = new StringBuffer();
		URL urls;
		HttpURLConnection uc = null;
		BufferedReader in = null;
        DataOutputStream out = null;
		try {
			urls = new URL(url);
			uc = (HttpURLConnection) urls.openConnection();
			uc.setRequestMethod("POST");
			uc.setDoOutput(true);
			uc.setDoInput(true);
			uc.setUseCaches(false);
			uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			uc.connect();
			out = new DataOutputStream(uc.getOutputStream());
			out.write(params.getBytes(charet));
			out.flush();
			in = new BufferedReader(new InputStreamReader(uc.getInputStream(),
					charet));
			String readLine = "";
			while ((readLine = in.readLine()) != null) {
				sb.append(readLine);
			}
		} finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
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
			if(StringUtils.equals(SystemParam.get("http-proxy-falg"),"true")){
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.7.1",58080));
				connection = (HttpsURLConnection) realUrl.openConnection(proxy);
			}else{
				connection = (HttpsURLConnection) realUrl.openConnection();
			}
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