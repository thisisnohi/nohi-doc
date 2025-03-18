package nohi.test.pdf;


import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.*;
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

        PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);

        // 2、Creating a PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addFont(font);
        PdfPage page = pdfDoc.addNewPage();
        // 3、Creating a Document
        Document document = new Document(pdfDoc);


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
        // 无边框
        c1.setBorder(Border.NO_BORDER);              // Setting border
        // 红色边框
        Border b1 = new DashedBorder(ColorConstants.RED, 1);
        c1.setBorder(b1);
        c1.setTextAlignment(TextAlignment.CENTER);   // Setting text alignment

        table.addHeaderCell(c1);

        Cell c2 = new Cell();
        c2.add(new Paragraph("ID"));
        c2.setBorder(new SolidBorder(ColorConstants.RED, 1));
        c2.setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(c2);

        Cell c3 = new Cell();
        c3.add(new Paragraph("年龄"));
        c3.setBorder(new DottedBorder(ColorConstants.DARK_GRAY, 3));
        c3.setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(c3);

        Cell c4 = new Cell();
        c4.add(new Paragraph("张三"));
        c4.setBorder(new DoubleBorder(ColorConstants.DARK_GRAY, 3));
        c4.setTextAlignment(TextAlignment.CENTER);
        table.addCell(c4);

        Cell c5 = new Cell();
        c5.add(new Paragraph("10001"));
        c5.setBorder(new RoundDotsBorder(ColorConstants.RED, 3));
        c5.setTextAlignment(TextAlignment.CENTER);
        table.addCell(c5);

        Cell c6 = new Cell();
        c6.add(new Paragraph("18"));
        c6.setBorder(new RoundDotsBorder(ColorConstants.RED, 3));
        c6.setTextAlignment(TextAlignment.CENTER);
        table.addCell(c6);

        /** 添加图片 **/
        String imFile = "src/test/resources/assert/images/sirref.png";
        ImageData data = ImageDataFactory.create(imFile);
        Image img = new Image(data);
        table.addCell(img.setAutoScale(true));

        /** 嵌套表 **/
        // Creating nested table for contact
        float[] pointColumnWidths2 = {150f, 150f};
        Table nestedTable = new Table(pointColumnWidths2);

        // Populating row 1 and adding it to the nested table
        Cell nested1 = new Cell();
        nested1.add(new Paragraph("Phone"));
        nestedTable.addCell(nested1);

        Cell nested2 = new Cell();
        nested2.add(new Paragraph("9848022338"));
        nestedTable.addCell(nested2);

        // Populating row 2 and adding it to the nested table
        Cell nested3 = new Cell();
        nested3.add(new Paragraph("email"));
        nestedTable.addCell(nested3);

        Cell nested4 = new Cell();
        nested4.add(new Paragraph("Raju123@gmail.com"));
        nestedTable.addCell(nested4);

        // Populating row 3 and adding it to the nested table
        Cell nested5 = new Cell();
        nested5.add(new Paragraph("Address"));
        nestedTable.addCell(nested5);

        Cell nested6 = new Cell();
        nested6.add(new Paragraph("Hyderabad"));
        nestedTable.addCell(nested6);

        table.addCell("联系人：");
        // 嵌套表
        table.addCell(nestedTable);

        table.addCell("李四");
        table.addCell("1002");
        table.addCell("19");

        /** 列表 **/
        List list1 = new List();
        ListItem item1 = new ListItem("JavaFX");
        ListItem item2 = new ListItem("Java");
        ListItem item3 = new ListItem("Java Servlets");
        list1.add(item1);
        list1.add(item2);
        list1.add(item3);

        table.addCell(list1);


        table.addCell("1003");
        table.addCell("20");

        // Adding Table to document
        document.add(table);

        /** 图像 **/
        imFile = "src/test/resources/assert/images/sirref.png";
        data = ImageDataFactory.create(imFile);
        img = new Image(data);
        // 设置位置
        img.setFixedPosition(0, 400);
        img.setWidth(0.1f);
        img.setHeight(0.1f);
