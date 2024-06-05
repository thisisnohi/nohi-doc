package nohi.doc.service.impl;

import lombok.extern.slf4j.Slf4j;
import nohi.doc.DocConsts;
import nohi.doc.config.meta.DocumentMeta;
import nohi.doc.config.meta.excel.ExcelBlockMeta;
import nohi.doc.config.meta.excel.ExcelColMeta;
import nohi.doc.config.meta.excel.ExcelSheetMeta;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


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
    // value解析异常是否退出
    private boolean valueFailExit = true;

    String key = null;
    // 模板文件
    DocumentMeta template;
    // 导入数据
    Object dataVO;

    public ExcelXlsxService(String title) {
        this.title = title;
    }

    @Override
    public <T> DocVO<T> exportDoc(DocVO<T> doc) {
        //1 取得文档模板
        template = this.getDocumentMeta(doc.getDocId());
        // 数据对象
        dataVO = doc.getDataVo();
        log.info("{} 模板:{}", title, template.getTemplate());

        //2,读取Excel模板
        InputStream is = ExcelXlsxService.class.getClassLoader().getResourceAsStream(template.getTemplate());
        Assertions.assertNotNull(is, "获取文档[" + doc.getDocId() + "]模板失败");

        try {
            templateBook = WorkbookFactory.create(is);
        } catch (IOException e) {
            log.error("{} 获取模板失败:{}", title, e.getMessage(), e);
            throw new RuntimeException("读取模板文件[" + template.getTemplate() + "]失败", e);
        }

        // 备份模板
        int templateSheetSize = templateBook.getNumberOfSheets();
        log.debug("{} template.getTemplateSheetSize(): {}", title, templateSheetSize);
        for (int i = 1; i <= templateSheetSize; i++) {
            templateBook.setSheetName(i - 1, templateBook.getSheetName(i - 1) + DocConsts.BACKUP_SUFFIX);
        }
        // 导出数据
        try {
            if (dataVO instanceof List) {
                for (Object obj : (List) dataVO) {
                    exportToSheet(obj);
                }
            } else {
                exportToSheet(dataVO);
            }

            // 删除备份的模板
            for (int i = 1; i <= templateSheetSize; i++) {
                templateBook.removeSheetAt(0);
            }
        } catch (Exception e) {
            log.error("{} 导出异常:{}", title, e.getMessage(), e);
            throw new RuntimeException("导出数据失败", e);
        }

        // 生成文件名
        if (StringUtils.isBlank(doc.getDocName())) {
            String docVersion = doc.getDocVersion();
            if (StringUtils.isBlank(docVersion)) {
                docVersion = DocCommonUtils.getCurrentTimeStr("yyyyMMddHHmmssSSS");
            }
            StringBuilder fileName = new StringBuilder();
            fileName.append(template.getId()).append("_").append(template.getName()).append(docVersion);

            if (template.getTemplate().endsWith("xlsx")) {
                fileName.append(".xlsx");
            } else {
                fileName.append(".xls");
            }
            doc.setDocName(fileName.toString());
        }

        //保存文件
        OutputStream os = null;
        try {
            doc.setFilePath(getStoreFileName(doc));
            os = Files.newOutputStream(Paths.get(doc.getFilePath()));
            templateBook.write(os);
        } catch (Exception e) {
            log.error("{} 生成文件[{}]失败:{}", title, doc.getFilePath(), e.getMessage(), e);
            throw new RuntimeException("保存数据失败", e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }

        return doc;
    }

    /**
     * 生成sheet名称
     */
    private String sheetName(ExcelSheetMeta sheet, Object data, int index) {
        String key = sheet.getName();
        String sheetName = sheet.getExportSheetName();
        // 设置sheet名称
        if (StringUtils.isNotBlank(sheetName) && !(data instanceof Collection)) {
            sheetName = (String) Clazz.getValue(data, sheetName);
        }

        if (StringUtils.isBlank(sheetName)) {
            if (index >= 0) {
                sheetName = key + "_" + index;
            } else {
                sheetName = key;
            }
        }
        return sheetName;
    }

    private void exportToSheet(Object data) throws Exception {
        // 解析每一个sheet
        List<ExcelSheetMeta> sheetList = template.getSheetList();
        int i = 0;
        for (ExcelSheetMeta sheet : sheetList) {
            // 根据配置，获取模板sheet
            key = sheet.getName();
            templateSheet = templateBook.getSheet(key + DocConsts.BACKUP_SUFFIX);
            if (null == templateSheet) {
                log.error("{} 没有找到[{}]sheet页", title, key);
                throw new RuntimeException("没有找到[" + key + "]sheet页");
            }

            // sheet页数据对象字段
            String sheetDataField = sheet.getSheetData();
            String sheetType = sheet.getType();
            // sheet页数据对象，如果sheetDataField为空(未配置)，则sheetData对象同document对象
            Object sheetData = ExcelUtils.getSheetDataVo(data, sheetDataField);
            log.debug("{} key:{} sheetType:{} sheetDataField:{} sheetData:{}", title, key, sheetType, sheetDataField, sheetData.getClass());

            // 重复sheet页, 且数据对象为集合，重复导出数据
            if (DocConsts.EXCEL_SHEET_REPEAT.equalsIgnoreCase(sheetType) && sheetData instanceof Collection) {
                // 循环导出数据到sheet页
                int sheetIndex = 0;
                for (Object obj : (Collection) sheetData) {
                    String sheetName = this.sheetName(sheet, obj, sheetIndex++);
                    log.info("{}, sheetName:{} sheetData:{}", title, sheetName, sheetDataField);
                    this.exportSheet(sheet, sheetName, obj);
                }
            }else {
                String sheetName = this.sheetName(sheet, sheetData, -1);
                log.info("{}, sheetName:{} sheetData:{}", title, sheetName, sheetDataField);
                // 导出数据到sheet页
                this.exportSheet(sheet, sheetName, sheetData);
            }
        }
    }

    private void exportSheet(ExcelSheetMeta sheet, String sheetName, Object sheetData) throws Exception {
        //2 创建sheet页
        Sheet excelSheet = templateBook.createSheet(sheetName);

        // 根据模板设置每一列的宽
        for (int cellIndex = 0; cellIndex <= ExcelUtils.getRow(templateSheet, 0).getLastCellNum(); cellIndex++) {
            excelSheet.setColumnWidth(cellIndex, templateSheet.getColumnWidth(cellIndex));
        }

        // 解析每一个块
        List<ExcelBlockMeta> blockList = sheet.getBlockList();

        // 上一块
        ExcelBlockMeta lastBlock = null;

        // 导出
        if (sheetData instanceof Collection) {
            log.info("{} 循环导出", title);
            int dataIndex = 0;
            for (Object obj : (Collection) sheetData) {
                log.info("{} 循环导出,第[{}]份", title, dataIndex);
                dataIndex++;
                lastBlock = this.exportBlockList(lastBlock, blockList, excelSheet, obj);
            }
        } else {
            lastBlock = this.exportBlockList(lastBlock, blockList, excelSheet, sheetData);
        }

        // 导入数据完成后，最后修改行index
        Integer lastBlockRowIndex = (null == lastBlock ? 0 : lastBlock.getRowIndex());

        // 拷贝模板最后的样式
        if (lastBlockRowIndex.compareTo(templateSheet.getLastRowNum()) < 0) {
            int offset = null == lastBlock || null == lastBlock.getLastModifyRowIndex() ? 0 : (lastBlock.getLastModifyRowIndex() - lastBlockRowIndex);
            int start = lastBlockRowIndex + 1;
            int templateLastRowIndex = templateSheet.getLastRowNum();
            log.debug("{} 最后拷贝样式[{} - {}] offset:{}", title, start, templateLastRowIndex, offset);
            for (; start <= templateLastRowIndex; start++) {
                log.debug("{} 最后拷贝样式[{} to {}]", title, start, start + offset);
                ExcelUtils.setRowStyle(ExcelUtils.getRow(templateSheet, start), ExcelUtils.getRow(excelSheet, start + offset));
            }
        }
    }

    private ExcelBlockMeta exportBlockList(ExcelBlockMeta lastBlock, List<ExcelBlockMeta> blockList, Sheet excelSheet, Object sheetData) throws Exception {
        for (ExcelBlockMeta block : blockList) {
            Integer blockRowIndex = block.getRowIndex();
            // 从-1开始计算
            Integer lastBlockRowIndex = null;
            if (null == lastBlock) {
                lastBlockRowIndex  = -1;
                block.setLastBlockLastModifyRowIndex(-1);
            } else {
                lastBlockRowIndex  = lastBlock.getRowIndex();
                block.setLastBlockLastModifyRowIndex(lastBlock.getLastModifyRowIndex());
            }

            if (block.getAddRows() == null) {
                block.setAddRows(blockRowIndex - lastBlockRowIndex);
            }

            log.debug("{} 开始导出[{}]行[{}] lastRowIndex[{}]", title, block.getName(), blockRowIndex, lastBlockRowIndex);
            // 拷贝所有操作行的数据、及格式
            if (blockRowIndex.compareTo(lastBlockRowIndex + 1) > 0) {
                // copy模板中未用到的行
                int start = lastBlockRowIndex + 1;
//                if (0 == lastBlockRowIndex) {
//                    start = 0;
//                }
                int offset = block.getLastBlockLastModifyRowIndex() - lastBlockRowIndex;
                log.debug("{} [{}]开始拷贝样式[{}-{}) offset:{}", title, block.getName(), start, blockRowIndex, offset);
                for (; start < blockRowIndex; start++) {
                    ExcelUtils.setRowStyle(ExcelUtils.getRow(templateSheet, start), ExcelUtils.getRow(excelSheet, start + offset));
                }
            }

            //导入数据到块中
            exportToBlock(sheetData, block, excelSheet);
            // 保留最后块
            lastBlock = block;
        }
        return lastBlock;
    }

    /**
     * 导入数据到每一个块中
     */
    private void exportToBlock(Object dataVO, ExcelBlockMeta block, Sheet mHSSFSheet) throws Exception {
        Row row = null;
        Row rowStyle = null;
        Cell cell = null;

        ExcelBlockMeta.BlockType type = block.getType();
        Integer startRow = block.getRowIndex();
        Integer addRows = block.getAddRows();
        // 上一块占用最后一行索引
        Integer lastModifyRowIndex = block.getLastBlockLastModifyRowIndex();
        // 根据配置索引，获取行的样式
        rowStyle = ExcelUtils.getRow(templateSheet, block.getRowIndex());

        if (null == lastModifyRowIndex) {
            lastModifyRowIndex = -1;
        }

        if (null == addRows) {
            addRows = 1;
        }
        // 相对位置: 计算开始位置
        startRow = lastModifyRowIndex + addRows;
        log.debug("{}, type[{}],startRow:{}, lastModifyRowIndex:{} addRows:{} copyStyle:{}", title,type, startRow, lastModifyRowIndex, addRows, block.isCopyStyle());

        Map<String, ExcelColMeta> colsMap = block.getCols();

        // 字段属性
        if (ExcelBlockMeta.BlockType.FIELD.equals(type)) {
            row = ExcelUtils.getRow(mHSSFSheet, startRow);
            block.setLastModifyRowIndex(startRow);

            //设置样式
            row = ExcelUtils.setRowStyle(rowStyle, row);

            String key = null;
            // 遍历配置文件中块对应的所有列属性
            for (Iterator<String> it = colsMap.keySet().iterator(); it.hasNext(); ) {
                key = it.next();
                // 每一列属性
                ExcelColMeta col = colsMap.get(key);
                // 属性字段
                String property = col.getProperty();

                // 取得属性对应的字段值
                Object rs = null;
                try {
                    rs = Clazz.getValue(dataVO, property);
                } catch (Exception e) {
                    log.error("{} 取[{}]属性值报错", title, property, e);
                    throw new Exception("取[" + property + "]属性值报错", e);
                }

                cell = ExcelUtils.getCell(row, col.getColumn());
                // 设置单元格值
                ExcelUtils.setExcelCellValue(cell, col, rs);
            }
        } else if (ExcelBlockMeta.BlockType.TABLE.equals(type)) {
            //取得属性对应的字段值
            List list = null;
            try {
                list = (List) Clazz.getValue(dataVO, block.getList());
            } catch (Exception e) {
                log.error("{} 取[{}]属性值报错", title, block.getList(), e);
                throw new Exception("取[" + block.getList() + "]属性值报错", e);
            }

            int i = 0;
            if (null == list || list.isEmpty()) {
                return;
            }
            //遍历对象
            for (Iterator itemIt = list.iterator(); itemIt.hasNext(); ) {
                Object item = itemIt.next();
                int rowIndex = startRow + i;
                row = ExcelUtils.getRow(mHSSFSheet, rowIndex);

                block.setLastModifyRowIndex(rowIndex);

                //设置样式
                if (block.isCopyStyle()) {
                    if (i == 0) {
                        ExcelUtils.setRowStyle(rowStyle, row);
                    } else {
                        ExcelUtils.setRowStyleFromListFirstRow(ExcelUtils.getRow(mHSSFSheet, startRow), row);
                    }
                }

                i++;
                if (i % 1000 == 0) {
                    log.debug("{} 第[{}]条数据", title, i);
                }
                // 遍历配置文件中块对应的所有列属性
                for (Iterator<String> it = colsMap.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    // 每一列属性
                    ExcelColMeta col = colsMap.get(key);
                    // 属性字段
                    String property = col.getProperty();

                    //取得属性对应的字段值
                    Object rs = null;
                    try {
                        rs = Clazz.getValue(item, property);
                    } catch (Exception e) {
                        log.error("{} 取[{}]属性值报错:{}", title, property, e.getMessage(), e);
                        throw new RuntimeException("取[" + property + "]属性值报错", e);
                    }
                    cell = ExcelUtils.getCell(row, col.getColumn());
                    // 设置单元格值
                    ExcelUtils.setExcelCellValue(cell, col, rs);

//					try{
//						CellRangeAddress array = cell.getArrayFormulaRange();
//						System.out.println(array.formatAsString());// 看看对不对
//					}catch (Exception e){}
                }
            }
        }

        block.setLastModifyRowIndex(mHSSFSheet.getLastRowNum());
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
            log.info("{} 解析sheet[{}]", title, key);
            // 2,取得sheet
            Sheet sheetData = hwb.getSheet(key);
            if (null == sheetData) {
                log.error("{} 没有找到[{}]对应的sheet", title, key);
                throw new RuntimeException("没有找到[" + key + "]对应的sheet");
            }
            // 3, 解析每一个块
            List<ExcelBlockMeta> blockList = sheet.getBlockList();
            Object sheetDataObject = null;
            try {
                sheetDataObject = ExcelUtils.getSheetDataVo(dataVo, sheet.getSheetData());
            } catch (Exception e) {
                log.error("{} 获取sheet数据对象[{}]失败：{}", title, sheet.getSheetData(), e.getMessage(), e);
                throw new RuntimeException("获取sheet数据对象[" + sheet.getSheetData() + "]失败");
            }
            for (ExcelBlockMeta block : blockList) {
                // 解析每一个块
                parseBlock(sheetDataObject, block, sheetData);
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
        //int startIndex = blockMeta.getRowIndex() + (null == blockMeta.getLastModifyRowIndex() ? 0 : blockMeta.getLastModifyRowIndex());
        int startIndex = blockMeta.getRowIndex();
        blockMeta.setLastModifyRowIndex(startIndex);

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
        //int startIndex = blockMeta.getRowIndex() + (null == blockMeta.getLastModifyRowIndex() ? 0 : blockMeta.getLastModifyRowIndex());
        int startIndex = blockMeta.getRowIndex();
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
