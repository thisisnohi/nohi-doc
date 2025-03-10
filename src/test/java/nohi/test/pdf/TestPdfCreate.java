package nohi.test.pdf;


import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>PDF创建</p>
 * @date 2025/03/07 22:09
 **/
public class TestPdfCreate {

    @Test
    public void create() throws IOException {
//        第 1 步：创建一个 PdfWriter 对象
//        该PdfWriter类表示PDF文档的作家。此类属于包com.itextpdf.kernel.pdf。此类的构造函数接受一个字符串，表示要在其中创建 PDF 的文件的路径。
//        通过向其构造函数传递一个字符串值（表示您需要创建 PDF 的路径）来实例化 PdfWriter 类，如下所示。
//
//        第 2 步：创建一个 PdfDocument 对象
//        该PdfDocument类为表示在iText的PDF文档类。此类属于包com.itextpdf.kernel.pdf。要实例化此类（在写入模式下），您需要将PdfWriter类的对象传递给其构造函数。
//        通过将上面创建的 PdfWriter 对象传递给其构造函数来实例化 PdfDocument 类，如下所示。
//
//        第 3 步：添加一个空页面
//        PdfDocument类的addNewPage()方法用于在 PDF 文档中创建一个空白页面。
//        为上一步创建的 PDF 文档添加一个空白页面，如下所示。
//
//        第 4 步：创建一个 Document 对象
//        包com.itextpdf.layout的Document类是创建自给自足的 PDF 时的根元素。此类的构造函数之一接受类 PdfDocument 的对象。
//        通过传递在前面的步骤中创建的类PdfDocument的对象来实例化Document类，如下所示。
//
//        步骤 5：关闭文档
//        使用Document类的close()方法关闭文档，如下所示。

        // 1、Creating a PdfWriter
        String dest = "itext_01.pdf";
        PdfWriter writer = new PdfWriter(dest);

        // 2、Creating a PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);

        // 3、Adding an empty page
        pdfDoc.addNewPage();

        // 4、Creating a Document
        Document document = new Document(pdfDoc);

        // 5、Closing the document
        document.close();
        System.out.println("PDF Created");

    }

    @Test
    @DisplayName("区域中断对象")
    public void createAreaBreak() throws IOException {
        // 1、Creating a PdfWriter
        String dest = "itext_02_areabreak.pdf";
        PdfWriter writer = new PdfWriter(dest);

        // 2、Creating a PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);

        // 3、Creating a Document
        Document document = new Document(pdfDoc);

        AreaBreak ab = new AreaBreak();
        document.add(ab);

        // 5、Closing the document
        document.close();
        System.out.println("PDF Created");

    }

    @Test
    @DisplayName("创建段落")
    public void createParagraph() throws IOException {
        // 1、Creating a PdfWriter
        String dest = "itext_02_paragraph.pdf";
        PdfWriter writer = new PdfWriter(dest);

        // 2、Creating a PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);

        // 3、Creating a Document
        Document document = new Document(pdfDoc);

        String para1 = "Tutorials Point originated from the idea that there exists a class of readers who respond better to online content and prefer to learn new skills at their own pace from the comforts of their drawing rooms.";
        String para2 = "The journey commenced with a single tutorial on HTML in 2006  and elated by the response it generated, we worked our way to adding fresh tutorials to our repository which now proudly flaunts a wealth of tutorials and allied articles on topics ranging from programming languages to web designing to academics and much more.";

        Paragraph p1 = new Paragraph(para1);
        Paragraph p2 = new Paragraph(para2);

        document.add(p1);
        document.add(p2);

        // 5、Closing the document
        document.close();
        System.out.println("PDF Created");

    }

    @Test
    @DisplayName("创建all")
    public void createAll() throws IOException {
        // 1、Creating a PdfWriter
        String dest = "itext_02_paragraph.pdf";
        PdfWriter writer = new PdfWriter(dest);

        // 2、Creating a PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);

        // 3、Creating a Document
        Document document = new Document(pdfDoc);

        PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);

        String para1 = "Tutorials  这里有中文 Point originated from the idea that there exists a class of readers who respond better to online content and prefer to learn new skills at their own pace from the comforts of their drawing rooms.";
        String para2 = "The journey 这里有中文 commenced with a single tutorial on HTML in 2006  and elated by the response it generated, we worked our way to adding fresh tutorials to our repository which now proudly flaunts a wealth of tutorials and allied articles on topics ranging from programming languages to web designing to academics and much more.";

        Paragraph p1 = new Paragraph(para1);
        Paragraph p2 = new Paragraph(para2);
        p2.setFont(font);

        document.add(p1);
        document.add(p2);

        // setListSymbol("\u2022") 不起作用，可能和字体有关
//        List list = new List().setSymbolIndent(12).setListSymbol("*").setFont(font);
        List list = new List().setSymbolIndent(12).setListSymbol("\t").setFont(font);
        list.add("Java");
        list.add("JavaFX");
        list.add("Apache Tika");
        list.add("二四六八十");
        list.add("OpenCV");

        list.add(new ListItem("Never gonna give you up")).add(new ListItem("一三五七九")).add(new ListItem("Never gonna let you down")).add(new ListItem("Never gonna run around and desert you")).add(new ListItem("Never gonna make you cry")).add(new ListItem("Never gonna say goodbye")).add(new ListItem("Never gonna tell a lie and hurt you"));

        document.add(list);

        // 表格
        // Creating a table
        float[] pointColumnWidths = {150F, 150F, 150F};
        Table table = new Table(pointColumnWidths);
        table.setWidth(UnitValue.createPercentValue(100));
        table.setFont(font);

        Cell c1 = new Cell();                        // Creating cell 1
        c1.add(new Paragraph("Name"));                              // Adding name to cell 1
        c1.setBackgroundColor(ColorConstants.ORANGE);      // Setting background color
        c1.setBorder(Border.NO_BORDER);              // Setting border
        c1.setTextAlignment(TextAlignment.CENTER);   // Setting text alignment

        table.addHeaderCell(c1);
        table.addHeaderCell("ID");
        table.addHeaderCell("年龄");

        table.addCell("张三");
        table.addCell("10001");
        table.addCell("18");
        table.addCell("李四");
        table.addCell("1002");
        table.addCell("19");

        // Adding Table to document
        document.add(table);

        // 5、Closing the document
        document.close();
        System.out.println("PDF Created");

    }
}
