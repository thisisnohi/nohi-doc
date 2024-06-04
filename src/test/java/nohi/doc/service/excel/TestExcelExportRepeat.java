package nohi.doc.service.excel;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
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
 * @description <p>excel导出重复页/重复数据</p>
 * @date 2024/05/16
 **/
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Excel导出重复数据")
public class TestExcelExportRepeat {
    private TestDocVO getData() {
        return getData(20);
    }

    private TestDocVO getData(int listSize) {
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
        for (int i = 0; i < listSize; i++) {
            TestListVO item = new TestListVO();
            Map<String, String> map = new HashMap<String, String>();
            item.setId(i + 1);
            item.setName("NAME_" + i);
            item.setAge(18 + i);
            item.setAmt(2000.012d + i);
            item.setTest("156");
            item.setBd(new BigDecimal(1000.012 + i));
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
     * 单页 -> 导出多sheet
     * 所有单页配置都可通过datvo传递List,导出多sheet页
     */
    @Test
    @Order(1)
    @DisplayName("单页导出重复sheet")
    public void exportSingle2RepeatSheet() throws Exception {

        List<TestDocVO> list = Lists.newLinkedList();
        for (int i = 0; i < 20; i++) {
            TestDocVO sheetObj1 = getData();
            sheetObj1.setStr1("第[" + (i + 1) + "]页字符串");
            sheetObj1.setSheetName("第" + (i + 1) + "页");
            list.add(sheetObj1);
        }

        IDocService docService = new DocService();

        // 单页 -> 导出多sheet
        // 所有单页配置都可通过datvo传递List,导出多sheet页
        DocVO<List<TestDocVO>> doc = new DocVO<>();
        doc.setDocId("MULTI_TABLE_BLOCK");
        doc.setDocType("EXCEL");
        doc.setDataVo(list);
        doc.setFilePath("/Users/nohi/Downloads");
        doc.setDocName("单页导出重复sheet.xlsx");

        doc = docService.exportDoc(doc);
        log.debug("doc:{}", JSONObject.toJSONString(doc));
    }

    /**
     * 导出：
     * 多页 -> 导出重复的多页
     */
    @Test
    @Order(2)
    @DisplayName("多页导出重复sheet")
    public void exportMultiSheet2RepeatSheet() throws Exception {

        List<TestDocVO> list = Lists.newLinkedList();
        for (int i = 0; i < 20; i++) {
            TestDocVO sheetObj1 = getData();
            sheetObj1.setStr1("第[" + (i + 1) + "]页字符串");
            sheetObj1.setSheetName("第" + (i + 1) + "页");
            list.add(sheetObj1);
        }

        IDocService docService = new DocService();

        // 多页 -> 导出重复的多页
        list = Lists.newLinkedList();
        for (int i = 0; i < 5; i++) {
            TestDocVO sheetObj1 = getData();
            TestDocVO sheetObj2 = getData();
            TestDocVO data = new TestDocVO();
            data.setSheetObject(sheetObj1);
            data.setTableSheetObject(sheetObj2);
            sheetObj1.setStr1("第[" + (i + 1) + "]页字符串");
            // 注意excel中sheet页名称不能重复
            sheetObj1.setSheetName("静态列" + (i + 1) + "页");
            sheetObj2.setSheetName("列表页" + (i + 1) + "页");

            list.add(data);
        }

        DocVO<List<TestDocVO>> doc = new DocVO<>();
        doc.setDocId("MULTI_SHEET_DATA");
        doc.setDocType("EXCEL");
        doc.setDataVo(list);
        doc.setFilePath("/Users/nohi/Downloads");
        doc.setDocName("多页导出重复sheet.xlsx");

        doc = docService.exportDoc(doc);
        log.debug("doc:{}", JSONObject.toJSONString(doc));
    }

    /**
     * 导出：
     * 通过repeat 导出多sheet页
     */
    @Test
    @Order(3)
    @DisplayName("repeat属性导出重复页")
    public void exportRepeatSheet() throws Exception {
        TestDocVO docVO = new TestDocVO();
        TestDocVO sheetObj1 = getData();
        docVO.setSheetObject(sheetObj1);

        // 设置列表页
        List<TestDocVO> list = Lists.newLinkedList();
        for (int i = 0; i < 20; i++) {
            sheetObj1 = getData();
            sheetObj1.setStr1("第[" + (i + 1) + "]页字符串");
            sheetObj1.setSheetName("第" + (i + 1) + "页");
            list.add(sheetObj1);
        }
        docVO.setRepeatSheetObject(list);

        IDocService docService = new DocService();

        // 单页 -> 导出多sheet
        // 所有单页配置都可通过datvo传递List,导出多sheet页
        DocVO<TestDocVO> doc = new DocVO<>();
        doc.setDocId("MULTI_REPEAT_SHEET");
        doc.setDocType("EXCEL");
        doc.setDataVo(docVO);
        doc.setFilePath("/Users/nohi/Downloads");
        doc.setDocName("repeat属性导出重复页.xlsx");

        doc = docService.exportDoc(doc);
        log.debug("doc:{}", JSONObject.toJSONString(doc));
    }

    /**
     * 导出：
     * 通过repeat 导出多sheet页
     */
    @Test
    @Order(4)
    @DisplayName("repeat属性导出重复页-大数据量")
    public void exportRepeatSheetBigData() throws Exception {
        long start = System.currentTimeMillis();
        TestDocVO docVO = new TestDocVO();
        TestDocVO sheetObj1 = getData();
        docVO.setSheetObject(sheetObj1);

        // 设置列表页
        List<TestDocVO> list = Lists.newLinkedList();
        for (int i = 0; i < 10; i++) {
            sheetObj1 = getData(10000);
            sheetObj1.setStr1("第[" + (i + 1) + "]页字符串");
            sheetObj1.setSheetName("第" + (i + 1) + "页");
            list.add(sheetObj1);
        }
        docVO.setRepeatSheetObject(list);

        log.info("数据对象创建完成,耗时[{}]", System.currentTimeMillis() - start);

        IDocService docService = new DocService();

        // 单页 -> 导出多sheet
        // 所有单页配置都可通过datvo传递List,导出多sheet页
        DocVO<TestDocVO> doc = new DocVO<>();
        doc.setDocId("MULTI_REPEAT_SHEET");
        doc.setDocType("EXCEL");
        doc.setDataVo(docVO);
        doc.setFilePath("/Users/nohi/Downloads");
        doc.setDocName("repeat属性导出重复页-大数据量.xlsx");
        log.info("开始导出,耗时[{}]", System.currentTimeMillis() - start);
        doc = docService.exportDoc(doc);
        log.debug("doc:{}", doc.getFilePath());
        log.info("导出完成,耗时[{}]", System.currentTimeMillis() - start);
    }

    /**
     * 导出：
     * 单页重复： singleRepeat
     */
    @Test
    @Order(5)
    @DisplayName("单页重复-多块")
    public void exportSingleRepeat() throws Exception {
        long start = System.currentTimeMillis();
        TestDocVO docVO = new TestDocVO();
        // 设置列表页
        List<TestDocVO> list = Lists.newLinkedList();
        for (int i = 0; i < 10; i++) {
            TestDocVO sheetObj1 = getData();
            sheetObj1.setStr1("第[" + (i + 1) + "]页字符串");
            sheetObj1.setSheetName("第" + (i + 1) + "页");
            list.add(sheetObj1);
        }
        docVO.setRepeatSheetObject(list);

        log.info("数据对象创建完成,耗时[{}]", System.currentTimeMillis() - start);

        IDocService docService = new DocService();

        // 单页 -> 导出多sheet
        // 所有单页配置都可通过datvo传递List,导出多sheet页
        DocVO<TestDocVO> doc = new DocVO<>();
        doc.setDocId("SINGLE_REPEAT_MULTI_BLOCK");
        doc.setDocType("EXCEL");
        doc.setDataVo(docVO);
        doc.setFilePath("/Users/nohi/Downloads");
        doc.setDocName("单页重复-多块.xlsx");
        log.info("开始导出,耗时[{}]", System.currentTimeMillis() - start);
        doc = docService.exportDoc(doc);
        log.debug("doc:{}", doc.getFilePath());
        log.info("导出完成,耗时[{}]", System.currentTimeMillis() - start);
    }
}
