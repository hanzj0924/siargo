#sql("list")
select #(columns?? "*") from #(table) #@where() #@order(true)
#end

#sql("delete")
delete from #(table) #@where() #@order(false)
#end

#sql("optionlist")
select #(value) as value,#(text) as text from #(table) #@where() #@order(true)
#end

#sql("count")
select count(*) from #(table) #@where() #@order(false)
#end

#sql("first")
select * from(select * from #(table) #@where() #@order(true)) where rownum=1
#end

#sql("firstrand")
select * from(select * from #(table) #@where() order by DBMS_RANDOM.VALUE()) where rownum=1
#end