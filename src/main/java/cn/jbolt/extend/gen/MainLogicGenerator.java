package cn.jbolt.extend.gen;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.gen.SystemLogTargetType;
import cn.jbolt.core.model.Permission;
import com.jfinal.plugin.activerecord.Model;
import cn.jbolt.siargo.model.*;

import cn.jbolt.core.gen.IndexHtmlLayoutType;
import cn.jbolt.core.gen.JBoltMainLogicBean;
import cn.jbolt.core.gen.JBoltMainLogicGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller Service html生成器
 * @ClassName:  MainLogicGenerator   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年4月11日   
 */
public class MainLogicGenerator extends JBoltMainLogicGenerator{
	/**
	 * 执行入口
	 * @param args
	 */
	public static void main(String[] args) {
		new MainLogicGenerator().generate();
	}
	
	/**
	 * 初始化需要 生成的逻辑的model 配置
	 */
	@Override
	public void initGenConfig() {
		/*
		 * index.html使用的UI布局 
		 *  默认普通表格CRUD布局 normal_crud
		 *  可选normal_crud、normal_table、master_slave 
		 *  对应普通crud类型、普通表格查询类型、主从表
		 */
		IndexHtmlLayoutType indexHtmlLayoutType = IndexHtmlLayoutType.NORMAL_CRUD;
		//生成java代码里的作者信息 默认 JBolt-Generator
		String author                    = "hanzj";
		//controller service等java代码生成的报名 路径
		String packageName               = "cn.jbolt.admin.siargo.equipment.inspectionBatch";
		//在路由配置里的controllerPath参数 也用在生成其它URL的前缀
		String controllerPath            = "/admin/siargo/equipment/inspectionBatch";
		//生成html存放位置 从src/main/webapp根目录下开始 /作为前缀
		String viewFolder                = "/_view/admin/siargo/equipment/inspectionBatch";
		//生成Index.html左上角页面标题
		String pageTitle                 = "检校批次记录";
		//在页面里使用增加 修改 删除 提示信息等用到的针对此模块操作的数据名称 例如 商品管理中是【商品】 品牌管理中是【品牌】
		String dataName                  = "InspectionBatch";
		//生成模块用的model是哪个？
		Class<? extends Model<?>> modelClass = EquipmentInspectionBatch.class;
		//是否需要分页查询
		boolean needPaginate             = true;
		//index.html 是否需要启用表格的工具条 toolbar
		boolean needToolbar              = true;
		//执行删除时是否做检测校验
		boolean checkDelete              = true;
		//关键词查询匹配字段 多个用逗号隔开
		String matchColumns              = "batch_date";
		//查询用默认排序字段 多个用逗号隔开
		String orderColumn               = "batch_date";
		//查询用默认排序方式 desc asc
		String orderType                 = "desc";
		//这个模块crud 等关键操作如果需要增加systemLog需要指定log类型 默认是空 需要设置按照下面注释的案例设置
		//SystemLogTargetType projectSystemLogTargetType   = SystemLogTargetType.NONE;
		SystemLogTargetType projectSystemLogTargetType   = new SystemLogTargetType("EquipmentInspectionBatch","检校批次记录",20006);

		/*
		 * 需要在Controller上方声明的@CheckPermission(PermissionKey.USER) 
		 * 默认PermissionKey.NONE 空权限是不可用的 需要自己处理
		 */
		List<Permission> checkPermissions = new ArrayList<>();
		//需要自己添加需要的权限 这里添加的Permission 数据库里如果不存在 就自动生成入库 然后PermissionKey生成
		{
			checkPermissions.add(new Permission().setTitle("siargo").setPermissionKey("siargo").setUrl("/admin/siargo"));
		}

		//是否使用@path注解 就不用去配置路由了 默认false
		boolean usePathAnnotation        = true;
		//访问Controller权限是是否支持超管员不校验直接放行 默认false
		boolean unCheckIfSystemAdmin     = true;
		
		//创建主逻辑生成配置Bean
		JBoltMainLogicBean mainLogicBean = new JBoltMainLogicBean(modelClass,projectPath, packageName,controllerPath, viewFolder ,pageTitle,dataName,needPaginate,needToolbar,checkDelete,matchColumns,orderColumn,orderType,usePathAnnotation,unCheckIfSystemAdmin,indexHtmlLayoutType,author);
		//设置权限
		mainLogicBean.setPermissions(checkPermissions);
		//设置日志类型
		mainLogicBean.setSystemLogTargetType(projectSystemLogTargetType);
		//如果是crud模式 特殊处理一下 行编辑删除按钮
		if(mainLogicBean.isCrudType()) {
			//index.html页面表格是否需要行末尾的编辑按钮
			mainLogicBean.setNeedTrEditBtn(!needToolbar); //如果启用了toolbar 默认不要每行后面的编辑按钮
			//index.html页面表格是否需要行末尾的删除按钮
			mainLogicBean.setNeedTrDeleteBtn(!needToolbar);//如果启用了toolbar 默认不要每行后面的删除按钮
		}
		//index.html页面主表格区域 是否启用headBox
		mainLogicBean.setNeedHeadBox(false);
		//index.html页面主表格区域 是否启用footBox
		mainLogicBean.setNeedFootBox(false);
		//index.html页面主表格区域 是否启用leftBox
		mainLogicBean.setNeedLeftBox(false);
		//index.html页面主表格区域 是否启用rightBox
		mainLogicBean.setNeedRightBox(false);
		if(mainLogicBean.isCrudType()) {
			//设置crud 操作里 dialog的宽高尺寸
			mainLogicBean.setDialogArea("800,600");
			//设置表单几列呈现
			mainLogicBean.setFormColCount(2);
			//设置一列最少几个控件摆放 只有表单列设置为大于1列的时候才有效
			mainLogicBean.setFormColControlMinCount(5);
		}
		
		//加入到生成队列中
		addGenBean(mainLogicBean);
	}
	
}






