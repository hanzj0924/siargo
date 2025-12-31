### 后台管理分页查询
#sql("paginateAdminList")
select #(columns?? "*") from #(table) where 1=1
#if(keywords??)
#setLocal(kw=likeValue(keywords))
 and ((name like '%#(kw)%') or (username like '%#(kw)%') or (phone like '%#(kw)%') or (pinyin like '%#(kw)%'))
#end
 order by id desc
#end
