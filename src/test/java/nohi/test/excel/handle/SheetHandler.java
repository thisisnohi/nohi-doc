package nohi.test.excel.handle;


import lombok.extern.slf4j.Slf4j;
import nohi.test.excel.vo.DataVO;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p>事件处理器</p>
 * @date 2024/07/15 21:26
 **/
@Slf4j
public class SheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
    private int a = 0;

    private DataVO dataVO;

    /**
     * 当开始解析某一行的时候触发
     * i:行索引
     */
    @Override
    public void startRow(int i) {
        // 实例化对象，开始解析excel的第二行也就是下标为1的行 初始化对象 excel的第一行是标题
        if (i > 0) {
            dataVO = new DataVO();
        }
    }

    /**
     * 当结束解析某一行的时候触发
     * i:行索引
     */
    @Override
    public void endRow(int i) {
        //使用对象进行业务操作， 一般是添加对象到数据库中
        a++;
        log.debug("{} {}", a, dataVO);
    }

    /**
     * 对行中的每一个表格进行处理 ，也就是一行中有多少个单元格 这个方法就会被调用几次
     * cellReference: 单元格名称
     * value：数据
     * xssfComment：批注
     */
    @Override
    public void cell(String cellReference, String value, XSSFComment xssfComment) {
        // 对对象属性赋值
        if (dataVO != null) {
            String pix = cellReference.substring(0, 1);
            switch (pix) {
                case "B":  //excel的第2列
                    dataVO.setName(value);
                    break;
                case "C": //excel的第3列
                    dataVO.setPassword(value);
                    break;
                default:
                    break;
            }
        }
    }
}
