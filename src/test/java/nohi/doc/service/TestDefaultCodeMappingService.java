package nohi.doc.service;

import lombok.extern.slf4j.Slf4j;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>测试类中自定义spi</p>
 * @date 2024/06/07 09:12
 **/
@Slf4j
public class TestDefaultCodeMappingService implements ICodeMappingService{
    @Override
    public String getCodeValue(String codeType, String codeKey) {
        return getCodeValue(codeType, codeKey, null);
    }

    @Override
    public String getCodeValue(String codeType, String codeKey, String defaultValue) {
        log.debug("codeType:{}, codeKey:{}, defaultValue:{}", codeType, codeKey, defaultValue);
        if ("156".equals(codeKey)) {
            return "元";
        }
        return defaultValue;
    }

    @Override
    public String getCodeKey(String codeType, String codeValue) {
        return getCodeKey(codeType, codeValue, null);
    }

    @Override
    public String getCodeKey(String codeType, String codeValue, String defaultKey) {
        log.debug("codeType:{}, codeKey:{}", codeType, codeValue);
        if ("人民币".equals(codeValue)) {
            return "156元";
        }
        return defaultKey;
    }
}
