### 获取 客户-项目-bom 对应关系
#sql("getProjectCustomerMapping")
select b.client, b.plant, count(distinct b.project) projectCnt, count(distinct b.bomID) bomCnt
from (
         select a.client, a.plant, a.project, a.bomID
         from bom_project_mapping a
         group by a.client, a.plant, a.project, a.bomID
     ) b
group by b.client, b.plant;
#end


### 根据 (客户, 工厂) 获取 bom 列表
#sql("getBomList")
select a.client, a.plant, a.project, a.bomID, a.partCount
from bom_project_mapping a
where a.client = #para(0)
  and a.plant =  #para(1)
order by a.project, a.bomID;
#end

### 根据 bomid, 获取明细清单
#sql("getBomItems")
select a1.*
from (
         select *, CONVERT(a.seq_num, SIGNED) seq
         from bom_item a
         where a.bom_id = #para(0) ) a1
         order by a1.seq asc;
#end


### 料号复用：bom 级别
#sql("reuseByBom")
select repeatedCnt, count(1) as size
from (
         select partNum, count(1) repeatedCnt
         from bom_mat
         group by partNum
     ) a
group by a.repeatedCnt
order by a.repeatedCnt;
#end


### BOM 数量
#sql("getBomCount")
select count(1) bomCnt
from bom_info;
#end

### 料号数量
#sql("getMatCount")
select count(1) partCnt
from mat_info;
#end

### 料号+BOM 的数量
#sql("getBomPartCnt")
select count(1)
from bom_mat;
#end

### 在两个 BOM 中出现的 料号数量
#sql("getMatReuseCount")
select count(1) as matCnt
from (
         select partNum, count(1) repeatedCnt
         from bom_mat
         group by partNum
     ) a
where a.repeatedCnt > 1;
#end

### 使用次数大于 1 次的物料，在 BOM 子件中的行数；
#sql("getMatBomReuseCount")
select count(1) as matCnt
from bom_mat a
where a.reused > 1;
#end


### BOM 子件数量，复用的子件数量
#sql("getBomReuse")
select aa.bomID, aa.partCount, ifnull(bb.reusePartCount, 0) repeatCnt, aa.project, aa.client
from bom_project_mapping aa
         left join bom_info bb on aa.bomID = bb.bomID
order by aa.client, aa.project, aa.bomID;
#end


###  根据重复使用的次数，查找料号清单
#sql("getMatByReuseCount")
select m.*
from (
         select a.partNum
         from bom_mat a
         group by a.partNum
         having count(1) = #para(0)
     ) rep
         inner join mat_info m on rep.partNum = m.part_num
order by m.part_family, m.part_num;
#end

### 根据料号，取得使用的 BOM
#sql("getBOMByMat")
select m.bomID, m.project, m.client, m.plant, bi.partCount, bi.reusePartCount, bi.reuseRate
from bom_project_mapping m
         inner join (
    select bomID
    from bom_mat
    where partNum = #para(0)
) a on a.bomID = m.bomID
         inner join bom_info bi on m.bomID = bi.bomID
order by m.client, m.project, m.bomID;
#end

### 根据料号，取得物料信息
#sql("getMatInfo")
select *
from mat_info a
where a.part_num = #para(0)
order by a.id desc;
#end


### 项目级别的物料复用
#sql("projectCount")
select count(1)
from (
         select m.project
         from bom_project_mapping m
         group by m.project
     ) a;
#end

### 统计在不同项目间复用的零件号数量
#sql("projectReuseMat")
select count(1)
from (
         select c.partNum, count(1) repeatCnt
         from project_mat c
         group by c.partNum
     ) d
where d.repeatCnt > 1;
#end

### （项目，料号）对的数量，project_mat 是预处理过的，颗粒度是（项目，料号）
#sql("projectMatPair")
select count(1)
from project_mat;
#end

### 复用料号的(项目，料号)对的数量
#sql("projectMatPairReuse")
select count(1)
from project_mat a
where a.reused > 1;
#end

### 每个项目的物料复用率：项目中复用的物料数量 / 项目中使用的所有物料的数量
### 在数据库中生成对应的数据，存入表中，这里直接从 table 读取；
#sql("projectInfo")
select *
from project_info a
order by a.client, a.reuseRate desc;
#end

### 取得项目下所有零件
#sql("projectMatList")
select a.project, a.partNum, a.reused, b.part_family partFamily, b.part_name partName, b.part_name_zh partNameZh
from project_mat a
         inner join mat_info b on a.partNum = b.part_num
where a.project = #para(0)
order by a.reused desc, a.partNum;
#end

#sql("projectInfobyName")
select *
from project_info a
where a.project = #para(0)
order by a.project;
#end

### 根据料号，查看使用到的项目信息
#sql("projectByMat")
select *
from project_info a
         inner join (
    select m.project
    from project_mat m
    where m.partNum = #para(0)
    group by m.project
) b on a.project = b.project
order by a.client, a.reuseRate desc;
#end

### part 在 project 之间的共用次数统计
#sql("projectMatReuseStats")
select a1.reuseCount, a1.size
from (
         select b.reuseCount, count(1) size
         from (
                  select a.partNum, count(1) reuseCount
                  from project_mat a
                  group by a.partNum) b
         group by b.reuseCount) a1
order by a1.size desc;
#end

### 根据 part 的共用次数，查询 part 清单
#sql("projectMatByReuseCount")
select m.*
from (
         select a.partNum
         from project_mat a
         group by a.partNum
         having count(1) = #para(0)
     ) rep
         inner join mat_info m on rep.partNum = m.part_num
order by m.part_family, m.part_num;
#end

### 根据年份，取得区域销售量
#sql("getRegionSales")
select *
from region_sales_stats
where year = #para(0)
order by quantity2 desc;
#end

### 根据年份，区域，取得下面城市的销量
#sql("getCitySales")
select *
from region_sales
where year =   #para(0)
  and region = #para(1)
order by quantity asc;
#end
