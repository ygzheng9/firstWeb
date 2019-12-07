package com.demo.workflow.rule;

import com.demo.model.WkInstance;
import com.demo.model.WkType;

/**
 * @author ygzheng
 */
public class FindDefault implements IFindApprover {
    @Override
    public boolean isMe(WkType wkType) {
        return true;
    }

    @Override
    public String find(WkInstance ins, WkType wkType) {
        return ins.getBizUnit() + " " + wkType.getCalcRule();
    }
}
