package nohi.doc.utils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nohi.doc.meta.DocColMeta;
import nohi.doc.service.CodeEncode;
import nohi.utils.Clazz;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.junit.Test;

/**
 * Created by nohi on 2017/11/21.
 */
public class ExcelUtils {
	private static Logger log = Logger.getLogger(ExcelUtils.class);
	// 创建 Pattern 对象
	static Pattern pattern = Pattern.compile("[A-Z]+\\d+");

	//设置样式
	public static Row setRowStyleFromListFirstRow(Row styleRow, Row row){
		if (null != styleRow && null != row) {
			int styleRowNum = styleRow.getRowNum();
			int editRowNum = row.getRowNum();

			for (int i = 0 ; i < styleRow.getSheet().getNumMergedRegions();i++) {
				CellRangeAddress ranage = styleRow.getSheet().getMergedRegion(i);
				int firstRow = ranage.getFirstRow();
				int loastRow = ranage.getLastRow();

				if (styleRowNum == ranage.getFirstRow()) {
					CellRangeAddress newRanage = new CellRangeAddress(firstRow + (editRowNum - styleRowNum) , loastRow + (editRowNum - styleRowNum), ranage.getFirstColumn(), ranage.getLastColumn());
					row.getSheet().addMergedRegion(newRanage);
				}
			}
			row.setHeight(styleRow.getHeight());
			for (int i = 0 ; i < styleRow.getLastCellNum(); i ++) {
				Cell from = getCell(styleRow, i);
				Cell to = getCell(row, i);
				CellStyle oldStyle = from.getCellStyle();
				to.setCellStyle(oldStyle);
				String ss = getCellValue(from);
				to.setCellValue(ss == null ? "" : ss.trim());

				//设置下拉框等验证信息
				setValidatation( from ,to );
				copyCellValue( from ,to );
			}
		}
		return row;
	}



	public static Cell getCell(Row row, int rol){
		if (rol >= row.getLastCellNum()) {
			return row.createCell(rol);
		}else {
			if (null == row.getCell(rol)) {
				return row.createCell(rol);
			}
			return row.getCell(rol);
		}
	}

