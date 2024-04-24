package nohi.doc.config.meta.ftp;

import lombok.Data;

/**
 * FTP服务器信息
 *
 * @author NOHI
 * @create date 2013-2-27
 */
@Data
public class FtpServiceInfo {
	private String ftpUsername;
	private String ftpPassword;
	private String ftpAddress;
	private String ftpPort;
	private String ftpRemotePath;
	private String ftpSystem;
	private String ftpCharset;
}
