package cn.jbolt._admin.user;

import cn.jbolt._admin.dept.DeptService;
import cn.jbolt._admin.onlineuser.OnlineUserService;
import cn.jbolt.common.model.UserExtend;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.cache.*;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.core.enumutil.JBoltEnum;
import cn.jbolt.core.kit.JBoltCurrentOfModuleKit;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.model.Application;
import cn.jbolt.core.model.Dept;
import cn.jbolt.core.model.User;
import cn.jbolt.core.service.JBoltUserService;
import cn.jbolt.core.util.JBoltPinYinUtil;
import cn.jbolt.extend.config.ExtendProjectOfModule;
import cn.jbolt.extend.user.ExtendUserOfModuleLinkService;
import com.jfinal.aop.Inject;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户Service
 *
 * @ClassName: UserService
 * @author: JFinal学院-小木 QQ：909854136
 * @date: 2020年5月2日
 */
public class UserService extends JBoltUserService {
    @Inject
    private DeptService deptService;
    @Inject
    private OnlineUserService onlineUserService;
    @Inject
    private UserExtendService userExtendService;
    @Inject
    ExtendUserOfModuleLinkService extendUserOfModuleLinkService;

    /**
     * 保存
     *
     * @param user
     * @return
     */
    public Ret save(User user, UserExtend extend) {
        if (user == null || isOk(user.getId()) || notOk(user.getName())
                || notOk(user.getUsername())) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        boolean userExtendEnable = JBoltGlobalConfigCache.me.userExtendEnable();
        if (userExtendEnable && extend != null && isOk(extend.getId())) {
            return fail("用户扩展信息" + JBoltMsg.PARAM_ERROR);
        }
        Ret ret = saveOrUpdate(user);
        if (ret.isFail()) {
            return ret;
        }
        //添加完user后处理extend
        if (userExtendEnable && extend != null) {
            extend.setId(user.getId());
            ret = userExtendService.saveByUser(extend);
            if (ret.isFail()) {
                return ret;
            }
        }
        return SUCCESS;
    }


    /**
     * 处理用户的所属模块信息
     *
     * @param user
     */
    protected void processUserOfModule(User user) {
        if (notOk(user.getOfModule()) || user.getOfModule().intValue() == 1) {
            //如果没设置就save 那得设置上 从当前线程获取 看看有没有 如果有就用了
            Integer jboltOfModule = JBoltCurrentOfModuleKit.getJboltOfModule();
            ExtendProjectOfModule type = null;
            if (notOk(jboltOfModule)) {
                type = ExtendProjectOfModule.PLATFORM_INNER_ADMIN;
            } else {
                type = JBoltEnum.getEnumObjectByValue(ExtendProjectOfModule.class, jboltOfModule.intValue());

            }
            if (type == null) {
                throw new RuntimeException("沒有找到对应的ExtendProjectOfModuleType");
            }
            user.setOfModule(type.getValue());
            processOfModuleLinkByType(user, type);
        }
    }

    private void processOfModuleLinkByType(User user, ExtendProjectOfModule type) {
        if (type == ExtendProjectOfModule.PLATFORM_INNER_ADMIN) {
            processOfModuleLinkIsInnerApplication(user);
        } else {
            user.setOfModuleLink(extendUserOfModuleLinkService.processOfModuleLinkByType(type));
        }
    }

    /**
     * 设置 内置模块link为内置app
     *
     * @param user
     */
    private void processOfModuleLinkIsInnerApplication(User user) {
        Application application = JBoltApplicationCache.me.getPcInnerPlatformApplication();
        user.setOfModuleLink(application.getId().toString());
    }

    /**
     * 更新
     *
     * @param user
     * @return
     */
    public Ret update(User user, UserExtend extend) {
        if (user == null || notOk(user.getId()) || notOk(user.getName())
                || notOk(user.getUsername())) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        boolean userExtendEnable = JBoltGlobalConfigCache.me.userExtendEnable();
        if (userExtendEnable) {
            if (extend != null && notOk(extend.getId())) {
                return fail("用户扩展信息" + JBoltMsg.PARAM_ERROR);
            }
            if (extend != null && !user.getId().equals(extend.getId())) {
                return fail("用户扩展信息" + JBoltMsg.PARAM_ERROR + ",与用户主键不一致");
            }
        }
        Ret ret = saveOrUpdate(user);
        if (ret.isFail()) {
            return ret;
        }
        //添加完user后处理extend
        if (userExtendEnable && extend != null) {
            extend.setId(user.getId());
            ret = userExtendService.updateByUser(extend);
            if (ret.isFail()) {
                return ret;
            }
        }
        return SUCCESS;
    }

