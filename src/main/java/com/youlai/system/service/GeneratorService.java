package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.query.TablePageQuery;
import com.youlai.system.model.vo.TableColumnVO;
import com.youlai.system.model.vo.TableGeneratePreviewVO;
import com.youlai.system.model.vo.TablePageVO;

import java.util.List;

/**
 * 数据库服务接口
 *
 * @author haoxr
 * @since 2.11.0
 */
public interface GeneratorService {


    /**
     * 获取数据表分页列表
     *
     * @param queryParams 查询参数
     * @return
     */
    Page<TablePageVO> getTablePage(TablePageQuery queryParams);

    /**
     * 获取数据表字段列表
     *
     * @param tableName 表名
     * @return
     */
    List<TableColumnVO> getTableColumns(String tableName);

    /**
     * 获取预览生成代码
     *
     * @param tableName 表名
     * @return
     */
    List<TableGeneratePreviewVO> getTablePreviewData(String tableName);
}
