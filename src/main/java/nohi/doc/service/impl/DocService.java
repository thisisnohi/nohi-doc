package nohi.doc.service.impl;

import lombok.extern.slf4j.Slf4j;
import nohi.doc.DocConsts;
import nohi.doc.service.IDocService;
import nohi.doc.vo.DocVO;

import java.io.File;
import java.io.InputStream;


/**
 * 文档实现类
 *
 * @author NOHI
 * @create date: 2013-2-15
 */
@Slf4j
public class DocService implements IDocService {

    /**
     * 导入文件
     *
     * @param doc 配置
     */
    @Override
    public <T> DocVO<T> exportDoc(DocVO<T> doc) throws Exception {
        //如果为Excel
        if (DocConsts.DOC_TYPE_EXCEL.equals(doc.getDocType())) {
            ExcelXlsxService<T> excel = new ExcelXlsxService<>("TODO 导出");
            excel.exportDoc(doc);
        } else if (DocConsts.DOC_TYPE_PDF.equals(doc.getDocType())) {
//            PdfService excel = new PdfService();
//            excel.exportDoc(doc);
        }
        log.debug("文件名[{}]", doc.getFilePath());
        return doc;
    }

    /**
     * 文件导入
     *
     * @param doc       配置信息
     * @param inputFile 导入文件
     */
    @Override
    public <T> void importFromFile(T dataVo, DocVO<T> doc, File inputFile) {
        //0 检验输入数据
        if (null == doc || null == inputFile) {
            log.error("配置为空/输入文件为空");
            throw new RuntimeException("配置为空/输入文件为空");
        }
        String title = String.format("导入[%s-%s]", doc.getDocType(), doc.getDocId());
        log.debug(title);

        //如果为Excel
        if (DocConsts.DOC_TYPE_EXCEL.equals(doc.getDocType())) {
            ExcelXlsxService<T> excel = new ExcelXlsxService<>(title);
            excel.importFromFile(dataVo, doc, inputFile);
        } else if (DocConsts.DOC_TYPE_PDF.equals(doc.getDocType())) {
            log.warn("{} PDF暂时不支持", title);
        } else {
            log.warn("{} 暂不支持的文档类型[{}]", title, doc.getDocType());
            throw new RuntimeException("暂不支持的文档类型");
        }
    }

    /**
     * 按输入流解析文件，文件流操作完成后不主动关闭
     *
     * @param doc 配置信息
     * @param is  文件流
     */
    @Override
    public <T> void importFromInputStream(T dataVo, DocVO<T> doc, InputStream is) {
        //0 检验输入数据
        if (null == doc || null == is) {
            log.error("配置为空/文件流为空");
            throw new RuntimeException("配置为空/文件流为空");
        }
        log.debug("docType: {}, docID: {}", doc.getDocType(), doc.getDocId());
        if (DocConsts.DOC_TYPE_EXCEL.equals(doc.getDocType())) {
            ExcelXlsxService<T> excel = new ExcelXlsxService<>(doc.getDocId());
            excel.importFromInputStream(dataVo, doc, is);
        } else if (DocConsts.DOC_TYPE_PDF.equals(doc.getDocType())) {
            log.debug("PDF暂时不支持");
        }
    }

    /**
     * 导入文件
     *
     * @param doc                      配置信息
     * @param is                       文件流
     * @param parseFieldValueErrorExit 解析字段值错误,是否退出,默认false-不退出;
     */
    @Override
    public <T> void importFromInputStream(T dataVo, DocVO<T> doc, InputStream is, boolean parseFieldValueErrorExit) {
        importFromInputStream(dataVo, doc, is);
    }
}
