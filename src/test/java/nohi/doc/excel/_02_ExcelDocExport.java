package nohi.doc.excel;

import nohi.doc.DocConsts;
import nohi.doc.excel.vo.InnerVO;
import nohi.doc.excel.vo.TestDocVO;
import nohi.doc.excel.vo.TestListVO;
import nohi.doc.service.IDocService;
import nohi.doc.service.impl.DocService;
import nohi.doc.vo.DocVO;
import nohi.utils.DocCommonUtils;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 导出数据到模板中
 *
 * @author NOHI
 * @create date: 2013-2-15
 */
public class _02_ExcelDocExport {

	/**
	 * 导出重复sheet
	 * @throws Exception
	 */
	@Test
	public void exportDocRepeatSheet() throws Exception {
		IDocService docService = new DocService();

		DocVO doc = new DocVO();
		doc.setDocId("TEST");
		doc.setDocType("EXCEL");
		doc.setRepeatSheet(DocConsts.EXCEL_SHEET_REPEAT); // 重复生成EXCEL SHEET

		List dateVOlist = new ArrayList();
		for (int re = 0; re < 24; re++) {
			TestDocVO datavo = new TestDocVO();
			datavo.setSheetName("测试sheet_" + re + "abc");
			datavo.setIntV1(123);
			datavo.setStr1("this is nohi");

			InnerVO ivo = new InnerVO();
			ivo.setInnerInt(222);
			ivo.setInnerStr("heheheh");
			datavo.setIvo(ivo);

			List<TestListVO> list = new ArrayList<TestListVO>();
			TestListVO lvo;
			for (int i = 2; i < 5; i++) {
				lvo = new TestListVO();
				lvo.setAge(re + i);
				lvo.setAmt(22d + i);
				lvo.setBd(new BigDecimal(23 + i));
				lvo.setDate(DocCommonUtils.getCurrentDate());
				lvo.setId(24 + i);
				lvo.setName("Name_" + re + i);
				lvo.setTest(i % 2 == 0 ? "156" : "840");// 测试代码值
				list.add(lvo);
			}

			datavo.setList(list);

			List<Map> listMap = new ArrayList<Map>();
			Map map = new HashMap<String,String>();
			map.put("a", "22");
			map.put("b", "123123");
			listMap.add(map);
			datavo.setListMap(listMap);

			dateVOlist.add(datavo);
		}

		doc.setDataVO(dateVOlist);

		// 生成EXCEL
		docService.exportDoc(doc);
		System.out.println("doc: " + doc.getFilePath());


	}

