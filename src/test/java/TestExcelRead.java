import nohi.doc.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author NOHI
 * @program: nohi-doc
 * @description:
 * @create 2019-11-13 13:42
 **/
public class TestExcelRead {

    /**
     * 测试
     */
    @Test
    public void testRead() {
        String filePath = "/Users/nohi/Downloads/bbb.xlsx";
        System.out.println("filePath:" + filePath);

        InputStream is = null;
        Workbook templatebook = null;//模板文件
        Sheet templateSheet = null; //模板文件Sheet
        Row row = null;
        try {
            is = new FileInputStream(new File(filePath));
            templatebook = WorkbookFactory.create(is);
            int sheets = templatebook.getNumberOfSheets();
            System.out.println("sheet.size:" + sheets);
            templateSheet = templatebook.getSheetAt(0);
            int rowNum = templateSheet.getLastRowNum();
            System.out.println("行数:" + rowNum);
            for (int i = 0 ; i < rowNum && i < 10; i ++ ) {
                row = templateSheet.getRow(i);
                if (null == row) {
                    System.out.println("行:" + i + " 为空");
                    continue;
                }
                int celNum = row.getLastCellNum();
                System.out.println("行:" + i + "列数:" + celNum);
                for (int j = 0; j < celNum; j++) {
//                    String value = row.getCell(j).getStringCellValue();
                    String value = ExcelUtils.getCellValue(row.getCell(j));//取值
                    System.out.println("行:" + i + "列" + j + ",====value:" + value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
