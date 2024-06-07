package nohi.doc.service;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>码值映射服务</p>
 * @date 2024/06/06 14:17
 **/
public interface ICodeMappingService {

    /**
     * 根据码值类型、key 获取对应key的值
     *
     * @param codeType 码值类型
     * @param codeKey  码值key
     * @return 码值Value
     */
    String getCodeValue(String codeType, String codeKey);

    /**
     * 根据码值类型、key 获取对应key的值
     * 如果没有找到返回默认值
     *
     * @param codeType 码值类型
     * @param codeKey  码值key
     * @return 码值Value
     */
    String getCodeValue(String codeType, String codeKey, String defaultValue);

    /**
     * 根据码值类型、值， 获取对应key
     *
     * @param codeType  码值类型
     * @param codeValue 码值key
     * @return 码值Value
     */
    String getCodeKey(String codeType, String codeValue);

    /**
     * 根据码值类型、key 获取对应key的值
     * 如果没有找到返回默认值
     *
     * @param codeType  码值类型
     * @param codeValue 码值key
     * @return 码值key
     */
    String getCodeKey(String codeType, String codeValue, String defaultKey);
}
