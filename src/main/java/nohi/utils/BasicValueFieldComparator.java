package nohi.utils;

import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * 对象比较
 * @author nohi
 */
public class BasicValueFieldComparator implements Comparator<Field>{

	@Override
	public int compare(Field f1, Field f2) {
		if (f1.getName().trim().isEmpty()) {
			return -1;
		}else if (f2.getName().trim().isEmpty()) {
			return 1;
		}else {
			return f1.getName().compareTo(f2.getName());
		}
	}
}
