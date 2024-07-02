package nohi.utils;

import lombok.extern.slf4j.Slf4j;
import nohi.doc.service.impl.CodeMappingService;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
public class Clazz {
    public static final String METHOD_TYPE_GET = "get";
    public static final String METHOD_TYPE_SET = "set";

    /**
     * 根据属性名，取得Get方法 / set方法
     *
     * @param methodType 方法类型 get / set
     */
    public static Method getMethod(Class<?> obj, Field field, String methodType) {
        Method method;
        try {
            if (METHOD_TYPE_SET.equals(methodType)) {
                method = obj.getMethod(METHOD_TYPE_SET + covertFirstChar2Upper(field.getName()), field.getType());
            } else {
                method = obj.getMethod(METHOD_TYPE_GET + covertFirstChar2Upper(field.getName()));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("获取对象[" + obj + "]属性[" + field.getName() + "][" + methodType + "]方法异常", e);
        }
        return method;
    }


    /**
     * 取得Get方法 / set方法
     *
     * @param methodType     方法类型 get / set
     * @param parameterTypes 方法的参数
     */
    public static Method getMethod(Class<?> obj, String fieldName, String methodType, Class<?> parameterTypes) {
        Method method;
        String title = "获取对象[" + obj + "]属性[" + fieldName + "][" + methodType + "]方法";
        try {
            Field field = obj.getDeclaredField(fieldName);
            if (METHOD_TYPE_SET.equalsIgnoreCase(methodType)) {
                method = obj.getMethod(METHOD_TYPE_SET + covertFirstChar2Upper(fieldName), parameterTypes);
            } else if ("other".equals(methodType)) {
                method = obj.getMethod(fieldName, parameterTypes);
            } else {
                if (field.getType() == boolean.class) {
                    return obj.getMethod("is" + covertFirstChar2Upper(fieldName));
                }
                method = obj.getMethod(METHOD_TYPE_GET + covertFirstChar2Upper(fieldName));
            }
        } catch (Exception e) {
            log.error("{} 获取方法异常:{}", title, e.getMessage(), e);
            throw new RuntimeException(title + "异常", e);
        }
        return method;
    }

    /**
     * 将字符串，首字母转换为大写，其他不变
     *
     * @param str 字符串
     * @return 首字母大写
     */
    public static String covertFirstChar2Upper(String str) {
        if (null == str || str.trim().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(str.charAt(0)));
        if (str.length() > 1) {
            sb.append(str.substring(1));
        }
        return sb.toString();
    }

    /**
     * 取得对象中，对应属性的值
     */
    public static Object getValue(Object obj, String property) {
        // 用正则，点是正则的关键字，必须转义
        String[] vm = property.split("\\.");
        int index = property.indexOf(".");

        try {
            if (null == obj) {
                return null;
            }
            if (index == -1) {

                if (property.contains("[") && property.endsWith("]")) {
                    return getMapValue(obj, property);
                } else {
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
            log.error(e.getMessage(), e);
            throw new RuntimeException("取类[" + obj.getClass() + "]的属性[" + property + "] 错误", e);
        }
    }

    public static Object getMapValue(Object obj, String property) throws Exception {
        String key = property.substring(property.indexOf("[") + 1, property.indexOf("]"));
        key = key.replace("'", "");
        // 对象本身是一个Map
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).get(key);
        } else if (obj instanceof List) {
            System.out.println("is list");
        } else {
            String mapProperty = property.substring(0, property.indexOf("["));
            Method method = getMethod(obj.getClass(), mapProperty, "get", null);
            if (null == method) {
                return null;
            }

            Object mapObj = method.invoke(obj);
            if (mapObj instanceof Map) {
                Map<?, ?> m = (Map<?, ?>) mapObj;
                return m.get(key);
            }
        }
        return null;
    }

    /**
     * 取得值
     *
     * @param rs       对象
     * @param dataType 数据类型
     * @param pattern  格式
     * @param codeType 类型
     * @return 结果
     */
    public static String getFieldStrValue(Object rs, String dataType, String pattern, String codeType) {
        String tempStr;
        if (null == rs) {
            tempStr = "";
        } else if ("int".equalsIgnoreCase(dataType) || "Integer".equalsIgnoreCase(dataType)) {
            Integer in = (Integer) rs;
            tempStr = in.toString();
        } else if ("double".equalsIgnoreCase(dataType)) {
            Double d = (Double) rs;
            DecimalFormat df = new DecimalFormat(pattern == null ? "0.00" : pattern);
            tempStr = df.format(d);
        } else if ("BigDecimal".equalsIgnoreCase(dataType)) {
            BigDecimal bd = (BigDecimal) rs;
            DecimalFormat df = new DecimalFormat(pattern == null ? "0.00" : pattern);
            tempStr = df.format(bd);
        } else if ("date".equalsIgnoreCase(dataType)) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern == null ? "yyyyMMdd" : pattern);
            Date bd = (Date) rs;
            tempStr = sdf.format(bd);
        } else if ("timestamp".equalsIgnoreCase(dataType)) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern == null ? "yyyyMMdd" : pattern);
            Timestamp bd = (Timestamp) rs;
            tempStr = sdf.format(bd);
        } else {
            tempStr = rs.toString();
        }

        if (StringUtils.isNotBlank(codeType)) {
            String s = CodeMappingService.getService().getCodeValue(codeType, tempStr);
            if (StringUtils.isNotBlank(s)) {
                tempStr = s;
            }
        }

        return tempStr;
    }


    /**
     * 转换字符串 => 字段属性类型的值
     *
     * @param field   字段属性
     * @param str     字符串值
     * @param pattern 格式化对象
     * @return 字段属性对应的对象
     */
    public static Object convertString2FieldType(Field field, String str, String pattern) {
        if (null == field || null == str) {
            return null;
        }
        String title = String.format("字段[%s]类型转换[%s]", field.getName(), str);

        if (field.getType() == Integer.class || field.getType() == int.class) {
            return new BigDecimal(str).intValue();
        } else if (field.getType() == Double.class || field.getType() == double.class) {
            return Double.valueOf(str);
        } else if (field.getType() == Float.class || field.getType() == float.class) {
            return Float.valueOf(str);
        } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
            return Boolean.valueOf(str);
        } else if (field.getType() == BigDecimal.class) {
            return new BigDecimal(str);
        } else if (field.getType() == Date.class) {

            SimpleDateFormat sdf = new SimpleDateFormat(pattern == null ? "yyyyMMdd" : pattern);
            try {
                return sdf.parse(str);
            } catch (ParseException e) {
                log.error("{} 日期转换失败:{}", title, e.getMessage(), e);
            }
            return null;
        } else if (field.getType() == Timestamp.class) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern == null ? "yyyyMMdd" : pattern);
            try {
                return new Timestamp(sdf.parse(str).getTime());
            } catch (ParseException e) {
                log.error("{} 日期转换失败:{}", title, e.getMessage(), e);
            }
            return null;
        } else {
            return str;
        }
    }
}
