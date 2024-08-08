package com.youlai.system.converter;

import org.mapstruct.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.entity.NoticeStatus;
import com.youlai.system.model.form.NoticeStatusForm;

/**
 * 用户公告状态对象转换器
 *
 * @author youlaitech
 * @since 2024-08-08 11:46
 */
@Mapper(componentModel = "spring")
public interface NoticeStatusConverter{

    NoticeStatusForm toForm(NoticeStatus entity);

    NoticeStatus toEntity(NoticeStatusForm formData);
}