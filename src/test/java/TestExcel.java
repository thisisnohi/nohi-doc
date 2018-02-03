import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class TestExcel {
	/**
	 * HSSFWorkbook excell的文档对象 HSSFSheet excell的表单 HSSFRow excell的行
	 * HSSFCell excell的格子单元 HSSFFont excell字体 HSSFName 名称 HSSFDataFormat 日期格式
	 * HSSFHeader sheet头 HSSFFooter sheet尾 HSSFCellStyle cell样式
	 */
	public static void main(String[] args) throws IOException {
		//新那一个Excel
		//writeToBlankExcel();

		//根据Excel模板,生成文件并导入数据
		writeToExcelTemplet();
	}

	/**
	 * 新建excel
	 * @throws IOException
	 */
	public static void writeToBlankExcel() throws IOException{
		HSSFWorkbook wb = new HSSFWorkbook();
		// 建立新HSSFWorkbook对象

		HSSFSheet sheet = wb.createSheet("new sheet");
		// 建立新的sheet对象

		// Create a row and put some cells in it.Rows are 0 based.
		HSSFRow row = sheet.createRow((short) 0);
		// 建立新行

		// Create a cell and put a value in it.
		HSSFCell cell = row.createCell(0); //row.createCell(0);
		// 建立新cell

		cell.setCellValue(1222);// 设置cell的整数类型的值
		// Or do it on one line.

		row.createCell(1).setCellValue(1.2);
		// 设置cell浮点类型的值
		row.createCell(2).setCellValue("test");
		// 设置cell字符类型的值
		row.createCell(3).setCellValue(true);
		// 设置cell布尔类型的值

		HSSFCellStyle cellStyle = wb.createCellStyle();
		// 建立新的cell样式
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));

		// 设置cell样式为定制的日期格式
		HSSFCell dCell = row.createCell(4);
		dCell.setCellValue(new Date());
		// 设置cell为日期类型的值
		dCell.setCellStyle(cellStyle);
		// 设置该cell日期的显示格式
		HSSFCell csCell = row.createCell(5);
		//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		// 设置cell编码解决中文高位字节截断

		csCell.setCellValue("中文测试_Chinese Words Test");

		// 设置背景色
		HSSFCellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(new HSSFColor.BLACK().getIndex());
		style.setFillBackgroundColor(new HSSFColor.ROSE().getIndex());
		style.setFillPattern(HSSFCellStyle.SPARSE_DOTS);
		HSSFCell cell1 = row.createCell(6);
		cell1.setCellStyle(style);
		//cell1.setCellValue("X");
		//setCellValue 被  cell1.setCellValue(new HSSFRichTextString("aaaaaaaaaaaa")); 替换
		cell1.setCellValue(new HSSFRichTextString("aaaaaaaaaaaa"));

		// 设置背景色
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFillForegroundColor(new HSSFColor.GREY_40_PERCENT().getIndex());
		style1.setFillBackgroundColor(new HSSFColor.GREY_40_PERCENT().getIndex());
		style1.setBorderBottom((short) 1);
		style1.setBorderTop((short) 1);
		style1.setBorderLeft((short) 1);
		style1.setBorderRight((short) 1);

		/**
		 * 注意这句代码， style1.setFillPattern, 如果你在你的程序中不设置fill pattern,那么
		 * 你上面设置的前景色和背景色就显示不出来.网络上很多文章都没有设置fillpattern,
		 * 不知道那些达人 的机器是不是比我的机器智能很多.
		 */
		style1.setFillPattern(HSSFCellStyle.SPARSE_DOTS);
		HSSFCell cell11 = row.createCell(7);
		cell11.setCellValue("X11");
		cell11.setCellStyle(style1);

		// 数字格式化
		HSSFCellStyle st = wb.createCellStyle();
		// 建立新的cell样式
		st.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
		HSSFCell cell12 = row.createCell(8);
		cell12.setCellValue((double) 10000000);
		cell12.setCellStyle(st);

		// 设置中西文结合字符串
		row.createCell(9).setCellType(HSSFCell.CELL_TYPE_ERROR);
		// 建立错误cell

		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream("d:/doc/2_workbook.xls");
		wb.write(fileOut);
		fileOut.close();
	}

	/**
	 * 导入数据到excel模板中
	 * @throws IOException
	 */
	public static void  writeToExcelTemplet() throws IOException{
		FileInputStream fi = null;
		HSSFWorkbook hwb = null;
		HSSFRow row = null;//行
		HSSFCell cell = null;//列

		//1,读取Excel
		fi = new FileInputStream("d:/doc/2_test_excel.xls");
		hwb = new HSSFWorkbook(fi);

		//2,取得第一个Sheet
		HSSFSheet sheet = hwb.getSheetAt(0);
		int rows = sheet.getLastRowNum();//行数, base 0
		int cols = -1;
		System.out.println("rows: " + (rows+1));
		for (int i = 0 ; i <= rows ; i++) {//遍历行
			row = sheet.getRow(i);//取得行

			cols = row.getLastCellNum();//取得最后一列有序号 base 1
			System.out.print("第["+ (i+1) +"]行有["+ (cols) +"]列(base 1): ");

			for (int j = 0 ; j < cols ; j++) {//遍历列
				cell = row.getCell(j);
				Object value = null;
				if (null != cell) {
					switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC: // 数值型
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								//如果是date类型则 ，获取该cell的date值
								value = HSSFDateUtil.getJavaDate(cell.getNumericCellValue()).toString();
								System.out.print(value);
							}else{//纯数字
								value = cell.getNumericCellValue() + 1;//数字加一
							}
							System.out.print(value);
							cell.setCellValue((Double)value);
							break;
	                    /* 此行表示单元格的内容为string类型 */
						case HSSFCell.CELL_TYPE_STRING: // 字符串型
							value = cell.getRichStringCellValue().toString();
							System.out.print(value);
							break;
						case HSSFCell.CELL_TYPE_FORMULA://公式型
							//读公式计算值
							value = String.valueOf(cell.getNumericCellValue());
							if(value.equals("NaN")){//如果获取的数据值为非法值,则转换为获取字符串
								value = cell.getRichStringCellValue().toString();
							}
							//cell.getCellFormula();读公式
							System.out.print(value);
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN://布尔
							value = " "+ cell.getBooleanCellValue();
							System.out.print(value);
							break;
	                    /* 此行表示该单元格值为空 */
						case HSSFCell.CELL_TYPE_BLANK: // 空值
							value = "";
							System.out.print(value);
							break;
						case HSSFCell.CELL_TYPE_ERROR: // 故障
							value = "";
							System.out.print(value);
							break;
						default:
							value = cell.getRichStringCellValue().toString();
							System.out.print(value);
					}
				}
			}
			System.out.println();
		}


		hwb.write(new FileOutputStream("d:/doc/2_test_excel_结果.xls"));
	}
}
