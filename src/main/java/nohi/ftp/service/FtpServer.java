package nohi.ftp.service;

import lombok.extern.slf4j.Slf4j;
import nohi.doc.config.meta.ftp.FtpServiceInfo;
import nohi.doc.service.NohiDocServices;
import nohi.doc.vo.DocVO;
import nohi.utils.DocCommonUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;


/**
 * FTP服务类
 *
 * @author NOHI
 * @crated date 2013-2-28
 */
@Slf4j
public class FtpServer {
    private FTPClient ftpClient;
    private FtpServiceInfo ftpInfo;

    public FtpServiceInfo getFtpInfo() {
        return ftpInfo;
    }

    public void setFtpInfo(FtpServiceInfo ftpInfo) {
        this.ftpInfo = ftpInfo;
    }

    /**
     * 取得存放路径的位置
     *
     * @param doc
     * @return
     * @throws Exception
     */
    public String getStoreFileName(DocVO doc) throws Exception {
        String path = null;
        if (null != doc.getTmpDir() && !"".equals(doc.getTmpDir())) {
            path = doc.getTmpDir();
        } else {
            path = null; //
        }
        if (path != null && !path.endsWith("/")) {
            path += "/";
        }
        path += DocCommonUtils.getCurrentTimeStr("yyyyMMdd") + "/" + doc.getDocId() + "/" + DocCommonUtils.getTimeMillis();
        String fileName = doc.getDocName();
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new Exception("创建文件目录[" + path + "]失败!");
            }
        }

        path = path + "/" + fileName;
        return path;
    }


    /**
     * 保存文件到指定FTP路径下
     *
     * @return
     * @throws Exception
     */
    public OutputStream storeFile2FTPserver(DocVO doc) throws Exception {
        //得到FTP服务配置
        FtpServiceInfo ftpInfo = getFtpInfo();

        if (null == ftpInfo) {
            log.info("没有取到FTP服务配置");
            return null;
        }
        String dir = ftpInfo.getFtpRemotePath();
        String charSet = ftpInfo.getFtpCharset();
        if (null == charSet || "".equals(charSet)) {
            charSet = "UTF-8";
        }

        //3 登录FTP
        ftpClient = new FTPClient();

        boolean flag = false;
        int port = Integer.valueOf(ftpInfo.getFtpPort());
        try {
            if (0 == port) {
                ftpClient.connect(ftpInfo.getFtpAddress()); // IP地址
            } else {
                ftpClient.connect(ftpInfo.getFtpAddress(), port); // IP地址和端口
            }
        } catch (Exception e) {
            // TODO: handle exception
            throw new Exception("连接FTP失败");
        }


        flag = ftpClient.login(ftpInfo.getFtpUsername(), ftpInfo.getFtpPassword()); // 用户名和密码，匿名登陆的话用户名为anonymous,密码为非空字符串
        if (!flag) {
            throw new Exception("登录FTP失败");
        }

        ftpClient.setFileType(FTP.BINARY_FILE_TYPE); //设置文件传输方式
        ftpClient.setControlEncoding(charSet);

        flag = ftpClient.changeWorkingDirectory(dir); // 切换到文件目录
        if (!flag) {
            log.debug("没有目录[" + dir + "]");
            ftpClient.mkd(dir); //创建目录
            flag = ftpClient.changeWorkingDirectory(dir); // 切换到文件目录
            if (!flag) {
                throw new Exception("没有进入到ftp目录[" + dir + "]");
            }
        }

        String dateStr = DocCommonUtils.getCurrentTimeStr("yyyyMMdd");
        ftpClient.mkd(dateStr); //创建目录

        flag = ftpClient.changeWorkingDirectory(dateStr); // 切换到文件目录
        if (!flag) {
            throw new Exception("没有进入到ftp目录[" + dateStr + "]");
        }

        ftpClient.mkd(doc.getDocId()); //创建目录

        flag = ftpClient.changeWorkingDirectory(doc.getDocId()); // 切换到文件目录
        if (!flag) {
            throw new Exception("没有进入到ftp目录[" + doc.getDocId() + "]");
        }
        doc.setFilePath(dir + "/" + dateStr + "/" + doc.getDocId() + "/" + doc.getDocName());

        //文件名中包含中文需要转化
        return ftpClient.storeFileStream(new String(doc.getDocName().getBytes("GBK"), "iso-8859-1"));
    }

    public void closeFtp() {
        if (null != ftpClient && ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

}
