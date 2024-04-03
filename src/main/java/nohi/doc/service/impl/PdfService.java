package nohi.doc.service.impl;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import lombok.extern.slf4j.Slf4j;
import nohi.doc.DocConsts;
import nohi.doc.config.meta.ftp.DocPdfUnitMeta;
import nohi.doc.config.meta.DocumentMeta;
import nohi.doc.service.IDocService;
import nohi.doc.service.NohiDocServices;
import nohi.doc.vo.DocVO;
import nohi.ftp.service.FtpServer;
import nohi.utils.Clazz;
import nohi.utils.DocCommonUtils;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * PDF服务类
 * @author NOHI
 * @crated date 2013-2-26
 */
@Slf4j
public class PdfService extends FtpServer implements IDocService{

	public DocVO exportDoc(DocVO doc) throws Exception {
		// TODO Auto-generated method stub
		//0 检验输入数据
		if (null == doc) {
			log.info("输入数据为空");
			return doc;
		}
		log.debug("docType: " + doc.getDocType() + " ,docID:" + doc.getDocId());

		//1 取得文档模板
		DocumentMeta template = NohiDocServices.getDocumentByDocId(doc.getDocId());
		if (null == template) {
			log.info("文档模板为空,没有取得文档ID[" + doc.getDocId()+ "]对应的配置文件");
			return doc;
		}
		//单元格
		Map<String, DocPdfUnitMeta>  unitMap = template.getPdfUnitMap();

		PdfReader reader = null;
		ByteArrayOutputStream bos = null;
		PdfStamper ps = null;

		String filePath = template.getTemplate();
//		filePath = "D:/NOHI/workspace/nohi-doc/conf/template/pdf/pdf_template.pdf";
		log.debug("文档模板:" + filePath);
		InputStream in = null;
		try {
			//1 得到输入流
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
			reader = new PdfReader(in);
			bos = new ByteArrayOutputStream();
			ps = new PdfStamper(reader, bos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * 使用中文字体
		 * 如果是利用 AcroFields填充值的不需要在程序中设置字体，在模板文件中设置字体为中文字体就行了
		 */
		BaseFont bf = null;
		try {
			bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
			Font FontChinese = new Font(bf, 12, Font.NORMAL);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AcroFields s = ps.getAcroFields();

		//设置单元格Map的值
		setUnitMapValue(unitMap,s,doc.getDataVO());


		//生成文件名
		if (null == doc.getDocName() || "".equals(doc.getDocName().trim())) {
			String docVersion = doc.getDocVersion();
			if (null == docVersion || "".equals(docVersion.trim())) {
				docVersion = DocCommonUtils.getCurrentTimeStr("yyyyMMddHHmmssSSS");
			}

			StringBuffer filename = new StringBuffer();
			filename.append(template.getId())
					.append("_")
					.append(template.getName())
					.append(docVersion)
					.append(".pdf");
			;
			doc.setDocName(filename.toString());
		}

		OutputStream os = null;
		try {

			//设置为true/false在点击生成的pdf文档的填充域时有区别，
			ps.setFormFlattening(true);
			ps.close();

			//文件路径
			doc.setFilePath(getStoreFileName(doc));
			os = new  FileOutputStream(doc.getFilePath());
			os.write(bos.toByteArray());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
		}finally{
			if (null != reader) {
				reader.close();
			}

			try {
				if (null != in) {
					in.close();
				}
				if (null != bos) {
					bos.close();
				}

				if (null != os) {
					os.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage());
			}
			closeFtp();
		}

		return doc;
	}

	/**
	 * 设置pdf模板单元格Map的值
	 * @param unitMap  pdf配置模板单元格
	 */
	private void setUnitMapValue(Map<String, DocPdfUnitMeta> unitMap,AcroFields af ,Object dataVO){
		String name = null; //单元格对应的name
		DocPdfUnitMeta unit = null;//单元格
		for (Iterator<String> it = unitMap.keySet().iterator();it.hasNext();) {
			name = it.next();
			unit = unitMap.get(name);

			setUnitValue(unit,af,dataVO);
		}
	}

	/**
	 * 设置单元格的值
	 * @param unit
	 */
	@SuppressWarnings("rawtypes")
	private void setUnitValue(DocPdfUnitMeta unit,AcroFields af,Object dataValue){
		//0 检验数据
		if (null == unit) {
			return;
		}

		if (DocConsts.PDF_UNIT_TYPE_TABLE.equals(unit.getType())) {
			//取得属性对应的字段值
			List list = null;
			try {
				list = (List) Clazz.getValue(dataValue, unit.getProperty());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (null == list) {
				log.info("没有找到dataValue[" + dataValue + "],属性[" + unit.getProperty() + "]的值");
				return ;
			}
			setUnitListValue(unit.getUnitTableMap(),af,list);
		}else if (DocConsts.PDF_UNIT_TYPE_TEXT.equals(unit.getType())) {
			String name = unit.getName();
			String property = unit.getProperty();
			String dataType = unit.getDataType();
			String pattern = unit.getPattern();
			String codeType = unit.getCodeType();

			//设置字段值
			try {
				//取得值
				Object obj = Clazz.getValue(dataValue, property);

				//设置PDF值
				af.setField(name, Clazz.getFieldStrValue(obj, dataType, pattern, codeType));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("设置字段[" + name + "]的值失败");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 设置PDF列表块的值
	 * @param unitMap
	 * @param af
	 */
	@SuppressWarnings("rawtypes")
	private void setUnitListValue(Map<String, DocPdfUnitMeta> unitMap,AcroFields af ,List list){
		String name = null;
		DocPdfUnitMeta unit = null;
		for (int i = 0 ; i < list.size();i++) {
			Object obj = list.get(i);

			for (Iterator<String> it = unitMap.keySet().iterator(); it.hasNext();) {
				name = it.next();
				unit = unitMap.get(name);

				//取得值
				try {
					Object value = Clazz.getValue(obj, unit.getProperty());
					String str = Clazz.getFieldStrValue(value, unit.getDataType(),unit.getPattern(), unit.getCodeType());
					log.debug(name + "[" + i + "]"+ str + "==" + str);
					//设置PDF值
					af.setField(name + "[" + i + "]", str);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.error("设置字段[" + name + "]的值失败");
					e.printStackTrace();
				}
			}
		}
	}


	public Object importFromFile(DocVO doc, File inputFile) {
		log.debug("pdf暂不支持导入");
		return null;
	}


	public Object importFromInputStream(DocVO doc, InputStream is) {
		// TODO Auto-generated method stub
		log.debug("pdf暂不支持导入");
		return null;
	}

	public Object importFromInputStream(DocVO doc, InputStream is, boolean _valueFailExit) throws Exception {
		return importFromInputStream(doc,is);
	}
}
