use yf_show; 

select * from bom_project_mapping; 

insert into id_name_mapping (type, name)
SELECT 'client', client  FROM yf_show.bom_project_mapping group by client; 


insert into id_name_mapping (type, name)
SELECT 'plant',  plant  FROM yf_show.bom_project_mapping group by plant; 

select * from id_name_mapping; 

update id_name_mapping 
  set display = CONCAT('客户 ', id) 
where type = 'client'; 

update id_name_mapping 
  set display = CONCAT('plant ', id) 
where type = 'plant'; 

update bom_project_mapping a
inner join id_name_mapping b 
  on b.name = a.client 
 and b.type = 'client'
  set a.client = b.display; 

update bom_project_mapping a
inner join id_name_mapping b 
  on b.name = a.plant 
 and b.type = 'plant'
  set a.plant = b.display; 

select CONCAT('客户 ', id) from id_name_mapping  where type = 'client'; 


################

insert into id_name_mapping (type, name)
SELECT 'vendorName',  vendorName  FROM yf_show.po_head group by vendorName; 

update id_name_mapping 
  set display = CONCAT(' 供应商 ', id) 
where type = 'vendorName'; 

update po_head a
inner join id_name_mapping b 
  on b.name = a.vendorName 
 and b.type = 'vendorName'
  set a.vendorName = b.display; 
 
 ## 
  insert into id_name_mapping (type, name)
SELECT 'plantName',  plantName  FROM yf_show.po_head group by plantName; 

update id_name_mapping 
  set display = CONCAT('  工厂名称 ', id) 
where type = 'plantName'; 

update po_head a
inner join id_name_mapping b 
  on b.name = a.plantName 
 and b.type = 'plantName'
  set a.plantName = b.display; 
  
 #####
 update po_vendor_mat_true a
inner join id_name_mapping b 
  on b.name = a.vendorName 
 and b.type = 'vendorName'
  set a.vendorName = b.display; 
  
   update po_vendor_plant_client a
inner join id_name_mapping b 
  on b.name = a.vendorName 
 and b.type = 'vendorName'
  set a.vendorName = b.display; 
  
  
   update po_vendor_plant_client a
inner join id_name_mapping b 
  on b.name = a.plant 
 and b.type = 'plant'
  set a.plant = b.display; 
  
     update po_vendor_plant_client a
inner join id_name_mapping b 
  on b.name = a.client 
 and b.type = 'client'
  set a.client = b.display; 
  
    update po_vendor_project a
inner join id_name_mapping b 
  on b.name = a.vendorName 
 and b.type = 'vendorName'
  set a.vendorName = b.display; 
  
 
    update po_vendor_stats a
inner join id_name_mapping b 
  on b.name = a.vendorName 
 and b.type = 'vendorName'
  set a.vendorName = b.display; 
   
  
    update project_info a
inner join id_name_mapping b 
  on b.name = a.client 
 and b.type = 'client'
  set a.client = b.display;   
  
     update project_info a
inner join id_name_mapping b 
  on b.name = a.plant 
 and b.type = 'plant'
  set a.plant = b.display;    
  
  
     update vendor_info a
inner join id_name_mapping b 
  on b.name = a.vendorName 
 and b.type = 'vendorName'
  set a.vendorName = b.display;      
  
  
select  * 
  from id_name_mapping 
  where name = '面套工厂/哈尔滨子公司骨架工区'
  
  
     update po_vendor_stats a
inner join id_name_mapping b 
  on b.name = a.toPlant 
 and b.type = 'plantName'
  set a.toPlant = b.display;      
  
  