package com.youlai.system.converter;

import com.youlai.system.model.entity.SysDept;
import com.youlai.system.model.form.DeptForm;
import com.youlai.system.model.vo.DeptVO;
import org.mapstruct.Mapper;

/**
 * 部门对象转换器
 *
 * @author haoxr
 * @since 2022/7/29
 */
@Mapper(componentModel = "spring")
public interface DeptConverter {

    DeptForm convertToForm(SysDept entity);
    
    DeptVO convertToVo(SysDept entity);

    SysDept convertToEntity(DeptForm deptForm);

}