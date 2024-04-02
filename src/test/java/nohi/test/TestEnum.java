package nohi.test;

import nohi.doc.config.meta.excel.ExcelBlockMeta;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>enum</p>
 * @date 2024/04/02 16:39
 **/
public class TestEnum {

    @Test
    public void enumRef() throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ExcelBlockMeta excelBlockMeta = new ExcelBlockMeta();
        Class clazz = ExcelBlockMeta.class;
        String fieldName = "type";
        Field field = clazz.getDeclaredField(fieldName);
        System.out.println("getType:" + field.getType());
        if (field.getType().isEnum()) {
            Object[] enums = field.getType().getEnumConstants();
            for (Object anEnum : enums) {
                System.out.println(anEnum);
                if (anEnum.toString().equalsIgnoreCase("TABLE")) {
                    Method method = clazz.getMethod("setType", ExcelBlockMeta.BlockType.class);
                    method.invoke(excelBlockMeta, anEnum);
                }
            }
        }

        System.out.println("excelBlockMeta:" + excelBlockMeta.getType());

    }

}
