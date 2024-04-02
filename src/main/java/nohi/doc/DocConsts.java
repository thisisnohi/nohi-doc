
package nohi.doc;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class DocConsts {

	public static void main(String[] args){
		System.out.println("dd:" + DocConsts.configfile);
		System.out.println("defaultConf:" + defaultConf + ",encodeConf:" + encodeConf);
	}


	public static String defaultConf = "nohi-doc.xml";
	public static String encodeConf = "docconf/nohi_encode_conf.xml";

	public static final String storeFileType = "storeFileType";
	public static final String localPath = "localPath";
	public static final String configfile = "doc.properties";
	static{
		InputStream resourceAsStream = DocConsts.class.getClassLoader().getResourceAsStream(configfile);

		java.util.Properties properties = new Properties();
		try {
			properties.load(resourceAsStream);
			String conf = properties.getProperty("appConf");
			if (null != conf && !"".equals(conf)) {
				defaultConf = conf;
			}
			encodeConf = properties.getProperty("encodeConf");
			log.debug("defaultConf:" + defaultConf + ",encodeConf:" + encodeConf);
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	/**
	 * NOHI_DOC  EXCEL 重复生成SHEET
	 */
	public static final String EXCEL_SHEET_REPEAT = "repeat";

	/**
	 * NOHI_DOC 工程存储文件类型  FTP
	 */
	public static final String NOHI_DOC_STORE_FILE_TYPE_FTP = "FTP";

	/**
	 * NOHI_DOC 工程存储文件类型  LOCAL PATH
	 */
	public static final String NOHI_DOC_STORE_FILE_TYPE_LOCAL = "LOCAL";

	/**
	 * 文档类型: EXCEL
	 */
	public static final String DOC_TYPE_EXCEL = "EXCEL";

	/**
	 * 文档类型: PDF
	 */
	public static final String DOC_TYPE_PDF = "PDF";

	/**
	 * EXCEL块类型: TABLE　列表
	 */
	public static final String BLOCK_TYPE_TABLE = "TABLE";

	/**
	 * EXCEL块类型: FIELD　字段
	 */
	public static final String BLOCK_TYPE_FIELD = "field";

	/**
	 * EXCEL块行位置类型：relative-相对位置　absolute-绝对的
	 */
	public static final String BLOCK_ROW_LOCATION_RELATIVE = "relative";

	/**
	 * EXCEL块行位置类型：relative-相对位置　absolute-绝对的
	 */
	public static final String BLOCK_ROW_LOCATION_ABSOLUTE = "absolute";

	/**
	 * EXCEL table 块中　list 数据插入方式: insert 新增行　update 更新现有行
	 */
	public static final String EXEXL_BLOCK_LIST_INSERTTYPE_INSERT = "insert";

	/**
	 * EXCEL table 块中　list 数据插入方式: insert 新增行　update 更新现有行
	 */
	public static final String EXEXL_BLOCK_LIST_INSERTTYPE_UPDATE = "update";

	/**
	 * PDF表格类型: TABLE　列表
	 */
	public static final String PDF_UNIT_TYPE_TABLE = "TABLE";

	/**
	 * PDF单元格类型: FIELD　字段
	 */
	public static final String PDF_UNIT_TYPE_TEXT = "text";


}
