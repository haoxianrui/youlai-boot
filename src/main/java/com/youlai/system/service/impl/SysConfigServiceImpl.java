package com.youlai.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.common.constant.RedisKeyConstants;
import com.youlai.system.common.constant.SystemConstants;
import com.youlai.system.converter.SysConfigConverter;
import com.youlai.system.model.form.ConfigForm;
import com.youlai.system.model.query.ConfigPageQuery;
import com.youlai.system.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.youlai.system.mapper.SysConfigMapper;
import com.youlai.system.model.entity.SysConfig;
import com.youlai.system.model.vo.ConfigVO;
import com.youlai.system.service.SysConfigService;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置Service接口实现
 *
 * @author Theo
 * @since 2024-07-29 11:17:26
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    private final SysConfigConverter sysConfigConverter;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 分页查询系统配置
     * @param configPageQuery 查询参数
     * @return 系统配置分页列表
     */
    @Override
    public IPage<ConfigVO> page(ConfigPageQuery configPageQuery) {
        Page<SysConfig> page = new Page<>(configPageQuery.getPageNum(), configPageQuery.getPageSize());
        QueryWrapper<SysConfig> query = new QueryWrapper<>();
        if(StringUtils.isNotBlank(configPageQuery.getKeywords())) {
            query.and(q -> q.like("config_key", configPageQuery.getKeywords()).or().like("config_name", configPageQuery.getKeywords()));
        }
        Page<SysConfig> pageList = this.page(page, query);
        return sysConfigConverter.convertToPageVo(pageList);
    }

    /**
     * 保存系统配置
     * @param configForm 系统配置表单
     * @return 是否保存成功
     */
    @Override
    public boolean save(ConfigForm configForm) {
        Assert.isTrue(super.count(new QueryWrapper<SysConfig>().eq("config_key", configForm.getConfigKey())) == 0, "配置key已存在");
        SysConfig sysConfig = sysConfigConverter.toEntity(configForm);
        sysConfig.setCreateBy(SecurityUtils.getUserId());
        sysConfig.setIsDeleted(SystemConstants.NOT_DELETED_STATUS);
        return this.save(sysConfig);
    }

    /**
     * 获取系统配置表单数据
     *
     * @param id 系统配置ID
     * @return
     */
    @Override
    public ConfigForm getConfigFormData(Long id) {
        SysConfig entity = this.getById(id);
        return sysConfigConverter.toForm(entity);
    }

    /**
     * 编辑系统配置
     * @param id  系统配置ID
     * @param configForm 系统配置表单
     * @return 是否编辑成功
     */
    @Override
    public boolean edit(Long id, ConfigForm configForm) {
        Assert.isTrue(super.count(new QueryWrapper<SysConfig>().eq("config_key", configForm.getConfigKey()).ne("id", id)) == 0, "配置key已存在");
        SysConfig sysConfig = sysConfigConverter.toEntity(configForm);
        sysConfig.setUpdateBy(SecurityUtils.getUserId());
        return this.update(sysConfig, new QueryWrapper<SysConfig>().eq("id", id));
    }

    /**
     * 删除系统配置
     * @param id 系统配置ID
     * @return 是否删除成功
     */
    @Override
    public boolean delete(Long id) {
        if (id != null) {
            return super.remove(new QueryWrapper<SysConfig>().eq("id", id));
        }
        return false;
    }

    /**
     * 刷新系统配置缓存
     * @return 是否刷新成功
     */
    @Override
    public boolean refreshCache() {
        redisTemplate.delete(RedisKeyConstants.SYSTEM_CONFIG_KEY);
        List<SysConfig> list = this.list();
        if (list != null) {
            Map<String, String> map = list.stream().collect(Collectors.toMap(SysConfig::getConfigKey, SysConfig::getConfigValue));
            redisTemplate.opsForHash().putAll(RedisKeyConstants.SYSTEM_CONFIG_KEY,map);
            return true;
        }
        return false;
    }

    /**
     * 获取系统配置
     * @param key 配置key
     * @return 配置value
     */
    @Override
    public Object getSystemConfig(String key) {
        if(StringUtils.isNotBlank(key)){
            return redisTemplate.opsForHash().get(RedisKeyConstants.SYSTEM_CONFIG_KEY, key);
        }
        return null;
    }

}
