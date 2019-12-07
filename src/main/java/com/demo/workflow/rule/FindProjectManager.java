package com.demo.workflow.rule;

import com.demo.model.WkInstance;
import com.demo.model.WkType;

/**
 * @author ygzheng
 */
public class FindProjectManager implements IFindApprover {
    @Override
    public boolean isMe(WkType wkType) {
        return wkType.getCalcRule().compareToIgnoreCase(IFindApprover.PROJECT_MANAGER) == 0;
    }

    @Override
    public String find(WkInstance ins, WkType wkType) {
        return ins.getProjectCode() + " PM";
    }
}
