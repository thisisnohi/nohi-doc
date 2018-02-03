package nohi.doc.meta;

import java.util.Map;

public class NOHIDocMeta {
	private Map<String, DocMeta> excelDocs;
	private Map<String, DocMeta> pdfDocs;

	public Map<String, DocMeta> getExcelDocs() {
		return excelDocs;
	}

	public void setExcelDocs(Map<String, DocMeta> excelDocs) {
		this.excelDocs = excelDocs;
	}

	public Map<String, DocMeta> getPdfDocs() {
		return pdfDocs;
	}

	public void setPdfDocs(Map<String, DocMeta> pdfDocs) {
		this.pdfDocs = pdfDocs;
	}
}
