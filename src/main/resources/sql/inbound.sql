### 外部供应商、采购总金额、品类数、工厂+供应商数量
#sql("summary")
select a1.vendorCount, a1.totalAmt, a2.matCount, a3.orderCount
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


### 收货工厂，供应商，采购金额
#sql("vendorAmtByPlant")
select b.*
from (
         select a.orderNum, a.toPlant, a.vendorCode, a.vendorName, sum(a.totalAmt) totalAmt
         from po_vendor_stats a
         where a.external = 'Y'
           and a.toPlant = #para(0)
         group by a.orderNum, a.vendorCode, a.vendorName) b
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
