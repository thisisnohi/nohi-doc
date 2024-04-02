package nohi.doc.config.meta;

import lombok.Data;
import nohi.doc.config.meta.excel.ExcelSheetMeta;
import nohi.doc.config.meta.ftp.DocPdfUnitMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 具体文档模板属性配置文件
 *
 * @author NOHI
 */
@Data
public class DocumentMeta {
    /**
     * 文档ID
     */
    private String id;
    /**
     * 文档名称
     */
    private String name;
    /**
     * 文档模板
     */
    private String template;
    /**
     * 文档存取数据的对象
     */
    private String dataClass;
    /**
     * 文档类型 EXCEL
     */
    private String docType;

    /**
     * excel sheet页配置列表
     */
    private List<ExcelSheetMeta> sheetList;


    // PDF单元格Map
    private Map<String, DocPdfUnitMeta> pdfUnitMap;

    public List<ExcelSheetMeta> getSheetList() {
        if (null == sheetList) {
            sheetList = new ArrayList<>();
        }
        return sheetList;
    }

}
