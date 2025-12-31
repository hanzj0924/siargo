#sql("list")
select #(columns?? "*") from #(table) #@where(true)
#end

#sql("delete")
delete from #(table) #@where(false)
#end

#sql("optionlist")
select #(value) as value,#(text) as text from #(table) #@where(true)
#end

#sql("count")
select count(*) from #(table) #@where(false)
#end

#sql("first")
select * from #(table) #@where(true) limit 1
#end

#sql("firstrand")
select * from #(table) #@where(false) limit 1
#end