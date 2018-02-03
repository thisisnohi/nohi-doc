package nohi.doc.meta;

import java.util.Map;

public class DocBlockMeta {
	private String type;
	private String row; // 行
	private String endRow;//列表截止行包含这行
	private String list;
	private String itemClass;//列表元素类型
	private String insertType;//插入数据类型
	private String rowLocation;

	private String rowpostion ; //块位置 absolute/relative
	private String addrows; //相对位置，

	private String lastModifyRowIndex;// 上一块最后占用的行
	private String thisModifyRowIndex; //该块占用最后行的index

	private Map<String, DocColMeta> cols;

	public String getRowpostion() {
		return rowpostion;
	}

	public void setRowpostion(String rowpostion) {
		this.rowpostion = rowpostion;
	}

	public String getAddrows() {
		return addrows;
	}

	public void setAddrows(String addrows) {
		this.addrows = addrows;
	}

	public String getLastModifyRowIndex() {
		return lastModifyRowIndex;
	}

	public void setLastModifyRowIndex(String lastModifyRowIndex) {
		this.lastModifyRowIndex = lastModifyRowIndex;
	}

	public String getThisModifyRowIndex() {
		return thisModifyRowIndex;
	}

	public void setThisModifyRowIndex(String thisModifyRowIndex) {
		this.thisModifyRowIndex = thisModifyRowIndex;
	}

	public String getRowLocation() {
		return rowLocation;
	}

	public void setRowLocation(String rowLocation) {
		this.rowLocation = rowLocation;
	}

	public String getInsertType() {
		return insertType;
	}

	public void setInsertType(String insertType) {
		this.insertType = insertType;
	}

	public String getEndRow() {
		return endRow;
	}

	public void setEndRow(String endRow) {
		this.endRow = endRow;
	}

	public String getItemClass() {
		return itemClass;
	}

	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
	}

	public Map<String, DocColMeta> getCols() {
		return cols;
	}

	public void setCols(Map<String, DocColMeta> cols) {
		this.cols = cols;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}
}
