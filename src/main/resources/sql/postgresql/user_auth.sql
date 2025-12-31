### 验证是否拥有某个 permission
#sql("hasPermission")
select * from role_permission where permission_id=#(permissionId) 
 and (
	#for (x : roleArray)
		#(for.first ? "" : "or") role_id = #(x)
	#end
	)
#end

