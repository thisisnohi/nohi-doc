package nohi.doc.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import nohi.doc.DocConsts;
import nohi.doc.config.xml.code.CodeItemMeta;
import nohi.doc.config.xml.code.CodeTypeMeta;
import nohi.doc.config.xml.code.CodesMeta;
import nohi.utils.FileUtils;
import nohi.utils.XmlUtils;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


/**
 * 读取Message Encode 文件，取得代码映射关系
 *
 * @author NOHI
 * @date 2012-11-13
 */
@Slf4j
public class DefaultCodeMappingService implements ICodeMappingService {
    static String filePath = DocConsts.encodeConf;

    private static Map<String, Map<String, String>> codeTypeMap;

    public static Map<String, Map<String, String>> getCodeTypeMap() {
        if (null == codeTypeMap) {
            initCodeTypeMap();
        }
        return codeTypeMap;
    }

    public static void initCodeTypeMap() {
        readXML();
    }

    static {
        initCodeTypeMap();
    }

    /**
     * 读取文件
     */
    public static void readXML() {
        if (null == filePath) {
            filePath = DocConsts.defaultConf;
        }

        try (InputStream inputStream = DefaultCodeMappingService.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new RuntimeException("配置文件不存在");
            }
            // 读取XML
            String xml = FileUtils.readStringFromStream(inputStream);
            log.debug("xml: {}", xml);
            // 解析
            CodesMeta meta = XmlUtils.convertXml2Bean(xml, CodesMeta.class);
            log.debug("meta:{}", JSONObject.toJSONString(meta));

            // 转换
            codeTypeMap = Maps.newHashMap();
            for (CodeTypeMeta codeType : meta.getCodeTypeList()) {
                Map<String, String> codeMapping = Maps.newHashMap();
                codeTypeMap.put(codeType.getTypeName(), codeMapping);
                for (CodeItemMeta code : codeType.getCodeList()) {
                    codeMapping.put(code.getCodeKey(), code.getCodeValue());
                }
            }

        } catch (IOException | JAXBException e) {
            log.error("解析配置文件[{}] 异常:{}", filePath, e.getMessage());
            throw new RuntimeException("解析配置文件异常" + e.getMessage());
        }
    }


    @Override
    public String getCodeValue(String codeType, String codeKey) {
        return getCodeValue(codeType, codeKey, null);
    }

    @Override
    public String getCodeValue(String codeType, String codeKey, String defaultValue) {
        Map<String, String> codeMapping =  codeTypeMap.get(codeType);
        if (null != codeMapping && codeMapping.containsKey(codeKey)) {
            return codeMapping.get(codeKey);
        }
        return defaultValue;
    }

    @Override
    public String getCodeKey(String codeType, String codeValue) {
        return getCodeKey(codeType,codeValue, null);
    }

    @Override
    public String getCodeKey(String codeType, String codeValue, String defaultKey) {
        Map<String, String> codeMapping =  codeTypeMap.get(codeType);
        if (null != codeMapping && codeMapping.containsValue(codeValue)) {
            for (Map.Entry<String, String> entry : codeMapping.entrySet()) {
                if  (entry.getValue().equals(codeValue)) {
                    return entry.getKey();
                }
            }
        }
        return defaultKey;
    }
}
