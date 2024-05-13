
package nohi.doc.excel.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class TestListVO {
    private int id;
    private String name;
    private Integer age;
    private Date date;
    private Double amt;

    private BigDecimal bd;
    private String test;

    private Map abcMap;

}
