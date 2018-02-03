package nohi.doc.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import nohi.doc.DocConsts;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 * 读取Message Encode 文件，取得代码映射关系
 * @author NOHI
 * @date 2012-11-13
 */
public class CodeEncode {
	static String filePath = DocConsts.encodeConf;
	private static Map<String,Map<String,String>> codeTypeMap;  //正向code
	private static Map<String,Map<String,String>> decocodeTypeMap;//反向decode

	public static Map<String, Map<String, String>> getCodeTypeMap() {
		if (null == codeTypeMap) {
			initCodeTypeMap();
		}
		return codeTypeMap;
	}

	public void setCodeTypeMap(Map<String, Map<String, String>> codeTypeMap) {
		this.codeTypeMap = codeTypeMap;
	}

	/**
	 * 根据代码值，键值取得映射值
	 * @param codeType
	 * @param codekey
	 * @return
	 */
	public static String getMappingValue(String codeType , String codekey){
		Map map = getCodeTypeMap().get(codeType);
		if (null == map) {
			return codekey;
		}
		return (String)map.get(codekey);
	}

	/**
	 * 根据代码值，映射值取得键值,getMappingValue的反向
	 * @param codeType
	 * @param codekey
	 * @return
	 */
	public static String getDecodeMappingValue(String codeType , String codeValue){
		Map map = getCodeTypeMap().get(codeType);
		return (String)map.get(codeValue);
	}


	public static void initCodeTypeMap(){
		readXML();
	}

	static {
		initCodeTypeMap();
	}

	/**
	 * 读取文件
	 *
	 * @throws IOException
	 * @throws JDOMException
	 */
	public static void readXML()  {
		SAXBuilder builder = new SAXBuilder();

		//得到输入流
		InputStream file = XmlParase.class.getClassLoader().getResourceAsStream(filePath);
		Document doc = null;
		try {
			doc = builder.build(file);
		} catch (JDOMException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Element root = doc.getRootElement();

		codeTypeMap = new HashMap<String, Map<String,String>>();
		decocodeTypeMap = new HashMap<String, Map<String,String>>();

		List<Element> childRenlist = root.getChildren();
		Element codeTypeE = null;

		for (Iterator<Element> it = childRenlist.iterator(); it.hasNext();) {
			codeTypeE = it.next();

			String codeType = codeTypeE.getAttribute("type-name").getValue();

			Map<String,String> codeValueMapping = new HashMap<String, String>();
			Map<String,String> deCodeValueMapping = new HashMap<String, String>();
			List<Element> eL = codeTypeE.getChildren();

			for (Element e : eL) {
				String loanCode = e.getAttribute("loan-code").getValue();
				String mappingCode = e.getAttribute("mapping-code").getValue();
				codeValueMapping.put(loanCode, mappingCode);
				deCodeValueMapping.put(mappingCode, loanCode);
			}

			codeTypeMap.put(codeType,codeValueMapping);
			decocodeTypeMap.put(codeType,deCodeValueMapping);
		}
	}

	public static void main(String[] args) throws Exception {

		CodeEncode m = new CodeEncode();
		String value = m.getMappingValue("CurrencyCd","156");
		System.out.println("value: " + value);
	}

}
