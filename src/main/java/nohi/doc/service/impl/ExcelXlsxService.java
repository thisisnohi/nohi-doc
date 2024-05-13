package nohi.doc.service.impl;

import lombok.extern.slf4j.Slf4j;
import nohi.doc.DocConsts;
import nohi.doc.config.meta.excel.ExcelBlockMeta;
import nohi.doc.config.meta.excel.ExcelColMeta;
import nohi.doc.config.meta.excel.ExcelSheetMeta;
import nohi.doc.config.meta.DocumentMeta;
import nohi.doc.service.IDocService;
import nohi.doc.utils.ExcelUtils;
import nohi.doc.vo.DocVO;
import nohi.ftp.service.FtpServer;
import nohi.utils.Clazz;
import nohi.utils.DocCommonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Excel服务类
 *
 * @author NOHI
 * @create date: 2013-2-14
 */
@Slf4j
public class ExcelXlsxService<T> extends FtpServer implements IDocService {
    private String title = "";
    // 模板文件
    private Workbook templateBook = null;
    // 模板文件Sheet
    private Sheet templateSheet = null;
    private String repeatSheet;
    // value解析异常是否退出
    private boolean valueFailExit = true;

    Workbook hwb = null;
    String key = null;
    // 模板文件
    DocumentMeta template;
    // 导入数据
    Object dataVO;

    public ExcelXlsxService(String title) {
        this.title = title;
    }

