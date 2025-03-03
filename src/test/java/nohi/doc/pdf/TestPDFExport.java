package nohi.doc.pdf;

import com.google.common.collect.Lists;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.io.font.CjkResourceLoader;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import nohi.doc.excel.vo.InnerVO;
import nohi.doc.excel.vo.TestDocVO;
import nohi.doc.excel.vo.TestListVO;
import nohi.utils.Clazz;
import nohi.utils.DateUtils;
import nohi.utils.FileUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Slf4j
public class TestPDFExport {

    @Test
    public void show() {
        String baseName = "STSong-Light";
        Set<String> set = CjkResourceLoader.getCompatibleCmaps(baseName);
        log.info("cmap : {}", set);
    }

    /**
     * 根据模板生成PDF文件
     */
    @Test
    public void exportPdfOld() throws Exception {
        String fileName = "template/pdf/pdf_template.pdf";
        String outPutFileName = "pdf_template_out.pdf";
        // 1、创建一个PdfWriter对象，将文档写入到文件中
        PdfReader reader = new PdfReader(fileName);
        PdfWriter writer = new PdfWriter(outPutFileName);
        // 2、初始化一个PdfDocument对象
        PdfDocument pdf = new PdfDocument(reader, writer);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

        // 创建字体
        // 宋体支持的 encoding
        // Load a CJK font with CMap
        // GBTpc-EUC-V, GBK2K-V, GBpc-EUC-H, UniGB-UTF8-H, GB-V,
        // UniGB-UTF32-H, GBT-EUC-H, GB-EUC-H, GBpc-EUC-V, UniGB-UTF8-V,
        // GBKp-EUC-H, GBT-H, GBTpc-EUC-H, GBK2K-H,
        // Adobe-GB1-4, Adobe-GB1-3, Adobe-GB1-2, Adobe-GB1-1, Adobe-GB1-0,
        // GBKp-EUC-V, GBT-V, GB-EUC-V, GBK-EUC-V, UniGB-UTF16-V, Adobe-GB1-5,
        // GBK-EUC-H, UniGB-UCS2-H, UniGB-UTF16-H, GB-H, GBT-EUC-V,
        // UniGB-UCS2-V, UniGB-UTF32-V
        PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        log.info("font: {}", font == null ? "IS NULL" : font.getFontProgram().toString());
        PdfFont defaultFont = pdf.getDefaultFont();
        log.info("defaultFont: {} isEmbedded:{}", defaultFont.getFontProgram().toString(), defaultFont.isEmbedded());

        // 循环表单字段
        form.getAllFormFields().forEach((item, field) -> {
            // 设置字段的值、字体...
            if ("Text3".equals(item)) {
                field.setFont(font);
                field.setValue("abcdefg1宋体").setFont(font).setColor(ColorConstants.RED).setFontSize(20);
            } else if ("Text9".equals(item)) {
                field.setFont(font);
                field.setFontSizeAutoScale();
                field.setValue("abcdefg2宋体");
            } else {
                field.setValue("abcdefg默认字体34");
            }
        });
        form.flattenFields();

        pdf.close();
    }

    public static TestDocVO getData(int listSize) {
        TestDocVO data = new TestDocVO();
        data.setStr1("字符串");
        data.setStr2("12345678.90123");
        data.setIntV1(123456789);
        data.setIntV2(123456789);
        data.setDouble1(1234.1234);
        data.setDouble2(123456789.01234);
        data.setFloatV1(1234.01234f);
        data.setFloatV2(0.01234f);
        data.setDate1(new Date());
        data.setDate2(DateUtils.parseDate("2024-01-02 23:24:25", DateUtils.HYPHEN_TIME));
        data.setBooleanV1(true);
        data.setBooleanV2(false);

        InnerVO vo = new InnerVO();
        data.setInnerObject(vo);
        vo.setStr1("字符串");
        vo.setStr2("12345678.90123");
        vo.setIntV1(123456789);
        vo.setIntV2(123456789);
        vo.setDouble1(1234.1234);
        vo.setDouble2(123456789.01234);
        vo.setFloatV1(1234.01234f);
        vo.setFloatV2(0.01234f);
        vo.setFloatV2(0.01234f);
        vo.setDate1(new Date());
        vo.setDate2(DateUtils.parseDate("2024-01-02 03:04:05", DateUtils.HYPHEN_TIME));
        vo.setBooleanV1(true);
        vo.setBooleanV2(false);

        List<TestListVO> list = Lists.newLinkedList();
        data.setList(list);
        for (int i = 0; i < listSize; i++) {
            TestListVO item = new TestListVO();
            Map<String, String> map = new HashMap<String, String>();
            item.setId(i + 1);
            item.setName("姓名" + i);
            item.setAge(18 + i);
            item.setAmt(2000.012d + i);
            if (i % 3 == 0) {
                item.setTest("156");
            } else if (i % 3 == 1) {
                item.setTest("084");
            } else {
                item.setTest("-1");
            }
            item.setBd(new BigDecimal(1000.012 + i));
            item.setDate(new Date());
            item.setAbcMap(map);

            map.put("aaa", "A_" + i);
            map.put("bbb", "BBB_" + i);

            list.add(item);
        }

        return data;
    }

