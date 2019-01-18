package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.advice.WarmException;
import com.hrtx.config.utils.RedisUtil;
import com.hrtx.dto.Result;
import com.hrtx.global.MapJsonUtils;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import com.hrtx.web.pojo.Number;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.System;
import java.text.ParseException;
import java.util.*;


@Service
public class ActivityService {

    @Autowired
    private NumberMapper numberMapper;
    @Autowired
    private NumPriceAgentMapper numPriceAgentMapper;
    @Autowired
    private AgentMapper agentMapper;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private ActivityItemMapper activityItemMapper;
    @Autowired
    private DictMapper dictMapper;
    @Autowired
    private RedisUtil redisUtil;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public Result pageActivity(Activity activity) {
        PageHelper.startPage(activity.startToPageNum(),activity.getLimit());
        Page<Object> ob=this.activityMapper.queryPageList(activity);
        PageInfo<Object> pm = new PageInfo<Object>(ob);
        return new Result(Result.OK, pm);
    }


    public Result findListSaleNums(String skuSaleNum,Integer agentId){
        //验证号码是否可做秒杀 status=2 上架
        Integer sellerId = SessionUtil.getUser().getCorpId();
        Result resd = this.verify(skuSaleNum,agentId,sellerId);
        if(resd.getCode()==888){
            return new Result(Result.OTHER, resd.getData());
        }
        Result ss = this.listNum(skuSaleNum,agentId,sellerId);
        return new Result(Result.OK, ss.getData());
    }

    public Result verify(String skuSaleNum,Integer agentId ,Integer sellerId){
       String  skuSaleNums = checkSkuSaleNum(skuSaleNum,agentId,sellerId);
        //有错误号码
        if (skuSaleNums.split("★").length > 1) {
            return new Result(Result.OTHER, "以下号码未上架，不可以做活动,请重新确认\n" + skuSaleNums.split("★")[1]);
        }
       return new Result(Result.OK, "参数验证成功");
    }


    private String checkSkuSaleNum(String skuSaleNum,Integer agentId,Integer sellerId) {
        String errorNum = "";
        String[] skuSaleNumbs = skuSaleNum.split("\\r?\\n");
        if(skuSaleNumbs!=null && skuSaleNumbs.length>0){
            skuSaleNum = "";
            for (int i = 0; i < skuSaleNumbs.length; i++) {
                if(StringUtils.isBlank(skuSaleNumbs[i])) continue;
                if(numberMapper.activitycheckNumberIsOkStatus(sellerId,skuSaleNumbs[i].trim(),agentId) > 0) {
                    skuSaleNum += skuSaleNumbs[i].trim()+"\n";
                }else{
                    errorNum += skuSaleNumbs[i].trim() + "\n";
                }
            }
        }else return "";

        if(skuSaleNum.length()!=0) skuSaleNum = skuSaleNum.substring(0, skuSaleNum.length()-1);
        return skuSaleNum+"★"+errorNum;
    }

    public Result listNum(String skuSaleNum,Integer agentId,Integer sellerId){
        String[] skuSaleNumbs = skuSaleNum.split("\\r?\\n");
        int size = skuSaleNumbs.length;
        int starts =0;
        Object[] numResource = null;
        int limitSize = 1000;
        List  numList ;
        Map map = new HashMap() ;
        while (starts < size){
            numResource = ArrayUtils.subarray(skuSaleNumbs,starts, starts+limitSize);
            starts = starts + numResource.length;
            String b = ArrayUtils.toString(numResource,"");
            String StrNums = b.substring(b.indexOf("{") + 1, b.indexOf("}"));
            numList = numPriceAgentMapper.listNum(StrNums,agentId,sellerId);
            map.put("numLists",numList);
        }

        return new Result(Result.OK, map);
    }

