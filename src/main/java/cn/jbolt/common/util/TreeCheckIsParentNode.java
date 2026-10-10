package cn.jbolt.common.util;

/** JBolt平台表数据转Tree结构时判断是否是父级 */
public interface TreeCheckIsParentNode<T> {
    boolean isParent(T t);
}
