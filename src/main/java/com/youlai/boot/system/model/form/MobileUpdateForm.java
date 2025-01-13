package com.youlai.boot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 修改手机表单
 *
 * @author Ray.Hao
 * @since 2024/8/19
 */
@Schema(description = "修改手机表单")
@Data
public class MobileUpdateForm {

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "验证码")
    private String code;

}
