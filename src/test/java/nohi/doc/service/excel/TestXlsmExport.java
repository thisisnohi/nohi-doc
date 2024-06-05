package nohi.doc.service.excel;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nohi.doc.excel.db.DbTableVo;
import nohi.doc.excel.db.FieldItem;
import nohi.doc.excel.db.IndexItem;
import nohi.doc.service.IDocService;
import nohi.doc.service.impl.DocService;
import nohi.doc.vo.DocVO;
import org.apache.commons.compress.utils.Lists;
import org.junit.jupiter.api.*;

import java.awt.*;
import java.util.List;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>xlsm导出</p>
 * @date 2024/05/16
 **/
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Xlsm导出测试")
public class TestXlsmExport {
    private DbTableVo getData() {
        return getData(20);
    }

    private DbTableVo getData(int listSize) {
        DbTableVo vo = new DbTableVo();
        vo.setTableDesc("这是表的描述信息");
        vo.setTableName("T_ABC_ADD");
        vo.setTableNameCn("表名..中文");
        vo.setSheetName("测试");
        List<FieldItem> fieldList = Lists.newArrayList();
        List<IndexItem> indexList = Lists.newArrayList();
        vo.setFieldList(fieldList);
        vo.setIndexList(indexList);

        // 索引
        IndexItem index = new IndexItem();
        index.setIndexName("IDX_TAB_1");
        index.setUnique("Y");
        index.setCols("A,B,C");
        indexList.add(index);

        index = new IndexItem();
        index.setIndexName("UN_TAB_1");
        index.setUnique("Y");
        index.setCols("A,B,C");
        indexList.add(index);

        // 字段
        for (int i = 0; i < listSize; i++) {
            FieldItem item = new FieldItem();
            fieldList.add(item);

            item.setFieldName("ID" + i);
            item.setFieldNameCn("主键");
            item.setDataType("varchar2");
            item.setDataLength((20 + i) + "");
            item.setPrimaryKey("Y");
            item.setNotNull("Y");
            item.setDefaultValue("默认值1");
            item.setComments("备注说明");
        }
        return vo;
    }

    /**
     * 导出：
     * 只有一个静态区块
     */
    @Test
    @Order(1)
    @DisplayName("导出xlsm")
    public void exportFieldBlock() throws Exception {
        DbTableVo data = getData();

        IDocService docService = new DocService();

        DocVO<DbTableVo> doc = new DocVO<>();
        doc.setDocId("XLSM_EXPORT");
        doc.setDocType("EXCEL");
        doc.setDataVo(data);
        doc.setFilePath("/Users/nohi/Downloads");
        doc.setDocName("导出xlsm.xlsm");

        doc = docService.exportDoc(doc);
        log.debug("doc:{}", JSONObject.toJSONString(doc));
    }

}
