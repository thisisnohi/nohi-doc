package nohi.doc.service;

import lombok.extern.slf4j.Slf4j;
import nohi.doc.DocConsts;
import nohi.doc.config.meta.DocumentMeta;
import nohi.doc.config.xml.DocMeta;
import nohi.doc.config.xml.NohiDocMeta;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;


/**
 * NOHI-doc 文档主服务
 *
 * @author NOHI
 * @version $Revision: 1.1 $ 建立日期 2013-2-7
 */
@Slf4j
public class NohiDocServices {
    /**
     * 主配置文件路径
     */
    private static String confPath = DocConsts.defaultConf;
    /**
     * NohiDoc主配置文件
     */
    private static NohiDocMeta mainDoc;

    /**
     * 所有文档模板配置文件
     */
    private static Map<String, DocumentMeta> template;


    public static NohiDocMeta getMainDoc() throws Exception {
        if (null == mainDoc) {
            init(confPath);
        }
        return mainDoc;
    }

    /**
     * 初始化
     */
    public static void init(String confPath) throws Exception {
        parseConf(confPath);// 解析主配置文件
    }

    /**
     * 解析文件
     */
    public static void parseConf(String confPath) throws JAXBException, IOException {
        mainDoc = XmlParse.parseNohiDoc(confPath);
    }

    /**
     * 取得文档配置属性
     */
    public static DocMeta getDocMeta(String docId) throws Exception {
        return getDocMetaByIdAndType(docId, null);
    }

    /**
     * 取得文档配置属性
     */
    public static DocMeta getDocMetaByIdAndType(String docId, String type) throws Exception {
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
     * @param id 文档ID
     * @return 文档配置信息
     * @throws Exception 异常
     */
    public static DocumentMeta getTemplate(String id) throws Exception {
        DocumentMeta tmp = null;
        if (null == template) {
            DocMeta dm = getDocMeta(id);
            if (null != dm) {
                Map<String, DocumentMeta> map = XmlParse.parseTemplateConf(dm.getConf());
                if (null != map) {
                    tmp = map.get(id);
                    template = map;
                }

            }
        } else {
            tmp = template.get(id);
            if (null == tmp) {
                DocMeta dm = getDocMeta(id);
                if (null != dm) {
                    Map<String, DocumentMeta> map = XmlParse.parseTemplateConf(dm.getConf());
                    if (null != map) {
                        tmp = map.get(id);
                        template = map;
                    }
                }
            }
        }

        return tmp;
    }
}
