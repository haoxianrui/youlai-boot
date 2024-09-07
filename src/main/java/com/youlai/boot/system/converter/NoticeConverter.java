package com.youlai.boot.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.bo.NoticeBO;
import com.youlai.boot.system.model.entity.Notice;
import com.youlai.boot.system.model.form.NoticeForm;
import com.youlai.boot.system.model.vo.NoticeVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * 通知公告对象转换器
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 */
@Mapper(componentModel = "spring")
public interface NoticeConverter{

    @Mappings({
            @Mapping(target = "tarIds", expression = "java(com.youlai.boot.common.util.CommonUtil.strToList(entity.getTarIds()))")
    })
    NoticeForm toForm(Notice entity);

    @Mappings({
            @Mapping(target = "tarIds", expression = "java(com.youlai.boot.common.util.CommonUtil.listToStr(formData.getTarIds()))")
    })
    Notice toEntity(NoticeForm formData);

    NoticeVO toVO(Notice notice);

    Page<NoticeVO> toPageVo(Page<NoticeBO> noticePage);

    @Mappings({
    })
    NoticeVO toPageVo(NoticeBO bo);

}
