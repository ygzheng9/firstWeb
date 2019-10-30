package com.demo.config;


import cn.hutool.core.util.StrUtil;
import com.jfinal.kit.LogKit;
import com.jfinal.render.Render;
import com.jfinal.render.RenderFactory;

/**
 * @author ygzheng
 */
public class ExceptionRenderFactory extends RenderFactory {

    @Override
    public Render getErrorRender(int errorCode) {
        return getErrorRender(errorCode, constants.getErrorView(errorCode));
    }

    @Override
    public Render getErrorRender(int errorCode, String view) {
        LogKit.error(StrUtil.format("ExceptionRenderFactory 错误码处理【errorCode:{},view:{}】", errorCode, view));
        return new ExceptionRender(errorCode, view);
    }
}
