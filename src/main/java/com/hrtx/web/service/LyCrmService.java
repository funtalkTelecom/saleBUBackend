package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.hrtx.config.advice.ServiceException;
import com.hrtx.dto.Parameter;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import com.hrtx.webservice.crmsps.CrmSpsServiceLocator;
import com.hrtx.webservice.crmsps.CrmSpsServicePortType;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import java.io.File;
import java.io.IOException;
import java.lang.System;
import java.rmi.RemoteException;
import java.util.*;

@Service
public class LyCrmService {

    private Logger log = LoggerFactory.getLogger(NumService.class);
    private final String res ="<?xml version='1.0'?>"+
            "<ContractRoot>"+
            "<TcpCont>"+
            "<ActionCode>1</ActionCode>"+
            "<TransactionID>%s</TransactionID>"+//与入参的流水号一样
            "<ReqTime>%s</ReqTime>"+//20171226103930
            "<Response>"+
            "<RspType>%s</RspType>"+//<!-- 0  成功          1 9错误-->
            "<RspCode>%s</RspCode>"+
            "<RspDesc>%s</RspDesc>"+
            "</Response>"+
            "</TcpCont> "+
            "<SvcCont>%s</SvcCont>"+
            "</ContractRoot>";
    private final String params = "<?xml version='1.0' encoding='UTF-8'?>"+
            "<ContractRoot>"+
            "<TcpCont>"+
            "<BusCode>%s</BusCode>"+//NEU6099060210
            "<ServiceCode>%s</ServiceCode>"+//NEU6099060210
            "<ServiceContractVer>"+ SystemParam.get("ly_crm_ver")+"</ServiceContractVer>"+
            "<ActionCode>0</ActionCode>"+
            "<TransactionID>%s</TransactionID>"+//1000010000201703089
            "<ServiceLevel>1</ServiceLevel>"+
            "<SrcOrgID>"+ SystemParam.get("ly_src_org")+"</SrcOrgID>"+//100000
            "<SrcSysID>"+ SystemParam.get("ly_src_sys")+"</SrcSysID>"+//1000000001
            "<DstOrgID>"+ SystemParam.get("ly_dst_org")+"</DstOrgID>"+//100001
            "<DstSysID>"+ SystemParam.get("ly_dst_sys")+"</DstSysID>"+//1000010000
            "<ReqTime>%s</ReqTime>"+//20171226103930
            "</TcpCont>"+
            "<SvcCont>%s</SvcCont>"+
            "</ContractRoot>";
    private static CrmSpsServiceLocator csl = null;
    @Autowired private NumMapper numMapper;
    @Autowired private NumBaseMapper numBaseMapper;
    @Autowired private SequenceMapper sequenceMapper;
    @Autowired private DictMapper dictMapper;
    @Autowired private DictService dictService;
    @Autowired private IccidMapper iccidMapper;
    @Autowired private NumRuleMapper numRuleMapper;
    @Autowired private NumPriceMapper numPriceMapper;
    @Autowired private NumPriceAgentMapper numPriceAgentMapper;
    @Autowired private CorporationMapper corporationMapper;

    /**
     * 获取服务
     * @return
     * @throws ServiceException
     */
    private CrmSpsServicePortType getSerice() throws javax.xml.rpc.ServiceException {
        log.info("尝试获取服务...");
        if(csl == null) csl = new CrmSpsServiceLocator();
        csl.setcrmSpsServiceHttpPortEndpointAddress(SystemParam.get("ly_crm_url"));
        CrmSpsServicePortType csp = csl.getcrmSpsServiceHttpPort();
        log.info("成功获服务");
        return csp;
    }

    public String getTransactionID(){
        String seqName = "ly_serial";
        int nextVal = sequenceMapper.nextVal(seqName);
        if(nextVal == 0) {
            Sequence sequence = new Sequence();
            sequence.setSeqName(seqName);
            sequenceMapper.insert(sequence);
            nextVal = sequenceMapper.nextVal(seqName);
        }
        String constantStr = SystemParam.get("ly_src_sys")+Utils.getDate(0, "yyyyMMdd");
        return constantStr+StringUtils.leftPad(nextVal+"", 10, "0");
    }

