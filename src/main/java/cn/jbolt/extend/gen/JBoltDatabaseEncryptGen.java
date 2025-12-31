package cn.jbolt.extend.gen;

import cn.jbolt.core.util.JBoltDesUtil;

/**
 * 平台数据库配置账号和密码加密生成器
 */
public class JBoltDatabaseEncryptGen {
    public static void main(String[] args) {
        //数据库原用户名
        String user = "root";
        //数据库原密码
        String password = "root";
        System.out.println("==========加密后数据输出==========");
        //密文生成
        String newUser = JBoltDesUtil.getEncryptData(user);
        System.out.println("user = " + newUser);
        //密文生成
        String newPwd = JBoltDesUtil.getEncryptData(password);
        System.out.println("password = " + newPwd);
        //输出解密
        //System.out.println(JBoltDesUtil.getDecryptData(newUser));
        //System.out.println(JBoltDesUtil.getDecryptData(newPwd));
    }
}
