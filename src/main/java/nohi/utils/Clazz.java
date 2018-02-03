package nohi.utils;

import nohi.doc.service.CodeEncode;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;



@SuppressWarnings({ "unchecked", "rawtypes" })
public class Clazz {
	private static Logger log = Logger.getLogger(Clazz.class);

	/**
	 * 取得Get方法 / set方法
	 *
	 * @param methodType
	 *            方法类型 get / set
	 * @param parameterTypes
	 *            方法的参数
	 * @param temp
	 * @return
	 * @throws Exception
	 */
	public static Method getMethod(Class c, String fileName, String methodType,Class parameterTypes) throws Exception {
		Method m = null;
		try {
			if ("set".equals(methodType)) {
				m = c.getMethod("set" + covertFirstChar2Upper(fileName),parameterTypes);
			} else if ("other".equals(methodType)) {
				m = c.getMethod(fileName,parameterTypes);
			}else {
				m = c.getMethod("get" + covertFirstChar2Upper(fileName));
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new Exception("获取对象[" + c + "]属性[" + fileName + "][" + methodType + "]方法异常",e);
		}
		return m;
	}

	/**
	 * 将字符串，首字母转换为大写，其他不变
	 *
	 * @param str
	 * @return
	 */
	public static String covertFirstChar2Upper(String str) {
		if (null == str || "".equals(str.trim())) {
			return "";
		}

		StringBuffer sb = new StringBuffer();

		sb.append(Character.toUpperCase(str.charAt(0)));
		if (str.length() > 1) {
			sb.append(str.substring(1));
		}
		return sb.toString();
	}

	/**
	 * 取得对象中，对应属性的值
	 * @param obj
	 * @param value
	 * @return
	 * @throws Exception
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object getValue(Object obj, String property) throws Exception{
		String[] vm = property.split("\\."); //用正则，点是正则的关键字，必须转义
		int index = property.indexOf(".");

		try {
			if (null == obj) {
				return null;
			}
			if (index == -1) {

				if (property.indexOf("[") >=0 && property.endsWith("]")) {
					return getMapValue(obj, property);
				}else {
					Method method = getMethod(obj.getClass(), property, "get", null);
					if (null == method) {
						return null;
					}

					return method.invoke(obj);
				}
			} else {
				Method method = getMethod(obj.getClass(), vm[0], "get", null);
				Object temp = method.invoke(obj);
				return getValue(temp, property.substring(index + 1));
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new Exception("取类[" + obj.getClass() + "]的属性[" + property + "] 错误",e);
		}
	}

	public static Object getMapValue(Object obj, String property) throws Exception{
		String key = property.substring( property.indexOf("[")  + 1 , property.indexOf("]") );
		if (null != key) {
			key = key.replace("'", "");
		}
		if (obj instanceof Map) { //对象本身是一个Map
			return ((Map)obj).get(key);
		}else if (obj instanceof List) {
			System.out.println("is list");
		}else {
			String mapProperty = property.substring(0,property.indexOf("["));
			Method method = getMethod(obj.getClass(), mapProperty, "get", null);
			if (null == method) {
				return null;
			}

			Object mapObj =  method.invoke(obj);
			if (null != mapObj && mapObj instanceof Map) {
				Map m = (Map) mapObj;
				return m.get(key);
			}
		}
		return null;
	}

	/**
	 * 取得值
	 * @param rs
	 * @param dataType
	 * @param pattern
	 * @param codeType
	 * @return
	 */
	public static String getFieldStrValue(Object rs , String dataType,String pattern ,String codeType){
		String tempStr = null;
		if (null == rs) {
			tempStr = "";
		}else if ("int".equalsIgnoreCase(dataType)
				|| "Integer".equalsIgnoreCase(dataType)) {
			Integer in = (Integer) rs;
			tempStr = in.toString();
		} else if ("double".equalsIgnoreCase(dataType)
				|| "double".equalsIgnoreCase(dataType)) {
			Double d = (Double) rs;
			DecimalFormat df = new DecimalFormat(pattern == null ? "0.00" : pattern);
			tempStr = df.format(d);
		} else if ("BigDecimal".equalsIgnoreCase(dataType)) {
			BigDecimal bd = (BigDecimal) rs;
			DecimalFormat df = new DecimalFormat(pattern == null ? "0.00": pattern);
			tempStr = df.format(bd);
		} else if ("date".equalsIgnoreCase(dataType)) {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern == null ? "yyyyMMdd" : pattern);
			Date bd = (Date) rs;
			tempStr = sdf.format(bd);
		}  else if ("timestamp".equalsIgnoreCase(dataType)) {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern == null ? "yyyyMMdd" : pattern);
			Timestamp bd = (Timestamp) rs;
			tempStr = sdf.format(bd);
		} else {
			tempStr = rs.toString();
		}

		if (null != codeType && !"".equals(codeType.trim())) {
			String s = CodeEncode.getMappingValue(codeType, tempStr);
			if (null != s && !"".equals(s)) {
				tempStr = s;
			}
		}

		return tempStr;
	}
}
