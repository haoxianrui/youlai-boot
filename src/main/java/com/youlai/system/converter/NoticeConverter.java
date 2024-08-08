package com.youlai.system.converter;

import org.mapstruct.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.entity.Notice;
import com.youlai.system.model.form.NoticeForm;

/**
 * 通知公告对象转换器
 *
 * @author youlaitech
 * @since 2024-08-08 11:46
 */
@Mapper(componentModel = "spring")
public interface NoticeConverter{

    NoticeForm toForm(Notice entity);

    Notice toEntity(NoticeForm formData);
}