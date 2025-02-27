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
    public static Method getMethod(Class<?> obj, Field field, String methodType) throws NoSuchMethodException {
        return getMethod(obj, field, methodType, null);
    }

    /**
     * 根据属性名，取得Get方法 / set方法
     *
     * @param methodType 方法类型 get / set
     */
    public static Method getMethod(Class<?> obj, Field field, String methodType, Class<?> parameterTypes) throws NoSuchMethodException {
        Method method;
        if (METHOD_TYPE_SET.equals(methodType)) {
            method = obj.getMethod(METHOD_TYPE_SET + covertFirstChar2Upper(field.getName()), field.getType());
        } else if ("other".equals(methodType)) {
            method = obj.getMethod(field.getName(), parameterTypes);
        } else {
            if (field.getType() == boolean.class) {
                return obj.getMethod("is" + covertFirstChar2Upper(field.getName()));
            }
            method = obj.getMethod(METHOD_TYPE_GET + covertFirstChar2Upper(field.getName()));
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
        return getMethod(obj, fieldName, methodType, parameterTypes, true);
    }

    /**
     * 取得Get方法 / set方法
     *
     * @param methodType     方法类型 get / set
     * @param parameterTypes 方法的参数
     */
    public static Method getMethod(Class<?> obj, String fieldName, String methodType, Class<?> parameterTypes, boolean fieldNotFoundException) {
        String title = "获取对象[" + obj + "]属性[" + fieldName + "][" + methodType + "]方法";
        if (!haveField(obj, fieldName)) {
            log.warn("{} 对象不存在属性[{}]", title, fieldName);
            if (fieldNotFoundException) {
                return null;
            }
        }
        try {
            Field field = obj.getDeclaredField(fieldName);
            return getMethod(obj, field, methodType, parameterTypes);
        } catch (Exception e) {
            log.error("{} 获取方法异常:{}", title, e.getMessage(), e);
            throw new RuntimeException(title + "异常", e);
        }
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
        return getValue(obj, property, true);
    }

    /**
     * 取得对象中，对应属性的值
     */
    public static Object getValue(Object obj, String property, boolean propertyNotFoundException) {
        log.debug("[{}] 获取属性[{}] {}", null == obj ? "NULL" : obj.getClass(), property, propertyNotFoundException);
        // 用正则，点是正则的关键字，必须转义
        String[] vm = property.split("\\.");
        int index = property.indexOf(".");

        try {
            if (null == obj) {
                return null;
            }
            // 没有层级
            if (index == -1) {
                log.debug("第一层级");
                if (property.contains("[") && property.endsWith("]")) {
                    return getMapValue(obj, property);
                } else if (obj instanceof Map) {
                    return ((Map<?, ?>) obj).get(property);
                } else {
                    Method method = getMethod(obj.getClass(), property, METHOD_TYPE_GET, null);
                    if (null == method) {
                        return null;
                    }

                    return method.invoke(obj);
                }
            } else {
                log.debug("[{}]存在子层级[{}]", vm[0], property.substring(index + 1));
                // Method method = getMethod(obj.getClass(), vm[0], METHOD_TYPE_GET, null);
                // Object temp = method.invoke(obj);
                Object temp = getValue(obj, vm[0]);
                log.debug("[{}]存在子层级,[{}]", vm[0], temp == null ? "IS NULL" : temp.getClass());
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
            return ((List) obj).get(Integer.parseInt(key));
        } else {
            String mapProperty = property.substring(0, property.indexOf("["));
            Method method = getMethod(obj.getClass(), mapProperty, "get", null);
            if (null == method) {
                return null;
            }

            Object collectionObj = method.invoke(obj);
            if (collectionObj instanceof Map) {
                Map<?, ?> m = (Map<?, ?>) collectionObj;
                return m.get(key);
            } else if (collectionObj instanceof List) {
                List<?> list = (List<?>) collectionObj;
                if (list.size() > Integer.parseInt(key)) {
                    return list.get(Integer.parseInt(key));
                }
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

    public static boolean haveField(Class<?> clazz, String fieldName) {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }
}
