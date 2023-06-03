package com.youlai.system.listener.easyexcel;

import com.alibaba.excel.event.AnalysisEventListener;

/**
 * 自定义解析结果监听器
 *
 * @author haoxr
 * @since 2023/03/01
 */
public abstract class MyAnalysisEventListener<T> extends AnalysisEventListener<T> {

    private String msg;
    public abstract String getMsg();
}
