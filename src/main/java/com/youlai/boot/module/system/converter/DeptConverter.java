package com.youlai.boot.module.system.converter;

import com.youlai.boot.module.system.model.entity.SysDept;
import com.youlai.boot.module.system.model.vo.DeptVO;
import com.youlai.boot.module.system.model.form.DeptForm;
import org.mapstruct.Mapper;

/**
 * 部门对象转换器
 *
 * @author haoxr
 * @since 2022/7/29
 */
@Mapper(componentModel = "spring")
public interface DeptConverter {

    DeptForm toForm(SysDept entity);
    
    DeptVO toVo(SysDept entity);

    SysDept toEntity(DeptForm deptForm);

}