
####### ########################################    
###  YF 的 采购订单号 不是单据号，而是 供应商+工厂 的编号，每次采购都是这个号，QAD 报表也是在查询期间一个这样的号；
### 每次送货有唯一的送货单号；


###  按 （工厂 + 供应商）看采购金额
### 采购订单规则：供应商编码 + 工厂标记
truncate table po_vendor_stats; 

insert into po_vendor_stats (orderNum, totalAmt, totalItem)
select a.orderNum, sum(totalAmt10) totalAmt, count(1) totalItem
  from  po_item a
group by a.orderNum; 


update po_vendor_stats a 
  inner join (
select a.orderNum, a.vendorCode, a.vendorName
  from  po_head a
group by a.orderNum, a.vendorCode, a.vendorName) b
on a.orderNum = b.orderNum 
set a.vendorCode = b.vendorCode,
      a.vendorName = b.vendorName; 


update po_vendor_stats a 
   set a.external = 'Y'; 

## 内部供应商的编码规则
update po_vendor_stats a 
  set a.external = 'N' 
where upper(left(a.orderNum,2)) = 'BS'  or upper(left(a.orderNum,2)) = 'IS'; 

update po_vendor_stats a 
  set a.external = 'N'
where upper(left(vendorCode, 2)) = 'BS' || upper(left(vendorCode, 2)) = 'IS'; 


## 外部供应商的订单编码规则： 供应商+工厂代码
update po_vendor_stats a 
  set a.toPlantInd = right(a.orderNum, 1) 
where a.external = 'Y' ; 

update po_vendor_stats a 
inner join plant_info b on a.toPlantInd = b.plantInd
  set a.toPlant = b.plantName; 
  

select * from po_vendor_stats; 

### 按物料看，采购金额
## 只关注外部供应商
truncate table po_mat_stats; 

insert into po_mat_stats (matCode, totalAmt, totalQty)
select a.material, sum(a.totalAmt10) totalAmt, sum(a.receivedQuantity) totalQty
  from po_item a
inner join po_vendor_stats b on a.orderNum = b.orderNum and b.external = 'Y'
group by a.material ; 

update po_mat_stats
  set unitPrice = totalAmt / totalQty
where totalQty <> 0; 


select b.*
from po_mat_stats b 
order by b.totalAmt desc; 

select count(1) from po_mat_stats; 

######


###  按 供应商+物料 来看
## 只关注外部供应商
truncate table po_vendor_mat; 

insert into po_vendor_mat (orderNum, matCode, totalAmt, totalQty)
 select a.orderNum, a.material, sum(a.totalAmt10) totalAmt, sum(a.receivedQuantity) totalQty
  from po_item a
inner join po_vendor_stats b on a.orderNum = b.orderNum  and b.external = 'Y'
group by a.orderNum, a.material; 

#  物料数量
select count(1)
from (
select matCode, count(1)
from po_vendor_mat
group by matCode ) a; 

# 订单数量
select count(1)
from (
select orderNum, count(1)
from po_vendor_mat
group by orderNum ) a; 





###  供应商 和 项目 的对应关系
truncate table po_vendor_project; 

## (工厂+供应商) 对应的 项目
truncate table po_vendor_project; 

insert into po_vendor_project (orderNum, project, totalAmt)
 select a.orderNum, b.project, sum(a.totalAmt) totalAmt
   from  po_vendor_mat a 
inner join (
select a.partNum, group_concat(a.project) project
  from project_mat a
  group by a.partNum ) b on a.matCode = b.partNum 
group by a.orderNum , b.project; 

## 供应商 对应的 项目
select count(1)
from (
select b.vendorCode, b.vendorName, a.project, sum(a.totalAmt) totalAmt
  from po_vendor_project a 
inner join po_vendor_stats b on a.orderNum = b.orderNum and b.external = 'Y'
group by b.vendorCode, b.vendorName, a.project) c ; 


select b.vendorCode, b.vendorName, sum(a.totalAmt) totalAmt
  from po_vendor_project a 
inner join po_vendor_stats b on a.orderNum = b.orderNum and b.external = 'Y'
group by b.vendorCode, b.vendorName; 

select * from po_vendor_project; 


select * from po_vendor_stats 
 where upper(left(vendorCode, 2)) = 'BS' || upper(left(vendorCode, 2)) = 'IS'; 
 
update po_vendor_stats a 
  set a.external = 'N'
where upper(left(vendorCode, 2)) = 'BS' || upper(left(vendorCode, 2)) = 'IS'; 
										

ALTER TABLE po_vendor_mat ADD INDEX po_vendor_mat_matCode (matCode); 	
ALTER TABLE po_vendor_mat ADD INDEX po_vendor_mat_vendorCode (vendorCode); 	

ALTER TABLE project_mat ADD INDEX project_mat_partNum (partNum); 	
								
select * from po_vendor_project; 


