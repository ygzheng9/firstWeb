
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



#### 料号 和 bom 的对应关系
select count(1)
from (
select a.*, b.part_name, b.part_name_zh
  from po_mat_stats a 
inner join mat_info b on a.matCode = b.part_num
) c; 

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

