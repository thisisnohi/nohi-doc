package nohi.doc.excel.vo;

import lombok.Data;

import java.util.*;

@Data
public class TestDocVO {

    /** 静态域 **/
    // 字符串
    private String str1;
    private String str2;
    // 整形
    private Integer intV1;
    private int intV2;

    // double
    private Double double1;
    private double double2;

    // float
    private Float floatV1;
    private float floatV2;

    // 日期
    private Date date1;
    private Date date2;

    // 布尔
    private Boolean booleanV1;
    private boolean booleanV2;

    // 内部对象
    private InnerVO innerObject; // 内部对象

    private String sheetName;
    private List<TestListVO> list;// 列表

    // sheetData
    private TestDocVO sheetObject;
    private TestDocVO tableSheetObject;
}
