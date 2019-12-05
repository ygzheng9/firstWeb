## echart 中绘制关系图：nodes, links

##  收货工厂 和 供应商，node 大小为采购金额；
## 供应商
select  a.vendorName, a.vendorCode, sum(a.totalAmt) totalAmt
  from po_vendor_stats a
where a.external = 'Y'
group by a.vendorName, a.vendorCode; 

## 工厂
select a.toPlant, sum(a.totalAmt) totalAmt
  from po_vendor_stats a
where a.external = 'Y'
group by a.toPlant; 

##  供应商 - 工厂
select  a.vendorName, a.vendorCode, a.toPlant, sum(a.totalAmt) totalAmt
  from po_vendor_stats a
where a.external = 'Y'
group by a.vendorName, a.vendorCode, a.toPlant; 

### 客户-项目
select * 
   from bom_project_mapping a; 
   
   
### 供应商 和 客户

## 更新工厂
update project_info b 
  inner join (select a.project, a.plant from bom_project_mapping a group by a.project, a.plant) a1
    on b.project = a1.project
set b.plant = a1.plant; 

insert into po_vendor_plant_client (vendorName, plant, client, amt)
select a.vendorName, b.plant, b.client, sum(a.totalAmt) amt
    from po_vendor_project a
             inner join project_info b on a.project = b.project
    group by a.vendorName, b.plant, b.client; 


select * from po_vendor_plant_client; 



                            po_vendor_plant_clientbom_info
