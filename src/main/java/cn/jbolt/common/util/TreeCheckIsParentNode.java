package cn.jbolt.common.util;
/**
 * JBolt平台表数据转Tree结构时判断是否是父级
 * @ClassName:  TreeCheckIsParentNode   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2020年6月14日   
 */
public interface TreeCheckIsParentNode<T>{
	boolean isParent(T t);
}
