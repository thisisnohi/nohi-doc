package nohi.doc;


import lombok.extern.slf4j.Slf4j;
import nohi.doc.excel.vo.TestDocVO;
import nohi.doc.excel.vo.TestListVO;
import nohi.utils.Clazz;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * @author NOHI
 */
@Slf4j
public class _02_SuportMap {

	@Test
	public void paraseMainDoc() throws Exception{
		// TestDocVO.map
		TestDocVO vo = new TestDocVO();
		Map map = new HashMap();
		map.put("a", "aaa");
		map.put("b", "bbb");

		vo.setMap(map);

		List<TestListVO> list = new ArrayList<TestListVO>();
		for (int i = 0 ; i < 4; i++) {
			TestListVO tvo = new TestListVO();

			map = new HashMap();
			map.put("a", "aaa_" + i);
			map.put("b", "bbb_" + i);

			tvo.setAbcMap(map);
			list.add(tvo);
		}


		Object obj = null;
		obj = Clazz.getValue(vo, "map['a']");
		System.out.println("obj:" + obj);

		obj = Clazz.getValue(vo, "map['b']");
		System.out.println("obj:" + obj);

		obj = Clazz.getValue(vo, "map['c']");
		System.out.println("obj:" + obj);

		System.out.println("----------------------------------");

		obj = Clazz.getValue(list.get(0), "abcMap['a']");
		System.out.println("obj:" + obj);

	}


}
