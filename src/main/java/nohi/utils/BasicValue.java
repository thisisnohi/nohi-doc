package nohi.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class BasicValue {

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Class:").append(this.getClass());
        sb.append("\n[");

        /**
         * this.getClass().getFields()只能取得子类的public属性，
         * this.getClass().getDeclaredFields();可以取得所有声明属性
         */
        // 取得所有属性
        Field[] fields = this.getClass().getDeclaredFields();
        // System.out.println("length: " + fields.length);

        Arrays.sort(fields, new BasicValueFieldComparator());

        // 遍历属性，取得属性值，拼接字符串
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];

            //根据属性取得方法名
            Method m = getMethod(f.getName());
            if (null == m) {
                continue;
            }
            //根据方法返回对象
            Object obj = getObject(m);

            sb.append("\n");
            sb.append(f.getName());
            sb.append(":[");

            sb.append(getString(obj));

            sb.append("]");

        }
        sb.append("\n]");

        return sb.toString();
    }

    /**
     * 根据对象取得对象的toString
     */
    public String getString(Object obj) {
        //System.out.println("obj: " + obj);
        StringBuffer sb = new StringBuffer();
        if (obj instanceof List) {
            for (Iterator it = ((List) obj).iterator(); it.hasNext(); ) {
                sb.append(it.next());
                sb.append(",");
            }

        } else if (obj instanceof Map) {
            Map map = (Map) obj;
            for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
                String key = it.next();
                sb.append("[");
                sb.append("key=").append(key);
                sb.append(",value=").append(map.get(key));
                sb.append("],");
            }
        } else if (obj instanceof Set) {
            for (Iterator it = ((Set) obj).iterator(); it.hasNext(); ) {
                sb.append(it.next());
                sb.append(",");
            }
        } else {
            sb.append(obj == null ? "" : obj.toString());
        }

        return sb.toString();
    }

    /**
     * 根据字段名取得该字段对应的get方法
     *
     * @param temp
     * @return
     */
    public Method getMethod(String temp) {
        Method m = null;
        try {
            m = this.getClass().getMethod("get" + covertFirstChar2Upper(temp));
        } catch (Exception ignored) {
        }
        return m;
    }

    /**
     * 取得get方法的返回对象
     */
    public Object getObject(Method mMethod) {
        try {
            return mMethod.invoke(this);
        } catch (Exception ignored) {
        }
        return null;
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

    public static String toString(Object c) {
        if (null == c) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("Class:").append(c.getClass());
        sb.append("\n[");

        /**
         * this.getClass().getFields()只能取得子类的public属性，
         * this.getClass().getDeclaredFields();可以取得所有声明属性
         */
        // 取得所有属性
        Field[] fields = c.getClass().getDeclaredFields();
        // System.out.println("length: " + fields.length);

        // 遍历属性，取得属性值，拼接字符串

        // 按字段名称排序
        Arrays.sort(fields, new BasicValueFieldComparator());

        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            // 字段属性首字母大写变量
            String temp = covertFirstChar2Upper(f.getName());

            Method m;

            try {
                m = c.getClass().getMethod("get" + temp);
            } catch (SecurityException e) {
                continue;
            } catch (NoSuchMethodException e) {
                continue;
            } catch (Exception e) {
                continue;
            }

            Object obj;
            try {
                obj = m.invoke(c);
            } catch (IllegalArgumentException e) {
                continue;
            } catch (IllegalAccessException e) {
                continue;
            } catch (InvocationTargetException e) {
                continue;
            }
            // System.out.println("obj: " + obj.getClass());;

            sb.append("\n");
            sb.append(f.getName());
            sb.append(":[");
            if (obj instanceof List) {
                for (Iterator it = ((List) obj).iterator(); it.hasNext(); ) {
                    sb.append(it.next());
                    sb.append(",");
                }

            } else if (obj instanceof Map) {
                Map map = (Map) obj;
                for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
                    String key = (String) it.next();
                    sb.append("[");
                    sb.append("key=").append(key);
                    sb.append(",value=").append(map.get(key));
                    sb.append("],");
                }
            } else if (obj instanceof Set) {
                for (Iterator it = ((Set) obj).iterator(); it.hasNext(); ) {
                    sb.append(it.next());
                    sb.append(",");
                }
            } else {
                sb.append(obj);
            }
            sb.append("]");

        }

        sb.append("\n]");

        return sb.toString();
    }
}
