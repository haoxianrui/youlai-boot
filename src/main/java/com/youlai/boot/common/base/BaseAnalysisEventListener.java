package com.youlai.boot.common.base;

import com.alibaba.excel.event.AnalysisEventListener;

/**
 * 自定义解析结果监听器
 *
 * @author Ray.Hao
 * @since 2023/03/01
 */
public abstract class BaseAnalysisEventListener<T> extends AnalysisEventListener<T> {

    private String msg;
    public abstract String getMsg();
}
