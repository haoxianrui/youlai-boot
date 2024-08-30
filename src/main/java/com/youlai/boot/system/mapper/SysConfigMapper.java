package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置 访问层
 *
 * @author Theo
 * @since 2024-7-29 11:41:04
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

}
