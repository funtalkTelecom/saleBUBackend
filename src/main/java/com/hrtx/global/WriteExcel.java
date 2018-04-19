package com.hrtx.global;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.UnderlineStyle;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

public class WriteExcel {
	/**
	 * @param os
	 * @param list  真正要写入的
	 * @param c  Integer[]数组 [开始列，开始行，结束列，结束行] 从 零 开始  合并单元格
	 * @param labelName  标签名
	 * @param sheetnum  表格名
	 * @throws Exception
	 */
	public static void  writeExcel(jxl.write.WritableWorkbook wwb, List<?> list,List<Integer> c,String sheetName,Integer[] inte) throws Exception {
		jxl.write.WritableSheet ws = wwb.createSheet(sheetName, 0);
		jxl.write.Label labelC = null;
		/************************************************************/
		WritableFont wfc2 = new WritableFont(
				WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false,
				jxl.format.UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);	
		WritableCellFormat w = new WritableCellFormat(wfc2);
		w.setAlignment(jxl.format.Alignment.CENTRE);//文字居中
		w.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
        // 设置边框线
        //w.setBorder(Border.ALL, BorderLineStyle.THIN);
        
        
        WritableFont titleFont = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD,
          	     false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);   
        WritableCellFormat titCellFormat = new WritableCellFormat(titleFont);
        titCellFormat.setAlignment(jxl.format.Alignment.CENTRE);//文字居中
        titCellFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
        // 设置边框线
     //   titCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        
        
        CellView cellView = new CellView();
        cellView.setAutosize(true);
		Object[] t = null;
		String s="";
		for (int i = 0; i < list.size(); i++) {
			t = (Object[]) list.get(i);
			int x = t.length;
			for (int j = 1; j < x + 1; j++) {
				Object val=t[j - 1];
				if(val instanceof Object[]){//添加EXCEL选择框
					Label subLabel = new Label(j-1, i, "");
					WritableCellFeatures wcf = new WritableCellFeatures();
					List<Object> angerlist = new ArrayList<Object>();
					for(Object sval:(Object[])val){
						angerlist.add(sval);
					}
					wcf.setDataValidationList(angerlist);
					subLabel.setCellFeatures(wcf);
					ws.addCell(subLabel);
				}else{
					s=val==null?"":String.valueOf(val).equals("null")?"":String.valueOf(val);
					if(i == 0){
						labelC = new jxl.write.Label(j-1, i, s,	titCellFormat);
					}else{
						labelC = new jxl.write.Label(j-1, i, s,w);
					}
					ws.setColumnView(j-1, cellView);
					ws.addCell(labelC);
				}
			}
		}
		System.gc();//清理一下内存垃圾
//		表示将从第x+1列，y+1行到m+1列，n+1行合并 (四个点定义了两个坐标，左上角和右下角)
//		ws.mergeCells(2,4,3,5);
		
		/********************合并单元格********************/
		if(c!=null&&c.size()>0){
			for (int i = 0; i < inte.length; i++) {
				int row = inte[i];
				int b1=1;
				int d1=1;
//				String colow_temp = "";
//				for(int j=1;j<list.size();j++){
//					String single = String.valueOf(ws.getCell(d1, row).getContents());
//					if(j>1 && colow_temp.equals(single)){
//						d1++;
//						colow_temp = single;
//						continue;
//					}
//					ws.mergeCells(row,b1,row,d1); //合并单元格，参数格式（开始列，开始行，结束列，结束行）
//					b1=d1+1;
//				}
				
				for(int j=0;j<c.size();j++){
//					Object[] in=(i[])c.get(j);
					d1=b1+c.get(j)-1;
					for(int m=b1+1; m<d1+1;m++){
						Blank b = new Blank(row, m);
						ws.addCell(b);
					}
					ws.mergeCells(row,b1,row,d1); //合并单元格，参数格式（开始列，开始行，结束列，结束行）
					b1=d1+1;
				}
			}
		} 		
	}
	public static void  sheetWriteExcel(OutputStream os, List<List<?>> list,List<List<Integer>> c, List<Integer[]> intes, String[] sheetNames) throws Exception {
		jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(os);
		int i=0;
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			List<?> li = (List<?>) iterator.next();
			if(c == null){
				writeExcel(wwb,li,null,sheetNames[i],null);
			}else {
				writeExcel(wwb,li,c.get(i),sheetNames[i],intes.get(i));
			}
			i++;
		}
		wwb.write();
		wwb.close();
	}
	
	
	/**
	 * 是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
		if(str==null || str.trim().equals("")) return false;
		Pattern pattern = Pattern.compile("^((\\+)?|(\\-)?)(\\d)*(\\.)?(\\d)+");//"-?[0-9]+"    "^-?[0-9]+.?[0-9]*"
		return pattern.matcher(str).matches();  
	 }
	
}
