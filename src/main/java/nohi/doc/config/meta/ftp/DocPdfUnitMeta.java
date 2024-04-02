package nohi.doc.config.meta.ftp;

import java.util.Map;

/**
 * PDF单元格元数据
 *
 * @author NOHI
 * @crated date 2013-2-26
 */
public class DocPdfUnitMeta {
	private String type; //text 或者 table
	private String name;
	private String property; // 对应Java属性字段
	private String dataType; // 字段类型
	private String pattern; // 格式标签
	private String codeType; // 代码值

	private String itemClass;//元素类: 对应如果是table列表时，对应相应的item
	private Map<String,DocPdfUnitMeta> unitTableMap;//单元格Map，可以有多个单元格块

	public String getItemClass() {
		return itemClass;
	}

	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
	}

	public Map<String, DocPdfUnitMeta> getUnitTableMap() {
		return unitTableMap;
	}

	public void setUnitTableMap(Map<String, DocPdfUnitMeta> unitTableMap) {
		this.unitTableMap = unitTableMap;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}
}
