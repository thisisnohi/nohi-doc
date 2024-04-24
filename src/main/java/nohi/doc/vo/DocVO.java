package nohi.doc.vo;

import lombok.Data;

/**
 * 文档服务传递数据
 *
 * @author NOHI
 * @create date: 2013-2-15
 */
@Data
public class DocVO<T> {
	private String docType;
	private String docId;
	private T dataVo;
	// 文件版本 如果没有指定，则为时间精确到毫秒
	private String docVersion;

	// 文件全路径
	private String filePath;
    // 文件名
    private String docName;
}
