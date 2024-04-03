package nohi.doc.service.impl;

import lombok.extern.slf4j.Slf4j;
import nohi.doc.DocConsts;
import nohi.doc.config.meta.DocumentMeta;
import nohi.doc.service.IDocService;
import nohi.doc.service.NohiDocServices;
import nohi.doc.vo.DocVO;
import nohi.utils.DocCommonUtils;

import java.io.File;
import java.io.InputStream;



/**
 * 文档实现类
 * @author NOHI
 * @create date: 2013-2-15
 */
@Slf4j
public class DocService implements IDocService{

	public DocVO exportDoc(DocVO doc) throws Exception {
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

		//生成文件名
		String docVersion = doc.getDocVersion();
		if (null == docVersion || "".equals(docVersion.trim())) {
			docVersion = DocCommonUtils.getCurrentTimeStr("yyyyMMddHHmmssSSS");
		}

		StringBuffer filename = new StringBuffer();
		filename.append(template.getId())
				.append("_").append(template.getName())
				.append("_").append(docVersion)
		;

		//如果为Excel
		if (DocConsts.DOC_TYPE_EXCEL.equals(doc.getDocType())){
			//doc.setDocName(filename.append(".xls").toString());
			ExcelXlsxService excel = new ExcelXlsxService();
			excel.exportDoc(doc);
		}else if(DocConsts.DOC_TYPE_PDF.equals(doc.getDocType())){
			doc.setDocName(filename.append(".pdf").toString());
			PdfService excel = new PdfService();
			excel.exportDoc(doc);
		}

		log.debug("文件名[" + doc.getFilePath() + "]");
		return doc;
	}

	public Object importFromFile(DocVO doc, File inputFile) throws Exception {
		//0 检验输入数据
		if (null == doc || null == inputFile) {
			log.info("输入数据为空");
			return null;
		}

		log.debug("docType: " + doc.getDocType() + " ,docID:" + doc.getDocId());


		//1 取得文档模板
		DocumentMeta template = NohiDocServices.getDocumentByDocId(doc.getDocId());
		if (null == template) {
			log.error("文档模板为空,没有取得文档ID[" + doc.getDocId()+ "]对应的配置文件");
			throw new Exception("文档模板为空,没有取得文档ID[" + doc.getDocId()+ "]对应的配置文件");
		}

		//如果为Excel
		if (DocConsts.DOC_TYPE_EXCEL.equals(doc.getDocType())){
			ExcelXlsxService excel = new ExcelXlsxService();
			return excel.importFromFile(doc,inputFile);
		}else if(DocConsts.DOC_TYPE_PDF.equals(doc.getDocType())){
			log.debug("PDF暂时没有");
		}


		return null;
	}

	public Object importFromInputStream(DocVO doc, InputStream is) throws Exception {
		// TODO Auto-generated method stub
		//0 检验输入数据
		if (null == doc || null == is) {
			log.info("输入数据为空");
			return null;
		}

		log.debug("docType: " + doc.getDocType() + " ,docID:" + doc.getDocId());


		//1 取得文档模板
		DocumentMeta template = NohiDocServices.getDocumentByDocId(doc.getDocId());
		if (null == template) {
			log.info("文档模板为空,没有取得文档ID[" + doc.getDocId()+ "]对应的配置文件");
			return null;
		}

		//如果为Excel
		if (DocConsts.DOC_TYPE_EXCEL.equals(doc.getDocType())){
			ExcelXlsxService excel = new ExcelXlsxService();
			return excel.importFromInputStream(doc,is);
		}else if(DocConsts.DOC_TYPE_PDF.equals(doc.getDocType())){
			log.debug("PDF暂时没有");
		}


		return null;
	}

	public Object importFromInputStream(DocVO doc, InputStream is, boolean _valueFailExit) throws Exception {
		return importFromInputStream(doc,is);
	}
}
