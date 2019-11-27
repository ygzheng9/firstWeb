### 外部供应商、采购总金额、品类数、工厂+供应商数量
#sql("summary")
select a1.vendorCount, ROUND(a1.totalAmt / 10000) totalAmt, a2.matCount, a3.orderCount
from (
         select count(1) vendorCount, sum(b.totalAmt) totalAmt
         from (
                  select a.vendorCode, sum(a.totalAmt) totalAmt
                  from po_vendor_stats a
                  where a.external = 'Y'
                  group by a.vendorCode) b) a1,
     (
         select sum(totalAmt) totalAmt, count(1) matCount
         from po_mat_stats a) a2,
     (
         select sum(totalAmt) totalAmt, count(1) orderCount
         from po_vendor_stats a
         where a.external = 'Y') a3;
#end

### 物料的来源数量分析
#sql("matSource")
select b.vendorCount, count(1) matCount, sum(b.totalAmt) totalAmt
from (
         select a.matCode, count(1) vendorCount, sum(a.totalAmt) totalAmt
         from po_vendor_mat_true a
         group by a.matCode) b
group by b.vendorCount
order by b.vendorCount;
#end

### 物料的来源分析-金额分布
#sql("matSourceAmt")
select a.matCode, count(1) vendorCount, sum(a.totalAmt) totalAmt
from po_vendor_mat_true a
group by a.matCode
order by vendorCount;
#end

### 同一颗聊，多个供应商的情况
#sql("matMultiSource")
select b.matCode,
       b.vendorCount,
       b.totalAmt,
       b.vendors,
       cc.maxPrice,
       cc.minPrice,
       cc.diffPrice,
       cc.diffRate
from (
         select a.matCode, count(1) vendorCount, sum(a.totalAmt) totalAmt, group_concat(a.vendorName) vendors
         from po_vendor_mat_true a
         group by a.matCode
         having count(1) > 1) b
         inner join (
    select f.matCode, f.minPrice, f.maxPrice, f.diffPrice, f.diffRate
    from (
             select e.matCode,
                    e.minPrice,
                    e.maxPrice,
                    e.maxPrice - e.minPrice                      diffPrice,
                    (e.maxPrice - e.minPrice) * 100 / e.minPrice diffRate
             from (
                      select d.matCode, min(d.unitPrice) minPrice, max(d.unitPrice) maxPrice
                      from (
                               select b.matCode, b.vendorCode, (b.totalAmt / b.totalQty) unitPrice
                               from po_vendor_mat_true b
                                        inner join (
                                   select a.matCode, count(1)
                                   from po_vendor_mat_true a
                                   group by a.matCode
                                   having count(1) > 1) c on b.matCode = c.matCode) d
                      group by d.matCode) e) f
    where f.diffRate >= 10
) cc on b.matCode = cc.matCode
order by totalAmt desc;
#end

#sql("matMultiSourceVendors")
select b.*, b.totalAmt / b.totalQty unitPrice
from (
         select a.matCode, a.vendorCode, a.vendorName, sum(a.totalAmt) totalAmt, sum(a.totalQty) totalQty
         from po_vendor_mat_true a
         where a.matCode = #para(0)
         group by a.matCode, a.vendorCode, a.vendorName
     ) b
order by b.totalAmt desc;
#end

