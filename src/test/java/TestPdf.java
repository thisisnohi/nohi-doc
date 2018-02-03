

import com.itextpdf.text.*;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

public class TestPdf {
	// http://prdownloads.sourceforge.net/itext/iTextAsian.jar

	public static void main(String[] args) {
		//createPDFWithChinese();
		// createPdf();
		fromPDFTempletToPdfWithValue();
		//fromHtmlToPdf();
	}

	/**
	 * 根据pdf模板填充相应的值：
	 * 	   1，如果是根据excel填充的话，在用Acrobat生成PDF模板前，
	 * 		  Excel单元格格式最好设置成文本，否则pdf填充值时可能中文无法显示
	 */
	public static void fromPDFTempletToPdfWithValue() {
		String fileName = "d:/logs/3_to_pdf.pdf"; //
		fileName = "template/pdf/pdf_template.pdf"; //
		try {
			PdfReader reader = new PdfReader(fileName);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PdfStamper ps = new PdfStamper(reader, bos);

			/**
			 * 使用中文字体
			 * 如果是利用 AcroFields填充值的不需要在程序中设置字体，在模板文件中设置字体为中文字体就行了
			 */
			BaseFont bf = BaseFont.createFont("STSong-Light","UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
			Font FontChinese = new Font(bf, 12, Font.NORMAL);

			AcroFields s = ps.getAcroFields();
			System.out.println("s: " + s);
			System.out.println("AcroFields: " + s.getFields());
			System.out.println("AcroFields.class: " + s.getFields().getClass());
			System.out.println("getSignatureNames: " + s.getSignatureNames());
			System.out.println("getSignatureNames: " + s.getTotalRevisions());
			System.out.println("s: " + s.getBlankSignatureNames());
			System.out.println("s: " + s.getFieldCache());
			System.out.println("s: " + s.getSubstitutionFonts());

			int i = 1;
			for (Iterator it = s.getFields().keySet().iterator(); it.hasNext(); i++) {
				String name = (String) it.next();
				String value = s.getField(name);
				System.out.println("[" + i + "- name:" + name + ", value: "+value+"]");
				s.setField(""+name.trim(), "aaa一二三");
			}

			s.setField("Text1", "NOHI");
			s.setField("Text2", "2011-04-05");//注意pdf中域的大小，这里设置的值太长，pdf中会显示不全

			//设置为true/false在点击生成的pdf文档的填充域时有区别，
			ps.setFormFlattening(true);
			ps.close();

			FileOutputStream fos = new FileOutputStream("d:/logs/3_to_pdf_export.pdf");
			fos.write(bos.toByteArray());


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			//doc.close();
		}

	}

	/**
	 * 创建pdf
	 */
	public static void createPdf() {
		Document doc = null;
		try {
			doc = new Document();
			PdfWriter.getInstance(doc, new FileOutputStream("d:\\itext.pdf"));
			doc.open();
			BaseFont bfChi = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			Font fontChi = new Font(bfChi, 12, Font.NORMAL);

			doc.add(new Paragraph("Hello World,看看中文支持不........aaaaaaaaaaaaaaaaa",fontChi));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			doc.close();
		}
	}

	/**
	 * 生成一个PDF，with图片
	 */
	public static void createPDFWithChinese() {
		System.out.println("createPDFWithChinese...........");
		try {
			// 1.建立Document实例
			Document document = new Document();
			// 2.建立一个书写器与Document对象关联，通过书写器将文档写入磁盘

			PdfWriter.getInstance(document,new FileOutputStream("D:\\test.pdf"));

			// 3.打开文档
			document.open();
			// 4.向文档中添加内容
			// a)添加一个图片
			Image gif = Image.getInstance("d:\\pdf.BMP");

			System.out.println("gif" + gif);
			gif.setDpi(100, 100);
			gif.setBorderWidth(200);

			document.add(gif);
			// b)添加一个段落
			document.add(new Paragraph("iText HelloWorld"));
			// c)添加一个块
			document.add(new Chunk("Text is underline", FontFactory.getFont(
					FontFactory.HELVETICA_BOLD, 12, Font.UNDERLINE)));
			// d)添加中文,需要引入iTextAsian.jar
			// BaseFont bfChi = BaseFont.createFont("STSong-Light",
			// "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
			BaseFont bfChi = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			Font fontChi = new Font(bfChi, 12, Font.NORMAL);
			document.add(new Paragraph("中文测试", fontChi));
			// e)添加一个表格
			// 表格内部格式和html中的格式差不多
			PdfPTable table = new PdfPTable(4);
			table.setWidthPercentage(100);
			table.setWidthPercentage(100);
			table.addCell(new Paragraph("学号", fontChi));
			PdfPCell cell = new PdfPCell(new Paragraph("00000001", fontChi));
			cell.setColspan(3);
			table.addCell(cell);
			table.addCell(new Paragraph("姓名", fontChi));
			table.addCell(new Paragraph("张三", fontChi));
			table.addCell(new Paragraph("总成绩", fontChi));
			table.addCell(new Paragraph("160", fontChi));

			table.addCell(new Paragraph("学号", fontChi));
			PdfPCell cell2 = new PdfPCell(new Paragraph("00000002", fontChi));
			cell2.setColspan(3);
			table.addCell(cell2);
			table.addCell(new Paragraph("姓名", fontChi));
			table.addCell(new Paragraph("李四", fontChi));
			table.addCell(new Paragraph("总成绩", fontChi));
			table.addCell(new Paragraph("167", fontChi));
			document.add(table);
			// 5.关闭文档
			document.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 根据html生成pdf没有试过,不知道行不
	 */
	public static void fromHtmlToPdf(){
		try {
			Document document = new Document();
			StyleSheet st = new StyleSheet();
			st.loadTagStyle("body", "leading", "16,0");
			PdfWriter.getInstance(document, new FileOutputStream("d:\\html2.pdf"));
			document.open();
			List<Element> p = HTMLWorker.parseToList(new FileReader("d:\\to_pdf.htm"), st);
			for (int k = 0; k < p.size(); ++k)
				document.add((Element) p.get(k));
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}