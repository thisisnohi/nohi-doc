package nohi.doc.excel.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDocVO {

	private String sheetName;
	private String str1;
	private Integer intV1;
	private InnerVO ivo; // 内部对象
	private List<TestListVO> list;// 列表

	private Map map;

	private List<Map> listMap;

	public List<Map> getListMap() {
		if (null == listMap) {
			listMap = new ArrayList<Map>();
		}
		return listMap;
	}

	public void setListMap(List<Map> listMap) {
		this.listMap = listMap;
	}

	public Map getMap() {
		if (null ==map) {
			map = new HashMap();
		}
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public InnerVO getIvo() {
		return ivo;
	}

	public void setIvo(InnerVO ivo) {
		this.ivo = ivo;
	}

	public String getStr1() {
		return str1;
	}

	public void setStr1(String str1) {
		this.str1 = str1;
	}

	public Integer getIntV1() {
		return intV1;
	}

	public void setIntV1(Integer intV1) {
		this.intV1 = intV1;
	}

	public List<TestListVO> getList() {
		return list;
	}

	public void setList(List<TestListVO> list) {
		this.list = list;
	}
}
