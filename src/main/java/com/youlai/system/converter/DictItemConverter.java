package com.youlai.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.entity.SysDictItem;
import com.youlai.system.model.form.DictForm;
import com.youlai.system.model.vo.DictPageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 字典项 对象转换器
 *
 * @author Ray
 * @since 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface DictItemConverter {

    Page<DictPageVO> toPageVo(Page<SysDictItem> page);

    DictForm toForm(SysDictItem entity);

    SysDictItem toEntity(DictForm.DictItem dictItems);
    List<SysDictItem> toEntity(List<DictForm.DictItem> dictItems);

    DictForm.DictItem toDictItem(SysDictItem entity);
    List<DictForm.DictItem> toDictItem(List<SysDictItem> entities);

    @Mappings({
            @Mapping(target = "value", source = "id"),
            @Mapping(target = "label", source = "name")
    })
    Option<Long> toOption(SysDictItem dictItem);
    List<Option<Long>> toOption(List<SysDictItem> dictItems);
}
