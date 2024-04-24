
package nohi.doc;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class DocConsts {


	public static String defaultConf = "nohi-doc.xml";
	public static String encodeConf = "docconf/nohi_encode_conf.xml";

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
	 * PDF表格类型: TABLE　列表
	 */
	public static final String PDF_UNIT_TYPE_TABLE = "TABLE";

	/**
	 * PDF单元格类型: FIELD　字段
	 */
	public static final String PDF_UNIT_TYPE_TEXT = "text";


}
