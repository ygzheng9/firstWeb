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


### BOM 数量
#sql("getBomCount")
select count(distinct bom_id) bomCnt
from bom_item;
#end

### 料号数量
#sql("getMatCount")
select count(distinct part_num) partCnt
from bom_item;
#end

### 料号+BOM 的数量
#sql("getBomPartCnt")
select count(1)
from (select part_num, bom_id
      from bom_item
      group by part_num, bom_id) a;
#end


### 在两个 BOM 中出现的 料号数量
#sql("getMatReuseCount")
select count(1) as matCnt
from (select part_num, count(1) as repeatedCnt
      from (select part_num, bom_id
            from bom_item
            group by part_num, bom_id) dist_bom_part
      group by part_num) a
where a.repeatedCnt > 1;
#end

### 使用次数大于 1 次的物料，在 BOM 子件中的行数；
#sql("getMatBomReuseCount")
select count(1) as matCnt
from (select part_num, count(1) as repeatedCnt
      from (select part_num, bom_id
            from bom_item
            group by part_num, bom_id) dist_bom_part
      group by part_num
     ) a
         inner join (select part_num, bom_id
                     from bom_item
                     group by part_num, bom_id) b
                    on a.part_num = b.part_num
                        and a.repeatedCnt > 1;
#end


### BOM 子件数量，复用的子件数量
#sql("getBomReuse")
select aa.bomID, aa.partCount, ifnull(bb.repeatCnt, 0) repeatCnt, aa.project, aa.client
from bom_project_mapping aa
         left join (
    select b.bom_id, count(1) repeatCnt
    from (select part_num, count(1) as repeatCnt
          from (select part_num, bom_id
                from bom_item
                group by part_num, bom_id) dist_bom_part
          group by part_num) a
             inner join (select part_num, bom_id
                         from bom_item
                         group by part_num, bom_id) b
                        on a.part_num = b.part_num
                            and a.repeatCnt > 1
    group by b.bom_id
) bb on aa.bomID = bb.bom_id
order by aa.client, aa.project, aa.bomID;
#end


###  重复使用次数对应的料号
#sql("getMatByReuseCount")
select m.*
from (
         select a.part_num
         from (select part_num, count(1) as repeatedCnt
               from (select part_num, bom_id
                     from bom_item
                     group by part_num, bom_id) dist_bom_part
               group by part_num
              ) a
                  inner join (select part_num, bom_id
                              from bom_item
                              group by part_num, bom_id) b
                             on a.part_num = b.part_num
                                 and a.repeatedCnt = #para(0)
         group by a.part_num) rep
         inner join mat_info m on rep.part_num = m.part_num
order by m.part_family, m.part_num;
#end


### 根据年份，取得区域销售量
#sql("getRegionSales")
select *
from region_sales_stats
where year = #para(0)
order by quantity2 desc;
#end
