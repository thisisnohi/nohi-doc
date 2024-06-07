package nohi.doc.config.xml.code;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;


/**
 * 文档配置主类
 *
 * @author NOHI
 * @date 2024/3/29
 */
@XmlRootElement(name = "code-type")
@XmlAccessorType(XmlAccessType.FIELD)  // 根据字段解析XML
@Data
public class CodeTypeMeta {

    @XmlAttribute(name="type-name")
    private String typeName;
    @XmlElement(name = "code")
    List<CodeItemMeta> codeList;
}
