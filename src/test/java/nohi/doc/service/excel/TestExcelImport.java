package nohi.doc.service.excel;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nohi.doc.DocConsts;
import nohi.doc.excel.vo.TestDocVO;
import nohi.doc.service.impl.DocService;
import nohi.doc.vo.DocVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
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
public class TestExcelImport {

    /**
     * 导入静态区：
     * 只有一个静态区块
     */
    @Test
    public void importFieldBlock() {
        // 文件路径, 根据项目路径加载
        Path filePath = Paths.get(new File("").getAbsolutePath(), "src/test/resources/importfile/excel/excel_field_01.xls");
        Assertions.assertTrue(filePath.toFile().exists(), "文件不存在");
        Assertions.assertTrue(filePath.toFile().isFile(), "文件是目录");

        // 解析文件
        TestDocVO docVO = new TestDocVO();
        DocVO<TestDocVO> vo = new DocVO<>();
        vo.setDocId("FIELD_01");
        vo.setDocType(DocConsts.DOC_TYPE_EXCEL);

        DocService docService = new DocService();
        docService.importFromFile(docVO, vo, filePath.toFile());

        log.debug("docVO:{}", JSONObject.toJSONString(docVO, true));
    }
}