	/**
	 * 导出单个sheet
	 */
	@Test
	public void exportDoc() {
		IDocService docService = new DocService();

		DocVO doc = new DocVO();
		doc.setDocId("TEST");
		doc.setDocType("EXCEL");
		doc.setFilePath("D:");
		int re = 0;

		TestDocVO datavo = new TestDocVO();
//		datavo.setSheetName("测试sheet_" + re);
		datavo.setIntV1(123);
		datavo.setStr1("this is nohi");

		InnerVO ivo = new InnerVO();
		ivo.setInnerInt(222);
		ivo.setInnerStr("abc");
		datavo.setIvo(ivo);

		Map map = new HashMap();
		map.put("a", "1111");

		datavo.setMap(map);

		List l = new ArrayList();
		for (int i = 0 ; i < 3 ; i++) {
			map = new HashMap();
			map.put("a", "1111");
			map.put("b", "bbb");
			l.add(map);
		}
		datavo.setListMap(l);


		List<TestListVO> list = new ArrayList<TestListVO>();
		TestListVO lvo;
		for (int i = 2; i < 5; i++) {
			lvo = new TestListVO();
			lvo.setAge(re + i);
			lvo.setAmt(22d + i);
			lvo.setBd(new BigDecimal(123000 + i));
			lvo.setDate(DocCommonUtils.getCurrentDate());
			lvo.setId(24 + i);
			lvo.setName("Name_" + re + i);
			lvo.setTest(i % 2 == 0 ? "156" : "840");// 测试代码值
			list.add(lvo);
		}

//		datavo.setList(list);
		datavo.setList(new ArrayList<TestListVO>(  ));

		doc.setDataVO(datavo);

		try {
			// 生成EXCEL
			docService.exportDoc(doc);
			System.out.println("doc: " + doc.getFilePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	/**
	 * 导出单个sheet
	 */
	@Test
	public void exportDoc2() {
		IDocService docService = new DocService();

		DocVO doc = new DocVO();
		doc.setDocId("TEST2");
		doc.setDocType("EXCEL");
		doc.setFilePath("D:");
		int re = 0;

		TestDocVO datavo = new TestDocVO();
		//datavo.setSheetName("测试sheet_" + re);
		datavo.setIntV1(123);
		datavo.setStr1("this is nohi");

		InnerVO ivo = new InnerVO();
		ivo.setInnerInt(222);
		ivo.setInnerStr("abc");
		datavo.setIvo(ivo);

		Map map = new HashMap();
		map.put("a", "1111");

		datavo.setMap(map);

		List l = new ArrayList();
		for (int i = 0 ; i < 3 ; i++) {
			map = new HashMap();
			map.put("a", "1111");
			map.put("b", "bbb");
			l.add(map);
		}
		datavo.setListMap(l);


		List<TestListVO> list = new ArrayList<TestListVO>();
		TestListVO lvo;
		for (int i = 2; i < 5; i++) {
			lvo = new TestListVO();
			lvo.setAge(re + i);
			lvo.setAmt(22d + i);
			lvo.setBd(new BigDecimal(123000 + i));
			lvo.setDate(DocCommonUtils.getCurrentDate());
			lvo.setId(24 + i);
			lvo.setName("Name_" + re + i);
			lvo.setTest(i % 2 == 0 ? "156" : "840");// 测试代码值
			list.add(lvo);
		}

		datavo.setList(list);

		doc.setDataVO(datavo);

		try {
			// 生成EXCEL
			docService.exportDoc(doc);
			System.out.println("doc: " + doc.getFilePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 导出单个sheet
	 * 测试导出备注、下拉列表问题
	 */
	@Test
	public void exportDocMark() {
		IDocService docService = new DocService();

		DocVO doc = new DocVO();
		doc.setDocId("TEST_MARK");
		doc.setDocType("EXCEL");
		int re = 0;

		TestDocVO datavo = new TestDocVO();
		List<TestListVO> list = new ArrayList<TestListVO>();
		TestListVO lvo;
		for (int i = 2; i < 10; i++) {
			lvo = new TestListVO();
			lvo.setId(24 + i);
			lvo.setName("Name_" + re + i);
			lvo.setAge(re + i);
			lvo.setDate(DocCommonUtils.getCurrentDate());
			lvo.setAmt(22d + i);
			lvo.setBd(new BigDecimal(123000 + i));
			lvo.setTest(i % 2 == 0 ? "156" : "840");// 测试代码值
			list.add(lvo);
		}

		datavo.setList(list);

		doc.setDataVO(datavo);

		try {
			// 生成EXCEL
			docService.exportDoc(doc);
			System.out.println("doc: " + doc.getFilePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	@Test
	public void test下拉(){
		InputStream is = null;
		try {
//			is = new FileInputStream( "C:\\workspaces-nohi\\nohi-doc\\src\\test\\resources\\template\\excel\\excel_test_template_mark.xlsx" );
//			is = new FileInputStream( "D:/in.xlsx" );
			is = new FileInputStream( "D:/ETL调度_任务配置导入模版_3.0.2.xlsx" );
			Workbook wb = WorkbookFactory.create(is);
			if (null == wb) {
				System.out.println("读取失败...");
				return ;
			}
			Sheet sheet = wb.getSheet( "Cron任务" );
			if (null == sheet) {
				System.out.println("获取sheet失败...");
				return ;
			}

			if (sheet instanceof HSSFSheet) {
				System.out.println("=======XSSFSheet===========");
				HSSFSheet xsheet = (HSSFSheet) sheet;
				List<HSSFDataValidation> validations = xsheet.getDataValidations();
				if ( null != validations ) {
					for (DataValidation item : validations) {
						CellRangeAddressList cral = item.getRegions();
						CellRangeAddress[] craArray = cral.getCellRangeAddresses();
						for (CellRangeAddress cra : craArray) {
							int f_c = cra.getFirstColumn();
							int f_r = cra.getFirstRow();
							int t_c = cra.getLastColumn();
							int t_r = cra.getLastRow();
							System.out.println(String.format( "from row:%s col:%s  to row:%s col:%s" , f_r, f_c,t_r,t_c ) );
						}

						String formula1 = item.getValidationConstraint().getFormula1();
						System.out.println( formula1);
					}
				}

				DataValidationHelper dvHelper = xsheet.getDataValidationHelper();
				DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(new String[]{"AAA","BBB"});
				CellRangeAddressList addressList = new CellRangeAddressList(2,3,2,3);
				DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
				//设置出错提示信息
				validation.setSuppressDropDownArrow(true);
				validation.setShowErrorBox(true);
//					setDataValidationErrorMessage(validation, errorTitle, errorMsg);
				xsheet.addValidationData(validation);


				 dvHelper = xsheet.getDataValidationHelper();
				 dvConstraint = dvHelper.createExplicitListConstraint(new String[]{"CCC","DDD"});
				 addressList = new CellRangeAddressList(4,5,0,0);
				 validation = dvHelper.createValidation(dvConstraint, addressList);
				xsheet.addValidationData(validation);

				dvHelper = xsheet.getDataValidationHelper();
				dvConstraint = dvHelper.createExplicitListConstraint(new String[]{"CCC","DDD"});
				addressList = new CellRangeAddressList(4,5,0,0);
				validation = dvHelper.createValidation(dvConstraint, addressList);


				//设置出错提示信息
//				validation.setSuppressDropDownArrow(true);
//				validation.setShowErrorBox(true);
//					setDataValidationErrorMessage(validation, errorTitle, errorMsg);
				xsheet.addValidationData(validation);

				FileOutputStream os = new FileOutputStream( "D:/out.xlsx" );
				wb.write( os );
			}else {
				System.out.println("=======HSSF===========");
			}

		} catch (Exception e) {
		 	e.printStackTrace();
		}
	}
}
