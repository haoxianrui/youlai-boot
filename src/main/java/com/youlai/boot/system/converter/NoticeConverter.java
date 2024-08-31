package com.youlai.boot.system.converter;

import com.youlai.boot.system.model.entity.Notice;
import com.youlai.boot.system.model.form.NoticeForm;
import com.youlai.boot.system.model.vo.NoticeVO;
import org.mapstruct.Mapper;

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