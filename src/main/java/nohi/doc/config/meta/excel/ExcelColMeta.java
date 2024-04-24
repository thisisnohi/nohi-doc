package nohi.doc.config.meta.excel;

import lombok.Data;

/**
 * Excel列配置
 * @author NOHI
 * @date 2024/3/29
 */
@Data
public class ExcelColMeta {
    /**
     * 单元格索引
     */
    private Integer column;
    /**
     * 数据对象属性
     */
    private String property;
    /**
     * 字段类型
     */
    private String dataType;
    /**
     * 格式化
     */
    private String pattern;
    /**
     * 代码值
     */
    private String codeType;
}
