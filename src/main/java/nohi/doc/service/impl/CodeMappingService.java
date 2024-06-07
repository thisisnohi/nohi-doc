package nohi.doc.service.impl;

import lombok.extern.slf4j.Slf4j;
import nohi.doc.service.ICodeMappingService;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>码值映射</p>
 * @date 2024/06/06 14:19
 **/
@Slf4j
public class CodeMappingService implements ICodeMappingService {
    private static final CodeMappingService SERVICE = new CodeMappingService();
    /**
     * 转换服务
     */
    private final ICodeMappingService mappingService;
    /**
     * 所有转换类
     */
    private final List<ICodeMappingService> mappingServiceList;

    private CodeMappingService() {
        ServiceLoader<ICodeMappingService> loader = ServiceLoader.load(ICodeMappingService.class);
        List<ICodeMappingService> list = new ArrayList<>();
        for (ICodeMappingService log : loader) {
            list.add(log);
        }
        // 所有 ServiceProvider
        mappingServiceList = list;
        if (!list.isEmpty()) {
            // 只取一个
            mappingService = list.get(0);
        } else {
            log.warn("没有ICodeMappingService对应配置");
            mappingService = null;
        }
    }

    public static CodeMappingService getService() {
        return SERVICE;
    }

    @Override
    public String getCodeValue(String codeType, String codeKey) {
        return this.getCodeValue(codeType, codeKey, null);
    }

    @Override
    public String getCodeValue(String codeType, String codeKey, String defaultValue) {
        if (mappingService == null) {
            log.warn("没有发现ICodeMappingService服务提供者");
            return null;
        }

        return mappingService.getCodeValue(codeType, codeKey, defaultValue);
    }

    @Override
    public String getCodeKey(String codeType, String codeValue) {
        return this.getCodeKey(codeType, codeValue, null);
    }

    @Override
    public String getCodeKey(String codeType, String codeValue, String defaultKey) {
        if (mappingService == null) {
            log.warn("没有发现ICodeMappingService服务提供者");
            return null;
        }
        return mappingService.getCodeKey(codeType, codeValue, defaultKey);
    }

}
