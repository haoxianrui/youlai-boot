package com.youlai.system.model.query;

import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 日志分页查询对象
 *
 * @author Ray
 * @since 2.10.0
 */
@Schema(description = "日志分页查询对象")
@Getter
@Setter
public class LogPageQuery extends BasePageQuery {

    @Schema(description="关键字(日志内容/请求路径/请求方法/地区/浏览器/终端系统)")
    private String keywords;

    @Schema(description="开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String startDate;

    @Schema(description="结束日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String endDate;

}
