package com.demo.workflow;

import java.math.BigDecimal;

/**
 * @author ygzheng
 * 审批流能处理的单据
 */
public interface IFlowTx {
    /**
     * @return 提交人
     */
    String getSubmitUser();

    String getSubmitDate();

    /**
     * @return 业务单元
     */
    String getBizUnit();

    /**
     * @return 业务类型
     */
    String getBizType();

    /**
     * @return 业务单据号
     */
    String getBizNum();

    /**
     * @return 项目号
     */
    String getProjectCode();

    /**
     * @return 涉及的金额
     */
    BigDecimal getBizAmt();

    /**
     * @return 提交人的事由
     */
    String getRemark();
}
