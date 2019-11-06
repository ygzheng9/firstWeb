### 获取 客户-项目-bom 对应关系
#sql("getProjectCustomerMapping")
select b.client, b.plant, count(distinct b.project) projectCnt, count(distinct b.bomID) bomCnt
from (
         select a.client, a.plant, a.project, a.bomID
         from bom_project_mapping a
         group by a.client, a.plant, a.project, a.bomID
     ) b
group by b.client, b.plant
;
#end


### 根据 (客户, 工厂) 获取 bom 列表
#sql("getBomList")
select a.client, a.plant, a.project, a.bomID, a.partCount
from bom_project_mapping a
where a.client = #para(0)
  and a.plant =  #para(1)
order by a.project, a.bomID
;
#end

### 根据 bomid, 获取明细清单
#sql("getBomItems")
select *
from bom_item a
where a.bom_id = #para(0)
;
#end


### 料号复用：bom 级别
#sql("reuseByBom")
select repeatedCnt, count(1) as size
from (select part_num, count(1) as repeatedCnt
      from (select part_num, bom_id
            from bom_item
            group by part_num, bom_id) dist_bom_part
      group by part_num) a
group by repeatedCnt
order by repeatedCnt;
#end
