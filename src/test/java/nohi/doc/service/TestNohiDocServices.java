package nohi.doc.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nohi.doc.config.meta.DocumentMeta;
import nohi.doc.config.xml.NohiDocMeta;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>测试文档配置服务</p>
 * @date 2024/04/03 11:22
 **/
@Slf4j
public class TestNohiDocServices {

    /**
     * 解析默认配置文件
     */
    @Test
    public void parseNohiDocDefault() {
        NohiDocMeta mainDoc = NohiDocServices.getMainDoc();
        log.debug("返回内容：{}", JSONObject.toJSONString(mainDoc));

        Assertions.assertNotNull(mainDoc);
        Assertions.assertNotNull(mainDoc.getExcel());
        Assertions.assertNotNull(mainDoc.getPdf());
    }

    /**
     * 解析指定配置文件
     */
    @Test
    public void parseNohiDocByConfigPath() {
        NohiDocMeta mainDoc = NohiDocServices.initMainDoc("nohi-doc.xml");
        Assertions.assertNotNull(mainDoc);
        Assertions.assertNotNull(mainDoc.getExcel());
        Assertions.assertNotNull(mainDoc.getPdf());
    }

    /**
     * 测试配置文件不存在，抛出异常，异常信息为：配置文件不存在
     */
    @Test
    public void parseNohiDocByConfigPathFail() {
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            NohiDocServices.initMainDoc("nohi-doc1.xml");
        });
        log.info("assertThrows通过后，返回的异常实例：{}", exception.getMessage());
        Assertions.assertTrue(exception.getMessage().contains("配置文件不存在"));
    }

    /**
     * 解析默认配置文件
     */
    @Test
    public void getExcelDocument() {
        NohiDocMeta mainDoc = NohiDocServices.getMainDoc();
        Assertions.assertNotNull(mainDoc);
        Assertions.assertNotNull(mainDoc.getExcel());
        Assertions.assertNotNull(mainDoc.getPdf());

        String docId = "TEST";
        DocumentMeta documentMeta = NohiDocServices.getDocumentByDocId(docId);
        log.debug("documentMeta:{}", JSONObject.toJSONString(documentMeta));
        Assertions.assertNotNull(documentMeta);
        Assertions.assertNotNull(documentMeta.getSheetList());
        Assertions.assertEquals(docId, documentMeta.getId());

        // TODO PDF解析暂未实现
//        docId = "pdf_template";
//        documentMeta = NohiDocServices.getDocumentByDocId(docId);
//        log.debug("documentMeta:{}", JSONObject.toJSONString(documentMeta));
//        Assertions.assertNotNull(documentMeta);
//        Assertions.assertNotNull(documentMeta.getSheetList());
//        Assertions.assertEquals(docId, documentMeta.getId());
    }

    /**
     * 测试获取文档ID失败
     */
    @Test
    public void getExcelDocumentFail() {
        NohiDocMeta mainDoc = NohiDocServices.getMainDoc();
        Assertions.assertNotNull(mainDoc);
        Assertions.assertNotNull(mainDoc.getExcel());
        Assertions.assertNotNull(mainDoc.getPdf());

        String docId = "NOTHING";
        DocumentMeta documentMeta = NohiDocServices.getDocumentByDocId(docId);
        log.debug("documentMeta:{}", null == documentMeta ? "null" : JSONObject.toJSONString(documentMeta));
        Assertions.assertNull(documentMeta);
    }
}
