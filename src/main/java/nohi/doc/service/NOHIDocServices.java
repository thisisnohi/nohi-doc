package nohi.doc.service;

import nohi.doc.DocConsts;
import nohi.doc.meta.DocMeta;
import nohi.doc.meta.DocTemplateMeta;
import nohi.doc.meta.FtpServiceInfo;
import nohi.doc.meta.NOHIDocMeta;
import org.apache.log4j.Logger;

import java.util.Map;



/**
 * NOHI-doc 文档主服务
 *
 * @author NOHI
 * @version $Revision: 1.1 $ 建立日期 2013-2-7
 */
public class NOHIDocServices {
	private static Logger log = Logger.getLogger(NOHIDocServices.class);

	private static String storeFileType; //存储文件路径
	private static String localPath; //localPath
	private static FtpServiceInfo ftpInfo;
	private static String confPath = DocConsts.defaultConf;// 主配置文件路径

	private static NOHIDocMeta mainDoc;
	private static Map<String, DocTemplateMeta> template;// 所有文档模板配置文件


	public static String getStoreFileType() {
		if (null == storeFileType) {
			init();
		}
		return storeFileType;
	}

	private static void init(){
		try {
			Map<String,String> map = XmlParase.paraseStoreFile(confPath);
			storeFileType = map.get(DocConsts.storeFileType);
			localPath = map.get(DocConsts.localPath);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public static void setStoreFileType(String storeFileType) {
		NOHIDocServices.storeFileType = storeFileType;
	}

	public static String getLocalPath() {
		if (null == localPath) {
			init();
		}
		return localPath;
	}

	public static void setLocalPath(String localPath) {
		NOHIDocServices.localPath = localPath;
	}

	/**
	 * 取得FTP服务器信息: 根据指定配置文件解析
	 * @param confPath
	 * @return
	 */
	public static FtpServiceInfo getFtpInfo(String confPath) {
		if (null == ftpInfo) {
			if (null == confPath) {
				confPath = DocConsts.defaultConf;
			}

			try {
				ftpInfo = XmlParase.paraseFtpServer(confPath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(),e);
			}
		}

		return ftpInfo;
	}

	/**
	 * 取得FTP服务器信息: 取得默认服务器配置
	 * @param confPath
	 * @return
	 */
	public static FtpServiceInfo getFtpInfo() {
		if (null == ftpInfo) {
			ftpInfo = getFtpInfo(confPath);
		}

		return ftpInfo;
	}

	/**
	 * 设置FTP服务器信息
	 * @param ftp
	 */
	public static void setFtpInfo(FtpServiceInfo ftp) {
		ftpInfo = ftp;
	}

	public static NOHIDocMeta getMainDoc() {
		if (null == mainDoc) {
			init(confPath);
		}
		return mainDoc;
	}

	public NOHIDocServices() {

	}

	public NOHIDocServices(String confPath) {
		this.confPath = confPath;
	}

	/**
	 * 初始化
	 */
	public static void init(String confPath) {
		paraseConf(confPath);// 解析主配置文件
	}

	/**
	 * 解析文件
	 *
	 * @param confPath
	 */
	public static void paraseConf(String confPath) {
		try {
			NOHIDocMeta doc = XmlParase.paraseNOHIDoc(confPath);
			mainDoc = doc;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	/**
	 * 取得文档配置属性
	 *
	 * @param id
	 * @return
	 */
	public static DocMeta getDocMeta(String id) {
		NOHIDocMeta doc = getMainDoc();
		Map<String, DocMeta> excel = doc.getExcelDocs();
		DocMeta d = excel.get(id);
		if (null != d) {
			return d;
		}

		Map<String, DocMeta> pdf = doc.getPdfDocs();
		d = pdf.get(id);
		if (null != d) {
			return d;
		}

		return null;
	}

	/**
	 * 得到具体一个文档类型的具体属性
	 *
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static DocTemplateMeta getTemplate(String id) throws Exception {
		DocTemplateMeta tmp = null;
		if (null == template) {
			DocMeta dm = getDocMeta(id);
			if (null != dm) {
				Map<String, DocTemplateMeta> map = XmlParase.paraseTemplateConf(dm.getConf());
				if (null != map) {
					tmp = map.get(id);
					template = map;
				}

			}
		} else {
			tmp = template.get(id);
			if (null == tmp) {
				DocMeta dm = getDocMeta(id);
				if (null != dm) {
					Map<String, DocTemplateMeta> map = XmlParase.paraseTemplateConf(dm.getConf());
					if (null != map) {
						tmp = map.get(id);
						template = map;
					}
				}
			}
		}

		return tmp;
	}
}
