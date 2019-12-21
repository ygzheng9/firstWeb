package com.demo.workday;

import cn.hutool.core.util.IdUtil;
import com.demo.model.UtEntry;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;

import java.util.List;

/**
 * @author ygzheng
 */
public class WorkdayService {
    private Log log = Log.getLog(WorkdayService.class);

    private UtEntry utEntryDao = new UtEntry().dao();

    public String saveEntry(UtEntry entry) {
        if (entry.getId() == null || entry.getId().length() == 0) {
            // 新增
            entry.setId(IdUtil.simpleUUID());
            if (entry.save()) {
                return entry.getId();
            }
        } else {
            // 修改
            if (entry.update()) {
                return entry.getId();
            }
        }

        return "";
    }

    public UtEntry findById(String id) {
        return utEntryDao.findById(id);
    }

    public List<UtEntry> search(String raw) {
        Kv params = JsonKit.parse(raw, Kv.class);
        return utEntryDao.template("workday.search", params).find();
    }
}
