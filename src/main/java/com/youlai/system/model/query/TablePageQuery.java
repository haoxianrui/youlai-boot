package com.youlai.system.model.query;

import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据表分页查询对象
 *
 * @author Ray
 * @since 2.10.0
 */
@Schema(description = "数据表分页查询对象")
@Getter
@Setter
public class TablePageQuery extends BasePageQuery {

    @Schema(description="关键字(表名)")
    private String keywords;

}
