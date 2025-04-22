package com.youlai.boot.system.model.event;

import lombok.Data;

/**
 * 字典更新事件
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Data
public class DictEvent {
    /**
     * 字典编码
     */
    private String dictCode;
    
    /**
     * 时间戳
     */
    private long timestamp;

    public DictEvent(String dictCode) {
        this.dictCode = dictCode;
        this.timestamp = System.currentTimeMillis();
    }
} 
