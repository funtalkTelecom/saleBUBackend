package com.hrtx.global;

import java.io.OutputStream;
import java.util.List;

public class OrderToExcel {
	public static void  SQLwriteExcel(OutputStream os, List<List<?>> list, List<List<Integer>> li, 
			List<Integer[]> intes, String[] sheetNames) throws Exception {
		WriteExcel.sheetWriteExcel(os, list, li, intes, sheetNames);
	}
}