    /**
     * saveOrUpdate
     *
     * @param user
     * @return
     */
    private Ret saveOrUpdate(User user) {
        if (user == null || notOk(user.getName())
                || notOk(user.getUsername())) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        if(user.getUsername().length()>40){
            return fail("用户名不能超过40个字符");
        }
        boolean update = isOk(user.getId());
        if (update) {
//			if (existsName(user.getName(),user.getId())) {
//				return fail("用户【"+user.getName()+"】已经存在，请输入其它名称");
//			}
            if (exists("username", user.getUsername(), user.getId())) {
                return fail("用户名【" + user.getUsername() + "】已经存在，请输入其它名称");
            }
            User dbUser = findById(user.getId());
            if (dbUser == null) {
                return fail(JBoltMsg.DATA_NOT_EXIST);
            }
            dbUser.deleteKeyCache();
            //清理角色变更缓存
            if ((isOk(dbUser.getRoles()) && !dbUser.getRoles().equals(user.getRoles()))
                    ||
                    (isOk(user.getRoles()) && !user.getRoles().equals(dbUser.getRoles()))
            ) {
                //存在其他跟自己相同角色的就不清理缓存 不存在说明自己独占 就清理
                boolean existSameRoles = existsSameRoles(dbUser.getRoles(), dbUser.getId());
                if (!existSameRoles) {
                    JBoltPermissionCache.me.removeRolesPermissionKeySet(dbUser.getRoles());
                    JBoltPermissionCache.me.removeRolesMenus(dbUser.getRoles());
                    JBoltPermissionCache.me.removeRolesMenusWithSystemAdminDefault(dbUser.getRoles());
                }
            }
        } else {
            if (notOk(user.getPassword())) {
                return fail("请输入用户密码");
            }
            if(user.getPassword().length()>40){
                return fail("密码不能超过40个字符");
            }
//			if (existsName(user.getName())) {
//				return fail("用户【"+user.getName()+"】已经存在，请输入其它名称");
//			}
            if (exists("username", user.getUsername())) {
                return fail("用户名【" + user.getUsername() + "】已经存在，请输入其它名称");
            }
        }

        if (notOk(user.getAvatar())) {
            if (isOk(user.getSex())) {
                user.setAvatar("assets/img/" + (user.getSex() == User.SEX_MALE ? "nan.png" : "nv.png"));
            } else {
                user.setAvatar("assets/img/nan.png");
            }
        }
        user.setName(user.getName().trim());
        user.setUsername(user.getUsername().trim());
        user.setPinyin(JBoltPinYinUtil.getSpells(user.getName()));
        user.setRoles(StrKit.isBlank(user.getRoles()) ? null : user.getRoles().trim());
        if (!update) {
            String password = user.getPassword().trim();
            String pwdSalt = HashKit.generateSaltForSha256();
            user.setPwdSalt(pwdSalt);
            user.setPassword(calPasswordWithSalt(password, pwdSalt));
            user.setCreateUserId(JBoltUserKit.getUserId());
            if (user.getEnable() == null) {
                user.setEnable(true);
            }
            if (user.getIsSystemAdmin() == null) {
                user.setIsSystemAdmin(false);
            }
        }
        //处理用户的deptPath
        if (isOk(user.getDeptId()) && notOk(user.getDeptPath())) {
            Dept dept = deptService.findById(user.getDeptId());
            if (dept == null) {
                return fail("指定部门数据不存在");
            }
            if (notOk(dept.getDeptPath())) {
                return fail("指定部门的路径数据不完整，请执行部门路径补全操作");
            }
            user.setDeptPath(dept.getDeptPath());
        }
        user.setLastPwdUpdateTime(new Date());
        if (!update) {
            processUserOfModule(user);
        }
        //保存或者更新
        boolean success = update ? user.update() : user.save();
        if (success) {
            if (update) {
                addUpdateSystemLog(user.getId(), JBoltUserKit.getUserId(), user.getName());
            } else {
                addSaveSystemLog(user.getId(), JBoltUserKit.getUserId(), user.getName());
            }
        }
        return ret(success);
    }

