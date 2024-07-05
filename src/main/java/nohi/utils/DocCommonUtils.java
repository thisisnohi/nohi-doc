package nohi.utils;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class DocCommonUtils {

    public static String getFileName(String fullPathStr) {
        String name = fullPathStr;
        int k = name.lastIndexOf("\\");
        name = name.substring(k + 1);
        int a = name.lastIndexOf("_");
        name = name.substring(a + 1);
        return name;
    }

    /**
     * 获取当前系统时间
     *
     * @return
     */
    public static Timestamp getCurrentTime() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        log.debug("CurrentTime: {}", currentTime);
        return currentTime;
    }

    /**
     * 获取当前系统时间字符串:精确到天
     *
     * @return
     */
    public static Date getCurrentDate() {
        Date date = new Date(System.currentTimeMillis());
        return date;
    }

    /**
     * 　获取时间毫秒数
     *
     * @return
     */
    public static String getTimeMillis() {
        return "" + System.currentTimeMillis();
    }

    /**
     * 获取当前系统时间字符串:精确到天
     *
     * @return
     */
    public static String getCurrentDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sdf.format(new Timestamp(System.currentTimeMillis()));
//		 log.debug("CurrentTimeStr: " + str);
        return str;
    }

    /**
     * 获取当前系统时间字符串:精确到时分秒
     *
     * @return
     */
    public static String getCurrentTimeStr(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(null == pattern ? "yyyy-MM-dd" : pattern);
        String str = sdf.format(new Timestamp(System.currentTimeMillis()));
        return str;
    }


    /**
     * 前后去空格，空字符串返回''
     *
     * @param str
     */
    public static String trim2str(String str) {
        if (null == str || "".equals(str.trim())) {
            return "''";
        }
        return "'" + str.trim() + "'";
    }

    /**
     * 生成流水号
     * 时间毫秒 + 随机数
     */
    public static String getFlow() {
        double d = Math.random() * 100000;
        long l = System.currentTimeMillis();
        log.debug("l: {} ,d:{} , Math.round(d): {}", l, d, Math.round(d));
        return "" + l + Math.round(d);
    }


}
