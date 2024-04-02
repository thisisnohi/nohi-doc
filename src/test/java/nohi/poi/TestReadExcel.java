package nohi.poi;

import com.alibaba.fastjson.JSONObject;
import nohi.doc.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author NOHI
 * 2021-11-20 15:35
 **/
public class TestReadExcel {

    @Test
    public void testStr() {
        String str = "1．下列选项中属于企业文化功能的是(　　)。";
        System.out.println(str.matches("^[0-9].*"));
        str = " A．下列选项中属于企业文化功能的是(　　)。";
        System.out.println(str.matches("^[0-9].*"));
    }

    @Test
    public void testSplit() {
        String str = "151. D         152. C         153. A         154. B         155. C         156. A         ";
        String[] ss = str.split("  ");
        for (String s : ss) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            System.out.println(s.trim().substring(s.trim().length() - 1));
        }
    }

    @Test
    public void testRead() {
        String filePath = "/Users/nohi/Downloads/sj2.xlsx";
        System.out.println("filePath:" + filePath);

        InputStream is = null;
        Workbook templatebook = null;//模板文件
        Sheet templateSheet = null; //模板文件Sheet

        try {
            is = new FileInputStream(new File(filePath));
            templatebook = WorkbookFactory.create(is);
            int sheets = templatebook.getNumberOfSheets();
            System.out.println("sheet.size:" + sheets);
            templateSheet = templatebook.getSheetAt(0);
            List<List<String>> sheetData = this.readSheet(templateSheet);

            System.out.println("==========打印结果=======:" + sheetData.size());
            boolean isSt = false;
            StringBuffer st = new StringBuffer();
            StringBuffer xx = new StringBuffer();

            for (List<String> rowData : sheetData) {
                String value = rowData.get(0);
                if (StringUtils.isNotBlank(value) && value.matches("^[0-9]{1,3}.*")) {
                    isSt = true;
                } else {
                    isSt = false;
                }
                if (isSt) {
//                    System.out.println("");
//                    System.out.println(value);
                    System.out.println("");
                    st.append(value).append("\n");
                } else {
                    System.out.print(value);
                }

            }

            System.out.println("==================");
            System.out.println(st.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public List<List<String>> readSheet(Sheet templateSheet) {
        List<List<String>> sheetData = new ArrayList<List<String>>();
        if (null == templateSheet) return sheetData;
        int rowNum = templateSheet.getLastRowNum();
        Row row = null;
        System.out.println("行数:" + rowNum);
        for (int i = 0; i <= rowNum; i++) {
            row = templateSheet.getRow(i);
            if (null == row) {
                System.out.println("行:" + i + " 为空");
                continue;
            }
            List<String> rowData = new ArrayList<String>();
            sheetData.add(rowData);
            int celNum = row.getLastCellNum();
            for (int j = 0; j < celNum; j++) {
                String value = ExcelUtils.getCellValue(row.getCell(j));//取值
                rowData.add(value);
            }
        }
        return sheetData;
    }

    /**
     * 解析答案
     */
    @Test
    public void testReadDaan() {
        String filePath = "/Users/nohi/Downloads/sj2.xlsx";
        System.out.println("filePath:" + filePath);

        InputStream is = null;
        Workbook templatebook = null;//模板文件
        Sheet templateSheet = null; //模板文件Sheet

        try {
            is = new FileInputStream(new File(filePath));
            templatebook = WorkbookFactory.create(is);
            int sheets = templatebook.getNumberOfSheets();
            System.out.println("sheet.size:" + sheets);
            templateSheet = templatebook.getSheetAt(0);
            List<List<String>> sheetData = this.readSheet(templateSheet);

            System.out.println("==========打印结果=======:" + sheetData.size());
            boolean isSt = false;
            List<String> rs = new ArrayList<String>();
            for (List<String> rowData : sheetData) {
                String value = rowData.get(0);
                String[] ss = value.split("  ");
                for (String s : ss) {
                    if (StringUtils.isBlank(s)) {
                        continue;
                    }
                    s = s.trim().substring(s.trim().length() - 1);
                    if (s.equalsIgnoreCase("√")) {
                        s = "Y";
                    }
                    if (s.equalsIgnoreCase("×")) {
                        s = "N";
                    }
                    rs.add(s);
                    System.out.println(s);
                }
            }
            System.out.println(rs.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }

    }


    /**
     * 组装json
     */
    @Test
    public void json() {
        String filePath = "/Users/nohi/Downloads/sj2.xlsx";
        System.out.println("filePath:" + filePath);

        InputStream is = null;
        Workbook templatebook = null;//模板文件
        Sheet templateSheet = null; //模板文件Sheet

        try {
            is = new FileInputStream(new File(filePath));
            templatebook = WorkbookFactory.create(is);
            int sheets = templatebook.getNumberOfSheets();
            System.out.println("sheet.size:" + sheets);
            templateSheet = templatebook.getSheetAt(0);
            List<List<String>> sheetData = this.readSheet(templateSheet);

            System.out.println("==========打印结果=======:" + sheetData.size());
            boolean isSt = false;
            List<Map> rs = new ArrayList();
            int index = 1;
            for (List<String> rowData : sheetData) {
                String stTitle = rowData.get(0);
                String option = rowData.get(1);
                String stAnswer = rowData.get(2);
                String optionJson = this.getOptions(option);
                System.out.println(stTitle + "  opetion:" + option + ",optionJson:" + optionJson + ",stAnswer:" + stAnswer);
                Map st = new HashMap();
                st.put("sjNo", "sj_02");
                st.put("sjTitle", "维修电工中级理论知识试卷");
                st.put("stIndex", index++);
                st.put("stTitle", stTitle);
                st.put("stDetail", "");
                st.put("stOptionType", "select");
                st.put("stOptions", optionJson);
                st.put("stScore", 0.5);
                st.put("stAnswer", stAnswer);

                rs.add(st);
            }

            System.out.println(JSONObject.toJSONString(rs));
            // [
            //  {
            //    sjNo: 'sj_01',
            //    sjTitle: '测试一',
            //    stIndex: 1,
            //    stTitle: '1．下列选项中属于企业文化功能的是(　　)。',
            //    stDetail: '',
            //    stOptionType: 'checkbox',
            //    stOptions: [
            //      {'opKey': 'A', 'opValue': 'A、体育锻炼'},
            //      {'opKey': 'B', 'opValue': 'B、整合功能'},
            //      {'opKey': 'C', 'opValue': 'C、歌舞娱乐'},
            //      {'opKey': 'D', 'opValue': 'D、社会交际'}
            //    ],
            //    stScore: 0.5,
            //    stAnswer: 'A'
            //  },
            //]
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }

    }

    @Test
    public void testOption() {
        String str = " A、体育锻炼         B、整合功能         C、歌舞娱乐         D、社会交际";
        str = this.getOptions(str);
        System.out.println(str);
    }

    public String getOptions(String option) {
        if (StringUtils.isBlank(option)) {
            return "[{'opKey': 'Y', 'opValue': '对'},{'opKey': 'N', 'opValue': '错'}]";
        }
        String[] ss = option.split("  ");
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (String s : ss) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            s = s.trim();
            String key = s.substring(0, 1);
            String value = s.substring(1).replaceAll("、", "");
            sb.append("{'opKey': '" + key + "', 'opValue': '" + value + "'},");
        }
        String json = sb.toString();
        if (json.endsWith(",")) {
            json = json.substring(0, json.length() - 1);
        }
        return json + "]";
    }
}
