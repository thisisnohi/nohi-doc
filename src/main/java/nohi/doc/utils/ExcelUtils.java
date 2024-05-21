package nohi.doc.utils;

import lombok.extern.slf4j.Slf4j;
import nohi.doc.config.meta.excel.ExcelColMeta;
import nohi.doc.service.CodeEncode;
import nohi.utils.Clazz;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nohi on 2017/11/21.
 */
@Slf4j
public class ExcelUtils {
    // 创建 Pattern 对象
    static Pattern pattern = Pattern.compile("[A-Z]+\\d+");

    // 设置样式
    public static Row setRowStyleFromListFirstRow(Row styleRow, Row row) {
        if (null != styleRow && null != row) {
            int styleRowNum = styleRow.getRowNum();
            int editRowNum = row.getRowNum();

            for (int i = 0; i < styleRow.getSheet().getNumMergedRegions(); i++) {
                CellRangeAddress ranage = styleRow.getSheet().getMergedRegion(i);
                int firstRow = ranage.getFirstRow();
                int loastRow = ranage.getLastRow();

                if (styleRowNum == ranage.getFirstRow()) {
                    CellRangeAddress newRanage = new CellRangeAddress(firstRow + (editRowNum - styleRowNum), loastRow + (editRowNum - styleRowNum), ranage.getFirstColumn(), ranage.getLastColumn());
                    row.getSheet().addMergedRegion(newRanage);
                }
            }
            row.setHeight(styleRow.getHeight());
            for (int i = 0; i < styleRow.getLastCellNum(); i++) {
                Cell from = getCell(styleRow, i);
                Cell to = getCell(row, i);
                CellStyle oldStyle = from.getCellStyle();
                to.setCellStyle(oldStyle);
                String ss = getCellValue(from);
                to.setCellValue(ss == null ? "" : ss.trim());

                //设置下拉框等验证信息
                setValidatation(from, to);
                copyCellValue(from, to);
            }
        }
        return row;
    }


    public static Cell getCell(Row row, int rol) {
        if (rol >= row.getLastCellNum()) {
            return row.createCell(rol);
        } else {
            if (null == row.getCell(rol)) {
                return row.createCell(rol);
            }
            return row.getCell(rol);
        }
    }

    /**
     * 取得单元格值
     *
     * @param cell 单元格
     */
    public static String getCellValue(Cell cell) {
        return getCellValue(cell, null);
    }

