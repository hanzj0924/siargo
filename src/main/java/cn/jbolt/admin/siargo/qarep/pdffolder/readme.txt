管理报告版号、PDF 模板文件与产品系列绑定，供报告生成及发布校验使用。
PdfFolderAdminController.java：版号、模板上传及系列绑定管理入口。
PdfFolderService.java：版号配置、模板与输出路径、启停及删除引用检查。
PdfTemplateService.java：模板文件、系列绑定规则、模板选择与发布校验。
PdfTemplateProdService.java：读取及替换模板与产品系列的关联。
报告生成按产品系列和版号选择唯一启用模板，填充逻辑位于 qarep/uploadImport。
