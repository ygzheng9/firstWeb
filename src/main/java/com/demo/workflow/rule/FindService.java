package com.demo.workflow.rule;

import com.demo.model.WkInstance;
import com.demo.model.WkType;
import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ygzheng
 * 根据不同的审批方式，执行不同的方法，查找审批者
 * 不同的方法，定义在不同的类中，这里是个 dispatch
 */
public class FindService {
    List<IFindApprover> items = null;

    WkInstance ins = null;
    WkType wkType = null;

    public FindService() {
        items = Lists.newArrayList();

        items.add(new FindLineManager());
        items.add(new FindProjectManager());

        // default 一定是最后一个
        items.add(new FindDefault());
    }

    public void initContext(WkInstance ins, WkType wkType) {
        // 初始化参数
        this.ins = ins;
        this.wkType = wkType;
    }

    public String getApprover() {
        checkNotNull(wkType, "flow node must NOT be null");
        checkNotNull(ins, "biz transaction must NOT be null");

        for (IFindApprover i : items) {
            if (i.isMe(wkType)) {
                return i.find(ins, wkType);
            }
        }

        return "";
    }
}
