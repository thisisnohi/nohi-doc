package nohi.doc.service.excel;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nohi.doc.DocConsts;
import nohi.doc.excel.vo.TestDocVO;
import nohi.doc.excel.vo.TestListVO;
import nohi.doc.service.impl.DocService;
import nohi.doc.vo.DocVO;
import nohi.utils.DateUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>excel导入</p>
 * @date 2024/04/12 18:10
 **/
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Excel导入测试")
public class TestExcelImport {

    /**
     * 导入静态区：
     * 只有一个静态区块
     */
    @Test
    @Order(1)
    @DisplayName("导入静态区")
    public void importFieldBlock() {
        // 文件路径, 根据项目路径加载
        Path filePath = Paths.get(new File("").getAbsolutePath(), "src/test/resources/importfile/excel/excel_静态块.xls");
        Assertions.assertTrue(filePath.toFile().exists(), "文件不存在");
        Assertions.assertTrue(filePath.toFile().isFile(), "文件是目录");

        // 解析文件
        TestDocVO docVO = new TestDocVO();
        DocVO<TestDocVO> vo = new DocVO<>();
        vo.setDocId("STATIC_FIELD_BLOCK");
        vo.setDocType(DocConsts.DOC_TYPE_EXCEL);

        DocService docService = new DocService();
        docService.importFromFile(docVO, vo, filePath.toFile());

        log.debug("docVO:{}", JSONObject.toJSONString(docVO, true));

        Assertions.assertEquals("字符串", docVO.getStr1());
        Assertions.assertEquals("1111", docVO.getStr2());
        Assertions.assertEquals(123, docVO.getIntV1());
        Assertions.assertEquals(1234567890, docVO.getIntV2());
        Assertions.assertTrue(docVO.getDouble1() - 12345678.1234567 < 0.01);
        Assertions.assertTrue(docVO.getDouble2() - 12345678.1234567 < 0.01);
        Assertions.assertTrue(docVO.getFloatV1() - 0.01 < 0.01);
        Assertions.assertTrue(docVO.getFloatV2() - 123 < 0.01);
        Assertions.assertEquals("2024-04-15", DateUtils.format(docVO.getDate1()));
        Assertions.assertEquals("2024-04-15", DateUtils.format(docVO.getDate2()));

        // 嵌套对象
        Assertions.assertEquals("内部对象字符串,AAA", docVO.getInnerObject().getStr1());
        Assertions.assertEquals("222", docVO.getInnerObject().getStr2());
        Assertions.assertEquals(123, docVO.getInnerObject().getIntV1());
        Assertions.assertEquals(1234567890, docVO.getInnerObject().getIntV2());
        Assertions.assertTrue(docVO.getInnerObject().getDouble1() - 12345678.1234567 < 0.01);
        Assertions.assertTrue(docVO.getInnerObject().getDouble2() - 12345678.1234567 < 0.01);
        Assertions.assertTrue(docVO.getInnerObject().getFloatV1() - 0.01 < 0.01);
        Assertions.assertTrue(docVO.getInnerObject().getFloatV2() - 123 < 0.01);
        Assertions.assertEquals("2024-04-15", DateUtils.format(docVO.getInnerObject().getDate1()));
        Assertions.assertEquals("2024-04-15", DateUtils.format(docVO.getInnerObject().getDate2()));
    }

