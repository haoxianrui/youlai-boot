package com.youlai.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.entity.SysDictItem;
import com.youlai.system.model.form.DictForm;
import com.youlai.system.model.vo.DictPageVO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 字典项 对象转换器
 *
 * @author Ray
 * @since 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface DictItemConverter {

    Page<DictPageVO> convertToPageVo(Page<SysDictItem> page);

    DictForm convertToForm(SysDictItem entity);

    @InheritInverseConfiguration(name="convertToForm")
    SysDictItem convertToEntity(DictForm entity);

    DictForm.DictItem convertToDictItem(SysDictItem dictItem);

    List<DictForm.DictItem> convertToDictForm(List<SysDictItem> dictItems);
}
