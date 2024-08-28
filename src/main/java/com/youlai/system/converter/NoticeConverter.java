package com.youlai.system.converter;

import com.youlai.system.model.vo.NoticeVO;
import org.mapstruct.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.entity.Notice;
import com.youlai.system.model.form.NoticeForm;

/**
 * 通知公告对象转换器
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 */
@Mapper(componentModel = "spring")
public interface NoticeConverter{

    NoticeForm toForm(Notice entity);

    Notice toEntity(NoticeForm formData);

    NoticeVO toVO(Notice notice);
}