    /**
     * 导入列表区
     * 只有一个列表区
     */
    @Test
    @DisplayName("导入列表区")
    @Order(2)
    public void importTableBlock() {
        // 文件路径, 根据项目路径加载
        Path filePath = Paths.get(new File("").getAbsolutePath(), "src/test/resources/importfile/excel/excel_列表区.xls");
        Assertions.assertTrue(filePath.toFile().exists(), "文件不存在");
        Assertions.assertTrue(filePath.toFile().isFile(), "文件是目录");

        // 解析文件
        TestDocVO docVO = new TestDocVO();
        DocVO<TestDocVO> vo = new DocVO<>();
        vo.setDocId("TABLE_BLOCK");
        vo.setDocType(DocConsts.DOC_TYPE_EXCEL);

        DocService docService = new DocService();
        docService.importFromFile(docVO, vo, filePath.toFile());

        log.debug("docVO:{}", JSONObject.toJSONString(docVO, true));

        Assertions.assertEquals(5, docVO.getList().size(), "列表size不相等");

        TestListVO data = docVO.getList().get(0);
        Assertions.assertEquals(1, data.getId());
        Assertions.assertEquals("张三", data.getName());
        Assertions.assertEquals(18, data.getAge());
        Assertions.assertEquals("2024-01-01", DateUtils.format(data.getDate()));
        Assertions.assertTrue(data.getAmt() - 10000.12 < 0.01);
        Assertions.assertEquals(new BigDecimal("123456789.12"), data.getBd());
        Assertions.assertEquals("人民币", data.getTest());

    }

    /**
     * 导入多块：静态块+列表块
     */
    @Test
    @DisplayName("导入多块：静态块+列表块")
    @Order(3)
    public void importMultiBlock() {
        // 文件路径, 根据项目路径加载
        Path filePath = Paths.get(new File("").getAbsolutePath(), "src/test/resources/importfile/excel/excel_多块.xlsx");
        Assertions.assertTrue(filePath.toFile().exists(), "文件不存在");
        Assertions.assertTrue(filePath.toFile().isFile(), "文件是目录");

        // 解析文件
        TestDocVO docVO = new TestDocVO();
        DocVO<TestDocVO> vo = new DocVO<>();
        vo.setDocId("MULTI_BLOCK");
        vo.setDocType(DocConsts.DOC_TYPE_EXCEL);

        DocService docService = new DocService();
        docService.importFromFile(docVO, vo, filePath.toFile());

        log.debug("docVO:{}", JSONObject.toJSONString(docVO, true));


        Assertions.assertEquals("字符串", docVO.getStr1());
        Assertions.assertEquals("1111", docVO.getStr2());
        Assertions.assertEquals(123, docVO.getIntV1());
        Assertions.assertEquals(1234567890, docVO.getIntV2());
        Assertions.assertTrue(docVO.getDouble1() - 12345678.1234567 < 0.01);
        Assertions.assertTrue(docVO.getDouble2() - 12345678.1234567 < 0.01);
        Assertions.assertTrue(docVO.getFloatV1() - 0.01 < 0.01);
        Assertions.assertTrue(docVO.getFloatV2() - 123 < 0.01);
        Assertions.assertEquals("2024-04-15", DateUtils.format(docVO.getDate1()));
        Assertions.assertEquals("2024-04-15", DateUtils.format(docVO.getDate2()));

        // 嵌套对象
        Assertions.assertEquals("内部对象字符串,AAA", docVO.getInnerObject().getStr1());
        Assertions.assertEquals("222", docVO.getInnerObject().getStr2());
        Assertions.assertEquals(123, docVO.getInnerObject().getIntV1());
        Assertions.assertEquals(1234567890, docVO.getInnerObject().getIntV2());
        Assertions.assertTrue(docVO.getInnerObject().getDouble1() - 12345678.1234567 < 0.01);
        Assertions.assertTrue(docVO.getInnerObject().getDouble2() - 12345678.1234567 < 0.01);
        Assertions.assertTrue(docVO.getInnerObject().getFloatV1() - 0.01 < 0.01);
        Assertions.assertTrue(docVO.getInnerObject().getFloatV2() - 123 < 0.01);
        Assertions.assertEquals("2024-04-15", DateUtils.format(docVO.getInnerObject().getDate1()));
        Assertions.assertEquals("2024-04-15", DateUtils.format(docVO.getInnerObject().getDate2()));


        Assertions.assertEquals(5, docVO.getList().size(), "列表size不相等");

        TestListVO data = docVO.getList().get(0);
        Assertions.assertEquals(1, data.getId());
        Assertions.assertEquals("张三", data.getName());
        Assertions.assertEquals(18, data.getAge());
        Assertions.assertEquals("2024-01-01", DateUtils.format(data.getDate()));
        Assertions.assertTrue(data.getAmt() - 10000.12 < 0.01);
        Assertions.assertEquals(new BigDecimal("123456789.12"), data.getBd());
        Assertions.assertEquals("人民币", data.getTest());


    }

