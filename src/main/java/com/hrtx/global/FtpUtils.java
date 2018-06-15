package com.hrtx.global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class FtpUtils {
    private static Logger log = LoggerFactory.getLogger(FtpUtils.class);
    private static FtpClient ftpClient = null;

    public static void disconnect(){
        if(ftpClient != null) {
            if(ftpClient.isConnected()) {
                try {
                    ftpClient.close();
                } catch (IOException e) {
                    log.error("Ftp关闭异常",e);
                }
            }
            ftpClient = null;
        }
    }

    public static FtpClient connectFTP(String url, int port, String username, String password) throws IOException, FtpProtocolException {
//		if(ftpClient != null && ftpClient.isConnected()) return ftpClient;
        log.info("ftpClient未连接或者已断开连接，重新获取连接");
        //创建ftp
        //创建地址
        SocketAddress addr = new InetSocketAddress(url, port);
        //连接
        ftpClient = FtpClient.create();
        ftpClient.connect(addr);
        //登陆
        ftpClient.login(username, password.toCharArray());
        ftpClient.setBinaryType();
        return ftpClient;
    }

    public static boolean upload(File localFile, String ftpFile, FtpClient ftp) throws Exception {
        OutputStream os = null;
        FileInputStream fis = null;
        try {
            // 将ftp文件加入输出流中。输出到ftp上
            os = ftp.putFileStream(ftpFile);

            // 创建一个缓冲区
            fis = new FileInputStream(localFile);
            byte[] bytes = new byte[1024];
            int c;
            while((c = fis.read(bytes)) != -1){
                os.write(bytes, 0, c);
            }
            log.info("upload success!!");
            return true;
        } finally {
            try {
                if(os!=null) {
                    os.close();
                }
                if(fis!=null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void download(File localFile, String ftpFile, FtpClient ftp) throws Exception {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            // 获取ftp上的文件
            is = ftp.getFileStream(ftpFile);
            byte[] bytes = new byte[1024];
            int i;
            fos = new FileOutputStream(localFile);
            while((i = is.read(bytes)) != -1){
                fos.write(bytes, 0, i);
            }
            log.info("download success!!");

        } finally {
            try {
                if(fos!=null) {
                    fos.close();
                }
                if(is!=null){
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
