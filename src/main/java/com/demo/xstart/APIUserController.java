package com.demo.xstart;

import com.demo.model.User;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

/**
 * @author ygzheng
 */

public class APIUserController extends Controller {
    @Inject
    private UserService userSvc;

    public void paginate() {
        int pageNum = getInt("page", 1);
        int pageSize = getInt("limit", 20);

        Page<User> users = userSvc.paginate(pageNum, pageSize);

        LayerTable<User> result = LayerTable.build(users);

        renderJson(result);
    }

    public void add() {
        User user = getBean(User.class);
        Ret ret = userSvc.save(user);
        renderJson(ret);
    }

    public void delete() {
        String id = get("id");
        Ret ret = userSvc.delete(id);
        renderJson(ret);
    }

    public void edit() {
        User user = getBean(User.class);
        Ret ret = userSvc.update(user);
        renderJson(ret);
    }


}
