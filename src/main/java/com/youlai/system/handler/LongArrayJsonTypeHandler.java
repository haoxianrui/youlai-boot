package com.youlai.system.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;

/**
 * Long 数组类型转换 json
 *
 * @author haoxr
 * @since 2022/10/14
 */
@Slf4j
@Component
@MappedTypes(value = {Long[].class})
@MappedJdbcTypes(value = {JdbcType.OTHER}, includeNullJdbcType = true)
public class LongArrayJsonTypeHandler extends ArrayObjectJsonTypeHandler<Long> {
    public LongArrayJsonTypeHandler() {
        super(Long[].class);
    }
}
