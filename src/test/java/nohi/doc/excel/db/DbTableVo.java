package nohi.doc.excel.db;

import lombok.Data;

import java.util.List;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>db</p>
 * @date 2024/06/05 14:21
 **/
@Data
public class DbTableVo {
    private String tableName;
    private String tableNameCn;
    private String tableDesc;

    private List<IndexItem> indexList;
    private List<FieldItem> fieldList;
    private String sheetName;
}