    /**
     * 通用接口
     * @param TcpCont
     * @param SvcCont
     * @return
     */
    public String callCommond(Map<String, String> TcpCont, Map<String, String> SvcCont){
        log.info("接收乐语CRM参数["+TcpCont+"],["+SvcCont+"]");
        String result = "";
        try {
            long stime = System.currentTimeMillis();
            StringBuffer content = new StringBuffer();
            Set<String> keys = SvcCont.keySet();
            for (String key : keys) {
                content.append("<"+key+"><![CDATA["+SvcCont.get(key)+"]]></"+key+">");
            }
            String param = String.format(params, TcpCont.get("BusCode"), TcpCont.get("ServiceCode"), TcpCont.get("TransactionID"), TcpCont.get("ReqTime"), content);
            log.info(param);
            result = getSerice().exchange(param);
            long etime = System.currentTimeMillis();
            log.info("请求耗时["+(etime-stime)+"]ms");
        } catch (RemoteException e) {
            log.error("连接异常！", e);
            result = String.format(res, "", "", "9", "1111", "连接异常！", "");
        } catch (ServiceException e) {
            log.error("连接服务异常！", e);
            result = String.format(res, "", "", "9", "1111", "连接服务异常！", "");
        } catch (Exception e){
            log.error("", e);
            result = String.format(res, "", "", "9", "1111", "未知异常！", "");
        }
        log.info("得到结果"+result);
        return result;
    }

    /**
     * 上传开卡文件
     */
//    @Scheduled(cron = "0 0 23 * * ?")update by zjc 2018.12.20  统一关闭应用定时器，由外部调用
    public void createAgentCardFile() {
        if(!"true".equals(SystemParam.get("exe_timer"))) return;
        log.info("开始执行上传开卡文件定时器");
        try {
            List<Map> nums = numMapper.queryDslNum();
            if(nums.size() <= 0) return;
            int date_offset = 0;
            int count = 1;
            String order = "1000000009"+Utils.getDate(0, "yyyyMMddHHmmss")+StringUtils.leftPad(count+"", 4, "0");
//          list.add(new Object[]{"190",order,"17003564498","8986031754351004498","LYHR_ZYQ1141","1370761","zhouyq","01","350782198706203512","29号","zhouyq11","18965902603"});
//            n.id, c.third_id, n.num_resource, n.iccid, m.meal_id
            int start = 0;
            int len = nums.size();
            int maxCapacity = 1000000;
            List<Long> snums = new ArrayList<>();
            while(start < len){
                List<Object[]> list = new ArrayList<>();
                int end = start + maxCapacity;
                end = end > len ? len : end;
                for (int i = start; i < end ; i++) {
                    Map num = nums.get(i);
//                    关于开卡接口字段说明：
//                    1、代理商工号：根据号码归属地市找该地是京东门店下任一工号。（代理商同步接口BOSS2HR）
//                    2、套餐编码：线下提供。京东套餐20181221.xlsx
//                    3、客户名称：北京乐语通信科技有限公司
//                    4、证件类型： 02（工商营业执照 ）
//                    5、证件编码：91110105669113779Y
//                    6、通讯地址：北京市朝阳区西大望路甲12号（国家广告产业园区）AB-012
//                    7、联系人：京东下单联系人姓名
//                    8、联系电话（手机号码）：京东下单联系人电话
//                    n.id, c.third_id, n.num_resource, n.iccid, m.meal_id, o.boss_num, o.person_name, o.person_tel
                    String boss_num = ObjectUtils.toString(num.get("boss_num"));
                    if(StringUtils.isBlank(boss_num)) boss_num = SystemParam.get("ly_work_login_name");

                    list.add(new Object[]{num.get("third_id"), order, num.get("num_resource"), num.get("iccid"), boss_num,
                            num.get("meal_id"), SystemParam.get("ly_work_name"), SystemParam.get("ly_work_card_type"), SystemParam.get("ly_work_card_code"),
                            SystemParam.get("ly_work_address"), num.get("person_name"),  num.get("person_tel")});
                    snums.add(NumberUtils.toLong(String.valueOf(num.get("id"))));
                }
                start = end;
                this.createOpenCardFile(snums, list, count, date_offset);
                count++;
            }
        }catch (ServiceException e) {
            log.error(e.getMessage(), e);
            Messager.send(SystemParam.get("system_phone"),"上传开卡文件异常("+e.getMessage()+")");
            throw e;
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            Messager.send(SystemParam.get("system_phone"),"上传开卡文件异常");
            throw e;
        }

    }

