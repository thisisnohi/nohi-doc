package nohi.doc.service;

import java.io.File;
import java.io.InputStream;

import nohi.doc.vo.DocVO;



/**
 * 文档处理类接口
 * @author NOHI
 * @create date: 2013-2-15
 */
public interface IDocService {

	/**
	 * 导出数据到文档中
	 * @param doc
	 */
	DocVO exportDoc(DocVO doc)  throws Exception;

	/**
	 * 导入文档中的数据
	 * @param doc
	 * @param inputFile
	 * @return
	 */
	Object importFromFile(DocVO doc , File inputFile)throws Exception ;

	/**
	 * 从输入流中根据文档解析数据
	 * @param doc
	 * @param is
	 * @return
	 */
	Object importFromInputStream(DocVO doc , InputStream is)throws Exception;

	/**
	 * 从输入流中根据文档解析数据
	 * @param doc
	 * @param is
	 *  @param _valueFailExit  解析字段值错误是否退出,默认false;
	 * @return
	 */
	Object importFromInputStream(DocVO doc , InputStream is,boolean _valueFailExit)throws Exception;
}
