### 审批流

use cqyfas; 

select * from wk_type; 

select * from wk_form_demo; 

select * from wk_instance; 

delete from wk_instance; 

select * from wk_instance_step order by instanceID, startTime; 

delete from wk_instance_step; 
