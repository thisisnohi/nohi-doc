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
@XmlAccessorType(XmlAccessType.FIELD)  // 根据字段解析XML
@XmlRootElement(name = "codes")
@Data
public class CodesMeta {
    @XmlElement(name = "code-type")
    private List<CodeTypeMeta> codeTypeList;
}
