package com.youlai.boot.system.converter;

import com.youlai.boot.system.model.entity.SysDept;
import com.youlai.boot.system.model.vo.DeptVO;
import com.youlai.boot.system.model.form.DeptForm;
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