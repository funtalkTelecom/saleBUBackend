package com.hrtx.global;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReadExcelNew {  
    public static void writer(OutputStream out, List list, String sheetName,String fileType) throws IOException {
        //创建工作文档对象
        Workbook wb = null;
        if (fileType.equals("xls")) {
            wb = new HSSFWorkbook();
        } else if(fileType.equals("xlsx")) {
//          wb = new XSSFWorkbook();
            wb = new SXSSFWorkbook(10000);
        } else {
        	throw new IOException("文件格式有误!");
        }

        //创建sheet对象
        Sheet sheet1 = (Sheet) wb.createSheet(sheetName);
        //循环写入行数据
        for (int i = 0; i < list.size(); i++) {
        	Row row = (Row) sheet1.createRow(i);
        	//循环写入列数据
        	Object[] obj = (Object[]) list.get(i);
        	for (int j = 0; j < obj.length; j++) {
        		Cell cell = row.createCell(j);
                cell.setCellValue(ObjectUtils.toString(obj[j]));
			}
        	if(i%10000==0)System.gc();//清理一下内存垃圾
//        	 删除单元格
//            row.removeCell(row.getCell(5));
		}
      //合并单元格
      //CellRangeAddress(int firstRow, int lastRow, int firstCol, int lastCol)
//      sheet1.addMergedRegion(new CellRangeAddress(1, 4, 2, 3));
        //写入数据
        wb.write(out);
    }
    public static ArrayList<ArrayList<String>> read(File file, String fileNmae) throws IOException {
    	ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
    	InputStream stream = null;
    	try {
    		stream = new FileInputStream(file);
        	if(StringUtils.isBlank(fileNmae))  throw new IOException("文件格式有误!");
        	String fileType = fileNmae.substring(fileNmae.lastIndexOf(".")+1);
            Workbook wb = null;
            if (fileType.equals("xls")) {
                wb = new HSSFWorkbook(stream);
            } else if (fileType.equals("xlsx")) {
            	wb = new XSSFWorkbook(stream);
            } else {
                throw new IOException("文件格式有误!");
            }
            Sheet sheet1 = wb.getSheetAt(0);
            int len = 0,i=0;
            for (Row row : sheet1) {
            	if(i==0) len = row.getLastCellNum();
            	ArrayList<String> t = new ArrayList<String>();
                for (int j=0; j<len; j++) {
                	Cell cell = row.getCell(j);
                	if(cell == null){
                        t.add("");
                	}else{
                		cell.setCellType(Cell.CELL_TYPE_STRING);
                        t.add(cell.getStringCellValue());
                	}
                }
               list.add(t);
               i++;
            }
		} finally{
			try {
				if(stream != null){
					stream.close();
					stream = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return list;
    }
	public static ArrayList<ArrayList<String>> read(InputStream inputStream, String fileName) throws IOException {
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
        Workbook wb = null;
        if (fileType.equals("xls")) {
            wb = new HSSFWorkbook(inputStream);
        } else if (fileType.equals("xlsx")) {
            wb = new XSSFWorkbook(inputStream);
        } else {
            throw new IOException("文件格式有误!");
        }
        Sheet sheet1 = wb.getSheetAt(0);
        int len = 0,i=0;
        for (Row row : sheet1) {
        	if(i==0) len = row.getLastCellNum();
        	ArrayList<String> t = new ArrayList<String>();
            for (int j=0; j<len; j++) {
            	Cell cell = row.getCell(j);
            	if(cell == null){
                    t.add("");
            	}else{
            		cell.setCellType(Cell.CELL_TYPE_STRING);
                    t.add(cell.getStringCellValue());
            	}
            }
           list.add(t);
           i++;
        }
        if(inputStream!=null) inputStream.close();
        return list;
	}
}  
