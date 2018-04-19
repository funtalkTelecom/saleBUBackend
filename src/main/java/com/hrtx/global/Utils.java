package com.hrtx.global;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Encoder;

public class Utils {
	private static Logger log = LoggerFactory.getLogger(Utils.class);
	
	
	private static MessageDigest md5 = null;
	private static BASE64Encoder base64Encoder = null;
	/**
	 * 使用md5对消息进行加密
	 * cqc
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 */
	public final static String encodeByMD5(String msg) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		if(md5 == null){
			md5 = MessageDigest.getInstance("MD5");
		}
		if(base64Encoder == null){
			base64Encoder = new BASE64Encoder();
		}
		return base64Encoder.encode(md5.digest(ObjectUtils.toString(msg).getBytes("UTF-8")));
	}
    
    public final static String doHttpPost(Map<String, String> map,String post_url) throws Exception{
		log.info("请求url:"+post_url);
		log.info("请求参数:"+map);
		HttpURLConnection conn = null;
		String imeis = "";
		Set<String> s = map.keySet();
		Collection<String> c = map.values();
		for (Iterator<String> iterator = c.iterator(),iterator1 = s.iterator(); iterator.hasNext();) {
			String value = (String) iterator.next();
			String key = (String) iterator1.next();
			imeis+="&"+key+"="+value;
		}
		OutputStream out = null;
		InputStream inStream = null;
		BufferedReader rd = null;
		StringBuffer tempStr = new StringBuffer();
		try{
			URL url = new URL(post_url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			out = conn.getOutputStream();
			out.write(imeis.getBytes("UTF-8"));
			out.flush();
			out.close();
			inStream = conn.getInputStream();
			rd = new BufferedReader(new InputStreamReader(inStream,"UTF-8"));
			String tempLine = rd.readLine();
			while (tempLine != null) {
				tempStr.append(tempLine);
				tempLine = rd.readLine();
			}
		}finally{
			try {
				if(rd != null){
					rd.close();
					rd = null;
				}
				if(out != null){
					out.close();
					out = null;
				}
				if(inStream != null){
					inStream.close();	
					inStream = null;
				}
				if(conn != null){
					conn.disconnect();	
					conn = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return tempStr.toString();
	}
    
    /**
     * @param offset  距离当前时间的 天偏移量
     * @param format  返回字符串格式
     * @return
     */
    public static String getDate(int offset,String format){
    	Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, c.get(Calendar.DATE)+offset);
    	return new SimpleDateFormat(format,Locale.ENGLISH).format(c.getTime());
    }
    
    public static String getDate(int offset, Date date,String format){
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	c.set(Calendar.DATE, c.get(Calendar.DATE)+offset);
    	return new SimpleDateFormat(format,Locale.ENGLISH).format(c.getTime());
    }
    
    public static int offsetDay(String date, String format) throws ParseException {
    	Calendar c1 = Calendar.getInstance();//当前
    	c1.set(Calendar.MINUTE,0);
    	c1.set(Calendar.HOUR,0);
    	c1.set(Calendar.SECOND,0);
		Date d=stringToDate(date,format);
		int days = (int) ((d.getTime()-c1.getTime().getTime()) / (1000*3600*24));
		return days+1;
    }
    
    /**
     * @param offset  距离当前时间的 天偏移量
     * @param format  返回字符串格式
     * @return
     */
    public static String getDate(Date date,String format){
    	return new SimpleDateFormat(format,Locale.ENGLISH).format(date);
    }
    
    /**
	 * 将时间转成字符串
	 * @param date
	 * @return
	 * @throws ParseException 
	 */
	public final static Date stringToDate(String dateStr, String format) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(dateStr);
	}
	
    /**
	 * yyyyMMddHHmmss 把当前时间格式化为指定的格式
	 * @param aFormat
	 * @return
	 */
    public static String getCurrentDate(String aFormat) {
    	return getCurrentDate(new java.util.Date(System.currentTimeMillis()),aFormat);
    }
    /**
     * yyyyMMddHHmmss 把当前时间格式化为指定的格式
     * @param date
     * @param aFormat
     * @return
     */
    public static String getCurrentDate(java.util.Date date, String aFormat) {
    	return new SimpleDateFormat(aFormat).format(date);
    }
    /**
     * 将value值另起一行追加到fileSavePath 后面
     * @param bean
     * @return
     * @throws IOException 
     * @throws IOException 
     */
	public static void writeFile(File file,String value) throws IOException{
    	boolean bool=true;
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true),"gb2312"));//"UTF-8"
			if(bool){
				String lineSeparator= "\n"; 
				value+=(lineSeparator);
			}
			out.write(value);
			out.flush();
		}finally {
			try {
				if(out!=null){
					out.close();
					out = null;
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				throw e;
			}
		}
    }

	public static List<String> trimToArray(String imei, String spit) {
		List<String> result = new ArrayList<String>();
		if(StringUtils.isBlank(imei)) return result;
		String[] t = imei.split(spit);
		for (int i = 0; i < t.length; i++) {
			if(StringUtils.isNotBlank(t[i])) result.add(t[i].trim());
		}
		return result;
	}

	public static int getBetweenMonthByCurrent(int year,int mouth) {//201505
		Calendar c1 = Calendar.getInstance();
		Calendar c0 = Calendar.getInstance();
		c0.set(Calendar.MONTH,mouth-1);
		c0.set(Calendar.YEAR,year);
		c0.set(Calendar.DATE,1);
		c1.set(Calendar.DATE,1);
		int beg_mount=((c1.get(Calendar.YEAR)-c0.get(Calendar.YEAR))*12)+c1.get(Calendar.MONTH)-c0.get(Calendar.MONTH);
		//System.out.println(beg_mount);
		return beg_mount;
	}
	/**
     * RMB 分-->元
     * @return
     */
    public static Double centDollar(String amt){
    	return Arith.div(NumberUtils.toDouble(amt,0d), 100);
    }
    /**
     * RMB 元-->分
     * @return
     */
    public static Double dollarCent(String amt){
    	return Arith.mul(NumberUtils.toDouble(amt,0d), 100);
    }
	/**
	 * 数字格式化 	小数点后保留2位(四舍五入)
	 * @param v		待格式化数字
	 * @param currency 1=货币格式
	 * @return
	 */
	public static String convertFormat(Double v,int currency){
		DecimalFormat bf=new DecimalFormat("#0.00");
		if(currency==1)bf=new DecimalFormat("###,##0.00");
        return bf.format(v);
	}
	/**
	 * 数字格式化 	小数点后保留2位(四舍五入)
	 * @param v		待格式化数字
	 * @param currency 1=货币格式
	 * @return
	 */
	public static Double convertFormat(Double v){
		return NumberUtils.toDouble(convertFormat(v,0));
	}
}
