package com.hrtx.global;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 短信发送器
 * @author Administrator
 *
 */
public final class Messager {
	
	
	
	private final static Logger log = LoggerFactory.getLogger(Messager.class);
	private static ThreadPoolExecutor pool = null;
	private final static String phoneMatcher = "^1\\d{10,10}$";//移动号码匹配
	
	
	public static void main(String[] args) throws Exception {
		InputStream inStream = null;
		String path = "";//Config.getString("message_url");
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("POST");
		OutputStream out = conn.getOutputStream();
		//method\=sendSms&destNum\={1}&message\={2} 
		out.write("&method=sendSms&destNum=18965902603&message=654fsdaffewfa虽然发疯啊".getBytes());
		out.flush();
		out.close();
		inStream = conn.getInputStream();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = null;
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len = inStream.read(buffer)) !=-1 ){
			outStream.write(buffer, 0, len);
		}
		data = outStream.toByteArray();//网页的二进制数据
		outStream.close();
	}
	//private static final String path = "http://localhost:11111/interfaceprj/isag/SendSmsServlet?method=sendSms&destNum={1}&message={2}";;
	
	/**
	 * 初始化数据
	 */
	public static final void init(){
		pool = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(600), new ThreadPoolExecutor.AbortPolicy());
	}
	
	public final static void send(String phoneNum, String message){
		try {
			if(StringUtils.isBlank(phoneNum)){
				log.error("要短信通知的手机号码为空");
				return;
			}
			String[] phoneNums = phoneNum.split(",");
			for(String phone : phoneNums){
				phone = phone.replaceAll(" ", "");//去除号码之间可能存在的空格
				if(!phone.matches(phoneMatcher)){
					log.error("手机号码【"+phoneNum+"】格式不对");
					return;
				}
				if(StringUtils.isBlank(message)){
					log.error("短信内容为空");
					return;
				}
				log.info("向手机号【"+phone+"】发送信息【"+message+"】");
				message=URLEncoder.encode(message, "UTF-8");
				pool.execute(new MessageTask(phone, message));
			}
		} catch (Exception e) {
			log.error("发送短信异常，短信内容【"+message+"】",e);
		}
	}
	
	public final static void close(){
		if(pool != null) pool.shutdown();
	}
	
}
/**
 * 消息任务
 * @author cqc
 *
 */
class MessageTask implements Runnable{
	private String phone;
	private String message;
	MessageTask( String phone, String message){
		this.phone = phone;
		this.message = message;
	}
	public void run() {
		InputStream inStream = null;
		try {
			String path = SystemParam.get("message_url");
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("POST");
			OutputStream out = conn.getOutputStream();
			out.write(("&method=sendSms&destNum="+phone+"&message="+message).getBytes());
			out.flush();
			out.close();
			inStream = conn.getInputStream();
			readInputStream(inStream);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(inStream!=null){
					inStream.close();
					inStream = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 从输入流中读取数据
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	private final static byte[] readInputStream(InputStream inStream){
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = null;
		try {
			byte[] buffer = new byte[1024];
			int len = 0;
			while( (len = inStream.read(buffer)) !=-1 ){
				outStream.write(buffer, 0, len);
			}
			data = outStream.toByteArray();//网页的二进制数据
			outStream.close();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(outStream!=null){
					outStream.close();
					inStream = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
}