    private void createOpenCardFile(List<Long> snums, List<Object[]> list, int count, int date_offset) {
        File dir = new File(this.getLyRootPath()+"upload"+File.separator);
        if(!dir.exists()) dir.mkdir();
        String ly_src_sys = SystemParam.get("ly_src_sys");
        String fileName = ly_src_sys+Utils.getDate(-1-date_offset, "yyyyMMddHHmmss")+StringUtils.leftPad(count+"", 6, "0")+".txt";
        File file = new File(dir.getPath()+File.separator+fileName);
        if(file.exists()) file.delete();
        createFile(list, file.getPath());
        Dict dict = new Dict(null, Utils.getDate(0-date_offset, "yyyyMMdd")+"-"+count, "opend_card_file_name", fileName, 0, "上传的开卡文件名", 0, 0);
//        dict.setId(dict.getGeneralId());
        dictMapper.insert(dict);
        int ucount = this.batchUpdateSlz(fileName, snums);
        if(snums.size() != ucount) throw new ServiceException(String.valueOf("需上传号码与更新号码不一致"));
        Result result = this.uploadFileToSftp("upload", "upload", fileName, 0);
        if(result.getCode() != Result.OK) throw new ServiceException(String.valueOf(result.getData()));

    }

    private int batchUpdateSlz(String fileName, List<Long> snums) {
        int ucount = 0;
        int start = 0;
        int len = snums.size();
        int maxCapacity = 1000;
        while(start < len){
            List<Object[]> list = new ArrayList<>();
            int end = start + maxCapacity;
            end = end > len ? len : end;
            int count = numMapper.batchUpdateSlz(fileName, snums.subList(start, end));
            ucount = ucount + count;
            start = end;
        }
        return ucount;
    }