    @Override
    public <T> DocVO<T> exportDoc(DocVO<T> doc) throws Exception {
        //1 取得文档模板
        template = this.getDocumentMeta(doc.getDocId());

        InputStream is = null;
        // doc.getDataVO();
        dataVO = null;

        log.info("template.getTemplate():{}", template.getTemplate());
        //2,读取Excel模板
        is = this.getClass().getClassLoader().getResourceAsStream(template.getTemplate());
        try {
            templateBook = WorkbookFactory.create(is);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new Exception("读取模板文件[" + template.getTemplate() + "]失败", e);
        }

        //创建新的EXCEL
        hwb = templateBook;

        //导出数据
        try {
            int templateSheetSize = templateBook.getNumberOfSheets();
            log.debug("template.getTemplateSheetSize():" + templateSheetSize);

            for (int i = 1; i <= templateSheetSize; i++) {
                hwb.setSheetName(i - 1, hwb.getSheetName(i - 1) + "_bak");
            }
            repeatSheet = null;
            log.debug("RepeatSheet ? :" + repeatSheet);
            if (DocConsts.EXCEL_SHEET_REPEAT.equals(repeatSheet)) {
                if (dataVO instanceof List) {
                    for (Object obj : (List) dataVO) {
                        exportToSheet(obj);
                    }
                }
            } else {
                exportToSheet(dataVO);
            }

            //删除备份的模板
            for (int i = 1; i <= templateSheetSize; i++) {
                hwb.removeSheetAt(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception("生成数据失败", e);
        }


        //生成文件名
        if (null == doc.getDocName() || "".equals(doc.getDocName().trim())) {
            String docVersion = doc.getDocVersion();
            if (null == docVersion || "".equals(docVersion.trim())) {
                docVersion = DocCommonUtils.getCurrentTimeStr("yyyyMMddHHmmssSSS");
            }

            StringBuffer filename = new StringBuffer();
            filename.append(template.getId()).append("_").append(template.getName()).append(docVersion);

            if (template.getTemplate().endsWith("xlsx")) {
                filename.append(".xlsx");
            } else {
                filename.append(".xls");
            }

            ;
            doc.setDocName(filename.toString());
        }

        //保存文件
        OutputStream os = null;
        try {
            doc.setFilePath(getStoreFileName(doc));
            os = new FileOutputStream(doc.getFilePath());
            hwb.write(os);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception("保存数据失败", e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
            closeFtp();
        }

        return doc;
    }

    private void exportToSheet(Object data) throws Exception {
        //解析每一个sheet
//		Map<String, DocSheetMeta> sheets = template.getSheetMap();
        List<ExcelSheetMeta> sheetList = template.getSheetList();
        int i = 0;
        for (Iterator<ExcelSheetMeta> it = sheetList.iterator(); it.hasNext(); ) {
            ExcelSheetMeta sheet = it.next();
            key = sheet.getName();
            String sheetName = sheet.getExportSheetName();
            //设置sheet名称
            if (null != sheetName && !"".equals(sheetName.trim())) {
                sheetName = (String) Clazz.getValue(data, sheetName);
            } else {
                sheetName = key;
            }

            if (null == sheetName || "".equals(sheetName.trim())) {
                if (DocConsts.EXCEL_SHEET_REPEAT.equals(repeatSheet)) {
                    sheetName = sheetName + "_" + i;
                } else {
                    sheetName = key;
                }
            }
            i++;

            log.debug("sheetName:{}", sheetName);
            //2,取得sheet
            Sheet excelSheet = hwb.createSheet(sheetName);
            templateSheet = templateBook.getSheet(key + "_bak");

            if (null == templateSheet) {
                log.error("没有找到[" + key + "]对应的sheet");
                throw new Exception("没有找到[" + key + "]对应的sheet");
            }

            //根据模板设置每一列的宽
            for (int cellIndex = 0; cellIndex < ExcelUtils.getRow(templateSheet, 0).getLastCellNum(); cellIndex++) {
                excelSheet.setColumnWidth(cellIndex, templateSheet.getColumnWidth(cellIndex));
            }

            //解析每一个块
            List<ExcelBlockMeta> blockList = sheet.getBlockList();
            Integer lastRowIndex = 0; // 保存上一个块占用的最后一行index
            for (ExcelBlockMeta block : blockList) {
                //放入上一次修改行index
                block.setLastModifyRowIndex(lastRowIndex);
                Integer startRow = block.getRowIndex();

                //拷贝所有操作行的数据、及格式
                if (Integer.valueOf(startRow).compareTo(Integer.valueOf(lastRowIndex) + 1) > 0 || "0".equals(lastRowIndex)) {
                    //copy模板中未用到的行
                    int start = Integer.valueOf(lastRowIndex) + 1;
                    if (Integer.valueOf(lastRowIndex) == 0) {
                        start = 0;
                    }
                    for (; start < Integer.valueOf(startRow); start++) {
                        ExcelUtils.setRowStyle(ExcelUtils.getRow(templateSheet, start), ExcelUtils.getRow(excelSheet, start));
                        block.setLastModifyRowIndex(startRow);
                    }
                }

                //导入数据到块中
                exportToBlock(data, block, excelSheet);
                //导入数据完成后，最后修改行index
                if (null != block.getThisModifyRowIndex()) {
                    lastRowIndex = block.getThisModifyRowIndex();
                }
            }
            if (Integer.valueOf(lastRowIndex).compareTo(templateSheet.getLastRowNum()) < 0) {
                for (int start = Integer.valueOf(lastRowIndex) + 1; start <= templateSheet.getLastRowNum(); start++) {
                    ExcelUtils.setRowStyle(ExcelUtils.getRow(templateSheet, start), ExcelUtils.getRow(excelSheet, start));
                }
            }

        }
    }

    /**
     * 导入数据到每一个块中
     *
     * @throws Exception
     */
    private void exportToBlock(Object dataVO, ExcelBlockMeta block, Sheet mHSSFSheet) throws Exception {
        Row row = null;
        Row rowStyle = null;
        Cell cell = null;

        ExcelBlockMeta.BlockType type = block.getType();
        Integer startRow = block.getRowIndex();
        Integer addRows = block.getAddRows();
        Integer lastModifyRowIndex = block.getLastModifyRowIndex();

        rowStyle = ExcelUtils.getRow(templateSheet, block.getRowIndex());

        if (null == lastModifyRowIndex) {
            lastModifyRowIndex = 0;
        }

        if (null == addRows) {
            addRows = 1;
        }
        // 相对位置: 计算开始位置
        startRow = Integer.valueOf(lastModifyRowIndex) + Integer.valueOf(addRows);

        //System.out.println("startRow:" + startRow);
        Map<String, ExcelColMeta> colsMap = block.getCols();

        //字段属性
        if (ExcelBlockMeta.BlockType.FIELD.equals(type)) {
            row = ExcelUtils.getRow(mHSSFSheet, Integer.valueOf(startRow));
            block.setThisModifyRowIndex(startRow);

            //设置样式
            row = ExcelUtils.setRowStyle(rowStyle, row);

            String key = null;
            //遍历配置文件中块对应的所有列属性
            for (Iterator<String> it = colsMap.keySet().iterator(); it.hasNext(); ) {
                key = it.next();
                ExcelColMeta col = colsMap.get(key);//每一列属性
                String property = col.getProperty();//属性字段
//    			System.out.println("property:" + property);

                //取得属性对应的字段值
                Object rs = null;
                try {
                    rs = Clazz.getValue(dataVO, property);
                } catch (Exception e) {
                    log.error("取[" + property + "]属性值报错", e);
                    throw new Exception("取[" + property + "]属性值报错", e);
                }

                cell = ExcelUtils.getCell(row, Integer.valueOf(col.getColumn()));

                //设置单元格值
                String dataType = col.getDataType();
                String pattern = col.getPattern();
                String codeType = col.getCodeType();

                String value = Clazz.getFieldStrValue(rs, dataType, pattern, codeType);
//    			System.out.println("value:" + value);
                //取得单元格，设置值
                cell.setCellValue(null == value ? "" : value);
            }
        } else if (ExcelBlockMeta.BlockType.TABLE.equals(type)) {
            //取得属性对应的字段值
            List list = null;
            try {
                list = (List) Clazz.getValue(dataVO, block.getList());
            } catch (Exception e) {
                log.error("取[" + block.getList() + "]属性值报错", e);
                throw new Exception("取[" + block.getList() + "]属性值报错", e);
            }

            int i = 0;
            if (null == list || list.isEmpty()) {
                return;
            }
            //遍历对象
            for (Iterator itemIt = list.iterator(); itemIt.hasNext(); ) {
                Object item = itemIt.next();
                int rowIndex = Integer.valueOf(startRow) + i;
                row = ExcelUtils.getRow(mHSSFSheet, rowIndex);

                block.setThisModifyRowIndex(rowIndex);

                //设置样式
                if (i == 0) {
                    ExcelUtils.setRowStyle(rowStyle, row);
                } else {
                    ExcelUtils.setRowStyleFromListFirstRow(ExcelUtils.getRow(mHSSFSheet, Integer.valueOf(startRow)), row);
                }


                i++;

                String key = null;
                //遍历配置文件中块对应的所有列属性
                for (Iterator<String> it = colsMap.keySet().iterator(); it.hasNext(); ) {
                    key = it.next();
                    ExcelColMeta col = colsMap.get(key);//每一列属性
                    String property = col.getProperty();//属性字段

                    //取得属性对应的字段值
                    Object rs = null;
                    try {
                        rs = Clazz.getValue(item, property);
                    } catch (Exception e) {
                        log.error("取[" + property + "]属性值报错", e);
                        throw new Exception("取[" + property + "]属性值报错", e);
                    }
                    cell = ExcelUtils.getCell(row, Integer.valueOf(col.getColumn()));

                    String dataType = col.getDataType();
                    String pattern = col.getPattern();
                    String codeType = col.getCodeType();

                    //取得单元格，设置值
                    cell.setCellValue(Clazz.getFieldStrValue(rs, dataType, pattern, codeType));

//					try{
//						CellRangeAddress array = cell.getArrayFormulaRange();
//						System.out.println(array.formatAsString());// 看看对不对
//					}catch (Exception e){}
                }
            }
        }

        block.setThisModifyRowIndex(mHSSFSheet.getLastRowNum());
    }

    @Override
    public <T> void importFromFile(T dataVO, DocVO<T> doc, File inputFile) {
        //0 检验输入数据
        if (null == doc || null == inputFile || !inputFile.exists() || inputFile.isDirectory()) {
            log.error("{} 配置为空/输入文件为空/文件不存在/文件是目录", title);
            throw new RuntimeException("配置为空/输入文件为空/文件不存在/文件是目录");
        }
        // 按流导入，并自动关闭流
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            importFromInputStream(dataVO, doc, fis);
        } catch (Exception e) {
            throw new RuntimeException("导入异常" + e.getMessage(), e);
        }
    }

    /**
     * 根据文档模板，输入流解析Excel
     *
     * @param dataVo   数据对象
     * @param fis      输入流
     * @param template 模板
     */
    private <T> void parseFromFileInputStream(T dataVo, InputStream fis, DocumentMeta template) {
        Assertions.assertNotNull(dataVo, "数据对象参数不能为空");
        // 使用UserModel方式解析文件
        // 缺点为整体解析，大文件内存占用高，可能出现OOM
        Workbook hwb = null;

        // 1,读取Excel
        try {
            hwb = WorkbookFactory.create(fis);
        } catch (IOException e) {
            log.error("{} 读取Excel异常:{}", title, e.getMessage(), e);
            throw new RuntimeException("读取Excel异常" + e.getMessage());
        }

        // 解析每一个sheet
        List<ExcelSheetMeta> sheetList = template.getSheetList();
        String key = null;
        ExcelSheetMeta sheet;
        for (ExcelSheetMeta excelSheetMeta : sheetList) {
            sheet = excelSheetMeta;
            key = sheet.getName();
            // 2,取得sheet
            Sheet sheetData = hwb.getSheet(key);
            if (null == sheetData) {
                log.error("{} 没有找到[{}]对应的sheet", title, key);
                throw new RuntimeException("没有找到[" + key + "]对应的sheet");
            }
            // 3, 解析每一个块
            List<ExcelBlockMeta> blockList = sheet.getBlockList();
            for (ExcelBlockMeta block : blockList) {
                // 解析每一个块
                parseBlock(dataVo, block, sheetData);
            }
        }
    }

    /**
     * 解析每一个块
     *
     * @param dataVO    数据对象
     * @param blockMeta 块元素
     * @param sheet     excelSheet
     */
    private void parseBlock(Object dataVO, ExcelBlockMeta blockMeta, Sheet sheet) {
        // 字段类型
        if (ExcelBlockMeta.BlockType.FIELD.equals(blockMeta.getType())) {
            this.parseBlockField(dataVO, blockMeta, sheet);
        } else if (ExcelBlockMeta.BlockType.TABLE.equals(blockMeta.getType())) {
            this.parseBlockTable(dataVO, blockMeta, sheet);
        }
    }


    /**
     * 解析静态埠
     *
     * @param dataVO    数据对象
     * @param blockMeta 块配置
     * @param sheet     表格
     */
    private void parseBlockField(Object dataVO, ExcelBlockMeta blockMeta, Sheet sheet) {
        // 得到行数据
        int startIndex = blockMeta.getRowIndex() + (null == blockMeta.getLastModifyRowIndex() ? 0 : blockMeta.getLastModifyRowIndex());
        blockMeta.setThisModifyRowIndex(startIndex);

        Row row = sheet.getRow(startIndex);
        Map<String, ExcelColMeta> docCols = blockMeta.getCols();

        log.info("{} 块[{}]解析开始行[{}] 列数[{}]", title, blockMeta.getName(), startIndex, docCols.size());

        // 解析所有列表
        this.parseAllCell(dataVO, docCols, row, startIndex);
    }

    private void parseAllCell(Object dataObject, Map<String, ExcelColMeta> docCols, Row row, int startIndex) {
        // 循环解析列
        docCols.forEach((key, col) -> {
            //取得单元格
            Cell cell = row.getCell(col.getColumn());
            if (null == cell) {
                log.warn("{} 单元格[{},{}]为空", title, startIndex, col.getColumn());
                return;
            }
            // 判断是否合并单元格
            boolean isMerge = ExcelUtils.isMergedRegion(cell.getSheet(), cell.getRowIndex(), cell.getColumnIndex());
            // 判断是否具有合并单元格
            String value = null;
            if (isMerge) {
                value = ExcelUtils.getMergedRegionValue(cell.getSheet(), cell.getRowIndex(), cell.getColumnIndex());
            } else {
                // 取值
                value = ExcelUtils.getCellValue(cell, col.getPattern());
            }

            // 对象赋值
            try {
                ExcelUtils.setValue(dataObject, col, value);
            } catch (Exception e) {
                String msg = String.format("解析单元格[%s,%s]异常:%s", startIndex, col.getColumn(), e.getMessage());
                if (valueFailExit) {
                    log.error("{} {}", title, msg);
                    throw new RuntimeException(msg, e);
                } else {
                    log.error("{} {}", title, msg, e);
                }
            }
        });
    }

    /**
     * 解析列表块
     *
     * @param dataVO    数据对象
     * @param blockMeta 块配置
     * @param sheet     表格
     */
    private void parseBlockTable(Object dataVO, ExcelBlockMeta blockMeta, Sheet sheet) {
        String subTitle = String.format("%s 列表块[%s]", title, blockMeta.getName());
        int startIndex = blockMeta.getRowIndex() + (null == blockMeta.getLastModifyRowIndex() ? 0 : blockMeta.getLastModifyRowIndex());
        // 最后一行行数
        int lastRow = sheet.getLastRowNum();
        // TODO 表格导入时，最后的行索引(防止表格下方有静态块，出现解析问题)
        Integer endRowIndex = blockMeta.getEndRowIndex();
        // 列表是很多行的数据，遍历
        if (null != endRowIndex) {
            lastRow = endRowIndex;
        }
        Map<String, ExcelColMeta> docCols = blockMeta.getCols();

        List<Object> list = new ArrayList<>();
        for (int i = blockMeta.getRowIndex(); i <= lastRow; i++) {
            //得到行数据
            Row row = sheet.getRow(i);
            if (null == row) {
                continue;
            }
            // 每次都得新建对象: 列表中每行为一个对象
            Object dataObject = null;
            try {
                dataObject = Class.forName(blockMeta.getItemClass()).newInstance();
            } catch (Exception e) {
                log.error("{} 实例行对象失败:{}", subTitle, e.getMessage(), e);
                throw new RuntimeException("生成VO出错" + e.getMessage(), e);
            }
            // 解析所有列表
            this.parseAllCell(dataObject, docCols, row, startIndex);

            // 添加到列表中
            list.add(dataObject);
        }

        // 把List放入对象中
        try {
            ExcelUtils.setValue(dataVO, blockMeta.getList(), list);
        } catch (Exception e) {
            String msg = String.format("块[%s]赋值异常:%s", blockMeta.getName(), e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }

    @Override
    public <T> void importFromInputStream(T dataVo, DocVO<T> doc, InputStream is) {
        //0 检验输入数据
        if (null == doc || null == is) {
            log.error("输入数据为空");
            throw new RuntimeException("输入数据为空");
        }
        log.debug(title);

        parseFromFileInputStream(dataVo, is, this.getDocumentMeta(doc.getDocId()));
    }

    /**
     * @param doc                      文档配置
     * @param is                       输入流
     * @param parseFieldValueErrorExit 解析字段值错误是否退出,默认false;
     */
    @Override
    public <T> void importFromInputStream(T dataVo, DocVO<T> doc, InputStream is, boolean parseFieldValueErrorExit) {
        this.valueFailExit = parseFieldValueErrorExit;
        importFromInputStream(dataVo, doc, is);
    }


}
