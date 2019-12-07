package com.demo.workflow;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import com.demo.model.WkInstance;
import com.demo.model.WkInstanceStep;
import com.demo.model.WkType;
import com.demo.workflow.rule.FindService;
import com.google.common.collect.Lists;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ygzheng
 * 审批流的流程控制
 */
public class EngineService {
    private Log logger = Log.getLog(EngineService.class);

    private WkInstance insDao = new WkInstance().dao();
    private List<WkType> steps = null;

    @Inject
    FindService findSvc;

    public EngineService() {
        WkType wkTypeDao = new WkType().dao();
        steps = wkTypeDao.template("workflow.allTypes").find();
    }

    String initWorkflow(IFlowTx entry) {
        checkNotNull(entry, "biz transaction must NOT be null.");

        // 根据原始单据，创建审批实例
        WkInstance ins = new WkInstance();
        ins.setBizUnit(entry.getBizUnit());
        ins.setBizType(entry.getBizType());
        ins.setBizNum(entry.getBizNum());
        ins.setBizID(entry.getId());
        ins.setProjectCode(entry.getProjectCode());

        ins.setActionKey(entry.getActionKey());
        ins.setSubmitUser(entry.getSubmitUser());
        ins.setSubmitDate(entry.getSubmitDate());
        ins.setSubmitAmt(entry.getBizAmt());
        ins.setSubmitRemark(entry.getRemark());

        // 生成工作流，默认：步骤为空，未完结；
        ins.setSeqNo("");
        ins.setIsFinished("N");

        ins.setId(IdUtil.simpleUUID());
        if (!ins.save()) {
            logger.warn("failed to init workflow." + ins);
            return "";
        }

        moveForward(ins);

        return ins.getId();
    }

    void moveForward(WkInstance ins) {
        checkNotNull(ins, "workflow instance must NOT be null.");

        // 后续所有的审批节点
        List<WkType> remains = furtherSteps(ins);

        boolean hasNext = false;
        for (WkType step : remains) {
            if (step.getIsAmt() != 0) {
                // 审批规则中，按金额设定了层级，从低到高；但是 当前金额 小于 节点判定的金额，跳过；
                if (ins.getSubmitAmt().intValue() < step.getLowAmt()) {
                    continue;
                }
            }

            String approver = getApprover(ins, step);
            if (StrKit.isBlank(approver)) {
                logger.warn("can not find approver: " + ins.getActionKey() + " " + ins.getSeqNo() + " " + step);
                return;
            }

            ins.setSeqNo(step.getSeqNo());
            ins.setApprover(approver);
            ins.remove("approverComment");
            ins.setStartDate(new DateTime().toString());
            ins.setStatus("处理中");

            if (!ins.update()) {
                logger.warn("failed save ins." + ins);
                return;
            }

            // 一次只向前推进一步，找到后续第一个审批节点
            hasNext = true;
            break;
        }

        // 找不到后续审批节点，也即全部审批通过
        if (!hasNext) {
            ins.setIsFinished("Y");
            if (!ins.update()) {
                logger.warn("failed save ins." + ins);
            }
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

    String getApprover(WkInstance tx, WkType wkType) {
        findSvc.initContext(tx, wkType);
        return findSvc.getApprover();
    }

    void doApprove(String insId, String comment) {
        // 如果审批已经结束，那么不需要再操作
        WkInstance ins = insDao.findById(insId);
        if (ins.getIsFinished().compareToIgnoreCase("Y") == 0) {
            return;
        }

        // 标记状态，更新数据库
        markInstance(insId, comment, "通过");

        // 重新加载
        ins = insDao.findById(insId);
        if (ins == null) {
            logger.warn("can not find instance. " + insId);
            return;
        }

        // 查找下一个审批节点
        moveForward(ins);
    }

    void doReject(String insId, String reason) {
        // 如果审批已经结束，那么不需要再操作
        WkInstance ins = insDao.findById(insId);
        if (ins.getIsFinished().compareToIgnoreCase("Y") == 0) {
            return;
        }

        // 标记审批状态
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
        ins.setIsFinished("N");

        if (status.compareToIgnoreCase("驳回") == 0) {
            ins.setIsFinished("Y");
        }

        if (!ins.update()) {
            logger.warn("failed: " + insId + " " + status + " " + remark);
            return;
        }

        saveToHistory(ins);
    }

    void saveToHistory(WkInstance ins) {
        checkNotNull(ins, "workflow instance must NOT be null.");

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

    List<WkInstance> findInstances() {
        return insDao.template("workflow.findInstances").find();
    }
}
