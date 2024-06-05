package nohi.doc.utils;

import lombok.extern.slf4j.Slf4j;
import nohi.utils.DateUtils;
import nohi.utils.StringFormat;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>excelUtils</p>
 * @date 2024/04/15 15:55
 **/
@Slf4j
public class TestExcelUtils {

    @Test
    public void getCellValue() throws IOException {
        String filePath = "src/test/resources/importfile/excel/excel_col_field_test.xls";
        InputStream fis = Files.newInputStream(Paths.get(filePath));
        Workbook hwb = WorkbookFactory.create(fis);
        log.info("hwb:" + hwb);

        Sheet sheet = hwb.getSheetAt(0);
        int rows = sheet.getLastRowNum();
        // 循环遍历行
        for (int i = 2; i <= rows; i++) {
            Row row = sheet.getRow(i);
            int cellNum = row.getLastCellNum();

            StringBuilder cellTypeString = new StringBuilder();
            StringBuilder cellValue = new StringBuilder();
            // 循环每一列
            for (int j = 0; j < cellNum; j++) {
                Cell cell = row.getCell(j);
                cellTypeString.append(rightPad(null == cell ? "-" : cell.getCellType().toString()));
                if (cell.getCellType().equals(CellType.NUMERIC) && DateUtil.isCellDateFormatted( cell )) {
                    Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
                    cellValue.append(rightPad(DateUtils.format(date)));
                } else {
                    cellValue.append(rightPad(null == cell ? "-" : ExcelUtils.getCellValue(cell)));
                }

            }
            System.out.println(cellTypeString.toString());
            System.out.println(cellValue.toString());
        }
    }

    public String rightPad(String str) {
        int length = str.getBytes().length - str.length();
        if (length > 0) {
            length = str.length() / 2;
        } else {
            length = 0;
        }
        // return StringUtils.rightPad(str, 20 - length, ' ');
        int formatLength = StringFormat.formatLength(str, 20);
        return String.format("%-" + formatLength + "s", str);
    }

    @Test
    public void date() throws IOException {
        String filePath = "src/test/resources/importfile/excel/excel_col_field_test.xls";
        InputStream fis = Files.newInputStream(Paths.get(filePath));
        Workbook hwb = WorkbookFactory.create(fis);
        log.info("hwb:" + hwb);

        Sheet sheet = hwb.getSheetAt(0);
        Row row = sheet.getRow(6);
        int cellNum = row.getLastCellNum();
        for (int j = 1; j < cellNum; j++) {
            Cell cell = row.getCell(j);
            if (cell != null) {
                if (DateUtil.isCellDateFormatted( cell )) {
                    LocalDateTime dateTime = cell.getLocalDateTimeCellValue();
                    System.out.println( cell.getCellType() + " " + (null != dateTime ? dateTime.toString() : ""));
                };
            }
        }

        System.out.println("==============");
        row = sheet.getRow(7);
        cellNum = row.getLastCellNum();
        for (int j = 1; j < cellNum; j++) {
            Cell cell = row.getCell(j);
            if (cell != null) {
                if (DateUtil.isCellDateFormatted( cell )) {
                    String date = DateUtil.getJavaDate(cell.getNumericCellValue()).toString();
                    System.out.println( cell.getCellType() + " " + date);
                };

            }
        }
    }
}
