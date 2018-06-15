package com.hrtx.global;

import java.util.ArrayList;
import java.util.List;

public class LockUtils {
	private static List<Object> lockitems =new ArrayList<Object>();
	public static void unLock(Object object) {
		synchronized (lockitems) {
			lockitems.remove(object);
		}
	}
	public static boolean tryLock(Object object) {
		synchronized (lockitems) {
			if(lockitems.contains(object)) return false;
			lockitems.add(object);
			return true;
		}
	}
	
	public static boolean tryLock(Object object, long time) {
		synchronized (lockitems) {
			if(lockitems.contains(object)) {
				if(time <= 0) return false;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return tryLock(object, time-500);
			}
			lockitems.add(object);
			return true;
		}
	}
}