    /**
     * 解析开卡结果
     */
//    @Scheduled(cron = "0 0 7 * * ?")update by zjc 2018.12.20  统一关闭应用定时器，由外部调用
    public void praseOpenCardFileResult() {
//        if(!"true".equals(SystemParam.get("exe_timer"))) return;
        log.info("开始执行解析开卡结果定时器");
        List<Map> list = dictService.findDictByGroup("opend_card_file_name");
        List<Object[]> fails = new ArrayList<>();
        for (Map map:list) {
            String yFileName = String.valueOf(map.get("keyValue"));
            String fileName = yFileName+".ok";
            log.info("开始解析["+fileName+"]结果");
            try {
//                this.downloadFileToSftp("download", "download", fileName);
                File dir = new File(this.getLyRootPath()+"download"+File.separator);
                List<String> datas = this.readFile(dir.getPath()+File.separator+fileName);
                if(datas != null) {
                    log.info("解析到数据["+datas.size()+"]条");
                    for (int j = 0, len = datas.size(); j < len; j++) {
                        String line = datas.get(j)+"|00";;
                        String[] row = line.split("\\|");
                        if(row.length<9){
                            log.info("第["+j+"]行数据字段不足，异常");
                            continue;
                        }
                        int status = NumberUtils.toInt(row[6]);
                        Num num = new Num();
                        num.setStatus(status == 3 ? 6 : 7);
                        num.setSlReason(row[7]);
                        Example example = new Example(Num.class);
                        example.createCriteria().andEqualTo("numResource",row[2]).andEqualTo("iccid", row[3]).andEqualTo("status", Constants.NUM_STATUS_11.getIntKey());
                        String thirdOrder = "";
                        if(status != 3) thirdOrder = numMapper.findThirdOrder(row[2], row[3], Constants.NUM_STATUS_11.getIntKey());
                        numMapper.updateByExampleSelective(num, example);
                        ////号码、iccid、京东订单号、失败原因
                        if(status != 3) fails.add(new Object[]{row[2], row[3], thirdOrder, row[7]});
                    }
                }
                Dict dict = new Dict();
                dict.setIsDel(1);
                Example example = new Example(Dict.class);
                example.createCriteria().andEqualTo("keyValue",yFileName).andEqualTo("isDel", 0);
                dictMapper.updateByExampleSelective(dict, example);
            }catch (ServiceException e) {
                log.error("解析["+fileName+"]结果异常，原因["+e.getMessage()+"]", e);
                Messager.send(SystemParam.get("system_phone"),"解析["+fileName+"]结果异常，原因["+e.getMessage()+"]");
            }catch (Exception e) {
                log.error("解析["+fileName+"]结果异常, 未知异常", e);
                Messager.send(SystemParam.get("system_phone"),"解析["+fileName+"]结果异常, 未知异常");
            }
        }
        try{
            Corporation corporation = corporationMapper.selectByPrimaryKey(10);
            String email = StringUtils.defaultString(corporation.getEmail(),"");
            if(fails.size() > 0 && email.matches(RegexConsts.REGEX_EMAIL)) {
                fails.add(0, new Object[]{"号码","iccid","京东订单号","失败原因"});
                SendMailUtils.sendAttachmentMail("开卡错误信息推送",EmailUtils.baseHtml(EmailUtils.tableHtml("乐语", fails)), new Parameter(email,"乐语"), null);
            }
        }catch (Exception e) {
            log.error("发送开卡失败邮件未知异常", e);
            Messager.send(SystemParam.get("system_phone"),"发送开卡失败邮件未知异常");
        }
    }

    public static void main(String[] args) {
        List t = new ArrayList();
        t.add("a");t.add("b");t.add("c");t.add("d");;t.add("e");
        int start = 0;
        int len = t.size();
        int maxCapacity = 2;
        while(start < len){
            List<Object[]> list = new ArrayList<>();
            int end = start + maxCapacity;
            end = end > len ? len : end;
            List bb = t.subList(start, end);
            System.out.println(bb.size()+ "***"+ bb.get(0)+"--***"+bb.get(bb.size()-1));
            start = end;
        }
    }

    /**
     * 下载号码资源
     */
//    @Scheduled(cron = "0 0 6 * * ?")update by zjc 2018.12.20  统一关闭应用定时器，由外部调用
    public void praseLyData(int date_offset) {//String type, int dateOffset
        if(!"true".equals(SystemParam.get("exe_timer"))) return;
//        if("ly_corp".equals(type)) this.praseLyCorpData(dateOffset);
//        if("ly_phone".equals(type))
        log.info("开始执行号码资源下载定时器");
        try {
            this.praseLyPhoneData(date_offset);
        }catch (ServiceException e) {
            log.error(e.getMessage(), e);
            Messager.send(SystemParam.get("system_phone"),"下载乐语号码库数据异常("+e.getMessage()+")");
            throw e;
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            Messager.send(SystemParam.get("system_phone"),"下载乐语号码库数据异常");
            throw e;
        }
//        if("ly_iccid".equals(type)) this.uploadLyIccidData(dateOffset);
    }

