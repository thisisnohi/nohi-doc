package nohi.doc.config.xml;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nohi.doc.config.meta.DocumentMeta;
import nohi.doc.service.config.XmlParseService;
import nohi.utils.FileUtils;
import nohi.utils.XmlUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>测试xml</p>
 * @date 2024/04/01 10:47
 **/
@Slf4j
public class TestXml {

//    @BeforeAll
//    public static void init() {
//        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
//        Logger logger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
//        logger.setLevel(Level.DEBUG);
//    }

    @Test
    public void log() {
        log.warn("====warn====");
        log.info("====info====");
        log.debug("====debug====");
        log.error("====error====");
    }

    /**
     * 解析文档主配置
     */
    @Test
    public void testNohiDocXml() throws Exception {
        // 读取主配置
        String projectPath = FileUtils.getProjectPath();
        Path xmlPath = Paths.get(projectPath, "src/test/resources/nohi-doc.xml");
        String xml = FileUtils.readStringFromPath(xmlPath);
        log.debug("xml: {}", xml);
        // 解析
        NohiDocMeta docMeta = XmlUtils.convertXml2Bean(xml, NohiDocMeta.class);
        log.debug("docMeta: {}", JSONObject.toJSONString(docMeta));
        // 验证
        Assertions.assertNotNull(docMeta);
        Assertions.assertNotNull(docMeta.getExcel());
        Assertions.assertNotNull(docMeta.getPdf());
        Assertions.assertFalse(docMeta.getExcel().isEmpty());
        Assertions.assertFalse(docMeta.getPdf().isEmpty());
    }

    /**
     * 解析Excel配置
     */
    @Test
    public void testExcelXml() throws Exception {
        // 读取文档模板配置
        String resourcePath = "docconf/excel/excel_docconf_template.xml";
        // 解析
        Map<String, DocumentMeta> documentMap = XmlParseService.parseTemplateConf(resourcePath);
        log.debug("documentMap: {}", JSONObject.toJSONString(documentMap));
        // 验证
        Assertions.assertNotNull(documentMap);
        Assertions.assertNotNull(documentMap.values());
    }
}
