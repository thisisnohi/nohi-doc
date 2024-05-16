package nohi.doc.service.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nohi.doc.DocConsts;
import nohi.doc.config.meta.DocumentMeta;
import nohi.doc.config.meta.excel.ExcelBlockMeta;
import nohi.doc.config.meta.excel.ExcelColMeta;
import nohi.doc.config.meta.excel.ExcelSheetMeta;
import nohi.doc.config.meta.ftp.DocPdfUnitMeta;
import nohi.doc.config.xml.NohiDocMeta;
import nohi.utils.Clazz;
import nohi.utils.FileUtils;
import nohi.utils.XmlUtils;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;


/**
 * XML解析
 *
 * @author NOHI
 * 建立日期 2013-2-7
 */
@Slf4j
public class XmlParseService {

    public static Document getDocumentByResourcePath(String confPath) {
        Document doc;
        try ( //1 得到输入流
              InputStream in = XmlParseService.class.getClassLoader().getResourceAsStream(confPath)) {
            //2 解析XML文件
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(in);
        } catch (Exception e) {
            log.error("解析配置文件异常:{}", e.getMessage(), e);
            throw new RuntimeException("读取配置文件[" + confPath + "]出错", e);
        }
        return doc;
    }

    /**
     * 　解析文档服务主配置
     *
     * @param confPath 配置文件路径
     */
    public static NohiDocMeta parseNohiDoc(String confPath) {
        if (null == confPath) {
            confPath = DocConsts.defaultConf;
        }

        try (InputStream inputStream = XmlParseService.class.getClassLoader().getResourceAsStream(confPath);) {
            if (inputStream == null) {
                throw new RuntimeException("配置文件不存在");
            }
            // 读取XML
            String xml = FileUtils.readStringFromStream(inputStream);
            log.debug("xml: {}", xml);
            // 解析
            NohiDocMeta docMeta = XmlUtils.convertXml2Bean(xml, NohiDocMeta.class);
            log.debug("docMeta: {}", JSONObject.toJSONString(docMeta));
            return docMeta;
        } catch (IOException | JAXBException e) {
            log.error("解析配置文件[{}] 异常:{}", confPath, e.getMessage());
            throw new RuntimeException("解析配置文件异常" + e.getMessage());
        }
    }


