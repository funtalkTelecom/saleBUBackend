package com.hrtx.web.service;

import com.hrtx.config.advice.ServiceException;
import com.hrtx.global.FtpUtils;
import com.hrtx.global.Messager;
import com.hrtx.global.SystemParam;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.NumBaseMapper;
import com.hrtx.web.mapper.NumMapper;
import com.hrtx.web.mapper.SequenceMapper;
import com.hrtx.web.pojo.NumBase;
import com.hrtx.web.pojo.Sequence;
import com.hrtx.webservice.crmsps.CrmSpsServiceLocator;
import com.hrtx.webservice.crmsps.CrmSpsServicePortType;
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

	/*<CityCode>110</CityCode>
	<CustName>李兵</CustName>
	<IdendityCode>131121198510025018</IdendityCode>
	<CustAddress>河北省石家庄市长安区</CustAddress>
	<PhoneNumber>15931108200</PhoneNumber>
	<AIdendityCode>HRCS12345678</AIdendityCode>*/


//    <cityCode>110</cityCode>
//    <dealerName>华睿周元强1090</dealerName>
//    <dealerID>C100333555</dealerID>
//    <workNumber>LYHR_ZYQ1111</workNumber>
//    <password>123456</password>

//	地市编码	3	如：110代表北京市，参见4.4.5归属地是编码
//	订单编号	28	发起方平台编码+时间（yyyymmddhhmiss）+4位序号（0000~9999）
//	电话号码	11
//	卡号	19	Iccid
//	代理商工号	30	要求工号中的字母都是大写
//	套餐编码	7	套餐编码线下提供
//	客户名称	60
//	证件类型	2	01：身份证；02：工商营业执照
//	证件编码	60
//	通讯地址	200
//	联系人	60
//	联系电话（手机号码）	20
//	8986031754351004498/17003564498
//	8986031754351004497/17003564497
//	8986011784020097494/17176877494
//	8986011784020097424/17176877424

    public void createAgentCardFile(int count, int date_offset) {
        List<Object[]> list = new ArrayList<>();
        String order = "1000000009"+Utils.getDate(0, "yyyyMMddHHmmss")+"0001";
        list.add(new Object[]{"190",order,"17003564498","8986031754351004498","LYHR_ZYQ1141","1370761","zhouyq","01","350782198706203512","29号","zhouyq11","18965902603"});
        list.add(new Object[]{"190",order,"17003564497","8986031754351004497","LYHR_ZYQ1141","1370761","zhouyq","01","350782198706203512","29号","zhouyq11","18965902603"});
        list.add(new Object[]{"110",order,"17176877494","8986011784020097494","LYHR_ZYQ1142","1371282","zhouyq","01","350782198706203512","29号","zhouyq11","18965902603"});
        list.add(new Object[]{"110",order,"17176877424","8986011784020097424","LYHR_ZYQ1142","1371282","zhouyq","01","350782198706203512","29号","zhouyq11","18965902603"});
        this.createOpenCardFile(list, count, date_offset);
    }

    private void createOpenCardFile(List<Object[]> list, int count, int date_offset) {
        File dir = new File(this.getLyRootPath()+"upload/");
        if(!dir.exists()) dir.mkdir();
        String ly_src_sys = SystemParam.get("ly_src_sys");
        String fileName = ly_src_sys+Utils.getDate(-1-date_offset, "yyyyMMddHHmmss")+StringUtils.leftPad(count+"", 6, "0")+".txt";
        File file = new File(dir.getPath()+File.separator+fileName);
        if(file.exists()) file.delete();
        createFile(list, file.getPath());
        this.uploadFileToSftp("upload", "upload", fileName, 0);
    }

    @Scheduled(cron = "0 0 6 * * ?")
    public void praseLyData() {//String type, int dateOffset
//        if("ly_corp".equals(type)) this.praseLyCorpData(dateOffset);
//        if("ly_phone".equals(type))
        try {
            this.praseLyPhoneData(0);
        }catch (ServiceException e) {
            log.error(e.getMessage(), e);
            Messager.send(SystemParam.get("system_phone"),"下载乐语号码库数据异常("+e.getMessage()+")");
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            Messager.send(SystemParam.get("system_phone"),"下载乐语号码库数据异常");
        }
//        if("ly_iccid".equals(type)) this.uploadLyIccidData(dateOffset);
    }

    public void uploadLyIccidData(int date_offset) {
        File dir = new File(this.getLyRootPath()+"iccid_hr2boss/");
        if(!dir.exists()) dir.mkdir();
        String fileName = Utils.getDate(-1-date_offset, "yyyyMMdd")+".txt";
        File file = new File(dir.getPath()+File.separator+fileName);
        if(file.exists()) file.delete();
        List<Object[]> list = null;//lyCrmDao.queryActiveIccid();
        createFile(list, file.getPath());
        this.uploadFileToSftp("iccid_hr2boss", "iccid_hr2boss", fileName, 0);
    }

    private void praseLyPhoneData(int date_offset) {
        String fileName = Utils.getDate(-1-date_offset, "yyyyMMdd")+".txt";
        this.downloadFileToSftp("phone_boss2hr", "phone_boss2hr", fileName);
        File dir = new File(this.getLyRootPath()+"phone_boss2hr/");
        String tFileName = dir.getPath()+File.separator+fileName;
        List<String> datas = this.readFile(tFileName);
        numBaseMapper.delete(null);
        if(datas != null) {
            log.info("解析到乐语号码数据["+datas.size()+"]条");
            for (int j = 0, len = datas.size(); j < len; j++) {
                String line = datas.get(j)+"|00";;
                String[] row = line.split("\\|");
                if(row.length<8){
                    log.info("第["+j+"]行数据字段不足，异常");
                    continue;
                }
                NumBase numBase = new NumBase(0l,row[0],row[1],row[2],row[3],row[4],row[5],NumberUtils.toDouble(row[6]),new Date(), tFileName);
                numBase.setId(numBase.getGeneralId());
                numBaseMapper.insert(numBase);
//              numMapper.insertLyPhone(ArrayUtils.subarray(row, 0, 7));
            }
            this.matchNum();
        }
    }

    private void matchNum() {
        numMapper.insertAcitveNum();
        numMapper.updateLoseNum();
    }

    public void praseLyCorpData(int date_offset) {
        String fileName = Utils.getDate(-1-date_offset, "yyyyMMdd")+".txt";
        this.downloadFileToSftp("channel_boss2hr", "channel_boss2hr", fileName);
        File dir = new File(this.getLyRootPath()+"channel_boss2hr/");
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

    private void uploadFileToSftp(String localPath, String ftpPath, String fileName, int up_count){
        log.info("进入上传文件方法");
        try {
            File file = new File(this.getLyRootPath()+localPath+File.separator+fileName);
            if(!file.exists()) {
                log.info("今天的日志还未生成");
                throw new ServiceException(fileName);
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
                    this.uploadFileToSftp(localPath, ftpPath, fileName, 1);
                }
            }
            if(!isup) throw new ServiceException("上传["+fileName+"]失败");
        } catch (Exception e) {
            log.error("上传文件异常", e);
            throw new ServiceException("上传["+fileName+"]失败");
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
}
