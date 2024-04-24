package nohi.doc.pdf;

import nohi.doc.pdf.vo.PdfInnerVO;
import nohi.doc.pdf.vo.PdfVO;
import nohi.doc.service.IDocService;
import nohi.doc.service.impl.DocService;
import nohi.doc.vo.DocVO;
import nohi.utils.DocCommonUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class _01_PDFDocExport {



	@Test
	public void export() throws Exception{
		IDocService docService = new DocService();

		DocVO doc = new DocVO();
		doc.setDocId("pdf_template");
		doc.setDocType("PDF");

		PdfVO pvo = new PdfVO();
		pvo.setBd(new BigDecimal(123456.789));
		pvo.setDate(DocCommonUtils.getCurrentDate());
		pvo.setDouble_1(22.33);
		pvo.setDouble_2(new Double(33.44));
		pvo.setInt_1(55);
		pvo.setInteger_1(66);
		pvo.setString_1("字符串");
		pvo.setCodeCd("156"); //币种 156 人民币

		PdfInnerVO inn = new PdfInnerVO();
		inn.setBd(new BigDecimal(123456.789));
		inn.setDate(DocCommonUtils.getCurrentDate());
		inn.setDouble_1(22.33);
		inn.setDouble_2(new Double(33.44));
		inn.setInt_1(55);
		inn.setInteger_1(66);
		inn.setString_1("内部对象String");
		inn.setCodeCd("840"); //币种 840 美元

		pvo.setInnerObject(inn);

		List<PdfInnerVO> temp = new ArrayList<PdfInnerVO>();
		for (int i = 1 ; i < 10 ; i ++) {
			PdfInnerVO pi = new PdfInnerVO();
			pi.setBd(new BigDecimal(123456.789));
			pi.setDate(DocCommonUtils.getCurrentDate());
			pi.setDouble_1(22.33);
			pi.setDouble_2(new Double(33.44));
			pi.setInt_1(i);
			pi.setInteger_1(66);
			pi.setString_1("字符串" + i);

			if ( i%3 == 1) {
				pi.setCodeCd("156"); //币种 840 美元
			}else if (i%3 == 2) {
				pi.setCodeCd("111"); //币种 840 美元
			}else {
				pi.setCodeCd("840"); //币种 840 美元
			}


			temp.add(pi);
		}
		pvo.setTestList(temp);
//		doc.setDataVO(pvo);

		doc = docService.exportDoc(doc);
		System.out.println("filePath: " + doc.getFilePath());
	}
}
