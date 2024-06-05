package nohi.doc.excel.db;

import lombok.Data;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>index</p>
 * @date 2024/06/05 14:17
 **/
@Data
public class IndexItem {
    private String indexName;
    private String unique;
    private String cols;

}
