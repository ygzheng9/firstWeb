package com.demo.workflow;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import com.demo.model.WkFormDemo;
import com.jfinal.kit.StrKit;

import java.util.List;

/**
 * @author ygzheng
 * 审批业务的 demo
 */
public class TxDemoService {
    private WkFormDemo txDao = new WkFormDemo().dao();

    Boolean save(WkFormDemo entry) {
        String now = new DateTime().toString();
        entry.setUpdateUser(entry.getCreateUser());
        entry.setUpdateDate(now);

        String id = entry.getId();
        if (StrKit.isBlank(id)) {
            entry.setId(IdUtil.simpleUUID());
            entry.setCreateDate(now);

            return entry.save();
        }

        entry.remove("createUser");
        return entry.update();
    }

    List<WkFormDemo> allItems() {
        return txDao.findAll();
    }

    WkFormDemo findbyID(Integer id) {
        return txDao.findById(id);
    }

}
