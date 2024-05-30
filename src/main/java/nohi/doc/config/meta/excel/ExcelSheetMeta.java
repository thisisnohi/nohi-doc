package nohi.doc.config.meta.excel;

import lombok.Data;

import java.util.List;

/**
 * Excel Sheet页信息
 * @author nohi
 */
@Data
public class ExcelSheetMeta {
    /**
     * sheet类型： single-单页，repeat-重复页(用于导出)
     */
    private String type;

    /**
     * sheet页数据对象，可为空，如果为空，则使用document对象
     */
    private String sheetData;

    /**
     * excel模板sheet页名称
     */
    private String name;
    /**
     * 导出文件sheet页名称
     */
    private String exportSheetName;


    /**
     * sheet页中所块信息
     */
    private List<ExcelBlockMeta> blockList;
}
