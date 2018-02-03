package nohi.doc.meta;

import java.util.List;

public class DocSheetMeta {
	private String dynamicsheetname;
	private String type;
	private String name;

	private List<DocBlockMeta> blockList; //块列表

	public String getDynamicsheetname() {
		return dynamicsheetname;
	}

	public void setDynamicsheetname(String dynamicsheetname) {
		this.dynamicsheetname = dynamicsheetname;
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

	public List<DocBlockMeta> getBlockList() {
		return blockList;
	}

	public void setBlockList(List<DocBlockMeta> blockList) {
		this.blockList = blockList;
	}

}
