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
group by b.vendorCount;
#end