	/**
	 * 取得单元格值
	 * @param cell
	 * @return
	 */
	public static String getCellValue(Cell cell){
		String value = null;
		if (null == cell) {
			return "";
		}
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC: // 数值型
				if (HSSFDateUtil.isCellDateFormatted( cell )) {
					value = DateUtil.getJavaDate( cell.getNumericCellValue() ).toString();
				} else {//纯数字
					value = String.valueOf( cell.getNumericCellValue() );
					//如果数值类型的，如果小数点后只有一个0,取整
					if (null != value) {
						int index = value.lastIndexOf( "." );
						if (index >= 1) {
							String prefix = value.substring( index );
							if (".0".equals( prefix )) {
								return value.substring( 0, index );
							}
						}
					}
				}
				break;
		/* 此行表示单元格的内容为string类型 */
			case Cell.CELL_TYPE_STRING: // 字符串型
				value = cell.getRichStringCellValue().toString();
				break;
			case Cell.CELL_TYPE_FORMULA://公式型
				//读公式计算值
				try {
					value = cell.getStringCellValue();
					if (value.equals( "NaN" )) {//如果获取的数据值为非法值,则转换为获取字符串
						value = cell.getRichStringCellValue().toString();
					}
				} catch (Exception e) {
					log.error( "sheetName:" + cell.getSheet().getSheetName()  + ",rowIndex:" + cell.getRowIndex() + ",columnIndex:" + cell.getColumnIndex()  );
					log.error( e.getMessage(), e );
					try {
						value = String.valueOf( cell.getNumericCellValue() );
					} catch (Exception a) {
						log.error( a.getMessage(), a );
					}
				}

				break;
			case Cell.CELL_TYPE_BOOLEAN://布尔
				value = " " + cell.getBooleanCellValue();
				break;
		/* 此行表示该单元格值为空 */
			case Cell.CELL_TYPE_BLANK: // 空值
				value = "";
				System.out.print( value );
				break;
			case Cell.CELL_TYPE_ERROR: // 故障
				value = "";
				break;
			default:
				value = cell.getRichStringCellValue().toString();
		}
		return value;
	}

	/**
	 * 获取合并单元格的值
	 * @param sheet
	 * @param row
	 * @param column
	 * @return
	 */
	public static  String getMergedRegionValue(Sheet sheet , int row , int column){
		int sheetMergeCount = sheet.getNumMergedRegions();

		for(int i = 0 ; i < sheetMergeCount ; i++){
			CellRangeAddress ca = sheet.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();
			int lastColumn = ca.getLastColumn();
			int firstRow = ca.getFirstRow();
			int lastRow = ca.getLastRow();

			if(row >= firstRow && row <= lastRow){

				if(column >= firstColumn && column <= lastColumn){
					Row fRow = sheet.getRow(firstRow);
					Cell fCell = fRow.getCell(firstColumn);
					return getCellValue(fCell) ;
				}
			}
		}

		return null ;
	}

	/**
	 * 判断指定的单元格是否是合并单元格
	 * @param sheet
	 * @param row 行下标
	 * @param column 列下标
	 * @return
	 */
	public static boolean isMergedRegion(Sheet sheet,int row ,int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			int firstColumn = range.getFirstColumn();
			int lastColumn = range.getLastColumn();
			int firstRow = range.getFirstRow();
			int lastRow = range.getLastRow();
			if(row >= firstRow && row <= lastRow){
				if(column >= firstColumn && column <= lastColumn){
					return true;
				}
			}
		}
		return false;
	}

	public static void setValue(Object obj , DocColMeta col, String temp) throws Exception{
		//给对象set值
		String fileName = col.getProperty();
		String dataType = col.getDataType();
		String pattern = col.getPattern();
		String codeType = col.getCodeType();
		Class dateType = null;//类型

		//结果
		Object rs = null;

		// 格式化字符串
		log.debug("属性[" + fileName + "],值[" + temp + "]" + ",类型[" + dataType + "]");

		boolean flag = false; //是否该字段属性对应的报文为空
		if ("int".equalsIgnoreCase(dataType) || "Integer".equalsIgnoreCase(dataType)) {
			if ("int".equalsIgnoreCase(dataType)){
				dateType = int.class;
			}else {
				dateType = Integer.class;
			}

			if (null == temp || "".equals(temp)) {
				flag = true;
			}else{
				BigDecimal bd = new BigDecimal(temp);
				rs = bd.intValue();
			}

		} else if ("double".equalsIgnoreCase(dataType)
				|| "double".equalsIgnoreCase(dataType)) {
			dateType = Double.class;
			if (null == temp || "".equals(temp)) {
				flag = true;
			}else{
				rs = Double.valueOf(temp);
			}
		} else if ("BigDecimal".equalsIgnoreCase(dataType)) {
			dateType = BigDecimal.class;
			if (null == temp || "".equals(temp)) {
				flag = true;
			}else{
				rs = new BigDecimal(temp);
			}
		} else if ("date".equalsIgnoreCase(dataType)) {
			dateType = Date.class;
			if (null == temp || "".equals(temp)) {
				flag = true;
			}else{
				SimpleDateFormat sdf = new SimpleDateFormat(pattern == null ? "yyyyMMdd" : pattern);
				try {
					rs = sdf.parse(temp);
				} catch (ParseException e) {
					log.error(e.getMessage(),e);
				}
			}
		}else if ("timestamp".equalsIgnoreCase(dataType)) {
			dateType = Timestamp.class;
			if (null == temp || "".equals(temp)) {
				flag = true;
			}else{
				SimpleDateFormat sdf = new SimpleDateFormat(pattern == null ? "yyyyMMdd" : pattern);
				try {
					rs = new Timestamp(sdf.parse(temp).getTime());
				} catch (ParseException e) {
					log.error(e.getMessage(),e);
				}
			}
		}  else {
			dateType = String.class;
			if (null != codeType&& !"".equals(codeType)){
				//反向取值
				String s = CodeEncode.getMappingValue(codeType, temp);
				if (null != s && !"".equals(s)) {
					temp = s;
				}
			}
			rs = temp;
		}

		if (flag) {
			return;
		}


		//根据value中的值，对对象层层设值
		try {
			setValue(obj,fileName,dateType,rs);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new Exception("解析字段[" + fileName + "]失败",e);
		}
	}

	/**
	 *
	 * @param obj
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static void setValue(Object obj, String fileName,Class dateType,Object value)
			throws Exception {
		if (null == obj) {
			log.error("对象[" + obj + "] 为空，filename[" + fileName + "]");
			return ;
		}
		if (null == fileName) {
			log.error("对象[" + obj + "] 对应的字段的字段名为空");
			return ;
		}

		if (null == value) {
			log.error("对象[" + obj + "] 对应的字段[" + fileName + "]的值为空");
			return ;
		}

		String[] vm = fileName.split("\\."); //用正则，点是正则的关键字，必须转义
		int index = fileName.indexOf(".");

		if (index == -1) {
			Method method = Clazz.getMethod(obj.getClass(), fileName, "set", dateType);
			method.invoke(obj,value);
		} else {
			Method method = Clazz.getMethod(obj.getClass(), vm[0], "get", null);
			Object temp = method.invoke(obj);
			if (null == temp) {
				try {
					temp = method.getReturnType().newInstance();
				} catch (InstantiationException e) {
					log.error(e.getMessage(),e);
					throw new Exception("实例[" + method.getReturnType() + "]失败");
				}
			}

			//循环嵌套调用
			setValue(temp, fileName.substring(index + 1),dateType,value);

			//该对象的set方法
			method = Clazz.getMethod(obj.getClass(), vm[0], "set", temp.getClass());
			method.invoke(obj,temp);
		}
	}

	public static Row getRow(Sheet mHSSFSheet, int row){
		if (row > mHSSFSheet.getLastRowNum()) {
			return mHSSFSheet.createRow(row);
		}else {
			if (null == mHSSFSheet.getRow(row)) {
				return mHSSFSheet.createRow(row);
			}
			return mHSSFSheet.getRow(row);
		}
	}

	//设置样式
	public static Row setRowStyle(Row styleRow,Row row){
		if (null != styleRow && null != row) {
			int styleRowNum = styleRow.getRowNum();
			int editRowNum = row.getRowNum();

			for (int i = 0 ; i < styleRow.getSheet().getNumMergedRegions();i++) {
				CellRangeAddress ranage = styleRow.getSheet().getMergedRegion(i);
				int firstRow = ranage.getFirstRow();
				int loastRow = ranage.getLastRow();

				if (styleRowNum == ranage.getFirstRow()) {
					CellRangeAddress newRanage = new CellRangeAddress(firstRow + (editRowNum - styleRowNum) , loastRow + (editRowNum - styleRowNum), ranage.getFirstColumn(), ranage.getLastColumn());
					row.getSheet().addMergedRegion(newRanage);
				}
			}
			row.setHeight(styleRow.getHeight());

			Drawing drawing = row.getSheet().createDrawingPatriarch();
			Workbook workbook = row.getSheet().getWorkbook();
			DataValidationHelper helper = row.getSheet().getDataValidationHelper();

			for (int i = 0 ; i < styleRow.getLastCellNum(); i ++) {
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
				copyCell(ExcelUtils.getCell(styleRow, i),ExcelUtils.getCell(row, i),drawing,workbook.getClass());
			}
		}
		return row;
	}

	public static void setValidatation(Cell fromCell, Cell toCell){
		//拷贝验证
		if (null != fromCell && null != toCell && fromCell.getSheet() instanceof XSSFSheet) {
			List<XSSFDataValidation> validationList = getDataValidationConstraint((XSSFSheet)fromCell.getSheet(),fromCell.getRowIndex(),fromCell.getColumnIndex());
			if (null != validationList) {

				for (XSSFDataValidation item : validationList) {
					DataValidationConstraint dvc = item.getValidationConstraint();
					int validationType = dvc.getValidationType();
					int operatorType = dvc.getOperator();
					String vMin = dvc.getFormula1();
					String vMax = dvc.getFormula2();
					DataValidationConstraint dvConstraint = null;
					//System.out.println("validationType:" + validationType + ",operatorType:" + operatorType);
					//System.out.println("vMin:" + vMin + ",vMax:" + vMax);
					DataValidationHelper dvHelper = toCell.getSheet().getDataValidationHelper();
					if(validationType == DataValidationConstraint.ValidationType.TEXT_LENGTH) {
						dvConstraint = dvHelper.createTextLengthConstraint(operatorType, vMin, vMax);
					} else if(validationType == DataValidationConstraint.ValidationType.DECIMAL) {
						dvConstraint = dvHelper.createDecimalConstraint(operatorType, vMin, vMax);
					} else if(validationType == DataValidationConstraint.ValidationType.INTEGER) {
						dvConstraint = dvHelper.createIntegerConstraint(operatorType, vMin, vMax);
					} else if(validationType == DataValidationConstraint.ValidationType.DATE) {
						dvConstraint = dvHelper.createDateConstraint(operatorType, vMin, vMax, null);
					} else if(validationType == DataValidationConstraint.ValidationType.TIME) {
						dvConstraint = dvHelper.createTimeConstraint(operatorType, vMin, vMax);
					} else if(validationType == DataValidationConstraint.ValidationType.FORMULA) {
						dvConstraint = dvHelper.createFormulaListConstraint( dvc.getFormula1() );
					} else if(validationType == DataValidationConstraint.ValidationType.LIST) {
						if (null != vMin) {
							vMin = vMin.replaceAll( "\"" ,"" );
							String[] dlist = vMin.split( "," );
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
		setValidatation(fromCell,toCell);

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
			case Cell.CELL_TYPE_STRING:
				to.setCellValue(from.getStringCellValue());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				to.setCellValue(from.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_BLANK:
				to.setCellType(Cell.CELL_TYPE_BLANK);
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				to.setCellValue(from.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_ERROR:
				to.setCellErrorValue(from.getErrorCellValue());
				break;
			case Cell.CELL_TYPE_FORMULA:
				String formula = from.getCellFormula();
				if (null != formula) {
					int from_row = from.getRowIndex();
					int from_col = from.getColumnIndex();
					int to_row = to.getRowIndex();
					int to_col = to.getColumnIndex();
					Matcher m = pattern.matcher( formula );
					if (m.find( )) {
						String str = m.group( 0 );
						String ziMu = null;
						String indexStr = null;

						if (null != str) {
							indexStr = str.replaceAll( "[^\\d]" ,"" );
							ziMu = str.replaceAll( "\\d" ,"" );
						}
						if (null != indexStr) {
							int index = Integer.valueOf( indexStr );

							if (index < (to_row+1)) {
								formula = formula.replaceAll( str , ziMu + (to_row+1));
							}
						}
					}
					formula = formula.replaceAll( "_bak!","!" );//解决从bak文件解析问题
				}
				to.setCellFormula(formula);
				break;
			default:
				break;
		}
	}
	private static void copyComment(Class<? extends Workbook> clazz, Comment fromComment, Drawing drawing,Cell fromCell, Cell toCell) {
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
		if (s.equals( "HSSFWorkbook" )) {
			aClientAnchor = new HSSFClientAnchor();

		} else {
			aClientAnchor = new XSSFClientAnchor();

		}
		return aClientAnchor;
	}

	public static List<XSSFDataValidation> getDataValidationConstraint(XSSFSheet sheet,int row,int col){
		if (null == sheet) return null;

		List<XSSFDataValidation> rs = new ArrayList<XSSFDataValidation>(  );
		List<XSSFDataValidation> validations = sheet.getDataValidations();
		if ( null != validations ) {
			for (XSSFDataValidation item : validations) {
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
					rs.add( item );
				}
			}
		}
		return rs;
	}

	public String getColString(int colIndex){
		int first = colIndex / 26;
		int index = colIndex % 26;

		System.out.println("first:" + first + ",index:" + index);

		String rs = String.valueOf( (char)('A' + (first-1)));//首字母
		if (colIndex < 26) {
			return String.valueOf( (char)('A' + index));
		}else{
			return rs + String.valueOf((char)('A' + index));//首字母;
		}
	}

	@Test
	public void test(){
//		System.out.println(String.valueOf('A'));
		int i = 0 ;
		System.out.println(getColString( i ));
		i = 25 ;
		System.out.println(getColString( i ));
		i = 26;
		System.out.println(getColString( i ));
		i = 51 ;
		System.out.println(getColString( i ));
		i = 52 ;
		System.out.println(getColString( i ));
	}

}
