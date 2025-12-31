### 定义order排序处理规则 pgsql需要判断是否自动处理加入按照ID排序
#define order(autoProcessOrderById)
#if(orderColumns?? && orderTypes??)
 #for(col:orderColumns)#(for.first?" order by ":"")#(col) #if(orderTypes!=null&&orderTypes.length>0)#(orderTypes[for.index])#end #(for.last?"":",")#end
#elseif(autoProcessOrderById)
 order by #(primaryKey?? 'id') asc
#end
#end

### 定义sql的where条件处理规则  pgsql需要判断是否自动处理加入按照ID排序
#define where(autoProcessOrderById)
#if(myparas)
 where 1=1 #for(myp:myparas) #((or??)?((for.index==0)?' and (':' or '):' and ') #(myp.key) #(customCompare?"":"=") #sqlValue(myp.value) #((for.index==for.size-1)?((or??)?")":""):"") #end #@order(autoProcessOrderById) 
#else
 #@order(autoProcessOrderById)
#end
#end

#namespace("common")
#include("common.sql")
#end

#namespace("user.auth")
#include("user_auth.sql")
#end

#namespace("wechat")
#include("wechat.sql")
#end

#namespace("user")
#include("user.sql")
#end

