package com.hrtx.web.controller;

import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.pojo.*;
import com.hrtx.web.pojo.Number;
import com.hrtx.web.service.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Array;
import java.util.*;

@RestController
@RequestMapping("/lianghao")
public class LiangHaoController extends BaseReturn{
    public final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private NumService numService;
    @Resource private ApiMealService apiMealService;
    @Resource private ApiOrderService apiOrderService;
    @Resource private DictService dictService;
    @Resource private AgentService agentService;


    @RequestMapping("/lianghao-query")
    @Powers({PowerConsts.LIANGHAOMOUDULE_COMMON_QUEYR})
    public ModelAndView lianghaoQuery(Number number){
        return new ModelAndView("admin/lianghao/lianghao-query");
    }

    @RequestMapping("/lianghao-list")
    @Powers({PowerConsts.LIANGHAOMOUDULE_COMMON_QUEYR})
    public Result listNumber(NumPrice numPrice){
        numPrice.setPageNum(numPrice.startToPageNum());
        User u = SessionUtil.getUser();
        Consumer user =new Consumer();
        user.setId(u.getId());
        user.setName(u.getName());
        user.setIsAgent(0);
        Result result = agentService.queryCurrAgent(user);
        if(result.getCode()!=200) return result;
        Agent data = (Agent) result.getData();
        numPrice.setChannel(data.getChannelId());
        numPrice.setAgentId(data.getId());
        PageInfo<Object> objectPageInfo = numService.queryNumPrice(numPrice);
        objectPageInfo =numService.queryFreeze(objectPageInfo);
        return new Result(Result.OK, objectPageInfo);
    }
    @PostMapping("/freeze-num")
    @Powers({PowerConsts.LIANGHAOMOUDULE_COMMON_FREEZE})
    public Result freezeNum(Num num){
        return numService.freezeNum(num);
    }

    @GetMapping("/add-order")
    @Powers({PowerConsts.LIANGHAOMOUDULE_COMMON_ADD})
    public ModelAndView addOrder(NumPrice numPrice, HttpServletRequest request){
        numPrice = numService.getNumPrice(numPrice.getId());
        if(numPrice == null) return new ModelAndView("admin/error-page").addObject("errormsg", "号码未找到");
        Result result = apiMealService.mealListForNum(numPrice.getNumId()+"", request);
        List mealList = new ArrayList();
        if(result.getCode() == Result.OK) mealList = (List) result.getData();
        return new ModelAndView("admin/lianghao/lianghao-add-order")
                .addObject("numPrice", numPrice)
                .addObject("types", dictService.findDictByGroup("phone_consumer_id_type"))
                .addObject("mealList", mealList);
    }

    @PostMapping("/add-order")
    @Powers({PowerConsts.LIANGHAOMOUDULE_COMMON_ADD})
    public Result addOrder(HttpServletRequest request){
        //第三方订单号、手机号码、套餐名称、BOSS开户工号、客户名称、客户证件类型、客户证件编码、邮寄联系人、邮寄联系电话、邮寄地址
        Result result = this.vaildAddOrder(request);
        if(result.getCode() != Result.OK) return  result;
        String id = request.getParameter("id");
        if(!LockUtils.tryLock("kfadd"+id)) return new Result(Result.ERROR, "此号码下单中，请稍后再试!");
        try {
            NumPrice numPrice = numService.getNumPrice(NumberUtils.toInt(id));
            if(numPrice == null) return new Result(Result.ERROR, "未找到号码");
            //判断是否冻结
//            Integer fuser = numService.queryFreeze(numPrice.getNumId());
//            if(fuser != null && !fuser.equals(SessionUtil.getUserId())) return new Result(Result.ERROR, "此号码已冻结不可下单");
            int mealId = NumberUtils.toInt(request.getParameter("mealId"));
            return apiOrderService.submitCustomOrder(numPrice.getNumId(), mealId,request.getParameter("personName"),request.getParameter("personTel"),
                    request.getParameter("address"),request.getParameter("conment"),request.getParameter("thirdOrder"),request.getParameter("bossNum"),
                    request.getParameter("phoneConsumer"),request.getParameter("phoneConsumerIdType"),request.getParameter("phoneConsumerIdNum"));
        }finally {
            LockUtils.unLock("kfadd"+id);
        }
    }

