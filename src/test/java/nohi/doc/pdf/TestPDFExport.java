package nohi.doc.pdf;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;


@Slf4j
public class TestPDFExport {

    /**
     * 根据模板生成PDF文件
     */
    @Test
    public void exportPdf2024() throws Exception {
        String fileName = "template/pdf/pdf_template.pdf";
        // 1、创建一个PdfWriter对象，将文档写入到文件中
        PdfReader reader = new PdfReader(fileName);
        // 2、初始化一个PdfDocument对象
        PdfDocument pdf = new PdfDocument(reader);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

        form.getAllFormFields().forEach((item, field) -> {
            log.info("Item: {}, field: {}", item, field);
        });
    }

}
