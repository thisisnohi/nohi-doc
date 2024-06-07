# nohi-doc

## 功能
* 文档类型
  * excel
* 程序技术模型
  * 配置
    * xml
    * annotation
    * 人工初始化
  * 运行
    * 输入
    * 输出
  * 测试

## 配置化
### TDD驱动
* excel 导入、导出测试类
[TestExcelExport.java](src%2Ftest%2Fjava%2Fnohi%2Fdoc%2Fservice%2Fexcel%2FTestExcelExport.java)
[TestExcelExportRepeat.java](src%2Ftest%2Fjava%2Fnohi%2Fdoc%2Fservice%2Fexcel%2FTestExcelExportRepeat.java)
[TestExcelImport.java](src%2Ftest%2Fjava%2Fnohi%2Fdoc%2Fservice%2Fexcel%2FTestExcelImport.java)
[TestXlsmExport.java](src%2Ftest%2Fjava%2Fnohi%2Fdoc%2Fservice%2Fexcel%2FTestXlsmExport.java)

* 配置
* `doc.properties` 指定各个配置文件名和相对路径
* `nohi-doc.xml`  文档主配置/根配置，配置了所有文档的基本信息、及具体配置文档路径

### 码值映射
* 配置: `codeType`
  ```xml
  <col column="6" property="test" codeType="CurrencyCd">测试代码值</col>
  ```
  * 导入时：根据excel值，查找对应的key
  * 导出时：根据对象的值，映射码值下对应的value,导出到excel中
* 映射服务
  映射服务：采用spi机制提供服务，并提供默认实现`DefaultCodeMappingService`
  该服务解析`docconf/nohi_encode_conf.xml`文件
* 自定义码值实现类：
  * 实现接口及接口对应方法
  ```java
  public class TestDefaultCodeMappingService implements ICodeMappingService {
  }
  ```
  * 注册spi
  ```
  1. resource下建立目录：META-INF/services/
  2. 创建文本文件(无后缀)： nohi.doc.service.ICodeMappingService
  3. 自定义类的包+类，写入文件[nohi.doc.service.ICodeMappingService]中
  ```

