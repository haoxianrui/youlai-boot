package com.youlai.system.model.form;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
 * 用户公告状态表单对象
 *
 * @author youlaitech
 * @since 2024-08-28 16:56
 */
@Getter
@Setter
@Schema(description = "用户公告状态表单对象")
public class NoticeStatusForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


}
