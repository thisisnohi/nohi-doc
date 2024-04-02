package nohi.doc.config.xml;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;


/**
 * 文档配置主类
 *
 * @author NOHI
 * @date 2024/3/29
 */
@XmlRootElement(name = "nohi-doc")
@XmlAccessorType(XmlAccessType.FIELD)  // 根据字段解析XML
@Data
public class NohiDocMeta {
    @XmlElementWrapper(name = "doc-excel")
    @XmlElement(name = "doc")
    private List<DocMeta> excel;

    @XmlElementWrapper(name = "doc-pdf")
    @XmlElement(name = "doc")
    private  List<DocMeta> pdf;
}
