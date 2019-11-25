package com.demo.okadmin;

import com.demo.config.ZCacheKit;
import com.demo.model.Session;
import com.demo.model.User;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

/**
 * @author ygzheng
 */
public class LoginService {
    private static Log logger = Log.getLog(LoginService.class);

    @Inject
    UserService userSvc;

    // 存放登录用户的 cacheName
    static final String loginAccountCacheName = "loginAccount";

    // "jfinalId" 仅用于 cookie 名称，其它地方如 cache 中全部用的 "sessionId" 来做 key
    static final String sessionIdName = "jfinalId";

    Ret login(String email, String password, boolean keepLogin) {
        User loginAccount = userSvc.checkLogin(email, password);
        if (loginAccount == null) {
            logger.info("failed login: " + email + ":" + password);
            return Ret.fail("msg", "这没你的地方，你不属于这");
        }

        // 如果用户勾选保持登录，暂定过期时间为 7 天，否则为 120 分钟，单位为秒
        long liveSeconds = keepLogin ? 7 * 24 * 60 * 60 : 120 * 60;
        // 传递给控制层的 cookie
        int maxAgeInSeconds = (int) (keepLogin ? liveSeconds : -1);
        // expireAt 用于设置 session 的过期时间点，需要转换成毫秒
        long expireAt = System.currentTimeMillis() + (liveSeconds * 1000);

        // 保存登录 session 到数据库
        Session session = new Session();
        String sessionId = StrKit.getRandomUUID();
        session.setId(sessionId);
        session.setAccountId(loginAccount.getId());
        session.setExpireAt(expireAt);
        if (!session.save()) {
            return Ret.fail("msg", "保存 session 到数据库失败，请联系管理员");
        }

        loginAccount.removeSensitiveInfo();
        ZCacheKit.put(loginAccountCacheName, sessionId, loginAccount);

        return Ret.ok(sessionIdName, sessionId)
            .set(loginAccountCacheName, loginAccount)
            .set("maxAgeInSeconds", maxAgeInSeconds);
    }

    /**
     * 通过 sessionId 获取登录用户信息
     * sessoin 表结构：session(id, accountId, expireAt)
     * <p>
     * 1：先从缓存里面取，如果取到则返回该值，如果没取到则从数据库里面取
     * 2：在数据库里面取，如果取到了，则检测是否已过期，如果过期则清除记录，
     * 如果没过期则先放缓存一份，然后再返回
     */
    public User getUserBySessionId(String sessionId) {
        // 先从 cache 中取，找到了直接返回
        User u = ZCacheKit.get(loginAccountCacheName, sessionId);
        if (u != null) {
            return u;
        }

        // cache 中没有，从 db 中找，并且保存到 cache 中
        Session session = new Session().dao().findById(sessionId);
        if (session == null) {
            // session 不存在
            return null;
        }
        if (session.isExpired()) {
            // session 已过期
            // 被动式删除过期数据，此外还需要定时线程来主动清除过期数据
            session.delete();
            return null;
        }

        User loginAccount = new User().dao().findById(session.getAccountId());
        // 找到 loginAccount 并且 是正常状态 才允许登录
        if (loginAccount != null) {
            // 移除 password 与 salt 属性值
            loginAccount.removeSensitiveInfo();
            ZCacheKit.put(loginAccountCacheName, sessionId, loginAccount);

            return loginAccount;
        }

        return null;
    }
}
