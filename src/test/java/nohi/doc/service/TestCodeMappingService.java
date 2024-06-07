package nohi.doc.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nohi.doc.DocConsts;
import nohi.doc.excel.vo.TestDocVO;
import nohi.doc.excel.vo.TestListVO;
import nohi.doc.service.impl.CodeMappingService;
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
 * @description <p>mappingService</p>
 * @date 2024/06/06 14:41
 **/
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("CodeMapping")
public class TestCodeMappingService {


    @Test
    @DisplayName("导入列表区")
    @Order(1)
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
        Assertions.assertEquals("156元", data.getTest());

    }

    /**
     * 导出：
     * 只有一个静态区块
     */
    @Test
    @Order(1)
    @DisplayName("导出静态区")
    public void exportFieldBlock() {
        CodeMappingService service = CodeMappingService.getService();
        Assertions.assertEquals("元", service.getCodeValue("CurrencyCd", "156"));
        Assertions.assertEquals("啥元", service.getCodeValue("CurrencyCd", "111", "啥元"));
        Assertions.assertEquals("无", service.getCodeValue("CurrencyCd", "222", "无"));
        Assertions.assertNull(service.getCodeValue("CurrencyCd", "222"));

        Assertions.assertEquals("156元", service.getCodeKey("CurrencyCd", "人民币"));
        Assertions.assertEquals("111", service.getCodeKey("CurrencyCd", "啥元", "111"));
        Assertions.assertEquals("-1", service.getCodeKey("CurrencyCd", "abc", "-1"));
        Assertions.assertNull(service.getCodeKey("CurrencyCd", "abc"));
    }


}
