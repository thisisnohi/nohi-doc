package nohi.doc.config.xml;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Doc
 * @author nohi
 */
@XmlRootElement(name = "doc")
@XmlAccessorType(XmlAccessType.FIELD)  // 根据字段解析XML
@Data
public class DocMeta {
    // 文档ID
    @XmlAttribute(name = "id")
    private String id;
    // 文档名称
    @XmlAttribute(name = "name")
    private String name;
    // 文档具体属性配置文件
    @XmlAttribute(name = "conf")
    private String conf;
}