    private Result vaildAddOrder(HttpServletRequest request) {
        if(NumberUtils.toLong(request.getParameter("mealId")) == 0) return new Result(Result.ERROR,"请选择套餐");
        if(StringUtils.isBlank(request.getParameter("thirdOrder"))) return new Result(Result.ERROR,"请填写第三方订单号");
        if(StringUtils.isBlank(request.getParameter("bossNum"))) return new Result(Result.ERROR,"请填写BOSS开户工号");
//        if(StringUtils.isBlank(request.getParameter("phoneConsumer"))) return new Result(Result.ERROR,"请填写客户名称");
//        if(NumberUtils.toInt(request.getParameter("phoneConsumerIdType")) == 0) return new Result(Result.ERROR,"请填写客户证件类型");
        if(StringUtils.isNotBlank(request.getParameter("phoneConsumerIdNum")) && !request.getParameter("phoneConsumerIdNum").matches(RegexConsts.REGEX_IC_CARD)) return new Result(Result.ERROR,"客户证件编码格式不正确");
        if(StringUtils.isBlank(request.getParameter("personName"))) return new Result(Result.ERROR,"请填写邮寄联系人");
        if(StringUtils.isBlank(request.getParameter("personTel"))) return new Result(Result.ERROR,"请填写邮寄联系电话");
        if(!request.getParameter("personTel").matches(RegexConsts.REGEX_MOBILE_COMMON)) return new Result(Result.ERROR,"邮寄联系电话格式不正确");
        if(StringUtils.isBlank(request.getParameter("address"))) return new Result(Result.ERROR,"请填写邮寄地址");
        return new Result(Result.OK, "");
    }


    String[] title = new String[]{"号码","套餐名称","第三方订单号","BOSS客户工号","客户名称","客户证件类型","客户证件编码","邮寄联系人","邮寄联系电话","邮寄地址","备注"};
    @RequestMapping("/download-batch-add-template")
    @Powers( {PowerConsts.LIANGHAOMOUDULE_COMMON_ADD })
    public void downloadBatchAddTemplate() throws Exception {
        List list = new ArrayList();
        list.add(title);
        Object[] temp = new Object[]{"17700000000","xxxxx","xxxxx","xxxxxx","xxxxx","xxxxx","xxxxx","xxxxx","xxxxx"};
        List<Meal> mealList = apiMealService.mealList();
        List mealList_t = new ArrayList();
        List<Map> dicts = dictService.findDictByGroup("phone_consumer_id_type");
        List dicts_t = new ArrayList();
        for (Map map:dicts) {
            dicts_t.add(map.get("keyId")+"#"+map.get("keyValue"));
        }
        for (Meal meal:mealList) {
            mealList_t.add(meal.getMid()+"#"+meal.getMealName());
        }
        temp = ArrayUtils.add(temp,1, mealList_t.toArray());
        temp = ArrayUtils.add(temp,5, dicts_t.toArray());
        list.add(temp);
        List<List<?>> list1 = new ArrayList();
        list1.add(list);
        Utils.export("批量下单模板.xls",list1, null, null, new String[]{"数据"});
    }

