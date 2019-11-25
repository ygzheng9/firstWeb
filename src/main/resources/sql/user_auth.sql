### 判定用户登录

#sql("checkLogin")
select a.*
from user a
where a.email = #para(0)
  and a.password = #para(1)
;
#end
