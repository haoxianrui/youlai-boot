package com.youlai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.youlai.system.model.entity.SysConfig;

/**
 * 系统配置 访问层
 *
 * @author Theo
 * @since 2024-7-29 11:41:04
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

}
