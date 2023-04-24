package com.youlai.system.sample;

import cn.hutool.core.util.RandomUtil;
import com.youlai.system.pojo.entity.SysDictType;
import com.youlai.system.service.SysDictService;
import com.youlai.system.service.SysDictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 动态数据源案例
 *
 * @author haoxr
 * @date 2023/4/24
 */
@Component
@RequiredArgsConstructor
public class DynamicDataSourceSample {

    private final SysDictTypeService dictTypeService;

    private final SysDictService dictService;


    @Transactional
    public boolean updateDictTypeCode(Long dictTypeId,String newTypeCode) {

        SysDictType dictType = dictTypeService.getById(dictTypeId);
        String originalTypeCode = dictType.getCode();
        dictType.setCode(newTypeCode);
        boolean result = dictTypeService.updateById(dictType);

        if (result) {
            result = dictService.updateDictTypeCode(originalTypeCode, newTypeCode);
        }
        return result;
    }


}