## 
# 1. 外部供应商、采购总金额、品类数、工厂+供应商数量
# 2. 物料的供应源分析：1 个，2 个，3 个，多余 3 个；
# 3. 采购料号，和 BOM 料号 关联：采购品名数量， bom 品名数量，关联量 ，关联金额，采购总金额
# 4. 基于关联量，分析： 供应商采购金额  ->  供应商对应项目列表  -> 供应商对该项目的供应物料 ->  该物料的价格走势、采购量 --> 到货记录 

# 1.  外部供应商，对应的工厂数量  -->   选定供应商的工厂下的采购金额（列表，从高到低）、品类数量 -->  每月的采购金额  
# 2. 工厂，对应的外部供应商数量  --> 和上面的可以统一；


####################
## 1. 外部供应商、采购总金额、品类数、工厂+供应商数量
select a1.vendorCount, a1.totalAmt, a2.matCount, a3.orderCount
  from (
select  count(1) vendorCount, sum(b.totalAmt) totalAmt
  from (
  select a.vendorCode, sum(a.totalAmt) totalAmt 
    from po_vendor_stats a 
where a.external = 'Y'
group by a.vendorCode) b) a1, 
(
select sum(totalAmt) totalAmt, count(1) matCount
  from po_mat_stats a ) a2, 
(
select sum(totalAmt) totalAmt, count(1) orderCount
  from po_vendor_stats a
where a.external = 'Y') a3; 

## # 2. 物料的供应源分析：1 个，2 个，3 个，多余 3 个；

update po_vendor_mat a
inner join po_vendor_stats b on a.orderNum = b.orderNum
  set a.vendorCode = b.vendorCode; 
  
insert into po_vendor_mat_true (matCode, vendorCode, totalAmt, totalQty) 
select a.matCode, a.vendorCode, sum(a.totalAmt) totalAmt, sum(a.totalQty) totalQty
  from po_vendor_mat a
group by a.matCode, a.vendorCode; 


select b.vendorCount, count(1) matCount, sum(b.totalAmt) totalAmt
  from (
select a.matCode, count(1) vendorCount, sum(a.totalAmt) totalAmt
  from po_vendor_mat_true a 
group by a.matCode ) b
group by b.vendorCount; 


select a.matCode, count(1) vendorCount, sum(a.totalAmt) totalAmt
  from po_vendor_mat_true a 
group by a.matCode; 


#### 料号 和 bom 的对应关系


ALTER TABLE po_mat_stats ADD INDEX po_mat_stats_matCode (matCode); 	

ALTER TABLE mat_info ADD INDEX mat_info_part_num (part_num); 	


select a1.matCount, a1.matAmt, a2.totalCount, a2.totalAmt
from (
    select count(1) matCount, sum(c.totalAmt) matAmt
    from (
             select a.matCode, a.totalAmt
             from po_mat_stats a
                      inner join mat_info b on a.matCode = b.part_num
         ) c) a1
   , (
    select count(1) totalCount, sum(a.totalAmt) totalAmt
    from po_mat_stats a) a2;


# 2. 工厂，对应的外部供应商数量  --> 和上面的可以统一；
select b.* , b.totalAmt / b.vendorCount vendorAvg
  from (
select a.toPlant, sum(a.totalAmt) totalAmt, count(distinct vendorCode) vendorCount 
  from po_vendor_stats a
where a.external = 'Y'
group by a.toPlant) b 
order by b.totalAmt desc; 


## 根据送货工厂，看供应商
select b.*
  from (
select  a.orderNum, a.toPlant, a.vendorCode, a.vendorName, sum(a.totalAmt) totalAmt
  from po_vendor_stats a 
where a.external = 'Y' 
  and a.toPlant = '成都总装工厂'
group by a.orderNum, a.vendorCode, a.vendorName) b 
order by b.totalAmt desc;  
  
update po_vendor_mat a 
   set a.unitPrice = 0; 
   
 update po_vendor_mat a 
   set a.unitPrice = a.totalAmt / a.totalQty
where a.totalQty <> 0; 
  
 select a.orderNum, a.matCode, a.totalAmt, a.totalQty, a.unitPrice
   from po_vendor_mat a 
where a.orderNum = 'DP1215Cq'; 


select a.orderNum, a.material, a.ibDate,  a.ibOrderNum, a.receivedQuantity, a.totalAmt10, a.unitCost10
  from po_item a 
where a.orderNum = 'DP1215Cq'
  and a.material = '3815999L'
order by a.ibDate desc; 


select b.ibDate, sum(b.totalQty) totalQty, sum(b.totalAmt) totalAmt, sum(b.totalAmt) / sum(b.totalQty) unitCost
  from (
select a.orderNum,
       a.material         matCode,
       CONCAT('20', right(a.ibDate,2), left(a.ibDate, 2), mid(a.ibDate, 4,2)) ibDate, 
       a.ibOrderNum,
       a.receivedQuantity totalQty,
       a.totalAmt10       totalAmt,
       a.unitCost10       unitCost
from po_item a
where a.orderNum = 'DP1215Cq'
  and a.material = '3815999L') b
group by b.ibDate 
order by b.ibDate desc;
  

