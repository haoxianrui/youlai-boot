package com.youlai.system.model.form;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
 * 通知公告表单对象
 *
 * @author youlaitech
 * @since 2024-08-08 11:46
 */
@Getter
@Setter
@Schema(description = "通知公告表单对象")
public class NoticeForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "通知标题")
    @NotBlank(message = "通知标题不能为空")
    @Size(max=50, message="通知标题长度不能超过50个字符")
    private String title;

    @Schema(description = "通知内容")
    @NotBlank(message = "通知内容不能为空")
    @Size(max=65535, message="通知内容长度不能超过65535个字符")
    private String content;

    @Schema(description = "通知类型")
    @NotNull(message = "通知类型不能为空")
    private Integer noticeType;

    @Schema(description = "发布人")
    @NotNull(message = "发布人不能为空")
    private Long release;

    @Schema(description = "优先级(0-低 1-中 2-高)")
    private Integer priority;

    @Schema(description = "目标类型(0-全体 1-指定)")
    private Integer tarType;

    @Schema(description = "发布状态(0-未发布 1已发布 2已撤回)")
    private Integer sendStatus;

    @Schema(description = "发布时间")
    @NotNull(message = "发布时间不能为空")
    private LocalDateTime sendTime;

    @Schema(description = "撤回时间")
    @NotNull(message = "撤回时间不能为空")
    private LocalDateTime recallTime;

    @Schema(description = "创建人ID")
    private Long createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;


}
