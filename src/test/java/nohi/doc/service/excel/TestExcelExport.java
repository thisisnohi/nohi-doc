package nohi.doc.service.excel;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import nohi.doc.config.meta.DocumentMeta;
import nohi.doc.excel.vo.InnerVO;
import nohi.doc.excel.vo.TestDocVO;
import nohi.doc.excel.vo.TestListVO;
import nohi.doc.service.IDocService;
import nohi.doc.service.impl.DocService;
import nohi.doc.vo.DocVO;
import nohi.utils.DateUtils;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>excel导出</p>
 * @date 2024/05/16
 **/
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Excel导出测试")
public class TestExcelExport {

    private TestDocVO getData() {
        TestDocVO data = new TestDocVO();
        data.setStr1("字符串");
        data.setStr2("12345678.90123");
        data.setIntV1(123456789);
        data.setIntV2(123456789);
        data.setDouble1(1234.1234);
        data.setDouble2(123456789.01234);
        data.setFloatV1(1234.01234f);
        data.setFloatV2(0.01234f);
        data.setDate1(new Date());
        data.setDate2(DateUtils.parseDate("2024-01-02 03:04:05", DateUtils.HYPHEN_TIME));
        data.setBooleanV1(true);
        data.setBooleanV2(false);

        InnerVO vo = new InnerVO();
        data.setInnerObject(vo);
        vo.setStr1("字符串");
        vo.setStr2("12345678.90123");
        vo.setIntV1(123456789);
        vo.setIntV2(123456789);
        vo.setDouble1(1234.1234);
        vo.setDouble2(123456789.01234);
        vo.setFloatV1(1234.01234f);
        vo.setFloatV2(0.01234f);
        vo.setFloatV2(0.01234f);
        vo.setDate1(new Date());
        vo.setDate2(DateUtils.parseDate("2024-01-02 03:04:05", DateUtils.HYPHEN_TIME));
        vo.setBooleanV1(true);
        vo.setBooleanV2(false);

        List<TestListVO> list = Lists.newLinkedList();
        data.setList(list);
        for (int i = 0; i < 20; i++) {
            TestListVO item = new TestListVO();
            Map<String, String> map = new HashMap<String, String>();
            item.setId(i+1);
            item.setName("NAME_" + i);
            item.setAge(18 + i);
            item.setAmt(2000.012d + i);
            item.setTest("156");
            item.setBd(new BigDecimal(1000 + i));
            item.setDate(new Date());
            item.setAbcMap(map);

            map.put("aaa", "A_" + i);
            map.put("bbb", "BBB_" + i);

            list.add(item);
        }

        return data;
    }
    /**
     * 导出：
     * 只有一个静态区块
     */
    @Test
    @Order(1)
    @DisplayName("导出静态区")
    public void exportFieldBlock() throws Exception {
        TestDocVO data = getData();

        IDocService docService = new DocService();

        DocVO<TestDocVO> doc = new DocVO<>();
        doc.setDocId("STATIC_FIELD_BLOCK");
        doc.setDocType("EXCEL");
        doc.setDataVo(data);
        doc.setFilePath("/Users/nohi/Downloads");
        doc.setDocName("field.xls");

        doc = docService.exportDoc(doc);
        log.debug("doc:{}", JSONObject.toJSONString(doc));
    }

    /**
     * 导出：
     * 只有一个列表块
     */
    @Test
    @Order(2)
    @DisplayName("导出列表块")
    public void exportTableBlock() throws Exception {
        TestDocVO data = getData();

        IDocService docService = new DocService();

        DocVO<TestDocVO> doc = new DocVO<>();
        doc.setDocId("TABLE_BLOCK");
        doc.setDocType("EXCEL");
        doc.setDataVo(data);
        doc.setFilePath("/Users/nohi/Downloads");
        doc.setDocName("table.xls");

        doc = docService.exportDoc(doc);
        log.debug("doc:{}", JSONObject.toJSONString(doc));
    }
}