//        img.setAutoScale(true);
        img.setRotationAngle(45);
        document.add(img);

        /** 文本注释 **/
        Rectangle rect = new Rectangle(20, 800, 0, 0);
        PdfAnnotation annotation = new PdfTextAnnotation(rect);
        annotation.setColor(ColorConstants.RED);
        annotation.setTitle(new PdfString("Hello World"));
        annotation.setContents("Hi 你好，又是一个新的开始...");
        page.addAnnotation(annotation);

        /** 链接注释 **/
        Rectangle rect2 = new Rectangle(0, 0);
        PdfLinkAnnotation linkAnn = new PdfLinkAnnotation(rect2);
        PdfAction action = PdfAction.createURI("https://nohi.online");
        linkAnn.setAction(action);
        Link link = new Link("Click here", linkAnn);

        Paragraph paragraph = new Paragraph("Hi welcome to NOHI online");
        paragraph.add(link.setUnderline());

        document.add(paragraph);

        /**  创建线注释  **/
        Rectangle rect3 = new Rectangle(0, 0);
        float[] floatArray = new float[]{
                20, 700, page.getPageSize().getWidth() - 20, 700
        };
        annotation = new PdfLineAnnotation(rect3, floatArray);
        annotation.setColor(ColorConstants.BLUE);
        annotation.setTitle(new PdfString("Hello 创建线注释", PdfEncodings.UNICODE_BIG));
        annotation.setContents("Hi welcome to NOHI 创建线注释");
        page.addAnnotation(annotation);

        /** 标记注释 **/
        rect = new Rectangle(105, 790, 64, 10);
        floatArray = new float[]{169, 790, 105, 790, 169, 800, 105, 800};
        annotation = PdfTextMarkupAnnotation.createHighLight(rect, floatArray);
        annotation.setColor(ColorConstants.YELLOW);
        annotation.setTitle(new PdfString("Hello 标记注释!", PdfEncodings.UNICODE_BIG));
        annotation.setContents(new PdfString("Hi welcome to 标记注释", PdfEncodings.UNICODE_BIG));
        page.addAnnotation(annotation);

        /** 圆形注释 **/
        rect = new Rectangle(200, 750, 50, 50);
        annotation = new PdfCircleAnnotation(rect);
        annotation.setColor(ColorConstants.GREEN);
        annotation.setTitle(new PdfString("Hello 圆形注释!", PdfEncodings.UNICODE_BIG));
        annotation.setContents(new PdfString("Hi welcome to 圆形注释", PdfEncodings.UNICODE_BIG));
        page.addAnnotation(annotation);

        // 添加新的页
        page = pdfDoc.addNewPage();
        /** 绘制圆弧 **/
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.arc(50, 750, 150, 800, 0, 100);
        canvas.fill();

        /** 在PDF上画线 **/
        // 参考：https://www.cnblogs.com/antLaddie/p/18263491
        canvas = new PdfCanvas(page);
        // 画直线（普通）
        canvas.saveState()
                .moveTo(50, 600)   // 将画笔移到指定位置
                .setLineWidth(2)          // 线粗
                .setStrokeColor(new DeviceRgb(255, 0, 0)) // 描边颜色
                .lineTo(50, 700)   // 画笔在画布上绘制线到指定位置
                .stroke().restoreState();

        canvas.moveTo(50, 600)
                .setLineWidth(2)
                .setStrokeColor(ColorConstants.ORANGE)
                .lineTo(300, 600)
                        .stroke();

        canvas.setColor(ColorConstants.BLUE, true);
        canvas.circle(300, 600, 10);

        canvas.fill();


        // 通过PDF页来构建画布
        PdfCanvas pdfCanvas = new PdfCanvas(page);
        // 画直线（普通）
        pdfCanvas.saveState()
                .moveTo(50, 50)   // 将画笔移到指定位置
                .setLineWidth(2)          // 线粗
                .setStrokeColor(new DeviceRgb(255, 0, 0)) // 描边颜色
                .lineTo(100, 100)   // 画笔在画布上绘制线到指定位置
                .stroke().restoreState();
        // 使用画布线条画一个 "L"
        pdfCanvas.saveState()
                .moveTo(120, 100)   // 从这个点开始下笔
                .lineTo(120, 50)    // 画 |
                .lineTo(150, 50)    // 画 ——
                .setStrokeColor(new DeviceRgb(255, 0, 255))
                .stroke().restoreState();
        // 使用画布线条画一个 "▲"（填充已闭合）
        pdfCanvas.saveState()
                .moveTo(200, 50)     // 从这个点开始下笔
                .setLineWidth(5)     // 设置线粗5磅
                .lineTo(225, 100)    // 画 /
                .lineTo(250, 50)     // 画 \
                // .lineTo(200, 50)     // 画 ——
                // 最后的 "——" 可以不用画，让closePath()帮我们关闭路径
                .closePath()            //  关闭路径（结束点连向开始点）
                .setStrokeColor(new DeviceRgb(0, 0, 255)) // 线条颜色
                .setFillColor(new DeviceRgb(255, 0, 0))   // 设置填充色
                .closePathFillStroke()     // 设置路径闭合及轮廓和填充（那些路径属性必须在闭合前设置完）
                .stroke().restoreState();
        // 使用画布线条画一个 "▲"（填充未闭合）
        pdfCanvas.saveState()
                .moveTo(400, 50)     // 从这个点开始下笔
                .setLineWidth(5)     // 设置线粗5磅
                .lineTo(425, 100)    // 画 /
                .lineTo(450, 50)     // 画 \
                .lineTo(400, 50)     // 画 ——
                .setStrokeColor(new DeviceRgb(0, 0, 255))   // 线条颜色
                .setFillColor(new DeviceRgb(255, 0, 0))     // 设置填充色
                .fillStroke()     // 设置路径轮廓及填充（那些路径属性必须在闭合前设置完）
                .stroke().restoreState();
        // 释放画布。使用完画布后，请使用此方法。
        pdfCanvas.release();


        // 5、Closing the document
        document.close();
        System.out.println("PDF Created");

    }
}
