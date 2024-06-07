package nohi.doc.config.xml;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import nohi.doc.config.meta.DocumentMeta;
import nohi.doc.config.xml.code.CodeItemMeta;
import nohi.doc.config.xml.code.CodeTypeMeta;
import nohi.doc.config.xml.code.CodesMeta;
import nohi.doc.service.DefaultCodeMappingService;
import nohi.doc.service.config.XmlParseService;
import nohi.utils.FileUtils;
import nohi.utils.XmlUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>测试xml</p>
 * @date 2024/04/01 10:47
 **/
@Slf4j
public class TestCodeMappingXml {

    @Test
    public void toXml() throws JAXBException {
        CodesMeta codesMeta = new CodesMeta();
        List<CodeTypeMeta> codeCodes = Lists.newArrayList();
        codesMeta.setCodeTypeList(codeCodes);

        for (int i = 0; i <2 ; i++) {
            CodeTypeMeta typeMeta  = new CodeTypeMeta();
            codeCodes.add(typeMeta);
            typeMeta.setTypeName("CODE_TYPE" + i);
            List<CodeItemMeta> codeList = Lists.newArrayList();
            typeMeta.setCodeList(codeList);

            for (int j = 0; j < 2; j++) {
                CodeItemMeta item = new CodeItemMeta();
                codeList.add(item);
                item.setCodeKey(i + "_" + j);
                item.setCodeValue(i*j + "");
            }
        }

        String xml = XmlUtils.convert2Xml(codesMeta);
        log.debug("xml:{}", xml);

        CodesMeta meta = XmlUtils.convertXml2Bean(xml, CodesMeta.class);
        log.debug("meta:{}", JSONObject.toJSONString(meta, true));

    }

    /**
     * 解析文档主配置
     */
    @Test
    public void testNohiEncodeConfXml() throws Exception {
        String filePath = "docconf/nohi_encode_conf.xml";
        try (InputStream inputStream = DefaultCodeMappingService.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new RuntimeException("配置文件不存在");
            }
            // 读取XML
            String xml = FileUtils.readStringFromStream(inputStream);
            log.debug("xml: {}", xml);
            // 解析
            CodesMeta meta = XmlUtils.convertXml2Bean(xml, CodesMeta.class);
            log.debug("meta:{}", JSONObject.toJSONString(meta, true));
        } catch (IOException e) {
            log.error("解析配置文件[{}] 异常:{}", filePath, e.getMessage());
            throw new RuntimeException("解析配置文件异常" + e.getMessage());
        }
    }
}