### 根据料号，查看入库记录明细，供应商，供货工厂
#sql("matMultiSourceIBItems")
select a1.*, a1.totalAmt / a1.totalQty unitPrice
from (
         select b.orderNum,
                b.vendorCode,
                b.vendorName,
                b.toPlant,
                CONCAT('20', right(a.ibDate, 2), left(a.ibDate, 2), mid(a.ibDate, 4, 2)) ibDate,
                a.ibOrderNum,
                a.receivedQuantity                                                       totalQty,
                a.totalAmt10                                                             totalAmt
         from po_item a
                  inner join po_vendor_stats b on a.orderNum = b.orderNum
         where a.material = #para(0) ) a1
         order by a1.toPlant, a1.ibDate;
#end

### 同一颗料在不同工厂的入库
#sql("matMultiSourcePlants")
select a1.*, a1.totalAmt / a1.totalQty unitPrice
from (
         select a.material              matCode,
                b.toPlant,
                sum(a.receivedQuantity) totalQty,
                sum(a.totalAmt10)       totalAmt
         from po_item a
                  inner join po_vendor_stats b on a.orderNum = b.orderNum
         where a.material = #para(0)
         group by a.material, b.toPlant
     ) a1
order by a1.totalAmt desc;
#end


### 入库明细：工厂 + 料号
#sql("matMultiSourceIBItemsByPlant")
select a1.*
from (select b.toPlant,
             b.vendorCode,
             b.vendorName,
             a.orderNum,
             a.material                                                               matCode,
             CONCAT('20', right(a.ibDate, 2), left(a.ibDate, 2), mid(a.ibDate, 4, 2)) ibDate,
             a.ibOrderNum,
             a.receivedQuantity                                                       totalQty,
             a.totalAmt10                                                             totalAmt,
             a.unitCost10                                                             unitCost
      from po_item a
               inner join po_vendor_stats b on a.orderNum = b.orderNum
      where a.material = #para(0)
        and b.toPlant =  #para(1) ) a1
      order by a1.ibDate desc;
#end

### 入库物料 和 bom 物料关联
#sql("matchedMat")
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
#end


### 收货工厂的供应商数量，采购金额
#sql("plantAmt")
select b.*, b.totalAmt / b.vendorCount vendorAvg
from (
         select a.toPlant, sum(a.totalAmt) totalAmt, count(distinct vendorCode) vendorCount
         from po_vendor_stats a
         where a.external = 'Y'
         group by a.toPlant) b
order by b.totalAmt desc;
#end

### 采购订单 by plant
### 同一个供应商，同一个工厂，会有多张采购订单！！！
#sql("orderAmtByPlant")
select b.*
from (
         select a.orderNum, a.toPlant, a.vendorCode, a.vendorName, sum(a.totalAmt) totalAmt
         from po_vendor_stats a
         where a.external = 'Y'
           and a.toPlant = #para(0)
         group by a.orderNum, a.toPlant, a.vendorCode, a.vendorName) b
order by b.totalAmt desc;
#end

#sql("orderAmtByPlantVendor")
select b.*
from (
         select a.orderNum, a.toPlant, a.vendorCode, a.vendorName, sum(a.totalAmt) totalAmt
         from po_vendor_stats a
         where a.external = 'Y'
           and a.toPlant =    #para(0)
           and a.vendorCode = #para(1)
         group by a.orderNum, a.toPlant, a.vendorCode, a.vendorName) b
order by b.totalAmt desc;
#end

### 收货工厂，供应商，采购金额
#sql("vendorAmtByPlant")
select b.*
from (
         select a.toPlant, a.vendorCode, a.vendorName, sum(a.totalAmt) totalAmt
         from po_vendor_stats a
         where a.external = 'Y'
           and a.toPlant = #para(0)
         group by a.toPlant, a.vendorCode, a.vendorName) b
order by b.totalAmt desc;
#end


### 根据 工厂、供应商（orderNum）查看物料
#sql("matAmtByOrderNum")
select a.orderNum, a.matCode, a.totalAmt, a.totalQty, a.unitPrice
from po_vendor_mat a
where a.orderNum = #para(0)
order by a.totalAmt desc;
#end


### 根据 orderNum，mat，查看入库明细
#sql("itemByOrderMat")
select b.*
from (select a.orderNum,
             a.material                                                               matCode,
             CONCAT('20', right(a.ibDate, 2), left(a.ibDate, 2), mid(a.ibDate, 4, 2)) ibDate,
             a.ibOrderNum,
             a.receivedQuantity                                                       totalQty,
             a.totalAmt10                                                             totalAmt,
             a.unitCost10                                                             unitCost
      from po_item a
      where a.orderNum = #para(0)
        and a.material = #para(1) ) b
      order by b.ibDate desc;
#end


### 到天的入库明细：order，mat
#sql("itemByOrderMatDay")
select b.ibDate,
       sum(b.totalQty)                   totalQty,
       sum(b.totalAmt)                   totalAmt,
       sum(b.totalAmt) / sum(b.totalQty) unitCost
from (
         select a.orderNum,
                a.material                                                               matCode,
                CONCAT('20', right(a.ibDate, 2), left(a.ibDate, 2), mid(a.ibDate, 4, 2)) ibDate,
                a.ibOrderNum,
                a.receivedQuantity                                                       totalQty,
                a.totalAmt10                                                             totalAmt,
                a.unitCost10                                                             unitCost
         from po_item a
         where a.orderNum = #para(0)
           and a.material = #para(1)) b
         group by b.ibDate
         order by b.ibDate desc;
#end
