package com.demo.config;

import com.google.common.io.ByteStreams;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author ygzheng
 */
public class StreamRender extends Render {
    private final InputStream input;
    private final String filename;

    public StreamRender(InputStream input, String filename) {
        this.input = input;
        this.filename = filename;
    }

    @Override
    public void render() {
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-disposition", "attachment; filename=" + filename);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        // vnd.ms-excel, pdf, msword

        OutputStream out = null;
        try {
            out = response.getOutputStream();
            ByteStreams.copy(input, out);
        } catch (IOException e) {
            throw new RenderException(e);
        }
    }

}
