
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


select * from po_vendor_stats 
 where upper(left(vendorCode, 2)) = 'BS' || upper(left(vendorCode, 2)) = 'IS'; 
 
update po_vendor_stats a 
  set a.external = 'N'
where upper(left(vendorCode, 2)) = 'BS' || upper(left(vendorCode, 2)) = 'IS'; 
										

ALTER TABLE po_vendor_mat ADD INDEX po_vendor_mat_matCode (matCode); 	
ALTER TABLE po_vendor_mat ADD INDEX po_vendor_mat_vendorCode (vendorCode); 	

ALTER TABLE project_mat ADD INDEX project_mat_partNum (partNum); 	
								

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


###   同一颗聊，有多个供应商的情况下，最高价和最低价 的差异比例 > 20%

select f.matCode, f.minPrice, f.maxPrice, f.diffPrice, f.diffRate
  from (
select e.matCode, e.minPrice, e.maxPrice, e.maxPrice - e.minPrice diffPrice, (e.maxPrice - e.minPrice) * 100 / e.minPrice diffRate
  from ( 
select d.matCode, min(d.unitPrice) minPrice, max(d.unitPrice) maxPrice
  from ( 
select b.matCode, b.vendorCode, (b.totalAmt / b.totalQty) unitPrice 
  from po_vendor_mat_true b 
inner join ( 
select a.matCode, count(1) 
  from po_vendor_mat_true a 
group by a.matCode
having count(1) > 1 ) c on b.matCode = c.matCode ) d 
group by d.matCode ) e ) f


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
  


select b.vendorName, count(1)
  from (
 select a.vendorName, a.vendorCode
         from po_vendor_stats a
         where a.external = 'Y'
           and a.toPlant = '金属件子公司'
           and  a.vendorName = '重庆美龙汽车零部件有限公司'
         group by a.vendorName, a.vendorCode 
         
         ) b 
group by b.vendorName
having count(1) > 1; 


insert into vendor_Info (vendorCode, vendorName)
  select a.vendorCode, a.vendorName 
    from po_vendor_stats a
group by a.vendorCode, a.vendorName; 

update po_vendor_mat_true a
  inner join vendor_info b 
  on a.vendorCode = b.vendorCode 
set a.vendorName = b.vendorName; 

select * from po_vendor_mat_true; 


select a1.*, a1.totalAmt / a1.totalQty unitPrice
  from (
select b.orderNum,  b.vendorCode, b.vendorName, b.toPlant,  
		  CONCAT('20', right(a.ibDate,2), left(a.ibDate, 2), mid(a.ibDate, 4,2)) ibDate, 
		  a.ibOrderNum,  a.receivedQuantity totalQty, a.totalAmt10 totalAmt
  from po_item a 
inner join po_vendor_stats b on a.orderNum = b.orderNum
where a.material = '2516937-Charc' ) a1
order by  a1.toPlant, a1.ibDate



select a1.*, a1.totalAmt / a1.totalQty unitPrice
  from (
select a.material, b.toPlant,  
		  sum(a.receivedQuantity) totalQty, sum(a.totalAmt10) totalAmt
  from po_item a 
inner join po_vendor_stats b on a.orderNum = b.orderNum
where a.material = '2516937-Charc' 
group by a.material, b.toPlant
) a1
order by  a1.totalAmt desc; 



####### 供应商相同，同一个工厂，订单号不同

select * 
  from (
 select a.toPlant, a.vendorCode, count(1) vendorCount
   from po_vendor_stats a
group by a.toPlant, a.vendorCode ) b 
where b.vendorCount > 3; 


select *
  from po_vendor_stats a
where a.vendorCode = 'DP2001A'
  and a.toPlant = '面套工厂';


#### 料号 和 bom 的对应关系
select a.*
from bom_mat a
where a.partNum = '2430307';

select b.*
from bom_project_mapping b
where b.bomID = 'K426_FS';


select a.partNum, b.bomID, b.project, b.client, b.plant
from bom_mat a
         inner join bom_project_mapping b on a.bomID = b.bomID
where a.partNum = '2430307';

########   供应商 项目 对应关系
ALTER TABLE po_vendor_mat_true ADD INDEX po_vendor_mat_true_matCode (matCode); 	
ALTER TABLE bom_mat ADD INDEX bom_mat_partNum (partNum); 	

insert into po_vendor_project (vendorCode, vendorName, project, totalMat, totalAmt)  
 select a.vendorCode, a.vendorName, c.project, count(1) totalMat, sum(a.totalAmt) totalAmt
   from po_vendor_mat_true a 
inner join bom_mat b on a.matCode = b.partNum 
inner join bom_project_mapping c on b.bomID = c.bomID
group by a.vendorCode, a.vendorName, c.project; 

truncate table po_vendor_project; 


select * from po_vendor_project; 

###  供应商分析
## 1. 供应商采购金额，供应商供应的物料数量，供应商的送货工厂（两种不同的结算方式）；供应商对应的项目信息


select a1.vendorCode, a1.vendorName, a1.totalAmt, (@csum := @csum + a1.totalAmt ) csum
 from (
select b.vendorCode, b.vendorName, b.totalAmt
  from (
select a.vendorCode, a.vendorName, sum(a.totalAmt) totalAmt
   from po_vendor_stats a 
where a.external = 'Y'
group by a.vendorCode, a.vendorName) b 
where b.totalAmt <> 0
order by b.totalAmt desc ) a1, (select @csum := 0) csum;  


select a1.vendorCode, a1.vendorName, a1.toPlant, a1.totalAmt
  from (
select a.vendorCode, a.vendorName, a.toPlant, sum(a.totalAmt) totalAmt
  from po_vendor_stats a 
where a.external = 'Y'
  and a.vendorCode = 'DP4404H'
group by a.vendorCode, a.vendorName, a.toPlant ) a1 
order by a1.totalAmt desc; 


