package nohi.test.excel;


import nohi.test.excel.handle.SheetHandler;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>大量数据读取</p>
 * @date 2024/07/15 21:23
 **/
public class TestHugeDataRead {

    @Test
    @DisplayName("用户模式读取大量数据")
    public void readMillionExcelData() throws Exception {
        String filePath = "src/test/resources/importfile/excel/bigdata/millionData_1.xlsx";
        filePath = "src/test/resources/importfile/excel/bigdata/millionData_1.xlsx";
        InputStream fis = Files.newInputStream(Paths.get(filePath));

        // 1.根据excel报表获取基于事件模型的 OPCPackage对象 其实就是把这个Excel文件以压缩包的形式打开成xml文件 PackageAccess.READ 只读
        OPCPackage opcPackage = OPCPackage.open(fis);
        // 2.创建XSSFReader
        XSSFReader reader = new XSSFReader(opcPackage);
        // 3.获取SharedStringTable对象
        SharedStrings table = reader.getSharedStringsTable();
        // 4.获取styleTable对象
        StylesTable stylesTable = reader.getStylesTable();
        // 5.创建Sax的xmlReader对象
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        // 6.注册自定义的事件处理器
        XSSFSheetXMLHandler xmlHandler = new XSSFSheetXMLHandler(stylesTable, table, new SheetHandler(), false);
        xmlReader.setContentHandler(xmlHandler);
        // 7.逐行读取 得到的是所有的 sheet
        XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) reader.getSheetsData();

        while (sheetIterator.hasNext()) {
            InputStream stream = sheetIterator.next(); //每一个sheet的流数据
            InputSource is = new InputSource(stream);  //每一个sheet对象
            xmlReader.parse(is);
        }
    }

    @Test
    @DisplayName("事件模型读取大量数据")
    public void readMillionExcelDataWithEventModel() throws Exception {
        String filePath = "src/test/resources/importfile/excel/bigdata/millionData_1.xlsx";
        filePath = "src/test/resources/importfile/excel/bigdata/millionData_2.xlsx";

        // 1.根据excel报表获取基于事件模型的 OPCPackage对象 其实就是把这个Excel文件以压缩包的形式打开成xml文件 PackageAccess.READ 只读
        OPCPackage opcPackage = OPCPackage.open(Paths.get(filePath).toFile(), PackageAccess.READ);
        // 2.创建XSSFReader
        XSSFReader reader = new XSSFReader(opcPackage);
        // 3.获取SharedStringTable对象
        SharedStrings table = reader.getSharedStringsTable();
        // 4.获取styleTable对象
        StylesTable stylesTable = reader.getStylesTable();
        // 5.创建Sax的xmlReader对象
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        // 6.注册自定义的事件处理器
        XSSFSheetXMLHandler xmlHandler = new XSSFSheetXMLHandler(stylesTable, table, new SheetHandler(), false);
        xmlReader.setContentHandler(xmlHandler);
        // 7.逐行读取 得到的是所有的 sheet
        XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) reader.getSheetsData();
        while (sheetIterator.hasNext()) {
            InputStream stream = sheetIterator.next(); //每一个sheet的流数据
            InputSource is = new InputSource(stream);  //每一个sheet对象
            xmlReader.parse(is);
        }
    }
}
