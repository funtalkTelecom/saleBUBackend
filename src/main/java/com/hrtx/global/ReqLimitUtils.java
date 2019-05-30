package com.hrtx.global;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReqLimitUtils {
    public final static Logger log = LoggerFactory.getLogger(ReqLimitUtils.class);
    private static Map<String,Object> redisUtil=new HashMap<String,Object>();
    private static Lock lock=new ReentrantLock();

    /**
     *
     * 在指定的时间范围内允许请求的次数，若在时间访问内超过请求次数，相应时间内禁止访问
     * @param methodName 限制的方法名
     * @param slog 限制的条件
     * 			String ipLimit	是否限制ip "yes"则对ip限制
     * 			String scope	作用域(平台"plat"或方法"method")
     * 			long secRange	时间范围内允许访问(单位秒)
     * 			int reqNum		可请求的次数
     * 			long limitReqTime	限制访问时间(单位秒) 若在时间访问内超过请求次数，相应时间内禁止访问
     *
     * 用法示例：此方法表示每ip每秒内只能访问FileUpload方法10次，若1秒内超过了10次，则限制访问1分钟
     * ReqLimitUtils.residualReqNum("FileUpload",new ReqLimitUtils.ReqLimit("yes","method",1L,10,60*1L));
     *
     * return 大于0可继续访问 小于等于0限制访问
     */
    public static int residualReqNum(String methodName,ReqLimit slog) {
        if(slog==null)return 1;
        String iplimit=slog.getIpLimit();
        String scope=slog.getScope();
        int reqNum=slog.getReqNum();
        long secRange=slog.getSecRange();
        String key="req_limit";
        if("yes".equals(iplimit))key+=SessionUtil.getUserIp();
        if(!"plat".equals(scope))key+=methodName;
        try{
            lock.lock();
            Object object=redisUtil.get(key);
            LimitBean bean=null;
            long expire=java.lang.System.currentTimeMillis()+secRange*1000;
            if(object==null){
                bean=new ReqLimitUtils.LimitBean(expire,expire+slog.getLimitReqTime()*1000,(reqNum+1)-1);
            }else{
                bean=(LimitBean)object;
                //if(bean.isExpire()&&bean.getNum()<=0)res_int=0;//尚未过期而次数已用完，则说明超限
                if(!bean.isExpire()){//若已过期，则恢复访问次数
                    if(bean.getNum()>0||bean.isCanReqTime()){
                        bean=new ReqLimitUtils.LimitBean(expire,expire+slog.getLimitReqTime()*1000,reqNum);
                    }
                }else{
                    bean.setNum(bean.getNum()-1);
                }
            }
            redisUtil.put(key,bean);
            log.info(String.format("总时间[%s]秒,总请求[%s]次,剩余[%s]次数 key[%s]",secRange,reqNum,bean.getNum(),key));
            return bean.getNum();
        }finally{
            lock.unlock();
        }
    }

    public static class ReqLimit implements java.io.Serializable{
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        /**
         * 是否根据ip限制
         */
        private String ipLimit="yes";
        /**
         * 访问范围(即作用域) 平台或方法
         */
        private String scope="plat";
        /**
         * 时间范围内允许访问(默认1秒)
         */
        private long secRange=1L;
        /**
         * 访问次数(默认60次)
         */
        private int reqNum=60;
        /**
         * 操作请求限制之后暂停访问时间(单位秒)
         */
        private long limitReqTime=0L;

        public ReqLimit(String ipLimit, String scope, long secRange, int reqNum) {
            super();
            this.ipLimit = ipLimit;
            this.scope = scope;
            this.secRange = secRange;
            this.reqNum = reqNum;
        }
        public ReqLimit(String ipLimit, String scope, long secRange, int reqNum,long limitReqTime) {
            this(ipLimit,scope,secRange,reqNum);
            this.limitReqTime=limitReqTime;
        }

        public String getIpLimit() {
            return ipLimit;
        }
        public void setIpLimit(String ipLimit) {
            this.ipLimit = ipLimit;
        }
        public String getScope() {
            return scope;
        }
        public void setScope(String scope) {
            this.scope = scope;
        }
        public long getSecRange() {
            return secRange;
        }
        public void setSecRange(long secRange) {
            this.secRange = secRange;
        }
        public int getReqNum() {
            return reqNum;
        }
        public void setReqNum(int reqNum) {
            this.reqNum = reqNum;
        }
        public long getLimitReqTime() {
            return limitReqTime;
        }
        public void setLimitReqTime(long limitReqTime) {
            this.limitReqTime = limitReqTime;
        }
    }

    static class LimitBean implements java.io.Serializable{
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private long expire;//过期时间  redis的key每次设置都会重新设置新的过期时间，同时过期的时间只精确到秒杀，细粒度不够
        private int num;//剩余次数
        private long canReqTime;
        public LimitBean(long expire,long canReqTime, int num) {
            this.expire = expire;
            this.num = num;
            this.canReqTime=canReqTime;
        }
        /**
         * 是否还有效
         * @return true 有效，false 无效(过期)
         */
        public boolean isExpire() {
            return this.expire>=java.lang.System.currentTimeMillis();
        }
        /**
         * 是否可访问
         * @return true 可访问，false 不可访问
         */
        public boolean isCanReqTime() {
            return this.canReqTime<java.lang.System.currentTimeMillis();
        }
        public int getNum() {
            return num;
        }
        public void setNum(int num) {
            this.num = num;
        }
    }


	/*public static void main(String[] args){
		for(int i=0;i<20;i++){
			new Thread(){
				public void run() {
					log.info("总时间");
					for(int i=0;i<10000;i++){
						ReqLimitUtils.residualReqNum("FileUpload",new ReqLimitUtils.ReqLimit("yes","method",1L,10,1L));
					}
					log.info("总时间");
				};
			}.start();
		}
		try{
			Thread.sleep(5000);
		}catch (Exception e) {
		}
	}*/
}



