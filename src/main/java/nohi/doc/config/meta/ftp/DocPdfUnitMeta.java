package nohi.doc.config.meta.ftp;

import lombok.Data;

import java.util.Map;

/**
 * PDF单元格元数据
 *
 * @author NOHI
 * @date 20240412
 */
@Data
public class DocPdfUnitMeta {
    // text 或者 table
    private String type;
    private String name;
    // 对应Java属性字段
    private String property;
    // 字段类型
    private String dataType;
    // 格式标签
    private String pattern;
    // 代码值
    private String codeType;
    // 元素类: 对应如果是table列表时，对应相应的item
    private String itemClass;
    // 单元格Map，可以有多个单元格块
    private Map<String, DocPdfUnitMeta> unitTableMap;
}
