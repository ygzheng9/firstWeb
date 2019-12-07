package com.demo.workflow.rule;

import com.demo.model.WkInstance;
import com.demo.model.WkType;

/**
 * @author ygzheng
 */
public interface IFindApprover {
    String LINE_MANAGER = "一线经理";
    String PROJECT_MANAGER = "项目经理";
    String OTHER_APPROVER = "";

    /**
     * @return 返回审批者的类型
     */
    boolean isMe(WkType wkType);

    /**
     * @return 返回审批者
     */
    String find(WkInstance ins, WkType wkType);


}
