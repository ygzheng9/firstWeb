package com.demo.okadmin;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.IdUtil;
import com.demo.model.Session;
import com.demo.model.User;
import com.demo.model.UserList;
import com.google.common.base.Splitter;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ygzheng
 */
public class UserService {
    private static Log log = Log.getLog(UserService.class);

    private User userDAO = new User().dao();
    private Session sessionDAO = new Session().dao();

    private List<User> allUsers = null;
    private List<Session> allSession = null;
    private Map<String, List<String>> allResources = null;

    private void loadAllUsers() {
        allUsers = userDAO.findAll();
    }

    void loadAllSession() {
        allSession = sessionDAO.findAll();
    }

    User fromUserPool(String userID) {
        // 从全局变量中，返回一个对象
        if (allUsers == null) {
            loadAllUsers();
        }

        for (User u : allUsers) {
            if (u.getStr("id").compareTo(userID) == 0) {
                return new User().put(u);
            }
        }

        return null;
    }

    Session fromSessionPool(String sessionID) {
        if (allSession == null) {
            loadAllSession();
        }

        for (Session a : allSession) {
            if (a.getStr("id").compareTo(sessionID) == 0) {
                return new Session().put(a);
            }
        }

        return null;
    }

    void loadAllResources() {
        List<Record> resources = Db.template("userAuth.allResources").find();
        allResources = new HashMap<>();

        // 固定格式：z_resources.permissions 空格 分割
        Splitter splter = Splitter.on(" ");

        for (Record r : resources) {
            String key = r.get("key");
            String s = r.get("permissions");
            List<String> fields = splter.splitToList(s);

            allResources.put(key, fields);
        }
    }

    public Boolean hasPermission(User u, String actionKey) {
        if (allResources == null) {
            loadAllResources();
        }

        List<String> perms = allResources.get(actionKey);
        if (perms == null) {
            // 约定：没有配置表示不需要做权限控制
            return true;
        }

        // 固定格式：user.role 中 空格 分割；
        Splitter splter = Splitter.on(" ");
        String roles = u.getRole();
        if (roles == null) {
            roles = "";
        }
        List<String> fields = splter.splitToList(roles);

        for (String p : perms) {
            for (String r : fields) {
                if (p.compareToIgnoreCase(r) == 0) {
                    return true;
                }
            }
        }

        return false;
    }

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

    public User checkLogin(String email, String password) {
        return userDAO.template("userAuth.checkLogin", email, password).findFirst();
    }
}
