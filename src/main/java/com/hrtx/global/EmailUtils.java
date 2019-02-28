package com.hrtx.global;

import com.hrtx.dto.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmailUtils {

	
	public static String baseHtml(String bodyhtml){
		StringBuffer sb =new StringBuffer();
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		sb.append("<head>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>");
		sb.append("<style type=\"text/css\">.subtable{text-align:center;} .mtable{border-collapse: collapse;font-size: 16px; color: #333;font-family:\"微软雅黑\",\"sans-serif\";background-color:#fff;text-align:center;} .tleft{text-align:left;padding-left:15px;width:600px;height:30px;} .tright{text-align:right;padding-right:15px;width:200px;} .tt{width:61px;} .tb{width:799px;height:30px;}</style>");
		sb.append("</head>");
		sb.append("<body style=\"margin: 0 auto;\">");
		sb.append(bodyhtml);
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}
	public static String tableHtml(String customer,List<Object[]> contexts){
		StringBuffer sb =new StringBuffer();
		sb.append("<table border=\"0\" cellspacing=\"0\" cellspadding=\"0\">");
		sb.append("<tr><td width=\"61\"></td><td width=\"799\" height=\"70\">");
		sb.append("<span style=\"font-weight: bold; color: #333;\">您好！ ").append(customer).append("</span>");
		sb.append("</table>");
		sb.append("<table bordercolor=\"black\" border=\"1\" align=\"center\" style=\"border-collapse:collapse;width:80%\">");
		if(contexts != null) {
			for (Object[] objects : contexts) {
				sb.append("<tr>");
				for (int i = 0; i < objects.length; i++) {
					sb.append("<td style=\"text-align:center;\">"+objects[i]+"</td>");
				}
				sb.append("</tr>");
			}
		}
		sb.append("</table>");
		return sb.toString();
	}
}