    /**
     * 根据模板生成PDF文件
     */
    @Test
    public void exportPdf2024() throws Exception {
        String fileName = "template/pdf/PDF_template_2024.pdf";
        String outPutFileName = "PDF_2024_out.pdf";
        // 1、创建一个PdfWriter对象，将文档写入到文件中
        PdfReader reader = new PdfReader(fileName);
        PdfWriter writer = new PdfWriter(outPutFileName);
        // 2、初始化一个PdfDocument对象
        PdfDocument pdf = new PdfDocument(reader, writer);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
        // 创建字体
        PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        pdf.addFont(font);
        // 数据对象
        TestDocVO data = getData(10);

        /**
         *  循环表单字段
         *   // 根据表单域中字段，自动匹配数据对象中的字段，支持列表、map、嵌套对象
         *   // pdf域字段         数据对象[TestDocVO]字段
         *   // str1             str1
         *   // innerObject.str1 innerObject.str1
         *   // list[0].id       list
         */
        form.getAllFormFields().forEach((item, field) -> {
            field.setFont(font).setFontSize(10);

            Object value = Clazz.getValue(data, item, false);
            if (null != value) {
                log.debug("==> [{}] = {}", item, value.toString());
                if (value instanceof Date) {
                    field.setValue(DateUtils.format((Date) value, DateUtils.HYPHEN_TIME));
                } else {
                    field.setValue(value.toString());
                }
            } else {
                field.setValue("");
            }
        });
        String projectPath = FileUtils.getProjectPath();
        Path txt = Paths.get(projectPath, "src/test/java/nohi/doc/pdf/Text.txt");
        String text = FileUtils.readStringFromPath(txt);

        // 设置文本域
        form.getField("text1").setValue(text);

        // 长文本，自动换行，超过文本域高度的行，被自动隐藏
        form.getField("text3").setValue(text.replaceAll("[\\r|\\n]", "    "));
        form.getField("text4").setValue(text);

        // 设置图片
        PdfButtonFormField imageField = (PdfButtonFormField) form.getField("image1");
        String imFile = "src/test/resources/assert/images/sirref.png";
        imageField.setImage(imFile);

        imageField = (PdfButtonFormField) form.getField("image2");
        imFile = "src/test/resources/assert/images/2.jpeg";
        imageField.setImage(imFile);

        form.flattenFields();
        pdf.close();
    }


    class DataVo2025 {
        private String flowNo;
        private int int1;
        private Integer integer1;
        private String string1;
        private Date date1;
        private double double1;
        private Double double2;
        private BigDecimal big;
        private Map<String, String> map;

        // 内部对象
        private DataVo2025InnerVO innerObject;

        private int[] intArray;
        private Integer[] integerArray;
        private String[] stringArray;
        private double[] db;
        private Double[] doubleArray;
        private BigDecimal[] bigDecimal;
        private Date[] date;
        private String[] code;
    }

    @Data
    class DataVo2025InnerVO {
        private String innerString;
        private int innerInt;
        private Integer innerInteger;


        private int id;
        private Integer integer;
        private String string;
        private double double1;
        private Double double2;
        private BigDecimal bd;
        private Date date;
        private String code;
    }

}
