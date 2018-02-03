
#nohi-doc 1.1
    
    1,主要功能是excel导入、导出
      PDF导出：需要用Adobe Acrobat工具制作导出模板的域，只能制作格式化的导出,列表只能固定行数。
              如果想制作动态pdf,可以搜索wkhtmltox通过页面导出。
    2,所有的配置基于XML，感觉配置过于麻烦。   
    3,几年前闲着无聊写的，最近项目中用到，修改了部分代码，支持xlsx,支持合并单元格导入、下拉、公式
    
    此工程bug多多，谨慎使用，如有建议/意见，欢迎指导（虽然不一定能听进去...）

    这次说明主要为了学习github使用。简单说明
    基本功能参见test/java/nohi/doc/下excel pdf 中的测试类   
#配置说明

     doc.properties  classpath下必需的主配置属性文件
        appConf=nohi-doc.xml   主文档配置文件
        encodeConf=docconf/nohi_encode_conf.xml   数据字典码值映射文件
    
     nohi-doc.xml
    	store-file节点配置需要修改为本地临时存放的目录。导出时，需要生成文件，返回目录路径，此目录为临时存放目录。
    	ftp-server 最初设计支持导出后存入ftp文件服务器。
        doc-excel  excel配置
        doc-pdf    pdf配置
                
#EXCEL

  ##导入:
  
        DocVO doc = new DocVO();
        doc.setDocId("TEST_IMPORT");  //对应 主文档配置文件nohi-doc.xml中 <doc id="TEST_IMPORT" ..>
        doc.setDocType("EXCEL");      //文件类型
        File file = new File(filePath);  //导入源文件路径
        TestDocVO vo = (TestDocVO)docService.importFromFile(doc, file); //返回对象
            //返回对象
            <document id="TEST_IMPORT" name="测试Excel"
                      data="nohi.doc.excel.vo.TestDocVO"
                      docType="EXCEL"
            >


  ##导出：
  
        配置内容同导入

        DocVO doc = new DocVO();
        doc.setDocId("TEST");
        doc.setDocType("EXCEL");
        doc.setRepeatSheet(DocConsts.EXCEL_SHEET_REPEAT); // 重复生成EXCEL SHEET
            支持导出多个sheet,  比如，导出模板中为公司基本信息，有十家需要导出，导出的excel中会用十个sheet页。
            datavo.setSheetName("测试sheet_" + re + "abc"); //指定sheet名称

#PDF

    pdf只支持导出
        pdf模板需要adobe acrobat编辑pdf，里面增加text的编辑域什么的.
        此功能相对单一，模板固定，无法自动扩展列表条数，列表如果很多，无法显示全部。
   
        建议如果需要，可以jsp或者其它工具，html2pdf.不要使用此工程的功能。            
   