    /**
     * 上传iccid资源
     */
//    @Scheduled(cron = "0 0 2 * * ?")update by zjc 2018.12.20  统一关闭应用定时器，由外部调用
    public void uploadLyIccidData() {
        if(!"true".equals(SystemParam.get("exe_timer"))) return;
        log.info("开始执行上传iccid定时器");
        try {
            this.uploadLyIccidData(0);
        }catch (ServiceException e) {
            log.error(e.getMessage(), e);
            Messager.send(SystemParam.get("system_phone"),"上传iccid资源数据异常("+e.getMessage()+")");
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            Messager.send(SystemParam.get("system_phone"),"上传iccid资源数据异常");
        }
    }

    public void uploadLyIccidData(int date_offset) {
        File dir = new File(this.getLyRootPath()+"iccid_hr2boss"+File.separator);
        if(!dir.exists()) dir.mkdir();
        String fileName = Utils.getDate(-1-date_offset, "yyyyMMdd")+".txt";
        File file = new File(dir.getPath()+File.separator+fileName);
        if(file.exists()) file.delete();
        Iccid iccid = new Iccid();
        iccid.setStockStatus(1);
        List<Iccid> list = iccidMapper.select(iccid);
        createIccidFile(list, file.getPath());
        Result result = this.uploadFileToSftp("iccid_hr2boss", "iccid_hr2boss", fileName, 0);
        if(result.getCode() != Result.OK) throw new ServiceException(String.valueOf(result.getData()));
    }

    private void praseLyPhoneData(int date_offset) {
        String fileName = Utils.getDate(-1-date_offset, "yyyyMMdd")+".txt";
        File dir = new File(this.getLyRootPath()+"phone_boss2hr"+File.separator);
        String tFileName = dir.getPath()+File.separator+fileName;
        File file = new File(tFileName);
        if(!file.exists()) this.downloadFileToSftp("phone_boss2hr", "phone_boss2hr", fileName);
        List<String> datas = this.readFile(tFileName);
        int sellerId = 10;//乐语
        NumBase nb = new NumBase();
        nb.setSellerId(sellerId);
        numBaseMapper.delete(nb);
        if(datas != null) {
            List<NumBase> batch = new ArrayList<>();
            log.info("解析到乐语号码数据["+datas.size()+"]条");
            for (int j = 0, len = datas.size(); j < len; j++) {
                String line = datas.get(j)+"|00";;
                String[] row = line.split("\\|");
                if(row.length<8){
                    log.info("第["+j+"]行数据字段不足，异常");
                    continue;
                }
                NumBase numBase = new NumBase(row[0],row[1],row[2],row[3],row[4],row[5],NumberUtils.toDouble(row[6]),new Date(), tFileName, sellerId);
//                numBase.setId(numBase.getGeneralId());
                batch.add(numBase);
                if(batch.size() >= 1000 || j+1 >= len) {
                    numBaseMapper.batchInsert(batch);
                    batch = new ArrayList<>();
                }
//              numMapper.insertLyPhone(ArrayUtils.subarray(row, 0, 7));
            }
            if(batch.size() > 0)  numBaseMapper.batchInsert(batch);
            long a = System.currentTimeMillis();
            this.addNumFeature(sellerId);
            log.info("------添加特性耗时"+((System.currentTimeMillis()-a)/1000)+"s");
            this.matchNum(sellerId);
        }
    }

    public void synchBaseToNum(int sellerId){
        long a = System.currentTimeMillis();
        this.addNumFeature(sellerId);
        log.info("------添加特性耗时"+((System.currentTimeMillis()-a)/1000)+"s");
        this.matchNum(sellerId);
        NumBase nb = new NumBase();
        nb.setSellerId(sellerId);
        numBaseMapper.delete(nb);
    }

