package nohi.doc.vo;

/**
 * 文档服务传递数据
 *
 * @author NOHI
 * @create date: 2013-2-15
 */
public class DocVO {
	private String repeatSheet;
	private String docType;
	private String docId;
	private Object dataVO;
	private String docVersion; //文件版本 如果没有指定，则为时间精确到毫秒
	private String docName;//文件名

	private String storeFileType;//存放文件类型  FTP / 路径

	private String filePath;//文件全路径
	private String tmpDir;


	public String getTmpDir() {
		return tmpDir;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

	public String getRepeatSheet() {
		return repeatSheet;
	}

	public void setRepeatSheet(String repeatSheet) {
		this.repeatSheet = repeatSheet;
	}

	public String getStoreFileType() {
		return storeFileType;
	}

	public void setStoreFileType(String storeFileType) {
		this.storeFileType = storeFileType;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocVersion() {
		return docVersion;
	}

	public void setDocVersion(String docVersion) {
		this.docVersion = docVersion;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public Object getDataVO() {
		return dataVO;
	}

	public void setDataVO(Object dataVO) {
		this.dataVO = dataVO;
	}
}
