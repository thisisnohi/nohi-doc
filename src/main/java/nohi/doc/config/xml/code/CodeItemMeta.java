package nohi.doc.config.xml.code;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 文档配置主类
 *
 * @author NOHI
 * @date 2024/3/29
 */
@XmlRootElement(name = "code")
@XmlAccessorType(XmlAccessType.FIELD)  // 根据字段解析XML
@Data
public class CodeItemMeta {
    // 文档ID
    @XmlAttribute(name = "codeKey")
    private String codeKey;
    // 文档名称
    @XmlAttribute(name = "codeValue")
    private String codeValue;
}
