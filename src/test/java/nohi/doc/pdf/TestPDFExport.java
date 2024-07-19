package nohi.doc.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;


public class TestPDFExport {

	/**
	 * 根据模板生成PDF文件
	 */
	@Test
	public void exportPdfFromPDFTemplate() throws Exception{
//		OutputStream os = null;
//		PdfStamper ps = null;
//		PdfReader reader = null;
//		try {
//			// 2 读入pdf表单
//			reader = new PdfReader(path+ "/"+filename);
//			// 3 根据表单生成一个新的pdf
//			ps = new PdfStamper(reader, os);
//			// 4 获取pdf表单
//			AcroFields form = ps.getAcroFields();
//			// 5给表单添加中文字体 这里采用系统字体。不设置的话，中文可能无法显示
//			BaseFont bf = BaseFont.createFont("C:/WINDOWS/Fonts/SIMSUN.TTC,1",
//					BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//			form.addSubstitutionFont(bf);
//			// 6查询数据================================================
//			Map data = new HashMap();
//			data.put("commitTime", gwclwxsqBean.getCommitTime());
//			data.put("driver", gwclwxsqBean.getDriver());
//			data.put("carId", gwclwxsqBean.getCarId());
//			data.put("carType", gwclwxsqBean.getCarType());
//			data.put("repairAddress", gwclwxsqBean.getRepairAddress());
//			data.put("repairCost",gwclwxsqBean.getRepairCost());
//			data.put("project", gwclwxsqBean.getProject());
//			data.put("fwbzzxfzrYj", gwclwxsqBean.getFwbzzxfzrYj());
//			data.put("fgldspYj", gwclwxsqBean.getFgldspYj());
//			data.put("remarks", gwclwxsqBean.getRemarks());
//			// 7遍历data 给pdf表单表格赋值
//			for (String key : data.keySet()) {
//				form.setField(key,data.get(key).toString());
//			}
//			ps.setFormFlattening(true);
//			log.info("*******************PDF导出成功***********************");
//		} catch (Exception e) {          log.error("*******************PDF导出失败***********************");
//			e.printStackTrace();
//		} finally {
//			try {
//				ps.close();
//				reader.close();
//				os.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	}

	@Test
	public void testPdfWithJavaCode() throws FileNotFoundException {
		String dest = System.getProperty("user.dir") + File.separator + "2024.pdf";
		PdfWriter writer = new PdfWriter(dest);
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf);
		document.add(new Paragraph("Hello World!"));
		document.close();
	}
}
