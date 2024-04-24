package nohi.doc.service;

import java.io.File;
import java.io.InputStream;

import nohi.doc.config.meta.DocumentMeta;
import nohi.doc.service.config.NohiDocService;
import nohi.doc.vo.DocVO;
import org.apache.commons.lang3.StringUtils;


/**
 * 文档处理类接口
 *
 * @author NOHI
 * @create date: 2013-2-15
 */
public interface IDocService {

    /**
     * 导出数据到文档中
     */
    <T> DocVO<T> exportDoc(DocVO<T> doc) throws Exception;

    /**
     * 导入文档中的数据
     *
     * @param doc       配置信息
     * @param inputFile 导入文件
     */
    <T> void importFromFile(T dataVo, DocVO<T> doc, File inputFile) throws Exception;

    /**
     * 从输入流中根据文档解析数据
     * <p></p>文件流操作完成后不主动关闭</p>
     *
     * @param doc 配置信息
     * @param is  文件流
     */
    <T> void importFromInputStream(T dataVo, DocVO<T> doc, InputStream is) throws Exception;

    /**
     * 从输入流中根据文档解析数据
     *
     * @param doc                      配置信息
     * @param is                       文件流
     * @param parseFieldValueErrorExit 解析字段值错误,是否退出,默认false-不退出;
     */
    <T> void importFromInputStream(T dataVo, DocVO<T> doc, InputStream is, boolean parseFieldValueErrorExit) throws Exception;


    default DocumentMeta getDocumentMeta(String docId) {
        if (StringUtils.isBlank(docId)) {
            throw new RuntimeException("配置输入为空");
        }

        //1 取得文档模板
        DocumentMeta template = NohiDocService.getDocumentByDocId(docId);
        if (null == template) {
            throw new RuntimeException("文档模板为空");
        }
        return template;
    }
}
