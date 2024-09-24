package com.youlai.boot.system.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * 用户公告状态分页查询对象
 *
 * @author youlaitech
 * @since 2024-08-28 16:56
 */
@Schema(description ="用户公告状态查询对象")
@Getter
@Setter
public class NoticeStatusQuery extends BasePageQuery {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    private Long id;

    @Schema(description = "公共通知id")
    private Long noticeId;

    @Schema(description = "用户id")
    private Integer userId;

    @Schema(description = "读取状态，0未读，1已读取")
    private Long readStatus;

    @Schema(description = "用户阅读时间")
    private List<String> readTime;
}