    /**
     * 取得单元格值
     *
     * @param cell 单元格
     */
    public static String getCellValue(Cell cell, String pattern) {
        if (null == cell) {
            return "";
        }

        String subTitle = String.format("sheet[%s][%s-%s]", cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex());
        String value = null;
        switch (cell.getCellType()) {
            // 数值型
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    LocalDateTime dateTime = cell.getLocalDateTimeCellValue();
                    if (null != dateTime) {
                        if (StringUtils.isNotBlank(pattern)) {
                            value = dateTime.format(DateTimeFormatter.ofPattern(pattern));
                        } else {
                            value = dateTime.toString();
                        }
                    }
                } else {//纯数字
                    value = String.valueOf(cell.getNumericCellValue());
                    //如果数值类型的，如果小数点后只有一个0,取整
                    int index = value.lastIndexOf(".");
                    if (index >= 1) {
                        String prefix = value.substring(index);
                        if (".0".equals(prefix)) {
                            return value.substring(0, index);
                        }
                    }
                }
                break;
            /* 此行表示单元格的内容为string类型 */
            case STRING: // 字符串型
                value = cell.getRichStringCellValue().toString();
                break;
            case FORMULA://公式型
                // 读公式计算值
                try {
                    value = cell.getStringCellValue();
                    // 如果获取的数据值为非法值,则转换为获取字符串
                    if (value.equals("NaN")) {
                        value = cell.getRichStringCellValue().toString();
                    }
                } catch (Exception e) {
                    log.warn("{} 获取公式失败:{}", subTitle, e.getMessage(), e);
                    try {
                        value = String.valueOf(cell.getNumericCellValue());
                    } catch (Exception a) {
                        log.warn("{} 获取数值失败:{}", subTitle, a.getMessage(), a);
                    }
                }
                break;
            case BOOLEAN:// 布尔
                value = " " + cell.getBooleanCellValue();
                break;
            /* 此行表示该单元格值为空 */
            case BLANK: // 空值
                value = "";
                break;
            case ERROR: // 故障
                value = "";
                break;
            default:
                value = cell.getRichStringCellValue().toString();
        }
        return value;
    }


    /**
     * 获取合并单元格的值
     *
     * @param sheet  sheet
     * @param row    行
     * @param column 列
     * @return 字符串
     */
    public static String getMergedRegionValue(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();

        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell);
                }
            }
        }

        return null;
    }

    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet
     * @param row    行下标
     * @param column 列下标
     * @return
     */
    public static boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据单元格配置，给数据对象字段赋值
     *
     * @param obj  数据对象
     * @param col  单元格配置
     * @param temp 值字符串
     */
    public static void setValue(Object obj, ExcelColMeta col, String temp) {
        // 给对象set
        String fieldName = col.getProperty();
        String pattern = col.getPattern();
        String codeType = col.getCodeType();
        String title = String.format("属性[%s] 值[%s]", fieldName, temp) + (null == codeType ? "" : "CodeType[" + codeType + "]") + (null == pattern ? "" : "Pattern[" + pattern + "]");
        // 格式化字符串
        log.debug("{}", title);
        if (StringUtils.isBlank(temp)) {
            return;
        }
        // 转换数据字典
        // TODO 考虑系统接口实例
        // 接口： Class.static method value2Key(codeType, codeValue) 返回 codeKey
        // 接口： Class.static method key2Value(codeType, codeKey) 返回 codeValue
        if (StringUtils.isNotBlank(codeType)) {
            temp = CodeEncode.getMappingValue(codeType, temp);
        }
        // 根据value中的值，对对象层层设值
        try {
            setValue(obj, fieldName, temp, pattern);
        } catch (Exception e) {
            throw new RuntimeException("字段[" + fieldName + "]赋值失败", e);
        }
    }

    /**
     * 通过反射给对象设置值
     *
     * @param obj   数据对象
     * @param value 值
     */
    public static void setValue(Object obj, String fieldName, Object value) throws Exception {
        setValue(obj, fieldName, value, null);
    }

    /**
     * 通过反射给对象设置值
     *
     * @param obj   数据对象
     * @param value 值
     */
    public static void setValue(Object obj, String fieldName, Object value, String pattern) throws Exception {
        String title = String.format("属性[%s],值[%s]", fieldName, value);
        if (null == obj) {
            log.warn("{} 数据对象为空", title);
            return;
        }
        if (StringUtils.isBlank(fieldName)) {
            log.warn("{} 对应的字段的字段名为空", title);
            return;
        }


        // 用正则，点是正则的关键字，必须转义
        String[] vm = fieldName.split("\\.");
        int index = fieldName.indexOf(".");

        // 没有嵌套对象
        if (index == -1) {
            Field field = obj.getClass().getDeclaredField(fieldName);

            if (null == value) {
                log.warn("{} 对应的字段[{}]的值为空", title, fieldName);
                return;
            }
            // 获取get方法
            Method method = Clazz.getMethod(obj.getClass(), field, Clazz.METHOD_TYPE_SET);

            if (value instanceof String) {
                // 根据属性类型，转换String为对应的类型的值
                Object fieldValue = Clazz.convertString2FieldType(field, (String) value, pattern);
                method.invoke(obj, fieldValue);
            } else {
                method.invoke(obj, value);
            }
        } else {
            // 多层嵌套对象
            Method method = Clazz.getMethod(obj.getClass(), vm[0], Clazz.METHOD_TYPE_GET, null);
            Object temp = method.invoke(obj);
            if (null == temp) {
                try {
                    temp = method.getReturnType().newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException("实例[" + method.getReturnType() + "]失败", e);
                }
            }

            // 循环嵌套调用
            setValue(temp, fieldName.substring(index + 1), value, pattern);

            // 该对象的set方法
            method = Clazz.getMethod(obj.getClass(), vm[0], Clazz.METHOD_TYPE_SET, temp.getClass());
            method.invoke(obj, temp);
        }
    }

    public static Row getRow(Sheet mHSSFSheet, int row) {
        if (row > mHSSFSheet.getLastRowNum()) {
            return mHSSFSheet.createRow(row);
        } else {
            if (null == mHSSFSheet.getRow(row)) {
                return mHSSFSheet.createRow(row);
            }
            return mHSSFSheet.getRow(row);
        }
    }

    // 设置样式
    public static Row setRowStyle(Row styleRow, Row row) {
        if (null != styleRow && null != row) {
            int styleRowNum = styleRow.getRowNum();
            int editRowNum = row.getRowNum();

            for (int i = 0; i < styleRow.getSheet().getNumMergedRegions(); i++) {
                CellRangeAddress ranage = styleRow.getSheet().getMergedRegion(i);
                int firstRow = ranage.getFirstRow();
                int loastRow = ranage.getLastRow();

                if (styleRowNum == ranage.getFirstRow()) {
                    CellRangeAddress newRanage = new CellRangeAddress(firstRow + (editRowNum - styleRowNum), loastRow + (editRowNum - styleRowNum), ranage.getFirstColumn(), ranage.getLastColumn());
                    row.getSheet().addMergedRegion(newRanage);
                }
            }
            row.setHeight(styleRow.getHeight());

            Drawing drawing = row.getSheet().createDrawingPatriarch();
            Workbook workbook = row.getSheet().getWorkbook();
            DataValidationHelper helper = row.getSheet().getDataValidationHelper();

            for (int i = 0; i < styleRow.getLastCellNum(); i++) {
//				CellStyle style = row.getSheet().getWorkbook().createCellStyle();
//				Cell cell = ExcelUtils.getCell(styleRow, i);
//
//				CellStyle oldStyle = cell.getCellStyle();
//				style.cloneStyleFrom(oldStyle);
//				ExcelUtils.getCell(row, i).setCellStyle(style);
//				System.out.println("cellcomment:" + cell.getCellComment());
//				ExcelUtils.getCell(row, i).setCellComment( cell.getCellComment() );
//				if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
//					ExcelUtils.getCell(row, i).setCellType(Cell.CELL_TYPE_FORMULA);
//					ExcelUtils.getCell(row, i).setCellFormula( ExcelUtils.getCell(styleRow, i).getCellFormula() );
//				}
//
//				String ss = ExcelUtils.getCellValue(ExcelUtils.getCell(styleRow, i));
//				ExcelUtils.getCell(row, i).setCellValue(ss == null ? "" : ss.trim());
                copyCell(ExcelUtils.getCell(styleRow, i), ExcelUtils.getCell(row, i), drawing, workbook.getClass());
            }
        }
        return row;
    }

    public static void setValidatation(Cell fromCell, Cell toCell) {
        //拷贝验证
        if (null != fromCell && null != toCell && fromCell.getSheet() instanceof HSSFSheet) {
            List<HSSFDataValidation> validationList = getDataValidationConstraint(fromCell.getSheet(), fromCell.getRowIndex(), fromCell.getColumnIndex());
            if (null != validationList) {

                for (HSSFDataValidation item : validationList) {
                    DataValidationConstraint dvc = item.getValidationConstraint();
                    int validationType = dvc.getValidationType();
                    int operatorType = dvc.getOperator();
                    String vMin = dvc.getFormula1();
                    String vMax = dvc.getFormula2();
                    DataValidationConstraint dvConstraint = null;
                    //System.out.println("validationType:" + validationType + ",operatorType:" + operatorType);
                    //System.out.println("vMin:" + vMin + ",vMax:" + vMax);
                    DataValidationHelper dvHelper = toCell.getSheet().getDataValidationHelper();
                    if (validationType == DataValidationConstraint.ValidationType.TEXT_LENGTH) {
                        dvConstraint = dvHelper.createTextLengthConstraint(operatorType, vMin, vMax);
                    } else if (validationType == DataValidationConstraint.ValidationType.DECIMAL) {
                        dvConstraint = dvHelper.createDecimalConstraint(operatorType, vMin, vMax);
                    } else if (validationType == DataValidationConstraint.ValidationType.INTEGER) {
                        dvConstraint = dvHelper.createIntegerConstraint(operatorType, vMin, vMax);
                    } else if (validationType == DataValidationConstraint.ValidationType.DATE) {
                        dvConstraint = dvHelper.createDateConstraint(operatorType, vMin, vMax, null);
                    } else if (validationType == DataValidationConstraint.ValidationType.TIME) {
                        dvConstraint = dvHelper.createTimeConstraint(operatorType, vMin, vMax);
                    } else if (validationType == DataValidationConstraint.ValidationType.FORMULA) {
                        dvConstraint = dvHelper.createFormulaListConstraint(dvc.getFormula1());
                    } else if (validationType == DataValidationConstraint.ValidationType.LIST) {
                        if (null != vMin) {
                            vMin = vMin.replaceAll("\"", "");
                            String[] dlist = vMin.split(",");
                            dvConstraint = dvHelper.createExplicitListConstraint(dlist);
                        }
                    }
                    if (null == dvConstraint) {
                        continue;
                    }
                    CellRangeAddressList addressList = new CellRangeAddressList(toCell.getRowIndex(), toCell.getRowIndex(), toCell.getColumnIndex(), toCell.getColumnIndex());
                    DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
                    //设置出错提示信息
                    validation.setSuppressDropDownArrow(true);
                    validation.setShowErrorBox(true);
//					setDataValidationErrorMessage(validation, errorTitle, errorMsg);
                    toCell.getSheet().addValidationData(validation);
                }
            }
        }

    }

    private static void copyCell(Cell fromCell, Cell toCell, Drawing drawing, Class<? extends Workbook> clazz) {
        //设置下拉框等验证信息
        setValidatation(fromCell, toCell);

        fromCell.getSheet().createDrawingPatriarch();
        if (fromCell.getSheet().getWorkbook() == toCell.getSheet().getWorkbook()) {// 同一个excel可以直接复制cellStyle
            toCell.setCellStyle(fromCell.getCellStyle());
        } else {
            // 不同的excel之间，需要调用cloneStyleFrom方法
            CellStyle newCellStyle = toCell.getSheet().getWorkbook().createCellStyle();
            newCellStyle.cloneStyleFrom(fromCell.getCellStyle());
            toCell.setCellStyle(newCellStyle);
        }
        copyCellValue(fromCell, toCell);
        Comment fromComment = fromCell.getCellComment();
        if (fromComment != null) {
            copyComment(clazz, fromComment, drawing, fromCell, toCell);
        }
    }

    public static void copyCellValue(Cell from, Cell to) {
        switch (from.getCellType()) {
            case STRING:
                to.setCellValue(from.getStringCellValue());
                break;
            case NUMERIC:
                to.setCellValue(from.getNumericCellValue());
                break;
            case BLANK:
                to.setCellType(CellType.BLANK);
                break;
            case BOOLEAN:
                to.setCellValue(from.getBooleanCellValue());
                break;
            case ERROR:
                to.setCellErrorValue(from.getErrorCellValue());
                break;
            case FORMULA:
                String formula = from.getCellFormula();
                if (null != formula) {
                    int from_row = from.getRowIndex();
                    int from_col = from.getColumnIndex();
                    int to_row = to.getRowIndex();
                    int to_col = to.getColumnIndex();
                    Matcher m = pattern.matcher(formula);
                    while (m.find()) {
                        String str = m.group(0);
                        String ziMu = null;
                        String indexStr = null;

                        if (null != str) {
                            indexStr = str.replaceAll("[^\\d]", "");
                            ziMu = str.replaceAll("\\d", "");
                        }
                        if (null != indexStr) {
                            int index = Integer.valueOf(indexStr);

                            if (index < (to_row + 1)) {
                                formula = formula.replaceAll(str, ziMu + (to_row + 1));
                            }
                        }
                    }
                    formula = formula.replaceAll("_bak!", "!");//解决从bak文件解析问题
                }
                to.setCellFormula(formula);
                break;
            default:
                break;
        }
    }

    private static void copyComment(Class<? extends Workbook> clazz, Comment fromComment, Drawing drawing, Cell fromCell, Cell toCell) {
        Comment newComment = drawing.createCellComment(createClientAnchor(clazz));
        newComment.setAuthor(fromComment.getAuthor());
        newComment.setColumn(fromComment.getColumn());
        newComment.setRow(fromComment.getRow());
        newComment.setString(fromComment.getString());
        newComment.setVisible(fromComment.isVisible());
        toCell.setCellComment(newComment);
    }

    private static ClientAnchor createClientAnchor(Class<? extends Workbook> clazz) {
        ClientAnchor aClientAnchor = null;
        String s = clazz.getSimpleName();
        if (s.equals("HSSFWorkbook")) {
            aClientAnchor = new HSSFClientAnchor();

        } else {
            aClientAnchor = new HSSFClientAnchor();

        }
        return aClientAnchor;
    }

    public static List<HSSFDataValidation> getDataValidationConstraint(Sheet sheet, int row, int col) {
        if (null == sheet) return null;

        List<HSSFDataValidation> rs = new ArrayList();
        List<HSSFDataValidation> validations = (List<HSSFDataValidation>) sheet.getDataValidations();
        if (null != validations) {
            for (HSSFDataValidation item : validations) {
                CellRangeAddressList cral = item.getRegions();
                CellRangeAddress[] craArray = cral.getCellRangeAddresses();
                boolean isIn = false;
                for (CellRangeAddress cra : craArray) {
                    int f_c = cra.getFirstColumn();
                    int f_r = cra.getFirstRow();
                    int t_c = cra.getLastColumn();
                    int t_r = cra.getLastRow();

                    if (row >= f_r && row <= t_r && col >= f_c && col <= t_c) {
                        isIn = true;
                        break;
                    }
                }

                if (isIn) {
                    rs.add(item);
                }
            }
        }
        return rs;
    }

    public String getColString(int colIndex) {
        int first = colIndex / 26;
        int index = colIndex % 26;

        System.out.println("first:" + first + ",index:" + index);

        String rs = String.valueOf((char) ('A' + (first - 1)));//首字母
        if (colIndex < 26) {
            return String.valueOf((char) ('A' + index));
        } else {
            return rs + String.valueOf((char) ('A' + index));//首字母;
        }
    }

    /**
     * 设置单元格值
     */
    public static void setExcelCellValue(Cell cell, ExcelColMeta colMeta, Object data) {
        // 设置单元格值
        String dataType = colMeta.getDataType();
        String pattern = colMeta.getPattern();
        String codeType = colMeta.getCodeType();

        if (null == data) {
            return;
        }
        // 如果设置了字段类型，按字段类型设置单元格的值
        if ("int".equalsIgnoreCase(dataType) || "Integer".equalsIgnoreCase(dataType) || data instanceof Integer) {
            Integer in = (Integer) data;
            cell.setCellValue(in);
        } else if ("double".equalsIgnoreCase(dataType) || data instanceof Double) {
            Double d = (Double) data;
            cell.setCellValue(d);
        } else if ("float".equalsIgnoreCase(dataType) || data instanceof Float) {
            Float d = (Float) data;
            cell.setCellValue(d);
        }else if ("BigDecimal".equalsIgnoreCase(dataType) || data instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) data;
            cell.setCellValue(bd.doubleValue());
        } else if ("timestamp".equalsIgnoreCase(dataType) || data instanceof Timestamp) {
            Timestamp bd = (Timestamp) data;
            // 如果单元格为字符串，则转换单元格值为字符串
            if (cell.getCellType().equals(CellType.STRING)) {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern == null ? "yyyyMMdd" : pattern);
                cell.setCellValue(sdf.format(bd));
            } else {
                cell.setCellValue(bd);
            }
        } else if ("date".equalsIgnoreCase(dataType) || data instanceof Date) {
            Date bd = (Date) data;
            // 如果单元格为字符串，则转换单元格值为字符串
            if (cell.getCellType().equals(CellType.STRING)) {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern == null ? "yyyyMMdd" : pattern);
                cell.setCellValue(sdf.format(bd));
            } else {
                cell.setCellValue(bd);
            }
        } else {
            cell.setCellValue(data.toString());
            if (StringUtils.isNotBlank(codeType)) {
                String s = CodeEncode.getMappingValue(codeType, data.toString());
                if (StringUtils.isNotBlank(s)) {
                    cell.setCellValue(s);
                }
            }
        }
    }
}