    @PostMapping("/batch-add-order")
    @Powers( {PowerConsts.LIANGHAOMOUDULE_COMMON_ADD })
    public Result importRecCard(@RequestParam(name = "file",required = false) MultipartFile file) throws Exception {
        Result result = this.uploadFile("batch_add_order/", "xls,xlsx", file, false, false);
        if(result.getCode() != Result.OK) return result;
        String sourceServerFileName = (String) ((Map)result.getData()).get("sourceServerFileName");

        ArrayList<ArrayList<String>> list = ReadExcelNew.read(file.getInputStream(), file.getOriginalFilename());
        if(list==null || list.size()<=1) {
            this.deleteFile("batch_add_order/", sourceServerFileName);
            return new Result(Result.ERROR, "导入文件无数据");
        }
        List errors = new ArrayList();
        Object[] title = ArrayUtils.addAll(this.title, new Object[]{"状态", "描述"});
        errors.add(title);
        try{
            List<Meal> mealList = apiMealService.mealList();
            List mealList_t = new ArrayList();
            List<Map> dicts = dictService.findDictByGroup("phone_consumer_id_type");
            List dicts_t = new ArrayList();
            for (Map map:dicts) {
                dicts_t.add(map.get("keyId")+"#"+map.get("keyValue"));
            }
            for (Meal meal:mealList) {
                mealList_t.add(meal.getMid()+"#"+meal.getMealName());
            }
            ArrayList<ArrayList<String>> normalList = new ArrayList<>();
            for(int i=1;i<list.size();i++){
                ArrayList<String> arr=list.get(i);
                String phone = arr.get(0);
                String meal = arr.get(1);
                String thirdOrder = arr.get(2);
                String bossNum = arr.get(3);
//                String phoneConsumer = arr.get(4);
                String phoneConsumerIdType = arr.get(5);
                String phoneConsumerIdNum = arr.get(6);
                String personName = arr.get(7);
                String personTel = arr.get(8);
                String address = arr.get(9);
//                String conment = arr.get(10);
                //"号码","套餐名称","第三方订单号","BOSS客户工号","客户名称","客户证件类型","客户证件编码","邮寄联系人","邮寄联系电话","邮寄地址","备注"
                if(StringUtils.isBlank(phone) || !phone.matches(RegexConsts.REGEX_MOBILE_COMMON)){
                    arr.add("失败");arr.add("号码格式错误");
                    errors.add(arr.toArray());
                    continue;
                }
                if(StringUtils.isBlank(meal) || !mealList_t.contains(meal)){
                    arr.add("失败");arr.add("套餐未按要求填写");
                    errors.add(arr.toArray());
                    continue;
                }
                if(StringUtils.isBlank(thirdOrder)){
                    arr.add("失败");arr.add("第三方订单号未填写");
                    errors.add(arr.toArray());
                    continue;
                }
                if(StringUtils.isBlank(bossNum)){
                    arr.add("失败");arr.add("BOSS客户工号未填写");
                    errors.add(arr.toArray());
                    continue;
                }
                if(StringUtils.isNotBlank(phoneConsumerIdType) && !dicts_t.contains(phoneConsumerIdType)){
                    arr.add("失败");arr.add("客户证件类型未按要求填写");
                    errors.add(arr.toArray());
                    continue;
                }
                if(StringUtils.isNotBlank(phoneConsumerIdNum) && !phoneConsumerIdNum.matches(RegexConsts.REGEX_IC_CARD)){
                    arr.add("失败");arr.add("客户证件编码格式错误");
                    errors.add(arr.toArray());
                    continue;
                }
                if(StringUtils.isBlank(personName)){
                    arr.add("失败");arr.add("邮寄联系人未填写");
                    errors.add(arr.toArray());
                    continue;
                }
                if(StringUtils.isBlank(personTel) || !personTel.matches(RegexConsts.REGEX_MOBILE_COMMON)){
                    arr.add("失败");arr.add("邮寄联系电话格式不正确");
                    errors.add(arr.toArray());
                    continue;
                }
                if(StringUtils.isBlank(address)){
                    arr.add("失败");arr.add("邮寄地址未填写");
                    errors.add(arr.toArray());
                    continue;
                }
                normalList.add(arr);
            }
            if(normalList.size() > 0) {
                for (ArrayList<String> arr:normalList) {
                    String phone = arr.get(0);
                    String meal = arr.get(1);
                    String thirdOrder = arr.get(2);
                    String bossNum = arr.get(3);
                    String phoneConsumer = arr.get(4);
                    String phoneConsumerIdType = arr.get(6);
                    String phoneConsumerIdNum = arr.get(6);
                    String personName = arr.get(7);
                    String personTel = arr.get(8);
                    String address = arr.get(9);
                    String conment = arr.get(10);
                    NumPrice numPrice = new NumPrice();
                    numPrice.setResource(phone);
                    List nps = numService.queryNumPriceList(numPrice);
                    if(nps.size() != 1) {
                        arr.add("失败");arr.add("未找到号码");
                        errors.add(arr.toArray());
                        continue;
                    }
                    Map np = (Map) nps.get(0);
                    Result result1 = apiOrderService.submitCustomOrder(NumberUtils.toInt(String.valueOf(np.get("id"))), NumberUtils.toInt(meal.split("#")[0]),
                            personName, personTel, address, conment, thirdOrder, bossNum, phoneConsumer,phoneConsumerIdType.split("#")[0],phoneConsumerIdNum);
                    if(result1.getCode() == Result.OK || result1.getCode() == Result.OTHER) {
                        arr.add("成功");arr.add("");
                        errors.add(arr.toArray());
                        continue;
                    }else {
                        arr.add("失败");arr.add(String.valueOf(result1.getData()));
                        errors.add(arr.toArray());
                        continue;
                    }
                }
            }
            List<List<?>> list1 = new ArrayList();
            list1.add(errors);
            String fileName = "RESULT-"+Utils.randomNoByDateTime()+".xls";
            String filePath = SystemParam.get("upload_root_path")+File.separator+"batch_add_order"+File.separator+fileName;
            Utils.createExportFile(filePath, list1, null, null, new String[]{"数据"});
            return new Result(Result.OK, fileName);
        }catch (Exception e){//sourceServerFileName
            log.error("批量下单异常", e);
//            this.deleteFile("batch_add_order/", sourceServerFileName);
            return new Result(Result.ERROR, "未知异常");
        }
    }
}
