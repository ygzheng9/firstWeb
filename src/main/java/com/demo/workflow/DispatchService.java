package com.demo.workflow;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import com.demo.model.WkInstance;
import com.demo.model.WkInstanceStep;
import com.demo.model.WkType;
import com.google.common.collect.Lists;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import java.util.List;

/**
 * @author ygzheng
 * 审批流的流程控制
 */
public class DispatchService {
    private Log logger = Log.getLog(DispatchService.class);

    private WkInstance insDao = new WkInstance().dao();
    private List<WkType> steps = null;

    DispatchService() {
        WkType wkTypeDao = new WkType().dao();
        steps = wkTypeDao.template("workflow.allTypes").find();
    }

    void initWorkflow(IFlowTx entry) {
        // 根据原始单据，创建审批实例
        WkInstance ins = new WkInstance();
        ins.setBizUnit(entry.getBizUnit());
        ins.setBizNum(entry.getBizNum());
        ins.setActionKey(entry.getBizType());
        ins.setSeqNo("");
        ins.setSubmitUser(entry.getSubmitUser());
        ins.setSubmitDate(entry.getSubmitDate());
        ins.setSubmitAmt(entry.getBizAmt());
        ins.setSubmitRemark(entry.getRemark());
        ins.setProjectCode(entry.getProjectCode());

        ins.setId(IdUtil.simpleUUID());
        if (!ins.save()) {
            logger.warn("failed to init workflow." + ins);
            return;
        }

        moveForward(ins);
    }

    void moveForward(WkInstance ins) {
        // 设置下一个审批节点
        List<WkType> remains = furtherSteps(ins);

        boolean hasNext = false;

        for (WkType s : remains) {
            if (s.getIsAmt() != 0) {
                if (ins.getSubmitAmt().intValue() < s.getLowAmt()) {
                    ins.setSeqNo(s.getSeqNo());
                    ins.remove("approver");
                    ins.remove("approverComment");
                    ins.setStatus("跳过");

                    String t = new DateTime().toString();
                    ins.setStartDate(t);
                    ins.setEndTime(t);

                    if (!ins.save()) {
                        logger.warn("failed 跳过节点." + s);
                        return;
                    }

                    saveToHistory(ins);

                    continue;
                }
            }

            String approver = calcApprover(s, ins);
            if (StrKit.isBlank(approver)) {
                logger.warn("can not find approver: " + ins.getActionKey() + " " + ins.getSeqNo() + " " + s);
                return;
            }

            ins.setSeqNo(s.getSeqNo());
            ins.setApprover(approver);
            ins.remove("approverComment");
            ins.setStartDate(new DateTime().toString());
            ins.setStatus("进行中");

            if (!ins.save()) {
                logger.warn("failed save ins." + ins);
                return;
            }

            hasNext = true;
            break;
        }

        if (!hasNext) {
            // 全部节点审批通过，
            // TODO: 需要更新 原单据 的审批状态；
        }
    }

    List<WkType> furtherSteps(WkInstance ins) {
        List<WkType> items = Lists.newArrayList();
        for (WkType w : steps) {
            if (w.getActionKey().compareToIgnoreCase(ins.getActionKey()) == 0 &&
                w.getSeqNo().compareToIgnoreCase(ins.getSeqNo()) > 0) {
                items.add(w);
            }
        }

        items.sort((a, b) -> a.getSeqNo().compareToIgnoreCase(b.getSeqNo()));

        return items;
    }

    String calcApprover(WkType wkType, WkInstance tx) {
        String approver = "";
        String calcRule = wkType.getCalcRule();

        if (calcRule.compareToIgnoreCase("一线经理") == 0) {
            approver = getPeopleManager(tx.getSubmitUser());
        } else if (calcRule.compareToIgnoreCase("项目经理") == 0) {
            approver = getProjectManager(tx.getProjectCode());
        } else {
            approver = getUnitRole(tx.getBizUnit(), calcRule);
        }

        return approver;
    }

    String getPeopleManager(String user) {
        return user + "' Pem";
    }

    String getProjectManager(String user) {
        return user + "' PM";
    }

    String getUnitRole(String bizUnit, String role) {
        return bizUnit + " " + role;
    }

    void approve(String insId, String comment) {
        markInstance(insId, comment, "通过");

        WkInstance ins = insDao.findById(insId);
        if (ins == null) {
            logger.warn("can not find instance. " + insId);
            return;
        }

        moveForward(ins);
    }

    void reject(String insId, String reason) {
        markInstance(insId, reason, "驳回");
    }

    void markInstance(String insId, String remark, String status) {
        WkInstance ins = insDao.findById(insId);
        if (ins == null) {
            logger.warn("can not find instance. " + insId);
            return;
        }

        ins.setApproverComment(remark);
        ins.setStatus(status);
        ins.setEndTime(new DateTime().toString());

        if (!ins.save()) {
            logger.warn("failed: " + insId + " " + status + " " + remark);
            return;
        }

        saveToHistory(ins);
    }

    void saveToHistory(WkInstance ins) {
        WkInstanceStep h = new WkInstanceStep();
        h.setInstanceID(ins.getId());
        h.setSeqNo(ins.getSeqNo());
        h.setStartTime(ins.getStartDate());
        h.setApprover(ins.getApprover());
        h.setEndTime(ins.getEndTime());
        h.setApproverComment(ins.getApproverComment());
        h.setStatus(ins.getStatus());

        if (!h.save()) {
            logger.warn("failed to save instance history." + h);
        }
    }
}
