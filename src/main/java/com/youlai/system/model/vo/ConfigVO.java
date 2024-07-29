package com.youlai.system.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author Theo
 * @description: 系统配置VO
 * @Company 利盈智能
 * @date 2024-07-29 11:17:26
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Schema(description = "系统配置VO")
public class ConfigVO {

    private Long id;

    @Schema(description = "配置名称")
    private String sysName;

    @Schema(description = "配置key")
    private String sysKey;

    @Schema(description = "配置值")
    private String sysValue;

    @Schema(description = "描述、备注")
    private String remark;
}
