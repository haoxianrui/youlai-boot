package com.youlai.boot.system.model.dto;

import lombok.Data;

/**
 * 字典更新事件消息
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Data
public class DictEventDTO {
    /**
     * 字典编码
     */
    private String dictCode;
    
    /**
     * 时间戳
     */
    private long timestamp;

    public DictEventDTO(String dictCode) {
        this.dictCode = dictCode;
        this.timestamp = System.currentTimeMillis();
    }
}