    /**
     * 解析Attribute对象，设置值到对应对象中
     *
     * @param obj  设置目标对象
     * @param list element列表
     */
    private static void parseAttributeToObject(Object obj, List<Attribute> list) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException {
        if (null != list && !list.isEmpty()) {
            for (Attribute att : list) {
                Class<?> clazz = obj.getClass();
                Field field = clazz.getDeclaredField(att.getName());
                if (field.getType().isEnum()) {
                    Object[] enums = field.getType().getEnumConstants();
                    for (Object anEnum : enums) {
                        if (anEnum.toString().equalsIgnoreCase(att.getValue())) {
                            Method method = clazz.getMethod("set" + Clazz.covertFirstChar2Upper(att.getName()), ExcelBlockMeta.BlockType.class);
                            //noinspection JavaReflectionInvocation
                            method.invoke(obj, anEnum);
                        }
                    }
                } else {
                    try {
                        Method method = Clazz.getMethod(clazz, att.getName(), "set", field.getType());

                        if (field.getType() == String.class) {
                            method.invoke(obj, att.getValue());
                        } else if (field.getType() == Integer.class || field.getType() == int.class) {
                            method.invoke(obj, att.getIntValue());
                        } else if (field.getType() == Double.class || field.getType() == double.class) {
                            method.invoke(obj, att.getDoubleValue());
                        } else if (field.getType() == Float.class || field.getType() == float.class) {
                            method.invoke(obj, att.getFloatValue());
                        } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                            method.invoke(obj, att.getBooleanValue());
                        } else if (field.getType() == Date.class) {
                            method.invoke(obj, att.getValue());
                        } else if (field.getType() == BigDecimal.class) {
                            method.invoke(obj, new BigDecimal(att.getValue()));
                        }
                    } catch (Exception e) {
                        log.error("SET[{}] 异常:{}", att.getName(), e.getMessage(), e);
                        throw new RuntimeException("set" + att.getName() + "异常：" + e.getMessage(), e);
                    }

                }
            }
        }
    }

    /**
     * 解析document模板配置文件
     *
     * @param confPath 配置该报路径
     */
    public static Map<String, DocumentMeta> parseTemplateConf(String confPath) {
        if (null == confPath) {
            log.warn("解析具体文档属性配置文件出错，没对应的配置文件");
            return null;
        }
        // 所有文档模板配置文件列表
        Map<String, DocumentMeta> templateMap = new HashMap<>();

        // 获取要根节点<documents>
        Element root = getDocumentByResourcePath(confPath).getRootElement();
        // 得到所有<document>
        List<Element> docElements = root.getChildren();
        for (Element documentElement : docElements) {
            DocumentMeta documentMeta = new DocumentMeta();
            try {
                // 解析 <document> 元素属性
                parseAttributeToObject(documentMeta, documentElement.getAttributes());

                // 如果文档是Excel.需要解析Excel sheet
                if (DocConsts.DOC_TYPE_EXCEL.equals(documentMeta.getDocType())) {
                    documentMeta.setSheetList(parseExcelSheet(documentElement));
                } else if (DocConsts.DOC_TYPE_PDF.equals(documentMeta.getDocType())) {
                    documentMeta.setPdfUnitMap(parsePdf(documentElement));
                }
            } catch (Exception e) {
                log.error("解析节点:[{}] 异常:{}", documentElement, e.getMessage(), e);
                throw new RuntimeException("解析节点[" + documentElement + "]异常");
            }

            if (templateMap.containsKey(documentMeta.getId())) {
                log.warn("存在重复文档ID[{}]覆盖处理", documentMeta.getId());
            }
            // 放入Map
            templateMap.put(documentMeta.getId(), documentMeta);
        }

        return templateMap;
    }

    /**
     * 解析PDF文件
     */
    private static Map<String, DocPdfUnitMeta> parsePdf(Element e) throws Exception {
        Map<String, DocPdfUnitMeta> unitMap = new HashMap<>();
        List<Element> unitList = e.getChildren();//unit对象
        for (Element unit : unitList) {
            DocPdfUnitMeta t = new DocPdfUnitMeta();

            parseAttributeToObject(t, unit.getAttributes());

            if (DocConsts.PDF_UNIT_TYPE_TABLE.equals(t.getType())) {
                t.setUnitTableMap(parsePdf(unit));
            }

            //放入Map
            unitMap.put(t.getName(), t);
        }
        return unitMap;
    }

    /**
     * 解析Excel表单
     */
    private static List<ExcelSheetMeta> parseExcelSheet(Element e) throws Exception {
        List<ExcelSheetMeta> sheetList = new ArrayList<>();
        // 得到所有sheet对象
        List<Element> sheetElements = e.getChildren();
        if (null != sheetElements) {
            for (Element sheet : sheetElements) {
                ExcelSheetMeta excelSheetMeta = new ExcelSheetMeta();
                String name = sheet.getAttributeValue("name");
                String exportSheetName = sheet.getAttributeValue("exportSheetName");

                // 解析块
                excelSheetMeta.setBlockList(parseBlock(sheet));
                excelSheetMeta.setName(name);
                excelSheetMeta.setExportSheetName(exportSheetName);

                // 集合
                sheetList.add(excelSheetMeta);
            }
        }
        return sheetList;
    }

    /**
     * 解析block
     */
    private static List<ExcelBlockMeta> parseBlock(Element sheet) throws Exception {
        List<ExcelBlockMeta> blockList = new ArrayList<>();

        List<Element> blockElements = sheet.getChildren();
        // 上一块
        ExcelBlockMeta lastBlockMeta = null;
        for (Element block : blockElements) {
            ExcelBlockMeta excelBlockMeta = new ExcelBlockMeta();

            // 解析属性
            parseAttributeToObject(excelBlockMeta, block.getAttributes());

            // 计算rowIndex addRows
            if (null == excelBlockMeta.getRowIndex() && null != lastBlockMeta) {
                excelBlockMeta.setRowIndex(lastBlockMeta.getRowIndex() + excelBlockMeta.getAddRows());
            }

            // 如果是列表
            if (null != block.getChildren()) {
                Map<String, ExcelColMeta> colMap = new HashMap<>();

                List<Element> colListEmt = block.getChildren();
                for (Element colElement : colListEmt) {
                    ExcelColMeta excelColMeta = new ExcelColMeta();
                    // 解析属性
                    parseAttributeToObject(excelColMeta, colElement.getAttributes());

                    //存放
                    colMap.put(excelColMeta.getColumn().toString(), excelColMeta);
                }
                excelBlockMeta.setCols(colMap);
            }
            // 赋值上一块
            lastBlockMeta = excelBlockMeta;
            // 集合
            blockList.add(excelBlockMeta);
        }

        return blockList;
    }
}
