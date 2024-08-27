package com.youlai.system.model.query;

import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 用户公告状态分页查询对象
 *
 * @author youlaitech
 * @since 2024-08-27 09:53
 */
@Schema(description ="用户公告状态查询对象")
@Getter
@Setter
public class NoticeStatusQuery extends BasePageQuery {

    private static final long serialVersionUID = 1L;
}