    private void addNumFeature(int corpId) {
        List<Map> nums = numMapper.queryActiveNum(corpId);
        List<Map> feathers = dictMapper.findDictByGroup("FEATHER_TYPE");
        List<Map> priceFeathers = dictMapper.findDictByGroupAndCorpId("feather_price", corpId);
        List<Map> tagFeathers = dictMapper.findDictByGroup("num_tags");
        List<NumRule> batch = new ArrayList<>();
        for (int j = 0, len = nums.size(); j < len; j++) {
            Map num = nums.get(j);
            int id = NumberUtils.toInt(String.valueOf(num.get("id")));
            String num_resource = String.valueOf(num.get("num_resource"));
            this.addNumFeature(id, num_resource, feathers, batch, "FEATHER_TYPE");
            this.addNumFeature(id, num_resource, priceFeathers, batch, "feather_price");
            this.addNumFeature(id, num_resource, tagFeathers, batch, "num_tags");
            if(batch.size() >= 1000) {
                numRuleMapper.batchInsert(batch);
                batch = new ArrayList<>();
            }
        }
        if(batch.size() > 0)  numRuleMapper.batchInsert(batch);
    }

    private void addNumFeature(Integer id, String num_resource, List<Map> feathers, List<NumRule> batch, String type) {
        for (Map map: feathers) {
            String keyId = org.apache.commons.lang.ObjectUtils.toString(map.get("keyId"));
            String note = org.apache.commons.lang.ObjectUtils.toString(map.get("note"));
            if(num_resource.matches(note)) {
                NumRule numRule = new NumRule();
//                numRule.setId(numRule.getGeneralId());
                numRule.setNum(num_resource);
                numRule.setNumId(id);
                numRule.setRuleType(type);
                numRule.setValue(keyId);
                batch.add(numRule);
            }
        }
    }
//    1在库、2销售中、3冻结(下单未付款)、4待配卡(已付款 针对2C或电销无需购买卡时、代理商买号而未指定白卡时)、5待受理(代理商已提交或仓库已发货，待提交乐语BOSS)、
//            6已受理(乐语BOSS处理成功)、7受理失败(BOSS受理失败，需要人介入解决)、8已失效(乐语BOSS提示号码已非可用)、9受理中

    private void matchNum(int corpId) {
        numMapper.insertAcitveNum(corpId);
        if(corpId == 10) numMapper.updateLoseNum(corpId);
    }

    public void addRule(Dict dict){
        List<Map> nums = numMapper.queryInNum(dict.getCorpId());
        List<Map> feathers = new ArrayList<>();
        feathers.add(CommonMap.create().put("keyId",dict.getKeyId()).put("note",dict.getNote()).getData());
        String type = dict.getKeyGroup();
        List<NumRule> batch = new ArrayList<>();
        for (int j = 0, len = nums.size(); j < len; j++) {
            Map num = nums.get(j);
            int id = NumberUtils.toInt(String.valueOf(num.get("id")));
            String num_resource = String.valueOf(num.get("num_resource"));
            this.addNumFeature(id, num_resource, feathers, batch, type);
            if(batch.size() >= 1000) {
                this.insertNumRule(batch, dict);
                batch = new ArrayList<>();
            }
        }
        if(batch.size() > 0) this.insertNumRule(batch, dict);

        if("feather_price".equals(type)) numPriceMapper.matchNumPrice(dict.getCorpId());
    }

    private void insertNumRule(List<NumRule> batch, Dict dict) {
        numRuleMapper.batchInsert(batch);
        if("FEATHER_TYPE".equals(dict.getKeyGroup())) {//同步Numprice feature
            numPriceMapper.batchUpateFeature(batch, dict.getKeyValue()+",");
        }
    }

    public void delRule(Dict dict){
        String type = dict.getKeyGroup();
        NumRule numRule = new NumRule();
        numRule.setValue(dict.getKeyId());
        numRule.setRuleType(dict.getKeyGroup());
        numRuleMapper.delete(numRule);

        if("feather_price".equals(type)) numPriceMapper.matchNumPrice(dict.getCorpId());
        if("FEATHER_TYPE".equals(type)) {
            numPriceMapper.updateFeature(","+dict.getKeyValue()+",");
        }
    }

