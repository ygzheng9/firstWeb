### 判定用户登录
#sql("checkLogin")
select a.*
from user a
where a.email = #para(0)
  and a.password = #para(1)
;
#end

### 需要做权限控制的资源
#sql("allResources")
select a.*
from z_resources a
order by a.id;
#end
