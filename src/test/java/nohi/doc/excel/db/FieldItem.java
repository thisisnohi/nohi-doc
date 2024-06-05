package nohi.doc.excel.db;

import lombok.Data;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>field</p>
 * @date 2024/06/05 14:17
 **/
@Data
public class FieldItem {
    private String fieldName;
    private String fieldNameCn;
    private String dataType;
    private String dataLength;
    private String primaryKey;
    private String notNull;
    private String defaultValue;
    private String comments;
    private String codeType;
}
