package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import java.io.Serializable;
import com.youlai.system.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 系统配置 实体
 *
 * @author Theo
 * @since 2024-07-29 11:17:26
 */
@Data
@TableName("sys_config")
@Schema(description = "系统配置")
public class SysConfig extends BaseEntity {

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "配置key")
    private String configKey;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "描述、备注")
    private String remark;

    @Schema(description = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @Schema(description = "更新人ID")
    @TableField(fill = FieldFill.UPDATE)
    private Long updateBy;

    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;

}
