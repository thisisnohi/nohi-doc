package nohi.doc.config.meta.ftp;

/**
 * FTP服务器信息
 *
 * @author NOHI
 * @crated date 2013-2-27
 */
public class FtpServiceInfo {

	private String ftpUsername;
	private String ftpPassword;
	private String ftpAddress;
	private String ftpPort;
	private String ftpRemotePath;
	private String ftpSystem;
	private String ftpCharset;


	public String getFtpCharset() {
		return ftpCharset;
	}

	public void setFtpCharset(String ftpCharset) {
		this.ftpCharset = ftpCharset;
	}

	public String getFtpUsername() {
		return ftpUsername;
	}

	public void setFtpUsername(String ftpUsername) {
		this.ftpUsername = ftpUsername;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getFtpAddress() {
		return ftpAddress;
	}

	public void setFtpAddress(String ftpAddress) {
		this.ftpAddress = ftpAddress;
	}

	public String getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(String ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpRemotePath() {
		return ftpRemotePath;
	}

	public void setFtpRemotePath(String ftpRemotePath) {
		this.ftpRemotePath = ftpRemotePath;
	}

	public String getFtpSystem() {
		return ftpSystem;
	}

	public void setFtpSystem(String ftpSystem) {
		this.ftpSystem = ftpSystem;
	}
}
