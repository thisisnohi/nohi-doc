package nohi.doc.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 具体文档模板属性配置文件
 *
 * @author NOHI
 * @version $Revision: 1.1 $ 建立日期 2013-2-7
 */
public class DocTemplateMeta {
	private String id; // 文档ID
	private String name;// 文档名称
	private String template;// 文档模板
	private String data;// 文档存取数据的对象
	private String docType;// 文档类型 EXCEL
	private String templateSheetSize ;//Excel模板中 sheet模板数

	private List<DocSheetMeta> sheetList;
	//	private Map<String, DocSheetMeta> sheetMap;// sheet列表
	private Map<String,DocPdfUnitMeta> pdfUnitMap;//PDF单元格Map

	public List<DocSheetMeta> getSheetList() {
		if (null == sheetList){
			sheetList = new ArrayList<DocSheetMeta>();
		}
		return sheetList;
	}

	public void setSheetList(List<DocSheetMeta> sheetList) {
		this.sheetList = sheetList;
	}

	public String getTemplateSheetSize() {
		return templateSheetSize;
	}

	public void setTemplateSheetSize(String templateSheetSize) {
		this.templateSheetSize = templateSheetSize;
	}

	public Map<String, DocPdfUnitMeta> getPdfUnitMap() {
		return pdfUnitMap;
	}

	public void setPdfUnitMap(Map<String, DocPdfUnitMeta> pdfUnitMap) {
		this.pdfUnitMap = pdfUnitMap;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
