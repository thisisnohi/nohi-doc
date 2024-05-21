package nohi.doc.config.meta.excel;

import lombok.Data;

import java.util.Map;

/**
 * Excel 块信息
 *
 * @author NOHI
 * @date 2024/3/29
 */
@Data
public class ExcelBlockMeta {
    public enum BlockType {
        /**
         * 静态块： 默认
         */
        FIELD,
        /**
         * 列表块
         */
        TABLE;
    }

    /**
     * 块类型：
     */
    private BlockType type = BlockType.FIELD;

    /**
     * 块区域名称
     */
    private String name;

    /**
     * 块相对上一块位置
     */
    private Integer addRows;
    /**
     * 块开始行索引
     * 当前块的开始位置 = 上一块开始行 + addRows
     */
    private Integer rowIndex;
    /**
     * 块结束行索引(包含这行)
     * 静态块，此行可以省略
     */
    private Integer endRowIndex;
    /**
     * 列表对象存储变量名
     * 1.导出时取值
     * 2.导入时存值
     */
    private String list;
    /**
     * 列表项Java对象
     */
    private String itemClass;
    /**
     * 单元格配置
     */
    private Map<String, ExcelColMeta> cols;

    /*** ===================运行时变量：非配置项=================== ***/
    /**
     * 该块占用最后行的index
     */
    private Integer lastModifyRowIndex;
    /**
     * 上一块战胜最后一行index
     */
    private Integer lastBlockLastModifyRowIndex;
}
