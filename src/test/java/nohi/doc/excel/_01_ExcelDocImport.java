package nohi.doc.excel;

import nohi.doc.excel.vo.TestDocVO;
import nohi.doc.excel.vo.TestListVO;
import nohi.doc.service.IDocService;
import nohi.doc.service.impl.DocService;
import nohi.doc.vo.DocVO;
import nohi.utils.BasicValue;
import org.junit.jupiter.api.Test;

import java.io.File;


/**
 * 测试Excel文件导入
 *
 * @author NOHI
 * @crated date 2013-2-26
 */
public class _01_ExcelDocImport {

    /**
     * 从文件导入
     *
     * @throws Exception
     */
    @Test
    public void importFromFile() throws Exception {
        IDocService docService = new DocService();

        String filePath = "D:/workspace/nohi-doc/test/nohi/doc/excel/excel_test_template.xls";
        filePath = "src/test/java/nohi/doc/excel/excel_test_template.xls";

        DocVO doc = new DocVO();
        doc.setDocId("TEST_IMPORT");
        doc.setDocType("EXCEL");
        File file = new File(filePath);
        TestDocVO vo = new TestDocVO();
        docService.importFromFile(vo, doc, file);

        System.out.println("aa: " + BasicValue.toString(vo));
        if (null != vo) {
            System.out.println("bb: " + BasicValue.toString(vo.getInnerObject()));
            System.out.println("list ================== : " + BasicValue.toString(vo.getList()));
            for (TestListVO t : vo.getList()) {
                System.out.println("t:" + BasicValue.toString(t));
            }
        }
    }

}
