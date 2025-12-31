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
select top 1 * from #(table) #@where()
#end

#sql("firstrand")
select top 1 * from #(table) #@where()
#end