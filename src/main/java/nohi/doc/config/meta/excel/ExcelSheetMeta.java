package nohi.doc.config.meta.excel;

import lombok.Data;

import java.util.List;

@Data
public class ExcelSheetMeta {
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