    /**
     * 导入多sheet页
     */
    @Test
    @DisplayName("导入多sheet页")
    @Order(4)
    public void importMultiSheet() {
        // 文件路径, 根据项目路径加载
        Path filePath = Paths.get(new File("").getAbsolutePath(), "src/test/resources/importfile/excel/excel_多sheet.xlsx");
        Assertions.assertTrue(filePath.toFile().exists(), "文件不存在");
        Assertions.assertTrue(filePath.toFile().isFile(), "文件是目录");

        // 解析文件
        TestDocVO docVO = new TestDocVO();
        DocVO<TestDocVO> vo = new DocVO<>();
        vo.setDocId("MULTI_SHEET");
        vo.setDocType(DocConsts.DOC_TYPE_EXCEL);

        DocService docService = new DocService();
        docService.importFromFile(docVO, vo, filePath.toFile());

        log.debug("docVO:{}", JSONObject.toJSONString(docVO, true));


        Assertions.assertEquals("字符串", docVO.getStr1());
        Assertions.assertEquals("1111", docVO.getStr2());
        Assertions.assertEquals(123, docVO.getIntV1());
        Assertions.assertEquals(1234567890, docVO.getIntV2());
        Assertions.assertTrue(docVO.getDouble1() - 12345678.1234567 < 0.01);
        Assertions.assertTrue(docVO.getDouble2() - 12345678.1234567 < 0.01);
        Assertions.assertTrue(docVO.getFloatV1() - 0.01 < 0.01);
        Assertions.assertTrue(docVO.getFloatV2() - 123 < 0.01);
        Assertions.assertEquals("2024-04-15", DateUtils.format(docVO.getDate1()));
        Assertions.assertEquals("2024-04-15", DateUtils.format(docVO.getDate2()));

        // 嵌套对象
        Assertions.assertEquals("内部对象字符串,AAA", docVO.getInnerObject().getStr1());
        Assertions.assertEquals("222", docVO.getInnerObject().getStr2());
        Assertions.assertEquals(123, docVO.getInnerObject().getIntV1());
        Assertions.assertEquals(1234567890, docVO.getInnerObject().getIntV2());
        Assertions.assertTrue(docVO.getInnerObject().getDouble1() - 12345678.1234567 < 0.01);
        Assertions.assertTrue(docVO.getInnerObject().getDouble2() - 12345678.1234567 < 0.01);
        Assertions.assertTrue(docVO.getInnerObject().getFloatV1() - 0.01 < 0.01);
        Assertions.assertTrue(docVO.getInnerObject().getFloatV2() - 123 < 0.01);
        Assertions.assertEquals("2024-04-15", DateUtils.format(docVO.getInnerObject().getDate1()));
        Assertions.assertEquals("2024-04-15", DateUtils.format(docVO.getInnerObject().getDate2()));


        Assertions.assertEquals(5, docVO.getList().size(), "列表size不相等");

        TestListVO data = docVO.getList().get(0);
        Assertions.assertEquals(1, data.getId());
        Assertions.assertEquals("张三", data.getName());
        Assertions.assertEquals(18, data.getAge());
        Assertions.assertEquals("2024-01-01", DateUtils.format(data.getDate()));
        Assertions.assertTrue(data.getAmt() - 10000.12 < 0.01);
        Assertions.assertEquals(new BigDecimal("123456789.12"), data.getBd());
        Assertions.assertEquals("人民币", data.getTest());
    }
}
