package nohi.test.pdf;


import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.attach.impl.layout.HtmlPageBreak;
import com.itextpdf.html2pdf.css.apply.impl.DefaultCssApplierFactory;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.media.MediaType;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>PDF创建</p>
 * @date 2025/03/07 22:09
 **/
@Slf4j
public class TestHtml2Pdf {


    @Test
    public void testHtml2Pdf() throws IOException {
        // 1、Creating a PdfWriter
        String dest = "html_to_pdf.pdf";
        PdfWriter writer = new PdfWriter(dest);
        // 2、Creating a PdfDocument
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf, PageSize.A4.rotate(), false);

        // 将HTML内容添加到文档
        String html = "abc这是一个标题<p>这是一个段落</p>";

        ConverterProperties props = new ConverterProperties();
        // props.setCharset("UFT-8"); 编码
        FontProvider fp = new FontProvider();

        // .ttf 字体所在目录
//        String resources = this.getClass().getResource(FONT_RESOURCE_DIR).getPath();
//        fp.addDirectory(resources);
        FontProgram fp1 = FontProgramFactory.createFont("ttf/zhanku_cangeryuyangti-W01.ttf");
        fp.addFont(fp1);
        fp.addStandardPdfFonts();
        props.setFontProvider(fp);
        // html中使用的图片等资源目录（图片也可以直接用url或者base64格式而不放到资源里）
        // props.setBaseUri(resources);
        List<IElement> elements = HtmlConverter.convertToElements(html, props);
        for (IElement element : elements) {
            log.info(element.toString());
            // 分页符
            if (element instanceof HtmlPageBreak) {
                document.add((HtmlPageBreak) element);
                //普通块级元素
            } else {
                document.add((IBlockElement) element);
            }
        }
        document.close();
    }

    /**
     * 使用Freemarker引擎加载HTML模板文件并填充变量值，并将HTML字符串转换为PDF文件
     *
     * @param data 模板要填充的数据
     * @return
     * @throws Exception
     */
    public String generatePDF(Map<String, Object> data, String templateDir, String templateName, String pdfPath, String fileName) throws Exception {
        // 使用Freemarker引擎加载HTML模板文件并填充变量值
        TemplateLoader templateLoader = new FileTemplateLoader(Paths.get(templateDir).toFile());
        Configuration cfg = new Configuration(Configuration.getVersion());
        cfg.setTemplateLoader(templateLoader);
        Template template = cfg.getTemplate(templateName, "UTF-8");
        StringWriter out = new StringWriter();
        template.process(data, out);
        out.flush();
        String htmlContent = out.toString();
        return convertHtmlToPdf(htmlContent, pdfPath, fileName);
    }

    /**
     * 使用iText 7将HTML字符串转换为PDF文件，并返回PDF文件的二进制数据
     *
     * @param htmlString 待转换的HTML字符串
     * @return 返回生成的PDF文件内容
     * @throws IOException
     */
    private static String convertHtmlToPdf(String htmlString, String path, String fileName) throws IOException {
        File compressedImageFile = new File(path, fileName);
        OutputStream os = new FileOutputStream(compressedImageFile);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, new PageSize(600.0F, 1150.0F));
        // 设置左、右、上、下四个边距的值，以点（pt）为单位
        document.setMargins(0, 0, 0, 0);
        // 设置中文字体
        FontProvider fontProvider = new DefaultFontProvider(false, false, false);
        //添加自定义字体，例如微软雅黑
        PdfFont microsoft = PdfFontFactory.createFont(FontProgramFactory.createFont("/Users/nohi/work/workspaces-nohi/nohi-doc/src/main/resources/ttf/zhanku_kuaileti.ttf"));
        fontProvider.addFont(microsoft.getFontProgram(), PdfEncodings.IDENTITY_H);
        PdfFont microsoft1 = PdfFontFactory.createFont(FontProgramFactory.createFont("/Users/nohi/work/workspaces-nohi/nohi-doc/src/main/resources/ttf/zhanku_cangeryuyangti-W01.ttf"));
        fontProvider.addFont(microsoft1.getFontProgram(), PdfEncodings.IDENTITY_H);

        ConverterProperties converterProps = new ConverterProperties();
        converterProps.setFontProvider(fontProvider);
        // 调用HtmlConverter类的convertToPdf函数，将HTML字符串转换为PDF文件
        converterProps.setMediaDeviceDescription(new MediaDeviceDescription(MediaType.PRINT));
        converterProps.setCssApplierFactory(new DefaultCssApplierFactory());
        HtmlConverter.convertToPdf(htmlString, pdf, converterProps);
        pdf.close();
        // 将PDF文件转换为字节数组并返回
        outputStream.writeTo(os);
        return compressedImageFile.getPath();
    }

    @Test
    public void testHtml2Pdf2() throws Exception {
        Map map = new HashMap();
        map.put("name", "太白金星");
        map.put("gender", "男");
        map.put("nationality", "仙族");
        map.put("address", "天庭");
        map.put("td_3_month_loan_dw", "0");
        map.put("bairong_network_time_dw", "[24,+)");
        map.put("fahai_ajlc_count", "0");
        map.put("fahai_fygg_count", "0");
        map.put("fahai_ktgg_count", "0");
        map.put("fahai_zxgg_count", "0");
        map.put("fahai_cpws_count", "0");
        map.put("pboc_edu_info_dw", "武神");
        map.put("arp_overdue_055_interval", "(3，5]");
        map.put("arp_account_028_interval", "0");
        map.put("arp_account_018_interval", "--");
        map.put("arp_account_012_interval", "(0，30000]");
        map.put("arp_overdue_105_interval", "＞20");
        map.put("arp_base_006", "2024-01-17 17:18");
        map.put("apply_con_approve_status_name_dw", "建议拒绝");
        map.put("pre_pboc_overdue_max_month_dw", "(5,7]");
        map.put("pboc_overdue_max_amount_dw", "(20000，40000]");
        map.put("apply_con_reject_reason_sales_dw", "拒绝：");
        map.put("pboc_partner_name", "占几个");
        map.put("apply_con_result_hf", "--");
        map.put("score_level_up", "B");
        map.put("pre_apply_con_cert_name", "李长庚");
        map.put("pre_pboc_belonger_marital_status", "已婚");
        map.put("pre_cert_no", "156956265498563654");
        map.put("pboc_phone_no_lastest", "17765571433");
        map.put("pboc_partner_phone", "15965395626");
        map.put("pboc_partner_cert_no", "163956956485236542");
        map.put("mobile_phone_operator", "移动");
        map.put("pboc_house_loan_exists", "否");
        map.put("pboc_phone_compare", "一致");
        map.put("pboc_monthly_pboc_repayments_interval", "(30000，50000]");
        map.put("pre_phone", "13890786915");
        map.put("bairong_bankfour_res", "一致");
        map.put("pboc_car_loan_exists", "是");
        map.put("pboc_total_credit_line_credit_card_interval", "(0，30000]");
        map.put("pboc_utilization_rate_used_quota_interval", "0");
        map.put("pboc_query_count_1_month_interval", "0");
        map.put("pboc_query_count_3_months_interval", "0");
        map.put("create_time", "2024/1/18 15:12");
        map.put("update_time", "12:58.3");
        map.put("create_user", "0");
        map.put("update_user", "0");
        map.put("sync_time", "2024/1/18 15:12");

        generatePDF(map, "src/test/resources/template/pdf", "html2pdf_tempate.html", ".", "html2pdf_tempate.pdf");
    }

}
