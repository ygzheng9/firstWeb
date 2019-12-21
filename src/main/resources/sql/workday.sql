#### workday 工作日志

###@formatter:off

#sql("search")
select *
from ut_entry a
where 1 = 1
#if(userName)
  and (a.userName like concat('%', #para(userName), '%') )
#end
#if(bizDate)
  and (a.bizDate >= #para(rangeStart) and a.bizDate <= #para(rangeEnd) )
#end
#if(projectCode)
  and a.projectCode = #para(projectCode)
#end
order by a.bizDate, a.userName, a.projectCode;
#end

###@formatter:on
