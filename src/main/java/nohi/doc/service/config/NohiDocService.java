package nohi.doc.service.config;

import lombok.extern.slf4j.Slf4j;
import nohi.doc.DocConsts;
import nohi.doc.config.meta.DocumentMeta;
import nohi.doc.config.xml.DocMeta;
import nohi.doc.config.xml.NohiDocMeta;

import java.util.Map;
import java.util.Optional;


/**
 * NOHI-doc 文档主服务
 *
 * @author NOHI
 * @date 2024/4/12
 */
@Slf4j
public class NohiDocService {
    /**
     * 默认配置文件路径
     */
    public static final String DEFAULT_CONF = DocConsts.defaultConf;
    /**
     * NohiDoc主配置文件
     */
    private static volatile NohiDocMeta mainDoc;

    /**
     * 所有文档模板配置文件
     */
    private static Map<String, DocumentMeta> template;


    /**
     * 获取NohiDoc配置，如果没有实例，使用默认配置文件初始化
     */
    public static NohiDocMeta getMainDoc() {
        if (null == mainDoc) {
            synchronized (NohiDocService.class) {
                // 防止重复实例化
                if (null == mainDoc) {
                    mainDoc = init(DEFAULT_CONF);
                }
            }
        }
        return mainDoc;
    }

    public static NohiDocMeta initMainDoc(String confPath) {
        return init(confPath);
    }

    /**
     * 初始化-指定配置文件
     */
    private static NohiDocMeta init(String confPath) {
        // 解析主配置文件
        return parseConf(confPath);
    }

    /**
     * 解析文件
     */
    public static NohiDocMeta parseConf(String confPath) {
        return XmlParseService.parseNohiDoc(confPath);
    }

    /**
     * 取得Excel文档配置属性
     */
    public static DocMeta getDocMeta(String docId) {
        return getDocMetaByIdAndType(docId, null);
    }

    /**
     * 取得文档配置属性
     *
     * @param docId 文档ID
     * @param type  类型，默认excel.   pdf/excel
     */
    public static DocMeta getDocMetaByIdAndType(String docId, String type) {
        // 获取主页面实例
        NohiDocMeta doc = getMainDoc();

        if ("pdf".equalsIgnoreCase(type)) {
            // 获取excel实例
            Optional<DocMeta> docMetaOptional = doc.getPdf().stream().filter(item -> item.getId().equalsIgnoreCase(docId)).findFirst();
            return docMetaOptional.orElse(null);
        } else {
            // 获取excel实例
            Optional<DocMeta> docMetaOptional = doc.getExcel().stream().filter(item -> item.getId().equalsIgnoreCase(docId)).findFirst();
            return docMetaOptional.orElse(null);
        }
    }

    /**
     * 得到具体一个文档类型的具体属性
     *
     * @param docId 文档ID
     * @return 文档配置信息
     */
    public static DocumentMeta getDocumentByDocId(String docId) {
        DocumentMeta tmp = null;
        if (null != template) {
            tmp = template.get(docId);
        }
        if (null == tmp) {
            DocMeta dm = getDocMeta(docId);
            if (null != dm) {
                Map<String, DocumentMeta> map = XmlParseService.parseTemplateConf(dm.getConf());
                if (null != map) {
                    tmp = map.get(docId);
                    // 防止重复解析
                    if (template == null) {
                        template = map;
                    } else {
                        template.putAll(map);
                    }
                }
            }
        }
        return tmp;
    }
}
