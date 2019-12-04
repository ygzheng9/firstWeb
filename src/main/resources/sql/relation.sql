## echart relation

### 供应商 - 送货工厂
#sql("vendorAmt")
select a.vendorName, a.vendorCode, 'vendor' category, sum(a.totalAmt) totalAmt
from po_vendor_stats a
where a.external = 'Y'
group by a.vendorName, a.vendorCode;
#end

#sql("plantAmt")
select a.toPlant, 'plant' category, sum(a.totalAmt) totalAmt
from po_vendor_stats a
where a.external = 'Y'
group by a.toPlant;
#end

#sql("vendorPlant")
select a.vendorName, a.vendorCode, a.toPlant, sum(a.totalAmt) totalAmt
from po_vendor_stats a
where a.external = 'Y'
group by a.vendorName, a.vendorCode, a.toPlant;
#end

### 客户，项目，工厂 相关关系
#sql("clientPlantNodes")
select a.project name, 'project' category, count(distinct a.project) value
from bom_project_mapping a
group by a.project
union all
select a.client name, 'client' category, count(distinct a.project) value
from bom_project_mapping a
group by a.client
union all
select a.plant name, 'plant' category, count(distinct a.project) value
from bom_project_mapping a
group by a.plant;
#end

#sql("clientPlantLinks")
select a.client 'target', a.project 'source'
from bom_project_mapping a
group by a.client, a.project
union all
select a.plant 'target', a.client 'source'
from bom_project_mapping a
group by a.plant, a.client
union all
select a.project 'target', a.plant 'source'
from bom_project_mapping a
group by a.project, a.plant;
#end

### 客户，工厂，供应商 相关关系
#sql("clientVendorNodes")
select vendorName name, 'vendor' category, sum(amt) amt
from po_vendor_plant_client
group by vendorName
union all
select client name, 'client' category, sum(amt) amt
from po_vendor_plant_client
group by client;
#end

#sql("clientVendorLinks")
select client 'source', vendorName 'target'
from po_vendor_plant_client
group by client, vendorName;
#end
