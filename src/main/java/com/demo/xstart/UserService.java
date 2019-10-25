package com.demo.xstart;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.IdUtil;
import com.demo.model.User;
import com.demo.model.UserList;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;

import java.util.List;

public class UserService {
    private static Log log = Log.getLog(UserService.class);

    private User userDAO = new User().dao();

    public void loadFile() {
        // 批量从文件中读取记录，然后插入到数据库中，供后续使用；
        String jsonFile = PropKit.get("baseFolder") + "/zdata/userlist.json";

        FileReader fileReader = new FileReader(jsonFile);
        String content = fileReader.readString();
        UserList result = JsonKit.parse(content, UserList.class);

        batchInsert(result.getData());

        return;
    }

    private void batchInsert(List<User> users) {
        for (User u : users) {
            boolean ok = u.save();

            if (!ok) {
                log.warn("save error: " + JsonKit.toJson(u));
            }
        }
    }

    public Ret save(User u) {
        u.setId(IdUtil.simpleUUID());

        boolean ok = u.save();

        if (!ok) {
            log.warn("save error: " + JsonKit.toJson(u));
            return Ret.fail("msg", "创建失败");
        }

        return Ret.ok("msg", "创建成功");
    }

    public Ret update(User u) {
        boolean ok = u.update();

        if (!ok) {
            log.warn("update error: " + JsonKit.toJson(u));
            return Ret.fail("msg", "更新失败");
        }

        return Ret.ok("msg", "更新成功");
    }

    public User edit(int id) {
        return userDAO.findById(id);
    }

    public Ret delete(String userId) {
        boolean ok = userDAO.deleteById(userId);

        if (!ok) {
            log.warn("delete error: " + userId);
            return Ret.fail("msg", "删除失败");
        }

        return Ret.ok("msg", "删除成功");
    }

    public Page<User> paginate(int pageNum, int pageSize) {
        return userDAO.paginate(pageNum, pageSize, "select *", "from user order by id desc");
    }

    public List<User> findAll() {
        return userDAO.findAll();
    }
}
