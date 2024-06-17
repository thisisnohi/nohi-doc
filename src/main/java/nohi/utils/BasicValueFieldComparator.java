package nohi.utils;

import java.lang.reflect.Field;
import java.util.Comparator;

public class BasicValueFieldComparator implements Comparator<Field>{

	@Override
	public int compare(Field f1, Field f2) {
		if (null == f1.getName() || "".equals(f1.getName().trim())) {
			return -1;
		}else if (null == f2.getName() || "".equals(f2.getName().trim())) {
			return 1;
		}else {
			return f1.getName().compareTo(f2.getName());
		}
	}
}
