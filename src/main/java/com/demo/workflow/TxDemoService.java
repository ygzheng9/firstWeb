package com.demo.workflow;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import com.demo.model.WkFormDemo;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;

import java.util.List;

/**
 * @author ygzheng
 * 审批业务的 demo
 */
public class TxDemoService {
    @Inject
    EngineService engSvc;

    private WkFormDemo txDao = new WkFormDemo().dao();

    Boolean save(WkFormDemo entry) {
        String now = new DateTime().toString();
        entry.setUpdateUser(entry.getCreateUser());
        entry.setUpdateDate(now);

        String id = entry.getId();
        if (StrKit.isBlank(id)) {
            entry.setId(IdUtil.simpleUUID());
            entry.setCreateDate(now);

            if (entry.getBizType().compareToIgnoreCase("业务报账") == 0) {
                entry.setActionKey(IFlowTx.TYPE_EXPENSE);
            } else if (entry.getBizType().compareToIgnoreCase("销售订单") == 0) {
                entry.setActionKey(IFlowTx.TYPE_SALES_ORDER);
            }

            return entry.save();
        }

        entry.remove("createUser");
        entry.remove("createDate");

        return entry.update();
    }

    List<WkFormDemo> allItems() {
        return txDao.findAll();
    }

    WkFormDemo findbyID(String id) {
        return txDao.findById(id);
    }

    String submitForApproval(WkFormDemo entry) {
        // 先保存，再启动审批流，并记下审批流的 id
        if (!save(entry)) {
            return "保存失败";
        }

        // 从数据库中重新加载数据
        entry = findbyID(entry.getId());

        String now = new DateTime().toString();
        entry.setSubmitUser(entry.getCreateUser());
        entry.setSubmitDate(now);

        String insId = engSvc.initWorkflow(entry);
        if (StrKit.isBlank(insId)) {
            return "审批流启动失败";
        }

        // 业务单据中，保存审批流 id，便于后续关联检索
        entry.setWkInsNum(insId);
        if (!save(entry)) {
            return "保存失败";
        }

        return "";
    }

}
