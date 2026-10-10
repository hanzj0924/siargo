package cn.jbolt._admin.dept;

import cn.jbolt.core.bean.JsTreeBean;
import cn.jbolt.core.model.Dept;
import cn.jbolt.core.service.JBoltDeptService;

import java.util.List;


public class DeptService extends JBoltDeptService {
    /**
     * 得到JSTree数据源
     *
     * @param checkedId 默认选中节点
     * @param openLevel -1全部 0 不动 >0指定层级
     */
    public List<JsTreeBean> getAllCrudJsTreeDatas(Long checkedId, int openLevel) {
        List<Dept> depts = getAllList();
        return convertJsTree(depts, checkedId, openLevel, null, "sn,name", SORT_RANK, null, false);
    }
}