    public Result activityEdit(Activity activity, HttpServletRequest request) throws ParseException{
        String strjson = request.getParameter("strjson") == null ? "" : request.getParameter("strjson");
        List<Map<String, Object>> list = MapJsonUtils.parseJSON2List(strjson);
        JSONArray jsonArr = JSONArray.fromObject(strjson);
        Date beginDate =Utils.stringToDate(activity.getBeginDates(),"yyyy-MM-dd HH:mm:ss");
        Date endDate =Utils.stringToDate(activity.getEndDates(),"yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.SECOND, -1);
        endDate = c.getTime();
        activity.setBeginDate(beginDate);
        activity.setEndDate(endDate);
        activity.setAddDate(new Date());
        activity.setAddSellerId(SessionUtil.getUser().getCorpId());
        activity.setAddUserId(SessionUtil.getUserId());
        activity.setStatus(1);
        activity.setId(activityMapper.getId());
        activityMapper.insertActivity(activity);

        Integer actId = activity.getId();
        Object[] Numbs = null;
        List<ActivityItem> activityItemsList = new ArrayList<ActivityItem>();
        List<NumPriceAgent> numagentList = new ArrayList<NumPriceAgent>();
        for (Map<String, Object> map : list) {
            String num_resource = String.valueOf(map.get("num_resource"));
            double price =  Double.parseDouble(String.valueOf(map.get("price")));
            double downPrice =  Double.parseDouble(String.valueOf(map.get("downPrice")));
            ActivityItem am = new ActivityItem();
            am.setId(activityItemMapper.getId());
            am.setActivityId(actId);
            am.setNum(num_resource);
            am.setPrice(price);
            am.setDownPrice(downPrice);
            activityItemsList.add(am);

            NumPriceAgent ng = new NumPriceAgent();
            ng.setActivityId(actId);
            ng.setActivityType(activity.getType());
            ng.setActivitySdate(beginDate);
            ng.setActivityEdate(endDate);
            ng.setActivityPrice(downPrice);
            ng.setAgentId(activity.getAgentId());
            ng.setCorpId(SessionUtil.getUser().getCorpId());
            ng.setResource(num_resource);
            numPriceAgentMapper.updateNumPriceAgent(ng);
        }
        if(activityItemsList!=null && activityItemsList.size()>0) activityItemMapper.insertBatch(activityItemsList);

        return new Result(Result.OK, "添加成功");
    }


    public Calendar parseCalendar(String key_value,int date) {
        Calendar o_calendar=Calendar.getInstance();
        o_calendar.set(o_calendar.get(Calendar.YEAR),o_calendar.get(Calendar.MONTH),o_calendar.get(Calendar.DATE)+date,NumberUtils.toInt(key_value,0),0,0);
        return o_calendar;
    }
    /**
     *	获得活动的时间范围
     * @param falg 1 当前正在进行；2即将开始
     * @return	时间数组[开始时间,当前时间，结束时间]
     */
    public Date[] queryActiveDate(int falg) {
        long _start_time=System.currentTimeMillis();
        Calendar calendar=Calendar.getInstance();
        String redis_key="hk_activity_time_start";
        /*从缓存中获取，若不存在则取数并存入缓存,缓存时间60秒*/
        Object object=redisUtil.get(redis_key);
        List<Dict> dictList1=null;
        if(object!=null)dictList1=(List<Dict>)object;
        else {
            Example example = new Example(Dict.class);
            example.createCriteria().andEqualTo("keyGroup","activityTimeS").andEqualTo("isDel",0);
            example.setOrderByClause("seq");
            dictList1=this.dictMapper.selectByExample(example);
            redisUtil.set(redis_key,dictList1,60);
        }
        if(dictList1.size()<2)throw  new WarmException("必须至少配置2个时间");
        Calendar begin_calendar=null;
        Calendar end_calendar=null;
        List<Calendar> cal_list=new ArrayList<Calendar>();
        /*补充时间昨日和未来的时间，以形成闭环*/
        for (int i=0;i<dictList1.size();i++) {//昨日18(补)	10	12	14	16	18	次日10(补)	次日12(补)
            if(i==0)cal_list.add(parseCalendar(dictList1.get(dictList1.size()-1).getKeyValue(),-1));//开始 //补昨天最后一段的时间
            cal_list.add(parseCalendar(dictList1.get(i).getKeyValue(),0));//当日时间
            if(i==dictList1.size()-1){//最后 //补明天第一段、第二段的时间
                for (int j=0;j<2&&j<cal_list.size();j++) {
                    cal_list.add(parseCalendar(dictList1.get(j).getKeyValue(),1));
                }
            }
        }
        /*直接获取时间*/
        for (int i=0;i<cal_list.size();i++) {
            Calendar cal=cal_list.get(i);
            if(cal.getTime().getTime()>calendar.getTime().getTime()){
                if(falg==1){
                    begin_calendar=cal_list.get(i-1);
                    end_calendar=cal_list.get(i);
                }
                if(falg==2){
                    begin_calendar=cal_list.get(i);
                    end_calendar=cal_list.get(i+1);
                }
                break;
            }
        }
        end_calendar.set(Calendar.SECOND,end_calendar.get(Calendar.SECOND)-1);//结束时间减一秒为实际活动的结束时间
        log.info(String.format("活动获取时间结果：耗时[%s]；标签[%s];当前时间[%s];开始时间[%s];结束时间[%s]",(System.currentTimeMillis()-_start_time),falg,Utils.getDate(calendar.getTime(),"MM-dd HH"),Utils.getDate(begin_calendar.getTime(),"yyyy-MM-dd HH:mm:ss"),Utils.getDate(end_calendar.getTime(),"yyyy-MM-dd HH:mm:ss")));
        return new Date[]{begin_calendar.getTime(),calendar.getTime(),end_calendar.getTime()};
    }

}
