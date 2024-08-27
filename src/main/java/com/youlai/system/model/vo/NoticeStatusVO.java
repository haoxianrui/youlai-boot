package com.youlai.system.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 用户公告状态视图对象
 *
 * @author youlaitech
 * @since 2024-08-27 09:53
 */
@Getter
@Setter
@Schema( description = "用户公告状态视图对象")
public class NoticeStatusVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