    public void praseLyCorpData(int date_offset) {
        String fileName = Utils.getDate(-1-date_offset, "yyyyMMdd")+".txt";
        this.downloadFileToSftp("channel_boss2hr", "channel_boss2hr", fileName);
        File dir = new File(this.getLyRootPath()+"channel_boss2hr"+File.separator);
        List<String> datas = this.readFile(dir.getPath()+File.separator+fileName);
//        lyCrmDao.deleteLyCorp();
        if(datas != null) {
            log.info("解析到乐语代理商数据["+datas.size()+"]条");
            for (int j = 0, len = datas.size(); j < len; j++) {
                String line = datas.get(j)+"|00";;
                String[] row = line.split("\\|");
                if(row.length<10){
                    log.info("第["+j+"]行数据字段不足，异常");
                    continue;
                }
//                lyCrmDao.insertLyCorp(ArrayUtils.subarray(row, 0, 9));
            }
        }

    }

    private void createIccidFile(List<Iccid> list, String pathName) {
        log.info("准备工作验证通过，开始生成iccid上传文件");
        try {
            List<String> lines = new ArrayList<String>();
            for(Iccid iccid : list){
                lines.add(iccid.getIccid());
            }
            FileUtils.writeLines(new File(pathName), "gbk", lines);
        } catch (Exception e) {
            log.error("", e);
            throw new ServiceException("生成文件["+pathName+"]异常");
        }
    }
    private void createFile(List<Object[]> list, String pathName) {
        log.info("准备工作验证通过，开始生成上传文件");
        try {
            List<String> lines = new ArrayList<String>();
            String sp_char = "|";
            for(Object[] array : list){
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < array.length; i++) {
                    String value = ObjectUtils.toString(array[i]);
                    if(i == array.length-1){
                        sb.append(value);
                    }else{
                        sb.append(value+sp_char);
                    }
                }
                lines.add(sb.toString());
            }
            FileUtils.writeLines(new File(pathName), "gbk", lines);
        } catch (Exception e) {
            log.error("", e);
            throw new ServiceException("生成文件["+pathName+"]异常");
        }
    }

    private Result uploadFileToSftp(String localPath, String ftpPath, String fileName, int up_count){
        log.info("进入上传文件方法");
        try {
            File file = new File(this.getLyRootPath()+localPath+File.separator+fileName);
            if(!file.exists()) {
                log.info("["+fileName+"]还未生成");
                return new Result(Result.ERROR, "["+fileName+"]还未生成");
            }
            log.info("查到到文件["+file+"]");
            boolean isup = false;
            try {
                isup = FtpUtils.upload(file, "/hr/"+ftpPath+"/"+fileName, this.getFtp());//SFTPUtil.upload("/"+type+"/upload",file);
            } catch (Exception e) {
                log.error("上传异常",e);
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e1) {
                    log.error("", e1);
                }
                if(up_count == 0) {
                    return this.uploadFileToSftp(localPath, ftpPath, fileName, 1);
                }
            }
            if(!isup) return new Result(Result.ERROR, "上传["+fileName+"]失败");
            return new Result(Result.OK, "上传["+fileName+"]成功");
        } catch (Exception e) {
            log.error("上传文件异常", e);
            return new Result(Result.ERROR, "上传["+fileName+"]失败");
        }
    }

    private void downloadFileToSftp(String loaclPath, String ftpPath, String fileName) {
        log.info("进入下载文件方法");
        String path = this.getLyRootPath()+loaclPath+File.separator;
        File dir = new File(path);
        if(!dir.exists()) dir.mkdir();
        try {
            FtpUtils.download(new File(path+fileName), "/hr/"+ftpPath+"/"+fileName, this.getFtp());
        } catch (Exception e) {
            log.error("下载文件["+fileName+"]异常", e);
            throw new ServiceException("下载文件["+fileName+"]异常");
        }
    }

    private FtpClient getFtp() throws IOException, FtpProtocolException {
        String ftp_ip = StringUtils.isBlank(SystemParam.get("ly_crm_ftp_ip")) ? "124.202.134.4" : SystemParam.get("ly_crm_ftp_ip");//"124.202.134.4"
        int ftp_port = SystemParam.getInt("ly_crm_ftp_port") == -99 ? 21 : SystemParam.getInt("ly_crm_ftp_port");//21
        String username = StringUtils.isBlank(SystemParam.get("ly_crm_ftp_username")) ? "hrftp" : SystemParam.get("ly_crm_ftp_username");//hrftp
        String pwd = StringUtils.isBlank(SystemParam.get("ly_crm_ftp_pwd")) ? "HRftp#pass" : SystemParam.get("ly_crm_ftp_pwd");//HRftp#pass
        return FtpUtils.connectFTP(ftp_ip, ftp_port, username, pwd);
    }

    private List<String> readFile(String pathName) {
        try {
            return FileUtils.readLines(new File(pathName), "GBK");
        } catch (Exception e) {
            log.error("读取反馈文件["+pathName+"]异常", e);
            throw new ServiceException("读取反馈文件["+pathName+"]异常");
        }
    }

    private String getLyRootPath(){
        String upload_path = SystemParam.get("ly_crm_main_path");
        if(StringUtils.isBlank(upload_path)) {
            log.error("生成文件目录未配置");
            throw new ServiceException("生成文件目录未配置");
        }
        return upload_path+File.separator;
    }

    public void synchNumPriceAgentCount() {
        List<Integer> numPriceAgentSkus = numPriceAgentMapper.queryNumPriceAgentSkus();
        List<Integer> goodSkus = numPriceAgentMapper.queryGoodSkus();
        long a = System.currentTimeMillis();
        //多出的供货单
        List<Integer> moreGoodSkus = ListUtils.removeAll(goodSkus, numPriceAgentSkus);
        if(moreGoodSkus.size()>0) {
            int count = numPriceMapper.insertNumPriceAgent(moreGoodSkus);
            log.info("插入上架记录数"+count+"耗时"+(System.currentTimeMillis()-a)+"ms");
        }
        //已下架的供货单
        long b = System.currentTimeMillis();
        List<Integer> moreNumPriceAgentSkus = ListUtils.removeAll(numPriceAgentSkus, goodSkus);
        if(moreNumPriceAgentSkus.size()>0) {
            int count = numPriceMapper.deleteNumPriceAgent(moreNumPriceAgentSkus);
            log.info("删除下架记录数"+count+"耗时"+(System.currentTimeMillis()-b)+"ms");
        }
        long c = System.currentTimeMillis();
        //删除已完成号码
        int count = numPriceMapper.deleteCompleteNumPriceAgent();
        log.info("删除已完成记录数"+count+"耗时"+(System.currentTimeMillis()-c)+"ms");
    }

    /**
     * 同步状态
     */
    public void synchNumPriceAgentStatus() {
        long a = System.currentTimeMillis();
        int count = numPriceMapper.updateNumPriceAgentStatus();
        log.info("更新状态记录数"+count+"耗时"+(System.currentTimeMillis()-a)+"ms");
    }

    /**
     * 同步价格
     */
    public void synchNumPriceAgentPrice() {
        //同步基础价格
        long a = System.currentTimeMillis();
        int count = numPriceMapper.updateNumPriceAgentBasePrice();
        log.info("更新基础价格记录数"+count+"耗时"+(System.currentTimeMillis()-a)+"ms");
        //同步代理商自定义价格
        long b = System.currentTimeMillis();
        count = numPriceMapper.updateNumPriceAgentAgentPrice();
        log.info("更新代理商价格记录数"+count+"耗时"+(System.currentTimeMillis()-b)+"ms");
    }

    public void paySynchNumPriceAgentData(){
        this.synchNumPriceAgentCount();
        this.synchNumPriceAgentStatus();
        this.synchNumPriceAgentPrice();
    }

    /**
     * 同步状态
     */
    public void synchNumPriceAgentStatus(int numId) {
        int count = numPriceMapper.updateNumPriceAgentStatusByNumId(numId);
    }
}
