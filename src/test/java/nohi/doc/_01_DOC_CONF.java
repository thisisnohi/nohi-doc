package nohi.doc;


import nohi.doc.meta.DocBlockMeta;
import nohi.doc.meta.DocColMeta;
import nohi.doc.meta.DocMeta;
import nohi.doc.meta.DocSheetMeta;
import nohi.doc.meta.DocTemplateMeta;
import nohi.doc.meta.FtpServiceInfo;
import nohi.doc.meta.NOHIDocMeta;
import nohi.doc.service.NOHIDocServices;
import nohi.utils.BasicValue;

import org.junit.Test;



/**
 * 文档配置文件测试
 * @author NOHI
 */
public class _01_DOC_CONF {

	/**
	 * 解析  ftp文件
	 */
	@Test
	public void paraseMainDoc(){
		FtpServiceInfo f = NOHIDocServices.getFtpInfo();

		System.out.println(BasicValue.toString(f));
	}

	/**
	 * 解析 doc-excel
	 */
	@Test
	public void paraseDocexcel(){
		NOHIDocMeta f = NOHIDocServices.getMainDoc();

		System.out.println(BasicValue.toString(f));

		if (null != f ) {
			System.out.println(f.getExcelDocs());
			if (null != f.getExcelDocs()) {
				for (DocMeta d : f.getExcelDocs().values()) {
					System.out.println(BasicValue.toString(d));
				}
			}

			if (null != f.getPdfDocs()) {
				for (DocMeta d : f.getPdfDocs().values()) {
					System.out.println(BasicValue.toString(d));
				}
			}
		}
	}

	/**
	 * 解析 doc-excel
	 * @throws Exception
	 */
	@Test
	public void paraseExcelTemplate() throws Exception{
		DocTemplateMeta d = NOHIDocServices.getTemplate("TEST");
		System.out.println(BasicValue.toString(d));
		if (null != d && null != d.getSheetList()) {
			System.out.println("---------------------------------------------");
			for (DocSheetMeta a : d.getSheetList()) {
				System.out.println(BasicValue.toString(a));

				if (null != a.getBlockList()) {
					for (DocBlockMeta b : a.getBlockList()) {
						System.out.println(BasicValue.toString(b));

						if (null != b.getCols()) {
							for (DocColMeta dc : b.getCols().values()) {
								System.out.println(BasicValue.toString(dc));
							}
						}
					}
				}
			}
		}
	}

}
