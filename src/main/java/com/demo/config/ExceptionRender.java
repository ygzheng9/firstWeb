package com.demo.config;


import cn.hutool.core.util.StrUtil;
import com.jfinal.kit.LogKit;
import com.jfinal.render.ErrorRender;
import com.jfinal.render.RenderManager;

/**
 * @author ygzheng
 */
public class ExceptionRender extends ErrorRender {

    private static final String jsonOrther = "{\"msg\":\"{}【服务器内部未知错误，请联系平台】\",\"state\":\"fail\"}";
    private static final String json404 = "{\"msg\":\"404【请求资源不存在】\",\"state\":\"fail\"}";

    public ExceptionRender(int errorCode, String view) {
        super(errorCode, view);
        LogKit.error(StrUtil.format("ExceptionRender 错误码处理【errorCode:{},view:{}】", errorCode, view));
    }

    @Override
    public void render() {
        String url = this.request.getServletPath();
        this.response.setStatus(this.getErrorCode());
        if (this.getErrorCode() == 400
            || this.getErrorCode() == 401
            || this.getErrorCode() == 403
            || this.getErrorCode() == 404
            || this.getErrorCode() == 500) {
            if (url.startsWith("/api")) {
                String errorJson = this.getErrorJson();
                RenderManager.me().getRenderFactory().getJsonRender(errorJson).setContext(this.request, this.response).render();
            } else {
                super.render();
            }
        } else {
            super.render();
        }
    }

    private String getErrorJson() {
        int errorCode = this.getErrorCode();
        if (errorCode == 404) {
            return json404;
        } else {
            return StrUtil.format(jsonOrther, errorCode);
        }
    }

}
