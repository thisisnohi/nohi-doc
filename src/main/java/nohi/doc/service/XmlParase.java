
package nohi.doc.service;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nohi.doc.DocConsts;
import nohi.doc.meta.DocBlockMeta;
import nohi.doc.meta.DocColMeta;
import nohi.doc.meta.DocMeta;
import nohi.doc.meta.DocPdfUnitMeta;
import nohi.doc.meta.DocSheetMeta;
import nohi.doc.meta.DocTemplateMeta;
import nohi.doc.meta.FtpServiceInfo;
import nohi.doc.meta.NOHIDocMeta;
import nohi.utils.Clazz;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


/**
 * XML解析
 * @author NOHI
 * @version $Revision: 1.1 $
 * 建立日期 2013-2-7
 */
@SuppressWarnings({ "unchecked"})
public class XmlParase{
	private static Logger log = Logger.getLogger(XmlParase.class);

	public static Document getDocumentasResourcePath(String confPath) throws Exception{
		//1 得到输入流
		InputStream in = XmlParase.class.getClassLoader().getResourceAsStream(confPath);
		in = DocConsts.class.getClassLoader().getResourceAsStream(confPath);
		//2 解析XML文件
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(in);
		} catch (Exception e1) {
			throw new Exception("读取配置文件[" + confPath + "]出错" , e1);
		} finally{
			if (null != in) {
				try{
					in.close();
				}catch(Exception e){
					log.error(e.getMessage(),e);
				}
			}
		}
		return doc;
	}

	/**
	 *　解析文档服务主配置
	 * @param confPath
	 * @return
	 * @throws Exception
	 */
	public static NOHIDocMeta paraseNOHIDoc(String confPath) throws Exception{
		if (null == confPath) {
			confPath = DocConsts.defaultConf;
		}

		Element root = getDocumentasResourcePath(confPath).getRootElement();

		// 3 解析服务列表: excel
		Map<String,DocMeta> excelMap = paraseDoc(root,"doc-excel");
		Map<String,DocMeta> pdfMap = paraseDoc(root,"doc-pdf");


		NOHIDocMeta nohiDoc = new NOHIDocMeta();
		nohiDoc.setExcelDocs(excelMap);
		nohiDoc.setPdfDocs(pdfMap);

		return nohiDoc;
	}

	/**
	 * 返回存储文件类型配置
	 * @param root
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> paraseStoreFile(String confPath) throws Exception{
		if (null == confPath) {
			confPath = DocConsts.defaultConf;
		}

		Element root = getDocumentasResourcePath(confPath).getRootElement();

		Element server = root.getChild("store-file");

		Map<String,String> map = new HashMap<String, String>();
		String storeFileType = server.getChild("storeFileType").getValue();
		String localPath = server.getChild("localPath").getValue();
		map.put(DocConsts.storeFileType, storeFileType);
		map.put(DocConsts.localPath, localPath);

		return map;

	}

	/**
	 * 返回ftp配置
	 * @param root
	 * @return
	 * @throws Exception
	 */
	public static FtpServiceInfo paraseFtpServer(String confPath) throws Exception{
		if (null == confPath) {
			confPath = DocConsts.defaultConf;
		}

		Element root = getDocumentasResourcePath(confPath).getRootElement();

		Element server = root.getChild("ftp-server");
		FtpServiceInfo ftp = new FtpServiceInfo();

		//解析children Element 列表属性到ftp对象中
		paraseElementToObject(ftp,server.getChildren());

		return ftp;

	}

	/**
	 * 解析element对象，设置值到对应对象中c
	 * @param obj   设置目标对象
	 * @param list  element列表
	 * @throws Exception
	 */
	private static void paraseElementToObject(Object obj , List<Element> list) throws Exception{

		if (null != list && !list.isEmpty()) {
			Method method = null;
			for (Element att : list) {
				method = Clazz.getMethod(obj.getClass(), att.getName(), "set", String.class);
				method.invoke(obj, att.getValue());
			}
		}
	}

	/**
	 * 解析Attribute对象，设置值到对应对象中
	 * @param obj   设置目标对象
	 * @param list  element列表
	 * @throws Exception
	 */
	private static void paraseAttributeToObject(Object obj , List<Attribute> list) throws Exception{

		if (null != list && !list.isEmpty()) {
			Method method = null;
			for (Attribute att : list) {
				method = Clazz.getMethod(obj.getClass(), att.getName(), "set", String.class);
				method.invoke(obj, att.getValue());
			}
		}
	}

	/**
	 * 解析主配置文件的doc
	 * @param root
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	private  static Map<String,DocMeta> paraseDoc(Element root , String doc) throws Exception{
		Element messageConf = root.getChild(doc);

		List<Element> childRenlist = messageConf.getChildren();
		Element e = null;
		Map<String,DocMeta> messageMap = new HashMap<String,DocMeta>();
		for (Iterator<Element> it = childRenlist.iterator(); it.hasNext();) {
			e = it.next();
			DocMeta d = new DocMeta();

			List<Attribute> attList = e.getAttributes();

			paraseAttributeToObject(d, attList);

			messageMap.put(d.getId(), d);
		}
		return messageMap;
	}

	/**
	 * 解析模板配置文件
	 * @param confPath
	 * @throws Exception
	 */
	public static Map<String,DocTemplateMeta> paraseTemplateConf(String confPath) throws Exception{
		if (null == confPath) {
			log.info("解析具体文档属性配置文件出错，没对应的配置文件");
			return null;
		}

		Element root = getDocumentasResourcePath(confPath).getRootElement();

		//所有文档模板配置文件列表
		Map<String,DocTemplateMeta> templateMap = new HashMap<String, DocTemplateMeta>();

		List<Element> eList = root.getChildren();//得到所有document对象
		for (Element e : eList) {
			DocTemplateMeta t = new DocTemplateMeta();

			// 解析  <document> 元素属性
			paraseAttributeToObject(t, e.getAttributes());


			//如果文档是Excel.需要解析Excel sheet
			if (DocConsts.DOC_TYPE_EXCEL.equals(t.getDocType())) {
				t.setSheetList(paraseExcelSheet(e));
			}else if (DocConsts.DOC_TYPE_PDF.equals(t.getDocType())) {
				t.setPdfUnitMap(parasePdf(e));
			}

			//放入Map
			templateMap.put(t.getId(), t);
		}

		return templateMap;
	}

	/**
	 * 解析PDF文件
	 * @param e
	 * @return
	 * @throws Exception
	 */
	private static Map<String,DocPdfUnitMeta> parasePdf(Element e) throws Exception{
		Map<String,DocPdfUnitMeta> unitMap = new HashMap<String, DocPdfUnitMeta>();
		List<Element> unitList = e.getChildren();//unit对象
		for (Element unit : unitList) {
			DocPdfUnitMeta t = new DocPdfUnitMeta();

			paraseAttributeToObject(t, unit.getAttributes());

			if (DocConsts.PDF_UNIT_TYPE_TABLE.equals(t.getType())) {
				t.setUnitTableMap(parasePdf(unit));
			}

			//放入Map
			unitMap.put(t.getName(), t);
		}
		return unitMap;
	}

	/**
	 * 解析Excel表单
	 * @throws Exception
	 */
	private static List<DocSheetMeta> paraseExcelSheet(Element e) throws Exception{
		List<DocSheetMeta> sheetList = new ArrayList<DocSheetMeta>();
		List<Element> sheetE = e.getChildren();//得到所有sheet对象
		if (null != sheetE) {
			for (Element sheet : sheetE) {
				DocSheetMeta t = new DocSheetMeta();
				String name = sheet.getAttributeValue("name");
				String type = sheet.getAttributeValue("type");
				String dynamicsheetname = sheet.getAttributeValue("dynamicsheetname");

				//解析块
				t.setBlockList(paraseBlock(sheet));

				t.setType(type);
				t.setName(name);
				t.setDynamicsheetname(dynamicsheetname);

				//放入Map
				sheetList.add(t);
			}
		}
		return sheetList;
	}

	/**
	 * 解析block
	 * @param e
	 * @return
	 * @throws Exception
	 */
	private static List<DocBlockMeta> paraseBlock(Element sheet) throws Exception{
		List<DocBlockMeta> blockList = new ArrayList<DocBlockMeta>();

		List<Element> blockE = sheet.getChildren();
		for (Element block : blockE) {
			DocBlockMeta t = new DocBlockMeta();

			paraseAttributeToObject(t, block.getAttributes());


			//如果是列表
			if (null != block.getChildren()) {
				Map<String,DocColMeta> colMap = new HashMap<String, DocColMeta>();

				List<Element> colListEmt = block.getChildren();
				for (Element colE : colListEmt) {
					DocColMeta col = new DocColMeta();

					paraseAttributeToObject(col, colE.getAttributes());

					//存放
					colMap.put(col.getColumn(), col);
				}
				t.setCols(colMap);
			}

			if (null == t.getInsertType() || "".equals(t.getInsertType())){
				t.setInsertType(DocConsts.EXEXL_BLOCK_LIST_INSERTTYPE_UPDATE);
			}

			//放入Map
			blockList.add(t);
		}

		return blockList;
	}
}
