package com.youlai.boot.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.DictItem;
import com.youlai.boot.system.model.vo.DictPageVO;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.system.model.form.DictForm;
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

    Page<DictPageVO> toPageVo(Page<DictItem> page);

    DictForm toForm(DictItem entity);

    DictItem toEntity(DictForm.DictItem dictItems);
    List<DictItem> toEntity(List<DictForm.DictItem> dictItems);

    DictForm.DictItem toDictItem(DictItem entity);
    List<DictForm.DictItem> toDictItem(List<DictItem> entities);

    @Mappings({
            @Mapping(target = "value", source = "value"),
            @Mapping(target = "label", source = "name")
    })
    Option<Long> toOption(DictItem dictItem);
    List<Option<Long>> toOption(List<DictItem> dictItems);
}
