package nohi.ftp.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import nohi.doc.config.meta.ftp.FtpServiceInfo;
import nohi.doc.vo.DocVO;
import nohi.utils.DocCommonUtils;
import org.junit.jupiter.api.Assertions;

import java.io.File;


/**
 * FTP服务类
 *
 * @author NOHI
 * @date 2024-5-16
 */
@Data
@Slf4j
public class FtpServer {
    private FtpServiceInfo ftpInfo;

    /**
     * 取得存放路径的位置
     *
     * @param doc 文档配置
     */
    public String getStoreFileName(DocVO doc) {
        String path = doc.getFilePath();
        if (path != null && !path.endsWith("/")) {
            path += "/";
        }
        Assertions.assertNotNull(path, "路径不能为空");
        String fileName = doc.getDocName();
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("创建文件目录[" + path + "]失败!");
            }
        }
        if (!path.endsWith("/")) {
            path += "/";
        }
        path = path + fileName;
        return path;
    }

}
