package com.youlai.boot.system.model.query;


import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description ="字典数据项分页查询对象")
public class DictPageQuery extends BasePageQuery {

    @Schema(description="关键字(字典名称)")
    private String keywords;

    @Schema(description="字典编码")
    private String typeCode;

}
