#sql("list")
select #(columns?? "*") from #(table) #@where()
#end

#sql("delete")
delete from #(table) #@where()
#end

#sql("optionlist")
select #(value) as value,#(text) as text from #(table) #@where()
#end

#sql("count")
select count(*) from #(table) #@where()
#end

#sql("first")
select * from #(table) #@where() limit 1
#end

#sql("firstrand")
select * from #(table) #@where() limit 1
#end