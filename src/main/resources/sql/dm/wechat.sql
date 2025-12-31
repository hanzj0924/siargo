### 后台管理分页多条件查询
#sql("mpinfo.paginateAdminList")
select * from #(table) where 1=1
#if(enable!=null)
 and "enable"=#(enable)
#end
#if(isAuthenticated!=null)
 and "is_authenticated"=#(isAuthenticated)
#end
#if(type!=null)
 and "type"=#(type)
#end
#if(subjectType!=null)
 and "subject_type"=#(subjectType)
#end
#if(keywords??)
#setLocal(mpinfokw=likeValue(keywords))
 and (("name" like '%#(mpinfokw)%') or ("wechat_id" like '%#(mpinfokw)%'))
#end
 order by id desc
#end


### 公众号-自动回复规则后台管理分页多条件查询
#sql("autoreply.paginateAdminList")
select * from #(table) where mp_id=#(mpId) and type=#(type)
#if(keywords??)
 and name like '%#(likeValue(keywords))%'
#end
 order by id desc
#end



### 公众号-素材库后台管理分页多条件查询
#sql("media.paginateAdminList")
select * from #(table) where "mp_id"=#(mpId) and "type"='#(type)'
#if(keywords??)
#setLocal(mediakw=likeValue(keywords))
 and (("title" like '%#(mediakw)%') or ("digest" like '%#(mediakw)%'))
#end
 order by update_time desc
#end


### 公众平台-微信用户后台管理分页多条件查询
#sql("user.paginateAdminList")
select * from #(table) where "mp_id"=#(mpId)
#if(sex?? || (sex!=null&&sex.toInt()==0))
 and "sex"=#(sex)
#end
#if(keywords??)
#setLocal(userkw=likeValue(keywords))
 and (("nickname" like '%#(userkw)%') or ("realname" like '%#(userkw)%') or ("open_id" like '%#(userkw)%') or ("union_id" like '%#(userkw)%')  or ("phone" like '%#(userkw)%') or ("weixin" like '%#(userkw)%'))
#end
 order by id desc
#end

