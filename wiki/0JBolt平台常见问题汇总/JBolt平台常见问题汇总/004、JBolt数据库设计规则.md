JBolt 数据库设计规则：

一、名称书写规则
1、表名、字段名全部小写  oracle等需要大写的数据库里 全大写
2、纯英文 见名知意
3、多单词使用下划线分隔 例如 category_id   dept_id   post_id
4、表名加前缀 jbolt核心表都是jb_前缀 二开所有业务表 可以根据实际情况给予前缀 比如学校端业务用sc_ 总平台表用pl_

二、主键
1、主键统一使用名称id
2、mysql中使用bigint(20) 对应java Long 其他数据库对应设计
3、主键策略统一使用雪花算法生成 不用自增  特殊需要自增的可能就是学校表 因为需要通过他的数字ID去给学校模块里的student表做分表 tb_student_[schoolId]
4、单一主键 不用符合多主键
5、单表树形设计 需要 主键id 和 父主键pid 固定名称

三、外键
1、外键使用逻辑外键，程序控制
2、外键名称 见名知意 例如 category_id   dept_id   post_id
3、mysql中使用bigint(20) 对应java Long 其他数据库对应设计


四、类型 状态 这种字段设计
1、mysql中用 int(11)  其他数据库里对应设计
2、类型一般用 type
3、状态一般用 state

五、boolean类型字段设计规则
1、使用char(1) 表示true false 不用bit char(1) 在所有数据库里通用 方便迁移转库 jbolt平台自动识别char(1)转Boolean
2、所有boolean类型字段必须必填给予默认值 默认值根据实际情况

六、【是否启用】 字段设计
1、用enable 
2、类型char(1)
3、必须有默认值

七、排序字段
1、用固定的sort_rank
2、类型int(11)
3、JBolt自动处理这个名字 底层做了优化

八、真删除还是假删除
1、根据需要 如果假删除 必须使用字段 is_deleted 标识
2、char(1) 默认值false
3、JBolt在调用删除的时候 底层自动识别是否存在这个标识字段 如果有 还是false 就执行假删除 没有这个字段就真删除

九、日期时间类型字段
1、创建时间 create_time  JBolt内置字段处理优化 不用自己赋值 save和update的时候 自动检测 自动赋值
2、更新时间 update_time JBolt内置字段处理优化 不用自己赋值 save和update的时候 自动检测 自动赋值
3、类型选择 datetime
4、java里使用java.util.Date 赋值 数据库查询后转为java.util.Date

十、当前用户作为 创建人 更新人 ID
1、创建人create_user_id JBolt内置字段处理优化 不用自己赋值 save和update的时候 自动检测 自动赋值
2、更新人update_user_id JBolt内置字段处理优化 不用自己赋值 save和update的时候 自动检测 自动赋值

十一、能用字典表解决的就用字典表解决
1、字典表 设计使用sn字段 作为选项的值 外键

十二、数据名称字段设计规则
1、举例 学校管理中 学校名称用name就行了 没必要在school表里学校名称写成school_name 这样java调用的时候school.getName()
2、如果有其他表冗余学校名称 字段名应该是school_name

十三、数字字段设计
1、如果确定就是整数 用int(11) 其他数据库对应设计
2、如果数字很大 或者 存在小数点 用 decimal(10,2) java里对应BigDecimal