    /**
     * 查询是否存在相同角色组用户 排除自身
     *
     * @param roles
     * @param userId
     * @return
     */
    private boolean existsSameRoles(String roles, Long userId) {
        return exists("roles", roles, userId);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public Ret delete(Long id) {
        Long userId = JBoltUserKit.getUserId();
        if (isOk(id) && userId.intValue() == id.intValue()) {
            return fail("自己不能删除自己");
        }
        User user = findById(id);
        if (user == null) {
            return fail(JBoltMsg.DATA_NOT_EXIST);
        }
        Boolean isSysAdmin = user.getIsSystemAdmin();
        if (isSysAdmin != null && isSysAdmin) {
            return fail("不能删除超级管理员");
        }
        boolean success = user.delete();
        if (success) {
            addDeleteSystemLog(user.getId(), userId, user.getName());
            boolean userExtendEnable = JBoltGlobalConfigCache.me.userExtendEnable();
            if (userExtendEnable) {
                //同步删除扩展信息
                Ret ret = userExtendService.deleteById(user.getId());
                if (ret.isFail()) {
                    return ret;
                }
            }
        }
        return ret(success);
    }

    /**
     * 后台管理查询
     *
     * @param pageNumber
     * @param pageSize
     * @param keywords
     * @param ofModule
     * @param sex
     * @param assignDept
     * @param deptId
     * @param postId
     * @param roleId
     * @param enable
     * @return
     */
    public Page<User> paginateAdminList(int pageNumber, int pageSize, String keywords, Integer ofModule, Integer sex, Boolean assignDept, Long deptId, Long postId, Long roleId, Boolean enable) {
        String[] columns = getTableSelectColumnsWithout("password");
        Sql sql = selectSql().page(pageNumber, pageSize);
        sql.select(columns);
        sql.eqIfOk(User.OF_MODULE, ofModule);
        //如果不是超管 就只能查询不是超管的用户
        if (!JBoltUserKit.isSystemAdmin()) {
            sql.eq("is_system_admin", FALSE);
        }
        sql.likeMulti(keywords, "name", "username", "phone", "pinyin");
        if (enable != null) {
            sql.eq("enable", enable ? TRUE : FALSE);
        }
		/*if(isOk(deptId)) {
			List<Long> ids=deptService.processSonDeptIds(deptId);
			if(ids.size()>0) {
				ids.add(0,deptId);
				sql.in("dept_id",ids);
			}else {
				sql.eq("dept_id", deptId);
			}
		}*/
        if (assignDept != null && assignDept) {
            //按照部门搜索
            if (isOk(deptId)) {
                sql.findInSet(deptId, "dept_path", true);
            }
        } else {
            sql.isNull("dept_id");
        }

        if (isOk(roleId)) {
            sql.findInSet(roleId.toString(), "roles", true);
        }
        sql.eq("sex", sex);
        sql.findInSet(postId, "posts", true);
        sql.orderById(true);
        Page<User> userPageData = paginate(sql);
        if (userPageData.getTotalRow() > 0) {
            userPageData.getList().forEach(user -> {
                user.setDeptSnAndName(JBoltDeptCache.me.getSnAndName(user.getDeptId()));
                user.setUserRoles(JBoltRoleCache.me.getRoles(user.getRoles()));
                user.setUserPosts(JBoltPostCache.me.getPosts(user.getPosts()));
            });
        }
        return userPageData;
    }

    /**
     * 系统通知选择用户数据源
     *
     * @param pageNumber
     * @param pageSize
     * @param keywords
     * @param sex
     * @param assignDept
     * @param deptId
     * @param postId
     * @param roleId
     * @return
     */
    public Page<User> paginateSysNoticeList(int pageNumber, int pageSize, String keywords, Integer sex, Boolean assignDept, Long deptId, Long postId, Long roleId) {
        return paginateAdminList(pageNumber, pageSize, keywords, ExtendProjectOfModule.PLATFORM_INNER_ADMIN.getValue(), sex, assignDept, deptId, postId, roleId, true);
    }

    /**
     * 处理所有用户的deptPath
     */
    public void processAllDeptPath() {
        List<User> users = new ArrayList<>();
        dao().each(user -> {
            Dept dept = JBoltDeptCache.me.get(user.getDeptId());
            if (dept == null) {
                throw new RuntimeException("指定部门数据不存在");
            }
            if (notOk(dept.getDeptPath())) {
                throw new RuntimeException("指定部门的路径数据不完整，请执行部门路径补全操作");
            }
            user.setDeptPath(dept.getDeptPath());
            users.add(user);
            return true;
        }, selectSql().select("id", "username", "dept_id").isNotNull("dept_id").toSql());

        if (isOk(users)) {
            batchUpdate(users);
        }
    }
}
